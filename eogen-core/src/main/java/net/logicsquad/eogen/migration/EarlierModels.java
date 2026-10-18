package net.logicsquad.eogen.migration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The earlier version of a project's models, which a migration changes the
 * tables from: those of a commit, which git reads into a scratch folder, or
 * those in a folder.
 */
public final class EarlierModels {
	/**
	 * The earlier version that a model with no migrations is compared with by
	 * default: the last commit. A model with migrations is compared with the
	 * commit that added its latest.
	 */
	public static final String DEFAULT_VERSION = "HEAD";

	private final List<File> modelFolders;

	private final String description;

	private EarlierModels(List<File> modelFolders, String description) {
		this.modelFolders = Collections.unmodifiableList(new ArrayList<>(modelFolders));
		this.description = description;
	}

	/**
	 * Returns the folders whose models are the earlier version, one for each
	 * of the project's model folders, for a commit.
	 *
	 * @return model folders
	 */
	public List<File> getModelFolders() {
		return modelFolders;
	}

	/**
	 * Returns what the earlier version is, for messages and a migration's
	 * comment: {@code commit 1a2b3c4}, or {@code the models in old}.
	 *
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Reads the earlier version of a project's models: a folder of models, if
	 * {@code version} names one, relative to the project; or else a git
	 * commit, such as {@code HEAD}, a branch or a tag, whose files in the
	 * model folders git writes to a scratch folder.
	 *
	 * @param projectDirectory the project folder
	 * @param modelFolders the project's model folders
	 * @param version a folder, or a commit
	 * @param scratch an empty folder to write a commit's models to
	 * @return the earlier version
	 * @throws IOException if the folder isn't a git repository, the commit
	 *         doesn't exist, or git fails
	 */
	public static EarlierModels read(File projectDirectory, List<File> modelFolders, String version, Path scratch) throws IOException {
		String from = version == null || version.trim().isEmpty() ? DEFAULT_VERSION : version.trim();
		File folder = new File(from);
		if (!folder.isAbsolute()) {
			folder = new File(projectDirectory, from);
		}
		if (folder.isDirectory()) {
			return new EarlierModels(Collections.singletonList(folder.getAbsoluteFile()), "the models in " + from.replace(File.separatorChar, '/'));
		}
		Path root = root(projectDirectory);
		String commit;
		try {
			commit = git(root.toFile(), "rev-parse", "--verify", "--quiet", from + "^{commit}").trim();
		} catch (IOException e) {
			throw new IOException(from + " is neither a folder nor a commit in " + root + ".", e);
		}
		String abbreviated = git(root.toFile(), "rev-parse", "--short", commit).trim();
		List<File> folders = new ArrayList<>();
		for (int i = 0; i < modelFolders.size(); i++) {
			File target = scratch.resolve(Integer.toString(i)).toFile();
			Files.createDirectories(target.toPath());
			folders.add(target);
			File modelFolder = modelFolders.get(i);
			if (!modelFolder.isDirectory()) {
				continue;
			}
			Path path = modelFolder.toPath().toRealPath();
			if (!path.startsWith(root)) {
				throw new IOException(modelFolder + " is not in the git repository " + root + ".");
			}
			String prefix = root.relativize(path).toString().replace(File.separatorChar, '/');
			List<String> files = new ArrayList<>();
			String listing = git(root.toFile(), "ls-tree", "-r", "-z", "--name-only", commit, "--", prefix.isEmpty() ? "." : prefix);
			for (String file : listing.split("\u0000")) {
				String relative = prefix.isEmpty() ? file : file.startsWith(prefix + "/") ? file.substring(prefix.length() + 1) : null;
				if (relative != null && isModelFile(relative)) {
					files.add(file);
				}
			}
			extract(root.toFile(), commit, files, prefix, target.toPath());
		}
		return new EarlierModels(folders, "commit " + abbreviated);
	}

	/**
	 * Returns the commit that added a file at its path, or null if no commit
	 * has it, as for a file that isn't committed yet. It doesn't follow the
	 * file back through renames, since git takes a migration that began as a
	 * copy of the one before for a copy of it.
	 *
	 * @param projectDirectory the project folder
	 * @param file a file in the project
	 * @return the commit's full name, or null
	 * @throws IOException if the project isn't in a git repository, or git
	 *         fails
	 */
	public static String commitAdding(File projectDirectory, File file) throws IOException {
		Path root = root(projectDirectory);
		Path path = file.toPath().toRealPath();
		if (!path.startsWith(root)) {
			return null;
		}
		String relative = root.relativize(path).toString().replace(File.separatorChar, '/');
		String commit = git(root.toFile(), "log", "-1", "--format=%H", "--no-renames", "--diff-filter=A", "--", relative).trim();
		return commit.isEmpty() ? null : commit;
	}

	/**
	 * Returns the top of the git repository that a folder is in.
	 *
	 * @throws IOException if it isn't in one
	 */
	private static Path root(File directory) throws IOException {
		try {
			return Paths.get(git(directory, "rev-parse", "--show-toplevel").trim()).toRealPath();
		} catch (IOException e) {
			throw new IOException(directory + " is not in a git repository, so give a folder with the earlier version of the models.", e);
		}
	}

	/**
	 * Returns whether a file, relative to a model folder, is in a model: in a
	 * folder whose name ends {@code .eomodeld}, and in no hidden folder.
	 */
	private static boolean isModelFile(String relative) {
		List<String> segments = Arrays.asList(relative.split("/"));
		boolean model = false;
		for (int i = 0; i < segments.size() - 1; i++) {
			if (segments.get(i).startsWith(".")) {
				return false;
			}
			model |= segments.get(i).endsWith(".eomodeld");
		}
		return model;
	}

	/**
	 * Writes files of a commit into a folder, with {@code git cat-file --batch},
	 * which reads them all in one process.
	 */
	private static void extract(File root, String commit, List<String> files, String prefix, Path target) throws IOException {
		if (files.isEmpty()) {
			return;
		}
		Process process = new ProcessBuilder("git", "cat-file", "--batch").directory(root).redirectError(ProcessBuilder.Redirect.DISCARD).start();
		try (OutputStream requests = process.getOutputStream(); InputStream responses = process.getInputStream()) {
			for (String file : files) {
				requests.write((commit + ":" + file + "\n").getBytes(StandardCharsets.UTF_8));
				requests.flush();
				String header = readLine(responses);
				String[] fields = header.split(" ");
				if (fields.length != 3 || !"blob".equals(fields[1])) {
					throw new IOException("git cat-file couldn't read " + file + ": " + header);
				}
				byte[] contents = readFully(responses, Integer.parseInt(fields[2]));
				readLine(responses);
				Path output = target.resolve(prefix.isEmpty() ? file : file.substring(prefix.length() + 1));
				Files.createDirectories(output.getParent());
				Files.write(output, contents);
			}
		} finally {
			process.destroy();
		}
	}

	private static String readLine(InputStream in) throws IOException {
		ByteArrayOutputStream line = new ByteArrayOutputStream();
		int b;
		while ((b = in.read()) != -1 && b != '\n') {
			line.write(b);
		}
		if (b == -1 && line.size() == 0) {
			throw new IOException("git cat-file stopped.");
		}
		return new String(line.toByteArray(), StandardCharsets.UTF_8);
	}

	private static byte[] readFully(InputStream in, int length) throws IOException {
		byte[] contents = new byte[length];
		int read = 0;
		while (read < length) {
			int count = in.read(contents, read, length - read);
			if (count == -1) {
				throw new IOException("git cat-file stopped.");
			}
			read += count;
		}
		return contents;
	}

	/**
	 * Runs git in a folder, and returns what it writes.
	 *
	 * @throws IOException if git can't run, or fails, with what it says
	 */
	private static String git(File directory, String... arguments) throws IOException {
		List<String> command = new ArrayList<>();
		command.add("git");
		command.addAll(Arrays.asList(arguments));
		Path errors = Files.createTempFile("eogen-git", ".txt");
		try {
			Process process = new ProcessBuilder(command).directory(directory).redirectError(errors.toFile()).start();
			byte[] output;
			try (InputStream in = process.getInputStream()) {
				output = in.readAllBytes();
			}
			int status = process.waitFor();
			if (status != 0) {
				String message = new String(Files.readAllBytes(errors), StandardCharsets.UTF_8).trim();
				throw new IOException("git " + String.join(" ", arguments) + " failed" + (message.isEmpty() ? "." : ": " + message));
			}
			return new String(output, StandardCharsets.UTF_8);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted while running git.", e);
		} finally {
			Files.deleteIfExists(errors);
		}
	}
}

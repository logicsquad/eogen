package net.logicsquad.eogen.cli;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.logicsquad.eogen.EOGenerator;
import net.logicsquad.eogen.EOGeneratorException;
import net.logicsquad.eogen.GeneratedFile;
import net.logicsquad.eogen.GeneratedFile.Kind;
import net.logicsquad.eogen.GeneratedFile.Status;
import net.logicsquad.eogen.GenerationResult;

/**
 * Generates the entity classes for one .eogen file.
 *
 * <pre>
 * eogen [--project folder] [--models folders] [--classpath path] [--check] [--force] [--strict] file.eogen
 * </pre>
 */
public final class Main {
	/**
	 * Exit status when generating succeeds, or {@code --check} finds nothing
	 * stale.
	 */
	static final int OK = 0;

	/**
	 * Exit status when {@code --check} finds stale files.
	 */
	static final int STALE = 1;

	/**
	 * Exit status for bad arguments, when generating fails, or when the model
	 * group or a template has errors and {@code --force} is not given.
	 */
	static final int FAILED = 2;

	private static final String USAGE = String.join(System.lineSeparator(),
			"Usage: eogen [options] <file.eogen>",
			"",
			"Generates the entity classes for an .eogen file as WOLips's EOGenerator does:",
			"every superclass whose contents have changed, and every subclass that is",
			"missing.",
			"",
			"Options:",
			"  --project <folder>    The folder that the .eogen file's paths are relative to.",
			"                        Default: the current folder.",
			"  --models <folders>    Folders whose models make up the model group, separated",
			"                        by '" + File.pathSeparator + "'. Default: <project>/src/main/resources.",
			"  --classpath <path>    Jars and folders whose Resources folders add their models",
			"                        to the model group, separated by '" + File.pathSeparator + "'.",
			"  --check               Write nothing. List the files that generating would",
			"                        change, and exit with status 1 if there are any.",
			"  --force               Write the classes even when the model group or a",
			"                        template has errors, as WOLips does. Without it, errors",
			"                        mean nothing is written, and the exit status is 2.",
			"  --strict              Make the lint's findings in the models, such as a",
			"                        misspelt key, errors rather than warnings.",
			"  --help                Show this help.",
			"  --version             Show eogen's version.");

	private Main() {
	}

	/**
	 * Runs eogen, and exits with its status.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", System.getProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn"));
		System.exit(run(args, System.out, System.err));
	}

	/**
	 * Runs eogen.
	 *
	 * @param args command-line arguments
	 * @param out where to report what was generated
	 * @param err where to report problems
	 * @return exit status: {@link #OK}, {@link #STALE} or {@link #FAILED}
	 */
	static int run(String[] args, PrintStream out, PrintStream err) {
		File project = new File(".");
		List<File> models = null;
		List<File> classpath = Collections.emptyList();
		boolean check = false;
		boolean force = false;
		boolean strict = false;
		File eogenFile = null;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if ("--help".equals(arg) || "-h".equals(arg)) {
				out.println(USAGE);
				return OK;
			} else if ("--version".equals(arg)) {
				String version = Main.class.getPackage().getImplementationVersion();
				out.println("eogen " + (version == null ? "(development)" : version));
				return OK;
			} else if ("--check".equals(arg)) {
				check = true;
			} else if ("--force".equals(arg)) {
				force = true;
			} else if ("--strict".equals(arg)) {
				strict = true;
			} else if ("--project".equals(arg) || "--models".equals(arg) || "--classpath".equals(arg)) {
				if (i + 1 == args.length) {
					return usage(err, arg + " needs a value.");
				}
				String value = args[++i];
				if ("--project".equals(arg)) {
					project = new File(value);
				} else if ("--models".equals(arg)) {
					models = files(value);
				} else {
					classpath = files(value);
				}
			} else if (arg.startsWith("-")) {
				return usage(err, "Unknown option " + arg + ".");
			} else if (eogenFile == null) {
				eogenFile = new File(arg);
			} else {
				return usage(err, "Give one .eogen file.");
			}
		}
		if (eogenFile == null) {
			return usage(err, "Give an .eogen file.");
		}
		if (!eogenFile.isFile()) {
			err.println("eogen: " + eogenFile + " does not exist.");
			return FAILED;
		}
		if (!project.isDirectory()) {
			err.println("eogen: the project folder " + project + " does not exist.");
			return FAILED;
		}
		if (models == null) {
			models = Collections.singletonList(new File(project, EOGenerator.DEFAULT_MODEL_FOLDER));
		}

		EOGenerator generator = new EOGenerator(project, models, classpath);
		generator.setForce(force);
		generator.setStrict(strict);
		GenerationResult result;
		try {
			result = check ? generator.check(eogenFile) : generator.generate(eogenFile);
		} catch (EOGeneratorException e) {
			err.println("eogen: " + e.getMessage());
			return FAILED;
		}
		for (String warning : result.getWarnings()) {
			err.println("eogen: warning: " + warning);
		}
		for (String error : result.getErrors()) {
			err.println("eogen: " + error);
		}
		if (result.hasErrors() && !force) {
			int errors = result.getErrors().size();
			err.println("eogen: " + errors + (errors == 1 ? " error" : " errors") + (check ? "." : ", so nothing was written. Fix them, or use --force to write anyway."));
			return FAILED;
		}
		if (check) {
			for (GeneratedFile file : result.getStaleFiles()) {
				out.println(relative(project, file.getFile()) + (file.getStatus() == Status.CREATED ? " is missing." : " is out of date."));
			}
			if (result.isStale()) {
				return STALE;
			}
			out.println("All " + (result.count(Kind.SUPERCLASS, Status.UNCHANGED)) + " generated superclasses are up to date.");
			return OK;
		}
		for (GeneratedFile file : result.getStaleFiles()) {
			out.println((file.getStatus() == Status.CREATED ? "Created " : "Updated ") + relative(project, file.getFile()));
		}
		out.println(eogenFile.getName() + ": " + result.count(Kind.SUPERCLASS, Status.CREATED) + " superclasses created, " + result.count(Kind.SUPERCLASS, Status.UPDATED)
				+ " updated, " + result.count(Kind.SUPERCLASS, Status.UNCHANGED) + " unchanged; " + result.count(Kind.SUBCLASS, Status.CREATED) + " subclasses created.");
		return OK;
	}

	/**
	 * Returns a file's path relative to the project, or its absolute path if it
	 * is outside the project.
	 */
	private static String relative(File project, File file) {
		Path projectPath = project.getAbsoluteFile().toPath().normalize();
		Path filePath = file.getAbsoluteFile().toPath().normalize();
		return filePath.startsWith(projectPath) ? projectPath.relativize(filePath).toString() : filePath.toString();
	}

	private static List<File> files(String path) {
		List<File> files = new ArrayList<>();
		for (String entry : path.split(File.pathSeparator)) {
			if (!entry.isEmpty()) {
				files.add(new File(entry));
			}
		}
		return files;
	}

	private static int usage(PrintStream err, String problem) {
		err.println("eogen: " + problem);
		err.println(USAGE);
		return FAILED;
	}
}

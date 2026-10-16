package net.logicsquad.eogen;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A copy of the test fixture project, {@code src/test/resources/fixtures/project}:
 * a Maven project with the Shop model and three .eogen files. Its dependency is
 * the Reference model, with the prototypes, which the copy packages as a jar
 * in Resources/, as a WebObjects framework would be.
 */
final class FixtureProject {
	/**
	 * The .eogen files in the fixture, and the destination each writes to.
	 */
	static final String[][] EOGEN_FILES = { { "ShopWonder", "src/main/java" }, { "ShopClassic", "generated/classic" }, { "ShopCustom", "generated/custom" } };

	final Path root;

	final Path referenceJar;

	private FixtureProject(Path root, Path referenceJar) {
		this.root = root;
		this.referenceJar = referenceJar;
	}

	/**
	 * Copies the fixture project into a folder.
	 *
	 * @param folder an empty folder
	 * @return the copy
	 * @throws IOException if the copy fails
	 */
	static FixtureProject copyTo(Path folder) throws IOException {
		Path root = folder.resolve("project");
		copyTree(fixtures().resolve("project"), root);
		Path referenceJar = folder.resolve("reference.jar");
		jar(fixtures().resolve("framework"), referenceJar);
		return new FixtureProject(root, referenceJar);
	}

	/**
	 * Returns a generator for the copy, with the Reference jar on its classpath.
	 *
	 * @return generator
	 */
	EOGenerator generator() {
		return new EOGenerator(root.toFile(), Collections.singletonList(root.resolve("src/main/resources").toFile()),
				Collections.singletonList(referenceJar.toFile()));
	}

	/**
	 * Returns a generator for the copy with nothing on its classpath.
	 *
	 * @return generator
	 */
	EOGenerator generatorWithoutDependencies() {
		return new EOGenerator(root.toFile());
	}

	File eogen(String name) {
		return root.resolve("src/main/resources/" + name + ".eogen").toFile();
	}

	Path resolve(String path) {
		return root.resolve(path);
	}

	/**
	 * Returns the folder of files that WOLips 5.0.3 generated from an .eogen
	 * file, with {@code src/test/oracle/run.sh}.
	 *
	 * @param name the .eogen file's name, without extension
	 * @return expected files
	 */
	static Path expected(String name) {
		return fixtures().resolve("expected").resolve(name);
	}

	static Path fixtures() {
		try {
			return Paths.get(FixtureProject.class.getResource("/fixtures").toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Returns the relative paths of the files under a folder.
	 *
	 * @param folder folder
	 * @return relative paths, sorted
	 * @throws IOException if the folder can't be read
	 */
	static List<String> files(Path folder) throws IOException {
		if (!Files.isDirectory(folder)) {
			return Collections.emptyList();
		}
		try (Stream<Path> paths = Files.walk(folder)) {
			return paths.filter(Files::isRegularFile).map(path -> folder.relativize(path).toString().replace(File.separatorChar, '/')).sorted()
					.collect(Collectors.toList());
		}
	}

	private static void copyTree(Path source, Path target) throws IOException {
		try (Stream<Path> paths = Files.walk(source)) {
			for (Path path : paths.collect(Collectors.toList())) {
				Path copy = target.resolve(source.relativize(path).toString());
				if (Files.isDirectory(path)) {
					Files.createDirectories(copy);
				} else {
					Files.copy(path, copy);
				}
			}
		}
	}

	/**
	 * Jars a folder, with an entry for each folder in it, as Maven's jar plugin
	 * does. WOLips only finds models in jars that have folder entries.
	 */
	static void jar(Path folder, Path jar) throws IOException {
		List<Path> paths;
		try (Stream<Path> walk = Files.walk(folder)) {
			paths = walk.filter(path -> !path.equals(folder)).sorted().collect(Collectors.toList());
		}
		try (OutputStream out = Files.newOutputStream(jar); JarOutputStream jarOut = new JarOutputStream(out)) {
			for (Path path : paths) {
				String name = folder.relativize(path).toString().replace(File.separatorChar, '/');
				if (Files.isDirectory(path)) {
					jarOut.putNextEntry(new JarEntry(name + "/"));
				} else {
					jarOut.putNextEntry(new JarEntry(name));
					jarOut.write(Files.readAllBytes(path));
				}
				jarOut.closeEntry();
			}
		}
	}
}

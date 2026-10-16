package net.logicsquad.eogen.golden;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Runs the Maven plugin on copies of real WebObjects projects, and compares
 * what it generates with the classes committed in them.
 * <p>
 * Each project in {@code eogen.golden.names} is copied from the folder
 * {@code eogen.golden.projects}, its pom.xml and src/main only, into
 * target/golden. Every {@code _*.java} superclass in the copy is deleted, then
 * {@code eogen:generate} must recreate each one byte for byte, and change
 * nothing else. {@code eogen:check} must then pass, and fail once a
 * superclass is edited and a subclass deleted. {@code eogen:verify} must
 * pass. Nothing is written to the projects themselves.
 * </p>
 * <p>
 * Run it with {@code mvn install -Deogen.golden.projects=<folder>
 * -Deogen.golden.names=<project>,<project>}: the tests run the plugin from the
 * local repository, so it must be installed first.
 * </p>
 */
@EnabledIfSystemProperty(named = "eogen.golden.projects", matches = ".+")
class GoldenProjectsTest {
	private static final String JAVA = "src/main/java";

	private static Path projects;

	private static Path work;

	private static String plugin;

	@BeforeAll
	static void findProjects() throws IOException {
		assertThat(projectNames()).as("Name the projects to test in eogen.golden.names, separated by commas.").isNotEmpty();
		projects = Paths.get(System.getProperty("eogen.golden.projects")).toAbsolutePath();
		work = Paths.get(System.getProperty("eogen.golden.work"));
		String version = System.getProperty("eogen.version");
		plugin = "net.logicsquad:eogen-maven-plugin:" + version + ":";
		Path built = Paths.get(System.getProperty("eogen.golden.plugin"));
		Path installed = Paths.get(System.getProperty("eogen.golden.repository"), "net/logicsquad/eogen-maven-plugin", version, "eogen-maven-plugin-" + version + ".jar");
		assertThat(installed).as("The plugin in the local repository must be the one just built. Run mvn install, not mvn verify.")
				.exists().hasSameBinaryContentAs(built);
	}

	static Stream<String> projectNames() {
		return Arrays.stream(System.getProperty("eogen.golden.names", "").split(",")).map(String::trim).filter(name -> !name.isEmpty());
	}

	@ParameterizedTest
	@MethodSource("projectNames")
	void generateRecreatesEverySuperclass(String name) throws Exception {
		Path original = projects.resolve(name);
		assertThat(original.resolve("pom.xml")).as(name + " must be a Maven project").exists();
		Path copy = copy(original, work.resolve(name));
		Map<String, byte[]> superclasses = superclasses(original.resolve(JAVA));
		assertThat(superclasses).as("committed superclasses in " + name).isNotEmpty();
		for (String superclass : superclasses(copy.resolve(JAVA)).keySet()) {
			Files.delete(copy.resolve(JAVA).resolve(superclass));
		}

		assertThat(mvn(copy, "generate")).as("mvn eogen:generate").isZero();

		assertSameFiles("superclasses generated for " + name, superclasses(copy.resolve(JAVA)), superclasses);
		assertSameFiles("every file in " + name + "'s " + JAVA, files(copy.resolve(JAVA)), files(original.resolve(JAVA)));
		assertThat(Files.readAllLines(log(copy, "generate"))).anyMatch(line -> line.contains("superclasses created"));

		assertThat(mvn(copy, "check")).as("mvn eogen:check").isZero();
		assertThat(Files.readAllLines(log(copy, "check"))).anyMatch(line -> line.matches(".*All \\d+ generated superclasses are up to date\\..*"));

		String stale = superclasses.keySet().iterator().next();
		Path staleFile = copy.resolve(JAVA).resolve(stale);
		Files.write(staleFile, "// Edited by the golden test.\n".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
		Path subclass = staleFile.resolveSibling(staleFile.getFileName().toString().substring(1));
		byte[] subclassContents = Files.readAllBytes(subclass);
		Files.delete(subclass);
		Map<String, byte[]> before = files(copy.resolve(JAVA));
		assertThat(mvn(copy, "check")).as("mvn eogen:check on a stale copy").isNotZero();
		List<String> checkLog = Files.readAllLines(log(copy, "check"));
		assertThat(checkLog).anyMatch(line -> line.contains(stale.replace('/', File.separatorChar) + " is out of date."))
				.anyMatch(line -> line.contains(subclass.getFileName() + " is missing."))
				.anyMatch(line -> line.contains("2 generated files are stale."));
		assertSameFiles("files after check", files(copy.resolve(JAVA)), before);
		Files.write(subclass, subclassContents);

		assertThat(mvn(copy, "verify")).as("mvn eogen:verify").isZero();
		assertThat(Files.readAllLines(log(copy, "verify"))).anyMatch(line -> line.contains("Verified ") && line.contains(" 0 errors"));
	}

	/**
	 * Runs one of the plugin's goals in a project, and returns Maven's exit
	 * status. The output goes to target/golden/name-goal.log.
	 */
	private static int mvn(Path project, String goal) throws IOException, InterruptedException {
		boolean windows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		Path mvn = Paths.get(System.getProperty("maven.home"), "bin", windows ? "mvn.cmd" : "mvn");
		ProcessBuilder builder = new ProcessBuilder(mvn.toString(), "-B", plugin + goal).directory(project.toFile()).redirectErrorStream(true)
				.redirectOutput(log(project, goal).toFile());
		Process process = builder.start();
		if (!process.waitFor(10, TimeUnit.MINUTES)) {
			process.destroyForcibly();
			throw new IllegalStateException("mvn eogen:" + goal + " took more than ten minutes in " + project);
		}
		return process.exitValue();
	}

	private static Path log(Path project, String goal) {
		return project.resolveSibling(project.getFileName() + "-" + goal + ".log");
	}

	/**
	 * Copies a project's pom.xml and src/main, which is all eogen needs.
	 */
	private static Path copy(Path project, Path copy) throws IOException {
		delete(copy);
		Files.createDirectories(copy);
		Files.copy(project.resolve("pom.xml"), copy.resolve("pom.xml"));
		Path source = project.resolve("src/main");
		List<Path> paths;
		try (Stream<Path> walk = Files.walk(source)) {
			paths = walk.collect(Collectors.toList());
		}
		for (Path path : paths) {
			Path target = copy.resolve("src/main").resolve(source.relativize(path).toString());
			if (Files.isDirectory(path)) {
				Files.createDirectories(target);
			} else {
				Files.copy(path, target);
			}
		}
		return copy;
	}

	/**
	 * Returns the _*.java files under a folder, by relative path, with their
	 * contents.
	 */
	private static Map<String, byte[]> superclasses(Path folder) throws IOException {
		Map<String, byte[]> superclasses = new TreeMap<>();
		for (Map.Entry<String, byte[]> file : files(folder).entrySet()) {
			String fileName = Paths.get(file.getKey()).getFileName().toString();
			if (fileName.startsWith("_") && fileName.endsWith(".java")) {
				superclasses.put(file.getKey(), file.getValue());
			}
		}
		return superclasses;
	}

	/**
	 * Returns every file under a folder, by relative path, with its contents.
	 */
	private static Map<String, byte[]> files(Path folder) throws IOException {
		Map<String, byte[]> files = new TreeMap<>();
		List<Path> paths;
		try (Stream<Path> walk = Files.walk(folder)) {
			paths = walk.filter(Files::isRegularFile).collect(Collectors.toList());
		}
		for (Path path : paths) {
			files.put(folder.relativize(path).toString().replace(File.separatorChar, '/'), Files.readAllBytes(path));
		}
		return files;
	}

	/**
	 * Asserts that two sets of files have the same paths and contents.
	 */
	private static void assertSameFiles(String description, Map<String, byte[]> actual, Map<String, byte[]> expected) {
		assertThat(actual.keySet()).as(description).containsExactlyElementsOf(expected.keySet());
		for (Map.Entry<String, byte[]> file : expected.entrySet()) {
			assertThat(actual.get(file.getKey())).as(description + ": " + file.getKey()).isEqualTo(file.getValue());
		}
	}

	private static void delete(Path folder) throws IOException {
		if (!Files.exists(folder)) {
			return;
		}
		List<Path> paths;
		try (Stream<Path> walk = Files.walk(folder)) {
			paths = walk.sorted((a, b) -> b.getNameCount() - a.getNameCount()).collect(Collectors.toList());
		}
		for (Path path : paths) {
			Files.delete(path);
		}
	}
}

package net.logicsquad.eogen.maven;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

/**
 * Runs the goals in Maven, on projects with a Library model whose prototypes
 * and Currency entity come from a dependency, target/it-reference.jar, or the
 * same jar in a Maven repository, target/it-repo.
 */
@MavenJupiterExtension
public class EOGenMojoIT {
	private static final String LIBRARY = "src/main/java/com/example/library/";

	/**
	 * Jars src/test/resources/it-reference, as a WebObjects framework would be,
	 * for the test projects' system-scoped dependency, and puts the jar in a
	 * repository for those that resolve it.
	 */
	@BeforeAll
	static void buildReferenceJar() throws IOException {
		Path folder = Paths.get("target/test-classes/it-reference");
		List<Path> paths;
		try (Stream<Path> walk = Files.walk(folder)) {
			paths = walk.filter(path -> !path.equals(folder)).sorted().collect(Collectors.toList());
		}
		try (OutputStream out = Files.newOutputStream(Paths.get("target/it-reference.jar")); JarOutputStream jar = new JarOutputStream(out)) {
			for (Path path : paths) {
				String name = folder.relativize(path).toString().replace(File.separatorChar, '/');
				jar.putNextEntry(new JarEntry(Files.isDirectory(path) ? name + "/" : name));
				if (!Files.isDirectory(path)) {
					jar.write(Files.readAllBytes(path));
				}
				jar.closeEntry();
			}
		}
		Path repository = Files.createDirectories(Paths.get("target/it-repo/net/logicsquad/eogen/it/reference/1.0"));
		Files.copy(Paths.get("target/it-reference.jar"), repository.resolve("reference-1.0.jar"), StandardCopyOption.REPLACE_EXISTING);
		Files.write(repository.resolve("reference-1.0.pom"), ("<project xmlns=\"http://maven.apache.org/POM/4.0.0\">\n  <modelVersion>4.0.0</modelVersion>\n"
				+ "  <groupId>net.logicsquad.eogen.it</groupId>\n  <artifactId>reference</artifactId>\n  <version>1.0</version>\n</project>\n").getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Generates twice: the first run creates every class, the second changes
	 * nothing. The superclasses use prototypes and an entity from the jar.
	 */
	@MavenTest
	void generate(MavenExecutionResult result) throws IOException {
		assertThat(result).isSuccessful();
		assertThat(result).out().info().contains("Created " + LIBRARY + "_Book.java", "Created " + LIBRARY + "Book.java")
				.anyMatch(line -> line.equals("src/main/resources/Library.eogen: 2 superclasses created, 0 updated, 0 unchanged; 2 subclasses created."))
				.anyMatch(line -> line.equals("src/main/resources/Library.eogen: 0 superclasses created, 0 updated, 2 unchanged; 0 subclasses created."));
		Path project = result.getMavenProjectResult().getTargetProjectDirectory();
		String book = read(project.resolve(LIBRARY + "_Book.java"));
		assertThat(book).contains("public abstract class _Book", "public java.math.BigDecimal price()", "public com.example.reference.Currency currency()");
		assertThat(project.resolve(LIBRARY + "Author.java")).exists();
		assertThat(project.resolve(LIBRARY + "_Author.java")).exists();
	}

	/**
	 * The jar is a runtime dependency here, as a framework of prototypes often
	 * is, and its models still join the model group.
	 */
	@MavenTest
	void generateWithRuntimeDependency(MavenExecutionResult result) throws IOException {
		assertThat(result).isSuccessful();
		String book = read(result.getMavenProjectResult().getTargetProjectDirectory().resolve(LIBRARY + "_Book.java"));
		assertThat(book).contains("public java.math.BigDecimal price()", "public com.example.reference.Currency currency()");
	}

	@MavenTest
	void checkAfterGenerate(MavenExecutionResult result) {
		assertThat(result).isSuccessful();
		assertThat(result).out().info().contains("All 2 generated superclasses are up to date.");
	}

	/**
	 * Checks a project with an old superclass and no other generated classes:
	 * check fails, names every stale file, and changes none of them.
	 */
	@MavenTest
	void checkStale(MavenExecutionResult result) throws IOException {
		assertThat(result).isFailure();
		assertThat(result).out().error().contains(LIBRARY + "_Book.java is out of date.", LIBRARY + "_Author.java is missing.", LIBRARY + "Author.java is missing.",
				LIBRARY + "Book.java is missing.");
		assertThat(result).out().error().anyMatch(line -> line.contains("4 generated files are stale. Run mvn eogen:generate, and commit the result."));
		Path project = result.getMavenProjectResult().getTargetProjectDirectory();
		assertThat(read(project.resolve(LIBRARY + "_Book.java"))).contains("An old superclass");
		assertThat(project.resolve(LIBRARY + "Book.java")).doesNotExist();
	}

	@MavenTest
	void verify(MavenExecutionResult result) {
		assertThat(result).isSuccessful();
		assertThat(result).out().info().contains("Verified Library: 0 errors, 0 warnings.");
	}

	/**
	 * Without the jar, the Library model's prototypes and Currency entity are
	 * missing, and verify fails.
	 */
	@MavenTest
	void verifyWithoutDependency(MavenExecutionResult result) {
		assertThat(result).isFailure();
		assertThat(result).out().error().anyMatch(line -> line.startsWith("Library: ") && line.contains("Currency"));
	}

	/**
	 * Without the jar, the Library model's prototypes and Currency entity are
	 * missing, so generate fails and writes nothing.
	 */
	@MavenTest
	void generateWithModelErrors(MavenExecutionResult result) {
		assertThat(result).isFailure();
		assertThat(result).out().error().anyMatch(line -> line.startsWith("src/main/resources/Library.eogen: ") && line.contains("Currency"))
				.contains("src/main/resources/Library.eogen: nothing written.")
				.anyMatch(line -> line.contains("so the classes for the .eogen files that have them were not written. Fix them, or set eogen.force to write them anyway."));
		assertThat(result.getMavenProjectResult().getTargetProjectDirectory().resolve("src/main/java")).doesNotExist();
	}

	/**
	 * With force, generate writes the classes despite the errors, as WOLips
	 * does, and reports the errors as warnings.
	 */
	@MavenTest
	void generateForced(MavenExecutionResult result) {
		assertThat(result).isSuccessful();
		assertThat(result).out().warn().anyMatch(line -> line.startsWith("src/main/resources/Library.eogen: ") && line.contains("Currency"));
		assertThat(result.getMavenProjectResult().getTargetProjectDirectory().resolve(LIBRARY + "_Book.java")).exists();
	}

	@MavenTest
	void skip(MavenExecutionResult result) {
		assertThat(result).isSuccessful();
		assertThat(result).out().info().contains("Skipping eogen.");
		assertThat(result.getMavenProjectResult().getTargetProjectDirectory().resolve("src/main/java")).doesNotExist();
	}

	private static String read(Path path) throws IOException {
		return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
	}
}

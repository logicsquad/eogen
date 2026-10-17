package net.logicsquad.eogen.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Runs the command line on a copy of a project with a Library model, whose
 * prototypes and Currency entity come from a Reference model on the
 * classpath.
 */
class MainTest {
	private static final String LIBRARY = "src/main/java/com/example/library/".replace('/', File.separatorChar);

	@TempDir
	Path temp;

	private Path project;

	private Path reference;

	private final ByteArrayOutputStream out = new ByteArrayOutputStream();

	private final ByteArrayOutputStream err = new ByteArrayOutputStream();

	@BeforeEach
	void copyProject() throws IOException, URISyntaxException {
		project = copy("library", temp.resolve("library"));
		reference = copy("reference", temp.resolve("reference"));
	}

	@Test
	void generatesTheClasses() throws Exception {
		assertThat(run("--project", project.toString(), "--classpath", reference.toString(), eogen())).isEqualTo(Main.OK);
		assertThat(out()).contains("Created " + LIBRARY + "_Book.java", "Created " + LIBRARY + "Book.java",
				"Library.eogen: 2 superclasses created, 0 updated, 0 unchanged; 2 subclasses created.");
		assertThat(err()).isEmpty();
		String book = new String(Files.readAllBytes(project.resolve(LIBRARY + "_Book.java")), StandardCharsets.UTF_8);
		assertThat(book).contains("public java.math.BigDecimal price()", "public com.example.reference.Currency currency()");
	}

	@Test
	void checkReportsStaleFilesAndWritesNothing() throws Exception {
		assertThat(run("--check", "--project", project.toString(), "--classpath", reference.toString(), eogen())).isEqualTo(Main.STALE);
		assertThat(out()).contains(LIBRARY + "_Book.java is missing.", LIBRARY + "Author.java is missing.");
		assertThat(project.resolve("src/main/java")).doesNotExist();
	}

	@Test
	void checkPassesAfterGenerating() throws Exception {
		run("--project", project.toString(), "--classpath", reference.toString(), eogen());
		out.reset();
		assertThat(run("--check", "--project", project.toString(), "--classpath", reference.toString(), eogen())).isEqualTo(Main.OK);
		assertThat(out()).contains("All 2 generated superclasses are up to date.");
	}

	@Test
	void modelFoldersCanBeGiven() throws Exception {
		String models = project.resolve("src/main/resources") + File.pathSeparator + reference.resolve("Resources");
		assertThat(run("--project", project.toString(), "--models", models, eogen())).isEqualTo(Main.OK);
		assertThat(project.resolve(LIBRARY + "_Author.java")).exists();
	}

	/**
	 * Without the Reference model, the prototypes and Currency are missing, so
	 * the classes would have unresolved references in them.
	 */
	@Test
	void modelErrorsMeanNothingIsWritten() throws Exception {
		assertThat(run("--project", project.toString(), eogen())).isEqualTo(Main.FAILED);
		assertThat(err()).contains("Currency", "$attribute.javaClassName has no value for attribute price",
				"errors, so nothing was written. Fix them, or use --force to write anyway.");
		assertThat(project.resolve("src/main/java")).doesNotExist();
	}

	@Test
	void checkFailsOnModelErrors() throws Exception {
		assertThat(run("--check", "--project", project.toString(), eogen())).isEqualTo(Main.FAILED);
		assertThat(err()).contains("Currency");
		assertThat(out()).doesNotContain("is missing.");
	}

	@Test
	void forceWritesDespiteModelErrors() throws Exception {
		assertThat(run("--force", "--project", project.toString(), eogen())).isEqualTo(Main.OK);
		assertThat(err()).contains("Currency");
		assertThat(project.resolve(LIBRARY + "_Book.java")).exists();
	}

	@Test
	void lintFindingsAreWarningsOrErrorsWhenStrict() throws Exception {
		Path book = project.resolve("src/main/resources/Library.eomodeld/Book.plist");
		String text = new String(Files.readAllBytes(book), StandardCharsets.UTF_8);
		assertThat(text).contains("classProperties = (author, currency, price, title);");
		Files.write(book, text.replace("classProperties = (author, currency, price, title);", "classProperties = (author, currency, price, titel);").getBytes(StandardCharsets.UTF_8));
		String finding = "Library.eomodeld/Book.plist: entity Book: classProperties names titel, which is not an attribute or relationship (did you mean title?).";

		assertThat(run("--strict", "--project", project.toString(), "--classpath", reference.toString(), eogen())).isEqualTo(Main.FAILED);
		assertThat(err()).contains("eogen: " + finding);
		assertThat(project.resolve("src/main/java")).doesNotExist();

		err.reset();
		assertThat(run("--project", project.toString(), "--classpath", reference.toString(), eogen())).isEqualTo(Main.OK);
		assertThat(err()).contains("eogen: warning: " + finding);
		assertThat(project.resolve(LIBRARY + "_Book.java")).exists();
	}

	@Test
	void helpAndVersion() throws Exception {
		assertThat(run("--help")).isEqualTo(Main.OK);
		assertThat(out()).startsWith("Usage: eogen [options] <file.eogen>");
		out.reset();
		assertThat(run("--version")).isEqualTo(Main.OK);
		assertThat(out()).startsWith("eogen ");
	}

	@Test
	void rejectsBadArguments() throws Exception {
		assertThat(run()).isEqualTo(Main.FAILED);
		assertThat(err()).contains("Give an .eogen file.", "Usage: eogen");
		err.reset();
		assertThat(run("--frobnicate", eogen())).isEqualTo(Main.FAILED);
		assertThat(err()).contains("Unknown option --frobnicate.");
		err.reset();
		assertThat(run("--project")).isEqualTo(Main.FAILED);
		assertThat(err()).contains("--project needs a value.");
		err.reset();
		assertThat(run(eogen(), eogen())).isEqualTo(Main.FAILED);
		assertThat(err()).contains("Give one .eogen file.");
	}

	@Test
	void reportsMissingFiles() throws Exception {
		assertThat(run("--project", project.toString(), temp.resolve("Missing.eogen").toString())).isEqualTo(Main.FAILED);
		assertThat(err()).contains("Missing.eogen does not exist.");
		err.reset();
		assertThat(run("--project", temp.resolve("nowhere").toString(), eogen())).isEqualTo(Main.FAILED);
		assertThat(err()).contains("does not exist.");
	}

	@Test
	void reportsGenerationFailures() throws Exception {
		Files.write(project.resolve("src/main/resources/Library.eogen"), " -model src/main/resources/Library.eomodeld\n".getBytes(StandardCharsets.UTF_8));
		assertThat(run("--project", project.toString(), eogen())).isEqualTo(Main.FAILED);
		assertThat(err()).contains("has no -destination");
	}

	private String eogen() {
		return project.resolve("src/main/resources/Library.eogen").toString();
	}

	private int run(String... args) throws UnsupportedEncodingException {
		return Main.run(args, new PrintStream(out, true, "UTF-8"), new PrintStream(err, true, "UTF-8"));
	}

	private String out() throws UnsupportedEncodingException {
		return out.toString("UTF-8");
	}

	private String err() throws UnsupportedEncodingException {
		return err.toString("UTF-8");
	}

	private static Path copy(String resource, Path target) throws IOException, URISyntaxException {
		Path source = Paths.get(MainTest.class.getResource("/" + resource).toURI());
		List<Path> paths;
		try (Stream<Path> walk = Files.walk(source)) {
			paths = walk.collect(Collectors.toList());
		}
		for (Path path : paths) {
			Path copy = target.resolve(source.relativize(path).toString());
			if (Files.isDirectory(path)) {
				Files.createDirectories(copy);
			} else {
				Files.copy(path, copy);
			}
		}
		return target;
	}
}

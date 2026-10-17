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
	private static final String LIBRARY = "src/main/java/com/example/library/";

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

	/**
	 * The fixture's models were written by hand, not by Entity Modeler.
	 */
	@Test
	void formatsTheModels() throws Exception {
		String book = "src/main/resources/Library.eomodeld/Book.plist";
		assertThat(run("--format", "--check", "--project", project.toString(), "--classpath", reference.toString())).isEqualTo(Main.STALE);
		assertThat(out()).contains(book + " is not formatted as Entity Modeler writes it.");
		out.reset();
		assertThat(run("--format", "--project", project.toString(), "--classpath", reference.toString())).isEqualTo(Main.OK);
		assertThat(out()).contains("Formatted " + book);
		out.reset();
		assertThat(run("--format", "--check", "--project", project.toString(), "--classpath", reference.toString())).isEqualTo(Main.OK);
		assertThat(out()).contains("model files checked, 0 to format.");

		Path path = project.resolve("src/main/resources/Library.eomodeld/Book.plist");
		String text = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		assertThat(text).contains("isMandatory = Y;");
		Files.write(path, text.replaceFirst("isMandatory = Y;", "isMandatory = YES;").getBytes(StandardCharsets.UTF_8));
		assertThat(run("--format", "--project", project.toString(), "--classpath", reference.toString())).isEqualTo(Main.FAILED);
		assertThat(err()).contains(book + ": its model is left alone, since saving it would change what it says: ", ".isMandatory would change from YES to Y.");
		err.reset();
		assertThat(run("--format", "--verify")).isEqualTo(Main.FAILED);
		assertThat(err()).contains("--format takes no .eogen files, and goes only with --check.");
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
		assertThat(run("--all", eogen())).isEqualTo(Main.FAILED);
		assertThat(err()).contains("Give .eogen files or --all, not both.");
		err.reset();
		assertThat(run("--verify", "--check")).isEqualTo(Main.FAILED);
		assertThat(err()).contains("--verify takes no .eogen files");
		err.reset();
		assertThat(run("--delete-orphans", eogen())).isEqualTo(Main.FAILED);
		assertThat(err()).contains("--delete-orphans needs --all");
		err.reset();
		assertThat(run("--include-dependencies", eogen())).isEqualTo(Main.FAILED);
		assertThat(err()).contains("--include-dependencies goes with --verify.");
	}

	@Test
	void allUsesEveryEOGenFileAndReportsOrphans() throws Exception {
		Path library = Files.createDirectories(project.resolve(LIBRARY));
		Files.write(library.resolve("_Loan.java"), "// DO NOT EDIT.  Make changes to com.example.library.Loan.java instead.\n".getBytes(StandardCharsets.UTF_8));
		Files.write(library.resolve("Loan.java"), "class Loan extends _Loan {}\n".getBytes(StandardCharsets.UTF_8));
		assertThat(run("--all", "--project", project.toString(), "--classpath", reference.toString())).isEqualTo(Main.OK);
		assertThat(out()).contains("Created " + LIBRARY + "_Book.java", LIBRARY + "_Loan.java is an orphan: no .eogen file generates it any more.",
				LIBRARY + "Loan.java is the subclass of an orphaned superclass. Delete it by hand if its entity is gone.");

		out.reset();
		assertThat(run("--all", "--check", "--project", project.toString(), "--classpath", reference.toString())).isEqualTo(Main.STALE);
		assertThat(out()).contains("src/main/resources/Library.eogen: All 2 generated superclasses are up to date.",
				LIBRARY + "_Loan.java is an orphan");

		out.reset();
		assertThat(run("--all", "--delete-orphans", "--project", project.toString(), "--classpath", reference.toString())).isEqualTo(Main.OK);
		assertThat(out()).contains("Deleted " + LIBRARY + "_Loan.java, which no .eogen file generates any more.");
		assertThat(library.resolve("_Loan.java")).doesNotExist();
		assertThat(library.resolve("Loan.java")).exists();
	}

	@Test
	void generatesSeveralEOGenFiles() throws Exception {
		Path second = project.resolve("src/main/resources/Second.eogen");
		Files.write(second, new String(Files.readAllBytes(Paths.get(eogen())), StandardCharsets.UTF_8).replace("src/main/java", "generated").getBytes(StandardCharsets.UTF_8));
		assertThat(run("--project", project.toString(), "--classpath", reference.toString(), eogen(), second.toString())).isEqualTo(Main.OK);
		assertThat(project.resolve(LIBRARY + "_Book.java")).exists();
		assertThat(project.resolve("generated/com/example/library/_Book.java")).exists();
	}

	@Test
	void verifyReportsTheModelGroup() throws Exception {
		assertThat(run("--verify", "--project", project.toString(), "--classpath", reference.toString())).isEqualTo(Main.OK);
		assertThat(out()).contains("Verified Library: 0 errors, 0 warnings.");
		out.reset();
		assertThat(run("--verify", "--include-dependencies", "--project", project.toString(), "--classpath", reference.toString())).isEqualTo(Main.OK);
		assertThat(out()).contains("Verified Library, Reference: 0 errors, 0 warnings.");
		out.reset();
		assertThat(run("--verify", "--project", project.toString())).isEqualTo(Main.FAILED);
		assertThat(err()).contains("eogen: Library: ", "Currency");
	}

	@Test
	void jsonReportsWhatHappened() throws Exception {
		assertThat(run("--json", "--check", "--project", project.toString(), "--classpath", reference.toString(), eogen())).isEqualTo(Main.STALE);
		String json = out();
		assertThat(json).startsWith("{\n  \"status\": \"stale\",\n  \"eogenFiles\": [\n").contains("\"written\": false", "\"kind\": \"superclass\"",
				"\"status\": \"created\"", "\"file\": \"" + LIBRARY + "_Book.java\"").doesNotContain("is missing.");
		out.reset();
		assertThat(run("--json", "--verify", "--project", project.toString())).isEqualTo(Main.FAILED);
		assertThat(out()).startsWith("{\n  \"status\": \"failed\",\n  \"verification\": {\n").contains("\"model\": \"Library\"");
	}

	@Test
	void jsonGivesEachFindingItsCodeAndWhereItIs() throws Exception {
		Path book = project.resolve("src/main/resources/Library.eomodeld/Book.plist");
		String text = new String(Files.readAllBytes(book), StandardCharsets.UTF_8);
		assertThat(text).containsPattern("name = currencyCode;\\s+prototypeName = varchar100;");
		Files.write(book, text.replaceFirst("(name = currencyCode;\\s+)prototypeName", "$1prototypeNam").getBytes(StandardCharsets.UTF_8));
		assertThat(run("--json", "--check", "--project", project.toString(), "--classpath", reference.toString(), eogen())).isEqualTo(Main.STALE);
		assertThat(out()).contains("\"warnings\": [\n        {\n          \"severity\": \"warning\",\n          \"code\": \"unknown-key\",\n"
				+ "          \"message\": \"Library.eomodeld/Book.plist: attribute currencyCode: unknown key prototypeNam (did you mean prototypeName?).\",\n"
				+ "          \"file\": \"src/main/resources/Library.eomodeld/Book.plist\",\n          \"model\": \"Library\",\n          \"entity\": \"Book\",\n"
				+ "          \"object\": \"attribute currencyCode\",\n          \"key\": \"prototypeNam\",\n          \"suggestion\": \"prototypeName\"\n        }");

		out.reset();
		Files.write(book, text.replace("name = price;", "name = price").getBytes(StandardCharsets.UTF_8));
		assertThat(run("--json", "--verify", "--project", project.toString(), "--classpath", reference.toString())).isEqualTo(Main.FAILED);
		assertThat(out()).containsPattern("\"code\": \"syntax\",\n.*\n\\s*\"file\": \"src/main/resources/Library.eomodeld/Book.plist\",\n\\s*\"line\": \\d+,\n\\s*\"column\": \\d+,\n"
				+ "\\s*\"model\": \"Library\"");

		out.reset();
		Files.write(project.resolve("src/main/resources/Library.eogen"), " -model src/main/resources/Library.eomodeld\n".getBytes(StandardCharsets.UTF_8));
		assertThat(run("--json", "--project", project.toString(), eogen())).isEqualTo(Main.FAILED);
		assertThat(out()).contains("\"problems\": [\n    {\n      \"severity\": \"error\",\n      \"code\": \"eogen-file\",\n",
				"\"file\": \"src/main/resources/Library.eogen\"");
	}

	@Test
	void aModelThatFailsToParseIsReportedInOneLine() throws Exception {
		Path book = project.resolve("src/main/resources/Library.eomodeld/Book.plist");
		String text = new String(Files.readAllBytes(book), StandardCharsets.UTF_8);
		assertThat(text).contains("name = price;");
		Files.write(book, text.replace("name = price;", "name = price").getBytes(StandardCharsets.UTF_8));
		assertThat(run("--project", project.toString(), "--classpath", reference.toString(), eogen())).isEqualTo(Main.FAILED);
		assertThat(err()).containsPattern("Library failed to load: Library\\.eomodeld/Book\\.plist:\\d+:\\d+: Property list parsing failed")
				.doesNotContain("\tat ").doesNotContain("At line number").doesNotContain("Failed to load the model");
		assertThat(err().split("Library failed to load", -1)).hasSize(2);
		err.reset();
		assertThat(run("--debug", "--project", project.toString(), "--classpath", reference.toString(), eogen())).isEqualTo(Main.FAILED);
		assertThat(err()).contains("\tat ");
	}

	@Test
	void templatesComeFromTheTemplatePathBeforeTheBundledOnes() throws Exception {
		Path templates = Files.createDirectories(temp.resolve("templates"));
		Files.write(templates.resolve("_Entity.java"), "// shared\npublic class $entity.prefixClassNameWithoutPackage {}\n".getBytes(StandardCharsets.UTF_8));
		assertThat(run("--project", project.toString(), "--classpath", reference.toString(), "--templates", templates.toString(), eogen())).isEqualTo(Main.OK);
		assertThat(new String(Files.readAllBytes(project.resolve(LIBRARY + "_Book.java")), StandardCharsets.UTF_8)).isEqualTo("// shared\npublic class _Book {}\n");
		// The folder has no Entity.java, so the subclass comes from the bundled one.
		assertThat(new String(Files.readAllBytes(project.resolve(LIBRARY + "Book.java")), StandardCharsets.UTF_8)).contains("public class Book extends");
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

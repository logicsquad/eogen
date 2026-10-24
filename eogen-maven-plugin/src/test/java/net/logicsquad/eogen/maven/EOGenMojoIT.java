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

import com.soebes.itf.jupiter.extension.MavenGoal;
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

	/**
	 * In a reactor, the app's prototypes and Currency come from a sibling
	 * framework, which Maven resolves to its target/classes, with its models in
	 * Resources, until it is packaged.
	 */
	@MavenTest
	@MavenGoal("compile")
	void generateInReactor(MavenExecutionResult result) throws IOException {
		assertThat(result).isSuccessful();
		assertThat(result).out().info().anyMatch(line -> line.equals("src/main/resources/Library.eogen: 2 superclasses created, 0 updated, 0 unchanged; 2 subclasses created."));
		String book = read(result.getMavenProjectResult().getTargetProjectDirectory().resolve("app/" + LIBRARY + "_Book.java"));
		assertThat(book).contains("public java.math.BigDecimal price()", "public com.example.reference.Currency currency()");
	}

	/**
	 * The plugin finds templates in a jar that it depends on, as a team shares
	 * them: here the reactor's templates module, which has them in
	 * eogen/templates.
	 */
	@MavenTest
	@MavenGoal("package")
	void generateWithSharedTemplates(MavenExecutionResult result) throws IOException {
		assertThat(result).isSuccessful();
		Path app = result.getMavenProjectResult().getTargetProjectDirectory().resolve("app");
		assertThat(read(app.resolve(LIBRARY + "_Book.java"))).isEqualTo("// From the shared templates.\npublic class _Book {}\n");
		assertThat(read(app.resolve(LIBRARY + "Book.java"))).isEqualTo("public class Book extends _Book {}\n");
	}

	/**
	 * With modelDirectories, the models are in models, and the broken model in
	 * src/main/resources is not part of the model group.
	 */
	@MavenTest
	void generateWithModelDirectories(MavenExecutionResult result) {
		assertThat(result).isSuccessful();
		assertThat(result).out().info().contains("Created " + LIBRARY + "_Book.java")
				.anyMatch(line -> line.equals("models/Library.eogen: 2 superclasses created, 0 updated, 0 unchanged; 2 subclasses created."));
		assertThat(result).out().plain().noneMatch(line -> line.contains("Broken"));
	}

	/**
	 * With eogenFiles naming Library.eogen alone, generate leaves Missing.eogen
	 * alone, and doesn't look for orphans, so Loan's classes go unreported.
	 */
	@MavenTest
	void generateNamedEogenFiles(MavenExecutionResult result) {
		assertThat(result).isSuccessful();
		assertThat(result).out().info()
				.anyMatch(line -> line.equals("src/main/resources/Library.eogen: 2 superclasses created, 0 updated, 0 unchanged; 2 subclasses created."));
		assertThat(result).out().plain().noneMatch(line -> line.contains("Missing") || line.contains("orphan"));
		assertThat(result).out().warn().noneMatch(line -> line.contains("orphan"));
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

	/**
	 * The project has a superclass and a subclass for Loan, an entity the model
	 * no longer has, so check fails on both.
	 */
	@MavenTest
	void checkOrphan(MavenExecutionResult result) {
		assertThat(result).isFailure();
		assertThat(result).out().error().contains(LIBRARY + "_Loan.java is an orphan: no .eogen file generates it any more.",
				LIBRARY + "Loan.java is the subclass of an orphaned superclass.")
				.anyMatch(line -> line.contains("2 orphans. Run mvn eogen:generate -Deogen.deleteOrphans to delete the superclasses"));
	}

	/**
	 * With checkOrphans false, check passes despite Loan's classes, which
	 * generate still reports.
	 */
	@MavenTest
	void checkIgnoresOrphans(MavenExecutionResult result) {
		assertThat(result).isSuccessful();
		assertThat(result).out().info().contains("All 2 generated superclasses are up to date.");
		assertThat(result).out().warn().contains(LIBRARY + "_Loan.java is an orphan: no .eogen file generates it any more. Delete it, or set eogen.deleteOrphans.");
		assertThat(result).out().error().isEmpty();
	}

	/**
	 * With deleteOrphans, generate deletes Loan's superclass, and leaves its
	 * subclass, which was edited by hand.
	 */
	@MavenTest
	void generateDeletesOrphans(MavenExecutionResult result) {
		assertThat(result).isSuccessful();
		assertThat(result).out().info().contains("Deleted " + LIBRARY + "_Loan.java, which no .eogen file generates any more.");
		assertThat(result).out().warn().contains(LIBRARY + "Loan.java is the subclass of an orphaned superclass. Delete it by hand if its entity is gone.");
		Path project = result.getMavenProjectResult().getTargetProjectDirectory();
		assertThat(project.resolve(LIBRARY + "_Loan.java")).doesNotExist();
		assertThat(project.resolve(LIBRARY + "Loan.java")).exists();
		assertThat(project.resolve(LIBRARY + "_Book.java")).exists();
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

	/**
	 * Book's class properties name titel, which is not an attribute, so the
	 * lint reports it, and strict makes that an error.
	 */
	@MavenTest
	void generateStrict(MavenExecutionResult result) {
		assertThat(result).isFailure();
		assertThat(result).out().error().contains("src/main/resources/Library.eogen: Library.eomodeld/Book.plist: entity Book: classProperties names titel,"
				+ " which is not an attribute or relationship (did you mean title?).");
		assertThat(result.getMavenProjectResult().getTargetProjectDirectory().resolve("src/main/java")).doesNotExist();
	}

	/**
	 * The test projects' models were written by hand, not by Entity Modeler.
	 */
	@MavenTest
	void format(MavenExecutionResult result) throws IOException {
		assertThat(result).isSuccessful();
		assertThat(result).out().info().contains("Formatted src/main/resources/Library.eomodeld/Book.plist");
		String book = read(result.getMavenProjectResult().getTargetProjectDirectory().resolve("src/main/resources/Library.eomodeld/Book.plist"));
		assertThat(book).contains("classProperties = (author, currency, price, title);");
	}

	/**
	 * Renames Book.title to name, and the property lists' entries for it, and
	 * reports what is left to do by hand.
	 */
	@MavenTest
	void rename(MavenExecutionResult result) throws IOException {
		assertThat(result).isSuccessful();
		assertThat(result).out().info().contains("Changed src/main/resources/Library.eomodeld/Book.plist",
				"Renaming Book.title to name changed 1 model file.");
		assertThat(result).out().warn().anyMatch(line -> line.startsWith("Code that uses the attribute's accessors"));
		String book = read(result.getMavenProjectResult().getTargetProjectDirectory().resolve("src/main/resources/Library.eomodeld/Book.plist"));
		assertThat(book).contains("name = name;", "columnName = title;").doesNotContain("name = title;");
	}

	/**
	 * Writes the Library model's pages, with a link from each entity's class
	 * name, into the site.
	 */
	@MavenTest
	void doc(MavenExecutionResult result) throws IOException {
		assertThat(result).isSuccessful();
		assertThat(result).out().info().contains("Documented Library in target/site/eomodeldoc: 11 files, 11 written.");
		Path site = result.getMavenProjectResult().getTargetProjectDirectory().resolve("target/site/eomodeldoc");
		assertThat(site.resolve("index.html")).exists();
		assertThat(read(site.resolve("Library/entities/Book.html"))).contains("<a href = \"../../../apidocs/Book.html\">com.example.library.Book</a>");
	}

	/**
	 * Writes the Library model's Markdown page into docs/models, and then
	 * checks it in verify, as a project that commits its pages would.
	 */
	@MavenTest
	@MavenGoal("verify")
	void docMarkdown(MavenExecutionResult result) throws IOException {
		assertThat(result).isSuccessful();
		assertThat(result).out().info().contains("Documented Library in docs/models: 2 files, 2 written.", "All 2 pages of Library in docs/models are up to date.");
		Path page = result.getMavenProjectResult().getTargetProjectDirectory().resolve("docs/models/Library.md");
		assertThat(read(page)).contains("```mermaid\nerDiagram\n    Author\n    Book\n", "    Book }o--|| Currency : \"currency\"");
	}

	/**
	 * Fails on a page that is out of date, and on an orphan, but leaves a page
	 * of the project's own, its README.md, which stands for the index, and
	 * changes nothing.
	 */
	@MavenTest
	@MavenGoal("verify")
	void docCheck(MavenExecutionResult result) throws IOException {
		assertThat(result).isFailure();
		assertThat(result).out().error().contains("docs/models/Library.md is out of date.",
				"docs/models/Catalogue.md is an orphan: eogen wrote it for a model that it no longer documents.")
				.anyMatch(line -> line.contains("2 pages are stale. Run mvn eogen:doc, and commit the result."));
		assertThat(result).out().info().contains("docs/models/README.md is the project's own, not eogen's, so the models have no index.");
		Path docs = result.getMavenProjectResult().getTargetProjectDirectory().resolve("docs/models");
		assertThat(read(docs.resolve("Library.md"))).endsWith("Library\n=======\n");
		assertThat(docs.resolve("Catalogue.md")).exists();
		assertThat(read(docs.resolve("README.md"))).isEqualTo("These pages describe the project's models.\n");
	}

	/**
	 * Writes the Library model's next migration, after the one written by
	 * hand, for two new columns, compared with the model in a folder: one of
	 * them doesn't allow null, so it needs a value for the rows a table has.
	 */
	@MavenTest
	void migration(MavenExecutionResult result) throws IOException {
		String migration = "src/main/java/com/example/library/migrations/Library1.java";
		assertThat(result).isSuccessful();
		assertThat(result).out().info().contains("Created " + migration + ", with 1 gap to fill in.", "Wrote 1 migration for the changes since the models in earlier.");
		assertThat(result).out().warn().anyMatch(line -> line.startsWith(migration + ":") && line.endsWith(": book may have rows, which need a value for edition, a new column that doesn't allow null."));
		String source = read(result.getMavenProjectResult().getTargetProjectDirectory().resolve(migration));
		assertThat(source).contains("public class Library1 extends ERXMigrationDatabase.Migration {", "bookTable.newIntegerColumn(\"edition\", NOT_NULL, EOGEN_GAP);",
				"bookTable.newStringColumn(\"isbn\", 100, ALLOWS_NULL);");
	}

	/**
	 * With checkMigrations, the Library model, which has a migration, has two
	 * new columns since the earlier version, but no migration for them, so the
	 * build fails, and nothing is written.
	 */
	@MavenTest
	void migrationCheck(MavenExecutionResult result) {
		assertThat(result).isFailure();
		assertThat(result).out().error().anyMatch(line -> line.startsWith("Library changed since the models in earlier, with no migration: ") && line.contains("Adds isbn to book."))
				.anyMatch(line -> line.contains("1 model has changed with no migration. Run mvn eogen:migration, and commit what it writes."));
		assertThat(result.getMavenProjectResult().getTargetProjectDirectory().resolve("src/main/java/com/example/library/migrations/Library1.java")).doesNotExist();
	}

	/**
	 * Book.authorID is the source of the author relationship's join, so it
	 * isn't removed.
	 */
	@MavenTest
	void removeInUse(MavenExecutionResult result) throws IOException {
		assertThat(result).isFailure();
		assertThat(result).out().error().contains("Book.authorID is in use, so it isn't removed: authorID is referenced by the relationship author.");
		String book = read(result.getMavenProjectResult().getTargetProjectDirectory().resolve("src/main/resources/Library.eomodeld/Book.plist"));
		assertThat(book).contains("name = authorID;");
	}

	@MavenTest
	void formatCheck(MavenExecutionResult result) {
		assertThat(result).isFailure();
		assertThat(result).out().error().contains("src/main/resources/Library.eomodeld/Book.plist is not formatted as Entity Modeler writes it.")
				.anyMatch(line -> line.contains("not formatted. Run mvn eogen:format, and commit the result."));
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

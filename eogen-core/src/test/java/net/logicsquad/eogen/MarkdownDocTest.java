package net.logicsquad.eogen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.logicsquad.eogen.doc.DocOptions;
import net.logicsquad.eogen.doc.DocResult;
import net.logicsquad.eogen.doc.DocResult.Status;
import net.logicsquad.eogen.doc.ModelDocGenerator;

/**
 * Writes the Markdown pages for a copy of the fixture project, and of the
 * corpus's Ledger model, and compares them with the expected ones in
 * {@code src/test/resources/doc/markdown}, which are eogen's own output.
 */
class MarkdownDocTest {
	@TempDir
	Path temp;

	private FixtureProject fixture;

	private Path output;

	private Path model;

	@BeforeEach
	void copyFixture() throws IOException {
		fixture = FixtureProject.copyTo(temp);
		output = temp.resolve("docs");
		model = fixture.resolve("src/main/resources/Shop.eomodeld");
	}

	@AfterEach
	void releaseJars() throws IOException {
		Jars.release(temp);
	}

	@Test
	void writesAPageForEachModel() throws Exception {
		DocResult result = fixture.generator().generateDoc(options());
		assertThat(result.getFindings()).isEmpty();
		assertThat(result.isWritten()).isTrue();
		assertThat(result.getModels()).containsExactly("Shop");
		assertThat(result.getPages()).extracting(page -> page.getFile().getName(), DocResult.Page::getStatus).containsExactly(tuple("Shop.md", Status.CREATED));
		assertThat(read("Shop.md")).isEqualTo(expected("Shop.md")).startsWith(ModelDocGenerator.MARKDOWN_MARKER);

		// The same model gives the same page, which isn't written again.
		Path page = output.resolve("Shop.md");
		FileTime old = FileTime.fromMillis(1_000_000_000_000L);
		Files.setLastModifiedTime(page, old);
		DocResult again = fixture.generator().generateDoc(options());
		assertThat(again.getPages()).extracting(DocResult.Page::getStatus).containsExactly(Status.UNCHANGED);
		assertThat(again.isStale()).isFalse();
		assertThat(Files.getLastModifiedTime(page)).isEqualTo(old);
		assertThat(read("Shop.md")).isEqualTo(expected("Shop.md"));
	}

	@Test
	void documentsACorpusModel() throws Exception {
		Path project = temp.resolve("ledger");
		FixtureProject.copyTree(CorpusTest.corpus().resolve("project/src/main/resources/Ledger.eomodeld"), project.resolve("src/main/resources/Ledger.eomodeld"));
		Path prototypes = temp.resolve("erprototypes.jar");
		FixtureProject.jar(CorpusTest.corpus().resolve("framework"), prototypes);
		EOGenerator generator = new EOGenerator(project.toFile(), Collections.singletonList(project.resolve("src/main/resources").toFile()),
				Collections.singletonList(prototypes.toFile()));
		DocResult result = generator.generateDoc(options());
		assertThat(result.getFindings()).isEmpty();
		assertThat(read("Ledger.md")).isEqualTo(expected("Ledger.md"));
	}

	@Test
	void writesToDocsModelsByDefault() throws Exception {
		DocResult result = fixture.generator().generateDoc(new DocOptions());
		assertThat(result.getFiles()).containsExactly(fixture.resolve("docs/models/Shop.md").toFile());
		assertThat(fixture.resolve("docs/models/Shop.md")).exists();
	}

	/**
	 * A model with more entities than the limit has no diagram of them all,
	 * but each entity with relationships still has its own.
	 */
	@Test
	void leavesOutTheModelsDiagramOverTheLimit() throws Exception {
		DocOptions options = options();
		options.setModelDiagramLimit(7);
		DocResult result = fixture.generator().generateDoc(options);
		assertThat(result.getFindings()).isEmpty();
		String page = read("Shop.md");
		assertThat(page).doesNotContain("    Company\n").contains("Order\n-----\n\n```mermaid\nerDiagram\n    Order\n    Order }o--|| Currency : \"currency\"\n");
		assertThat(page.split("```mermaid", -1)).hasSize(7);

		options.setModelDiagramLimit(8);
		fixture.generator().generateDoc(options);
		assertThat(read("Shop.md")).isEqualTo(expected("Shop.md"));
		assertThatThrownBy(() -> options.setModelDiagramLimit(-1)).isInstanceOf(IllegalArgumentException.class);
	}

	/**
	 * Each end of a line says how many of its entity the other end reaches,
	 * from the relationship that points at it, or from the join if none does.
	 */
	@Test
	void drawsEachKindOfEnd() throws Exception {
		// The fixture's: a mandatory to-one with an optional to-many inverse,
		// drawn from the to-many's side, and to-ones with no inverse, one to an
		// entity in another model. A flattened relationship isn't drawn.
		assertThat(diagram()).contains("    Party ||--o{ Order : \"orders / customer\"", "    Order }o--|| Currency : \"currency\"",
				"    OrderLine }o--|| Product : \"product\"", "    Tag ||--o{ ProductTag : \"productTags / tag\"").doesNotContain("tags\"");

		// Optional to-ones, with an inverse and without, and a mandatory to-many.
		replace(model.resolve("Order.plist"), "destination = Party; \n            isMandatory = Y; \n", "destination = Party; \n");
		replace(model.resolve("Order.plist"), "destination = Currency; \n            isMandatory = Y; \n", "destination = Currency; \n");
		replace(model.resolve("Order.plist"), "destination = OrderLine; \n            isToMany = Y; \n",
				"destination = OrderLine; \n            isMandatory = Y; \n            isToMany = Y; \n");
		assertThat(diagram()).contains("    Party |o--o{ Order : \"orders / customer\"", "    Order }o--o| Currency : \"currency\"",
				"    Order ||--|{ OrderLine : \"lines / order\"");

		// A to-many with no inverse: each line has one order, whose key doesn't
		// allow null, or at most one if it does.
		replace(model.resolve("OrderLine.plist"), "classProperties = (order, price, product, quantity);", "classProperties = (price, product, quantity);");
		String orderLine = read(model.resolve("OrderLine.plist"));
		write(model.resolve("OrderLine.plist"), orderLine.replaceFirst("(?s)\\{\\s*destination = Order; .*?name = order; \\n        \\}, \\n        ", ""));
		assertThat(diagram()).contains("    Order ||--|{ OrderLine : \"lines\"");
		replace(model.resolve("OrderLine.plist"), "{allowsNull = N; columnName = \"order_id\";", "{allowsNull = Y; columnName = \"order_id\";");
		assertThat(diagram()).contains("    Order |o--|{ OrderLine : \"lines\"");

		// A to-one with no inverse that joins on its primary key has at most one
		// row for each destination.
		replace(model.resolve("OrderLine.plist"), "sourceAttribute = productID;", "sourceAttribute = id;");
		assertThat(diagram()).contains("    OrderLine |o--|| Product : \"product\"");
	}

	@Test
	void declaresEachEntitySoThatOneWithNoRelationshipsShows() throws Exception {
		assertThat(diagram()).startsWith("erDiagram\n    Company\n    Order\n    OrderLine\n    Party\n    Person\n    Product\n    ProductTag\n    Tag\n");
	}

	@Test
	void putsDocumentationInATableOnOneLine() throws Exception {
		replace(model.resolve("Order.plist"), "When the order was placed.", "When it was <em>placed</em> | paid.\\n\\nIn the shop's\\ntime zone.");
		replace(model.resolve("Party.plist"), "Someone who places orders.", "Someone who\\nplaces <code>Order</code>s | pays.");
		fixture.generator().generateDoc(options());
		assertThat(read("Shop.md")).contains("| `placed` | `placed` | `timestamp` | `NSTimestamp` | no | When it was <em>placed</em> \\| paid.<br><br>In the shop's time zone. |",
				"\nSomeone who\nplaces <code>Order</code>s | pays.\n");
	}

	@Test
	void checksThePagesWithoutWritingThem() throws Exception {
		DocResult missing = fixture.generator().generateDoc(options(), false);
		assertThat(missing.isWritten()).isFalse();
		assertThat(missing.isStale()).isTrue();
		assertThat(missing.getStalePages()).extracting(DocResult.Page::getStatus).containsExactly(Status.CREATED);
		assertThat(output).doesNotExist();

		fixture.generator().generateDoc(options());
		assertThat(fixture.generator().generateDoc(options(), false).isStale()).isFalse();

		replace(model.resolve("Party.plist"), "Someone who places orders.", "Someone who orders.");
		DocResult changed = fixture.generator().generateDoc(options(), false);
		assertThat(changed.getStalePages()).extracting(DocResult.Page::getStatus).containsExactly(Status.UPDATED);
		assertThat(read("Shop.md")).contains("Someone who places orders.");
	}

	/**
	 * A Windows checkout with Git's core.autocrlf has CRLF line endings: a page
	 * that differs only in them is the same, and one that changes keeps them.
	 */
	@Test
	void ignoresLineEndingsAndKeepsThem() throws Exception {
		fixture.generator().generateDoc(options());
		Path page = output.resolve("Shop.md");
		write(page, read(page).replace("\n", "\r\n"));
		assertThat(fixture.generator().generateDoc(options(), false).isStale()).isFalse();

		replace(model.resolve("Party.plist"), "Someone who places orders.", "Someone who orders.");
		fixture.generator().generateDoc(options());
		assertThat(read(page)).contains("Someone who orders.\r\n").doesNotContain("Someone who places").doesNotContainPattern("[^\r]\n");
	}

	@Test
	void findsAndDeletesTheOrphansButNotAProjectsOwnPages() throws Exception {
		Files.createDirectories(output);
		write(output.resolve("Sales.md"), ModelDocGenerator.MARKDOWN_MARKER + " from the Sales model. -->\n\nSales\n=====\n");
		write(output.resolve("README.md"), "These pages describe the models.\n");
		fixture.generator().generateDoc(options());
		DocResult check = fixture.generator().generateDoc(options(), false);
		assertThat(check.getOrphans()).isEmpty();
		assertThat(output.resolve("Sales.md")).doesNotExist();
		assertThat(output.resolve("README.md")).exists();

		write(output.resolve("Sales.md"), ModelDocGenerator.MARKDOWN_MARKER + " from the Sales model. -->\n");
		DocResult orphaned = fixture.generator().generateDoc(options(), false);
		assertThat(orphaned.getOrphans()).containsExactly(output.resolve("Sales.md").toFile());
		assertThat(orphaned.isStale()).isTrue();
		assertThat(output.resolve("Sales.md")).exists();
	}

	@Test
	void usesTheProjectsOwnTemplate() throws Exception {
		Path templates = Files.createDirectories(temp.resolve("templates"));
		// With LF line endings, as .gitattributes checks out the bundled ones, on
		// every platform.
		write(templates.resolve("model.md.vm"), String.join("\n", "# $model.name", "", "#foreach ($entity in $entities)", "- $md.link($entity, $model, $documented)", "#end", ""));
		DocOptions options = options();
		options.setTemplateFolder(templates.toFile());
		DocResult result = fixture.generator().generateDoc(options);
		assertThat(result.getFindings()).isEmpty();
		assertThat(read("Shop.md")).startsWith("# Shop\n\n- [Company](#company)\n- [Order](#order)\n");
	}

	@Test
	void linksEachClassNameAsTheTemplateSays() throws Exception {
		DocOptions options = options();
		options.setEntityURL("../../target/apidocs/${entity.className.replace(\".\", \"/\")}.html");
		fixture.generator().generateDoc(options);
		assertThat(read("Shop.md")).contains("- Class: [`com.example.shop.Order`](../../target/apidocs/com/example/shop/Order.html)\n");
	}

	@Test
	void writesNothingForAModelGroupWithErrors() throws Exception {
		replace(model.resolve("Order.plist"), "destination = Party;", "destination = Partee;");
		DocResult result = fixture.generator().generateDoc(options());
		assertThat(result.hasErrors()).isTrue();
		assertThat(result.isWritten()).isFalse();
		assertThat(output).doesNotExist();
	}

	private DocOptions options() {
		DocOptions options = new DocOptions();
		options.setFormat(DocOptions.Format.MARKDOWN);
		options.setOutput(output.toFile());
		return options;
	}

	/**
	 * Returns the diagram in the Shop model's page, as written now.
	 */
	private String diagram() throws Exception {
		DocResult result = fixture.generator().generateDoc(options());
		assertThat(result.getFindings()).isEmpty();
		String page = read("Shop.md");
		int start = page.indexOf("```mermaid\n") + "```mermaid\n".length();
		return page.substring(start, page.indexOf("\n```", start) + 1);
	}

	private String read(String page) throws IOException {
		return read(output.resolve(page));
	}

	private static String read(Path file) throws IOException {
		return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
	}

	private static String expected(String page) throws IOException, URISyntaxException {
		return read(Paths.get(MarkdownDocTest.class.getResource("/doc/markdown/" + page).toURI()));
	}

	private static void write(Path file, String text) throws IOException {
		Files.write(file, text.getBytes(StandardCharsets.UTF_8));
	}

	private static void replace(Path file, String from, String to) throws IOException {
		String text = read(file);
		assertThat(text).as(file.getFileName().toString()).contains(from);
		write(file, text.replace(from, to));
	}
}

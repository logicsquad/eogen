package net.logicsquad.eogen;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.logicsquad.eogen.doc.DocOptions;
import net.logicsquad.eogen.doc.DocResult;

/**
 * Writes EOModelDoc's pages for a copy of the fixture project, and of the
 * corpus's Ledger model, and compares them with the expected ones in
 * {@code src/test/resources/doc}, which are eogen's own output.
 */
class DocTest {
	private static final List<String> COPIED = Arrays.asList("eomodeldoc.css", "eomodeldoc.js", "prototype.js");

	@TempDir
	Path temp;

	private FixtureProject fixture;

	private Path output;

	@BeforeEach
	void copyFixture() throws IOException {
		fixture = FixtureProject.copyTo(temp);
		output = temp.resolve("doc");
	}

	@AfterEach
	void releaseJars() throws IOException {
		Jars.release(temp);
	}

	@Test
	void documentsTheProjectsModels() throws Exception {
		DocResult result = fixture.generator().generateDoc(options());
		assertThat(result.getFindings()).isEmpty();
		assertThat(result.isWritten()).isTrue();
		// Reference, a dependency, isn't documented.
		assertThat(result.getModels()).containsExactly("Shop");
		assertThat(relative(result.getFiles())).containsExactly("index.html", "content.html", "overview.html", "models.html", "eomodeldoc.css", "eomodeldoc.js", "prototype.js",
				"Shop/overview.html", "Shop/content.html", "Shop/entities/Company.html", "Shop/entities/Order.html", "Shop/entities/OrderLine.html", "Shop/entities/Party.html",
				"Shop/entities/Person.html", "Shop/entities/Product.html", "Shop/entities/ProductTag.html", "Shop/entities/Tag.html");
		assertSameAsExpected("shop", result);
		String order = read("Shop/entities/Order.html");
		// A relationship to a dependency's entity names it, with no link.
		assertThat(order).contains("<a href = \"../../Shop/entities/Party.html\">Party</a>").contains("\t\t\t\tCurrency").doesNotContain("Reference/entities/Currency.html");
		// A fetch specification with no qualifier has no row for one.
		assertThat(order).contains("<div class = \"name\">recent</div>");
	}

	@Test
	void documentsTheDependenciesModelsButNotPrototypes() throws Exception {
		DocOptions options = options();
		options.setIncludeDependencies(true);
		DocResult result = fixture.generator().generateDoc(options);
		assertThat(result.getFindings()).isEmpty();
		assertThat(result.getModels()).containsExactly("Reference", "Shop");
		assertThat(relative(result.getFiles())).contains("Reference/entities/Currency.html").doesNotContain("Reference/entities/EOJDBCPrototypes.html");
		assertThat(read("Shop/entities/Order.html")).contains("<a href = \"../../Reference/entities/Currency.html\">Currency</a>");
		assertThat(read("overview.html")).contains("Currency").doesNotContain("EOJDBCPrototypes");
	}

	@Test
	void documentsACorpusModel() throws Exception {
		Path project = temp.resolve("ledger");
		FixtureProject.copyTree(CorpusTest.corpus().resolve("project/src/main/resources/Ledger.eomodeld"), project.resolve("src/main/resources/Ledger.eomodeld"));
		Path prototypes = temp.resolve("erprototypes.jar");
		FixtureProject.jar(CorpusTest.corpus().resolve("framework"), prototypes);
		EOGenerator generator = new EOGenerator(project.toFile(), Collections.singletonList(project.resolve("src/main/resources").toFile()), Collections.singletonList(prototypes.toFile()));
		DocResult result = generator.generateDoc(options());
		assertThat(result.getFindings()).isEmpty();
		assertThat(relative(result.getFiles())).contains("Ledger/storedProcedures/closeAccounts.html", "Ledger/storedProcedures/fetchAccounts.html");
		assertSameAsExpected("ledger", result);
		assertThat(read("Ledger/content.html")).contains("A ledger, with an entity index and stored procedures");
		assertThat(read("Ledger/entities/Account.html")).contains("<div class = \"name\">account_number</div>", "Account numbers are unique.");
	}

	@Test
	void showsTheDocumentationOfEachKindOfObjectOnItsPage() throws Exception {
		Path model = fixture.resolve("src/main/resources/Shop.eomodeld");
		replace(model.resolve("Order.plist"), "{columnName = note; name = note; prototypeName = longText; }",
				"{columnName = note; name = note; prototypeName = longText; userInfo = {\"_EntityModeler\" = {documentation = \"What the customer asked for.\"; }; }; }");
		replace(model.resolve("Order.plist"), "            name = lines; \n", "            name = lines; \n            userInfo = {\"_EntityModeler\" = {documentation = \"The order's lines.\"; }; }; \n");
		replace(model.resolve("Order.fspec"), "    recent = {\n", "    recent = {\n        userInfo = {\"_EntityModeler\" = {documentation = \"The latest orders.\"; }; }; \n");
		DocResult result = fixture.generator().generateDoc(options());
		assertThat(result.getFindings()).isEmpty();
		assertThat(read("content.html")).contains("A small shop, for testing eogen.");
		assertThat(read("Shop/content.html")).contains("Someone who places orders.");
		assertThat(read("Shop/entities/Party.html")).contains("<div class = \"documentation\">Someone who places orders.</div>");
		assertThat(read("Shop/entities/Order.html")).contains("<div class = \"documentation\">What the customer asked for.</div>", "<div class = \"documentation\">The order's lines.</div>",
				"<div class = \"documentation\">The latest orders.</div>");
	}

	@Test
	void showsAStoredProceduresDocumentationOnItsPage() throws Exception {
		Path project = temp.resolve("ledger");
		Path model = project.resolve("src/main/resources/Ledger.eomodeld");
		FixtureProject.copyTree(CorpusTest.corpus().resolve("project/src/main/resources/Ledger.eomodeld"), model);
		String procedure = new String(Files.readAllBytes(model.resolve("closeAccounts.storedProcedure")), StandardCharsets.UTF_8);
		Files.write(model.resolve("closeAccounts.storedProcedure"), procedure.replaceFirst("\\{\n", "{\n    userInfo = {\"_EntityModeler\" = {documentation = \"Closes the accounts.\"; }; }; \n")
				.getBytes(StandardCharsets.UTF_8));
		Path prototypes = temp.resolve("erprototypes.jar");
		FixtureProject.jar(CorpusTest.corpus().resolve("framework"), prototypes);
		EOGenerator generator = new EOGenerator(project.toFile(), Collections.singletonList(project.resolve("src/main/resources").toFile()), Collections.singletonList(prototypes.toFile()));
		DocResult result = generator.generateDoc(options());
		assertThat(result.getFindings()).isEmpty();
		assertThat(read("Ledger/storedProcedures/closeAccounts.html")).contains("<div class = \"documentation\">Closes the accounts.</div>");
		assertThat(read("Ledger/content.html")).contains("Closes the accounts.");
	}

	@Test
	void linksEachClassNameAsTheTemplateSays() throws Exception {
		DocOptions options = options();
		options.setEntityURL("../../../apidocs/${entity.className.replace(\".\", \"/\")}.html");
		DocResult result = fixture.generator().generateDoc(options);
		assertThat(result.getFindings()).isEmpty();
		assertThat(read("Shop/entities/Order.html")).contains("<td><a href = \"../../../apidocs/com/example/shop/Order.html\">com.example.shop.Order</a></td>");
		// ProductTag's class, EOGenericRecord, is linked too.
		assertThat(read("Shop/entities/ProductTag.html")).contains("<a href = \"../../../apidocs/EOGenericRecord.html\">EOGenericRecord</a>");
	}

	@Test
	void usesTheExtraTemplatesFirst() throws Exception {
		Path templates = Files.createDirectories(temp.resolve("templates"));
		Files.write(templates.resolve("indexModels.html.vm"), Arrays.asList("<ul>", "#foreach ($model in $models)", "<li>${model.name}, ${model.entities.size()} entities</li>", "#end", "</ul>"));
		Files.write(templates.resolve("eomodeldoc.css"), Collections.singletonList("body { color: green; }"));
		DocOptions options = options();
		options.setTemplateFolder(templates.toFile());
		DocResult result = fixture.generator().generateDoc(options);
		assertThat(result.getFindings()).isEmpty();
		assertThat(read("models.html")).isEqualTo("<ul>\n<li>Shop, 8 entities</li>\n</ul>\n");
		assertThat(read("eomodeldoc.css")).isEqualTo("body { color: green; }\n");
		// The rest are the bundled ones.
		assertThat(read("Shop/content.html")).contains("No documentation available for Company.");
	}

	@Test
	void writesNothingWhenATemplateRendersAReferenceWithNoValue() throws Exception {
		Path templates = Files.createDirectories(temp.resolve("templates"));
		Files.write(templates.resolve("modelOverview.html.vm"), Collections.singletonList("<h1>$model.name, $model.notAProperty</h1>"));
		DocOptions options = options();
		options.setTemplateFolder(templates.toFile());
		DocResult result = fixture.generator().generateDoc(options);
		assertThat(result.isWritten()).isFalse();
		assertThat(output).doesNotExist();
		assertThat(result.getFindings()).hasSize(1);
		Finding finding = result.getFindings().get(0);
		assertThat(finding.getCode()).isEqualTo(Finding.UNRESOLVED_REFERENCE);
		// The pages are outside the project, so the path is absolute.
		assertThat(finding.getFile()).isEqualTo(output.resolve("Shop/overview.html").toString());
		assertThat(finding.getKey()).isEqualTo("$model.notAProperty");

		// Unless forced.
		EOGenerator forced = fixture.generator();
		forced.setForce(true);
		assertThat(forced.generateDoc(options).isWritten()).isTrue();
		assertThat(read("Shop/overview.html")).contains("$model.notAProperty");
	}

	@Test
	void writesNothingForAModelGroupWithErrors() throws Exception {
		replace(fixture.resolve("src/main/resources/Shop.eomodeld/Order.plist"), "destination = Party;", "destination = Partee;");
		DocResult result = fixture.generator().generateDoc(options());
		assertThat(result.hasErrors()).isTrue();
		assertThat(result.isWritten()).isFalse();
		assertThat(output).doesNotExist();
	}

	@Test
	void writesToTheSiteByDefault() throws Exception {
		DocResult result = fixture.generator().generateDoc(new DocOptions());
		assertThat(result.getFiles().get(0)).isEqualTo(fixture.resolve("target/site/eomodeldoc/index.html").toFile());
		assertThat(fixture.resolve("target/site/eomodeldoc/index.html")).exists();
	}

	private DocOptions options() {
		DocOptions options = new DocOptions();
		options.setOutput(output.toFile());
		return options;
	}

	private String read(String page) throws IOException {
		return new String(Files.readAllBytes(output.resolve(page)), StandardCharsets.UTF_8);
	}

	private List<String> relative(List<File> files) {
		return files.stream().map(file -> output.relativize(file.toPath()).toString().replace(File.separatorChar, '/')).collect(Collectors.toList());
	}

	/**
	 * Compares the pages with those in {@code src/test/resources/doc/<name>},
	 * and the files that are copied with the bundled ones.
	 */
	private void assertSameAsExpected(String name, DocResult result) throws IOException, URISyntaxException {
		Path expected = Paths.get(DocTest.class.getResource("/doc/" + name).toURI());
		List<String> pages = new ArrayList<>();
		for (String page : relative(result.getFiles())) {
			if (COPIED.contains(page)) {
				try (InputStream in = DocTest.class.getResourceAsStream("/net/logicsquad/eogen/doc/" + page)) {
					assertThat(output.resolve(page)).as(page).hasBinaryContent(in.readAllBytes());
				}
			} else {
				pages.add(page);
				assertThat(output.resolve(page)).as(page).hasSameTextualContentAs(expected.resolve(page), StandardCharsets.UTF_8);
			}
		}
		assertThat(pages).containsExactlyInAnyOrderElementsOf(FixtureProject.files(expected));
	}

	private static void replace(Path file, String from, String to) throws IOException {
		String contents = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
		assertThat(contents).as(file.toString()).containsOnlyOnce(from);
		Files.write(file, contents.replace(from, to).getBytes(StandardCharsets.UTF_8));
	}
}

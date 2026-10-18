package net.logicsquad.eogen.lint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.logicsquad.eogen.Finding;

/**
 * Lints copies of the fixture's Shop model, with mistakes made in them.
 */
class ModelLinterTest {
	@TempDir
	Path temp;

	private Path shop;

	@BeforeEach
	void copyShop() throws IOException, URISyntaxException {
		Path source = Paths.get(ModelLinterTest.class.getResource("/fixtures/project/src/main/resources/Shop.eomodeld").toURI());
		shop = Files.createDirectories(temp.resolve("Shop.eomodeld"));
		try (Stream<Path> files = Files.list(source)) {
			for (Path file : files.collect(Collectors.toList())) {
				Files.copy(file, shop.resolve(file.getFileName().toString()));
			}
		}
	}

	@Test
	void findsNothingInAModelEntityModelerWrote() {
		assertThat(ModelLinter.lint(shop.toFile())).isEmpty();
	}

	@Test
	void findsUnknownKeysWithWhatTheyProbablyMeant() throws IOException {
		replace("Product.plist", "name = price; prototypeName = amount;", "name = price; prototypeNam = amount;");
		replace("Product.plist", "    className = \"com.example.shop.Product\";", "    clasName = \"com.example.shop.Product\";");
		replace("Tag.plist", "isToMany = Y;", "isToMny = Y;");
		replace("Tag.plist", "sourceAttribute = id;", "sourceAtribute = id;");
		replace("index.eomodeld", "adaptorName = JDBC;", "adaptorNam = JDBC;");
		replace("Order.fspec", "fetchLimit = 10;", "fetchLimt = 10;");
		assertThat(messages()).containsExactly(
				"Shop.eomodeld/index.eomodeld: the model: unknown key adaptorNam (did you mean adaptorName?).",
				"Shop.eomodeld/Order.fspec: fetch specification recent: unknown key fetchLimt (did you mean fetchLimit?).",
				"Shop.eomodeld/Product.plist: entity Product: unknown key clasName (did you mean className?).",
				"Shop.eomodeld/Product.plist: attribute price: unknown key prototypeNam (did you mean prototypeName?).",
				"Shop.eomodeld/Tag.plist: relationship productTags: unknown key isToMny (did you mean isToMany?).",
				"Shop.eomodeld/Tag.plist: a join of relationship productTags: unknown key sourceAtribute (did you mean sourceAttribute?).");
	}

	@Test
	void leavesUserInfoAlone() throws IOException {
		replace("Product.plist", "userInfo = {ERXConstantClassName = \"com.example.shop.Category\"; };",
				"userInfo = {anythingAtAll = Y; ERXConstantClassName = \"com.example.shop.Category\"; };");
		assertThat(messages()).isEmpty();
	}

	@Test
	void findsNamesInTheEntityListsThatMatchNothing() throws IOException {
		replace("Product.plist", "classProperties = (active, category, name, price, tags);", "classProperties = (active, category, name, prise, tags, tag);");
		replace("Product.plist", "primaryKeyAttributes = (id);", "primaryKeyAttributes = (ID);");
		replace("Product.plist", "attributesUsedForLocking = (active, category, id, name, price);", "attributesUsedForLocking = (active, category, id, name, cost);");
		assertThat(messages()).containsExactly(
				"Shop.eomodeld/Product.plist: entity Product: classProperties names prise, which is not an attribute or relationship (did you mean price?).",
				"Shop.eomodeld/Product.plist: entity Product: classProperties names tag, which is not an attribute or relationship (did you mean tags?).",
				"Shop.eomodeld/Product.plist: entity Product: primaryKeyAttributes names ID, which is not an attribute (did you mean id?).",
				"Shop.eomodeld/Product.plist: entity Product: attributesUsedForLocking names cost, which is not an attribute.");
	}

	@Test
	void findsEntityFilesTheIndexDoesNotMatch() throws IOException {
		Files.copy(shop.resolve("Tag.plist"), shop.resolve("Label.plist"));
		replace("Label.plist", "name = Tag;", "name = Label;");
		Files.copy(shop.resolve("Order.fspec"), shop.resolve("Invoice.fspec"));
		Files.delete(shop.resolve("Company.plist"));
		replace("Tag.plist", "name = Tag;", "name = Tags;");
		assertThat(messages()).containsExactly(
				"Shop.eomodeld/index.eomodeld: entity Company: there is no Company.plist.",
				"Shop.eomodeld/Invoice.fspec: there is no entity Invoice for these fetch specifications, so the model ignores them.",
				"Shop.eomodeld/Label.plist: entity Label: index.eomodeld doesn't list it, so the model ignores it.",
				"Shop.eomodeld/Tag.plist: entity Tags: index.eomodeld lists it as Tag.");
	}

	@Test
	void givesEachKindOfFindingItsCode() throws IOException {
		replace("Product.plist", "name = price; prototypeName = amount;", "name = price; prototypeNam = amount;");
		replace("Product.plist", "primaryKeyAttributes = (id);", "primaryKeyAttributes = (ID);");
		Files.copy(shop.resolve("Tag.plist"), shop.resolve("Label.plist"));
		replace("Label.plist", "name = Tag;", "name = Label;");
		Files.delete(shop.resolve("Company.plist"));
		replace("Tag.plist", "name = Tag;", "name = Tags;");
		assertThat(ModelLinter.lint(shop.toFile())).extracting(LintFinding::getCode, LintFinding::getEntity, LintFinding::getObject, LintFinding::getKey,
				LintFinding::getSuggestion).containsExactly(
						tuple(Finding.MISSING_FILE, "Company", "entity Company", null, null),
						tuple(Finding.UNLISTED_FILE, "Label", "entity Label", null, null),
						tuple(Finding.UNKNOWN_KEY, "Product", "attribute price", "prototypeNam", "prototypeName"),
						tuple(Finding.UNMATCHED_NAME, "Product", "entity Product", "ID", "id"),
						tuple(Finding.MISNAMED_ENTITY, "Tags", "entity Tags", null, null));
	}

	/**
	 * The class templates can put documentation in Javadoc comments, where
	 * Javadoc's checks fail a build on HTML that a browser would forgive.
	 */
	@Test
	void findsDocumentationThatJavadocWouldReject() throws IOException {
		documentParty("A <codwe>Party</code> places <code>Order</code>s.");
		replace("Order.plist", "deletes its lines.", "deletes its lines & their <em>notes</p>.");
		replace("index.eomodeld", "for testing eogen.", "for testing eogen, with fewer < 10 <tt>Tag</tt>s and <codeParty</code>.");
		replace("Order.plist", "When the order was placed.", "When the <p>order was <code>placed.");
		assertThat(messages()).containsExactly(
				"Shop.eomodeld/index.eomodeld: the model: documentation has a < that starts no tag (did you mean &lt;?).",
				"Shop.eomodeld/index.eomodeld: the model: documentation has <tt>, which HTML5 doesn't have.",
				"Shop.eomodeld/index.eomodeld: the model: documentation has <codeParty with no >.",
				"Shop.eomodeld/Order.plist: attribute placed: documentation has <code> with no </code>.",
				"Shop.eomodeld/Order.plist: relationship lines: documentation has an & that starts no character reference (did you mean &amp;?).",
				"Shop.eomodeld/Order.plist: relationship lines: documentation has </p>, which closes no <p>.",
				"Shop.eomodeld/Order.plist: relationship lines: documentation has <em> with no </em>.",
				"Shop.eomodeld/Party.plist: entity Party: documentation has <codwe>, which isn't an HTML tag (did you mean <code>?).");
		assertThat(ModelLinter.lint(shop.toFile())).extracting(LintFinding::getCode).containsOnly(Finding.MALFORMED_HTML);
		assertThat(ModelLinter.lint(shop.toFile()).get(7)).extracting(LintFinding::getEntity, LintFinding::getObject, LintFinding::getKey, LintFinding::getSuggestion)
				.containsExactly("Party", "entity Party", "codwe", "<code>");
	}

	@Test
	void acceptsDocumentationThatJavadocWould() throws IOException {
		documentParty("<p>A <CODE>Party</CODE>&mdash;someone, or <a href=\\\"https://example.com\\\">something</a>, that places <em>orders</em>:</p>"
				+ "<ul><li>one<li>two</ul><p>Its <code>name</code> &amp; <code>id</code>&#33;<br/><br>&#x21;<!-- not shown --></p><p>Or none.");
		assertThat(messages()).isEmpty();
	}

	/**
	 * A {@code <code>} written for a {@code </code>} leaves elements open,
	 * which are reported once.
	 */
	@Test
	void reportsElementsLeftOpenOnce() throws IOException {
		documentParty("<p>A <code>Party</p> and <b>bold</b></i>, after <br></br>, with <code>Order<code>s and <code>Tag<code>s.");
		assertThat(messages()).containsExactly("Shop.eomodeld/Party.plist: entity Party: documentation has <code> with no </code> before </p>.",
				"Shop.eomodeld/Party.plist: entity Party: documentation has </i>, which closes no <i>.",
				"Shop.eomodeld/Party.plist: entity Party: documentation has </br>, which closes no <br>.",
				"Shop.eomodeld/Party.plist: entity Party: documentation has <code> with no </code>.");
	}

	@Test
	void leavesUnreadableFilesToTheModelLoader() throws IOException {
		Files.write(shop.resolve("Tag.plist"), "{ attributes = (".getBytes(StandardCharsets.UTF_8));
		assertThat(messages()).isEmpty();
	}

	@Test
	void suggestsADifferenceInCaseFirstThenTheClosestWithinTwoEdits() {
		List<String> names = Arrays.asList("bookID", "title", "titles");
		assertThat(ModelLinter.suggest("bookId", names)).isEqualTo("bookID");
		assertThat(ModelLinter.suggest("titel", names)).isEqualTo("title");
		assertThat(ModelLinter.suggest("author", names)).isNull();
		assertThat(ModelLinter.distance("kitten", "sitting")).isEqualTo(3);
	}

	private List<String> messages() {
		return ModelLinter.lint(shop.toFile()).stream().map(LintFinding::toString).collect(Collectors.toList());
	}

	private void documentParty(String documentation) throws IOException {
		replace("Party.plist", "documentation = \"Someone who places orders.\";", "documentation = \"" + documentation + "\";");
	}

	private void replace(String file, String from, String to) throws IOException {
		Path path = shop.resolve(file);
		String text = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		assertThat(text).as(file).contains(from);
		Files.write(path, text.replace(from, to).getBytes(StandardCharsets.UTF_8));
	}
}

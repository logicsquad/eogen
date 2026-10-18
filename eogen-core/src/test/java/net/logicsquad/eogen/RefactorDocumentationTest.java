package net.logicsquad.eogen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.logicsquad.eogen.refactor.RefactorResult;

/**
 * Renames and removes in a copy of the fixture project, whose documentation
 * names entities and attributes, and reports what names them, in a
 * {@code <code>} element, as left to do by hand.
 */
class RefactorDocumentationTest {
	@TempDir
	Path temp;

	private FixtureProject fixture;

	private Path model;

	@BeforeEach
	void copyFixture() throws Exception {
		fixture = FixtureProject.copyTo(temp);
		model = fixture.resolve("src/main/resources/Shop.eomodeld");
		assertThat(fixture.generator().format(true).getRefusedFiles()).isEmpty();
	}

	@AfterEach
	void releaseJars() throws IOException {
		Jars.release(temp);
	}

	/**
	 * An entity's name is reported wherever it is in a {@code <code>} element,
	 * but not in the text around one, and the documentation stays as it is.
	 */
	@Test
	void reportsTheDocumentationThatNamesARenamedEntity() throws Exception {
		replace("index.eomodeld", "A small shop, for testing eogen.", "A small shop, for testing eogen, where each <code>Party</code> places orders.");
		replace("Party.plist", "Someone who places orders.", "A Party: someone who places orders.");
		RefactorResult result = fixture.generator().rename("Party", "Client", false, true);
		assertThat(result.isRefused()).as(String.valueOf(result.getFindings())).isFalse();
		assertThat(documentation(result)).containsExactly(
				tuple("The documentation of the model Shop names Party. Change it to Client by hand.", "Shop", null, "the model"),
				tuple("The documentation of Order's relationship customer names Party. Change it to Client by hand.", "Shop", "Order", "relationship customer"));
		assertThat(messages(result)).noneMatch(message -> message.startsWith("The userInfo of"));
		assertThat(read("Order.plist")).contains("The <code>Party</code> who placed the order.");
	}

	/**
	 * A property's name is reported in the documentation of its entity and its
	 * properties, or after its entity's name and a dot. In another entity's
	 * documentation, the name alone may be another entity's property.
	 */
	@Test
	void reportsTheDocumentationThatNamesARenamedProperty() throws Exception {
		documentOrder("Its <code>placed()</code> says when.");
		replace("OrderLine.plist", "            name = order; \n",
				"            name = order; \n            userInfo = {\"_EntityModeler\" = {documentation = \"The line's <code>Order.placed</code> dates it.\"; }; }; \n");
		replace("OrderLine.plist", "{allowsNull = N; columnName = price; name = price; prototypeName = amount; }",
				"{allowsNull = N; columnName = price; name = price; prototypeName = amount; userInfo = {\"_EntityModeler\" = {documentation = \"Fixed once <code>placed</code>.\"; }; }; }");
		RefactorResult result = fixture.generator().rename("Order.placed", "placedAt", false, true);
		assertThat(result.isRefused()).as(String.valueOf(result.getFindings())).isFalse();
		assertThat(documentation(result)).containsExactly(
				tuple("The documentation of Order names placed. Change it to placedAt by hand.", "Shop", "Order", "entity Order"),
				tuple("The documentation of OrderLine's relationship order names placed. Change it to placedAt by hand.", "Shop", "OrderLine", "relationship order"));
		// The attribute's own documentation says "placed" only in its text.
		assertThat(messages(result)).noneMatch(message -> message.startsWith("The userInfo of"));
	}

	@Test
	void reportsTheDocumentationThatNamesARenamedRelationship() throws Exception {
		documentOrder("Its <code>customer</code> placed it.");
		replace("Party.plist", "Someone who places orders.", "The <code>Order.customer</code> of each order they place.");
		RefactorResult result = fixture.generator().rename("Order.customer", "buyer", false, true);
		assertThat(result.isRefused()).as(String.valueOf(result.getFindings())).isFalse();
		assertThat(documentation(result)).containsExactly(
				tuple("The documentation of Order names customer. Change it to buyer by hand.", "Shop", "Order", "entity Order"),
				tuple("The documentation of Party names customer. Change it to buyer by hand.", "Shop", "Party", "entity Party"));
	}

	@Test
	void reportsTheDocumentationThatNamesARemovedProperty() throws Exception {
		replace("Order.plist", "{allowsNull = N; columnName = total; name = total; prototypeName = amount; }",
				"{allowsNull = N; columnName = total; name = total; prototypeName = amount; userInfo = {\"_EntityModeler\" = {documentation = \"Not counting the <code>note</code>.\"; }; }; }");
		RefactorResult result = fixture.generator().remove("Order.note", true);
		assertThat(result.isRefused()).as(String.valueOf(result.getFindings())).isFalse();
		assertThat(documentation(result))
				.containsExactly(tuple("The documentation of Order's attribute total names note, which is removed. Change it by hand.", "Shop", "Order", "attribute total"));
	}

	@Test
	void reportsNothingWhenNoDocumentationNamesIt() throws Exception {
		RefactorResult result = fixture.generator().rename("Tag.name", "label", false, true);
		assertThat(result.isRefused()).as(String.valueOf(result.getFindings())).isFalse();
		assertThat(documentation(result)).isEmpty();
	}

	/**
	 * Gives Order, which has none, documentation.
	 */
	private void documentOrder(String documentation) throws IOException {
		String order = read("Order.plist");
		write("Order.plist", order.substring(0, order.lastIndexOf('}')) + "    userInfo = {\"_EntityModeler\" = {documentation = \"" + documentation + "\"; }; }; \n}\n");
	}

	private static List<org.assertj.core.groups.Tuple> documentation(RefactorResult result) {
		return result.getFindings().stream().filter(finding -> finding.getMessage().startsWith("The documentation of"))
				.peek(finding -> assertThat(finding.getCode()).isEqualTo(Finding.BY_HAND))
				.map(finding -> tuple(finding.getMessage(), finding.getModel(), finding.getEntity(), finding.getObject())).collect(Collectors.toList());
	}

	private static List<String> messages(RefactorResult result) {
		return result.getFindings().stream().map(Finding::getMessage).collect(Collectors.toList());
	}

	private String read(String file) throws IOException {
		return new String(Files.readAllBytes(model.resolve(file)), StandardCharsets.UTF_8);
	}

	private void write(String file, String text) throws IOException {
		Files.write(model.resolve(file), text.getBytes(StandardCharsets.UTF_8));
	}

	private void replace(String file, String from, String to) throws IOException {
		String text = read(file);
		assertThat(text).as(file).contains(from);
		write(file, text.replace(from, to));
	}
}

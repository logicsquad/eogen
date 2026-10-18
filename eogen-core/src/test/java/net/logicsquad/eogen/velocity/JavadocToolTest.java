package net.logicsquad.eogen.velocity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.logicsquad.eogen.model.EOAttribute;
import net.logicsquad.eogen.model.EOEntity;

class JavadocToolTest {
	private final JavadocTool javadoc = new JavadocTool();

	@Test
	void writesAModelObjectsDocumentationAsAComment() {
		EOEntity entity = new EOEntity("Party");
		entity.setDocumentation("Someone who places <code>Order</code>s.");
		assertThat(javadoc.has(entity)).isTrue();
		assertThat(javadoc.comment(entity, "")).isEqualTo("/**\n * Someone who places <code>Order</code>s.\n */");
		EOAttribute attribute = new EOAttribute("name");
		attribute.setDocumentation("What the party is called.");
		assertThat(javadoc.comment(attribute, "  ")).isEqualTo("  /**\n   * What the party is called.\n   */");
		assertThat(javadoc.lines(attribute, "  ")).isEqualTo("   * What the party is called.");
	}

	@Test
	void keepsTheLinesButNotBlankOnesAtEitherEnd() {
		String documentation = "\r\n  \nFirst line.  \r\n\r\nSecond line.\rThird line.\n\n";
		assertThat(javadoc.comment(documentation, "  ")).isEqualTo("  /**\n   * First line.\n   *\n   * Second line.\n   * Third line.\n   */");
		assertThat(javadoc.lines(documentation, "")).isEqualTo(" * First line.\n *\n * Second line.\n * Third line.");
	}

	/**
	 * A star and a slash would end the comment, a backslash and a u would start
	 * a Unicode escape, and an at sign at the start of a line would start a
	 * block tag.
	 */
	@Test
	void escapesWhatWouldBreakTheCommentOrTheSource() {
		assertThat(javadoc.comment("Ends with */ here.\nC:\\users\\shop\n  @deprecated, but {@code x} and a@b stay.", ""))
				.isEqualTo("/**\n * Ends with *&#47; here.\n * C:&#92;users&#92;shop\n *   &#64;deprecated, but {@code x} and a@b stay.\n */");
	}

	@Test
	void hasNothingForNoDocumentation() {
		EOEntity entity = new EOEntity("Party");
		assertThat(javadoc.has(entity)).isFalse();
		assertThat(javadoc.comment(entity, "  ")).isEmpty();
		assertThat(javadoc.lines(entity, "  ")).isEmpty();
		entity.setDocumentation(" \n\t\r\n");
		assertThat(javadoc.has(entity)).isFalse();
		assertThat(javadoc.comment(entity, "")).isEmpty();
		assertThat(javadoc.has(null)).isFalse();
		assertThat(javadoc.has(Integer.valueOf(1))).isFalse();
	}
}

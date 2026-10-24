package net.logicsquad.eogen.doc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.logicsquad.eogen.model.EOAttribute;
import net.logicsquad.eogen.model.EOEntity;

class MarkdownToolTest {
	private final MarkdownTool md = new MarkdownTool();

	@Test
	void putsDocumentationInACellOnOneLine() {
		assertThat(md.cell("\r\nFirst | line\r\nof one.\r\n\r\n\r\nSecond.  \n\n")).isEqualTo("First \\| line of one.<br><br>Second.");
		assertThat(md.cell(null)).isEmpty();
		EOEntity entity = new EOEntity("Party");
		entity.setDocumentation("Someone who places <code>Order</code>s.");
		assertThat(md.cell(entity)).isEqualTo("Someone who places <code>Order</code>s.");
	}

	@Test
	void keepsDocumentationsLinesOutsideATable() {
		assertThat(md.block("\n\nFirst | line  \r\nSecond line.\n\n")).isEqualTo("First | line\nSecond line.");
		assertThat(md.has(" \n ")).isFalse();
		assertThat(md.has(new EOEntity("Party"))).isFalse();
	}

	@Test
	void writesCodeThatATableCanHold() {
		assertThat(md.code("customer.name")).isEqualTo("`customer.name`");
		assertThat(md.code("a | b")).isEqualTo("`a \\| b`");
		assertThat(md.code("say `hello`")).isEqualTo("`` say `hello` ``");
		assertThat(md.code("")).isEmpty();
		assertThat(md.code(null)).isEmpty();
	}

	@Test
	void writesAColumnsTypeWithItsWidthOrPrecision() {
		EOAttribute attribute = new EOAttribute("price");
		assertThat(md.type(attribute)).isEmpty();
		attribute.setExternalType("numeric");
		assertThat(md.type(attribute)).isEqualTo("numeric");
		attribute.setPrecision(Integer.valueOf(12));
		attribute.setScale(Integer.valueOf(2));
		assertThat(md.type(attribute)).isEqualTo("numeric(12, 2)");
		EOAttribute name = new EOAttribute("name");
		name.setExternalType("varchar");
		name.setWidth(Integer.valueOf(100));
		assertThat(md.type(name)).isEqualTo("varchar(100)");
	}

	@Test
	void writesAnAttributesJavaTypeAsItsAccessorsHaveIt() {
		EOAttribute count = new EOAttribute("count");
		assertThat(md.javaType(count)).isEmpty();
		count.setValueClassName("NSNumber", false);
		count.setValueType("l", false);
		assertThat(md.javaType(count)).isEqualTo("Long");
		EOAttribute status = new EOAttribute("status");
		status.setValueClassName("com.example.shop.Party$Status", false);
		assertThat(md.javaType(status)).isEqualTo("com.example.shop.Party.Status");
		status.getUserInfo().put("ERXConstantClassName", "com.example.shop.Category");
		assertThat(md.javaType(status)).isEqualTo("com.example.shop.Category");
	}

	@Test
	void underlinesAHeadingWithAtLeastThreeCharacters() {
		assertThat(md.rule("Shop", "=")).isEqualTo("====");
		assertThat(md.rule("Id", "-")).isEqualTo("---");
	}
}

package net.logicsquad.eogen.velocity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UnresolvedReferenceHandlerTest {
	@Test
	void rootIsTheVariableAPropertyOrMethodBelongsTo() {
		assertThat(UnresolvedReferenceHandler.root("$attribute.javaClassName")).isEqualTo("attribute");
		assertThat(UnresolvedReferenceHandler.root("${attribute.javaClassName}")).isEqualTo("attribute");
		assertThat(UnresolvedReferenceHandler.root("$entity.name.substring(\"1\")")).isEqualTo("entity");
		assertThat(UnresolvedReferenceHandler.root("$foreach-count.index")).isEqualTo("foreach-count");
	}

	@Test
	void bareReferencesHaveNoRoot() {
		assertThat(UnresolvedReferenceHandler.root("$LastChangedRevision")).isNull();
		assertThat(UnresolvedReferenceHandler.root("${entity}")).isNull();
		assertThat(UnresolvedReferenceHandler.root("$entity")).isNull();
	}

	@Test
	void describesTheReferenceItsObjectAndTemplate() {
		Named attribute = new Named("price");
		assertThat(UnresolvedReferenceHandler.describe("$attribute.javaClassName", "attribute", attribute, "_Entity.java"))
				.isEqualTo("$attribute.javaClassName has no value for attribute price (_Entity.java).");
		assertThat(UnresolvedReferenceHandler.describe("$model.foo", "model", new Object(), null)).isEqualTo("$model.foo has no value.");
	}

	/**
	 * Has a name, as the model's objects do.
	 */
	public static class Named {
		private final String name;

		Named(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}

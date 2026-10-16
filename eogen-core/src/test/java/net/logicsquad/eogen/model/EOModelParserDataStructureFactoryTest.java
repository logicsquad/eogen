package net.logicsquad.eogen.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.objectstyle.woenvironment.plist.WOLPropertyListSerialization;

/**
 * The parser's sets keep the order they are read in and drop duplicates, as
 * commons-collections' ListOrderedSet did.
 */
class EOModelParserDataStructureFactoryTest {
	private final EOModelParserDataStructureFactory factory = new EOModelParserDataStructureFactory();

	@Test
	void setsKeepTheirOrderAndDropDuplicates() {
		Collection<Object> attributes = factory.createCollection("root.attributes");
		attributes.add("b");
		attributes.add("a");
		attributes.add("b");
		attributes.add("c");
		assertThat(attributes).isInstanceOf(Set.class).containsExactly("b", "a", "c");
	}

	@Test
	void otherCollectionsAreLists() {
		assertThat(factory.createCollection("root.arguments")).isInstanceOf(LinkedList.class);
		assertThat(factory.createCollection("root.userInfo.list")).isInstanceOf(LinkedList.class);
	}

	@Test
	@SuppressWarnings("unchecked")
	void parsedEntityKeepsItsClassPropertyOrder() throws Exception {
		String plist = "{ classProperties = (name, email, name, id); joins = (a, a); }";
		Map<?, ?> map = (Map<?, ?>) WOLPropertyListSerialization.propertyListFromStream(new ByteArrayInputStream(plist.getBytes(StandardCharsets.UTF_8)), factory);
		assertThat((Collection<Object>) map.get("classProperties")).containsExactly("name", "email", "id");
		assertThat((Collection<Object>) map.get("joins")).containsExactly("a", "a");
	}
}

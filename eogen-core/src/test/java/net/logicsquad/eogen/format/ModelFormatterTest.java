package net.logicsquad.eogen.format;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ModelFormatterTest {
	@Test
	void ignoresTheOrderOfKeysAndLists() {
		Map<String, Object> original = map("name", "Book", "classProperties", Arrays.asList("title", "author"), "attributes",
				Arrays.asList(map("name", "title", "width", "100"), map("name", "author")));
		Map<String, Object> formatted = map("attributes", Arrays.asList(map("name", "author"), map("width", "100", "name", "title")), "classProperties",
				Arrays.asList("author", "title"), "name", "Book");
		assertThat(ModelFormatter.difference(original, formatted, "")).isNull();
	}

	@Test
	void namesTheFirstThingThatWouldBeLost() {
		Map<String, Object> original = map("attributes", Collections.singletonList(map("name", "price", "prototypeNam", "amount")));
		assertThat(ModelFormatter.difference(original, map("attributes", Collections.singletonList(map("name", "price"))), ""))
				.isEqualTo("attributes[price].prototypeNam would be dropped");
		assertThat(ModelFormatter.difference(map("attributes", Collections.singletonList(map("name", "price"))), map("attributes", Collections.emptyList()), ""))
				.isEqualTo("attributes[price] would be dropped");
		assertThat(ModelFormatter.difference(map("allowsNull", "YES"), map("allowsNull", "Y"), "")).isEqualTo("allowsNull would change from YES to Y");
		assertThat(ModelFormatter.difference(map("classProperties", Arrays.asList("title", "author")), map("classProperties", Collections.singletonList("title")), ""))
				.isEqualTo("classProperties would lose author");
	}

	/**
	 * A list whose entries have no names, such as a qualifier's, keeps its
	 * order, so an entry that would change is compared with the one in its
	 * place.
	 */
	@Test
	void namesWhatWouldChangeInAnEntryWithoutAName() {
		Map<String, Object> original = map("qualifiers", Arrays.asList(map("key", "login", "selectorName", "isEqualTo"), map("key", "password", "selectorName", "isEqualTo")));
		Map<String, Object> formatted = map("qualifiers", Arrays.asList(map("key", "login", "selectorName", "isEqualTo:"), map("key", "password", "selectorName", "isEqualTo:")));
		assertThat(ModelFormatter.difference(original, formatted, "")).isEqualTo("qualifiers[0].selectorName would change from isEqualTo to isEqualTo:");
		assertThat(ModelFormatter.difference(map("sortOrderings", Collections.singletonList(map("key", "placed", "selector", "compareDescending:"))),
				map("sortOrderings", Collections.singletonList(map("key", "placed"))), "")).isEqualTo("sortOrderings[0].selector would be dropped");
		// The entry in customer's place is store, which is kept, so customer is lost.
		Map<String, Object> customer = map("sourceAttribute", "customerID", "destinationAttribute", "id");
		Map<String, Object> store = map("sourceAttribute", "storeID", "destinationAttribute", "id");
		assertThat(ModelFormatter.difference(map("joins", Arrays.asList(customer, store)), map("joins", Collections.singletonList(store)), ""))
				.isEqualTo("joins would lose an entry");
	}

	@Test
	void allowsAdditionsEmptyValuesAndDuplicatesToGo() {
		assertThat(ModelFormatter.difference(map("name", "Book"), map("name", "Book", "isReadOnly", "Y"), "")).isNull();
		assertThat(ModelFormatter.difference(map("name", "Order", "fetchSpecificationDictionary", Collections.emptyMap()), map("name", "Order"), "")).isNull();
		List<String> twice = Arrays.asList("title", "title");
		assertThat(ModelFormatter.difference(map("classProperties", twice), map("classProperties", Collections.singletonList("title")), "")).isNull();
	}

	private static Map<String, Object> map(Object... keysAndValues) {
		Map<String, Object> map = new LinkedHashMap<>();
		for (int i = 0; i < keysAndValues.length; i += 2) {
			map.put((String) keysAndValues[i], keysAndValues[i + 1]);
		}
		return map;
	}
}

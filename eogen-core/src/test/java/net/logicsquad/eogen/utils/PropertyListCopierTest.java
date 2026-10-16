package net.logicsquad.eogen.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import net.logicsquad.eogen.model.PropertyListMap;

/**
 * PropertyListCopier copies property-list values deeply, keeping the classes
 * of the containers it copies.
 */
class PropertyListCopierTest {
	@Test
	void copiesNestedContainersDeeply() {
		Map<Object, Object> inner = new PropertyListMap<>();
		inner.put("documentation", "A note.");
		List<Object> list = new LinkedList<>(Arrays.<Object>asList("a", inner));
		NotificationMap<Object, Object> userInfo = new NotificationMap<>();
		userInfo.put("_EntityModeler", inner);
		userInfo.put("list", list);

		NotificationMap<Object, Object> copy = PropertyListCopier.deepCopy(userInfo);

		assertThat(copy).isNotSameAs(userInfo).isEqualTo(userInfo);
		assertThat(copy.get("_EntityModeler")).isNotSameAs(inner).isEqualTo(inner).isInstanceOf(PropertyListMap.class);
		assertThat(copy.get("list")).isNotSameAs(list).isEqualTo(list).isInstanceOf(LinkedList.class);
		assertThat(((List<?>) copy.get("list")).get(1)).isNotSameAs(inner);
		inner.put("documentation", "Changed.");
		assertThat(((Map<?, ?>) copy.get("_EntityModeler")).get("documentation")).isEqualTo("A note.");
	}

	@Test
	void sortedContainersKeepTheirComparators() {
		TreeMap<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		map.put("b", "2");
		map.put("A", "1");
		TreeMap<String, String> mapCopy = PropertyListCopier.deepCopy(map);
		assertThat(mapCopy.comparator()).isSameAs(String.CASE_INSENSITIVE_ORDER);
		assertThat(mapCopy.keySet()).containsExactly("A", "b");

		TreeSet<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		set.addAll(Arrays.asList("b", "A"));
		TreeSet<String> setCopy = PropertyListCopier.deepCopy(set);
		assertThat(setCopy.comparator()).isSameAs(String.CASE_INSENSITIVE_ORDER);
		assertThat(setCopy).containsExactly("A", "b");
	}

	@Test
	void setsKeepTheirOrder() {
		Set<String> set = new LinkedHashSet<>(Arrays.asList("z", "a", "m"));
		Set<String> copy = PropertyListCopier.deepCopy(set);
		assertThat(copy).isInstanceOf(LinkedHashSet.class).containsExactly("z", "a", "m");
	}

	@Test
	void otherContainersBecomeJdkContainers() {
		assertThat((Object) PropertyListCopier.deepCopy(Arrays.asList("x", "y"))).isInstanceOf(ArrayList.class).isEqualTo(Arrays.asList("x", "y"));
		assertThat((Object) PropertyListCopier.deepCopy(java.util.Collections.singletonMap("k", "v"))).isInstanceOf(java.util.LinkedHashMap.class);
		assertThat((Object) PropertyListCopier.deepCopy(new HashMap<>())).isInstanceOf(HashMap.class);
	}

	@Test
	void copiesArraysAndDates() {
		byte[] data = { 1, 2, 3 };
		byte[] dataCopy = PropertyListCopier.deepCopy(data);
		assertThat(dataCopy).isNotSameAs(data).containsExactly(1, 2, 3);
		Object[] objects = { "s", new ArrayList<>(Arrays.asList("x")) };
		Object[] objectsCopy = PropertyListCopier.deepCopy(objects);
		assertThat(objectsCopy).isNotSameAs(objects).isEqualTo(objects);
		assertThat(objectsCopy[1]).isNotSameAs(objects[1]);
		Date date = new Date(0);
		assertThat(PropertyListCopier.deepCopy(date)).isNotSameAs(date).isEqualTo(date);
	}

	@Test
	void immutableValuesAreShared() {
		String string = new String("text");
		Integer number = Integer.valueOf(1000);
		assertThat(PropertyListCopier.deepCopy(string)).isSameAs(string);
		assertThat(PropertyListCopier.deepCopy(number)).isSameAs(number);
		assertThat(PropertyListCopier.deepCopy(Boolean.TRUE)).isSameAs(Boolean.TRUE);
		assertThat((Object) PropertyListCopier.deepCopy(null)).isNull();
	}

	@Test
	void refusesOtherObjects() {
		assertThatThrownBy(() -> PropertyListCopier.deepCopy(new Object())).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("java.lang.Object");
	}
}

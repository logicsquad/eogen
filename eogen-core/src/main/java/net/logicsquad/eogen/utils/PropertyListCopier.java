package net.logicsquad.eogen.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Makes deep copies of property-list values: the maps, collections and
 * arrays that EOModel files parse into, and the immutable values inside them.
 * It replaces RIFE's {@code ObjectUtils.deepClone()}, which WOLips used to copy
 * a model object's userInfo.
 */
public final class PropertyListCopier {
	private PropertyListCopier() {
	}

	/**
	 * Returns a deep copy of a property-list value.
	 * <ul>
	 * <li>A {@link NotificationMap} is copied into a new {@code NotificationMap}
	 * backed by a {@link HashMap}, as {@link NotificationMap#clone()} does.</li>
	 * <li>Other maps and collections are copied into new, empty instances of the
	 * same class when that class is one of the JDK's cloneable ones, so that a
	 * sorted map or set keeps its comparator. Any other map becomes a
	 * {@link LinkedHashMap}, list an {@link ArrayList}, and set a
	 * {@link LinkedHashSet}.</li>
	 * <li>Arrays are copied element by element.</li>
	 * <li>{@link Date}s are cloned.</li>
	 * <li>Strings, numbers, booleans, characters and enums are immutable, and are
	 * returned as they are.</li>
	 * </ul>
	 *
	 * @param <T> the value's type
	 * @param value a property-list value, or {@code null}
	 * @return a deep copy of {@code value}, or {@code null} if it is {@code null}
	 * @throws IllegalArgumentException if {@code value} contains an object of any
	 *         other kind
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deepCopy(T value) {
		Object copy;
		if (value == null) {
			copy = null;
		} else if (value instanceof NotificationMap) {
			copy = new NotificationMap<Object, Object>(copyEntries((Map<?, ?>) value, new HashMap<Object, Object>()));
		} else if (value instanceof Map) {
			copy = copyEntries((Map<?, ?>) value, emptyMapLike((Map<?, ?>) value));
		} else if (value instanceof Collection) {
			Collection<Object> elements = emptyCollectionLike((Collection<?>) value);
			for (Object element : (Collection<?>) value) {
				elements.add(deepCopy(element));
			}
			copy = elements;
		} else if (value.getClass().isArray()) {
			copy = copyArray(value);
		} else if (value instanceof Date) {
			copy = ((Date) value).clone();
		} else if (value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof Character || value instanceof Enum) {
			copy = value;
		} else {
			throw new IllegalArgumentException("Can't copy a " + value.getClass().getName() + " in a property list.");
		}
		return (T) copy;
	}

	private static Map<Object, Object> copyEntries(Map<?, ?> source, Map<Object, Object> target) {
		for (Map.Entry<?, ?> entry : source.entrySet()) {
			target.put(deepCopy(entry.getKey()), deepCopy(entry.getValue()));
		}
		return target;
	}

	@SuppressWarnings("unchecked")
	private static Map<Object, Object> emptyMapLike(Map<?, ?> map) {
		Map<Object, Object> empty;
		if (map instanceof TreeMap) {
			empty = (Map<Object, Object>) ((TreeMap<?, ?>) map).clone();
			empty.clear();
		} else if (map instanceof HashMap) {
			empty = (Map<Object, Object>) ((HashMap<?, ?>) map).clone();
			empty.clear();
		} else {
			empty = new LinkedHashMap<Object, Object>();
		}
		return empty;
	}

	@SuppressWarnings("unchecked")
	private static Collection<Object> emptyCollectionLike(Collection<?> collection) {
		Collection<Object> empty;
		if (collection instanceof ArrayList) {
			empty = (Collection<Object>) ((ArrayList<?>) collection).clone();
			empty.clear();
		} else if (collection instanceof LinkedList) {
			empty = (Collection<Object>) ((LinkedList<?>) collection).clone();
			empty.clear();
		} else if (collection instanceof TreeSet) {
			empty = (Collection<Object>) ((TreeSet<?>) collection).clone();
			empty.clear();
		} else if (collection instanceof HashSet) {
			empty = (Collection<Object>) ((HashSet<?>) collection).clone();
			empty.clear();
		} else if (collection instanceof List) {
			empty = new ArrayList<Object>();
		} else if (collection instanceof Set) {
			empty = new LinkedHashSet<Object>();
		} else {
			empty = new ArrayList<Object>();
		}
		return empty;
	}

	private static Object copyArray(Object array) {
		int length = Array.getLength(array);
		Class<?> componentType = array.getClass().getComponentType();
		Object copy = Array.newInstance(componentType, length);
		if (componentType.isPrimitive()) {
			System.arraycopy(array, 0, copy, 0, length);
		} else {
			for (int i = 0; i < length; i++) {
				Array.set(copy, i, deepCopy(Array.get(array, i)));
			}
		}
		return copy;
	}
}

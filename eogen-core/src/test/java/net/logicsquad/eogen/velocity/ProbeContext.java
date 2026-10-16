package net.logicsquad.eogen.velocity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.velocity.context.Context;

/**
 * The values that the templates in {@code src/test/resources/velocity} render.
 * The oracle in {@code src/test/oracle} compiles this class against Velocity
 * 1.5 as well, so it only uses {@link Context#put(String, Object)}.
 */
public final class ProbeContext {
	private ProbeContext() {
	}

	/**
	 * A bean with a property {@code flag} and a void method of the same name,
	 * like EOFetchSpecification's {@code fetchEnterpriseObjects}.
	 */
	public static class Bean {
		public int calls;

		public void flag() {
			calls++;
		}

		public boolean isFlag() {
			return true;
		}

		public String getName() {
			return "bean";
		}

		public int count() {
			return 42;
		}
	}

	/**
	 * Puts the probe values in a context.
	 *
	 * @param context the context to fill
	 * @param listTool the ListTool to render as {@code $list}
	 * @param setTool the SetTool to render as {@code $set}
	 */
	public static void fill(Context context, Object listTool, Object setTool) {
		context.put("items", new ArrayList<String>(Arrays.asList("one", "two", "three")));
		context.put("empty", "");
		context.put("emptyList", new ArrayList<String>());
		context.put("zero", Integer.valueOf(0));
		context.put("a-b", "hyphen-key");
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("key", "value");
		map.put("k2", "v2");
		context.put("map", map);
		context.put("str", "hello");
		Map<String, Object> entity = new HashMap<String, Object>();
		entity.put("name", "Person");
		context.put("entity", entity);
		context.put("withNulls", new ArrayList<String>(Arrays.asList("a", null, "c")));
		context.put("b", new Bean());
		context.put("num", Integer.valueOf(3));
		context.put("big", Long.valueOf(3));
		context.put("bool", Boolean.TRUE);
		context.put("sb", new StringBuilder("abc"));
		final List<String> iterated = Arrays.asList("i1", "i2");
		context.put("iterable", new Iterable<String>() {
			@Override
			public java.util.Iterator<String> iterator() {
				return iterated.iterator();
			}
		});
		context.put("iterator", Arrays.asList("t1", "t2").iterator());
		context.put("array", new String[] { "a1", "a2" });
		context.put("aset", new LinkedHashSet<String>(Arrays.asList("x", "y")));
		context.put("list", listTool);
		context.put("set", setTool);
	}
}

package net.logicsquad.eogen.cli;

import java.util.List;
import java.util.Map;

/**
 * Writes the maps, lists, strings, numbers and booleans of a report as JSON.
 */
final class Json {
	private Json() {
	}

	/**
	 * Returns a value as JSON, indented by two spaces.
	 *
	 * @param value a map, list, string, number, boolean or null
	 * @return JSON
	 */
	static String write(Object value) {
		StringBuilder json = new StringBuilder();
		write(value, json, "");
		return json.toString();
	}

	private static void write(Object value, StringBuilder json, String indent) {
		if (value instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) value;
			if (map.isEmpty()) {
				json.append("{}");
				return;
			}
			json.append("{\n");
			int i = 0;
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				json.append(indent).append("  ");
				string(String.valueOf(entry.getKey()), json);
				json.append(": ");
				write(entry.getValue(), json, indent + "  ");
				json.append(++i < map.size() ? ",\n" : "\n");
			}
			json.append(indent).append('}');
		} else if (value instanceof List) {
			List<?> list = (List<?>) value;
			if (list.isEmpty()) {
				json.append("[]");
				return;
			}
			json.append("[\n");
			for (int i = 0; i < list.size(); i++) {
				json.append(indent).append("  ");
				write(list.get(i), json, indent + "  ");
				json.append(i + 1 < list.size() ? ",\n" : "\n");
			}
			json.append(indent).append(']');
		} else if (value instanceof Number || value instanceof Boolean) {
			json.append(value);
		} else if (value == null) {
			json.append("null");
		} else {
			string(value.toString(), json);
		}
	}

	private static void string(String string, StringBuilder json) {
		json.append('"');
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			switch (c) {
			case '"':
				json.append("\\\"");
				break;
			case '\\':
				json.append("\\\\");
				break;
			case '\n':
				json.append("\\n");
				break;
			case '\r':
				json.append("\\r");
				break;
			case '\t':
				json.append("\\t");
				break;
			default:
				if (c < 0x20) {
					json.append(String.format("\\u%04x", (int) c));
				} else {
					json.append(c);
				}
			}
		}
		json.append('"');
	}
}

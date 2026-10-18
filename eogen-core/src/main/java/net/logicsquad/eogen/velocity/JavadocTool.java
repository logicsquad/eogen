package net.logicsquad.eogen.velocity;

import java.util.ArrayList;
import java.util.List;

import net.logicsquad.eogen.model.UserInfoableEOModelObject;

/**
 * Writes the documentation that Entity Modeler keeps for a model, entity,
 * attribute, relationship or other object as a Javadoc comment, as
 * {@code $javadoc} in the class templates:
 *
 * <pre>
 * #if ($javadoc.has($attribute))
 * $javadoc.comment($attribute, "  ")
 * #end
 * </pre>
 * <p>
 * The documentation is HTML, which Javadoc takes as it is, so it stays as it
 * is, apart from what would break the comment or the source around it: a
 * {@code *} followed by a {@code /}, which would end the comment; a
 * backslash, since a backslash and a {@code u} start a Unicode escape even in
 * a comment; and an {@code @} at the start of a line, which would start a
 * block tag. Each becomes a character reference. Blank lines at either end
 * are dropped, and every line ends with {@code \n}.
 * </p>
 */
public final class JavadocTool {
	/**
	 * Returns whether an object has documentation that isn't blank.
	 *
	 * @param object a model object, its documentation, or null
	 * @return {@code true} if it has
	 */
	public boolean has(Object object) {
		return !lines(object).isEmpty();
	}

	/**
	 * Returns an object's documentation as a Javadoc comment, from {@code /**}
	 * to its end, with each line indented.
	 *
	 * @param object a model object, its documentation, or null
	 * @param indent what goes before each line, such as two spaces for a
	 *        member of a class
	 * @return the comment, with no line end after it, or an empty string if the
	 *         object has no documentation
	 */
	public String comment(Object object, String indent) {
		List<String> lines = lines(object);
		if (lines.isEmpty()) {
			return "";
		}
		return indent + "/**\n" + join(lines, indent) + "\n" + indent + " */";
	}

	/**
	 * Returns the lines of {@link #comment(Object, String)} between its first
	 * and its last, for a template that writes a comment of its own around
	 * them.
	 *
	 * @param object a model object, its documentation, or null
	 * @param indent what goes before each line
	 * @return the lines, each starting with {@code indent} and {@code " *"},
	 *         with no line end after the last, or an empty string if the object
	 *         has no documentation
	 */
	public String lines(Object object, String indent) {
		return join(lines(object), indent);
	}

	private static List<String> lines(Object object) {
		String documentation = null;
		if (object instanceof UserInfoableEOModelObject) {
			documentation = ((UserInfoableEOModelObject<?>) object).getDocumentation();
		} else if (object instanceof String) {
			documentation = (String) object;
		}
		List<String> lines = new ArrayList<>();
		if (documentation == null) {
			return lines;
		}
		for (String line : documentation.split("\r\n|\r|\n", -1)) {
			lines.add(escape(line.stripTrailing()));
		}
		while (!lines.isEmpty() && lines.get(0).isEmpty()) {
			lines.remove(0);
		}
		while (!lines.isEmpty() && lines.get(lines.size() - 1).isEmpty()) {
			lines.remove(lines.size() - 1);
		}
		return lines;
	}

	private static String escape(String line) {
		String escaped = line.replace("\\", "&#92;").replace("*/", "*&#47;");
		int start = 0;
		while (start < escaped.length() && Character.isWhitespace(escaped.charAt(start))) {
			start++;
		}
		if (escaped.startsWith("@", start)) {
			escaped = escaped.substring(0, start) + "&#64;" + escaped.substring(start + 1);
		}
		return escaped;
	}

	private static String join(List<String> lines, String indent) {
		StringBuilder joined = new StringBuilder();
		for (String line : lines) {
			if (joined.length() > 0) {
				joined.append('\n');
			}
			joined.append(indent).append(line.isEmpty() ? " *" : " * " + line);
		}
		return joined.toString();
	}
}

package net.logicsquad.eogen.doc;

/**
 * Escapes text for HTML, as {@code $html} in EOModelDoc's templates, for what
 * a model holds as text rather than as HTML, such as a qualifier, which may
 * have a {@code <} in it.
 */
public final class HtmlTool {
	/**
	 * Returns a value's text, with {@code &}, {@code <}, {@code >} and
	 * {@code "} escaped.
	 *
	 * @param value a value
	 * @return escaped text, or an empty string for null
	 */
	public String escape(Object value) {
		if (value == null) {
			return "";
		}
		String text = value.toString();
		StringBuilder escaped = new StringBuilder(text.length());
		for (char c : text.toCharArray()) {
			switch (c) {
			case '&':
				escaped.append("&amp;");
				break;
			case '<':
				escaped.append("&lt;");
				break;
			case '>':
				escaped.append("&gt;");
				break;
			case '"':
				escaped.append("&quot;");
				break;
			default:
				escaped.append(c);
			}
		}
		return escaped.toString();
	}
}

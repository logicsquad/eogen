package net.logicsquad.eogen.lint;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds what in the HTML of a model's documentation a Javadoc build would
 * fail on, now that the class templates can put it in Javadoc comments: a tag
 * that HTML5 doesn't have, such as a misspelt {@code <code>}; an end tag that
 * closes nothing; an element whose end tag HTML requires, left open; and an
 * {@code &} or a {@code <} that starts nothing. The rules are those of
 * Javadoc's own checks, which reject what a browser would forgive.
 */
final class DocumentationChecker {
	/**
	 * The elements that need an end tag.
	 */
	private static final Set<String> CLOSED = names("a", "abbr", "address", "article", "aside", "b", "bdi", "bdo", "blockquote", "cite", "code", "del", "details", "dfn",
			"div", "dl", "em", "figcaption", "figure", "footer", "h1", "h2", "h3", "h4", "h5", "h6", "header", "i", "ins", "kbd", "main", "mark", "nav", "ol", "pre", "q", "s",
			"samp", "section", "small", "span", "strong", "sub", "summary", "sup", "table", "time", "u", "ul", "var");

	/**
	 * The elements whose end tag may be left out.
	 */
	private static final Set<String> OPTIONAL = names("caption", "colgroup", "dd", "dt", "li", "p", "tbody", "td", "tfoot", "th", "thead", "tr");

	/**
	 * The elements that have no end tag.
	 */
	private static final Set<String> VOID = names("br", "col", "hr", "img", "wbr");

	/**
	 * Tags of earlier HTML, which Javadoc rejects in HTML5.
	 */
	private static final Set<String> OBSOLETE = names("acronym", "big", "center", "font", "strike", "tt");

	private static final Pattern REFERENCE = Pattern.compile("&(?:[A-Za-z][A-Za-z0-9]*|#[0-9]+|#[xX][0-9A-Fa-f]+);");

	private static final Pattern START_TAG = Pattern.compile("<([A-Za-z][A-Za-z0-9]*)((?:\\s+[^<>]*)?)(/?)>");

	private static final Pattern END_TAG = Pattern.compile("</([A-Za-z][A-Za-z0-9]*)\\s*>");

	private static final Pattern TAG_NAME = Pattern.compile("</?([A-Za-z][A-Za-z0-9]*)");

	/**
	 * Stands, among the open elements, for a tag that isn't HTML's, so that the
	 * end tag that was meant to close it isn't reported too.
	 */
	private static final String UNKNOWN = "?";

	private DocumentationChecker() {
	}

	/**
	 * What is wrong with a piece of documentation: its key, such as the tag,
	 * what it most likely meant, and a description.
	 */
	static final class Problem {
		private final String key;

		private final String suggestion;

		private final String description;

		Problem(String key, String suggestion, String description) {
			this.key = key;
			this.suggestion = suggestion;
			this.description = description;
		}

		String getKey() {
			return key;
		}

		String getSuggestion() {
			return suggestion;
		}

		String getDescription() {
			return description;
		}
	}

	/**
	 * Checks documentation.
	 *
	 * @param html documentation
	 * @return what is wrong with it, in the order it comes
	 */
	static List<Problem> check(String html) {
		List<Problem> problems = new ArrayList<>();
		Deque<String> open = new ArrayDeque<>();
		int i = 0;
		while (i < html.length()) {
			char c = html.charAt(i);
			if (c == '&') {
				Matcher reference = REFERENCE.matcher(html).region(i, html.length());
				if (reference.lookingAt()) {
					i = reference.end();
				} else {
					problems.add(new Problem("&", "&amp;", "an & that starts no character reference"));
					i++;
				}
			} else if (c == '<') {
				i = tag(html, i, open, problems);
			} else {
				i++;
			}
		}
		for (Iterator<String> names = open.descendingIterator(); names.hasNext();) {
			String name = names.next();
			if (CLOSED.contains(name)) {
				problems.add(new Problem(name, null, "<" + name + "> with no </" + name + ">"));
			}
		}
		return problems;
	}

	/**
	 * Reads the tag or comment at a {@code <}, and returns where it ends.
	 */
	private static int tag(String html, int i, Deque<String> open, List<Problem> problems) {
		if (html.startsWith("<!--", i)) {
			int end = html.indexOf("-->", i + 4);
			if (end < 0) {
				problems.add(new Problem("<!--", null, "a comment with no end (-->)"));
				return html.length();
			}
			return end + 3;
		}
		Matcher end = END_TAG.matcher(html).region(i, html.length());
		if (end.lookingAt()) {
			String name = end.group(1).toLowerCase(Locale.ROOT);
			if (isKnown(name)) {
				close(name, open, problems);
			} else if (!open.removeFirstOccurrence(UNKNOWN)) {
				known(name, problems, "/" + end.group(1));
			}
			return end.end();
		}
		Matcher start = START_TAG.matcher(html).region(i, html.length());
		if (start.lookingAt()) {
			String name = start.group(1).toLowerCase(Locale.ROOT);
			if (!known(name, problems, start.group(1))) {
				open.push(UNKNOWN);
			} else if (!VOID.contains(name) && start.group(3).isEmpty()) {
				open.push(name);
			}
			return start.end();
		}
		Matcher name = TAG_NAME.matcher(html).region(i, html.length());
		if (name.lookingAt()) {
			String tag = name.group(1);
			String suggestion = ModelLinter.suggest(tag.toLowerCase(Locale.ROOT), allNames());
			problems.add(new Problem(tag, suggestion == null ? null : "<" + suggestion + ">", name.group() + " with no >"));
			if (!html.startsWith("</", i)) {
				open.push(UNKNOWN);
			}
			return name.end();
		}
		problems.add(new Problem("<", "&lt;", "a < that starts no tag"));
		return i + 1;
	}

	private static boolean isKnown(String name) {
		return CLOSED.contains(name) || OPTIONAL.contains(name) || VOID.contains(name);
	}

	/**
	 * Returns whether a tag's name is one that Javadoc accepts, and reports it
	 * if it isn't.
	 */
	private static boolean known(String name, List<Problem> problems, String written) {
		if (isKnown(name)) {
			return true;
		}
		if (OBSOLETE.contains(name)) {
			problems.add(new Problem(written, null, "<" + written + ">, which HTML5 doesn't have"));
		} else {
			String suggestion = ModelLinter.suggest(name, allNames());
			String slash = written.startsWith("/") ? "/" : "";
			problems.add(new Problem(written, suggestion == null ? null : "<" + slash + suggestion + ">", "<" + written + ">, which isn't an HTML tag"));
		}
		return false;
	}

	/**
	 * Closes the innermost open element of a name, and the elements inside it,
	 * reporting those that needed an end tag. An end tag that closes nothing is
	 * reported, unless a tag that isn't HTML's is open, which it is taken to
	 * close.
	 */
	private static void close(String name, Deque<String> open, List<Problem> problems) {
		if (VOID.contains(name) || !open.contains(name)) {
			if (VOID.contains(name) || !open.removeFirstOccurrence(UNKNOWN)) {
				problems.add(new Problem("/" + name, null, "</" + name + ">, which closes no <" + name + ">"));
			}
			return;
		}
		while (!open.isEmpty()) {
			String inner = open.pop();
			if (inner.equals(name)) {
				return;
			}
			if (CLOSED.contains(inner)) {
				problems.add(new Problem(inner, null, "<" + inner + "> with no </" + inner + "> before </" + name + ">"));
			}
		}
	}

	private static Set<String> allNames() {
		Set<String> all = new TreeSet<>(CLOSED);
		all.addAll(OPTIONAL);
		all.addAll(VOID);
		return all;
	}

	private static Set<String> names(String... names) {
		return Collections.unmodifiableSet(new TreeSet<>(Arrays.asList(names)));
	}
}

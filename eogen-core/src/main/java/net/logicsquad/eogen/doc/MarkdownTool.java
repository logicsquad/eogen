package net.logicsquad.eogen.doc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.logicsquad.eogen.model.EOAttribute;
import net.logicsquad.eogen.model.EOAttributePath;
import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EOModelObject;
import net.logicsquad.eogen.model.EORelationship;
import net.logicsquad.eogen.model.UserInfoableEOModelObject;

/**
 * Writes what a model has as Markdown, as {@code $md} in the template of the
 * Markdown pages. Documentation is HTML, which Markdown keeps as it is, so it
 * goes in as it is, except that in a table it is one line, with {@code |}
 * escaped.
 */
public final class MarkdownTool {
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
	 * Returns the first paragraph of an object's documentation, where a blank
	 * line ends it, for a table's cell: its lines joined with a space, with
	 * {@code |} escaped.
	 *
	 * @param object a model object, its documentation, or null
	 * @return the paragraph, or an empty string
	 */
	public String summary(Object object) {
		List<String> paragraph = new ArrayList<>();
		for (String line : lines(object)) {
			if (line.trim().isEmpty()) {
				break;
			}
			paragraph.add(line.trim());
		}
		return escapeBars(String.join(" ", paragraph));
	}

	/**
	 * Returns an object's documentation, to stand on its own: with each line
	 * end an LF, no space at the end of a line, and no blank line at either
	 * end.
	 *
	 * @param object a model object, its documentation, or null
	 * @return documentation, or an empty string
	 */
	public String block(Object object) {
		return String.join("\n", lines(object));
	}

	/**
	 * Returns an object's documentation for a table's cell: its paragraphs,
	 * where a blank line separates them, joined with {@code <br><br>}, and the
	 * lines in each joined with a space, with {@code |} escaped.
	 *
	 * @param object a model object, its documentation, or null
	 * @return documentation, or an empty string
	 */
	public String cell(Object object) {
		StringBuilder cell = new StringBuilder();
		boolean paragraph = false;
		for (String line : lines(object)) {
			String trimmed = line.trim();
			if (trimmed.isEmpty()) {
				paragraph = true;
				continue;
			}
			if (cell.length() > 0) {
				cell.append(paragraph ? "<br><br>" : " ");
			}
			cell.append(trimmed);
			paragraph = false;
		}
		return escapeBars(cell.toString());
	}

	/**
	 * Returns a value as code: in backticks, or in two if it has one, with
	 * {@code |} escaped, so that it can go in a table's cell.
	 *
	 * @param value a value
	 * @return code, or an empty string for null or an empty string
	 */
	public String code(Object value) {
		if (value == null || value.toString().isEmpty()) {
			return "";
		}
		String text = value.toString().replace("\r\n", " ").replace('\r', ' ').replace('\n', ' ');
		String fence = text.contains("`") ? "``" : "`";
		String padding = text.startsWith("`") || text.endsWith("`") ? " " : "";
		return fence + padding + escapeBars(text) + padding + fence;
	}

	/**
	 * Returns the line under a heading: a character as many times as the
	 * heading has characters, and at least three.
	 *
	 * @param heading the heading
	 * @param character {@code =} or {@code -}
	 * @return the line
	 */
	public String rule(String heading, String character) {
		StringBuilder rule = new StringBuilder();
		for (int i = 0; i < Math.max(3, heading.length()); i++) {
			rule.append(character);
		}
		return rule.toString();
	}

	/**
	 * Returns an attribute's column type, from its external type and its width,
	 * or precision and scale, such as {@code varchar(100)}.
	 *
	 * @param attribute an attribute
	 * @return the type, or an empty string if it has no external type
	 */
	public String type(EOAttribute attribute) {
		String externalType = attribute.getExternalType();
		if (externalType == null || externalType.isEmpty()) {
			return "";
		}
		Integer width = attribute.getWidth();
		Integer precision = attribute.getPrecision();
		Integer scale = attribute.getScale();
		if (precision != null && precision.intValue() > 0) {
			return externalType + "(" + precision + (scale != null ? ", " + scale : "") + ")";
		}
		if (width != null && width.intValue() > 0) {
			return externalType + "(" + width + ")";
		}
		return externalType;
	}

	/**
	 * Returns the Java class of an attribute's values, as the bundled templates
	 * write it in its accessors: its {@code ERXConstantClassName}, if it has
	 * one, or else its value class, such as {@code String} or
	 * {@code com.example.shop.Status}, with a nested class's {@code $} as a dot.
	 * A flattened attribute has the class of the attribute that it flattens.
	 *
	 * @param attribute an attribute
	 * @return the class, or an empty string if it has none
	 */
	public String javaType(EOAttribute attribute) {
		EOAttribute resolved = attribute;
		Set<EOAttribute> seen = new HashSet<>();
		while (resolved != null && resolved.isFlattened() && seen.add(resolved)) {
			EOAttributePath path = resolved.getDefinitionPath();
			resolved = path == null ? null : path.getChildAttribute();
		}
		if (resolved == null || resolved.isFlattened()) {
			return "";
		}
		Object constant = resolved.getUserInfo() == null ? null : resolved.getUserInfo().get("ERXConstantClassName");
		String className = constant != null && !constant.toString().isEmpty() ? constant.toString() : resolved.getJavaClassName();
		return className == null ? "" : className.replace('$', '.');
	}

	/**
	 * Returns the anchor of an entity's section in its model's page, as GitHub
	 * and GitLab make it from the heading.
	 *
	 * @param entity an entity
	 * @return the anchor, without {@code #}
	 */
	public String anchor(EOEntity entity) {
		return entity.getName().toLowerCase(Locale.ROOT);
	}

	/**
	 * Returns a link to an entity's section, in this page or in its model's,
	 * or else its name, if its model isn't documented.
	 *
	 * @param entity an entity
	 * @param page the model whose page it is on
	 * @param documented the names of the models documented
	 * @return a link, or the entity's name
	 */
	public String link(EOEntity entity, EOModel page, Collection<String> documented) {
		EOModel model = entity.getModel();
		if (model == page) {
			return "[" + entity.getName() + "](#" + anchor(entity) + ")";
		}
		if (model != null && documented.contains(model.getName())) {
			return "[" + entity.getName() + "](" + model.getName() + ".md#" + anchor(entity) + ")";
		}
		return entity.getName();
	}

	/**
	 * Returns links to entities, in name order, separated by commas.
	 *
	 * @param entities entities
	 * @param page the model whose page they are on
	 * @param documented the names of the models documented
	 * @return links
	 */
	public String links(Collection<EOEntity> entities, EOModel page, Collection<String> documented) {
		List<EOEntity> sorted = new ArrayList<>(entities);
		sorted.sort(Comparator.comparing(EOEntity::getName));
		List<String> links = new ArrayList<>();
		for (EOEntity entity : sorted) {
			links.add(link(entity, page, documented));
		}
		return String.join(", ", links);
	}

	/**
	 * Returns the names of model objects, as code, in their order, separated
	 * by commas.
	 *
	 * @param objects model objects, such as an entity index's attributes
	 * @return names
	 */
	public String names(Collection<?> objects) {
		List<String> names = new ArrayList<>();
		for (Object object : objects) {
			names.add(code(object instanceof EOModelObject ? ((EOModelObject<?>) object).getName() : object));
		}
		return String.join(", ", names);
	}

	/**
	 * Returns a model's entities, other than its prototypes, in name order.
	 *
	 * @param model a model
	 * @return entities
	 */
	public List<EOEntity> entities(EOModel model) {
		return documentedEntities(model);
	}

	/**
	 * Returns a model's entities, other than its prototypes, in name order: the
	 * ones that its pages document.
	 */
	static List<EOEntity> documentedEntities(EOModel model) {
		List<EOEntity> entities = new ArrayList<>();
		for (EOEntity entity : model.getSortedEntities()) {
			if (!entity.isPrototype()) {
				entities.add(entity);
			}
		}
		return entities;
	}

	/**
	 * Returns the attributes that an entity has of its own, rather than from
	 * its parent, in name order.
	 *
	 * @param entity an entity
	 * @return attributes
	 */
	public List<EOAttribute> attributes(EOEntity entity) {
		List<EOAttribute> attributes = new ArrayList<>();
		for (EOAttribute attribute : entity.getSortedAttributes()) {
			if (!attribute.isInherited()) {
				attributes.add(attribute);
			}
		}
		return attributes;
	}

	/**
	 * Returns the relationships that an entity has of its own, rather than
	 * from its parent, in name order.
	 *
	 * @param entity an entity
	 * @return relationships
	 */
	public List<EORelationship> relationships(EOEntity entity) {
		List<EORelationship> relationships = new ArrayList<>();
		for (EORelationship relationship : entity.getSortedRelationships()) {
			if (!relationship.isInherited()) {
				relationships.add(relationship);
			}
		}
		return relationships;
	}

	private static String escapeBars(String text) {
		return text.replace("|", "\\|");
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
			lines.add(line.stripTrailing());
		}
		while (!lines.isEmpty() && lines.get(0).isEmpty()) {
			lines.remove(0);
		}
		while (!lines.isEmpty() && lines.get(lines.size() - 1).isEmpty()) {
			lines.remove(lines.size() - 1);
		}
		return lines;
	}
}

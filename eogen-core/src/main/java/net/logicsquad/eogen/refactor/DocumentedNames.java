package net.logicsquad.eogen.refactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.logicsquad.eogen.model.EOArgument;
import net.logicsquad.eogen.model.EOAttribute;
import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOEntityIndex;
import net.logicsquad.eogen.model.EOFetchSpecification;
import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EORelationship;
import net.logicsquad.eogen.model.EOStoredProcedure;
import net.logicsquad.eogen.model.UserInfoableEOModelObject;

/**
 * Finds the names in a model's documentation: what its {@code <code>}
 * elements hold, where documentation names entities, attributes and
 * relationships, such as {@code <code>Book</code>} or
 * {@code <code>Book.title</code>}. A name in the text around them isn't one,
 * since it may be a word, such as "name" or "status".
 */
final class DocumentedNames {
	private static final Pattern CODE = Pattern.compile("<code>(.*?)</code>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	private DocumentedNames() {
	}

	/**
	 * An object's documentation, and how to name the object.
	 */
	static final class Documented {
		private final String documentation;

		private final EOEntity entity;

		private final String description;

		private final String object;

		private Documented(String documentation, EOEntity entity, String description, String object) {
			this.documentation = documentation;
			this.entity = entity;
			this.description = description;
			this.object = object;
		}

		/**
		 * Returns the entity that the object is, or belongs to.
		 *
		 * @return entity, or null for a model or a stored procedure
		 */
		EOEntity getEntity() {
			return entity;
		}

		/**
		 * Returns how a message names the object, such as
		 * {@code Book's attribute title}.
		 *
		 * @return description
		 */
		String getDescription() {
			return description;
		}

		/**
		 * Returns how a finding names the object, such as
		 * {@code attribute title}.
		 *
		 * @return object
		 */
		String getObject() {
			return object;
		}

		/**
		 * Returns whether the documentation names a name, alone or as a part of
		 * a dotted name, in a {@code <code>} element.
		 *
		 * @param name a name
		 * @return {@code true} if it does
		 */
		boolean names(String name) {
			for (List<String> parts : codeParts(documentation)) {
				if (parts.contains(name)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Returns whether the documentation names a property after one of some
		 * entities' names and a dot, such as {@code Book.title}, in a
		 * {@code <code>} element.
		 *
		 * @param entityNames entities' names
		 * @param property a property's name
		 * @return {@code true} if it does
		 */
		boolean namesProperty(Collection<String> entityNames, String property) {
			for (List<String> parts : codeParts(documentation)) {
				for (int i = 0; i + 1 < parts.size(); i++) {
					if (entityNames.contains(parts.get(i)) && parts.get(i + 1).equals(property)) {
						return true;
					}
				}
			}
			return false;
		}
	}

	/**
	 * Returns the documented objects of a model, in the order of their
	 * entities' names and their own: the model, each entity and its
	 * attributes, relationships, fetch specifications and entity indexes, and
	 * each stored procedure and its arguments.
	 *
	 * @param model a model
	 * @return the documented objects
	 */
	static List<Documented> of(EOModel model) {
		List<Documented> documented = new ArrayList<>();
		add(documented, model, null, "the model " + model.getName(), "the model");
		List<EOEntity> entities = new ArrayList<>(model.getSortedEntities());
		for (EOEntity entity : entities) {
			String name = entity.getName();
			add(documented, entity, entity, name, "entity " + name);
			for (EOAttribute attribute : entity.getSortedAttributes()) {
				add(documented, attribute, entity, name + "'s attribute " + attribute.getName(), "attribute " + attribute.getName());
			}
			for (EORelationship relationship : entity.getSortedRelationships()) {
				add(documented, relationship, entity, name + "'s relationship " + relationship.getName(), "relationship " + relationship.getName());
			}
			for (EOFetchSpecification fetchSpecification : entity.getSortedFetchSpecs()) {
				add(documented, fetchSpecification, entity, name + "'s fetch specification " + fetchSpecification.getName(), "fetch specification " + fetchSpecification.getName());
			}
			for (EOEntityIndex index : entity.getSortedEntityIndexes()) {
				add(documented, index, entity, name + "'s entity index " + index.getName(), "index " + index.getName());
			}
		}
		for (EOStoredProcedure procedure : model.getSortedStoredProcedures()) {
			add(documented, procedure, null, "stored procedure " + procedure.getName(), "stored procedure " + procedure.getName());
			for (EOArgument argument : procedure.getSortedArguments()) {
				add(documented, argument, null, "stored procedure " + procedure.getName() + "'s argument " + argument.getName(), "argument " + argument.getName());
			}
		}
		return documented;
	}

	private static void add(List<Documented> documented, UserInfoableEOModelObject<?> object, EOEntity entity, String description, String name) {
		String documentation = object.getDocumentation();
		if (documentation != null && !documentation.trim().isEmpty()) {
			documented.add(new Documented(documentation, entity, description, name));
		}
	}

	/**
	 * Returns the parts of each {@code <code>} element's text, split at dots,
	 * without a trailing {@code ()}.
	 */
	private static List<List<String>> codeParts(String documentation) {
		List<List<String>> codes = new ArrayList<>();
		Matcher matcher = CODE.matcher(documentation);
		while (matcher.find()) {
			List<String> parts = new ArrayList<>();
			for (String part : matcher.group(1).split("\\.", -1)) {
				String trimmed = part.trim();
				if (trimmed.endsWith("()")) {
					trimmed = trimmed.substring(0, trimmed.length() - 2);
				}
				parts.add(trimmed);
			}
			codes.add(parts);
		}
		return codes;
	}
}

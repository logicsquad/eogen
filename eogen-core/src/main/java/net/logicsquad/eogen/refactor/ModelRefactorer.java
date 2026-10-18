package net.logicsquad.eogen.refactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.logicsquad.eogen.Finding;
import net.logicsquad.eogen.model.EOAttribute;
import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOFetchSpecification;
import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EOModelGroup;
import net.logicsquad.eogen.model.EOModelReferenceFailure;
import net.logicsquad.eogen.model.EORelationship;
import net.logicsquad.eogen.model.IEOAttribute;
import net.logicsquad.eogen.model.UserInfoableEOModelObject;

/**
 * Renames and removes entities, attributes and relationships in a loaded and
 * resolved model group, through Entity Modeler's model classes, which carry a
 * rename into subentities, flattened definitions, joins, entity indexes and
 * the property lists. The keys that models name as text, in fetch
 * specifications and restricting qualifiers, it rewrites itself.
 * <p>
 * It changes names only. Entity Modeler's own rename also renames the table,
 * the column or the class, but this keeps them, so that the database needs no
 * change, and changes an entity's class name only if asked. A removal is
 * refused while anything else uses what it would remove: nothing is removed
 * by cascade.
 * </p>
 * <p>
 * It changes no documentation, since only the words around a name say
 * whether it means what was renamed, but reports, as left to do by hand, the
 * documentation that names it in a {@code <code>} element.
 * </p>
 */
public final class ModelRefactorer {
	private ModelRefactorer() {
	}

	/**
	 * What a rename or a removal did, or why it was refused.
	 */
	public static final class Change {
		private final List<Finding> refusals = new ArrayList<>();

		private final List<Finding> byHand = new ArrayList<>();

		private Change() {
		}

		/**
		 * Returns why the change was refused, as errors: then the models are as
		 * they were.
		 *
		 * @return refusals
		 */
		public List<Finding> getRefusals() {
			return Collections.unmodifiableList(refusals);
		}

		/**
		 * Returns what is left to do by hand, as warnings with the code
		 * {@link Finding#BY_HAND}.
		 *
		 * @return what to do by hand
		 */
		public List<Finding> getByHand() {
			return Collections.unmodifiableList(byHand);
		}

		private Change refuse(String code, String message, String model, String entity) {
			refusals.add(Finding.error(code, message).model(model).entity(entity).build());
			return this;
		}

		private void byHand(String message, String model, String entity, String object) {
			byHand.add(Finding.builder(Finding.Severity.WARNING, Finding.BY_HAND, message).model(model).entity(entity).object(object).build());
		}
	}

	/**
	 * Renames an entity, or an attribute or relationship.
	 *
	 * @param group the loaded and resolved model group
	 * @param projectModels the project's models, which alone are changed
	 * @param target {@code Entity}, or {@code Entity.property}
	 * @param newName the new name
	 * @param renameClass for an entity, whether to rename its class too, when
	 *        the class's simple name is the entity's
	 * @return what it did, or why it was refused
	 */
	public static Change rename(EOModelGroup group, Collection<EOModel> projectModels, String target, String newName, boolean renameClass) {
		Change change = new Change();
		Target found = find(group, projectModels, target, change);
		if (found == null) {
			return change;
		}
		if (newName == null || !newName.matches("[A-Za-z_][A-Za-z0-9_]*")) {
			return change.refuse(Finding.FAILURE, target + " can't be renamed to " + newName + ", which isn't a name.", found.modelName(), found.entity.getName());
		}
		if (found.property == null) {
			return renameEntity(group, projectModels, found, newName, renameClass, change);
		}
		return renameProperty(projectModels, found, newName, change);
	}

	private static Change renameEntity(EOModelGroup group, Collection<EOModel> projectModels, Target found, String newName, boolean renameClass, Change change) {
		EOEntity entity = found.entity;
		String oldName = entity.getName();
		if (newName.equals(oldName)) {
			return change;
		}
		if (group.getEntityNamed(newName) != null) {
			return change.refuse(Finding.NAME_TAKEN, "There is already an entity named " + newName + ", so " + oldName + " can't be renamed to it.", found.modelName(), oldName);
		}
		String oldClassName = entity.getClassName();
		Set<String> deletedInObjectStore = new TreeSet<>(found.model.getDeletedEntityNamesInObjectStore());
		try {
			// Without events, so that Entity Modeler doesn't rename the table and
			// the class to match.
			entity.setName(newName, false);
		} catch (Exception e) {
			return change.refuse(Finding.NAME_TAKEN, oldName + " can't be renamed to " + newName + ": " + e.getMessage(), found.modelName(), oldName);
		}
		// The table stays, so it isn't deleted from the object store either.
		found.model.getDeletedEntityNamesInObjectStore().clear();
		found.model.getDeletedEntityNamesInObjectStore().addAll(deletedInObjectStore);
		if (renameClass && oldClassName != null) {
			int dot = oldClassName.lastIndexOf('.');
			String simpleName = oldClassName.substring(dot + 1);
			if (simpleName.equals(oldName)) {
				entity.setClassName(oldClassName.substring(0, dot + 1) + newName);
			} else {
				change.byHand("The class " + oldClassName + " isn't named for the entity, so it stays as it is. Rename it by hand if it should change.", found.modelName(),
						newName, "entity " + newName);
			}
		}
		if (!newName.equals(oldName) && oldClassName != null && !oldClassName.equals(entity.getClassName())) {
			change.byHand("Rename the subclass " + oldClassName + " to " + entity.getClassName()
					+ " by hand, since eogen never edits a subclass. Then generate with deleteOrphans to delete its old superclass.", found.modelName(), newName,
					"entity " + newName);
		}
		change.byHand("Code that names the entity " + oldName + " as text, such as in a fetch, needs changing to " + newName + ".", found.modelName(), newName,
				"entity " + newName);
		if (newName.equalsIgnoreCase(oldName)) {
			// A class is renamed only if it is named for the entity, as are its files.
			boolean classRenamed = oldClassName != null && !oldClassName.equals(entity.getClassName());
			String files = classRenamed ? "the entity's files, and its classes', such as " + oldName + ".java and _" + oldName + ".java," : "the entity's files";
			change.byHand(oldName + " and " + newName + " differ only in case, so where the file system ignores case, version control may keep " + files
					+ " under their old names. Rename them there too, as with git mv " + oldName + ".plist " + newName + ".plist.", found.modelName(), newName, "entity " + newName);
		}
		textThatNames(projectModels, oldName, change);
		documentationThatNames(projectModels, Collections.singletonList(entity), null, oldName, newName, change);
		return change;
	}

	private static Change renameProperty(Collection<EOModel> projectModels, Target found, String newName, Change change) {
		EOEntity entity = found.entity;
		String oldName = found.property.getName();
		if (newName.equals(oldName)) {
			return change;
		}
		List<EOEntity> entities = family(entity);
		for (EOEntity member : entities) {
			if (member.getAttributeOrRelationshipNamed(newName) != null) {
				return change.refuse(Finding.NAME_TAKEN, member.getName() + " already has a property named " + newName + ", so " + target(found) + " can't be renamed to it.",
						found.modelName(), member.getName());
			}
		}
		// The key paths resolve only while the property has its old name.
		List<KeyPaths.Use> unwritten = KeyPaths.rename(projectModels, entity, oldName, newName);
		try {
			if (found.property instanceof EOAttribute) {
				// Without events, so that Entity Modeler doesn't rename the column to
				// match. It renames a subentity's copy, and the flattened
				// definitions that use it.
				((EOAttribute) found.property).setName(newName, false);
			} else {
				for (EOEntity member : entities) {
					EORelationship relationship = member.getRelationshipNamed(oldName);
					if (relationship != null) {
						relationship.setName(newName, false);
					}
				}
			}
		} catch (Exception e) {
			return change.refuse(Finding.NAME_TAKEN, target(found) + " can't be renamed to " + newName + ": " + e.getMessage(), found.modelName(), entity.getName());
		}
		for (EOEntity member : entities) {
			member._renameInPropertyLists(oldName, newName);
			member.setEntityDirty(true);
		}
		for (KeyPaths.Use use : unwritten) {
			change.byHand(use.describe() + " uses " + oldName + ", but eogen can't rewrite it. Change it to " + newName + " by hand.", use.getEntity().getModel().getName(),
					use.getEntity().getName(), use.getObject());
		}
		String kind = found.property instanceof EOAttribute ? "attribute" : "relationship";
		change.byHand("Code that uses the " + kind + "'s accessors, or names " + oldName + " as text, such as in a qualifier, needs changing for " + newName
				+ ". Compile to find the accessors.", found.modelName(), entity.getName(), kind + " " + newName);
		textThatNames(projectModels, oldName, change);
		documentationThatNames(projectModels, entities, oldName, oldName, newName, change);
		return change;
	}

	/**
	 * Removes an entity, or an attribute or relationship.
	 *
	 * @param group the loaded and resolved model group
	 * @param projectModels the project's models, which alone are changed
	 * @param target {@code Entity}, or {@code Entity.property}
	 * @return what it did, or why it was refused
	 */
	public static Change remove(EOModelGroup group, Collection<EOModel> projectModels, String target) {
		Change change = new Change();
		Target found = find(group, projectModels, target, change);
		if (found == null) {
			return change;
		}
		List<String> uses = new ArrayList<>();
		if (found.property == null) {
			for (EOModelReferenceFailure<?, ?> failure : references(found.entity.getReferenceFailures())) {
				uses.add(failure.getMessage());
			}
		} else {
			Set<EOModelReferenceFailure> failures = found.property instanceof EOAttribute ? ((EOAttribute) found.property).getReferenceFailures()
					: ((EORelationship) found.property).getReferenceFailures();
			for (EOModelReferenceFailure<?, ?> failure : references(failures)) {
				uses.add(failure.getMessage());
			}
			if (found.property instanceof EOAttribute && Boolean.TRUE.equals(((EOAttribute) found.property).isPrimaryKey())) {
				uses.add(found.property.getName() + " is a primary key attribute of " + found.entity.getName() + ".");
			}
			for (KeyPaths.Use use : KeyPaths.uses(projectModels, found.entity, found.property.getName())) {
				uses.add(found.property.getName() + " is in " + use.describe() + ".");
			}
		}
		if (!uses.isEmpty()) {
			for (String use : uses) {
				change.refuse(Finding.IN_USE, target(found) + " is in use, so it isn't removed: " + use, found.modelName(), found.entity.getName());
			}
			return change;
		}
		if (found.property == null) {
			EOEntity entity = found.entity;
			found.model.removeEntity(entity);
			change.byHand("Generate with deleteOrphans to delete the superclass of " + entity.getName() + ", and delete its subclass, " + entity.getClassName()
					+ ", by hand.", found.modelName(), entity.getName(), "entity " + entity.getName());
		} else if (found.property instanceof EOAttribute) {
			found.entity.removeAttribute((EOAttribute) found.property, true);
			change.byHand("Code that uses the attribute's accessors, " + found.property.getName() + "() and its setter, needs changing. Compile to find it. Its column stays in the database.",
					found.modelName(), found.entity.getName(), "attribute " + found.property.getName());
		} else {
			found.entity.removeRelationship((EORelationship) found.property, true);
			change.byHand("Code that uses the relationship's accessors, " + found.property.getName() + "() and the others, needs changing. Compile to find it.",
					found.modelName(), found.entity.getName(), "relationship " + found.property.getName());
		}
		if (found.property == null) {
			documentationThatNames(projectModels, Collections.singletonList(found.entity), null, found.entity.getName(), null, change);
		} else {
			documentationThatNames(projectModels, family(found.entity), found.property.getName(), found.property.getName(), null, change);
		}
		for (EOEntity entity : found.model.getEntities()) {
			entity.setEntityDirty(true);
		}
		return change;
	}

	/**
	 * Reports text in the models that still names an old name, such as in a
	 * custom SQL hint or userInfo, which only a person can judge.
	 */
	private static void textThatNames(Collection<EOModel> projectModels, String oldName, Change change) {
		for (EOModel model : projectModels) {
			for (EOEntity entity : sortedEntities(model)) {
				for (EOFetchSpecification fetchSpecification : entity.getFetchSpecs()) {
					String sql = fetchSpecification.getCustomQueryExpression();
					if (sql != null && containsWord(sql, oldName)) {
						change.byHand(entity.getName() + "'s fetch specification " + fetchSpecification.getName() + " has custom SQL that names " + oldName
								+ ". Check it by hand.", model.getName(), entity.getName(), "fetch specification " + fetchSpecification.getName());
					}
				}
				if (mentions(withoutDocumentation(entity.getUserInfo()), oldName)) {
					change.byHand("The userInfo of " + entity.getName() + " names " + oldName + ". Check it by hand.", model.getName(), entity.getName(), "entity " + entity.getName());
				}
			}
		}
	}

	/**
	 * Reports the documentation in the project's models that names a renamed
	 * or removed entity, attribute or relationship in a {@code <code>} element:
	 * an entity's name anywhere, and a property's in the documentation of its
	 * entity, the entity's subentities and their properties, or after one of
	 * their names and a dot, as in {@code Book.title}.
	 *
	 * @param entities the entity, or the property's entity and its subentities
	 * @param property the property's old name, or null for an entity
	 * @param oldName the old name
	 * @param newName the new name, or null for a removal
	 */
	private static void documentationThatNames(Collection<EOModel> projectModels, List<EOEntity> entities, String property, String oldName, String newName,
			Change change) {
		Set<String> entityNames = new TreeSet<>();
		for (EOEntity entity : entities) {
			entityNames.add(entity.getName());
		}
		List<EOModel> models = new ArrayList<>(projectModels);
		models.sort(Comparator.comparing(EOModel::getName));
		for (EOModel model : models) {
			for (DocumentedNames.Documented documented : DocumentedNames.of(model)) {
				boolean names;
				if (property == null) {
					names = documented.names(oldName);
				} else {
					names = entities.stream().anyMatch(entity -> entity == documented.getEntity()) && documented.names(property)
							|| documented.namesProperty(entityNames, property);
				}
				if (names) {
					change.byHand("The documentation of " + documented.getDescription() + " names " + oldName
							+ (newName == null ? ", which is removed. Change it by hand." : ". Change it to " + newName + " by hand."), model.getName(),
							documented.getEntity() == null ? null : documented.getEntity().getName(), documented.getObject());
				}
			}
		}
	}

	/**
	 * Returns a userInfo without the documentation that Entity Modeler keeps
	 * in it, which {@link #documentationThatNames} looks at instead.
	 */
	private static Map<Object, Object> withoutDocumentation(Map<?, ?> userInfo) {
		Map<Object, Object> copy = new LinkedHashMap<>(userInfo);
		Object entityModeler = copy.get(UserInfoableEOModelObject.ENTITY_MODELER_KEY);
		if (entityModeler instanceof Map) {
			Map<Object, Object> rest = new LinkedHashMap<>((Map<?, ?>) entityModeler);
			rest.remove(UserInfoableEOModelObject.DOCUMENTATION_KEY);
			copy.put(UserInfoableEOModelObject.ENTITY_MODELER_KEY, rest);
		}
		return copy;
	}

	private static boolean mentions(Object value, String word) {
		if (value instanceof Map) {
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
				if (mentions(entry.getKey(), word) || mentions(entry.getValue(), word)) {
					return true;
				}
			}
			return false;
		}
		if (value instanceof Collection) {
			for (Object item : (Collection<?>) value) {
				if (mentions(item, word)) {
					return true;
				}
			}
			return false;
		}
		return value instanceof String && containsWord((String) value, word);
	}

	private static boolean containsWord(String text, String word) {
		return text.matches("(?s).*\\b" + java.util.regex.Pattern.quote(word) + "\\b.*");
	}

	/**
	 * Returns an entity and its subentities, which have their own copies of its
	 * properties.
	 */
	private static List<EOEntity> family(EOEntity entity) {
		List<EOEntity> family = new ArrayList<>();
		family.add(entity);
		for (int i = 0; i < family.size(); i++) {
			family.addAll(family.get(i).getChildrenEntities());
		}
		return family;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<EOModelReferenceFailure<?, ?>> references(Set failures) {
		List<EOModelReferenceFailure<?, ?>> sorted = new ArrayList<>((Set<EOModelReferenceFailure<?, ?>>) failures);
		sorted.sort(Comparator.comparing(EOModelReferenceFailure::getMessage));
		return sorted;
	}

	private static List<EOEntity> sortedEntities(EOModel model) {
		List<EOEntity> entities = new ArrayList<>(model.getEntities());
		entities.sort(Comparator.comparing(EOEntity::getName));
		return entities;
	}

	private static String target(Target found) {
		return found.property == null ? found.entity.getName() : found.entity.getName() + "." + found.property.getName();
	}

	/**
	 * What a target names: an entity in one of the project's models, and
	 * perhaps a property of it.
	 */
	private static final class Target {
		private final EOModel model;

		private final EOEntity entity;

		private final IEOAttribute property;

		private Target(EOModel model, EOEntity entity, IEOAttribute property) {
			this.model = model;
			this.entity = entity;
			this.property = property;
		}

		private String modelName() {
			return model.getName();
		}
	}

	private static Target find(EOModelGroup group, Collection<EOModel> projectModels, String target, Change change) {
		String[] parts = target == null ? new String[0] : target.split("\\.", -1);
		if (parts.length < 1 || parts.length > 2 || parts[0].isEmpty() || parts.length == 2 && parts[1].isEmpty()) {
			change.refuse(Finding.NOT_FOUND, target + " isn't an entity, or an entity's property, such as Book or Book.title.", null, null);
			return null;
		}
		EOEntity entity = group.getEntityNamed(parts[0]);
		if (entity == null) {
			change.refuse(Finding.NOT_FOUND, "There is no entity named " + parts[0] + ".", null, null);
			return null;
		}
		if (!projectModels.contains(entity.getModel())) {
			change.refuse(Finding.IN_DEPENDENCY, parts[0] + " is in " + entity.getModel().getName() + ", which isn't one of the project's models, so eogen doesn't change it.",
					entity.getModel().getName(), parts[0]);
			return null;
		}
		IEOAttribute property = null;
		if (parts.length == 2) {
			property = entity.getAttributeOrRelationshipNamed(parts[1]);
			if (property == null) {
				change.refuse(Finding.NOT_FOUND, parts[0] + " has no attribute or relationship named " + parts[1] + ".", entity.getModel().getName(), parts[0]);
				return null;
			}
		}
		return new Target(entity.getModel(), entity, property);
	}
}

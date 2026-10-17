package net.logicsquad.eogen.lint;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.objectstyle.woenvironment.plist.WOLPropertyListSerialization;

import net.logicsquad.eogen.Finding;

/**
 * Finds the mistakes in a model that loading it hides, because the model
 * classes ignore what they don't expect: a misspelt key, such as
 * {@code prototypeNam}, which leaves an attribute without its prototype; a
 * name in {@code classProperties}, {@code primaryKeyAttributes} or
 * {@code attributesUsedForLocking} that matches no attribute or relationship,
 * which silently drops it; and an entity file that {@code index.eomodeld}
 * doesn't list, which the model never loads.
 * <p>
 * The keys it knows are the ones the model classes read and write, together
 * with the ones found in Project Wonder's models and other real models.
 * {@code userInfo} and {@code internalInfo} are free-form, and so are
 * qualifiers, so it doesn't look inside them.
 * </p>
 */
public final class ModelLinter {
	private static final Set<String> INFO_KEYS = keys("internalInfo", "userDictionary", "userInfo");

	private static final Set<String> INDEX_KEYS = keys(INFO_KEYS, "EOModelVersion", "_deletedEntityNamesInObjectStore", "activeDatabaseConfigName", "adaptorName",
			"connectionDictionary", "databaseConfigs", "entities", "entitiesWithSharedObjects", "reverseEngineered", "storedProcedures");

	private static final Set<String> INDEX_ENTITY_KEYS = keys("className", "name", "parent");

	private static final Set<String> ENTITY_KEYS = keys(INFO_KEYS, "_clientClassPropertyNames", "_commonClassPropertyNames", "_javaClientClassName", "attributes",
			"attributesUsedForLocking", "cachesObjects", "cardinality", "className", "classProperties", "entityIndexes", "externalName", "externalQuery",
			"fetchSpecificationDictionary", "isAbstractEntity", "isFetchable", "isImmutable", "isRawRowsOnly", "isReadOnly", "mappingQualifier",
			"maxNumberOfInstancesToBatchFetch", "name", "parent", "primaryKeyAttributes", "relationships", "restrictingQualifier",
			"sharedObjectFetchSpecificationNames", "storedProcedureNames");

	private static final Set<String> ARGUMENT_KEYS = keys(INFO_KEYS, "adaptorValueConversionClassName", "adaptorValueConversionMethodName", "allowsNull", "className",
			"columnName", "definition", "externalName", "externalType", "factoryMethodArgumentType", "maximumLength", "name", "precision", "scale", "serverTimeZone",
			"valueClassName", "valueFactoryClassName", "valueFactoryMethodName", "valueType", "width");

	private static final Set<String> ATTRIBUTE_KEYS = keys(ARGUMENT_KEYS, "insertFormat", "isIndexed", "isReadOnly", "prototypeName", "readFormat", "selectFormat",
			"updateFormat", "writeFormat");

	private static final Set<String> RELATIONSHIP_KEYS = keys(INFO_KEYS, "dataPath", "definition", "deleteRule", "destination", "isMandatory", "isToMany",
			"joinSemantic", "joins", "name", "numberOfToManyFaultsToBatchFetch", "ownsDestination", "propagatesPrimaryKey");

	private static final Set<String> JOIN_KEYS = keys("destinationAttribute", "sourceAttribute");

	private static final Set<String> ENTITY_INDEX_KEYS = keys(INFO_KEYS, "attributes", "constraint", "indexType", "name", "order");

	/**
	 * Includes WOLips's misspelling, {@code prompsAfterFetchLimit}, and EOF's.
	 */
	private static final Set<String> FETCH_SPECIFICATION_KEYS = keys(INFO_KEYS, "EOCustomQueryExpressionHintKey", "EOStoredProcedureNameHintKey", "class",
			"entityName", "fetchLimit", "hints", "isDeep", "locksObjects", "prefetchingRelationshipKeyPaths", "prompsAfterFetchLimit", "promptsAfterFetchLimit",
			"qualifier", "rawRowKeyPaths", "refreshesRefetchedObjects", "requiresAllQualifierBindingVariables", "sortOrderings", "usesDistinct");

	private static final Set<String> SORT_ORDERING_KEYS = keys("class", "key", "selectorName");

	private static final Set<String> STORED_PROCEDURE_KEYS = keys(INFO_KEYS, "arguments", "externalName", "name");

	private static final Set<String> STORED_PROCEDURE_ARGUMENT_KEYS = keys(ARGUMENT_KEYS, "parameterDirection");

	private final File modelFolder;

	private final String modelPath;

	private final List<LintFinding> findings = new ArrayList<>();

	private ModelLinter(File modelFolder) {
		this.modelFolder = modelFolder;
		this.modelPath = modelFolder.getName();
	}

	/**
	 * Lints a model folder.
	 *
	 * @param modelFolder an {@code .eomodeld} folder
	 * @return what is wrong, in file order; files that can't be read are left to
	 *         the model loader to report
	 */
	public static List<LintFinding> lint(File modelFolder) {
		ModelLinter linter = new ModelLinter(modelFolder);
		linter.lint();
		return Collections.unmodifiableList(linter.findings);
	}

	private void lint() {
		File index = new File(modelFolder, "index.eomodeld");
		Map<?, ?> indexMap = map(read(index));
		if (indexMap == null) {
			return;
		}
		unknownKeys(index, null, "the model", indexMap, INDEX_KEYS);
		Set<String> entityNames = new LinkedHashSet<>();
		for (Object entityObject : list(indexMap.get("entities"))) {
			Map<?, ?> entity = map(entityObject);
			if (entity == null) {
				continue;
			}
			String name = string(entity.get("name"));
			unknownKeys(index, name, "entity " + name, entity, INDEX_ENTITY_KEYS);
			if (name != null) {
				entityNames.add(name);
				if (!new File(modelFolder, name + ".plist").isFile()) {
					finding(index, Finding.MISSING_FILE, name, "entity " + name, null, null, "entity " + name + ": there is no " + name + ".plist.");
				}
			}
		}
		Set<String> procedureNames = new LinkedHashSet<>();
		for (Object procedure : list(indexMap.get("storedProcedures"))) {
			String name = string(procedure);
			if (name != null) {
				procedureNames.add(name);
			}
		}

		File[] files = modelFolder.listFiles();
		if (files == null) {
			return;
		}
		Arrays.sort(files);
		for (File file : files) {
			String fileName = file.getName();
			if (!file.isFile()) {
				continue;
			}
			if (fileName.endsWith(".plist")) {
				String name = fileName.substring(0, fileName.length() - ".plist".length());
				if (!entityNames.contains(name)) {
					finding(file, Finding.UNLISTED_FILE, name, "entity " + name, null, null, "entity " + name + ": index.eomodeld doesn't list it, so the model ignores it.");
				} else {
					lintEntity(file, name);
				}
			} else if (fileName.endsWith(".fspec")) {
				String name = fileName.substring(0, fileName.length() - ".fspec".length());
				if (!entityNames.contains(name)) {
					finding(file, Finding.UNLISTED_FILE, name, null, null, null, "there is no entity " + name + " for these fetch specifications, so the model ignores them.");
				} else {
					lintFetchSpecifications(file, name, map(read(file)));
				}
			} else if (fileName.endsWith(".storedProcedure")) {
				String name = fileName.substring(0, fileName.length() - ".storedProcedure".length());
				if (!procedureNames.contains(name)) {
					finding(file, Finding.UNLISTED_FILE, null, "stored procedure " + name, null, null, "stored procedure " + name + ": index.eomodeld doesn't list it, so the model ignores it.");
				} else {
					lintStoredProcedure(file, name);
				}
			}
		}
	}

	private void lintEntity(File file, String fileEntityName) {
		Map<?, ?> entity = map(read(file));
		if (entity == null) {
			return;
		}
		String name = string(entity.get("name"));
		String entityName = name == null ? fileEntityName : name;
		String object = "entity " + entityName;
		if (name != null && !name.equals(fileEntityName)) {
			finding(file, Finding.MISNAMED_ENTITY, name, object, null, null, object + ": index.eomodeld lists it as " + fileEntityName + ".");
		}
		unknownKeys(file, entityName, object, entity, ENTITY_KEYS);

		Set<String> attributes = new LinkedHashSet<>();
		for (Object attributeObject : list(entity.get("attributes"))) {
			Map<?, ?> attribute = map(attributeObject);
			if (attribute != null) {
				String attributeName = string(attribute.get("name"));
				unknownKeys(file, entityName, "attribute " + attributeName, attribute, ATTRIBUTE_KEYS);
				if (attributeName != null) {
					attributes.add(attributeName);
				}
			}
		}
		Set<String> relationships = new LinkedHashSet<>();
		for (Object relationshipObject : list(entity.get("relationships"))) {
			Map<?, ?> relationship = map(relationshipObject);
			if (relationship != null) {
				String relationshipName = string(relationship.get("name"));
				unknownKeys(file, entityName, "relationship " + relationshipName, relationship, RELATIONSHIP_KEYS);
				for (Object join : list(relationship.get("joins"))) {
					Map<?, ?> joinMap = map(join);
					if (joinMap != null) {
						unknownKeys(file, entityName, "a join of relationship " + relationshipName, joinMap, JOIN_KEYS);
					}
				}
				if (relationshipName != null) {
					relationships.add(relationshipName);
				}
			}
		}
		for (Object indexObject : list(entity.get("entityIndexes"))) {
			Map<?, ?> index = map(indexObject);
			if (index != null) {
				unknownKeys(file, entityName, "index " + string(index.get("name")), index, ENTITY_INDEX_KEYS);
			}
		}
		lintFetchSpecifications(file, entityName, map(entity.get("fetchSpecificationDictionary")));

		Set<String> properties = new LinkedHashSet<>(attributes);
		properties.addAll(relationships);
		danglingNames(file, entityName, object, entity, "classProperties", properties, "an attribute or relationship");
		danglingNames(file, entityName, object, entity, "_clientClassPropertyNames", properties, "an attribute or relationship");
		danglingNames(file, entityName, object, entity, "_commonClassPropertyNames", properties, "an attribute or relationship");
		danglingNames(file, entityName, object, entity, "primaryKeyAttributes", attributes, "an attribute");
		danglingNames(file, entityName, object, entity, "attributesUsedForLocking", attributes, "an attribute");
	}

	private void lintFetchSpecifications(File file, String entityName, Map<?, ?> fetchSpecifications) {
		if (fetchSpecifications == null) {
			return;
		}
		for (Map.Entry<?, ?> entry : fetchSpecifications.entrySet()) {
			Map<?, ?> fetchSpecification = map(entry.getValue());
			if (fetchSpecification == null) {
				continue;
			}
			String object = "fetch specification " + entry.getKey();
			unknownKeys(file, entityName, object, fetchSpecification, FETCH_SPECIFICATION_KEYS);
			for (Object ordering : list(fetchSpecification.get("sortOrderings"))) {
				Map<?, ?> orderingMap = map(ordering);
				if (orderingMap != null) {
					unknownKeys(file, entityName, "a sort ordering of " + object, orderingMap, SORT_ORDERING_KEYS);
				}
			}
		}
	}

	private void lintStoredProcedure(File file, String fileName) {
		Map<?, ?> procedure = map(read(file));
		if (procedure == null) {
			return;
		}
		String name = string(procedure.get("name"));
		unknownKeys(file, null, "stored procedure " + (name == null ? fileName : name), procedure, STORED_PROCEDURE_KEYS);
		for (Object argumentObject : list(procedure.get("arguments"))) {
			Map<?, ?> argument = map(argumentObject);
			if (argument != null) {
				unknownKeys(file, null, "argument " + string(argument.get("name")), argument, STORED_PROCEDURE_ARGUMENT_KEYS);
			}
		}
	}

	private void unknownKeys(File file, String entity, String object, Map<?, ?> map, Set<String> known) {
		for (Object keyObject : map.keySet()) {
			String key = String.valueOf(keyObject);
			if (!known.contains(key)) {
				String suggestion = suggest(key, known);
				finding(file, Finding.UNKNOWN_KEY, entity, object, key, suggestion, object + ": unknown key " + key + (suggestion == null ? "." : " (did you mean " + suggestion + "?)."));
			}
		}
	}

	private void danglingNames(File file, String entityName, String object, Map<?, ?> entity, String listKey, Set<String> names, String what) {
		for (Object nameObject : list(entity.get(listKey))) {
			String name = string(nameObject);
			if (name != null && !names.contains(name)) {
				String suggestion = suggest(name, names);
				finding(file, Finding.UNMATCHED_NAME, entityName, object, name, suggestion, object + ": " + listKey + " names " + name + ", which is not " + what + (suggestion == null ? "." : " (did you mean " + suggestion + "?)."));
			}
		}
	}

	private void finding(File file, String code, String entity, String object, String key, String suggestion, String message) {
		String path = file.equals(modelFolder) ? modelPath : modelPath + "/" + file.getName();
		findings.add(new LintFinding(file, path, code, entity, object, key, suggestion, message));
	}

	/**
	 * Returns the candidate a misspelling most likely meant: one that differs
	 * only in case, or else the closest within two edits, or null.
	 */
	static String suggest(String word, Collection<String> candidates) {
		for (String candidate : candidates) {
			if (candidate.equalsIgnoreCase(word)) {
				return candidate;
			}
		}
		String best = null;
		int bestDistance = 3;
		for (String candidate : new TreeSet<>(candidates)) {
			int distance = distance(word.toLowerCase(Locale.ROOT), candidate.toLowerCase(Locale.ROOT));
			if (distance < bestDistance) {
				best = candidate;
				bestDistance = distance;
			}
		}
		return best;
	}

	/**
	 * Returns the Levenshtein distance between two strings.
	 */
	static int distance(String a, String b) {
		int[] previous = new int[b.length() + 1];
		int[] current = new int[b.length() + 1];
		for (int j = 0; j <= b.length(); j++) {
			previous[j] = j;
		}
		for (int i = 1; i <= a.length(); i++) {
			current[0] = i;
			for (int j = 1; j <= b.length(); j++) {
				int substitution = previous[j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1);
				current[j] = Math.min(substitution, Math.min(previous[j], current[j - 1]) + 1);
			}
			int[] swap = previous;
			previous = current;
			current = swap;
		}
		return previous[b.length()];
	}

	private static Object read(File file) {
		if (!file.isFile()) {
			return null;
		}
		try {
			return WOLPropertyListSerialization.propertyListFromFile(file);
		} catch (Exception e) {
			return null;
		}
	}

	private static Map<?, ?> map(Object object) {
		return object instanceof Map ? (Map<?, ?>) object : null;
	}

	private static List<?> list(Object object) {
		return object instanceof List ? (List<?>) object : Collections.emptyList();
	}

	private static String string(Object object) {
		return object instanceof String ? (String) object : null;
	}

	private static Set<String> keys(String... keys) {
		return Collections.unmodifiableSet(new TreeSet<>(Arrays.asList(keys)));
	}

	private static Set<String> keys(Set<String> base, String... keys) {
		Set<String> all = new TreeSet<>(base);
		all.addAll(Arrays.asList(keys));
		return Collections.unmodifiableSet(all);
	}
}

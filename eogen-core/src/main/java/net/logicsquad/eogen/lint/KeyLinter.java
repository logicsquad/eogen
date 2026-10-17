package net.logicsquad.eogen.lint;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.logicsquad.eogen.Finding;
import net.logicsquad.eogen.model.EOAttribute;
import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOFetchSpecification;
import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EOQualifierFactory;
import net.logicsquad.eogen.model.EORelationship;
import net.logicsquad.eogen.model.EOSortOrdering;
import net.logicsquad.eogen.model.IEOAttribute;
import net.logicsquad.eogen.model.qualifier.EOQualifier;

/**
 * Finds the keys in a model's fetch specifications and restricting qualifiers
 * that match nothing. They name keys as text, in qualifiers, sort orderings,
 * and prefetching and raw-row key paths, so after a rename or a typo one can
 * name a key that doesn't exist, and nothing says so until the fetch runs.
 * WOLips's verifier works out the keys of a fetch specification's qualifier,
 * but doesn't report them.
 * <p>
 * Unlike {@link ModelLinter}, which reads a model's files, this needs the
 * loaded and resolved model group, since a key path can lead through
 * relationships into the group's other models. A qualifier's variables, such
 * as {@code $username}, are values, not keys, so they aren't checked.
 */
public final class KeyLinter {
	private final EOModel model;

	private final File modelFolder;

	private final List<LintFinding> findings = new ArrayList<>();

	private KeyLinter(EOModel model, File modelFolder) {
		this.model = model;
		this.modelFolder = modelFolder;
	}

	/**
	 * Lints the keys of a model's fetch specifications and restricting
	 * qualifiers.
	 *
	 * @param model a model in a loaded and resolved model group
	 * @param modelFolder the model's {@code .eomodeld} folder, which the
	 *        findings name
	 * @return the keys that match nothing, by entity
	 */
	public static List<LintFinding> lint(EOModel model, File modelFolder) {
		KeyLinter linter = new KeyLinter(model, modelFolder);
		linter.lint();
		return Collections.unmodifiableList(linter.findings);
	}

	private void lint() {
		List<EOEntity> entities = new ArrayList<>(model.getEntities());
		entities.sort(Comparator.comparing(EOEntity::getName));
		for (EOEntity entity : entities) {
			File entityFile = new File(modelFolder, entity.getName() + ".plist");
			String restrictingQualifier = entity.getRestrictingQualifier();
			if (restrictingQualifier != null && !restrictingQualifier.trim().isEmpty()) {
				check(entityFile, "entity " + entity.getName(), "restrictingQualifier key", entity, keys(restrictingQualifier));
			}
			File fetchSpecificationFile = new File(modelFolder, entity.getName() + ".fspec");
			File file = fetchSpecificationFile.isFile() ? fetchSpecificationFile : entityFile;
			List<EOFetchSpecification> fetchSpecifications = new ArrayList<>(entity.getFetchSpecs());
			fetchSpecifications.sort(Comparator.comparing(EOFetchSpecification::getName));
			for (EOFetchSpecification fetchSpecification : fetchSpecifications) {
				String object = "fetch specification " + fetchSpecification.getName();
				check(file, object, "qualifier key", entity, keys(fetchSpecification.getQualifier()));
				List<String> sortKeys = new ArrayList<>();
				if (fetchSpecification.getSortOrderings() != null) {
					for (EOSortOrdering ordering : fetchSpecification.getSortOrderings()) {
						sortKeys.add(ordering.getKey());
					}
				}
				check(file, object, "sort key", entity, sortKeys);
				check(file, object, "prefetching key path", entity, fetchSpecification.getPrefetchingRelationshipKeyPaths());
				check(file, object, "raw-row key path", entity, fetchSpecification.getRawRowKeyPaths());
			}
		}
	}

	private void check(File file, String object, String kind, EOEntity entity, Collection<String> keys) {
		if (keys == null) {
			return;
		}
		for (String key : keys) {
			// A key with an operator, such as @count, isn't a plain key path.
			if (key == null || key.isEmpty() || key.indexOf('@') >= 0) {
				continue;
			}
			Problem problem = problem(entity, key);
			if (problem != null) {
				String path = file.equals(modelFolder) ? modelFolder.getName() : modelFolder.getName() + "/" + file.getName();
				findings.add(new LintFinding(file, path, Finding.UNMATCHED_KEY, entity.getName(), object, key, problem.suggestion,
						object + ": " + kind + " " + key + problem.text));
			}
		}
	}

	/**
	 * Follows a key path from an entity, through its relationships.
	 *
	 * @return what is wrong with the key path, or null if it leads to an
	 *         attribute or a relationship
	 */
	private static Problem problem(EOEntity entity, String keyPath) {
		String[] names = keyPath.split("\\.", -1);
		EOEntity current = entity;
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			IEOAttribute property = current.getAttributeOrRelationshipNamed(name);
			if (property == null) {
				Set<String> candidates = new TreeSet<>();
				for (EOAttribute attribute : current.getAttributes()) {
					candidates.add(attribute.getName());
				}
				for (EORelationship relationship : current.getRelationships()) {
					candidates.add(relationship.getName());
				}
				String where = names.length == 1 ? " matches nothing in " + current.getName() : " matches nothing: " + current.getName() + " has no " + name;
				String suggestion = name.isEmpty() ? null : ModelLinter.suggest(name, candidates);
				if (suggestion == null) {
					return new Problem(where + ".", null);
				}
				names[i] = suggestion;
				String suggested = String.join(".", names);
				return new Problem(where + " (did you mean " + suggested + "?).", suggested);
			}
			if (i < names.length - 1) {
				if (!(property instanceof EORelationship)) {
					return new Problem(" matches nothing: " + name + " is an attribute of " + current.getName() + ", not a relationship.", null);
				}
				EOEntity destination = ((EORelationship) property).getDestination();
				if (destination == null) {
					// The verifier reports a relationship without a destination.
					return null;
				}
				current = destination;
			}
		}
		return null;
	}

	private static Collection<String> keys(EOQualifier qualifier) {
		Set<String> keys = new LinkedHashSet<>();
		if (qualifier != null) {
			try {
				EOQualifierFactory.fillInQualifierKeysFromQualifier(qualifier, keys);
			} catch (IllegalArgumentException e) {
				// A kind of qualifier that eogen can't take apart.
			}
		}
		return keys;
	}

	private static Collection<String> keys(String qualifier) {
		try {
			return keys(EOQualifierFactory.fromString(qualifier));
		} catch (RuntimeException e) {
			// The verifier reports a qualifier that doesn't parse.
			return Collections.emptySet();
		}
	}

	/**
	 * What is wrong with a key path: the text that follows it in a message, and
	 * the key path most likely meant, or null.
	 */
	private static final class Problem {
		private final String text;

		private final String suggestion;

		private Problem(String text, String suggestion) {
			this.text = text;
			this.suggestion = suggestion;
		}
	}
}

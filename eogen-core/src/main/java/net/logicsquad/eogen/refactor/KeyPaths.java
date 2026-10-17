package net.logicsquad.eogen.refactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOFetchSpecification;
import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EOQualifierFactory;
import net.logicsquad.eogen.model.EORelationship;
import net.logicsquad.eogen.model.EOSortOrdering;
import net.logicsquad.eogen.model.IEOAttribute;
import net.logicsquad.eogen.model.qualifier.EOAndQualifier;
import net.logicsquad.eogen.model.qualifier.EOKeyComparisonQualifier;
import net.logicsquad.eogen.model.qualifier.EOKeyValueQualifier;
import net.logicsquad.eogen.model.qualifier.EONotQualifier;
import net.logicsquad.eogen.model.qualifier.EOOrQualifier;
import net.logicsquad.eogen.model.qualifier.EOQualifier;

/**
 * The key paths that models name as text, in fetch specifications'
 * qualifiers, sort orderings, and prefetching and raw-row key paths, and in
 * entities' restricting qualifiers. A rename rewrites the ones that pass
 * through what it renames, and a removal refuses while any passes through
 * what it removes.
 * <p>
 * A key path passes through a property when one of its steps is the
 * property's name on its entity, or on a subentity of it, which has its own
 * copy.
 * </p>
 */
final class KeyPaths {
	private KeyPaths() {
	}

	/**
	 * A key path, and where a model has it.
	 */
	static final class Use {
		private final EOEntity entity;

		private final String object;

		private final String kind;

		private final String keyPath;

		private Use(EOEntity entity, String object, String kind, String keyPath) {
			this.entity = entity;
			this.object = object;
			this.kind = kind;
			this.keyPath = keyPath;
		}

		/**
		 * Returns the entity whose fetch specification or restricting qualifier
		 * has the key path.
		 */
		EOEntity getEntity() {
			return entity;
		}

		/**
		 * Returns what has the key path, as messages name it, such as
		 * {@code fetch specification byTitle}.
		 */
		String getObject() {
			return object;
		}

		/**
		 * Returns the key path.
		 */
		String getKeyPath() {
			return keyPath;
		}

		/**
		 * Describes where the key path is, such as {@code Movie's fetch
		 * specification byTitle: sort key title}.
		 */
		String describe() {
			return entity.getName() + "'s " + object + ": " + kind + " " + keyPath;
		}
	}

	/**
	 * Returns the key paths in models that pass through a property.
	 *
	 * @param models the models to look in
	 * @param entity the property's entity
	 * @param name the property's name
	 * @return where they are
	 */
	static List<Use> uses(Collection<EOModel> models, EOEntity entity, String name) {
		List<Use> uses = new ArrayList<>();
		visit(models, (start, object, kind, keyPath) -> {
			if (!steps(start, keyPath, entity, name).isEmpty()) {
				uses.add(new Use(start, object, kind, keyPath));
			}
			return keyPath;
		});
		return uses;
	}

	/**
	 * Rewrites the key paths in models that pass through a property, for its
	 * new name. Call it before renaming the property, while the key paths
	 * still resolve.
	 *
	 * @param models the models to rewrite
	 * @param entity the property's entity
	 * @param name the property's name
	 * @param newName its new name
	 * @return the qualifiers that couldn't be rewritten, since eogen can't take
	 *         them apart
	 */
	static List<Use> rename(Collection<EOModel> models, EOEntity entity, String name, String newName) {
		List<Use> unwritten = new ArrayList<>();
		// Work out every new key path first, while the old ones resolve, then
		// apply them.
		List<Runnable> changes = new ArrayList<>();
		for (EOModel model : sorted(models)) {
			for (EOEntity start : sortedEntities(model)) {
				String restrictingQualifier = start.getRestrictingQualifier();
				if (restrictingQualifier != null && !restrictingQualifier.trim().isEmpty()) {
					EOQualifier qualifier;
					try {
						qualifier = EOQualifierFactory.fromString(restrictingQualifier);
					} catch (RuntimeException e) {
						qualifier = null;
					}
					if (qualifier != null) {
						EOQualifier rewritten = rebuild(qualifier, key -> rewrite(start, key, entity, name, newName));
						if (rewritten == null) {
							unwritten.add(new Use(start, "entity " + start.getName(), "restrictingQualifier", restrictingQualifier));
						} else if (rewritten != qualifier) {
							changes.add(() -> start.setRestrictingQualifier(EOQualifierFactory.toString(rewritten)));
						}
					}
				}
				for (EOFetchSpecification fetchSpecification : sortedFetchSpecifications(start)) {
					String object = "fetch specification " + fetchSpecification.getName();
					EOQualifier qualifier = fetchSpecification.getQualifier();
					if (qualifier != null) {
						EOQualifier rewritten = rebuild(qualifier, key -> rewrite(start, key, entity, name, newName));
						if (rewritten == null) {
							unwritten.add(new Use(start, object, "qualifier", EOQualifierFactory.toString(qualifier)));
						} else if (rewritten != qualifier) {
							changes.add(() -> fetchSpecification.setQualifier(rewritten));
						}
					}
					if (fetchSpecification.getSortOrderings() != null) {
						for (EOSortOrdering ordering : fetchSpecification.getSortOrderings()) {
							String key = ordering.getKey();
							String rewritten = rewrite(start, key, entity, name, newName);
							if (!rewritten.equals(key)) {
								changes.add(() -> ordering.setKey(rewritten));
							}
						}
					}
					for (String keyPath : copy(fetchSpecification.getPrefetchingRelationshipKeyPaths())) {
						String rewritten = rewrite(start, keyPath, entity, name, newName);
						if (!rewritten.equals(keyPath)) {
							changes.add(() -> {
								fetchSpecification.removePrefetchingRelationshipKeyPath(keyPath, true);
								fetchSpecification.addPrefetchingRelationshipKeyPath(rewritten, true);
							});
						}
					}
					for (String keyPath : copy(fetchSpecification.getRawRowKeyPaths())) {
						String rewritten = rewrite(start, keyPath, entity, name, newName);
						if (!rewritten.equals(keyPath)) {
							changes.add(() -> {
								fetchSpecification.removeRawRowKeyPath(keyPath, true);
								fetchSpecification.addRawRowKeyPath(rewritten, true);
							});
						}
					}
				}
			}
		}
		for (Runnable change : changes) {
			change.run();
		}
		return unwritten;
	}

	/**
	 * Something that sees each key path, and returns what it should be.
	 */
	private interface Visitor {
		String visit(EOEntity start, String object, String kind, String keyPath);
	}

	private static void visit(Collection<EOModel> models, Visitor visitor) {
		for (EOModel model : sorted(models)) {
			for (EOEntity start : sortedEntities(model)) {
				String restrictingQualifier = start.getRestrictingQualifier();
				if (restrictingQualifier != null && !restrictingQualifier.trim().isEmpty()) {
					for (String key : keys(restrictingQualifier)) {
						visitor.visit(start, "entity " + start.getName(), "restrictingQualifier key", key);
					}
				}
				for (EOFetchSpecification fetchSpecification : sortedFetchSpecifications(start)) {
					String object = "fetch specification " + fetchSpecification.getName();
					for (String key : keys(fetchSpecification.getQualifier())) {
						visitor.visit(start, object, "qualifier key", key);
					}
					if (fetchSpecification.getSortOrderings() != null) {
						for (EOSortOrdering ordering : fetchSpecification.getSortOrderings()) {
							visitor.visit(start, object, "sort key", ordering.getKey());
						}
					}
					for (String keyPath : copy(fetchSpecification.getPrefetchingRelationshipKeyPaths())) {
						visitor.visit(start, object, "prefetching key path", keyPath);
					}
					for (String keyPath : copy(fetchSpecification.getRawRowKeyPaths())) {
						visitor.visit(start, object, "raw-row key path", keyPath);
					}
				}
			}
		}
	}

	/**
	 * Returns the indexes of a key path's steps that are a property.
	 */
	private static List<Integer> steps(EOEntity start, String keyPath, EOEntity entity, String name) {
		List<Integer> steps = new ArrayList<>();
		if (keyPath == null) {
			return steps;
		}
		String[] names = keyPath.split("\\.", -1);
		EOEntity current = start;
		for (int i = 0; i < names.length && current != null; i++) {
			if (names[i].equals(name) && isOrInherits(current, entity)) {
				steps.add(i);
			}
			IEOAttribute property = current.getAttributeOrRelationshipNamed(names[i]);
			current = property instanceof EORelationship ? ((EORelationship) property).getDestination() : null;
		}
		return steps;
	}

	private static String rewrite(EOEntity start, String keyPath, EOEntity entity, String name, String newName) {
		List<Integer> steps = steps(start, keyPath, entity, name);
		if (steps.isEmpty()) {
			return keyPath;
		}
		String[] names = keyPath.split("\\.", -1);
		for (int step : steps) {
			names[step] = newName;
		}
		return String.join(".", names);
	}

	/**
	 * Returns whether an entity is another, or one of its subentities.
	 */
	static boolean isOrInherits(EOEntity entity, EOEntity ancestor) {
		for (EOEntity current = entity; current != null; current = current.getParent()) {
			if (current == ancestor) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Rebuilds a qualifier with its keys rewritten.
	 *
	 * @return the qualifier itself if no key changed, a new one if any did, or
	 *         null if it has a kind of qualifier that eogen can't take apart
	 */
	private static EOQualifier rebuild(EOQualifier qualifier, UnaryOperator<String> keys) {
		if (qualifier instanceof EOKeyValueQualifier) {
			EOKeyValueQualifier keyValue = (EOKeyValueQualifier) qualifier;
			String key = keys.apply(keyValue.getKey());
			return key.equals(keyValue.getKey()) ? qualifier : new EOKeyValueQualifier(key, keyValue.getComparison(), keyValue.getValue());
		}
		if (qualifier instanceof EOKeyComparisonQualifier) {
			EOKeyComparisonQualifier comparison = (EOKeyComparisonQualifier) qualifier;
			String left = keys.apply(comparison.getLeftKey());
			String right = keys.apply(comparison.getRightKey());
			return left.equals(comparison.getLeftKey()) && right.equals(comparison.getRightKey()) ? qualifier
					: new EOKeyComparisonQualifier(left, comparison.getComparison(), right);
		}
		if (qualifier instanceof EOAndQualifier || qualifier instanceof EOOrQualifier) {
			Collection<EOQualifier> children = qualifier instanceof EOAndQualifier ? ((EOAndQualifier) qualifier).getQualifiers() : ((EOOrQualifier) qualifier).getQualifiers();
			List<EOQualifier> rebuilt = new ArrayList<>();
			boolean changed = false;
			for (EOQualifier child : children) {
				EOQualifier rebuiltChild = rebuild(child, keys);
				if (rebuiltChild == null) {
					return null;
				}
				changed |= rebuiltChild != child;
				rebuilt.add(rebuiltChild);
			}
			if (!changed) {
				return qualifier;
			}
			return qualifier instanceof EOAndQualifier ? new EOAndQualifier(rebuilt) : new EOOrQualifier(rebuilt);
		}
		if (qualifier instanceof EONotQualifier) {
			EOQualifier child = ((EONotQualifier) qualifier).getQualifier();
			EOQualifier rebuiltChild = rebuild(child, keys);
			return rebuiltChild == null ? null : rebuiltChild == child ? qualifier : new EONotQualifier(rebuiltChild);
		}
		return null;
	}

	private static Set<String> keys(EOQualifier qualifier) {
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

	private static Set<String> keys(String qualifier) {
		try {
			return keys(EOQualifierFactory.fromString(qualifier));
		} catch (RuntimeException e) {
			return new LinkedHashSet<>();
		}
	}

	private static List<String> copy(Collection<String> keyPaths) {
		return keyPaths == null ? new ArrayList<>() : new ArrayList<>(keyPaths);
	}

	private static List<EOModel> sorted(Collection<EOModel> models) {
		List<EOModel> sorted = new ArrayList<>(models);
		sorted.sort(Comparator.comparing(EOModel::getName));
		return sorted;
	}

	private static List<EOEntity> sortedEntities(EOModel model) {
		List<EOEntity> entities = new ArrayList<>(model.getEntities());
		entities.sort(Comparator.comparing(EOEntity::getName));
		return entities;
	}

	private static List<EOFetchSpecification> sortedFetchSpecifications(EOEntity entity) {
		List<EOFetchSpecification> fetchSpecifications = new ArrayList<>(entity.getFetchSpecs());
		fetchSpecifications.sort(Comparator.comparing(EOFetchSpecification::getName));
		return fetchSpecifications;
	}
}

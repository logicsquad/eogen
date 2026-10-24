package net.logicsquad.eogen.doc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import net.logicsquad.eogen.model.EOAttribute;
import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOJoin;
import net.logicsquad.eogen.model.EORelationship;

/**
 * Writes a Mermaid {@code erDiagram} of a model's entities and their
 * relationships, as {@code $diagram} in the Markdown page's template, or of an
 * entity and the entities that its relationships, and theirs, reach directly,
 * as {@code $entityDiagrams}.
 * <p>
 * Each entity is declared, in name order, so that one with no relationships
 * still shows. Then each relationship is a line, with its inverse if it has
 * one, in the order of its entity's name and its own. A one-to-many is drawn
 * from its one side. Each end says how many of its entity the other end
 * reaches: one or none, exactly one, any number, or at least one, from the
 * relationship that points at it. Without an inverse, the near end comes from
 * the join: a to-one's source has any number of rows for each destination,
 * unless it joins on its primary key, and a to-many's destination rows each
 * have one source, or at most one if their foreign key allows null. Flattened
 * and inherited relationships aren't drawn, nor are attributes, so that a
 * large model stays legible. An entity in another model that a relationship
 * reaches is in the diagram too, as its relationships' lines declare it.
 * </p>
 */
final class MermaidDiagram {
	private MermaidDiagram() {
	}

	/**
	 * Returns the diagram's lines, from {@code erDiagram}, each after a line
	 * end but the first.
	 *
	 * @param entities the model's entities, in name order
	 * @return the diagram
	 */
	static String erDiagram(List<EOEntity> entities) {
		List<EORelationship> relationships = new ArrayList<>();
		for (EOEntity entity : entities) {
			relationships.addAll(entity.getSortedRelationships());
		}
		return erDiagram(entities, relationships);
	}

	/**
	 * Returns the diagram of an entity: its relationships, and those of the
	 * other entities that reach it, with the entities at their other ends.
	 *
	 * @param entity an entity
	 * @param entities the entities whose relationships to it are drawn, in
	 *        name order
	 * @return the diagram, or null if it has no lines
	 */
	static String erDiagram(EOEntity entity, List<EOEntity> entities) {
		List<EORelationship> relationships = new ArrayList<>();
		for (EOEntity other : entities) {
			for (EORelationship relationship : other.getSortedRelationships()) {
				if (drawable(relationship) && (other == entity || relationship.getDestination() == entity)) {
					relationships.add(relationship);
				}
			}
		}
		return relationships.isEmpty() ? null : erDiagram(Collections.singletonList(entity), relationships);
	}

	/**
	 * Returns the diagram of the relationships, in their order, after
	 * declaring the entities.
	 */
	private static String erDiagram(List<EOEntity> entities, List<EORelationship> relationships) {
		List<String> lines = new ArrayList<>();
		lines.add("erDiagram");
		for (EOEntity entity : entities) {
			lines.add("    " + entity.getName());
		}
		Set<EORelationship> drawn = Collections.newSetFromMap(new IdentityHashMap<>());
		for (EORelationship relationship : relationships) {
			if (!drawable(relationship) || drawn.contains(relationship)) {
				continue;
			}
			EORelationship inverse = relationship.getInverseRelationship();
			if (inverse != null && !drawable(inverse)) {
				inverse = null;
			}
			drawn.add(relationship);
			if (inverse != null) {
				drawn.add(inverse);
				if (!isToMany(relationship) && isToMany(inverse)) {
					EORelationship swap = relationship;
					relationship = inverse;
					inverse = swap;
				}
			}
			String near = inverse == null ? inferredEnd(relationship) : leftEnd(inverse);
			String label = relationship.getName() + (inverse == null ? "" : " / " + inverse.getName());
			lines.add("    " + relationship.getEntity().getName() + " " + near + "--" + rightEnd(relationship) + " " + relationship.getDestination().getName() + " : \"" + label
					+ "\"");
		}
		return String.join("\n", lines);
	}

	private static boolean drawable(EORelationship relationship) {
		return !relationship.isFlattened() && !relationship.isInherited() && relationship.getEntity() != null && relationship.getDestination() != null;
	}

	/**
	 * Returns the end at a relationship's destination, for how many of its
	 * destination the relationship reaches.
	 */
	private static String rightEnd(EORelationship relationship) {
		if (isToMany(relationship)) {
			return isMandatory(relationship) ? "|{" : "o{";
		}
		return isMandatory(relationship) ? "||" : "o|";
	}

	/**
	 * Returns the end at a relationship's destination, as the left end of a
	 * line drawn from the other side.
	 */
	private static String leftEnd(EORelationship relationship) {
		if (isToMany(relationship)) {
			return isMandatory(relationship) ? "}|" : "}o";
		}
		return isMandatory(relationship) ? "||" : "|o";
	}

	/**
	 * Returns the end at a relationship's source, when nothing points back at
	 * it, from its join.
	 */
	private static String inferredEnd(EORelationship relationship) {
		List<EOJoin> joins = relationship.getJoins();
		if (isToMany(relationship)) {
			for (EOJoin join : joins) {
				EOAttribute foreignKey = join.getDestinationAttribute();
				if (foreignKey == null || !Boolean.FALSE.equals(foreignKey.isAllowsNull())) {
					return "|o";
				}
			}
			return joins.isEmpty() ? "|o" : "||";
		}
		for (EOJoin join : joins) {
			EOAttribute source = join.getSourceAttribute();
			if (source == null || !Boolean.TRUE.equals(source.isPrimaryKey())) {
				return "}o";
			}
		}
		return joins.isEmpty() ? "}o" : "|o";
	}

	private static boolean isToMany(EORelationship relationship) {
		return Boolean.TRUE.equals(relationship.isToMany());
	}

	private static boolean isMandatory(EORelationship relationship) {
		return Boolean.TRUE.equals(relationship.isMandatory());
	}
}

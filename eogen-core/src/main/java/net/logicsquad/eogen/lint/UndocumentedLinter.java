package net.logicsquad.eogen.lint;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.logicsquad.eogen.Finding;
import net.logicsquad.eogen.model.EOAttribute;
import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EORelationship;
import net.logicsquad.eogen.model.UserInfoableEOModelObject;

/**
 * Finds what a model leaves undocumented, for a team that wants everything
 * documented: the model, each entity other than a prototype, and each
 * attribute and relationship that is a class property, with no documentation
 * in Entity Modeler's {@code documentation}. A subentity's inherited
 * properties are its parent's to document. An attribute that isn't a class
 * property, such as a primary or foreign key, isn't in the generated classes,
 * so it isn't reported.
 * <p>
 * Like {@link KeyLinter}, this needs the loaded and resolved model group,
 * which knows what a subentity inherits.
 * </p>
 */
public final class UndocumentedLinter {
	private UndocumentedLinter() {
	}

	/**
	 * Lints a model for what it leaves undocumented.
	 *
	 * @param model a model in a loaded and resolved model group
	 * @param modelFolder the model's {@code .eomodeld} folder, which the
	 *        findings name
	 * @return what has no documentation: the model, then each entity and its
	 *         attributes and relationships, in name order
	 */
	public static List<LintFinding> lint(EOModel model, File modelFolder) {
		List<LintFinding> findings = new ArrayList<>();
		check(findings, modelFolder, new File(modelFolder, "index.eomodeld"), null, "the model", model);
		List<EOEntity> entities = new ArrayList<>(model.getEntities());
		entities.sort(Comparator.comparing(EOEntity::getName));
		for (EOEntity entity : entities) {
			if (entity.isPrototype()) {
				continue;
			}
			File file = new File(modelFolder, entity.getName() + ".plist");
			check(findings, modelFolder, file, entity, "entity " + entity.getName(), entity);
			for (EOAttribute attribute : entity.getSortedAttributes()) {
				if (!attribute.isInherited() && Boolean.TRUE.equals(attribute.isClassProperty())) {
					check(findings, modelFolder, file, entity, "attribute " + attribute.getName(), attribute);
				}
			}
			for (EORelationship relationship : entity.getSortedRelationships()) {
				if (!relationship.isInherited() && Boolean.TRUE.equals(relationship.isClassProperty())) {
					check(findings, modelFolder, file, entity, "relationship " + relationship.getName(), relationship);
				}
			}
		}
		return Collections.unmodifiableList(findings);
	}

	private static void check(List<LintFinding> findings, File modelFolder, File file, EOEntity entity, String object, UserInfoableEOModelObject<?> documented) {
		String documentation = documented.getDocumentation();
		if (documentation == null || documentation.trim().isEmpty()) {
			findings.add(new LintFinding(file, modelFolder.getName() + "/" + file.getName(), Finding.UNDOCUMENTED, entity == null ? null : entity.getName(), object,
					null, null, object + ": no documentation."));
		}
	}
}

package net.logicsquad.eogen.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import net.logicsquad.eogen.EOGenerator;
import net.logicsquad.eogen.EOGeneratorException;

/**
 * Renames an entity, attribute or relationship in the project's models,
 * through Entity Modeler's model classes, which carry the rename into
 * subentities, flattened definitions, joins, entity indexes and the property
 * lists, and rewrites the keys that fetch specifications and restricting
 * qualifiers name as text. It renames names only: columns and tables stay as
 * they are, and an entity's class changes only with {@code renameClass}. It
 * writes only the model files that the rename changes, and reports what is
 * left to do by hand, such as renaming a subclass.
 */
@Mojo(name = "rename", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class RenameMojo extends AbstractRefactorMojo {
	/**
	 * What to rename: an entity, such as {@code Book}, or an attribute or
	 * relationship, such as {@code Book.titel}.
	 */
	@Parameter(property = "eogen.rename", required = true)
	private String rename;

	/**
	 * The new name.
	 */
	@Parameter(property = "eogen.to", required = true)
	private String to;

	/**
	 * For an entity, renames its class too, when the class's simple name is the
	 * entity's.
	 */
	@Parameter(property = "eogen.renameClass", defaultValue = "false")
	private boolean renameClass;

	@Override
	protected void execute(EOGenerator generator) throws MojoExecutionException, MojoFailureException {
		try {
			report(generator.rename(rename, to, renameClass, !isDryRun()), "Renaming " + rename + " to " + to);
		} catch (EOGeneratorException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}

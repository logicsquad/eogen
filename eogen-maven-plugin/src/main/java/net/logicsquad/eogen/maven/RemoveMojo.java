package net.logicsquad.eogen.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import net.logicsquad.eogen.EOGenerator;
import net.logicsquad.eogen.EOGeneratorException;

/**
 * Removes an entity, attribute or relationship from the project's models,
 * unless anything else uses it: a join, a flattened definition, an index, a
 * relationship to an entity, a subentity, or a key in a fetch specification or
 * restricting qualifier. Then it names each use, and removes nothing, so that
 * the uses can be removed first. It writes only the model files that the
 * removal changes, and reports what is left to do by hand.
 */
@Mojo(name = "remove", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class RemoveMojo extends AbstractRefactorMojo {
	/**
	 * What to remove: an entity, such as {@code Book}, or an attribute or
	 * relationship, such as {@code Book.notes}.
	 */
	@Parameter(property = "eogen.remove", required = true)
	private String remove;

	@Override
	protected void execute(EOGenerator generator) throws MojoExecutionException, MojoFailureException {
		try {
			report(generator.remove(remove, !isDryRun()), "Removing " + remove);
		} catch (EOGeneratorException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}

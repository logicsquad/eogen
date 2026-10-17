package net.logicsquad.eogen.maven;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import net.logicsquad.eogen.EOGenerator;
import net.logicsquad.eogen.EOGeneratorException;
import net.logicsquad.eogen.Finding;
import net.logicsquad.eogen.doc.DocOptions;
import net.logicsquad.eogen.doc.DocResult;

/**
 * Writes EOModelDoc's HTML pages for the project's models, as WOLips's
 * EOModelDoc did: an index of every model and entity, and a page for each
 * model, entity and stored procedure, with the documentation that Entity
 * Modeler keeps for them. By default they go in the project's site, which
 * {@code pre-site} writes them into before the site is built. Nothing is
 * written if the model group or a template has errors, unless forced.
 */
@Mojo(name = "doc", defaultPhase = LifecyclePhase.PRE_SITE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class DocMojo extends AbstractEOGenMojo {
	/**
	 * The folder of the pages.
	 */
	@Parameter(property = "eogen.doc.output", defaultValue = "${project.build.directory}/site/eomodeldoc")
	private File outputDirectory;

	/**
	 * A folder of templates, such as {@code entityContent.html.vm}, and of the
	 * files that are copied, such as {@code eomodeldoc.css}, which come before
	 * the bundled ones.
	 */
	@Parameter(property = "eogen.doc.templates")
	private File templateDirectory;

	/**
	 * A Velocity template for the link from each entity's class name, such as
	 * {@code ../../../apidocs/${entity.className.replace(".", "/")}.html},
	 * relative to the entity's page. It sees {@code $entity} and
	 * {@code $model}.
	 */
	@Parameter(property = "eogen.doc.entityURL")
	private String entityURL;

	/**
	 * Documents the models of the dependencies as well as the project's own.
	 */
	@Parameter(property = "eogen.doc.includeDependencies", defaultValue = "false")
	private boolean includeDependencies;

	@Override
	protected void execute(EOGenerator generator) throws MojoExecutionException, MojoFailureException {
		DocOptions options = new DocOptions();
		options.setOutput(outputDirectory);
		options.setTemplateFolder(templateDirectory);
		options.setEntityURL(entityURL);
		options.setIncludeDependencies(includeDependencies);
		DocResult result;
		try {
			result = generator.generateDoc(options);
		} catch (EOGeneratorException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		for (Finding finding : result.getFindings()) {
			if (isForce()) {
				getLog().warn(finding.getMessage());
			} else {
				getLog().error(finding.getMessage());
			}
		}
		if (!result.isWritten()) {
			throw new MojoFailureException("There are errors, so no page was written. Fix them, or set eogen.force to write anyway.");
		}
		getLog().info("Documented " + String.join(", ", result.getModels()) + " in " + relative(outputDirectory) + ": " + result.getFiles().size() + " files.");
	}
}

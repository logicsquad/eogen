package net.logicsquad.eogen.maven;

import java.io.File;
import java.util.Locale;

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
 * Writes a Markdown page for each of the project's models, with the
 * documentation that Entity Modeler keeps for it and its entities, and a
 * Mermaid diagram of its entities and relationships, in {@code docs/models},
 * to commit; writing them deletes the pages that eogen wrote for models that
 * it no longer documents. Or, with {@code format} {@code html}, EOModelDoc's
 * HTML pages, as WOLips's EOModelDoc wrote them: an index of every model and
 * entity, and a page for each model, entity and stored procedure. They go in
 * the project's site, which {@code pre-site} writes them into before the site
 * is built. With {@code checkDoc}, it writes nothing, and fails the build if a
 * page would change, or an orphan would be deleted. Nothing is written if the
 * model group or a template has errors, unless forced.
 */
@Mojo(name = "doc", defaultPhase = LifecyclePhase.PRE_SITE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class DocMojo extends AbstractEOGenMojo {
	/**
	 * What the pages are written in: {@code markdown}, a page for each model,
	 * or {@code html}, EOModelDoc's pages.
	 */
	@Parameter(property = "eogen.doc.format", defaultValue = "markdown")
	private String format;

	/**
	 * The folder of the pages. The default is
	 * {@code ${project.build.directory}/site/eomodeldoc} for HTML, and
	 * {@code ${project.basedir}/docs/models} for Markdown.
	 */
	@Parameter(property = "eogen.doc.output")
	private File outputDirectory;

	/**
	 * A folder of templates, such as {@code entityContent.html.vm} or
	 * {@code model.md.vm}, and of the files that are copied, such as
	 * {@code eomodeldoc.css}, which come before the bundled ones.
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

	/**
	 * The most entities that a model can have and still have a diagram of them
	 * all at the top of its Markdown page: 0 for none. Each entity's section
	 * has a diagram of the entities that it reaches, and that reach it.
	 */
	@Parameter(property = "eogen.doc.modelDiagramLimit", defaultValue = "10")
	private int modelDiagramLimit;

	/**
	 * Writes nothing, and fails the build if a page would change, or an orphan
	 * would be deleted.
	 */
	@Parameter(property = "eogen.checkDoc", defaultValue = "false")
	private boolean checkDoc;

	@Override
	protected void execute(EOGenerator generator) throws MojoExecutionException, MojoFailureException {
		DocOptions options = new DocOptions();
		options.setFormat(format());
		File output = outputDirectory;
		if (output == null) {
			output = options.getFormat() == DocOptions.Format.MARKDOWN ? new File(project.getBasedir(), DocOptions.DEFAULT_MARKDOWN_OUTPUT)
					: new File(project.getBuild().getDirectory(), "site/eomodeldoc");
		}
		options.setOutput(output);
		options.setTemplateFolder(templateDirectory);
		options.setEntityURL(entityURL);
		options.setIncludeDependencies(includeDependencies);
		if (modelDiagramLimit < 0) {
			throw new MojoExecutionException("modelDiagramLimit is at least 0, not " + modelDiagramLimit + ".");
		}
		options.setModelDiagramLimit(modelDiagramLimit);
		DocResult result;
		try {
			result = generator.generateDoc(options, !checkDoc);
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
		if (result.hasErrors() && !isForce()) {
			throw new MojoFailureException("There are errors, so no page was " + (checkDoc ? "checked" : "written") + ". Fix them, or set eogen.force to "
					+ (checkDoc ? "check" : "write") + " anyway.");
		}
		String models = String.join(", ", result.getModels());
		int pages = result.getPages().size();
		if (checkDoc) {
			for (DocResult.Page page : result.getStalePages()) {
				getLog().error(relative(page.getFile()) + (page.getStatus() == DocResult.Status.CREATED ? " is missing." : " is out of date."));
			}
			for (File orphan : result.getOrphans()) {
				getLog().error(relative(orphan) + " is an orphan: eogen wrote it for a model that it no longer documents.");
			}
			int stale = result.getStalePages().size() + result.getOrphans().size();
			if (stale > 0) {
				throw new MojoFailureException(stale + (stale == 1 ? " page is" : " pages are") + " stale. Run mvn eogen:doc, and commit the result.");
			}
			getLog().info((pages == 1 ? "The page" : "All " + pages + " pages") + " of " + models + " in " + relative(output) + (pages == 1 ? " is" : " are") + " up to date.");
			return;
		}
		for (File orphan : result.getOrphans()) {
			getLog().info("Deleted " + relative(orphan) + ", which eogen wrote for a model that it no longer documents.");
		}
		getLog().info("Documented " + models + " in " + relative(output) + ": " + pages + (pages == 1 ? " file, " : " files, ") + result.getStalePages().size() + " written.");
	}

	private DocOptions.Format format() throws MojoExecutionException {
		try {
			return DocOptions.Format.valueOf(format.trim().toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			throw new MojoExecutionException("eogen.doc.format is html or markdown, not " + format + ".", e);
		}
	}
}

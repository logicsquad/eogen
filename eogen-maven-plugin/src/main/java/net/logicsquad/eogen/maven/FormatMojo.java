package net.logicsquad.eogen.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import net.logicsquad.eogen.EOGenerator;
import net.logicsquad.eogen.EOGeneratorException;
import net.logicsquad.eogen.format.FormatResult;
import net.logicsquad.eogen.format.FormatResult.FormattedFile;

/**
 * Formats the project's models as Entity Modeler saves them, so that a model
 * edited by hand, or by a coding agent, looks as Entity Modeler would write
 * it. Only the layout changes: if saving a model would change what any of its
 * files says, the model is left alone, and the build fails. With
 * {@code eogen.checkFormat}, it changes nothing, and fails if any model needs
 * formatting.
 */
@Mojo(name = "format", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class FormatMojo extends AbstractEOGenMojo {
	/**
	 * Changes nothing, and fails the build if any model needs formatting.
	 */
	@Parameter(property = "eogen.checkFormat", defaultValue = "false")
	private boolean checkFormat;

	@Override
	protected void execute(EOGenerator generator) throws MojoExecutionException, MojoFailureException {
		FormatResult result;
		try {
			result = generator.format(!checkFormat);
		} catch (EOGeneratorException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		for (String error : result.getErrors()) {
			getLog().error(error);
		}
		if (!result.getErrors().isEmpty()) {
			throw new MojoFailureException("The model group has errors, so no model was formatted.");
		}
		for (FormattedFile file : result.getRefusedFiles()) {
			getLog().error(relative(file.getFile()) + ": its model is left alone, since saving it would change what it says: " + file.getReason() + ".");
		}
		for (FormattedFile file : result.getFormattedFiles()) {
			if (checkFormat) {
				getLog().error(relative(file.getFile()) + " is not formatted as Entity Modeler writes it.");
			} else {
				getLog().info("Formatted " + relative(file.getFile()));
			}
		}
		if (!result.getRefusedFiles().isEmpty()) {
			throw new MojoFailureException(result.getRefusedFiles().size() + (result.getRefusedFiles().size() == 1 ? " model file" : " model files")
					+ " would change in content, not only layout, so their models were left alone.");
		}
		if (checkFormat && !result.getFormattedFiles().isEmpty()) {
			throw new MojoFailureException(result.getFormattedFiles().size() + (result.getFormattedFiles().size() == 1 ? " model file is" : " model files are")
					+ " not formatted. Run mvn eogen:format, and commit the result.");
		}
		getLog().info(result.getFiles().size() + " model files checked, " + result.getFormattedFiles().size() + (checkFormat ? " to format." : " formatted."));
	}
}

package net.logicsquad.eogen.maven;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import net.logicsquad.eogen.EOGenerator;
import net.logicsquad.eogen.EOGeneratorException;
import net.logicsquad.eogen.VerificationResult;
import net.logicsquad.eogen.model.EOModelVerificationFailure;

/**
 * Loads the project's model group, then resolves and verifies it, as Entity
 * Modeler does. It reports what it finds, and fails the build on errors.
 */
@Mojo(name = "verify", defaultPhase = LifecyclePhase.VERIFY, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class VerifyMojo extends AbstractEOGenMojo {
	/**
	 * Whether to report failures in models from dependencies, as well as in the
	 * project's own.
	 */
	@Parameter(property = "eogen.verify.includeDependencies", defaultValue = "false")
	private boolean includeDependencies;

	/**
	 * Whether warnings fail the build too.
	 */
	@Parameter(property = "eogen.verify.failOnWarning", defaultValue = "false")
	private boolean failOnWarning;

	@Override
	protected void execute(EOGenerator generator) throws MojoExecutionException, MojoFailureException {
		VerificationResult result;
		try {
			result = generator.verify(includeDependencies);
		} catch (EOGeneratorException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		if (result.getModelNames().isEmpty()) {
			getLog().warn("No models in " + modelDirectories() + ".");
			return;
		}
		List<EOModelVerificationFailure> warnings = result.getWarnings();
		List<EOModelVerificationFailure> errors = result.getErrors();
		for (EOModelVerificationFailure warning : warnings) {
			getLog().warn(describe(warning));
		}
		for (EOModelVerificationFailure error : errors) {
			getLog().error(describe(error));
		}
		String summary = "Verified " + String.join(", ", result.getModelNames()) + ": " + errors.size() + (errors.size() == 1 ? " error, " : " errors, ")
				+ warnings.size() + (warnings.size() == 1 ? " warning." : " warnings.");
		if (!errors.isEmpty() || (failOnWarning && !warnings.isEmpty())) {
			throw new MojoFailureException(summary);
		}
		getLog().info(summary);
	}

	private static String describe(EOModelVerificationFailure failure) {
		return (failure.getModel() == null ? "" : failure.getModel().getName() + ": ") + failure.getMessage();
	}
}

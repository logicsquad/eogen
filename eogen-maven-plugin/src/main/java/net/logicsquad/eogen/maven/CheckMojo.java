package net.logicsquad.eogen.maven;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import net.logicsquad.eogen.EOGenerator;
import net.logicsquad.eogen.EOGeneratorException;
import net.logicsquad.eogen.GeneratedFile;
import net.logicsquad.eogen.GeneratedFile.Kind;
import net.logicsquad.eogen.GeneratedFile.Status;
import net.logicsquad.eogen.GenerationResult;

/**
 * Fails the build if the generated classes are stale: if {@code generate}
 * would update a superclass, or create a missing superclass or subclass. It
 * generates into a scratch folder, and changes nothing in the project. Errors
 * in the model group or templates fail it too, unless {@code eogen.force} is
 * set, since {@code generate} would not write the classes.
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.VERIFY, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class CheckMojo extends AbstractEOGenMojo {
	@Override
	protected void execute(EOGenerator generator) throws MojoExecutionException, MojoFailureException {
		int superclasses = 0;
		int stale = 0;
		int errors = 0;
		for (File eogenFile : eogenFiles()) {
			GenerationResult result;
			try {
				result = generator.check(eogenFile);
			} catch (EOGeneratorException e) {
				throw new MojoExecutionException(e.getMessage(), e);
			}
			errors += logErrors(eogenFile, result);
			if (result.hasErrors() && !isForce()) {
				continue;
			}
			for (GeneratedFile file : result.getFiles()) {
				if (file.getKind() == Kind.SUPERCLASS) {
					superclasses++;
				}
				if (file.isStale()) {
					stale++;
					getLog().error(relative(file.getFile()) + (file.getStatus() == Status.CREATED ? " is missing." : " is out of date."));
				}
			}
		}
		if (errors > 0 && !isForce()) {
			throw new MojoFailureException(errors + (errors == 1 ? " error" : " errors") + " in the model group or templates. Fix them, or set eogen.force to check anyway.");
		}
		if (stale > 0) {
			throw new MojoFailureException(stale + (stale == 1 ? " generated file is" : " generated files are") + " stale. Run mvn eogen:generate, and commit the result.");
		}
		getLog().info("All " + superclasses + " generated superclasses are up to date.");
	}
}

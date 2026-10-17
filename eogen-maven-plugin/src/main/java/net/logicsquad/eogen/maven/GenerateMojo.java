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
 * Generates the entity classes for the project's .eogen files. It writes every
 * superclass whose contents have changed, and every subclass that is missing,
 * and leaves existing subclasses alone, as WOLips does. Unlike WOLips, it
 * writes nothing for an .eogen file whose model group or templates have
 * errors, and fails the build, unless {@code eogen.force} is set.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class GenerateMojo extends AbstractEOGenMojo {
	@Override
	protected void execute(EOGenerator generator) throws MojoExecutionException, MojoFailureException {
		int errors = 0;
		for (File eogenFile : eogenFiles()) {
			GenerationResult result;
			try {
				result = generator.generate(eogenFile);
			} catch (EOGeneratorException e) {
				throw new MojoExecutionException(e.getMessage(), e);
			}
			errors += logErrors(eogenFile, result);
			if (!result.isWritten()) {
				getLog().error(relative(eogenFile) + ": nothing written.");
				continue;
			}
			for (GeneratedFile file : result.getStaleFiles()) {
				getLog().info((file.getStatus() == Status.CREATED ? "Created " : "Updated ") + relative(file.getFile()));
			}
			getLog().info(relative(eogenFile) + ": " + result.count(Kind.SUPERCLASS, Status.CREATED) + " superclasses created, "
					+ result.count(Kind.SUPERCLASS, Status.UPDATED) + " updated, " + result.count(Kind.SUPERCLASS, Status.UNCHANGED) + " unchanged; "
					+ result.count(Kind.SUBCLASS, Status.CREATED) + " subclasses created.");
		}
		if (errors > 0 && !isForce()) {
			throw new MojoFailureException(errors + (errors == 1 ? " error" : " errors")
					+ " in the model group or templates, so the classes for the .eogen files that have them were not written. Fix them, or set eogen.force to write them anyway.");
		}
	}
}

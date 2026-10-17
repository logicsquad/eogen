package net.logicsquad.eogen.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
 * set, since {@code generate} would not write the classes. So do orphans:
 * superclasses that no .eogen file generates any more, and their subclasses.
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.VERIFY, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class CheckMojo extends AbstractEOGenMojo {
	@Override
	protected void execute(EOGenerator generator) throws MojoExecutionException, MojoFailureException {
		int superclasses = 0;
		int stale = 0;
		int errors = 0;
		List<GenerationResult> results = new ArrayList<>();
		for (File eogenFile : eogenFiles()) {
			GenerationResult result;
			try {
				result = generator.check(eogenFile);
			} catch (EOGeneratorException e) {
				throw new MojoExecutionException(e.getMessage(), e);
			}
			results.add(result);
			errors += logProblems(eogenFile, result);
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
		int orphans = 0;
		if (findsOrphans()) {
			try {
				for (GeneratedFile orphan : generator.findOrphans(results)) {
					orphans++;
					getLog().error(relative(orphan.getFile()) + (orphan.getKind() == Kind.SUBCLASS ? " is the subclass of an orphaned superclass."
							: " is an orphan: no .eogen file generates it any more."));
				}
			} catch (EOGeneratorException e) {
				throw new MojoExecutionException(e.getMessage(), e);
			}
		}
		if (errors > 0 && !isForce()) {
			throw new MojoFailureException(errors + (errors == 1 ? " error" : " errors") + " in the model group or templates. Fix them, or set eogen.force to check anyway.");
		}
		if (stale > 0) {
			throw new MojoFailureException(stale + (stale == 1 ? " generated file is" : " generated files are") + " stale. Run mvn eogen:generate, and commit the result.");
		}
		if (orphans > 0) {
			throw new MojoFailureException(orphans + (orphans == 1 ? " orphan" : " orphans")
					+ ". Run mvn eogen:generate -Deogen.deleteOrphans to delete the superclasses, delete the subclasses by hand if their entities are gone, and commit the result.");
		}
		getLog().info("All " + superclasses + " generated superclasses are up to date.");
	}
}

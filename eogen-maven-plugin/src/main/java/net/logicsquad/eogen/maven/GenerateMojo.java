package net.logicsquad.eogen.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
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
 * <p>
 * It also reports orphans: superclasses that no .eogen file generates any
 * more, such as those of entities since deleted or renamed, and their
 * subclasses. With {@code eogen.deleteOrphans}, it deletes the orphaned
 * superclasses. It never deletes a subclass, which was edited by hand.
 * </p>
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class GenerateMojo extends AbstractEOGenMojo {
	/**
	 * Deletes orphaned superclasses: those that no .eogen file generates any
	 * more. Their subclasses are only reported.
	 */
	@Parameter(property = "eogen.deleteOrphans", defaultValue = "false")
	private boolean deleteOrphans;

	@Override
	protected void execute(EOGenerator generator) throws MojoExecutionException, MojoFailureException {
		int errors = 0;
		List<GenerationResult> results = new ArrayList<>();
		for (File eogenFile : eogenFiles()) {
			GenerationResult result;
			try {
				result = generator.generate(eogenFile);
			} catch (EOGeneratorException e) {
				throw new MojoExecutionException(e.getMessage(), e);
			}
			results.add(result);
			errors += logProblems(eogenFile, result);
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
		if (findsOrphans()) {
			handleOrphans(generator, results);
		}
		if (errors > 0 && !isForce()) {
			throw new MojoFailureException(errors + (errors == 1 ? " error" : " errors")
					+ " in the model group or templates, so the classes for the .eogen files that have them were not written. Fix them, or set eogen.force to write them anyway.");
		}
	}

	private void handleOrphans(EOGenerator generator, List<GenerationResult> results) throws MojoExecutionException {
		try {
			List<GeneratedFile> orphans = generator.findOrphans(results);
			if (deleteOrphans) {
				for (GeneratedFile deleted : EOGenerator.deleteOrphans(orphans)) {
					getLog().info("Deleted " + relative(deleted.getFile()) + ", which no .eogen file generates any more.");
				}
			}
			for (GeneratedFile orphan : orphans) {
				if (orphan.getKind() == Kind.SUBCLASS) {
					getLog().warn(relative(orphan.getFile()) + " is the subclass of an orphaned superclass. Delete it by hand if its entity is gone.");
				} else if (!deleteOrphans) {
					getLog().warn(relative(orphan.getFile()) + " is an orphan: no .eogen file generates it any more. Delete it, or set eogen.deleteOrphans.");
				}
			}
		} catch (EOGeneratorException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}

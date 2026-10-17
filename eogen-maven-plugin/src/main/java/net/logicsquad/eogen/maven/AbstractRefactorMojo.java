package net.logicsquad.eogen.maven;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import net.logicsquad.eogen.Finding;
import net.logicsquad.eogen.refactor.RefactorResult;
import net.logicsquad.eogen.refactor.RefactorResult.ChangedFile;

/**
 * A goal that renames or removes something in the project's models, and
 * reports the files it changes and what is left to do by hand.
 */
abstract class AbstractRefactorMojo extends AbstractEOGenMojo {
	/**
	 * Changes nothing, and only reports what would change.
	 */
	@Parameter(property = "eogen.dryRun", defaultValue = "false")
	private boolean dryRun;

	/**
	 * Returns whether to change nothing, and only report.
	 *
	 * @return {@code true} for a dry run
	 */
	protected boolean isDryRun() {
		return dryRun;
	}

	/**
	 * Reports a rename or a removal, and fails the build if it was refused.
	 *
	 * @param result what it did
	 * @param what what it was, such as {@code Renaming Book.titel}
	 * @throws MojoFailureException if it was refused
	 */
	protected void report(RefactorResult result, String what) throws MojoFailureException {
		for (Finding finding : result.getFindings()) {
			if (finding.isError()) {
				getLog().error(finding.getMessage());
			}
		}
		if (result.isRefused()) {
			throw new MojoFailureException(what + " was refused, so nothing changed.");
		}
		for (ChangedFile file : result.getFiles()) {
			getLog().info(verb(file.getStatus()) + " " + relative(file.getFile()));
		}
		for (Finding finding : result.getFindings()) {
			if (!finding.isError()) {
				getLog().warn(finding.getMessage());
			}
		}
		int count = result.getFiles().size();
		getLog().info(what + (dryRun ? " would change " : " changed ") + count + (count == 1 ? " model file." : " model files."));
	}

	private String verb(RefactorResult.Status status) {
		switch (status) {
		case CREATED:
			return dryRun ? "Would create" : "Created";
		case DELETED:
			return dryRun ? "Would delete" : "Deleted";
		default:
			return dryRun ? "Would change" : "Changed";
		}
	}
}

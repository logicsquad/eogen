package net.logicsquad.eogen.refactor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.logicsquad.eogen.Finding;

/**
 * What renaming or removing something in a project's models did, or would
 * do: the model files it changes, and its findings, which are the reasons it
 * was refused, as errors, or what is left to do by hand, as warnings.
 */
public final class RefactorResult {
	/**
	 * What happens to a model file.
	 */
	public enum Status {
		/**
		 * Its contents change.
		 */
		CHANGED,

		/**
		 * It is new, such as a renamed entity's file.
		 */
		CREATED,

		/**
		 * It goes, such as a removed entity's file, or a renamed entity's old one.
		 */
		DELETED
	}

	/**
	 * A model file, and what happens to it.
	 */
	public static final class ChangedFile {
		private final File file;

		private final Status status;

		/**
		 * Constructor.
		 *
		 * @param file the file in the project
		 * @param status what happens to it
		 */
		public ChangedFile(File file, Status status) {
			this.file = file;
			this.status = status;
		}

		/**
		 * Returns the file in the project.
		 *
		 * @return file
		 */
		public File getFile() {
			return file;
		}

		/**
		 * Returns what happens to it.
		 *
		 * @return status
		 */
		public Status getStatus() {
			return status;
		}

		@Override
		public String toString() {
			return status + " " + file;
		}
	}

	private final List<ChangedFile> files;

	private final List<Finding> findings;

	private final boolean written;

	/**
	 * Constructor.
	 *
	 * @param files the model files that change
	 * @param findings the reasons for a refusal, and what is left to do by hand
	 * @param written whether the files were written
	 */
	public RefactorResult(List<ChangedFile> files, List<Finding> findings, boolean written) {
		this.files = Collections.unmodifiableList(new ArrayList<>(files));
		this.findings = Collections.unmodifiableList(new ArrayList<>(findings));
		this.written = written;
	}

	/**
	 * Returns the model files that change, in name order.
	 *
	 * @return files
	 */
	public List<ChangedFile> getFiles() {
		return files;
	}

	/**
	 * Returns the reasons for a refusal, as errors, and what is left to do by
	 * hand, as warnings.
	 *
	 * @return findings
	 */
	public List<Finding> getFindings() {
		return findings;
	}

	/**
	 * Returns whether the change was refused, so that nothing changes.
	 *
	 * @return {@code true} if refused
	 */
	public boolean isRefused() {
		for (Finding finding : findings) {
			if (finding.isError()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the changed files were written to the project.
	 *
	 * @return {@code true} if written
	 */
	public boolean isWritten() {
		return written;
	}
}

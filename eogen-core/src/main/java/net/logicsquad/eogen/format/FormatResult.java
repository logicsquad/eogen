package net.logicsquad.eogen.format;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.logicsquad.eogen.Finding;

/**
 * What formatting a project's models did, or would do.
 */
public final class FormatResult {
	/**
	 * What formatting does to a model file.
	 */
	public enum Status {
		/**
		 * The file is already as Entity Modeler writes it.
		 */
		UNCHANGED,

		/**
		 * Only the file's layout changes: once parsed, its contents are the same.
		 */
		FORMATTED,

		/**
		 * Saving would change what the file says, so its model is left alone.
		 */
		REFUSED
	}

	/**
	 * A model file, and what formatting does to it.
	 */
	public static final class FormattedFile {
		private final File file;

		private final Status status;

		private final String reason;

		/**
		 * Constructor.
		 *
		 * @param file the model file
		 * @param status what formatting does to it
		 * @param reason why it is refused, or null
		 */
		public FormattedFile(File file, Status status, String reason) {
			this.file = file;
			this.status = status;
			this.reason = reason;
		}

		/**
		 * Returns the model file.
		 *
		 * @return file
		 */
		public File getFile() {
			return file;
		}

		/**
		 * Returns what formatting does to the file.
		 *
		 * @return status
		 */
		public Status getStatus() {
			return status;
		}

		/**
		 * Returns why the file is refused: the first thing that saving it would
		 * change.
		 *
		 * @return reason, or null if it isn't refused
		 */
		public String getReason() {
			return reason;
		}

		@Override
		public String toString() {
			return status + " " + file + (reason == null ? "" : ": " + reason);
		}
	}

	private final List<FormattedFile> files;

	private final List<String> errors;

	private final List<Finding> findings;

	private final boolean written;

	/**
	 * Constructor.
	 *
	 * @param files the model files, and what formatting does to each
	 * @param errors errors that stopped the model group loading, so that
	 *        nothing was formatted
	 * @param written whether the formatted files were written to the project
	 */
	public FormatResult(List<FormattedFile> files, List<String> errors, boolean written) {
		this(files, errors, findings(errors), written);
	}

	/**
	 * Constructor.
	 *
	 * @param files the project's model files, and what formatting does to each
	 * @param errors the model group's errors, which stop formatting
	 * @param findings the errors, and the files left alone, as findings
	 * @param written whether the formatted files were written
	 */
	public FormatResult(List<FormattedFile> files, List<String> errors, List<Finding> findings, boolean written) {
		this.files = Collections.unmodifiableList(new ArrayList<>(files));
		this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
		this.findings = Collections.unmodifiableList(new ArrayList<>(findings));
		this.written = written;
	}

	private static List<Finding> findings(List<String> errors) {
		List<Finding> findings = new ArrayList<>();
		for (String error : errors) {
			findings.add(Finding.error(Finding.FAILURE, error).build());
		}
		return findings;
	}

	/**
	 * Returns every model file that Entity Modeler writes, and what formatting
	 * does to each.
	 *
	 * @return files, sorted by path
	 */
	public List<FormattedFile> getFiles() {
		return files;
	}

	/**
	 * Returns the files whose layout changes: those that were written, or would
	 * be. A file in a model with a refused file is left alone, so it is not one
	 * of them.
	 *
	 * @return formatted files
	 */
	public List<FormattedFile> getFormattedFiles() {
		Set<File> refusedModels = new HashSet<>();
		for (FormattedFile file : withStatus(Status.REFUSED)) {
			refusedModels.add(file.getFile().getParentFile());
		}
		List<FormattedFile> formatted = new ArrayList<>();
		for (FormattedFile file : withStatus(Status.FORMATTED)) {
			if (!refusedModels.contains(file.getFile().getParentFile())) {
				formatted.add(file);
			}
		}
		return formatted;
	}

	/**
	 * Returns the files that saving would change in content, which leave their
	 * models unformatted.
	 *
	 * @return refused files
	 */
	public List<FormattedFile> getRefusedFiles() {
		return withStatus(Status.REFUSED);
	}

	/**
	 * Returns the errors that stopped the model group loading.
	 *
	 * @return error messages
	 */
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * Returns whether {@link #getFormattedFiles()} were written to the project,
	 * rather than only found.
	 *
	 * @return {@code true} if the formatted files were written
	 */
	public boolean isWritten() {
		return written;
	}

	private List<FormattedFile> withStatus(Status status) {
		List<FormattedFile> matching = new ArrayList<>();
		for (FormattedFile file : files) {
			if (file.getStatus() == status) {
				matching.add(file);
			}
		}
		return matching;
	}

	/**
	 * Returns the model group's errors, and a {@link Finding#FORMAT_LOSS} for
	 * each file left alone, as findings.
	 *
	 * @return findings
	 */
	public List<Finding> getFindings() {
		return findings;
	}
}

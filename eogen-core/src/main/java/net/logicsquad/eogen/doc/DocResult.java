package net.logicsquad.eogen.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.logicsquad.eogen.Finding;

/**
 * What documenting the models did: the models documented, the files written,
 * or that would have been written, and the findings, which are errors.
 */
public final class DocResult {
	private final List<String> models;

	private final List<File> files;

	private final List<Finding> findings;

	private final boolean written;

	/**
	 * Constructor.
	 *
	 * @param models the names of the models documented
	 * @param files the files
	 * @param findings errors
	 * @param written whether the files were written
	 */
	public DocResult(List<String> models, List<File> files, List<Finding> findings, boolean written) {
		this.models = Collections.unmodifiableList(new ArrayList<>(models));
		this.files = Collections.unmodifiableList(new ArrayList<>(files));
		this.findings = Collections.unmodifiableList(new ArrayList<>(findings));
		this.written = written;
	}

	/**
	 * Returns the names of the models documented, in name order.
	 *
	 * @return model names
	 */
	public List<String> getModels() {
		return models;
	}

	/**
	 * Returns the files, the index first.
	 *
	 * @return files
	 */
	public List<File> getFiles() {
		return files;
	}

	/**
	 * Returns the errors: those that loading the model group finds, and each
	 * reference that a template renders with no value.
	 *
	 * @return findings
	 */
	public List<Finding> getFindings() {
		return findings;
	}

	/**
	 * Returns whether there are errors.
	 *
	 * @return {@code true} if there are
	 */
	public boolean hasErrors() {
		for (Finding finding : findings) {
			if (finding.isError()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the files were written: they are unless there are
	 * errors, or when forced.
	 *
	 * @return {@code true} if written
	 */
	public boolean isWritten() {
		return written;
	}
}

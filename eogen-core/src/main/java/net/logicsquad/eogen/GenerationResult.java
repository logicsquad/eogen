package net.logicsquad.eogen;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * What generating from one .eogen file did, or would do.
 */
public final class GenerationResult {
	private final File eogenFile;

	private final boolean written;

	private final List<GeneratedFile> files;

	private final List<String> errors;

	/**
	 * Constructor.
	 *
	 * @param eogenFile the .eogen file
	 * @param written whether the stale files were written to the project
	 * @param files the files generated
	 * @param errors the model and template errors the generator reported
	 */
	public GenerationResult(File eogenFile, boolean written, List<GeneratedFile> files, List<String> errors) {
		this.eogenFile = eogenFile;
		this.written = written;
		this.files = Collections.unmodifiableList(new ArrayList<>(files));
		this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
	}

	/**
	 * Returns the .eogen file.
	 *
	 * @return .eogen file
	 */
	public File getEOGenFile() {
		return eogenFile;
	}

	/**
	 * Returns whether the stale files were written to the project, as
	 * {@link EOGenerator#generate(File)} does, rather than only compared, as
	 * {@link EOGenerator#check(File)} does. Generating writes nothing when there
	 * are errors, unless the generator is forced.
	 *
	 * @return {@code true} if the project was changed
	 */
	public boolean isWritten() {
		return written;
	}

	/**
	 * Returns every superclass generated, and every subclass that was missing.
	 *
	 * @return generated files: superclasses, then subclasses, each sorted by
	 *         path
	 */
	public List<GeneratedFile> getFiles() {
		return files;
	}

	/**
	 * Returns the files that differ from, or are missing in, the project.
	 *
	 * @return stale files
	 */
	public List<GeneratedFile> getStaleFiles() {
		List<GeneratedFile> stale = new ArrayList<>();
		for (GeneratedFile file : files) {
			if (file.isStale()) {
				stale.add(file);
			}
		}
		return stale;
	}

	/**
	 * Returns whether any generated file differs from, or is missing in, the
	 * project.
	 *
	 * @return {@code true} if any file is stale
	 */
	public boolean isStale() {
		return !getStaleFiles().isEmpty();
	}

	/**
	 * Returns how many generated files have a status.
	 *
	 * @param kind superclass or subclass
	 * @param status status
	 * @return number of files
	 */
	public int count(GeneratedFile.Kind kind, GeneratedFile.Status status) {
		int count = 0;
		for (GeneratedFile file : files) {
			if (file.getKind() == kind && file.getStatus() == status) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns the errors that loading and verifying the model group reported,
	 * and the references that a template rendered without a value. WOLips shows
	 * these after generating, and generates anyway; eogen writes nothing unless
	 * forced.
	 *
	 * @return error messages
	 */
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * Returns whether there are any errors.
	 *
	 * @return {@code true} if {@link #getErrors()} is not empty
	 */
	public boolean hasErrors() {
		return !errors.isEmpty();
	}
}

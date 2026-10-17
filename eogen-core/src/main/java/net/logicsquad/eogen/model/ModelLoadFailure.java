package net.logicsquad.eogen.model;

import java.io.File;

/**
 * eogen's failure for a model that failed to load, which says where: the file,
 * and the line and column the parser stopped at, as far as the exceptions
 * say.
 */
public class ModelLoadFailure extends EOModelVerificationFailure {
	private final File file;

	private final Integer line;

	private final Integer column;

	/**
	 * Constructor.
	 *
	 * @param model the model that failed to load
	 * @param message the failure, in one line
	 * @param file the absolute file that failed to load, or null
	 * @param line the line the parser stopped at, or null
	 * @param column the column the parser stopped at, or null
	 */
	public ModelLoadFailure(EOModel model, String message, File file, Integer line, Integer column) {
		super(model, model, message, false, null);
		this.file = file;
		this.line = line;
		this.column = column;
	}

	/**
	 * Returns the file that failed to load.
	 *
	 * @return absolute file, or null
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the line the parser stopped at.
	 *
	 * @return line, from 1, or null
	 */
	public Integer getLine() {
		return line;
	}

	/**
	 * Returns the column the parser stopped at.
	 *
	 * @return column, from 1, or null
	 */
	public Integer getColumn() {
		return column;
	}
}

package net.logicsquad.eogen.lint;

import java.io.File;

/**
 * A mistake in a model that loading it hides: a key that nothing reads, or a
 * name in an entity's lists that matches nothing.
 */
public final class LintFinding {
	private final File file;

	private final String path;

	private final String message;

	/**
	 * Constructor.
	 *
	 * @param file the model file the mistake is in
	 * @param path the file's path from the model's parent folder, such as
	 *        {@code Library.eomodeld/Book.plist}
	 * @param message what is wrong
	 */
	public LintFinding(File file, String path, String message) {
		this.file = file;
		this.path = path;
		this.message = message;
	}

	/**
	 * Returns the model file the mistake is in.
	 *
	 * @return file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the file's path from the model's parent folder.
	 *
	 * @return path, such as {@code Library.eomodeld/Book.plist}
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns what is wrong.
	 *
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the path and the message, as eogen reports them.
	 *
	 * @return description
	 */
	@Override
	public String toString() {
		return path + ": " + message;
	}
}

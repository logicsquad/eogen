package net.logicsquad.eogen.lint;

import java.io.File;

import net.logicsquad.eogen.Finding;

/**
 * A mistake in a model that loading it hides: a key that nothing reads, or a
 * name or key that matches nothing.
 */
public final class LintFinding {
	private final File file;

	private final String path;

	private final String code;

	private final String entity;

	private final String object;

	private final String key;

	private final String suggestion;

	private final String message;

	/**
	 * Constructor.
	 *
	 * @param file the model file the mistake is in
	 * @param path the file's path from the model's parent folder, such as
	 *        {@code Library.eomodeld/Book.plist}
	 * @param code its kind, such as {@link Finding#UNKNOWN_KEY}
	 * @param entity the name of the entity it is in, or null
	 * @param object what it is about, as the message names it, such as
	 *        {@code attribute price}, or null
	 * @param key the key, name or key path that is wrong, or null
	 * @param suggestion what the key was most likely meant to be, or null
	 * @param message what is wrong
	 */
	public LintFinding(File file, String path, String code, String entity, String object, String key, String suggestion, String message) {
		this.file = file;
		this.path = path;
		this.code = code;
		this.entity = entity;
		this.object = object;
		this.key = key;
		this.suggestion = suggestion;
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
	 * Returns the mistake's kind.
	 *
	 * @return code, such as {@link Finding#UNKNOWN_KEY}
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Returns the name of the entity the mistake is in.
	 *
	 * @return entity, or null
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * Returns what the mistake is about, as the message names it.
	 *
	 * @return object, such as {@code attribute price}, or null
	 */
	public String getObject() {
		return object;
	}

	/**
	 * Returns the key, name or key path that is wrong.
	 *
	 * @return key, or null
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Returns what the key was most likely meant to be.
	 *
	 * @return suggestion, or null
	 */
	public String getSuggestion() {
		return suggestion;
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

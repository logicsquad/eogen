package net.logicsquad.eogen.utils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Describes why a model failed to load in one line, such as
 * {@code Library.eomodeld/Book.plist:12:58: Property list parsing failed while
 * attempting to read dictionary. Read key and value with no terminating
 * semicolon.}, from the chain of exceptions that the model classes and the
 * property list parser throw.
 */
public final class LoadErrors {
	/**
	 * A model file, as the model classes quote it: "Failed to load entity from
	 * '/path/Book.plist'." That's its URL's path, or its URL.
	 */
	private static final Pattern FILE = Pattern.compile("'([^']+\\.(?:plist|fspec|storedProcedure|eomodeld))'");

	/**
	 * A model's index, which the model classes don't quote: "index.eomodeld is
	 * corrupted."
	 */
	private static final String CORRUPT_INDEX = "index.eomodeld is corrupted.";

	/**
	 * Where the parser stopped: "At line number: 12, column: 58."
	 */
	private static final Pattern POSITION = Pattern.compile("\\s*At line number: (-?\\d+), column: (-?\\d+)\\.?");

	private LoadErrors() {
	}

	/**
	 * Describes why a model failed to load.
	 *
	 * @param failure what the model classes threw
	 * @param modelFolder the model's folder, whose parent file paths are given
	 *        relative to, or null
	 * @return description
	 */
	public static String describe(Throwable failure, File modelFolder) {
		return locate(failure, modelFolder).getDescription();
	}

	/**
	 * Works out where a model failed to load, and why.
	 *
	 * @param failure what the model classes threw
	 * @param modelFolder the model's folder, whose parent the description gives
	 *        file paths relative to, or null
	 * @return the file, line and column, as far as the exceptions say, and the
	 *         description
	 */
	public static Location locate(Throwable failure, File modelFolder) {
		String file = null;
		String detail = null;
		String line = null;
		String column = null;
		for (Throwable cause = failure; cause != null; cause = cause.getCause() == cause ? null : cause.getCause()) {
			String message = cause.getMessage();
			if (message == null) {
				continue;
			}
			Matcher fileMatcher = FILE.matcher(message);
			if (fileMatcher.find()) {
				file = fileMatcher.group(1);
			} else if (message.startsWith(CORRUPT_INDEX)) {
				file = modelFolder == null ? "index.eomodeld" : new File(modelFolder, "index.eomodeld").getPath();
			}
			Matcher positionMatcher = POSITION.matcher(message);
			if (positionMatcher.find()) {
				line = positionMatcher.group(1);
				column = positionMatcher.group(2);
				detail = (message.substring(0, positionMatcher.start()) + message.substring(positionMatcher.end())).trim();
			} else if (detail == null && cause.getCause() == null) {
				detail = message;
			}
		}
		if (detail == null) {
			detail = failure.getMessage() == null ? failure.getClass().getName() : failure.getMessage();
		}
		Integer lineNumber = line == null || line.startsWith("-") ? null : Integer.valueOf(line);
		Integer columnNumber = lineNumber == null || column == null || column.startsWith("-") ? null : Integer.valueOf(column);
		StringBuilder description = new StringBuilder();
		if (file != null) {
			description.append(relative(file, modelFolder));
			if (lineNumber != null) {
				description.append(':').append(lineNumber);
				if (columnNumber != null) {
					description.append(':').append(columnNumber);
				}
			}
			description.append(": ");
		}
		return new Location(file == null ? null : absolute(file), lineNumber, columnNumber, description.append(detail).toString());
	}

	private static File absolute(String file) {
		try {
			return toPath(file).toAbsolutePath().normalize().toFile();
		} catch (InvalidPathException e) {
			return new File(file).getAbsoluteFile();
		}
	}

	private static String relative(String file, File modelFolder) {
		if (modelFolder == null || modelFolder.getParentFile() == null) {
			return file;
		}
		try {
			Path parent = modelFolder.getAbsoluteFile().getParentFile().toPath().normalize();
			Path path = toPath(file).toAbsolutePath().normalize();
			return path.startsWith(parent) ? parent.relativize(path).toString().replace(File.separatorChar, '/') : file;
		} catch (InvalidPathException e) {
			return file;
		}
	}

	/**
	 * Returns a file that the model classes quote as a URL, or as a URL's path,
	 * which has %20 for a space and on Windows is /C:/..., as a path.
	 */
	private static Path toPath(String file) {
		if (file.startsWith("file:") || file.startsWith("/")) {
			try {
				return Paths.get(new URI(file.startsWith("file:") ? file : "file:" + file));
			} catch (URISyntaxException | IllegalArgumentException e) {
				// A path after all, such as one with a space in it.
			}
		}
		return Paths.get(file);
	}

	/**
	 * Where a model failed to load, and why.
	 */
	public static final class Location {
		private final File file;

		private final Integer line;

		private final Integer column;

		private final String description;

		private Location(File file, Integer line, Integer column, String description) {
			this.file = file;
			this.line = line;
			this.column = column;
			this.description = description;
		}

		/**
		 * Returns the file that failed to load.
		 *
		 * @return absolute file, or null if the exceptions don't name one
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

		/**
		 * Returns the description, such as {@code Library.eomodeld/Book.plist:12:58:
		 * ...}.
		 *
		 * @return description
		 */
		public String getDescription() {
			return description;
		}
	}
}

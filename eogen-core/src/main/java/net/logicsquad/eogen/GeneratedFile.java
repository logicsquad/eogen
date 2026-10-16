package net.logicsquad.eogen;

import java.io.File;
import java.util.Objects;

/**
 * A file that generating from an .eogen file writes, or would write.
 */
public final class GeneratedFile {
	/**
	 * Which half of the generation gap a file belongs to.
	 */
	public enum Kind {
		/**
		 * A superclass, such as {@code _Person.java}, which is written every time.
		 */
		SUPERCLASS,

		/**
		 * A subclass, such as {@code Person.java}, which is only written when it is
		 * missing, so that it can be edited by hand.
		 */
		SUBCLASS
	}

	/**
	 * How the generated file compares with the one in the project.
	 */
	public enum Status {
		/**
		 * The project has no such file.
		 */
		CREATED,

		/**
		 * The project's file has different contents.
		 */
		UPDATED,

		/**
		 * The project's file already has these contents.
		 */
		UNCHANGED
	}

	private final File file;

	private final Kind kind;

	private final Status status;

	/**
	 * Constructor.
	 *
	 * @param file the file in the project
	 * @param kind superclass or subclass
	 * @param status how the generated file compares with the project's
	 */
	public GeneratedFile(File file, Kind kind, Status status) {
		this.file = Objects.requireNonNull(file);
		this.kind = Objects.requireNonNull(kind);
		this.status = Objects.requireNonNull(status);
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
	 * Returns whether the file is a superclass or a subclass.
	 *
	 * @return kind
	 */
	public Kind getKind() {
		return kind;
	}

	/**
	 * Returns how the generated file compares with the project's.
	 *
	 * @return status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Returns whether generating changes, or would change, the project's file.
	 *
	 * @return {@code true} if the file is {@link Status#CREATED} or
	 *         {@link Status#UPDATED}
	 */
	public boolean isStale() {
		return status != Status.UNCHANGED;
	}

	@Override
	public String toString() {
		return kind + " " + status + " " + file;
	}
}

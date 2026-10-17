package net.logicsquad.eogen.migration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.logicsquad.eogen.Finding;

/**
 * What generating migrations did, or would do: for each of the project's
 * models, the migration it writes, or why it writes none, and its findings,
 * which are errors that stop it, and a warning for each gap that a migration
 * leaves to fill in.
 */
public final class MigrationResult {
	/**
	 * What happens for a model.
	 */
	public enum Status {
		/**
		 * Its next migration is written.
		 */
		CREATED,

		/**
		 * Its tables haven't changed, so it has no migration.
		 */
		UNCHANGED,

		/**
		 * It has no migration, for a reason, such as that Wonder's
		 * {@code er.migration.skipModelNames} names it.
		 */
		SKIPPED
	}

	/**
	 * A model, and its migration.
	 */
	public static final class ModelMigration {
		private final String model;

		private final Status status;

		private final File file;

		private final String className;

		private final int number;

		private final boolean first;

		private final int gaps;

		private final String reason;

		/**
		 * Constructor.
		 *
		 * @param model the model's name
		 * @param status what happens for it
		 * @param file the migration's file, or null
		 * @param className the migration's class, with its package, or null
		 * @param number the migration's number, or -1
		 * @param first whether it is the model's first migration
		 * @param gaps how many gaps it leaves
		 * @param reason why there is none, or null
		 */
		public ModelMigration(String model, Status status, File file, String className, int number, boolean first, int gaps, String reason) {
			this.model = model;
			this.status = status;
			this.file = file;
			this.className = className;
			this.number = number;
			this.first = first;
			this.gaps = gaps;
			this.reason = reason;
		}

		/**
		 * Returns the model's name.
		 *
		 * @return model name
		 */
		public String getModel() {
			return model;
		}

		/**
		 * Returns what happens for the model.
		 *
		 * @return status
		 */
		public Status getStatus() {
			return status;
		}

		/**
		 * Returns the migration's file.
		 *
		 * @return file, or null if there is none
		 */
		public File getFile() {
			return file;
		}

		/**
		 * Returns the migration's class, with its package.
		 *
		 * @return class name, or null if there is none
		 */
		public String getClassName() {
			return className;
		}

		/**
		 * Returns the migration's number.
		 *
		 * @return number, or -1 if there is none
		 */
		public int getNumber() {
			return number;
		}

		/**
		 * Returns whether it is the model's first migration, which creates its
		 * tables.
		 *
		 * @return {@code true} if first
		 */
		public boolean isFirst() {
			return first;
		}

		/**
		 * Returns how many gaps the migration leaves to fill in.
		 *
		 * @return number of gaps
		 */
		public int getGaps() {
			return gaps;
		}

		/**
		 * Returns why the model has no migration.
		 *
		 * @return reason, or null
		 */
		public String getReason() {
			return reason;
		}

		@Override
		public String toString() {
			return model + ": " + status + (file == null ? "" : " " + file);
		}
	}

	private final List<ModelMigration> migrations;

	private final List<Finding> findings;

	private final String since;

	private final boolean written;

	/**
	 * Constructor.
	 *
	 * @param migrations each model's migration
	 * @param findings errors, and a warning for each gap
	 * @param since the earlier version of the models, or null if it wasn't read
	 * @param written whether the migrations were written
	 */
	public MigrationResult(List<ModelMigration> migrations, List<Finding> findings, String since, boolean written) {
		this.migrations = Collections.unmodifiableList(new ArrayList<>(migrations));
		this.findings = Collections.unmodifiableList(new ArrayList<>(findings));
		this.since = since;
		this.written = written;
	}

	/**
	 * Returns each of the project's models, in name order, and its migration.
	 *
	 * @return migrations
	 */
	public List<ModelMigration> getMigrations() {
		return migrations;
	}

	/**
	 * Returns the migrations that are, or would be, written.
	 *
	 * @return migrations
	 */
	public List<ModelMigration> getCreated() {
		List<ModelMigration> created = new ArrayList<>();
		for (ModelMigration migration : migrations) {
			if (migration.getStatus() == Status.CREATED) {
				created.add(migration);
			}
		}
		return created;
	}

	/**
	 * Returns the errors, which stop anything being written, and a warning for
	 * each gap that a migration leaves to fill in.
	 *
	 * @return findings
	 */
	public List<Finding> getFindings() {
		return findings;
	}

	/**
	 * Returns the earlier version of the models, such as
	 * {@code commit 1a2b3c4}.
	 *
	 * @return description, or null if it wasn't read
	 */
	public String getSince() {
		return since;
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
	 * Returns whether the migrations were written to the project.
	 *
	 * @return {@code true} if written
	 */
	public boolean isWritten() {
		return written;
	}
}

package net.logicsquad.eogen;

import java.util.Objects;

import net.logicsquad.eogen.model.AbstractEOArgument;
import net.logicsquad.eogen.model.EOAttribute;
import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOEntityIndex;
import net.logicsquad.eogen.model.EOFetchSpecification;
import net.logicsquad.eogen.model.EOModelObject;
import net.logicsquad.eogen.model.EOModelVerificationFailure;
import net.logicsquad.eogen.model.EORelationship;
import net.logicsquad.eogen.model.EOStoredProcedure;
import net.logicsquad.eogen.model.ModelLoadFailure;

/**
 * Something that eogen found wrong: an error, which stops generating, or a
 * warning. Its {@link #getMessage() message} is the text eogen prints, and
 * its {@link #getCode() code} names its kind, and keeps its meaning once it
 * is released, so that a script or a coding agent can act on a finding
 * without parsing the text. The other fields are null where eogen doesn't
 * know them.
 */
public final class Finding {
	/**
	 * Whether a finding stops generating.
	 */
	public enum Severity {
		/**
		 * Generating writes nothing, unless forced, and the run fails.
		 */
		ERROR,

		/**
		 * Reported, but generating goes ahead.
		 */
		WARNING
	}

	/**
	 * A model file that doesn't parse, with the line and column the parser
	 * stopped at.
	 */
	public static final String SYNTAX = "syntax";

	/**
	 * A model that fails to load for another reason.
	 */
	public static final String LOAD = "load";

	/**
	 * What Entity Modeler's own verifier finds, which it describes only in
	 * text.
	 */
	public static final String VERIFIER = "verifier";

	/**
	 * A key in a model file that the model classes don't read, such as a
	 * misspelt {@code prototypeNam}.
	 */
	public static final String UNKNOWN_KEY = "unknown-key";

	/**
	 * A name in {@code classProperties}, {@code primaryKeyAttributes} or
	 * {@code attributesUsedForLocking} that matches no attribute or
	 * relationship.
	 */
	public static final String UNMATCHED_NAME = "unmatched-name";

	/**
	 * A model file that {@code index.eomodeld} doesn't list, so that the model
	 * ignores it.
	 */
	public static final String UNLISTED_FILE = "unlisted-file";

	/**
	 * An entity that {@code index.eomodeld} lists, but that has no file.
	 */
	public static final String MISSING_FILE = "missing-file";

	/**
	 * An entity whose {@code name} isn't its file's.
	 */
	public static final String MISNAMED_ENTITY = "misnamed-entity";

	/**
	 * A key in a fetch specification or a restricting qualifier that matches
	 * nothing.
	 */
	public static final String UNMATCHED_KEY = "unmatched-key";

	/**
	 * A template reference that rendered with no value.
	 */
	public static final String UNRESOLVED_REFERENCE = "unresolved-reference";

	/**
	 * A model that formatting leaves alone, or a rename or a removal that is
	 * refused, since saving a file would change what it says.
	 */
	public static final String FORMAT_LOSS = "format-loss";

	/**
	 * An .eogen file that eogen can't use, such as one with no
	 * {@code -destination}.
	 */
	public static final String EOGEN_FILE = "eogen-file";

	/**
	 * A rename or a removal whose target names nothing.
	 */
	public static final String NOT_FOUND = "not-found";

	/**
	 * A rename to a name that something already has, such as another entity,
	 * or a file in the model's folder.
	 */
	public static final String NAME_TAKEN = "name-taken";

	/**
	 * A removal of something that something else uses.
	 */
	public static final String IN_USE = "in-use";

	/**
	 * A rename or a removal in a model that isn't one of the project's, such as
	 * a dependency's.
	 */
	public static final String IN_DEPENDENCY = "in-dependency";

	/**
	 * What a rename or a removal leaves to do by hand, such as renaming a
	 * subclass.
	 */
	public static final String BY_HAND = "by-hand";

	/**
	 * Anything else that stops a run, such as a file that can't be written.
	 */
	public static final String FAILURE = "failure";

	private final Severity severity;

	private final String code;

	private final String message;

	private final String file;

	private final Integer line;

	private final Integer column;

	private final String model;

	private final String entity;

	private final String object;

	private final String key;

	private final String suggestion;

	private final String template;

	private Finding(Builder builder) {
		this.severity = Objects.requireNonNull(builder.severity);
		this.code = Objects.requireNonNull(builder.code);
		this.message = Objects.requireNonNull(builder.message);
		this.file = builder.file;
		this.line = builder.line;
		this.column = builder.column;
		this.model = builder.model;
		this.entity = builder.entity;
		this.object = builder.object;
		this.key = builder.key;
		this.suggestion = builder.suggestion;
		this.template = builder.template;
	}

	/**
	 * Starts a finding.
	 *
	 * @param severity whether it is an error or a warning
	 * @param code its kind, such as {@link #UNKNOWN_KEY}
	 * @param message the text eogen prints
	 * @return a builder for the rest of its fields
	 */
	public static Builder builder(Severity severity, String code, String message) {
		Builder builder = new Builder();
		builder.severity = severity;
		builder.code = code;
		builder.message = message;
		return builder;
	}

	/**
	 * Starts an error.
	 *
	 * @param code its kind
	 * @param message the text eogen prints
	 * @return a builder for the rest of its fields
	 */
	public static Builder error(String code, String message) {
		return builder(Severity.ERROR, code, message);
	}

	/**
	 * Starts a finding from what loading, resolving or verifying a model group
	 * found: a model that failed to load, or what Entity Modeler's verifier
	 * reports, with the model and the object it is about. The file of a model
	 * that failed to load is absolute.
	 *
	 * @param failure what was found
	 * @return a builder for the rest of its fields
	 */
	public static Builder of(EOModelVerificationFailure failure) {
		Severity severity = failure.isWarning() ? Severity.WARNING : Severity.ERROR;
		Builder builder;
		if (failure instanceof ModelLoadFailure) {
			ModelLoadFailure loadFailure = (ModelLoadFailure) failure;
			builder = builder(severity, loadFailure.getLine() == null ? LOAD : SYNTAX, failure.getMessage());
			builder.file(loadFailure.getFile() == null ? null : loadFailure.getFile().getPath()).line(loadFailure.getLine()).column(loadFailure.getColumn());
		} else {
			builder = builder(severity, VERIFIER, failure.getMessage());
			describe(builder, failure.getFailedObject());
		}
		return builder.model(failure.getModel() == null ? null : failure.getModel().getName());
	}

	/**
	 * Names the entity and the object, such as {@code attribute price}, that a
	 * verification failure is about.
	 */
	private static void describe(Builder builder, EOModelObject<?> object) {
		if (object instanceof EOEntity) {
			builder.entity(((EOEntity) object).getName()).object("entity " + ((EOEntity) object).getName());
		} else if (object instanceof EOAttribute) {
			EOAttribute attribute = (EOAttribute) object;
			builder.entity(attribute.getEntity() == null ? null : attribute.getEntity().getName()).object("attribute " + attribute.getName());
		} else if (object instanceof EORelationship) {
			EORelationship relationship = (EORelationship) object;
			builder.entity(relationship.getEntity() == null ? null : relationship.getEntity().getName()).object("relationship " + relationship.getName());
		} else if (object instanceof EOFetchSpecification) {
			EOFetchSpecification fetchSpecification = (EOFetchSpecification) object;
			builder.entity(fetchSpecification.getEntity() == null ? null : fetchSpecification.getEntity().getName())
					.object("fetch specification " + fetchSpecification.getName());
		} else if (object instanceof EOEntityIndex) {
			EOEntityIndex index = (EOEntityIndex) object;
			builder.entity(index.getEntity() == null ? null : index.getEntity().getName()).object("index " + index.getName());
		} else if (object instanceof EOStoredProcedure) {
			builder.object("stored procedure " + ((EOStoredProcedure) object).getName());
		} else if (object instanceof AbstractEOArgument) {
			builder.object("argument " + ((AbstractEOArgument<?>) object).getName());
		}
	}

	/**
	 * Returns a builder with this finding's fields.
	 *
	 * @return builder
	 */
	public Builder toBuilder() {
		Builder builder = builder(severity, code, message);
		builder.file = file;
		builder.line = line;
		builder.column = column;
		builder.model = model;
		builder.entity = entity;
		builder.object = object;
		builder.key = key;
		builder.suggestion = suggestion;
		builder.template = template;
		return builder;
	}

	/**
	 * Returns whether this is an error or a warning.
	 *
	 * @return severity
	 */
	public Severity getSeverity() {
		return severity;
	}

	/**
	 * Returns whether this is an error.
	 *
	 * @return {@code true} if an error
	 */
	public boolean isError() {
		return severity == Severity.ERROR;
	}

	/**
	 * Returns the finding's kind, such as {@link #UNKNOWN_KEY}.
	 *
	 * @return code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Returns the text eogen prints for the finding.
	 *
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the file the finding is in, relative to the project and with
	 * {@code /} on every platform, or absolute if it is outside the project.
	 *
	 * @return file, or null
	 */
	public String getFile() {
		return file;
	}

	/**
	 * Returns the line in {@link #getFile()} that the finding is at.
	 *
	 * @return line, from 1, or null
	 */
	public Integer getLine() {
		return line;
	}

	/**
	 * Returns the column in {@link #getLine()} that the finding is at.
	 *
	 * @return column, from 1, or null
	 */
	public Integer getColumn() {
		return column;
	}

	/**
	 * Returns the name of the model the finding is in.
	 *
	 * @return model, or null
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Returns the name of the entity the finding is in.
	 *
	 * @return entity, or null
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * Returns what the finding is about, as eogen names it in messages, such as
	 * {@code attribute price} or {@code fetch specification byAuthor}.
	 *
	 * @return object, or null
	 */
	public String getObject() {
		return object;
	}

	/**
	 * Returns the key, name, key path or template reference that the finding is
	 * about, such as {@code prototypeNam} or {@code $attribute.javaClassName}.
	 *
	 * @return key, or null
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Returns what {@link #getKey()} was most likely meant to be.
	 *
	 * @return suggestion, or null
	 */
	public String getSuggestion() {
		return suggestion;
	}

	/**
	 * Returns the template that rendered a reference with no value.
	 *
	 * @return template, or null
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * Returns the message.
	 *
	 * @return message
	 */
	@Override
	public String toString() {
		return message;
	}

	/**
	 * Builds a {@link Finding}.
	 */
	public static final class Builder {
		private Severity severity;

		private String code;

		private String message;

		private String file;

		private Integer line;

		private Integer column;

		private String model;

		private String entity;

		private String object;

		private String key;

		private String suggestion;

		private String template;

		private Builder() {
		}

		/**
		 * Sets the severity.
		 *
		 * @param severity whether it is an error or a warning
		 * @return this
		 */
		public Builder severity(Severity severity) {
			this.severity = severity;
			return this;
		}

		/**
		 * Sets the file.
		 *
		 * @param file the file the finding is in
		 * @return this
		 */
		public Builder file(String file) {
			this.file = file;
			return this;
		}

		/**
		 * Sets the line.
		 *
		 * @param line from 1
		 * @return this
		 */
		public Builder line(Integer line) {
			this.line = line;
			return this;
		}

		/**
		 * Sets the column.
		 *
		 * @param column from 1
		 * @return this
		 */
		public Builder column(Integer column) {
			this.column = column;
			return this;
		}

		/**
		 * Sets the model.
		 *
		 * @param model its name
		 * @return this
		 */
		public Builder model(String model) {
			this.model = model;
			return this;
		}

		/**
		 * Sets the entity.
		 *
		 * @param entity its name
		 * @return this
		 */
		public Builder entity(String entity) {
			this.entity = entity;
			return this;
		}

		/**
		 * Sets what the finding is about.
		 *
		 * @param object such as {@code attribute price}
		 * @return this
		 */
		public Builder object(String object) {
			this.object = object;
			return this;
		}

		/**
		 * Sets the key.
		 *
		 * @param key such as {@code prototypeNam}
		 * @return this
		 */
		public Builder key(String key) {
			this.key = key;
			return this;
		}

		/**
		 * Sets the suggestion.
		 *
		 * @param suggestion what the key was most likely meant to be
		 * @return this
		 */
		public Builder suggestion(String suggestion) {
			this.suggestion = suggestion;
			return this;
		}

		/**
		 * Sets the template.
		 *
		 * @param template its name
		 * @return this
		 */
		public Builder template(String template) {
			this.template = template;
			return this;
		}

		/**
		 * Builds the finding.
		 *
		 * @return finding
		 */
		public Finding build() {
			return new Finding(this);
		}
	}
}

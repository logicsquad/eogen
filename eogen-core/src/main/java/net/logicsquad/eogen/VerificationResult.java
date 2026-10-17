package net.logicsquad.eogen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.logicsquad.eogen.model.EOModelVerificationFailure;

/**
 * What resolving and verifying a project's model group found.
 */
public final class VerificationResult {
	private final List<String> modelNames;

	private final List<EOModelVerificationFailure> failures;

	private final List<Finding> findings;

	/**
	 * Constructor, with the findings worked out from the failures.
	 *
	 * @param modelNames the names of the models verified
	 * @param failures the failures found
	 */
	public VerificationResult(List<String> modelNames, List<EOModelVerificationFailure> failures) {
		this(modelNames, failures, findings(failures));
	}

	/**
	 * Constructor.
	 *
	 * @param modelNames the names of the models verified
	 * @param failures the failures found
	 * @param findings the failures as findings, in the same order
	 */
	public VerificationResult(List<String> modelNames, List<EOModelVerificationFailure> failures, List<Finding> findings) {
		this.modelNames = Collections.unmodifiableList(new ArrayList<>(modelNames));
		this.failures = Collections.unmodifiableList(new ArrayList<>(failures));
		this.findings = Collections.unmodifiableList(new ArrayList<>(findings));
	}

	private static List<Finding> findings(List<EOModelVerificationFailure> failures) {
		List<Finding> findings = new ArrayList<>();
		for (EOModelVerificationFailure failure : failures) {
			findings.add(Finding.of(failure).build());
		}
		return findings;
	}

	/**
	 * Returns the names of the models whose failures are reported.
	 *
	 * @return model names, sorted
	 */
	public List<String> getModelNames() {
		return modelNames;
	}

	/**
	 * Returns every failure found, errors and warnings.
	 *
	 * @return failures, sorted by model and message
	 */
	public List<EOModelVerificationFailure> getFailures() {
		return failures;
	}

	/**
	 * Returns the failures that are errors.
	 *
	 * @return errors
	 */
	public List<EOModelVerificationFailure> getErrors() {
		List<EOModelVerificationFailure> errors = new ArrayList<>();
		for (EOModelVerificationFailure failure : failures) {
			if (!failure.isWarning()) {
				errors.add(failure);
			}
		}
		return errors;
	}

	/**
	 * Returns the failures that are warnings.
	 *
	 * @return warnings
	 */
	public List<EOModelVerificationFailure> getWarnings() {
		List<EOModelVerificationFailure> warnings = new ArrayList<>();
		for (EOModelVerificationFailure failure : failures) {
			if (failure.isWarning()) {
				warnings.add(failure);
			}
		}
		return warnings;
	}

	/**
	 * Returns whether any failure is an error.
	 *
	 * @return {@code true} if there are errors
	 */
	public boolean hasErrors() {
		return !getErrors().isEmpty();
	}

	/**
	 * Returns every failure as a finding, with its code and what eogen knows of
	 * where it is, in the order of {@link #getFailures()}.
	 *
	 * @return findings
	 */
	public List<Finding> getFindings() {
		return findings;
	}
}

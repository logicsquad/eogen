package net.logicsquad.eogen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thrown when an .eogen file can't be read, or its models can't be loaded or
 * generated.
 */
public class EOGeneratorException extends Exception {
	private static final long serialVersionUID = 1L;

	private final transient List<Finding> findings;

	/**
	 * Constructor.
	 *
	 * @param message what failed
	 */
	public EOGeneratorException(String message) {
		this(message, null, Finding.FAILURE);
	}

	/**
	 * Constructor.
	 *
	 * @param message what failed
	 * @param cause why
	 */
	public EOGeneratorException(String message, Throwable cause) {
		this(message, cause, Finding.FAILURE);
	}

	/**
	 * Constructor, for a failure with a code.
	 *
	 * @param message what failed
	 * @param cause why, or null
	 * @param code its kind, such as {@link Finding#EOGEN_FILE}
	 */
	public EOGeneratorException(String message, Throwable cause, String code) {
		this(message, cause, Collections.singletonList(Finding.error(code, message).build()));
	}

	/**
	 * Constructor, for a failure that findings describe.
	 *
	 * @param message what failed
	 * @param cause why, or null
	 * @param findings what was found, which must not be empty
	 */
	public EOGeneratorException(String message, Throwable cause, List<Finding> findings) {
		super(message, cause);
		this.findings = Collections.unmodifiableList(new ArrayList<>(findings));
	}

	/**
	 * Returns what failed, as findings.
	 *
	 * @return findings, at least one
	 */
	public List<Finding> getFindings() {
		return findings == null ? Collections.singletonList(Finding.error(Finding.FAILURE, String.valueOf(getMessage())).build()) : findings;
	}
}

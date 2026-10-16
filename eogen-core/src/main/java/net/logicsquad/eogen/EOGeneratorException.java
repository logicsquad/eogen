package net.logicsquad.eogen;

/**
 * Thrown when an .eogen file can't be read, or its models can't be loaded or
 * generated.
 */
public class EOGeneratorException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param message detail message
	 */
	public EOGeneratorException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message detail message
	 * @param cause cause
	 */
	public EOGeneratorException(String message, Throwable cause) {
		super(message, cause);
	}
}

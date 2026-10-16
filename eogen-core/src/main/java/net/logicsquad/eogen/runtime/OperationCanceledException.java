package net.logicsquad.eogen.runtime;

/**
 * Thrown when an operation stops because its {@link IProgressMonitor} was
 * cancelled. It replaces Eclipse's
 * {@code org.eclipse.core.runtime.OperationCanceledException}.
 */
public class OperationCanceledException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param message detail message
	 */
	public OperationCanceledException(String message) {
		super(message);
	}
}

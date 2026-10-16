package net.logicsquad.eogen.runtime;

/**
 * Receives progress from long-running operations, and lets the caller
 * cancel them. It replaces Eclipse's
 * {@code org.eclipse.core.runtime.IProgressMonitor} in the code extracted
 * from WOLips, and has only the methods that code calls.
 */
public interface IProgressMonitor {
	/**
	 * Names the task in progress.
	 *
	 * @param name the task's name
	 */
	void setTaskName(String name);

	/**
	 * Returns whether the caller has asked for the operation to stop.
	 *
	 * @return {@code true} if the operation should stop
	 */
	boolean isCanceled();
}

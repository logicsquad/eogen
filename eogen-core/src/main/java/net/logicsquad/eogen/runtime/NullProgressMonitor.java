package net.logicsquad.eogen.runtime;

/**
 * An {@link IProgressMonitor} that ignores progress and never cancels.
 */
public class NullProgressMonitor implements IProgressMonitor {
	@Override
	public void setTaskName(String name) {
		// Ignored
	}

	@Override
	public boolean isCanceled() {
		return false;
	}
}

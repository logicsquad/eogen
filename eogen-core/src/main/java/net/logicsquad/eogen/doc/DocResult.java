package net.logicsquad.eogen.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.logicsquad.eogen.Finding;

/**
 * What documenting the models did: the models documented, the pages written,
 * or that would have been written, with whether each is new, changed or
 * unchanged, the orphans, the project's own files that it kept, and the
 * findings, which are errors.
 */
public final class DocResult {
	/**
	 * How a page compares with the file it is written to.
	 */
	public enum Status {
		/**
		 * The file doesn't exist.
		 */
		CREATED,

		/**
		 * The file differs, other than in its line endings.
		 */
		UPDATED,

		/**
		 * The file is the same.
		 */
		UNCHANGED
	}

	/**
	 * A page, and how it compares with its file.
	 */
	public static final class Page {
		private final File file;

		private final Status status;

		/**
		 * Constructor.
		 *
		 * @param file the file
		 * @param status how it compares
		 */
		public Page(File file, Status status) {
			this.file = file;
			this.status = status;
		}

		/**
		 * Returns the file.
		 *
		 * @return file
		 */
		public File getFile() {
			return file;
		}

		/**
		 * Returns how the page compares with its file.
		 *
		 * @return status
		 */
		public Status getStatus() {
			return status;
		}

		/**
		 * Returns whether writing the page changes its file.
		 *
		 * @return {@code true} if the file is missing or differs
		 */
		public boolean isStale() {
			return status != Status.UNCHANGED;
		}

		@Override
		public String toString() {
			return file + " (" + status + ")";
		}
	}

	private final List<String> models;

	private final List<Page> pages;

	private final List<File> orphans;

	private final List<File> kept;

	private final List<Finding> findings;

	private final boolean written;

	/**
	 * Constructor.
	 *
	 * @param models the names of the models documented
	 * @param pages the pages
	 * @param orphans Markdown pages that eogen wrote for models that it no
	 *        longer documents
	 * @param kept the project's own files where a page would go, which are
	 *        left alone
	 * @param findings errors
	 * @param written whether the pages were written
	 */
	public DocResult(List<String> models, List<Page> pages, List<File> orphans, List<File> kept, List<Finding> findings, boolean written) {
		this.models = Collections.unmodifiableList(new ArrayList<>(models));
		this.pages = Collections.unmodifiableList(new ArrayList<>(pages));
		this.orphans = Collections.unmodifiableList(new ArrayList<>(orphans));
		this.kept = Collections.unmodifiableList(new ArrayList<>(kept));
		this.findings = Collections.unmodifiableList(new ArrayList<>(findings));
		this.written = written;
	}

	/**
	 * Returns the names of the models documented, in name order.
	 *
	 * @return model names
	 */
	public List<String> getModels() {
		return models;
	}

	/**
	 * Returns the pages, the index first.
	 *
	 * @return pages
	 */
	public List<Page> getPages() {
		return pages;
	}

	/**
	 * Returns the pages' files, the index first.
	 *
	 * @return files
	 */
	public List<File> getFiles() {
		List<File> files = new ArrayList<>();
		for (Page page : pages) {
			files.add(page.getFile());
		}
		return files;
	}

	/**
	 * Returns the pages whose files are missing or differ.
	 *
	 * @return pages
	 */
	public List<Page> getStalePages() {
		List<Page> stale = new ArrayList<>();
		for (Page page : pages) {
			if (page.isStale()) {
				stale.add(page);
			}
		}
		return stale;
	}

	/**
	 * Returns the Markdown pages in the folder that eogen wrote for models that
	 * it no longer documents, which writing the pages deletes.
	 *
	 * @return files
	 */
	public List<File> getOrphans() {
		return orphans;
	}

	/**
	 * Returns the project's own files where a page would go, which are left
	 * alone, and so have no page: a {@code README.md} that eogen didn't write
	 * in the folder of the Markdown pages, where their index would go.
	 *
	 * @return files
	 */
	public List<File> getKept() {
		return kept;
	}

	/**
	 * Returns whether writing the pages changes the folder: whether a page is
	 * missing or differs, or there are orphans.
	 *
	 * @return {@code true} if it does
	 */
	public boolean isStale() {
		return !getStalePages().isEmpty() || !orphans.isEmpty();
	}

	/**
	 * Returns the errors: those that loading the model group finds, and each
	 * reference that a template renders with no value.
	 *
	 * @return findings
	 */
	public List<Finding> getFindings() {
		return findings;
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
	 * Returns whether the pages were written, and the orphans deleted: they are
	 * when asked, unless there are errors, or when forced.
	 *
	 * @return {@code true} if written
	 */
	public boolean isWritten() {
		return written;
	}
}

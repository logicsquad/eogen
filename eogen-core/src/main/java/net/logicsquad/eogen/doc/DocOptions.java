package net.logicsquad.eogen.doc;

import java.io.File;

/**
 * Where documenting the models writes its pages, and what with.
 */
public final class DocOptions {
	/**
	 * What the pages are written in.
	 */
	public enum Format {
		/**
		 * EOModelDoc's HTML pages, for a project's Maven site.
		 */
		HTML,

		/**
		 * A Markdown page for each model, with a Mermaid diagram, to commit beside
		 * the code.
		 */
		MARKDOWN
	}

	/**
	 * The folder of the HTML pages by default, relative to the project, so that
	 * a project's Maven site includes them.
	 */
	public static final String DEFAULT_OUTPUT = "target/site/eomodeldoc";

	/**
	 * The folder of the Markdown pages by default, relative to the project.
	 */
	public static final String DEFAULT_MARKDOWN_OUTPUT = "docs/models";

	private Format format = Format.MARKDOWN;

	private File output;

	private File templateFolder;

	private String entityURL;

	private boolean includeDependencies;

	/**
	 * Returns what the pages are written in.
	 *
	 * @return format
	 */
	public Format getFormat() {
		return format;
	}

	/**
	 * Sets what the pages are written in. The default is Markdown.
	 *
	 * @param format format, or null for Markdown
	 */
	public void setFormat(Format format) {
		this.format = format == null ? Format.MARKDOWN : format;
	}

	/**
	 * Returns the folder of the pages.
	 *
	 * @return folder
	 */
	public File getOutput() {
		if (output != null) {
			return output;
		}
		return new File(format == Format.MARKDOWN ? DEFAULT_MARKDOWN_OUTPUT : DEFAULT_OUTPUT);
	}

	/**
	 * Sets the folder of the pages, relative to the project if it isn't
	 * absolute. The default is {@link #DEFAULT_OUTPUT} for HTML, and
	 * {@link #DEFAULT_MARKDOWN_OUTPUT} for Markdown.
	 *
	 * @param output folder, or null for the default
	 */
	public void setOutput(File output) {
		this.output = output;
	}

	/**
	 * Returns the folder of extra templates.
	 *
	 * @return folder, or null
	 */
	public File getTemplateFolder() {
		return templateFolder;
	}

	/**
	 * Sets a folder of extra templates, relative to the project if it isn't
	 * absolute, which come before the bundled ones: a page's template, such as
	 * {@code entityContent.html.vm}, or a file that is copied, such as
	 * {@code eomodeldoc.css}.
	 *
	 * @param templateFolder folder, or null
	 */
	public void setTemplateFolder(File templateFolder) {
		this.templateFolder = templateFolder;
	}

	/**
	 * Returns the template for the link from an entity's class name.
	 *
	 * @return template, or null
	 */
	public String getEntityURL() {
		return entityURL;
	}

	/**
	 * Sets a Velocity template for the link from an entity's class name, such
	 * as to its Javadoc, which sees {@code $entity} and {@code $model}. A link
	 * is relative to the entity's page, which is
	 * {@code <Model>/entities/<Entity>.html} in the folder of the pages. The
	 * default is no link.
	 *
	 * @param entityURL template, or null
	 */
	public void setEntityURL(String entityURL) {
		this.entityURL = entityURL;
	}

	/**
	 * Returns whether the dependencies' models are documented too.
	 *
	 * @return {@code true} if they are
	 */
	public boolean isIncludeDependencies() {
		return includeDependencies;
	}

	/**
	 * Sets whether to document the models of the dependencies, such as a
	 * framework's, as well as the project's own. The default is {@code false}.
	 *
	 * @param includeDependencies whether to document them
	 */
	public void setIncludeDependencies(boolean includeDependencies) {
		this.includeDependencies = includeDependencies;
	}
}

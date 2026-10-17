package net.logicsquad.eogen.migration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * What generating migrations compares, and where it writes them.
 */
public final class MigrationOptions {
	/**
	 * The template that a migration is rendered from by default, which eogen
	 * bundles.
	 */
	public static final String DEFAULT_TEMPLATE = "Migration.java";

	/**
	 * The folder of a Maven project's Java sources, relative to the project.
	 */
	public static final String DEFAULT_SOURCE_FOLDER = "src/main/java";

	private String from = EarlierModels.DEFAULT_VERSION;

	private List<File> sourceFolders = Collections.emptyList();

	private String packageName;

	private String template = DEFAULT_TEMPLATE;

	private List<String> models = Collections.emptyList();

	/**
	 * Returns the earlier version of the models.
	 *
	 * @return a commit, or a folder
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * Sets the earlier version of the models, which a migration changes the
	 * tables from: a git commit, such as a branch, a tag or {@code HEAD~2}, or
	 * a folder of models, relative to the project. The default is
	 * {@code HEAD}, the last commit, so that a migration is for the changes
	 * that aren't committed yet.
	 *
	 * @param from a commit, or a folder
	 */
	public void setFrom(String from) {
		this.from = from == null || from.trim().isEmpty() ? EarlierModels.DEFAULT_VERSION : from;
	}

	/**
	 * Returns the folders of the project's Java sources.
	 *
	 * @return source folders, or empty for the default
	 */
	public List<File> getSourceFolders() {
		return sourceFolders;
	}

	/**
	 * Sets the folders of the project's Java sources, where its migrations are,
	 * and where a new one goes: beside the model's latest migration, or else in
	 * the first folder. The default is {@code src/main/java}.
	 *
	 * @param sourceFolders source folders
	 */
	public void setSourceFolders(List<File> sourceFolders) {
		this.sourceFolders = Collections.unmodifiableList(new ArrayList<>(sourceFolders));
	}

	/**
	 * Returns the package for new migrations.
	 *
	 * @return package name, or null
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Sets the package for new migrations, unless a model's
	 * {@code MigrationClassPrefix} names one. By default, a migration goes in
	 * the package of the model's latest migration, and a model's first goes
	 * beside most of the other models' latest migrations, or else in the
	 * package of most of its entity classes.
	 *
	 * @param packageName package name, or null
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * Returns the template.
	 *
	 * @return template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * Sets the template a migration is rendered from: a path relative to the
	 * project, or the name of a template among the shared templates, or of the
	 * bundled one, {@link #DEFAULT_TEMPLATE}.
	 *
	 * @param template template
	 */
	public void setTemplate(String template) {
		this.template = template == null || template.trim().isEmpty() ? DEFAULT_TEMPLATE : template;
	}

	/**
	 * Returns the models to write migrations for.
	 *
	 * @return model names, or empty for every model
	 */
	public List<String> getModels() {
		return models;
	}

	/**
	 * Sets the models to write migrations for. By default, it is every one of
	 * the project's models whose tables changed. A model named here that has no
	 * migrations yet gets its first, which creates its tables, even if they
	 * didn't change.
	 *
	 * @param models model names
	 */
	public void setModels(List<String> models) {
		this.models = Collections.unmodifiableList(new ArrayList<>(models));
	}
}

package net.logicsquad.eogen.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import net.logicsquad.eogen.EOGenerator;
import net.logicsquad.eogen.EOGeneratorException;
import net.logicsquad.eogen.Finding;
import net.logicsquad.eogen.migration.MigrationOptions;
import net.logicsquad.eogen.migration.MigrationResult;
import net.logicsquad.eogen.migration.MigrationResult.ModelMigration;

/**
 * Writes the next Project Wonder migration for each of the project's models
 * whose tables changed since an earlier version of the models: by default
 * the last commit, so that the migration is for the changes not yet
 * committed. A migration is named as {@code ERXMigrator} looks for it, from
 * the model's {@code MigrationClassPrefix} and the number after its highest
 * migration, and goes beside its latest one. A model's first migration
 * creates its tables.
 * <p>
 * Where it can't decide, such as on the value that a new column that doesn't
 * allow null needs in the rows a table already has, a migration has a gap, a
 * {@code // TODO eogen:} comment and a line that doesn't compile, which the
 * goal reports as a warning. Nothing changes an existing migration.
 * </p>
 */
@Mojo(name = "migration", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class MigrationMojo extends AbstractEOGenMojo {
	/**
	 * The earlier version of the models: a git commit, such as {@code HEAD}, a
	 * branch or a tag, or a folder of models, relative to the project.
	 */
	@Parameter(property = "eogen.migration.from", defaultValue = "HEAD")
	private String from;

	/**
	 * The package for new migrations, unless a model's
	 * {@code MigrationClassPrefix} names one. The default is that of the
	 * model's latest migration, and for a model's first, that of most of the
	 * other models' latest migrations, or else that of most of its entity
	 * classes.
	 */
	@Parameter(property = "eogen.migration.package")
	private String packageName;

	/**
	 * The template a migration is rendered from: a path relative to the
	 * project, or the name of a shared template or of the bundled one, which
	 * extends {@code ERXMigrationDatabase.Migration}.
	 */
	@Parameter(property = "eogen.migration.template", defaultValue = MigrationOptions.DEFAULT_TEMPLATE)
	private String template;

	/**
	 * The models to write migrations for. The default is every one of the
	 * project's models whose tables changed. A model named here that has no
	 * migrations yet gets its first, which creates its tables, even if they
	 * didn't change.
	 */
	@Parameter(property = "eogen.migration.models")
	private List<String> models;

	/**
	 * Writes nothing, and only reports what would be written.
	 */
	@Parameter(property = "eogen.dryRun", defaultValue = "false")
	private boolean dryRun;

	@Override
	protected void execute(EOGenerator generator) throws MojoExecutionException, MojoFailureException {
		MigrationOptions options = new MigrationOptions();
		options.setFrom(from);
		options.setPackageName(packageName);
		options.setTemplate(template);
		options.setModels(models == null ? Collections.<String>emptyList() : models);
		List<File> sourceFolders = new ArrayList<>();
		for (String root : project.getCompileSourceRoots()) {
			sourceFolders.add(new File(root));
		}
		options.setSourceFolders(sourceFolders);
		MigrationResult result;
		try {
			result = generator.generateMigrations(options, !dryRun);
		} catch (EOGeneratorException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		for (Finding finding : result.getFindings()) {
			if (finding.isError()) {
				getLog().error(finding.getMessage());
			}
		}
		if (result.hasErrors()) {
			throw new MojoFailureException("There are errors, so no migration was written.");
		}
		int created = 0;
		for (ModelMigration migration : result.getMigrations()) {
			if (migration.getStatus() == MigrationResult.Status.CREATED) {
				created++;
				int gaps = migration.getGaps();
				getLog().info((dryRun ? "Would create " : "Created ") + relative(migration.getFile()) + (gaps == 0 ? "." : ", with " + gaps + (gaps == 1 ? " gap" : " gaps") + " to fill in."));
			} else {
				getLog().info(migration.getModel() + ": " + migration.getReason());
			}
		}
		for (Finding finding : result.getFindings()) {
			if (!finding.isError()) {
				getLog().warn(finding.getMessage());
			}
		}
		getLog().info((dryRun ? "Would write " : "Wrote ") + created + (created == 1 ? " migration" : " migrations") + " for the changes since " + result.getSince() + ".");
	}
}

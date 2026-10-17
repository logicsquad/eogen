package net.logicsquad.eogen.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import net.logicsquad.eogen.EOGenerator;
import net.logicsquad.eogen.GenerationResult;

/**
 * What the eogen goals share: the project's model folders, its .eogen files,
 * and the classpath whose jars add their models to the model group.
 */
public abstract class AbstractEOGenMojo extends AbstractMojo {
	/**
	 * Scopes whose dependencies are on the compile classpath.
	 */
	private static final Set<String> COMPILE_SCOPES = new HashSet<>(Arrays.asList(Artifact.SCOPE_COMPILE, Artifact.SCOPE_PROVIDED, Artifact.SCOPE_SYSTEM));

	/**
	 * The project.
	 */
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	protected MavenProject project;

	/**
	 * Folders whose models make up the project's model group, along with the
	 * models in its dependencies. The default is {@code src/main/resources},
	 * where a WebObjects Maven project keeps its models, whatever the POM's
	 * resources say.
	 */
	@Parameter(property = "eogen.modelDirectories")
	private List<File> modelDirectories;

	/**
	 * The .eogen files to generate from. The default is every .eogen file under
	 * the model directories.
	 */
	@Parameter(property = "eogen.eogenFiles")
	private List<File> eogenFiles;

	/**
	 * Skips the goal.
	 */
	@Parameter(property = "eogen.skip", defaultValue = "false")
	private boolean skip;

	/**
	 * Generates even when the model group or a template has errors, as WOLips
	 * does. Without it, errors fail the build, and {@code generate} writes
	 * nothing for an .eogen file that has them.
	 */
	@Parameter(property = "eogen.force", defaultValue = "false")
	private boolean force;

	@Override
	public final void execute() throws MojoExecutionException, MojoFailureException {
		if (skip) {
			getLog().info("Skipping eogen.");
			return;
		}
		EOGenerator generator = new EOGenerator(project.getBasedir(), modelDirectories(), classpath());
		generator.setForce(force);
		execute(generator);
	}

	/**
	 * Returns whether the goal generates despite errors.
	 *
	 * @return {@code true} if forced
	 */
	protected boolean isForce() {
		return force;
	}

	/**
	 * Logs an .eogen file's errors: as errors, or as warnings when forced.
	 *
	 * @param eogenFile the .eogen file
	 * @param result what generating or checking it found
	 * @return the number of errors
	 */
	protected int logErrors(File eogenFile, GenerationResult result) {
		for (String error : result.getErrors()) {
			if (force) {
				getLog().warn(relative(eogenFile) + ": " + error);
			} else {
				getLog().error(relative(eogenFile) + ": " + error);
			}
		}
		return result.getErrors().size();
	}

	/**
	 * Runs the goal.
	 *
	 * @param generator a generator for the project
	 * @throws MojoExecutionException if the goal can't run
	 * @throws MojoFailureException if the goal finds a problem in the project
	 */
	protected abstract void execute(EOGenerator generator) throws MojoExecutionException, MojoFailureException;

	/**
	 * Returns the folders whose models make up the project's model group.
	 *
	 * @return model folders
	 */
	protected List<File> modelDirectories() {
		if (modelDirectories == null || modelDirectories.isEmpty()) {
			return Collections.singletonList(new File(project.getBasedir(), EOGenerator.DEFAULT_MODEL_FOLDER));
		}
		List<File> folders = new ArrayList<>();
		for (File folder : modelDirectories) {
			folders.add(resolve(folder));
		}
		return folders;
	}

	/**
	 * Returns the .eogen files to generate from.
	 *
	 * @return .eogen files
	 * @throws MojoExecutionException if a configured file does not exist, or a
	 *         model folder can't be searched
	 */
	protected List<File> eogenFiles() throws MojoExecutionException {
		if (eogenFiles != null && !eogenFiles.isEmpty()) {
			List<File> files = new ArrayList<>();
			for (File file : eogenFiles) {
				File resolved = resolve(file);
				if (!resolved.isFile()) {
					throw new MojoExecutionException("The .eogen file " + resolved + " does not exist.");
				}
				files.add(resolved);
			}
			return files;
		}
		try {
			List<File> files = EOGenerator.findEOGenFiles(modelDirectories());
			if (files.isEmpty()) {
				getLog().warn("No .eogen files in " + modelDirectories() + ".");
			}
			return files;
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to find the .eogen files: " + e.getMessage(), e);
		}
	}

	/**
	 * Returns the jars and folders of the project's compile, provided and system
	 * dependencies, in the order Maven resolved them.
	 *
	 * @return classpath
	 */
	protected List<File> classpath() {
		List<File> classpath = new ArrayList<>();
		for (Artifact artifact : project.getArtifacts()) {
			if (COMPILE_SCOPES.contains(artifact.getScope()) && artifact.getFile() != null) {
				classpath.add(artifact.getFile());
			}
		}
		return classpath;
	}

	/**
	 * Returns a file's path relative to the project, for messages.
	 *
	 * @param file a file
	 * @return relative path, or the absolute path of a file outside the project
	 */
	protected String relative(File file) {
		File basedir = project.getBasedir().getAbsoluteFile();
		if (file.getAbsolutePath().startsWith(basedir.getPath() + File.separator)) {
			return basedir.toPath().relativize(file.getAbsoluteFile().toPath()).toString();
		}
		return file.getPath();
	}

	private File resolve(File file) {
		return file.isAbsolute() ? file : new File(project.getBasedir(), file.getPath());
	}
}

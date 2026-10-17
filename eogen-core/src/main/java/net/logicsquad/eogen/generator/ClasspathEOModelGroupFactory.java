package net.logicsquad.eogen.generator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.logicsquad.eogen.model.EOModelException;
import net.logicsquad.eogen.model.EOModelGroup;
import net.logicsquad.eogen.model.EOModelVerificationFailure;
import net.logicsquad.eogen.model.IEOModelGroupFactory;
import net.logicsquad.eogen.runtime.IProgressMonitor;
import net.logicsquad.eogen.utils.URLUtils;

/**
 * Loads a model group from a project's model folders and its classpath. It
 * replaces the JDT and m2e classpath lookup that WOLips's
 * {@code EclipseEOModelGroupFactory} performs for {@code -loadModelGroup}, and
 * searches the same places in the same order:
 * <ol>
 * <li>every {@code .eomodeld} under each model folder, in name order, such as a
 * Maven project's {@code src/main/resources};</li>
 * <li>for each classpath entry in turn, the models in {@code Resources/} and
 * {@code META-INF/webobjects/Resources/}, whether the entry is a jar or a
 * folder;</li>
 * <li>last, the model the group is loaded for, in case it is somewhere
 * else.</li>
 * </ol>
 * When two models have the same name, the first one found wins. When a model
 * declares an entity that an earlier model declares, EOModelGroup removes the
 * earlier model, so loading the model the group is for last makes sure it
 * survives a dependency's model that shares one of its entities.
 */
public class ClasspathEOModelGroupFactory implements IEOModelGroupFactory {
	/**
	 * The folders inside a classpath entry that hold a framework's models.
	 */
	private static final String[] RESOURCE_FOLDERS = { "Resources", "META-INF/webobjects/Resources" };

	private final List<File> _modelFolders;

	private final List<File> _classpath;

	/**
	 * Constructor.
	 *
	 * @param modelFolders folders to search for models, at any depth
	 * @param classpath jars and folders whose resource folders to search
	 */
	public ClasspathEOModelGroupFactory(List<File> modelFolders, List<File> classpath) {
		_modelFolders = Collections.unmodifiableList(new ArrayList<File>(modelFolders));
		_classpath = Collections.unmodifiableList(new ArrayList<File>(classpath));
	}

	/**
	 * Returns the folders searched for models.
	 *
	 * @return model folders
	 */
	public List<File> getModelFolders() {
		return _modelFolders;
	}

	/**
	 * Returns the classpath entries searched for models.
	 *
	 * @return classpath entries
	 */
	public List<File> getClasspath() {
		return _classpath;
	}

	@Override
	public boolean loadModelGroup(Object modelGroupResource, EOModelGroup modelGroup, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws EOModelException {
		int previousModelCount = modelGroup.getModels().size();
		try {
			for (File modelFolder : _modelFolders) {
				loadModelsUnder(modelFolder, modelGroup, failures, skipOnDuplicates, progressMonitor);
			}
			for (File entry : _classpath) {
				if (entry.isDirectory()) {
					for (String folder : RESOURCE_FOLDERS) {
						loadModelsIn(new File(entry, folder), modelGroup, failures, skipOnDuplicates, progressMonitor);
					}
				} else if (entry.isFile() && entry.getName().endsWith(".jar")) {
					for (String folder : RESOURCE_FOLDERS) {
						URL url = new URL("jar:" + entry.toURI().toURL() + "!/" + folder);
						if (URLUtils.exists(url)) {
							modelGroup.loadModelsFromURL(url, 1, failures, skipOnDuplicates, progressMonitor);
						}
					}
				}
			}
			File modelFile = toFile(modelGroupResource);
			if (modelFile != null && EOModelGroup.getModelNameForFile(modelFile) != null) {
				modelGroup.loadModelFromURL(modelFile.toURI().toURL(), failures, skipOnDuplicates, progressMonitor);
			}
		} catch (IOException e) {
			throw new EOModelException("Failed to load the model group.", e);
		}
		return modelGroup.getModels().size() - previousModelCount > 1;
	}

	/**
	 * Loads every model under a folder, in the order of
	 * {@link #findModelFolders(File)}.
	 */
	private static void loadModelsUnder(File folder, EOModelGroup modelGroup, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws IOException, EOModelException {
		for (File model : findModelFolders(folder)) {
			modelGroup.loadModelFromURL(model.toURI().toURL(), failures, skipOnDuplicates, progressMonitor);
		}
	}

	/**
	 * Returns every {@code .eomodeld} under a folder, at any depth. Like
	 * {@link EOModelGroup#loadModelsFromURL(URL, int, Set, boolean, IProgressMonitor)},
	 * it skips hidden folders, {@code .wo} folders and folders named
	 * {@code Java}, but it visits folders in name order, so the result does not
	 * depend on the file system.
	 *
	 * @param folder a folder
	 * @return model folders, in name order, depth first
	 */
	public static List<File> findModelFolders(File folder) {
		List<File> models = new ArrayList<File>();
		findModelFolders(folder, models);
		return models;
	}

	private static void findModelFolders(File folder, List<File> models) {
		File[] children = folder.listFiles();
		if (children == null) {
			return;
		}
		Arrays.sort(children);
		for (File child : children) {
			String name = child.getName();
			if (!child.isDirectory() || child.isHidden() || name.startsWith(".")) {
				continue;
			}
			if (name.endsWith(".eomodeld")) {
				models.add(child);
			} else if (!name.endsWith(".wo") && !name.equals("Java")) {
				findModelFolders(child, models);
			}
		}
	}

	/**
	 * Loads the models directly in a folder, as
	 * {@link EOModelGroup#loadModelsFromURL(URL, int, Set, boolean, IProgressMonitor)}
	 * does with a depth of 1. That method can't read a file URL whose path has
	 * an escaped character, such as a space.
	 */
	private static void loadModelsIn(File folder, EOModelGroup modelGroup, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws IOException, EOModelException {
		File[] children = folder.listFiles();
		if (children == null) {
			return;
		}
		Arrays.sort(children);
		for (File child : children) {
			if (child.isDirectory() && !child.isHidden() && child.getName().endsWith(".eomodeld")) {
				modelGroup.loadModelFromURL(child.toURI().toURL(), failures, skipOnDuplicates, progressMonitor);
			}
		}
	}

	private static File toFile(Object modelGroupResource) {
		File file;
		if (modelGroupResource instanceof File) {
			file = (File) modelGroupResource;
		} else if (modelGroupResource instanceof Path) {
			file = ((Path) modelGroupResource).toFile();
		} else if (modelGroupResource instanceof URL && "file".equals(((URL) modelGroupResource).getProtocol())) {
			file = URLUtils.cheatAndTurnIntoFile((URL) modelGroupResource);
		} else if (modelGroupResource instanceof URI && "file".equals(((URI) modelGroupResource).getScheme())) {
			file = new File((URI) modelGroupResource);
		} else {
			file = null;
		}
		return file;
	}
}

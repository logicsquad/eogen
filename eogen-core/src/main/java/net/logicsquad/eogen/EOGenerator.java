package net.logicsquad.eogen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.logicsquad.eogen.GeneratedFile.Kind;
import net.logicsquad.eogen.GeneratedFile.Status;
import net.logicsquad.eogen.generator.ClasspathEOModelGroupFactory;
import net.logicsquad.eogen.generator.EOGeneratorModel;
import net.logicsquad.eogen.generator.EOGeneratorResourceLoader;
import net.logicsquad.eogen.generator.EOModelReference;
import net.logicsquad.eogen.generator.VelocityEOGeneratorRunner;
import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EOModelException;
import net.logicsquad.eogen.model.EOModelGroup;
import net.logicsquad.eogen.model.EOModelVerificationFailure;
import net.logicsquad.eogen.runtime.NullProgressMonitor;
import net.logicsquad.eogen.utils.URLUtils;

/**
 * Generates EOF entity classes from a project's .eogen files without Eclipse,
 * with the Velocity EOGenerator extracted from WOLips.
 * <p>
 * An .eogen file names its models, templates and destinations relative to
 * the project folder. For {@code -loadModelGroup}, the model group comes from
 * the project's model folders and the {@code Resources} folders of its
 * classpath, as {@link ClasspathEOModelGroupFactory} describes. Generating
 * writes every superclass, and only those subclasses that are missing, so that
 * subclasses can be edited by hand.
 * </p>
 */
public class EOGenerator {
	/**
	 * The model folder of a Maven project, relative to the project folder.
	 */
	public static final String DEFAULT_MODEL_FOLDER = "src/main/resources";

	private final File projectDirectory;

	private final List<File> modelFolders;

	private final List<File> classpath;

	/**
	 * Constructor for a Maven project with no dependencies: models come from
	 * {@code src/main/resources}.
	 *
	 * @param projectDirectory the project folder
	 */
	public EOGenerator(File projectDirectory) {
		this(projectDirectory, Collections.singletonList(new File(projectDirectory, DEFAULT_MODEL_FOLDER)), Collections.<File>emptyList());
	}

	/**
	 * Constructor.
	 *
	 * @param projectDirectory the project folder
	 * @param modelFolders folders whose models make up the project's model group
	 * @param classpath jars and folders whose {@code Resources} folders add their
	 *        models to the model group, in classpath order
	 */
	public EOGenerator(File projectDirectory, List<File> modelFolders, List<File> classpath) {
		this.projectDirectory = absolute(projectDirectory);
		this.modelFolders = Collections.unmodifiableList(modelFolders.stream().map(EOGenerator::absolute).collect(Collectors.toList()));
		this.classpath = Collections.unmodifiableList(classpath.stream().map(EOGenerator::absolute).collect(Collectors.toList()));
	}

	/**
	 * Returns the project folder.
	 *
	 * @return project folder
	 */
	public File getProjectDirectory() {
		return projectDirectory;
	}

	/**
	 * Returns the folders whose models make up the project's model group.
	 *
	 * @return model folders
	 */
	public List<File> getModelFolders() {
		return modelFolders;
	}

	/**
	 * Returns the jars and folders whose models are added to the model group.
	 *
	 * @return classpath
	 */
	public List<File> getClasspath() {
		return classpath;
	}

	/**
	 * Returns the .eogen files under some folders, at any depth.
	 *
	 * @param folders folders to search
	 * @return .eogen files, sorted by path
	 * @throws IOException if a folder can't be read
	 */
	public static List<File> findEOGenFiles(List<File> folders) throws IOException {
		Set<File> eogenFiles = new LinkedHashSet<>();
		for (File folder : folders) {
			if (!folder.isDirectory()) {
				continue;
			}
			try (Stream<Path> paths = Files.walk(folder.toPath())) {
				paths.filter(path -> path.getFileName().toString().endsWith(".eogen") && Files.isRegularFile(path))
						.filter(path -> !isHidden(folder.toPath().relativize(path)))
						.map(path -> absolute(path.toFile()))
						.forEach(eogenFiles::add);
			}
		}
		List<File> sorted = new ArrayList<>(eogenFiles);
		Collections.sort(sorted);
		return sorted;
	}

	/**
	 * Reads an .eogen file.
	 *
	 * @param eogenFile an .eogen file
	 * @return its contents
	 * @throws EOGeneratorException if the file can't be read or parsed
	 */
	public EOGeneratorModel readEOGenFile(File eogenFile) throws EOGeneratorException {
		try {
			return EOGeneratorModel.createModelFromFile(eogenFile, projectDirectory.toPath());
		} catch (IOException | ParseException e) {
			throw new EOGeneratorException("Failed to read " + eogenFile + ": " + e.getMessage(), e);
		}
	}

	/**
	 * Generates the classes for an .eogen file: writes every superclass whose
	 * contents have changed, and every missing subclass.
	 *
	 * @param eogenFile an .eogen file
	 * @return the files generated
	 * @throws EOGeneratorException if the file or its models can't be loaded, or
	 *         a template fails
	 */
	public GenerationResult generate(File eogenFile) throws EOGeneratorException {
		return run(eogenFile, true);
	}

	/**
	 * Generates the classes for an .eogen file into a scratch folder, and
	 * compares them with the project's. Nothing in the project changes.
	 *
	 * @param eogenFile an .eogen file
	 * @return the files that generating would write, and whether each is stale
	 * @throws EOGeneratorException if the file or its models can't be loaded, or
	 *         a template fails
	 */
	public GenerationResult check(File eogenFile) throws EOGeneratorException {
		return run(eogenFile, false);
	}

	/**
	 * Loads the project's model group, then resolves and verifies it, as
	 * Entity Modeler does.
	 *
	 * @param includeDependencyModels whether to report failures in models from
	 *        the classpath as well as the project's own
	 * @return the failures found
	 * @throws EOGeneratorException if the model group can't be loaded
	 */
	public VerificationResult verify(boolean includeDependencyModels) throws EOGeneratorException {
		Set<EOModelVerificationFailure> failures = new LinkedHashSet<>();
		EOModelGroup modelGroup = new EOModelGroup();
		try {
			modelGroupFactory().loadModelGroup(null, modelGroup, failures, true, new NullProgressMonitor());
		} catch (EOModelException e) {
			throw new EOGeneratorException("Failed to load the model group: " + e.getMessage(), e);
		}
		modelGroup.resolve(failures);
		modelGroup.verify(failures);

		Set<EOModel> reportedModels = new HashSet<>();
		for (EOModel model : modelGroup.getModels()) {
			if (includeDependencyModels || isProjectModel(model)) {
				reportedModels.add(model);
			}
		}
		List<EOModelVerificationFailure> reported = new ArrayList<>();
		for (EOModelVerificationFailure failure : failures) {
			if (failure.getModel() == null || reportedModels.contains(failure.getModel())) {
				reported.add(failure);
			}
		}
		reported.sort(Comparator.comparing((EOModelVerificationFailure failure) -> failure.getModel() == null ? "" : failure.getModel().getName())
				.thenComparing(EOModelVerificationFailure::isWarning)
				.thenComparing(EOModelVerificationFailure::getMessage));
		List<String> modelNames = reportedModels.stream().map(EOModel::getName).sorted().collect(Collectors.toList());
		return new VerificationResult(modelNames, reported);
	}

	private ClasspathEOModelGroupFactory modelGroupFactory() {
		return new ClasspathEOModelGroupFactory(modelFolders, classpath);
	}

	private boolean isProjectModel(EOModel model) {
		if (model.getModelURL() == null || !"file".equals(model.getModelURL().getProtocol())) {
			return false;
		}
		Path modelPath = absolute(URLUtils.cheatAndTurnIntoFile(model.getModelURL())).toPath();
		for (File modelFolder : modelFolders) {
			if (modelPath.startsWith(modelFolder.toPath())) {
				return true;
			}
		}
		return false;
	}

	private GenerationResult run(File eogenFile, boolean write) throws EOGeneratorException {
		EOGeneratorModel eogeneratorModel = readEOGenFile(eogenFile);
		validate(eogenFile, eogeneratorModel);
		Path scratch;
		try {
			scratch = Files.createTempDirectory("eogen");
		} catch (IOException e) {
			throw new EOGeneratorException("Failed to create a scratch folder: " + e.getMessage(), e);
		}
		try {
			Map<String, File> destinations = new LinkedHashMap<>();
			Map<String, File> outputs = new LinkedHashMap<>();
			VelocityEOGeneratorRunner runner = new VelocityEOGeneratorRunner();
			runner.setModelGroupFactories(Collections.singletonList(modelGroupFactory()));
			runner.setDestinationMapper((name, destination) -> {
				File output = new File(scratch.toFile(), name);
				destinations.put(name, destination);
				outputs.put(name, output);
				return output;
			});
			StringBuffer results = new StringBuffer();
			try {
				runner.generate(eogeneratorModel, results, null, null, EOGeneratorResourceLoader.class, new NullProgressMonitor());
			} catch (Exception e) {
				throw new EOGeneratorException("Failed to generate from " + eogenFile + ": " + e.getMessage(), e);
			}
			List<GeneratedFile> files = new ArrayList<>();
			for (Map.Entry<String, File> output : outputs.entrySet()) {
				Kind kind = EOGeneratorModel.SUBCLASS_DESTINATION.equals(output.getKey()) ? Kind.SUBCLASS : Kind.SUPERCLASS;
				files.addAll(compare(output.getValue().toPath(), destinations.get(output.getKey()).toPath(), kind, write));
			}
			return new GenerationResult(eogenFile, write, files, errors(results));
		} catch (IOException e) {
			throw new EOGeneratorException("Failed to write the files for " + eogenFile + ": " + e.getMessage(), e);
		} finally {
			delete(scratch);
		}
	}

	/**
	 * Checks what WOLips would otherwise fail on with a NullPointerException, and
	 * gives a missing -subclassDestination the -destination, as veogen did.
	 */
	private void validate(File eogenFile, EOGeneratorModel eogeneratorModel) throws EOGeneratorException {
		if (eogeneratorModel.getDestination() == null) {
			throw new EOGeneratorException(eogenFile + " has no -destination.");
		}
		if (eogeneratorModel.getSubclassDestination() == null) {
			eogeneratorModel.setSubclassDestination(eogeneratorModel.getDestination());
		}
		if (eogeneratorModel.getModels().isEmpty()) {
			throw new EOGeneratorException(eogenFile + " names no -model.");
		}
		for (EOModelReference modelReference : eogeneratorModel.getModels()) {
			File modelFolder = new File(modelReference.getPath((Path) null));
			if (!modelFolder.isAbsolute()) {
				modelFolder = new File(projectDirectory, modelFolder.getPath());
			}
			if (!new File(modelFolder, "index.eomodeld").isFile()) {
				throw new EOGeneratorException(eogenFile + " names the model " + modelFolder + ", which does not exist.");
			}
		}
	}

	/**
	 * Compares the files generated into a scratch folder with the destination's,
	 * and copies the stale ones there if asked.
	 */
	private static List<GeneratedFile> compare(Path output, Path destination, Kind kind, boolean write) throws IOException {
		List<GeneratedFile> files = new ArrayList<>();
		if (!Files.isDirectory(output)) {
			return files;
		}
		List<Path> generated;
		try (Stream<Path> paths = Files.walk(output)) {
			generated = paths.filter(Files::isRegularFile).sorted().collect(Collectors.toList());
		}
		for (Path file : generated) {
			Path target = destination.resolve(output.relativize(file).toString());
			byte[] contents = Files.readAllBytes(file);
			Status status;
			if (!Files.exists(target)) {
				status = Status.CREATED;
			} else if (Arrays.equals(contents, Files.readAllBytes(target))) {
				status = Status.UNCHANGED;
			} else {
				status = Status.UPDATED;
			}
			if (write && status != Status.UNCHANGED) {
				Files.createDirectories(target.getParent());
				Files.write(target, contents);
			}
			files.add(new GeneratedFile(target.toFile(), kind, status));
		}
		return files;
	}

	/**
	 * Returns the errors the runner reported, one per "Error: " line.
	 */
	private static List<String> errors(StringBuffer results) {
		List<String> errors = new ArrayList<>();
		String prefix = "Error: ";
		for (String line : results.toString().split("\n")) {
			if (line.startsWith(prefix)) {
				errors.add(line.substring(prefix.length()));
			} else if (!line.isEmpty() && !errors.isEmpty()) {
				int last = errors.size() - 1;
				errors.set(last, errors.get(last) + "\n" + line);
			}
		}
		return errors;
	}

	private static boolean isHidden(Path relativePath) {
		for (Path name : relativePath) {
			if (name.toString().startsWith(".")) {
				return true;
			}
		}
		return false;
	}

	private static File absolute(File file) {
		return file.getAbsoluteFile().toPath().normalize().toFile();
	}

	private static void delete(Path folder) {
		try (Stream<Path> paths = Files.walk(folder)) {
			for (Path path : paths.sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
				Files.deleteIfExists(path);
			}
		} catch (IOException e) {
			// A scratch folder left behind in the temporary folder does no harm.
		}
	}
}

package net.logicsquad.eogen;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
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
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.logicsquad.eogen.GeneratedFile.Kind;
import net.logicsquad.eogen.GeneratedFile.Status;
import net.logicsquad.eogen.generator.ClasspathEOModelGroupFactory;
import net.logicsquad.eogen.generator.EOGeneratorModel;
import net.logicsquad.eogen.generator.EOGeneratorResourceLoader;
import net.logicsquad.eogen.generator.EOModelReference;
import net.logicsquad.eogen.format.FormatResult;
import net.logicsquad.eogen.format.ModelFormatter;
import net.logicsquad.eogen.generator.VelocityEOGeneratorRunner;
import net.logicsquad.eogen.lint.LintFinding;
import net.logicsquad.eogen.lint.ModelLinter;
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
 * <p>
 * Unlike WOLips, it writes nothing when loading or verifying the model group
 * finds errors, or when a template renders a reference that has no value,
 * because the classes it would write are broken. {@link #setForce(boolean)}
 * writes them anyway, as WOLips does.
 * </p>
 * <p>
 * It also lints the models it uses, with {@link ModelLinter}, for the mistakes
 * that loading a model hides, such as a misspelt key. Its findings are
 * warnings, or errors when {@link #setStrict(boolean) strict}.
 * </p>
 */
public class EOGenerator {
	/**
	 * The model folder of a Maven project, relative to the project folder.
	 */
	public static final String DEFAULT_MODEL_FOLDER = "src/main/resources";

	/**
	 * The text that marks a file as generated: WOLips's and Project Wonder's
	 * templates put {@code // DO NOT EDIT.} on the first line of every
	 * superclass.
	 */
	public static final String DEFAULT_GENERATED_MARKER = "DO NOT EDIT";

	/**
	 * How many lines at the start of a file {@link #findOrphans(List)} looks in
	 * for the generated marker.
	 */
	private static final int MARKER_LINES = 5;

	private final File projectDirectory;

	private final List<File> modelFolders;

	private final List<File> classpath;

	private boolean force;

	private boolean strict;

	private String generatedMarker = DEFAULT_GENERATED_MARKER;

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
	 * Returns whether {@link #generate(File)} writes the files even when there
	 * are errors.
	 *
	 * @return {@code true} if forced
	 */
	public boolean isForce() {
		return force;
	}

	/**
	 * Sets whether {@link #generate(File)} writes the files even when loading
	 * or verifying the model group finds errors, or a template renders a
	 * reference that has no value, as WOLips does. The default is
	 * {@code false}: errors mean nothing is written.
	 *
	 * @param force whether to write despite errors
	 */
	public void setForce(boolean force) {
		this.force = force;
	}

	/**
	 * Returns whether lint findings are errors rather than warnings.
	 *
	 * @return {@code true} if strict
	 */
	public boolean isStrict() {
		return strict;
	}

	/**
	 * Sets whether the findings of {@link ModelLinter} are errors, which stop
	 * {@link #generate(File)} writing and fail {@link #verify(boolean)}, rather
	 * than warnings. The default is {@code false}.
	 *
	 * @param strict whether lint findings are errors
	 */
	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	/**
	 * Returns the text that marks a superclass as generated.
	 *
	 * @return marker
	 */
	public String getGeneratedMarker() {
		return generatedMarker;
	}

	/**
	 * Sets the text that marks a superclass as generated, which
	 * {@link #findOrphans(List)} looks for in a file's first lines. The default
	 * is {@link #DEFAULT_GENERATED_MARKER}.
	 *
	 * @param generatedMarker marker
	 */
	public void setGeneratedMarker(String generatedMarker) {
		this.generatedMarker = generatedMarker;
	}

	/**
	 * Returns the orphans in the destinations of some .eogen files: the
	 * superclasses that none of them generates any more, such as those of
	 * entities since deleted or renamed, and the subclasses of those
	 * superclasses, which were edited by hand. A superclass counts only if it is
	 * in a superclass destination with the .eogen file's prefix and extension,
	 * and has the generated marker in its first lines.
	 * <p>
	 * Give it the results for every .eogen file that writes to those
	 * destinations: a superclass that another .eogen file generates would
	 * otherwise look orphaned.
	 * </p>
	 *
	 * @param results the results of generating or checking each .eogen file
	 * @return orphans: superclasses, then subclasses, each sorted by path
	 * @throws EOGeneratorException if an .eogen file can't be read, or a
	 *         destination can't be searched
	 */
	public List<GeneratedFile> findOrphans(List<GenerationResult> results) throws EOGeneratorException {
		Set<Path> generated = new HashSet<>();
		for (GenerationResult result : results) {
			for (GeneratedFile file : result.getFiles()) {
				generated.add(absolute(file.getFile()).toPath());
			}
		}
		Set<Path> superclasses = new TreeSet<>();
		Set<Path> subclasses = new TreeSet<>();
		for (GenerationResult result : results) {
			EOGeneratorModel eogeneratorModel = readEOGenFile(result.getEOGenFile());
			if (eogeneratorModel.getDestination() == null) {
				continue;
			}
			String prefix = eogeneratorModel.getPrefix() == null ? "_" : eogeneratorModel.getPrefix();
			String extension = "." + (eogeneratorModel.getExtension() == null ? "java" : eogeneratorModel.getExtension());
			Path destination = resolve(eogeneratorModel.getDestination());
			Path subclassDestination = eogeneratorModel.getSubclassDestination() == null ? destination : resolve(eogeneratorModel.getSubclassDestination());
			if (!Files.isDirectory(destination) || prefix.isEmpty()) {
				continue;
			}
			List<Path> candidates;
			try (Stream<Path> paths = Files.walk(destination)) {
				candidates = paths.filter(Files::isRegularFile).filter(path -> {
					String name = path.getFileName().toString();
					return name.startsWith(prefix) && name.endsWith(extension) && name.length() > prefix.length() + extension.length();
				}).map(path -> path.toAbsolutePath().normalize()).collect(Collectors.toList());
			} catch (IOException e) {
				throw new EOGeneratorException("Failed to search " + destination + ": " + e.getMessage(), e);
			}
			for (Path candidate : candidates) {
				if (generated.contains(candidate) || !isMarkedAsGenerated(candidate)) {
					continue;
				}
				superclasses.add(candidate);
				if (eogeneratorModel.getSuperclassPackage() == null) {
					Path relative = destination.relativize(candidate);
					String subclassName = relative.getFileName().toString().substring(prefix.length());
					Path parent = relative.getParent();
					Path subclass = subclassDestination.resolve(parent == null ? subclassName : parent.resolve(subclassName).toString()).normalize();
					if (Files.isRegularFile(subclass) && !generated.contains(subclass)) {
						subclasses.add(subclass);
					}
				}
			}
		}
		List<GeneratedFile> orphans = new ArrayList<>();
		for (Path superclass : superclasses) {
			orphans.add(new GeneratedFile(superclass.toFile(), Kind.SUPERCLASS, Status.ORPHANED));
		}
		for (Path subclass : subclasses) {
			orphans.add(new GeneratedFile(subclass.toFile(), Kind.SUBCLASS, Status.ORPHANED));
		}
		return orphans;
	}

	/**
	 * Deletes the orphaned superclasses that {@link #findOrphans(List)} found.
	 * Subclasses were edited by hand, so it leaves them alone.
	 *
	 * @param orphans orphans
	 * @return the superclasses it deleted
	 * @throws EOGeneratorException if a superclass can't be deleted
	 */
	public static List<GeneratedFile> deleteOrphans(List<GeneratedFile> orphans) throws EOGeneratorException {
		List<GeneratedFile> deleted = new ArrayList<>();
		for (GeneratedFile orphan : orphans) {
			if (orphan.getKind() == Kind.SUPERCLASS && orphan.getStatus() == Status.ORPHANED) {
				try {
					Files.deleteIfExists(orphan.getFile().toPath());
				} catch (IOException e) {
					throw new EOGeneratorException("Failed to delete " + orphan.getFile() + ": " + e.getMessage(), e);
				}
				deleted.add(orphan);
			}
		}
		return deleted;
	}

	private Path resolve(String path) {
		File file = new File(path);
		return absolute(file.isAbsolute() ? file : new File(projectDirectory, path)).toPath();
	}

	/**
	 * Returns whether a file has the generated marker in its first lines.
	 */
	private boolean isMarkedAsGenerated(Path file) throws EOGeneratorException {
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			for (int i = 0; i < MARKER_LINES; i++) {
				String line = reader.readLine();
				if (line == null) {
					return false;
				}
				if (line.contains(generatedMarker)) {
					return true;
				}
			}
			return false;
		} catch (MalformedInputException e) {
			return false;
		} catch (IOException e) {
			throw new EOGeneratorException("Failed to read " + file + ": " + e.getMessage(), e);
		}
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
	 * contents have changed, and every missing subclass. If there are errors,
	 * it writes nothing unless forced, and {@link GenerationResult#isWritten()}
	 * is {@code false}.
	 *
	 * @param eogenFile an .eogen file
	 * @return the files generated, and any errors
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
		for (File modelFolder : modelFolders) {
			for (File model : ClasspathEOModelGroupFactory.findModelFolders(modelFolder)) {
				String name = EOModelGroup.getModelNameForFile(model);
				EOModel eomodel = name == null ? null : modelGroup.getModelNamed(name);
				for (LintFinding finding : ModelLinter.lint(model)) {
					failures.add(new EOModelVerificationFailure(eomodel, finding.toString(), !strict));
				}
			}
		}

		Set<EOModel> reportedModels = new HashSet<>();
		for (EOModel model : modelGroup.getModels()) {
			if (includeDependencyModels || isProjectModel(model)) {
				reportedModels.add(model);
			}
		}
		List<EOModelVerificationFailure> reported = new ArrayList<>();
		for (EOModelVerificationFailure failure : failures) {
			EOModel model = failure.getModel();
			if (model == null || reportedModels.contains(model)) {
				reported.add(failure);
			} else if (!modelGroup.getModels().contains(model) && (includeDependencyModels || isProjectModel(model))) {
				// A model that failed to load is not in the group, but its failure counts.
				reportedModels.add(model);
				reported.add(failure);
			}
		}
		reported.sort(Comparator.comparing((EOModelVerificationFailure failure) -> failure.getModel() == null ? "" : failure.getModel().getName())
				.thenComparing(EOModelVerificationFailure::isWarning)
				.thenComparing(EOModelVerificationFailure::getMessage));
		List<String> modelNames = reportedModels.stream().map(EOModel::getName).sorted().collect(Collectors.toList());
		return new VerificationResult(modelNames, reported);
	}

	/**
	 * Formats the project's models as Entity Modeler saves them, with
	 * {@link ModelFormatter}: only their layout changes, and a model whose
	 * contents saving would change is left alone. Nothing is formatted if the
	 * model group has errors.
	 *
	 * @param write whether to write the formatted files, or only report them
	 * @return the model files, and what formatting does to each
	 * @throws EOGeneratorException if the model group can't be loaded, or a
	 *         model can't be saved
	 */
	public FormatResult format(boolean write) throws EOGeneratorException {
		Set<EOModelVerificationFailure> failures = new LinkedHashSet<>();
		EOModelGroup modelGroup = new EOModelGroup();
		try {
			modelGroupFactory().loadModelGroup(null, modelGroup, failures, true, new NullProgressMonitor());
		} catch (EOModelException e) {
			throw new EOGeneratorException("Failed to load the model group: " + e.getMessage(), e);
		}
		modelGroup.resolve(failures);
		List<String> errors = new ArrayList<>();
		for (EOModelVerificationFailure failure : failures) {
			if (!failure.isWarning()) {
				errors.add((failure.getModel() == null ? "" : failure.getModel().getName() + ": ") + failure.getMessage());
			}
		}
		if (!errors.isEmpty()) {
			return new FormatResult(Collections.<FormatResult.FormattedFile>emptyList(), errors, false);
		}
		Path scratch;
		try {
			scratch = Files.createTempDirectory("eogen-format");
		} catch (IOException e) {
			throw new EOGeneratorException("Failed to create a scratch folder: " + e.getMessage(), e);
		}
		try {
			List<FormatResult.FormattedFile> files = new ArrayList<>();
			for (File modelFolder : modelFolders) {
				for (File model : ClasspathEOModelGroupFactory.findModelFolders(modelFolder)) {
					String name = EOModelGroup.getModelNameForFile(model);
					EOModel eomodel = name == null ? null : modelGroup.getModelNamed(name);
					if (eomodel == null || !isProjectModel(eomodel)) {
						continue;
					}
					File modelScratch = Files.createTempDirectory(scratch, name).toFile();
					files.addAll(ModelFormatter.format(eomodel, model, modelScratch, write));
				}
			}
			return new FormatResult(files, errors, write);
		} catch (IOException e) {
			throw new EOGeneratorException("Failed to format the models: " + e.getMessage(), e);
		} finally {
			delete(scratch);
		}
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
		List<File> models = validate(eogenFile, eogeneratorModel);
		List<String> findings = new ArrayList<>();
		for (File model : models) {
			for (LintFinding finding : ModelLinter.lint(model)) {
				findings.add(finding.toString());
			}
		}
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
			List<String> errors = errors(results);
			List<String> warnings = new ArrayList<>();
			if (strict) {
				errors.addAll(findings);
			} else {
				warnings.addAll(findings);
			}
			boolean written = write && (errors.isEmpty() || force);
			List<GeneratedFile> files = new ArrayList<>();
			for (Map.Entry<String, File> output : outputs.entrySet()) {
				Kind kind = EOGeneratorModel.SUBCLASS_DESTINATION.equals(output.getKey()) ? Kind.SUBCLASS : Kind.SUPERCLASS;
				files.addAll(compare(output.getValue().toPath(), destinations.get(output.getKey()).toPath(), kind, written));
			}
			return new GenerationResult(eogenFile, written, files, errors, warnings);
		} catch (IOException e) {
			throw new EOGeneratorException("Failed to write the files for " + eogenFile + ": " + e.getMessage(), e);
		} finally {
			delete(scratch);
		}
	}

	/**
	 * Checks what WOLips would otherwise fail on with a NullPointerException, and
	 * gives a missing -subclassDestination the -destination, as veogen did.
	 *
	 * @return the folders of the models that the .eogen file names
	 */
	private List<File> validate(File eogenFile, EOGeneratorModel eogeneratorModel) throws EOGeneratorException {
		if (eogeneratorModel.getDestination() == null) {
			throw new EOGeneratorException(eogenFile + " has no -destination.");
		}
		if (eogeneratorModel.getSubclassDestination() == null) {
			eogeneratorModel.setSubclassDestination(eogeneratorModel.getDestination());
		}
		if (eogeneratorModel.getModels().isEmpty()) {
			throw new EOGeneratorException(eogenFile + " names no -model.");
		}
		List<File> models = new ArrayList<>();
		for (EOModelReference modelReference : eogeneratorModel.getModels()) {
			File modelFolder = new File(modelReference.getPath((Path) null));
			if (!modelFolder.isAbsolute()) {
				modelFolder = new File(projectDirectory, modelFolder.getPath());
			}
			boolean indexFile = modelFolder.getName().equals("index.eomodeld") && modelFolder.isFile();
			if (!indexFile && !new File(modelFolder, "index.eomodeld").isFile()) {
				throw new EOGeneratorException(eogenFile + " names the model " + modelFolder + ", which does not exist.");
			}
			models.add(indexFile ? modelFolder.getParentFile() : modelFolder);
		}
		return models;
	}

	/**
	 * Compares the files generated into a scratch folder with the destination's,
	 * and copies the stale ones there if asked. A file that differs only in its
	 * line endings, as a Windows checkout with Git's {@code core.autocrlf} has
	 * them, is unchanged, and an updated file keeps the line endings it had.
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
			} else {
				byte[] existing = Files.readAllBytes(target);
				if (Arrays.equals(contents, existing) || Arrays.equals(toLF(contents), toLF(existing))) {
					status = Status.UNCHANGED;
				} else {
					status = Status.UPDATED;
					if (usesCRLF(existing)) {
						contents = toCRLF(contents);
					}
				}
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
	 * Returns contents with each CRLF line ending as an LF.
	 */
	static byte[] toLF(byte[] contents) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(contents.length);
		for (int i = 0; i < contents.length; i++) {
			if (!(contents[i] == '\r' && i + 1 < contents.length && contents[i + 1] == '\n')) {
				out.write(contents[i]);
			}
		}
		return out.toByteArray();
	}

	/**
	 * Returns contents with each LF line ending as a CRLF.
	 */
	static byte[] toCRLF(byte[] contents) {
		byte[] lf = toLF(contents);
		ByteArrayOutputStream out = new ByteArrayOutputStream(lf.length + lf.length / 20);
		for (byte b : lf) {
			if (b == '\n') {
				out.write('\r');
			}
			out.write(b);
		}
		return out.toByteArray();
	}

	/**
	 * Returns whether contents end their first line with a CRLF.
	 */
	static boolean usesCRLF(byte[] contents) {
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] == '\n') {
				return i > 0 && contents[i - 1] == '\r';
			}
		}
		return false;
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

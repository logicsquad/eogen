package net.logicsquad.eogen;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
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
import net.logicsquad.eogen.lint.KeyLinter;
import net.logicsquad.eogen.lint.LintFinding;
import net.logicsquad.eogen.lint.ModelLinter;
import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EOModelException;
import net.logicsquad.eogen.model.EOModelGroup;
import net.logicsquad.eogen.model.EOModelVerificationFailure;
import net.logicsquad.eogen.refactor.ModelRefactorer;
import net.logicsquad.eogen.refactor.RefactorResult;
import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOStoredProcedure;
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
 * that loading a model hides, such as a misspelt key, and with
 * {@link KeyLinter}, for keys in fetch specifications and restricting
 * qualifiers that match nothing. Their findings are warnings, or errors when
 * {@link #setStrict(boolean) strict}.
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

	/**
	 * Orders paths by their text, with / separators, so that files come in the
	 * same order everywhere: {@link Path} and {@link File} ignore case on
	 * Windows.
	 */
	private static final Comparator<Path> BY_PATH = Comparator.comparing(path -> path.toString().replace(File.separatorChar, '/'));

	private final File projectDirectory;

	private final List<File> modelFolders;

	private final List<File> classpath;

	private boolean force;

	private boolean strict;

	private String generatedMarker = DEFAULT_GENERATED_MARKER;

	private List<File> templatePath = Collections.emptyList();

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
	 * Sets whether the findings of {@link ModelLinter} and {@link KeyLinter} are
	 * errors, which stop {@link #generate(File)} writing and fail
	 * {@link #verify(boolean)}, rather than warnings. The default is
	 * {@code false}.
	 *
	 * @param strict whether lint findings are errors
	 */
	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	/**
	 * Returns the jars and folders of shared templates.
	 *
	 * @return template path
	 */
	public List<File> getTemplatePath() {
		return templatePath;
	}

	/**
	 * Sets the jars and folders of shared templates. A jar has them at
	 * {@code eogen/templates/}, and a folder at its top. They come after the
	 * project folder and an .eogen file's {@code -templatedir}, so that a
	 * project can override one, then the templates at
	 * {@code eogen/templates/} on eogen's own classpath, where the Maven
	 * plugin's dependencies are, and then WOLips's folders and the bundled
	 * templates. The default is none.
	 *
	 * @param templatePath jars and folders
	 */
	public void setTemplatePath(List<File> templatePath) {
		this.templatePath = Collections.unmodifiableList(new ArrayList<>(templatePath));
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
		Map<String, Path> generatedIgnoringCase = new HashMap<>();
		for (Path path : generated) {
			generatedIgnoringCase.put(path.toString().toLowerCase(Locale.ROOT), path);
		}
		Set<Path> superclasses = new TreeSet<>(BY_PATH);
		Set<Path> subclasses = new TreeSet<>(BY_PATH);
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
				if (generated.contains(candidate) || isGeneratedUnderAnotherCase(candidate, generatedIgnoringCase) || !isMarkedAsGenerated(candidate)) {
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

	/**
	 * Returns whether a file is one that generating writes under a name that
	 * differs only in case. Where the file system ignores case, the superclass
	 * of a class renamed that way keeps its old name until it is written, and
	 * is the same file.
	 */
	private static boolean isGeneratedUnderAnotherCase(Path file, Map<String, Path> generatedIgnoringCase) throws EOGeneratorException {
		Path generated = generatedIgnoringCase.get(file.toString().toLowerCase(Locale.ROOT));
		try {
			return generated != null && Files.exists(generated) && Files.isSameFile(file, generated);
		} catch (IOException e) {
			throw new EOGeneratorException("Failed to compare " + file + " with " + generated + ": " + e.getMessage(), e);
		}
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
		sorted.sort(Comparator.comparing(File::toPath, BY_PATH));
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
			throw eogenFileException("Failed to read " + eogenFile + ": " + e.getMessage(), e, eogenFile);
		}
	}

	private EOGeneratorException eogenFileException(String message, Throwable cause, File eogenFile) {
		return new EOGeneratorException(message, cause, Collections.singletonList(Finding.error(Finding.EOGEN_FILE, message).file(relative(eogenFile.getPath())).build()));
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
			throw new EOGeneratorException("Failed to load the model group: " + e.getMessage(), e, Finding.LOAD);
		}
		modelGroup.resolve(failures);
		modelGroup.verify(failures);
		Map<EOModelVerificationFailure, Finding> lintFindings = new IdentityHashMap<>();
		for (File modelFolder : modelFolders) {
			for (File model : ClasspathEOModelGroupFactory.findModelFolders(modelFolder)) {
				String name = EOModelGroup.getModelNameForFile(model);
				EOModel eomodel = name == null ? null : modelGroup.getModelNamed(name);
				List<LintFinding> found = new ArrayList<>(ModelLinter.lint(model));
				if (eomodel != null) {
					found.addAll(KeyLinter.lint(eomodel, model));
				}
				for (LintFinding finding : found) {
					EOModelVerificationFailure failure = new EOModelVerificationFailure(eomodel, finding.toString(), !strict);
					failures.add(failure);
					Finding.Builder builder = finding(finding, name).toBuilder().severity(strict ? Finding.Severity.ERROR : Finding.Severity.WARNING);
					lintFindings.put(failure, builder.build());
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
		List<Finding> findings = new ArrayList<>();
		for (EOModelVerificationFailure failure : reported) {
			Finding finding = lintFindings.get(failure);
			findings.add(finding != null ? finding : relativize(Collections.singletonList(Finding.of(failure).build())).get(0));
		}
		return new VerificationResult(modelNames, reported, findings);
	}

	/**
	 * Renames an entity, or an attribute or relationship, in the project's
	 * models, with {@link ModelRefactorer}: through Entity Modeler's model
	 * classes, which carry the rename into subentities, flattened definitions,
	 * joins, entity indexes and the property lists, and rewriting the keys in
	 * fetch specifications and restricting qualifiers. It renames names only:
	 * columns and tables stay as they are.
	 * <p>
	 * It saves the models with Entity Modeler's writer into a scratch folder,
	 * before and after the rename, and writes back only the files the rename
	 * changes, as Entity Modeler saved them. So it is refused, as
	 * {@link #format(boolean)} leaves a model alone, if saving any of those
	 * files would lose anything else that it says, such as a key that the
	 * writer drops, or a {@code YES} that it writes as {@code Y}. A renamed
	 * entity's files are renamed too, so it is refused if another file in the
	 * model's folder already has one of their names. Nothing changes if the
	 * model group has errors, or if the rename is refused.
	 * </p>
	 *
	 * @param target {@code Entity}, or {@code Entity.property}
	 * @param newName the new name
	 * @param renameClass for an entity, whether to rename its class too, when
	 *        the class's simple name is the entity's
	 * @param write whether to write the changed files, or only report them
	 * @return the files that change, and what is left to do by hand, or why it
	 *         was refused
	 * @throws EOGeneratorException if the model group can't be loaded, or a
	 *         model can't be saved
	 */
	public RefactorResult rename(String target, String newName, boolean renameClass, boolean write) throws EOGeneratorException {
		return refactor((modelGroup, models) -> ModelRefactorer.rename(modelGroup, models, target, newName, renameClass), write);
	}

	/**
	 * Removes an entity, or an attribute or relationship, from the project's
	 * models, with {@link ModelRefactorer}, unless anything else uses it: a
	 * join, a flattened definition, an index, a relationship to an entity, a
	 * subentity, or a key in a fetch specification or restricting qualifier.
	 * It saves as {@link #rename(String, String, boolean, boolean)} does.
	 *
	 * @param target {@code Entity}, or {@code Entity.property}
	 * @param write whether to write the changed files, or only report them
	 * @return the files that change, and what is left to do by hand, or why it
	 *         was refused
	 * @throws EOGeneratorException if the model group can't be loaded, or a
	 *         model can't be saved
	 */
	public RefactorResult remove(String target, boolean write) throws EOGeneratorException {
		return refactor((modelGroup, models) -> ModelRefactorer.remove(modelGroup, models, target), write);
	}

	private RefactorResult refactor(BiFunction<EOModelGroup, Collection<EOModel>, ModelRefactorer.Change> operation, boolean write) throws EOGeneratorException {
		Set<EOModelVerificationFailure> failures = new LinkedHashSet<>();
		EOModelGroup modelGroup = new EOModelGroup();
		try {
			modelGroupFactory().loadModelGroup(null, modelGroup, failures, true, new NullProgressMonitor());
		} catch (EOModelException e) {
			throw new EOGeneratorException("Failed to load the model group: " + e.getMessage(), e, Finding.LOAD);
		}
		modelGroup.resolve(failures);
		List<Finding> errors = new ArrayList<>();
		for (EOModelVerificationFailure failure : failures) {
			if (!failure.isWarning()) {
				errors.add(Finding.of(failure).build());
			}
		}
		if (!errors.isEmpty()) {
			return new RefactorResult(Collections.<RefactorResult.ChangedFile>emptyList(), relativize(errors), false);
		}
		// Each project model's folder, before saving moves its URL.
		Map<EOModel, File> folders = new LinkedHashMap<>();
		for (File modelFolder : modelFolders) {
			for (File model : ClasspathEOModelGroupFactory.findModelFolders(modelFolder)) {
				String name = EOModelGroup.getModelNameForFile(model);
				EOModel eomodel = name == null ? null : modelGroup.getModelNamed(name);
				if (eomodel != null && isProjectModel(eomodel)) {
					folders.put(eomodel, model);
				}
			}
		}
		Path scratch;
		try {
			scratch = Files.createTempDirectory("eogen-refactor");
		} catch (IOException e) {
			throw new EOGeneratorException("Failed to create a scratch folder: " + e.getMessage(), e);
		}
		try {
			// Each entity's name, so that a renamed entity's files can be found.
			Map<EOEntity, String> entityNames = new IdentityHashMap<>();
			for (EOModel model : folders.keySet()) {
				for (EOEntity entity : model.getEntities()) {
					entityNames.put(entity, entity.getName());
				}
			}
			Map<EOModel, File> before = save(folders.keySet(), scratch.resolve("before").toFile());
			ModelRefactorer.Change change = operation.apply(modelGroup, folders.keySet());
			if (!change.getRefusals().isEmpty()) {
				return new RefactorResult(Collections.<RefactorResult.ChangedFile>emptyList(), relativize(change.getRefusals()), false);
			}
			Map<EOModel, File> after = save(folders.keySet(), scratch.resolve("after").toFile());
			List<RefactorResult.ChangedFile> files = new ArrayList<>();
			List<Finding> refusals = new ArrayList<>();
			for (Map.Entry<EOModel, File> folder : folders.entrySet()) {
				File saved = after.get(folder.getKey());
				Map<String, String> renamed = renamedFiles(folder.getKey(), entityNames);
				for (Map.Entry<String, String> file : renamed.entrySet()) {
					Finding taken = nameTaken(new File(folder.getValue(), file.getKey()), new File(folder.getValue(), file.getValue()), folder.getKey().getName());
					if (taken != null) {
						refusals.add(taken);
					}
				}
				// By the names that each save lists: where the file system ignores
				// case, asking for a renamed entity's new file finds its old one if
				// only the case of the name changes.
				Set<String> oldNames = modelFiles(before.get(folder.getKey()));
				Set<String> newNames = modelFiles(saved);
				for (String name : modelFiles(before.get(folder.getKey()), saved)) {
					File old = new File(before.get(folder.getKey()), name);
					File updated = new File(saved, name);
					File file = new File(folder.getValue(), name);
					// The project's file that says what this one will, but for the
					// change, which saving must lose nothing from: itself, or a
					// renamed entity's old file.
					String original = null;
					if (!newNames.contains(name)) {
						files.add(new RefactorResult.ChangedFile(file, RefactorResult.Status.DELETED));
					} else if (!oldNames.contains(name)) {
						files.add(new RefactorResult.ChangedFile(file, RefactorResult.Status.CREATED));
						original = renamed.get(name);
					} else if (!Arrays.equals(Files.readAllBytes(old.toPath()), Files.readAllBytes(updated.toPath()))) {
						files.add(new RefactorResult.ChangedFile(file, RefactorResult.Status.CHANGED));
						original = name;
					}
					if (original != null) {
						Finding loss = loss(new File(folder.getValue(), original), new File(before.get(folder.getKey()), original), folder.getKey().getName());
						if (loss != null) {
							refusals.add(loss);
						}
					}
				}
			}
			if (!refusals.isEmpty()) {
				return new RefactorResult(Collections.<RefactorResult.ChangedFile>emptyList(), refusals, false);
			}
			if (write) {
				// Deleting first: where the file system ignores case, deleting a
				// renamed entity's old file would delete its new one, if only the
				// case of the name changes.
				for (RefactorResult.ChangedFile file : files) {
					if (file.getStatus() == RefactorResult.Status.DELETED) {
						Files.deleteIfExists(file.getFile().toPath());
					}
				}
				for (RefactorResult.ChangedFile file : files) {
					if (file.getStatus() != RefactorResult.Status.DELETED) {
						EOModel model = folders.entrySet().stream().filter(entry -> entry.getValue().equals(file.getFile().getParentFile())).findFirst().get().getKey();
						Files.copy(new File(after.get(model), file.getFile().getName()).toPath(), file.getFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
					}
				}
			}
			List<Finding> findings = new ArrayList<>();
			for (Finding finding : change.getByHand()) {
				findings.add(finding);
			}
			return new RefactorResult(files, relativize(findings), write);
		} catch (IOException e) {
			throw new EOGeneratorException("Failed to save the models: " + e.getMessage(), e);
		} finally {
			delete(scratch);
		}
	}

	/**
	 * Returns the old name of each file of a model's renamed entities, by its
	 * new name.
	 *
	 * @param model the model, after a change
	 * @param entityNames each entity's name before the change
	 */
	private static Map<String, String> renamedFiles(EOModel model, Map<EOEntity, String> entityNames) {
		Map<String, String> renamed = new TreeMap<>();
		for (EOEntity entity : model.getEntities()) {
			String oldName = entityNames.get(entity);
			if (oldName != null && !oldName.equals(entity.getName())) {
				renamed.put(entity.getName() + ".plist", oldName + ".plist");
				renamed.put(entity.getName() + ".fspec", oldName + ".fspec");
			}
		}
		return renamed;
	}

	/**
	 * Returns an error if a renamed entity's file would take the place of a
	 * file in the project that isn't the entity's own: one that index.eomodeld
	 * doesn't list, say, which writing the entity's file would replace, or a
	 * stale .fspec, which the renamed entity would load. Otherwise returns
	 * null.
	 *
	 * @param file where the renamed entity's file goes
	 * @param own where the entity's file was before the rename
	 * @param model the model's name
	 */
	private Finding nameTaken(File file, File own, String model) throws IOException {
		// Where the file system ignores case, a rename that changes only the
		// case of the name finds the entity's own file.
		if (!file.exists() || own.exists() && Files.isSameFile(file.toPath(), own.toPath())) {
			return null;
		}
		String oldName = own.getName().substring(0, own.getName().lastIndexOf('.'));
		String newName = file.getName().substring(0, file.getName().lastIndexOf('.'));
		String path = relative(file.getPath());
		String message = "There is already a file at " + path + ", so " + oldName + " can't be renamed to " + newName + ". Move it away first.";
		return Finding.error(Finding.NAME_TAKEN, message).file(path).model(model).entity(oldName).build();
	}

	/**
	 * Returns an error if saving a model file would lose anything that it says,
	 * as {@link ModelFormatter#loss(File, File)} names it, since writing a
	 * change to the file would take that with it. Otherwise returns null.
	 *
	 * @param file the project's file
	 * @param saved the file as Entity Modeler's writer saved it before the
	 *        change
	 * @param model the model's name
	 */
	private Finding loss(File file, File saved, String model) {
		String loss = ModelFormatter.loss(file, saved);
		if (loss == null) {
			return null;
		}
		String path = relative(file.getPath());
		String message = path + ": nothing is changed, since saving it would change what it says: " + loss + ". Fix that by hand first; format names the same loss.";
		return Finding.error(Finding.FORMAT_LOSS, message).file(path).model(model).build();
	}

	/**
	 * Saves models with Entity Modeler's writer, every entity and stored
	 * procedure of them.
	 *
	 * @return each model's saved folder
	 */
	private static Map<EOModel, File> save(Collection<EOModel> models, File folder) throws IOException {
		Map<EOModel, File> saved = new LinkedHashMap<>();
		for (EOModel model : models) {
			for (EOEntity entity : model.getEntities()) {
				entity.setEntityDirty(true);
				// An entity writes its .fspec only when its fetch specifications are.
				entity.setFetchSpecsDirty(true);
			}
			for (EOStoredProcedure storedProcedure : model.getStoredProcedures()) {
				storedProcedure.setStoredProcedureDirty(true);
			}
			try {
				saved.put(model, model.saveToFolder(folder));
			} catch (Exception e) {
				throw new IOException("Failed to save " + model.getName() + ": " + e.getMessage(), e);
			}
		}
		return saved;
	}

	/**
	 * Returns the names of the model files in any of some saved folders, as the
	 * file system lists them, sorted.
	 */
	private static Set<String> modelFiles(File... folders) {
		Set<String> names = new TreeSet<>();
		for (File folder : folders) {
			File[] files = folder == null ? null : folder.listFiles();
			if (files != null) {
				for (File file : files) {
					String name = file.getName();
					if (file.isFile() && (name.equals("index.eomodeld") || name.endsWith(".plist") || name.endsWith(".fspec") || name.endsWith(".storedProcedure"))) {
						names.add(name);
					}
				}
			}
		}
		return names;
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
			throw new EOGeneratorException("Failed to load the model group: " + e.getMessage(), e, Finding.LOAD);
		}
		modelGroup.resolve(failures);
		List<String> errors = new ArrayList<>();
		List<Finding> findings = new ArrayList<>();
		for (EOModelVerificationFailure failure : failures) {
			if (!failure.isWarning()) {
				errors.add((failure.getModel() == null ? "" : failure.getModel().getName() + ": ") + failure.getMessage());
				findings.add(Finding.of(failure).build());
			}
		}
		if (!errors.isEmpty()) {
			return new FormatResult(Collections.<FormatResult.FormattedFile>emptyList(), errors, relativize(findings), false);
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
			for (FormatResult.FormattedFile file : files) {
				if (file.getStatus() == FormatResult.Status.REFUSED) {
					String path = relative(file.getFile().getPath());
					findings.add(Finding.error(Finding.FORMAT_LOSS, path + ": its model is left alone, since saving it would change what it says: " + file.getReason() + ".").file(path)
							.model(EOModelGroup.getModelNameForFile(file.getFile().getParentFile())).build());
				}
			}
			return new FormatResult(files, errors, findings, write);
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
		List<Finding> lintFindings = new ArrayList<>();
		for (File model : models) {
			for (LintFinding finding : ModelLinter.lint(model)) {
				lintFindings.add(finding(finding, EOModelGroup.getModelNameForFile(model)));
			}
		}
		lintFindings.addAll(keyFindings(models));
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
			runner.setTemplatePath(templatePath);
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
				String message = "Failed to generate from " + eogenFile + ": " + e.getMessage();
				List<Finding> found = relativize(runner.getFindings());
				throw new EOGeneratorException(message, e, found.isEmpty() ? Collections.singletonList(Finding.error(Finding.FAILURE, message).build()) : found);
			}
			List<Finding> findings = new ArrayList<>();
			if (strict) {
				// A lint finding, such as a key that matches nothing, can be the cause
				// of a template's error, so it comes first.
				for (Finding finding : lintFindings) {
					findings.add(finding.toBuilder().severity(Finding.Severity.ERROR).build());
				}
				findings.addAll(relativize(runner.getFindings()));
			} else {
				findings.addAll(relativize(runner.getFindings()));
				findings.addAll(lintFindings);
			}
			boolean written = write && (findings.stream().noneMatch(Finding::isError) || force);
			List<GeneratedFile> files = new ArrayList<>();
			for (Map.Entry<String, File> output : outputs.entrySet()) {
				Kind kind = EOGeneratorModel.SUBCLASS_DESTINATION.equals(output.getKey()) ? Kind.SUBCLASS : Kind.SUPERCLASS;
				files.addAll(compare(output.getValue().toPath(), destinations.get(output.getKey()).toPath(), kind, written));
			}
			return new GenerationResult(eogenFile, written, files, findings);
		} catch (IOException e) {
			throw new EOGeneratorException("Failed to write the files for " + eogenFile + ": " + e.getMessage(), e);
		} finally {
			delete(scratch);
		}
	}

	/**
	 * Lints the keys of the models that an .eogen file names, with
	 * {@link KeyLinter}, which needs the model group loaded and resolved.
	 *
	 * @return the findings, as warnings
	 */
	private List<Finding> keyFindings(List<File> models) {
		EOModelGroup modelGroup = new EOModelGroup();
		Set<EOModelVerificationFailure> failures = new LinkedHashSet<>();
		try {
			modelGroupFactory().loadModelGroup(null, modelGroup, failures, true, new NullProgressMonitor());
			modelGroup.resolve(failures);
		} catch (EOModelException | RuntimeException e) {
			// Generating reports why the model group doesn't load.
			return Collections.emptyList();
		}
		List<Finding> findings = new ArrayList<>();
		for (File model : models) {
			String name = EOModelGroup.getModelNameForFile(model);
			EOModel eomodel = name == null ? null : modelGroup.getModelNamed(name);
			if (eomodel != null) {
				for (LintFinding finding : KeyLinter.lint(eomodel, model)) {
					findings.add(finding(finding, name));
				}
			}
		}
		return findings;
	}

	/**
	 * Returns a lint finding as a warning, with its file relative to the
	 * project.
	 */
	private Finding finding(LintFinding finding, String model) {
		return Finding.builder(Finding.Severity.WARNING, finding.getCode(), finding.toString()).file(relative(finding.getFile().getPath())).model(model)
				.entity(finding.getEntity()).object(finding.getObject()).key(finding.getKey()).suggestion(finding.getSuggestion()).build();
	}

	/**
	 * Returns findings with their files relative to the project.
	 */
	private List<Finding> relativize(List<Finding> findings) {
		List<Finding> relative = new ArrayList<>();
		for (Finding finding : findings) {
			relative.add(finding.getFile() == null ? finding : finding.toBuilder().file(relative(finding.getFile())).build());
		}
		return relative;
	}

	/**
	 * Returns a path relative to the project, with / on every platform, or the
	 * path as it is if it is outside the project.
	 */
	private String relative(String path) {
		Path projectPath = absolute(projectDirectory).toPath();
		Path filePath = absolute(new File(path)).toPath();
		return filePath.startsWith(projectPath) ? projectPath.relativize(filePath).toString().replace(File.separatorChar, '/') : path;
	}

	/**
	 * Checks what WOLips would otherwise fail on with a NullPointerException, and
	 * gives a missing -subclassDestination the -destination, as veogen did.
	 *
	 * @return the folders of the models that the .eogen file names
	 */
	private List<File> validate(File eogenFile, EOGeneratorModel eogeneratorModel) throws EOGeneratorException {
		if (eogeneratorModel.getDestination() == null) {
			throw eogenFileException(eogenFile + " has no -destination.", null, eogenFile);
		}
		if (eogeneratorModel.getSubclassDestination() == null) {
			eogeneratorModel.setSubclassDestination(eogeneratorModel.getDestination());
		}
		if (eogeneratorModel.getModels().isEmpty()) {
			throw eogenFileException(eogenFile + " names no -model.", null, eogenFile);
		}
		List<File> models = new ArrayList<>();
		for (EOModelReference modelReference : eogeneratorModel.getModels()) {
			File modelFolder = new File(modelReference.getPath((Path) null));
			if (!modelFolder.isAbsolute()) {
				modelFolder = new File(projectDirectory, modelFolder.getPath());
			}
			boolean indexFile = modelFolder.getName().equals("index.eomodeld") && modelFolder.isFile();
			if (!indexFile && !new File(modelFolder, "index.eomodeld").isFile()) {
				throw eogenFileException(eogenFile + " names the model " + modelFolder + ", which does not exist.", null, eogenFile);
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
			generated = paths.filter(Files::isRegularFile).sorted(BY_PATH).collect(Collectors.toList());
		}
		Map<Path, Set<String>> listings = new HashMap<>();
		for (Path file : generated) {
			Path target = destination.resolve(output.relativize(file).toString());
			byte[] contents = Files.readAllBytes(file);
			// Where the file system ignores case, a file whose name differs only in
			// case, such as the superclass of a class renamed that way, answers for
			// the target, which is then new, and replaces it.
			boolean found = Files.exists(target);
			boolean exists = found && names(target.getParent(), listings).contains(target.getFileName().toString());
			Status status;
			if (!exists) {
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
				if (found && !exists) {
					Files.delete(target);
				}
				Files.write(target, contents);
			}
			files.add(new GeneratedFile(target.toFile(), kind, status));
		}
		return files;
	}

	/**
	 * Returns the names of the files in a folder, as the file system lists
	 * them, listing each folder once.
	 */
	private static Set<String> names(Path folder, Map<Path, Set<String>> listings) throws IOException {
		Set<String> names = listings.get(folder);
		if (names == null) {
			try (Stream<Path> files = Files.list(folder)) {
				names = files.map(file -> file.getFileName().toString()).collect(Collectors.toSet());
			}
			listings.put(folder, names);
		}
		return names;
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

package net.logicsquad.eogen.migration;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import net.logicsquad.eogen.EOGeneratorException;
import net.logicsquad.eogen.Finding;
import net.logicsquad.eogen.generator.ClasspathEOModelGroupFactory;
import net.logicsquad.eogen.generator.EOGeneratorResourceLoader;
import net.logicsquad.eogen.migration.MigrationResult.ModelMigration;
import net.logicsquad.eogen.migration.MigrationResult.Status;
import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EOModelException;
import net.logicsquad.eogen.model.EOModelGroup;
import net.logicsquad.eogen.model.EOModelVerificationFailure;
import net.logicsquad.eogen.runtime.NullProgressMonitor;
import net.logicsquad.eogen.utils.URLUtils;
import net.logicsquad.eogen.velocity.ListTool;
import net.logicsquad.eogen.velocity.SetTool;
import net.logicsquad.eogen.velocity.UnresolvedReference;
import net.logicsquad.eogen.velocity.VelocityOneContext;
import net.logicsquad.eogen.velocity.WOLipsVelocityUtils;

/**
 * Generates the next Project Wonder migration for each of a project's models
 * whose tables changed since an earlier version of the models, as
 * {@link MigrationWriter} writes it, rendered from a template, and named as
 * {@link MigrationNaming} names it. A model's first migration creates its
 * tables. Nothing changes an existing migration, and nothing is written if
 * there are errors.
 * <p>
 * The template sees {@code $migration}, a {@link GeneratedMigration}, and
 * {@code $model}, the model, and is found as the entity templates are: in the
 * project folder, then among the shared templates, then WOLips's folders, and
 * last eogen's own, {@link MigrationOptions#DEFAULT_TEMPLATE}.
 * </p>
 */
public final class MigrationGenerator {
	private final File projectDirectory;

	private final List<File> modelFolders;

	private final List<File> classpath;

	private final List<File> templatePath;

	/**
	 * Constructor.
	 *
	 * @param projectDirectory the project folder
	 * @param modelFolders the folders of the project's models
	 * @param classpath jars and folders whose models join the model group
	 * @param templatePath jars and folders of shared templates
	 */
	public MigrationGenerator(File projectDirectory, List<File> modelFolders, List<File> classpath, List<File> templatePath) {
		this.projectDirectory = projectDirectory.getAbsoluteFile();
		this.modelFolders = modelFolders;
		this.classpath = classpath;
		this.templatePath = templatePath;
	}

	/**
	 * Generates the migrations.
	 *
	 * @param options what to compare, and where to write
	 * @param write whether to write the migrations, or only report them
	 * @return what was generated
	 * @throws EOGeneratorException if a model group can't be loaded, the
	 *         template fails, or a file can't be written
	 */
	public MigrationResult generate(MigrationOptions options, boolean write) throws EOGeneratorException {
		List<File> sourceFolders = new ArrayList<>();
		for (File folder : options.getSourceFolders().isEmpty() ? Collections.singletonList(new File(MigrationOptions.DEFAULT_SOURCE_FOLDER)) : options.getSourceFolders()) {
			sourceFolders.add(folder.isAbsolute() ? folder : new File(projectDirectory, folder.getPath()));
		}
		Set<EOModelVerificationFailure> failures = new LinkedHashSet<>();
		EOModelGroup modelGroup = load(modelFolders, failures);
		List<Finding> errors = errors(failures, null);
		if (!errors.isEmpty()) {
			return new MigrationResult(Collections.<ModelMigration>emptyList(), errors, null, false);
		}
		Map<String, EOModel> models = models(modelGroup, modelFolders);
		for (String name : options.getModels()) {
			if (!models.containsKey(name)) {
				errors.add(Finding.error(Finding.NOT_FOUND, "The project has no model named " + name + ".").model(name).build());
			}
		}
		if (!errors.isEmpty()) {
			return new MigrationResult(Collections.<ModelMigration>emptyList(), errors, null, false);
		}
		Path scratch;
		try {
			scratch = Files.createTempDirectory("eogen-migration");
		} catch (IOException e) {
			throw new EOGeneratorException("Failed to create a scratch folder: " + e.getMessage(), e);
		}
		try {
			EarlierModels earlier;
			try {
				earlier = EarlierModels.read(projectDirectory, modelFolders, options.getFrom(), scratch);
			} catch (IOException e) {
				return new MigrationResult(Collections.<ModelMigration>emptyList(), Collections.singletonList(Finding.error(Finding.EARLIER_VERSION, e.getMessage()).build()), null, false);
			}
			Set<EOModelVerificationFailure> earlierFailures = new LinkedHashSet<>();
			EOModelGroup earlierGroup = load(earlier.getModelFolders(), earlierFailures);
			errors = errors(earlierFailures, earlier.getDescription());
			if (!errors.isEmpty()) {
				return new MigrationResult(Collections.<ModelMigration>emptyList(), errors, earlier.getDescription(), false);
			}
			Map<String, EOModel> earlierModels = models(earlierGroup, earlier.getModelFolders());
			Properties properties = MigrationNaming.properties(modelFolders);
			List<String> skipped = MigrationNaming.skippedModels(properties);
			List<ModelMigration> migrations = new ArrayList<>();
			List<Finding> findings = new ArrayList<>();
			Map<File, String> contents = new LinkedHashMap<>();
			VelocityEngine engine = null;
			Map<String, MigrationNaming> namings = new TreeMap<>();
			for (Map.Entry<String, EOModel> entry : models.entrySet()) {
				namings.put(entry.getKey(), MigrationNaming.of(entry.getValue(), properties, sourceFolders, options.getPackageName()));
			}
			namings = MigrationNaming.place(namings);
			for (Map.Entry<String, EOModel> entry : models.entrySet()) {
				String name = entry.getKey();
				EOModel model = entry.getValue();
				boolean named = options.getModels().contains(name);
				if (!options.getModels().isEmpty() && !named) {
					continue;
				}
				if (skipped.contains(name)) {
					migrations.add(new ModelMigration(name, Status.SKIPPED, null, null, -1, false, 0, "er.migration.skipModelNames names it."));
					continue;
				}
				MigrationNaming naming = namings.get(name);
				EOModel earlierModel = earlierModels.get(name);
				Schema now = Schema.of(model);
				Schema then;
				if (naming.isFirst()) {
					then = Schema.empty(name);
					if (!named && earlierModel != null && !new MigrationWriter(Schema.of(earlierModel), now).hasChanges()) {
						migrations.add(new ModelMigration(name, Status.UNCHANGED, null, null, -1, true, 0, "Its tables haven't changed since " + earlier.getDescription()
								+ ", and it has no migrations: name it to write its first, " + naming.getClassName() + ", which creates them."));
						continue;
					}
				} else if (earlierModel == null) {
					String reason = name + " has migrations, but " + earlier.getDescription() + " has no " + name + " model, so eogen can't tell what changed.";
					findings.add(Finding.builder(Finding.Severity.WARNING, Finding.NOT_COMPARED, reason).model(name).build());
					migrations.add(new ModelMigration(name, Status.SKIPPED, null, null, -1, false, 0, reason));
					continue;
				} else {
					then = Schema.of(earlierModel);
				}
				MigrationWriter writer = new MigrationWriter(then, now);
				if (!writer.hasChanges()) {
					migrations.add(new ModelMigration(name, Status.UNCHANGED, null, null, -1, naming.isFirst(), 0, "Its tables haven't changed since " + earlier.getDescription() + "."));
					continue;
				}
				GeneratedMigration migration = new GeneratedMigration(name, naming.getClassName(), naming.getPackageName(), naming.getNumber(), naming.isFirst(), earlier.getDescription(), writer);
				File file = naming.getFile();
				String path = relative(file);
				if (engine == null) {
					engine = engine();
				}
				String content = render(engine, options.getTemplate(), migration, model, path, findings);
				List<Integer> gapLines = new ArrayList<>();
				String[] lines = content.split("\n", -1);
				for (int i = 0; i < lines.length; i++) {
					if (lines[i].contains(MigrationWriter.GAP) && !lines[i].trim().startsWith("//")) {
						gapLines.add(i + 1);
					}
				}
				for (int i = 0; i < writer.getGaps().size(); i++) {
					MigrationWriter.Gap gap = writer.getGaps().get(i);
					Integer line = i < gapLines.size() ? gapLines.get(i) : null;
					findings.add(Finding.builder(Finding.Severity.WARNING, Finding.MIGRATION_GAP, path + (line == null ? "" : ":" + line) + ": " + gap.getMessage()).file(path).line(line).model(name)
							.entity(gap.getEntity()).object(gap.getObject()).build());
				}
				if (file.exists()) {
					findings.add(Finding.error(Finding.FAILURE, path + " exists already, and eogen never changes a migration.").file(path).model(name).build());
				}
				String className = naming.getPackageName() == null ? naming.getClassName() : naming.getPackageName() + "." + naming.getClassName();
				migrations.add(new ModelMigration(name, Status.CREATED, file, className, naming.getNumber(), naming.isFirst(), writer.getGaps().size(), null));
				contents.put(file, content);
			}
			boolean written = write && findings.stream().noneMatch(Finding::isError);
			if (written) {
				for (Map.Entry<File, String> content : contents.entrySet()) {
					Files.createDirectories(content.getKey().getParentFile().toPath());
					Files.write(content.getKey().toPath(), content.getValue().getBytes(StandardCharsets.UTF_8));
				}
			}
			return new MigrationResult(migrations, findings, earlier.getDescription(), written);
		} catch (IOException e) {
			throw new EOGeneratorException("Failed to write the migrations: " + e.getMessage(), e);
		} finally {
			delete(scratch);
		}
	}

	private EOModelGroup load(List<File> folders, Set<EOModelVerificationFailure> failures) throws EOGeneratorException {
		EOModelGroup modelGroup = new EOModelGroup();
		try {
			new ClasspathEOModelGroupFactory(folders, classpath).loadModelGroup(null, modelGroup, failures, true, new NullProgressMonitor());
		} catch (EOModelException e) {
			throw new EOGeneratorException("Failed to load the model group: " + e.getMessage(), e, Finding.LOAD);
		}
		modelGroup.resolve(failures);
		return modelGroup;
	}

	/**
	 * Returns the errors among what loading a model group found, as findings,
	 * with the earlier version, if it is that one.
	 */
	private List<Finding> errors(Set<EOModelVerificationFailure> failures, String earlier) {
		List<Finding> errors = new ArrayList<>();
		for (EOModelVerificationFailure failure : failures) {
			if (!failure.isWarning()) {
				Finding finding = Finding.of(failure).build();
				Finding.Builder builder = finding.toBuilder();
				if (finding.getFile() != null) {
					builder.file(relative(new File(finding.getFile())));
				}
				if (earlier != null) {
					builder.message("In " + earlier + ": " + finding.getMessage());
				}
				errors.add(builder.build());
			}
		}
		return errors;
	}

	/**
	 * Returns the models of a model group that are in some folders, by name.
	 */
	private static Map<String, EOModel> models(EOModelGroup modelGroup, List<File> folders) {
		Map<String, EOModel> models = new TreeMap<>();
		for (File folder : folders) {
			for (File modelFolder : ClasspathEOModelGroupFactory.findModelFolders(folder)) {
				String name = EOModelGroup.getModelNameForFile(modelFolder);
				EOModel model = name == null ? null : modelGroup.getModelNamed(name);
				if (model != null && model.getModelURL() != null && "file".equals(model.getModelURL().getProtocol())) {
					Path path = URLUtils.cheatAndTurnIntoFile(model.getModelURL()).getAbsoluteFile().toPath().normalize();
					if (path.startsWith(folder.getAbsoluteFile().toPath().normalize())) {
						models.putIfAbsent(name, model);
					}
				}
			}
		}
		return models;
	}

	private VelocityEngine engine() throws EOGeneratorException {
		try {
			return WOLipsVelocityUtils.createVelocityEngine("EOGenerator", null, projectDirectory.toPath(), templatePath, EOGeneratorResourceLoader.class);
		} catch (Exception e) {
			throw new EOGeneratorException("Failed to start Velocity: " + e.getMessage(), e);
		}
	}

	/**
	 * Renders a migration, and reports each reference that rendered without a
	 * value as an error.
	 */
	private static String render(VelocityEngine engine, String templateName, GeneratedMigration migration, EOModel model, String path, List<Finding> findings) throws EOGeneratorException {
		Template template;
		try {
			template = engine.getTemplate(templateName, "UTF-8");
		} catch (ResourceNotFoundException e) {
			String message = "The migration template " + templateName + " doesn't exist.";
			throw new EOGeneratorException(message, e, Collections.singletonList(Finding.error(Finding.FAILURE, message).template(templateName).build()));
		} catch (ParseErrorException e) {
			String message = "The migration template " + templateName + " doesn't parse: " + e.getMessage();
			throw new EOGeneratorException(message, e, Collections.singletonList(Finding.error(Finding.FAILURE, message).template(templateName).build()));
		}
		VelocityOneContext context = new VelocityOneContext();
		context.put("migration", migration);
		context.put("model", model);
		context.put("list", new ListTool());
		context.put("set", new SetTool());
		StringWriter output = new StringWriter();
		try {
			template.merge(context, output);
		} catch (RuntimeException e) {
			String message = "The migration template " + templateName + " failed: " + e.getMessage();
			throw new EOGeneratorException(message, e, Collections.singletonList(Finding.error(Finding.FAILURE, message).template(templateName).build()));
		}
		for (UnresolvedReference reference : context.takeUnresolvedReferences()) {
			String message = path + ": " + reference.getDescription();
			findings.add(Finding.error(Finding.UNRESOLVED_REFERENCE, message).file(path).model(model.getName()).object(reference.getObject()).key(reference.getReference())
					.template(reference.getTemplate()).build());
		}
		return output.toString();
	}

	/**
	 * Returns a path relative to the project, with / on every platform, or the
	 * path as it is if it is outside the project.
	 */
	private String relative(File file) {
		Path projectPath = projectDirectory.toPath().normalize();
		Path filePath = file.getAbsoluteFile().toPath().normalize();
		return filePath.startsWith(projectPath) ? projectPath.relativize(filePath).toString().replace(File.separatorChar, '/') : file.getPath();
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

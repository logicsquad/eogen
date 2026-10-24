package net.logicsquad.eogen.doc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import net.logicsquad.eogen.EOGeneratorException;
import net.logicsquad.eogen.Finding;
import net.logicsquad.eogen.generator.ClasspathEOModelGroupFactory;
import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EOModelException;
import net.logicsquad.eogen.model.EOModelGroup;
import net.logicsquad.eogen.model.EOModelVerificationFailure;
import net.logicsquad.eogen.model.EOStoredProcedure;
import net.logicsquad.eogen.runtime.NullProgressMonitor;
import net.logicsquad.eogen.utils.LineEndings;
import net.logicsquad.eogen.utils.URLUtils;
import net.logicsquad.eogen.velocity.ListTool;
import net.logicsquad.eogen.velocity.SetTool;
import net.logicsquad.eogen.velocity.UnresolvedReference;
import net.logicsquad.eogen.velocity.VelocityOneContext;
import net.logicsquad.eogen.velocity.WOLipsVelocityUtils;

/**
 * Writes EOModelDoc's HTML pages for a project's models, as WOLips's
 * {@code EOModelDocGenerator} did, from its templates: an index of every
 * model and entity, in frames, and a page for each model, entity and stored
 * procedure, with the documentation that Entity Modeler keeps in their
 * {@code userInfo}. Or, in Markdown, a page for each model, from
 * {@code model.md.vm}, with a Mermaid diagram of its entities and
 * relationships, to commit beside the code.
 * <p>
 * It documents the project's own models, and with
 * {@link DocOptions#setIncludeDependencies(boolean)} the dependencies' too,
 * but not prototype entities, nor a model that has nothing else. A page links
 * only to the pages of models that it documents. A template finds a page's
 * template as the entity templates are found, after a folder of extra
 * templates, and sees {@code $modelGroup}, {@code $models} and
 * {@code $entities}, the models and entities documented, {@code $documented},
 * the names of those models, {@code $html}, an {@link HtmlTool}, and on each
 * page its {@code $model}, {@code $entity}, with {@code $entityURL}, or
 * {@code $storedProcedure}. The
 * style sheet and the scripts, among them Prototype 1.5.0, are copied as they
 * are. A Markdown page sees {@code $model}, {@code $entities} and
 * {@code $storedProcedures}, its model's, {@code $diagram}, the Mermaid
 * diagram of its entities, if it has no more than
 * {@link DocOptions#getModelDiagramLimit()}, {@code $entityDiagrams}, the
 * diagram of each entity that has relationships, by entity, {@code $md}, a
 * {@link MarkdownTool}, {@code $documented}, and
 * {@code $entityURLs}, the link from each entity's class name, by entity. A
 * reference that renders with no value is an error, and nothing is written,
 * unless forced.
 * </p>
 * <p>
 * Each page is compared with its file, and only those that are missing or
 * differ are written. A file that differs only in its line endings is the
 * same, and one that is written keeps the line endings it had. A Markdown
 * page in the folder that starts with {@link #MARKDOWN_MARKER}, but that
 * isn't written now, is an orphan, which writing deletes.
 * </p>
 */
public final class ModelDocGenerator {
	/**
	 * How a Markdown page that eogen wrote starts, which tells an orphan from
	 * a page of the project's own.
	 */
	public static final String MARKDOWN_MARKER = "<!-- Written by eogen";

	/**
	 * The files that are copied rather than rendered.
	 */
	private static final List<String> FILES = Collections.unmodifiableList(Arrays.asList("eomodeldoc.css", "eomodeldoc.js", "prototype.js"));

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
	public ModelDocGenerator(File projectDirectory, List<File> modelFolders, List<File> classpath, List<File> templatePath) {
		this.projectDirectory = projectDirectory.getAbsoluteFile();
		this.modelFolders = modelFolders;
		this.classpath = classpath;
		this.templatePath = templatePath;
	}

	/**
	 * Writes the pages, or compares them with their files.
	 *
	 * @param options where to write them, and what with
	 * @param write whether to write them, or only compare them
	 * @param force whether to write them despite errors
	 * @return what was written, or would be
	 * @throws EOGeneratorException if the model group can't be loaded, a
	 *         template fails, or a file can't be read or written
	 */
	public DocResult generate(DocOptions options, boolean write, boolean force) throws EOGeneratorException {
		Set<EOModelVerificationFailure> failures = new LinkedHashSet<>();
		EOModelGroup modelGroup = new EOModelGroup();
		try {
			new ClasspathEOModelGroupFactory(modelFolders, classpath).loadModelGroup(null, modelGroup, failures, true, new NullProgressMonitor());
		} catch (EOModelException e) {
			throw new EOGeneratorException("Failed to load the model group: " + e.getMessage(), e, Finding.LOAD);
		}
		modelGroup.resolve(failures);
		List<Finding> findings = new ArrayList<>();
		for (EOModelVerificationFailure failure : failures) {
			if (!failure.isWarning()) {
				Finding finding = Finding.of(failure).build();
				findings.add(finding.getFile() == null ? finding : finding.toBuilder().file(relative(new File(finding.getFile()))).build());
			}
		}
		if (!findings.isEmpty() && !force) {
			return new DocResult(Collections.<String>emptyList(), Collections.<DocResult.Page>emptyList(), Collections.<File>emptyList(), findings, false);
		}
		List<EOModel> models = new ArrayList<>();
		for (EOModel model : modelGroup.getModels()) {
			if ((options.isIncludeDependencies() || isProjectModel(model)) && hasDocumentation(model)) {
				models.add(model);
			}
		}
		models.sort(Comparator.comparing(EOModel::getName));
		Set<String> documented = new TreeSet<>();
		List<EOEntity> entities = new ArrayList<>();
		for (EOModel model : models) {
			documented.add(model.getName());
			entities.addAll(entities(model));
		}
		entities.sort(Comparator.comparing(EOEntity::getName).thenComparing(entity -> entity.getModel().getName()));

		File templateFolder = options.getTemplateFolder() == null ? null : resolve(options.getTemplateFolder());
		VelocityEngine engine;
		try {
			engine = WOLipsVelocityUtils.createVelocityEngine("EOModelDoc", templateFolder == null ? null : templateFolder.getPath(), projectDirectory.toPath(), templatePath, DocResourceLoader.class);
		} catch (Exception e) {
			throw new EOGeneratorException("Failed to start Velocity: " + e.getMessage(), e);
		}
		File output = resolve(options.getOutput());
		Map<String, byte[]> pages = new LinkedHashMap<>();
		boolean markdown = options.getFormat() == DocOptions.Format.MARKDOWN;
		if (markdown) {
			markdown(engine, options, modelGroup, models, documented, pages, output, findings);
		} else {
			html(engine, options, modelGroup, models, entities, documented, templateFolder, pages, output, findings);
		}
		boolean written = write && (findings.isEmpty() || force);
		List<DocResult.Page> files = new ArrayList<>();
		List<File> orphans = new ArrayList<>();
		try {
			for (Map.Entry<String, byte[]> page : pages.entrySet()) {
				File file = new File(output, page.getKey().replace('/', File.separatorChar));
				byte[] contents = page.getValue();
				DocResult.Status status = DocResult.Status.CREATED;
				if (file.isFile()) {
					byte[] existing = Files.readAllBytes(file.toPath());
					if (LineEndings.same(contents, existing)) {
						status = DocResult.Status.UNCHANGED;
					} else {
						status = DocResult.Status.UPDATED;
						contents = LineEndings.like(contents, existing);
					}
				}
				if (written && status != DocResult.Status.UNCHANGED) {
					Files.createDirectories(file.getParentFile().toPath());
					Files.write(file.toPath(), contents);
				}
				files.add(new DocResult.Page(file, status));
			}
			if (markdown) {
				for (File orphan : orphans(output, pages.keySet())) {
					orphans.add(orphan);
					if (written) {
						Files.delete(orphan.toPath());
					}
				}
			}
		} catch (IOException e) {
			throw new EOGeneratorException("Failed to write the documentation: " + e.getMessage(), e);
		}
		List<String> names = new ArrayList<>(documented);
		return new DocResult(names, files, orphans, findings, written);
	}

	/**
	 * Renders EOModelDoc's HTML pages, and the files that are copied, into
	 * memory.
	 */
	private void html(VelocityEngine engine, DocOptions options, EOModelGroup modelGroup, List<EOModel> models, List<EOEntity> entities, Set<String> documented,
			File templateFolder, Map<String, byte[]> pages, File output, List<Finding> findings) throws EOGeneratorException {
		VelocityOneContext context = new VelocityOneContext();
		context.put("modelGroup", modelGroup);
		context.put("models", models);
		context.put("entities", entities);
		context.put("documented", documented);
		context.put("html", new HtmlTool());
		context.put("list", new ListTool());
		context.put("set", new SetTool());
		render(engine, context, "index.html.vm", "index.html", pages, output, findings);
		render(engine, context, "indexContent.html.vm", "content.html", pages, output, findings);
		render(engine, context, "indexOverview.html.vm", "overview.html", pages, output, findings);
		render(engine, context, "indexModels.html.vm", "models.html", pages, output, findings);
		for (String name : FILES) {
			pages.put(name, copy(templateFolder, name));
		}
		for (EOModel model : models) {
			context.put("model", model);
			render(engine, context, "modelOverview.html.vm", model.getName() + "/overview.html", pages, output, findings);
			render(engine, context, "modelContent.html.vm", model.getName() + "/content.html", pages, output, findings);
			for (EOEntity entity : entities(model)) {
				context.put("entity", entity);
				context.remove("entityURL");
				String className = entity.getClassName();
				if (options.getEntityURL() != null && className != null && !className.isEmpty()) {
					context.put("entityURL", entityURL(engine, options.getEntityURL(), entity, findings));
				}
				render(engine, context, "entityContent.html.vm", model.getName() + "/entities/" + entity.getName() + ".html", pages, output, findings);
			}
			context.remove("entity");
			context.remove("entityURL");
			for (EOStoredProcedure storedProcedure : model.getSortedStoredProcedures()) {
				context.put("storedProcedure", storedProcedure);
				render(engine, context, "storedProcedureContent.html.vm", model.getName() + "/storedProcedures/" + storedProcedure.getName() + ".html", pages, output, findings);
			}
			context.remove("storedProcedure");
		}
	}

	/**
	 * Renders a Markdown page for each model into memory.
	 */
	private void markdown(VelocityEngine engine, DocOptions options, EOModelGroup modelGroup, List<EOModel> models, Set<String> documented, Map<String, byte[]> pages,
			File output, List<Finding> findings) throws EOGeneratorException {
		MarkdownTool md = new MarkdownTool();
		List<EOEntity> documentedEntities = new ArrayList<>();
		for (EOModel model : models) {
			documentedEntities.addAll(entities(model));
		}
		documentedEntities.sort(Comparator.comparing(EOEntity::getName));
		for (EOModel model : models) {
			VelocityOneContext context = new VelocityOneContext();
			context.put("modelGroup", modelGroup);
			context.put("models", models);
			context.put("documented", documented);
			context.put("md", md);
			context.put("list", new ListTool());
			context.put("set", new SetTool());
			context.put("model", model);
			List<EOEntity> entities = entities(model);
			context.put("entities", entities);
			context.put("storedProcedures", new ArrayList<>(model.getSortedStoredProcedures()));
			if (!entities.isEmpty() && entities.size() <= options.getModelDiagramLimit()) {
				context.put("diagram", MermaidDiagram.erDiagram(entities));
			}
			Map<String, String> entityDiagrams = new LinkedHashMap<>();
			for (EOEntity entity : entities) {
				String diagram = MermaidDiagram.erDiagram(entity, documentedEntities);
				if (diagram != null) {
					entityDiagrams.put(entity.getName(), diagram);
				}
			}
			context.put("entityDiagrams", entityDiagrams);
			Map<String, String> entityURLs = new LinkedHashMap<>();
			if (options.getEntityURL() != null) {
				for (EOEntity entity : entities) {
					String className = entity.getClassName();
					if (className != null && !className.isEmpty()) {
						entityURLs.put(entity.getName(), entityURL(engine, options.getEntityURL(), entity, findings));
					}
				}
			}
			context.put("entityURLs", entityURLs);
			render(engine, context, "model.md.vm", model.getName() + ".md", pages, output, findings);
		}
	}

	/**
	 * Returns the Markdown pages in a folder that start with
	 * {@link #MARKDOWN_MARKER}, other than those written now.
	 */
	private static List<File> orphans(File output, Set<String> pages) throws IOException {
		List<File> orphans = new ArrayList<>();
		File[] files = output.listFiles((folder, name) -> name.endsWith(".md"));
		if (files == null) {
			return orphans;
		}
		Arrays.sort(files);
		byte[] marker = MARKDOWN_MARKER.getBytes(StandardCharsets.UTF_8);
		for (File file : files) {
			if (!file.isFile() || pages.contains(file.getName())) {
				continue;
			}
			byte[] start = new byte[marker.length];
			int read;
			try (InputStream in = Files.newInputStream(file.toPath())) {
				read = in.readNBytes(start, 0, start.length);
			}
			if (read == marker.length && Arrays.equals(start, marker)) {
				orphans.add(file);
			}
		}
		return orphans;
	}

	/**
	 * Returns whether a model has anything to document: an entity that isn't
	 * a prototype, or a stored procedure.
	 */
	private static boolean hasDocumentation(EOModel model) {
		return !entities(model).isEmpty() || !model.getStoredProcedures().isEmpty();
	}

	/**
	 * Returns a model's entities that aren't prototypes, in name order.
	 */
	private static List<EOEntity> entities(EOModel model) {
		List<EOEntity> entities = new ArrayList<>();
		for (EOEntity entity : model.getSortedEntities()) {
			if (!entity.isPrototype()) {
				entities.add(entity);
			}
		}
		return entities;
	}

	/**
	 * Renders a page into memory, and reports each reference that rendered
	 * without a value as an error.
	 */
	private void render(VelocityEngine engine, VelocityOneContext context, String templateName, String page, Map<String, byte[]> pages, File output, List<Finding> findings)
			throws EOGeneratorException {
		Template template;
		try {
			template = engine.getTemplate(templateName, "UTF-8");
		} catch (ResourceNotFoundException e) {
			String message = "The template " + templateName + " doesn't exist.";
			throw new EOGeneratorException(message, e, Collections.singletonList(Finding.error(Finding.FAILURE, message).template(templateName).build()));
		} catch (ParseErrorException e) {
			String message = "The template " + templateName + " doesn't parse: " + e.getMessage();
			throw new EOGeneratorException(message, e, Collections.singletonList(Finding.error(Finding.FAILURE, message).template(templateName).build()));
		}
		StringWriter writer = new StringWriter();
		try {
			template.merge(context, writer);
		} catch (RuntimeException e) {
			String message = "The template " + templateName + " failed for " + page + ": " + e.getMessage();
			throw new EOGeneratorException(message, e, Collections.singletonList(Finding.error(Finding.FAILURE, message).template(templateName).build()));
		}
		String path = relative(new File(output, page));
		for (UnresolvedReference reference : context.takeUnresolvedReferences()) {
			findings.add(Finding.error(Finding.UNRESOLVED_REFERENCE, path + ": " + reference.getDescription()).file(path).entity(entityName(context)).object(reference.getObject())
					.key(reference.getReference()).template(reference.getTemplate()).build());
		}
		pages.put(page, writer.toString().getBytes(StandardCharsets.UTF_8));
	}

	private static String entityName(VelocityOneContext context) {
		Object entity = context.get("entity");
		return entity instanceof EOEntity ? ((EOEntity) entity).getName() : null;
	}

	/**
	 * Returns the link from an entity's class name, from its template.
	 */
	private static String entityURL(VelocityEngine engine, String template, EOEntity entity, List<Finding> findings) {
		VelocityOneContext context = new VelocityOneContext();
		context.put("entity", entity);
		context.put("model", entity.getModel());
		StringWriter writer = new StringWriter();
		engine.evaluate(context, writer, "entityURL", template);
		for (UnresolvedReference reference : context.takeUnresolvedReferences()) {
			findings.add(Finding.error(Finding.UNRESOLVED_REFERENCE, "The link from " + entity.getName() + "'s class name: " + reference.getDescription()).entity(entity.getName())
					.object(reference.getObject()).key(reference.getReference()).build());
		}
		return writer.toString();
	}

	/**
	 * Returns a file that is copied as it is: the extra templates' own, or
	 * else the bundled one.
	 */
	private static byte[] copy(File templateFolder, String name) throws EOGeneratorException {
		try {
			if (templateFolder != null) {
				Path file = templateFolder.toPath().resolve(name);
				if (Files.isRegularFile(file)) {
					return Files.readAllBytes(file);
				}
			}
			try (InputStream in = ModelDocGenerator.class.getResourceAsStream(DocResourceLoader.FOLDER + name)) {
				if (in == null) {
					throw new IOException(name + " isn't bundled.");
				}
				return in.readAllBytes();
			}
		} catch (IOException e) {
			throw new EOGeneratorException("Failed to read " + name + ": " + e.getMessage(), e);
		}
	}

	private boolean isProjectModel(EOModel model) {
		if (model.getModelURL() == null || !"file".equals(model.getModelURL().getProtocol())) {
			return false;
		}
		Path path = URLUtils.cheatAndTurnIntoFile(model.getModelURL()).getAbsoluteFile().toPath().normalize();
		for (File folder : modelFolders) {
			if (path.startsWith(folder.getAbsoluteFile().toPath().normalize())) {
				return true;
			}
		}
		return false;
	}

	private File resolve(File file) {
		return file.isAbsolute() ? file : new File(projectDirectory, file.getPath());
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
}

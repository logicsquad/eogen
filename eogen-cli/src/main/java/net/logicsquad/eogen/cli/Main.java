package net.logicsquad.eogen.cli;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.logicsquad.eogen.EOGenerator;
import net.logicsquad.eogen.EOGeneratorException;
import net.logicsquad.eogen.Finding;
import net.logicsquad.eogen.GeneratedFile;
import net.logicsquad.eogen.GeneratedFile.Kind;
import net.logicsquad.eogen.GeneratedFile.Status;
import net.logicsquad.eogen.GenerationResult;
import net.logicsquad.eogen.VerificationResult;
import net.logicsquad.eogen.doc.DocOptions;
import net.logicsquad.eogen.doc.DocResult;
import net.logicsquad.eogen.format.FormatResult;
import net.logicsquad.eogen.format.FormatResult.FormattedFile;
import net.logicsquad.eogen.migration.MigrationOptions;
import net.logicsquad.eogen.migration.MigrationResult;
import net.logicsquad.eogen.migration.MigrationResult.ModelMigration;
import net.logicsquad.eogen.model.EOModelVerificationFailure;
import net.logicsquad.eogen.refactor.RefactorResult;

/**
 * Generates the entity classes for .eogen files, checks them, or verifies the
 * model group; or formats, renames or removes in the models, or writes their
 * migrations or their documentation.
 *
 * <pre>
 * eogen [options] [file.eogen...]
 * </pre>
 */
public final class Main {
	/**
	 * Exit status when generating succeeds, {@code --check} finds nothing stale,
	 * or {@code --verify} finds no errors.
	 */
	static final int OK = 0;

	/**
	 * Exit status when {@code --check} finds stale files or orphans.
	 */
	static final int STALE = 1;

	/**
	 * Exit status for bad arguments, when generating fails, when the model
	 * group or a template has errors and {@code --force} is not given, or when
	 * {@code --verify} finds errors.
	 */
	static final int FAILED = 2;

	private static final String USAGE = String.join(System.lineSeparator(),
			"Usage: eogen [options] <file.eogen>...",
			"       eogen [options] --all",
			"       eogen [options] --verify",
			"       eogen [options] --format",
			"       eogen [options] --rename <Entity[.property]> --to <name>",
			"       eogen [options] --remove <Entity[.property]>",
			"       eogen [options] --migration",
			"       eogen [options] --doc",
			"",
			"Generates the entity classes for .eogen files as WOLips's EOGenerator does:",
			"every superclass whose contents have changed, and every subclass that is",
			"missing.",
			"",
			"Options:",
			"  --project <folder>      The folder that the .eogen files' paths are relative",
			"                          to. Default: the current folder.",
			"  --models <folders>      Folders whose models make up the model group,",
			"                          separated by '" + File.pathSeparator + "'. Default: <project>/src/main/resources.",
			"  --classpath <path>      Jars and folders whose Resources folders add their",
			"                          models to the model group, separated by '" + File.pathSeparator + "'.",
			"  --templates <path>      Jars, with templates in eogen/templates, and folders of",
			"                          templates, separated by '" + File.pathSeparator + "'. Their templates come",
			"                          after the project's own and before WOLips's.",
			"  --all                   Use every .eogen file under the model folders, and",
			"                          report orphans: superclasses that no .eogen file",
			"                          generates any more, and their subclasses.",
			"  --check                 Write nothing. List the files that generating would",
			"                          change, and the orphans, and exit with status 1 if",
			"                          there are any.",
			"  --verify                Load, resolve and verify the model group as Entity",
			"                          Modeler does, and lint the project's models. Exit",
			"                          with status 2 if there are errors.",
			"  --include-dependencies  With --verify, report on the dependencies' models",
			"                          too. With --doc, document them too.",
			"  --format                Format the project's models as Entity Modeler saves",
			"                          them. Only the layout changes; a model whose contents",
			"                          saving would change is left alone, and the exit",
			"                          status is 2. With --check, write nothing, and exit",
			"                          with status 1 if any model needs formatting.",
			"  --rename <target>       Rename an entity, such as Book, or an attribute or",
			"                          relationship, such as Book.titel, to the name that",
			"                          --to gives, through Entity Modeler's model classes,",
			"                          and the keys that name it. Columns and tables stay as",
			"                          they are. With --check, write nothing.",
			"  --rename-class          With --rename, rename an entity's class too, when its",
			"                          simple name is the entity's.",
			"  --remove <target>       Remove an entity, attribute or relationship, unless",
			"                          anything else uses it, when each use is reported and",
			"                          the exit status is 2. With --check, write nothing.",
			"  --migration             Write the next Project Wonder migration for each model",
			"                          whose tables changed since an earlier version of the",
			"                          models, beside its latest migration. A model's first",
			"                          migration creates its tables. Where eogen can't decide,",
			"                          it leaves a gap that doesn't compile, and warns. With",
			"                          --check, write nothing, and exit with status 1 if a",
			"                          model with migrations has changed without one.",
			"  --from <commit|folder>  With --migration, the earlier version of every model: a",
			"                          git commit, or a folder of models. Default: for a model",
			"                          with migrations, the last commit that changed its latest",
			"                          migration with the model, or else added it, and for one",
			"                          with none, HEAD.",
			"  --sources <folders>     With --migration, the folders of the Java sources,",
			"                          separated by '" + File.pathSeparator + "'. Default: <project>/src/main/java.",
			"  --package <name>        With --migration, the package for new migrations.",
			"                          Default: that of the model's latest migration, or of",
			"                          the other models' migrations, or of its entities.",
			"  --template <name>       With --migration, the template: a path relative to the",
			"                          project, or a template's name. Default: Migration.java.",
			"  --migration-models <names>",
			"                          With --migration, the models to write migrations for,",
			"                          separated by ','. A model named here with no migrations",
			"                          gets its first, even if it didn't change.",
			"  --doc                   Write a Markdown page for each of the project's",
			"                          models, with its documentation and a Mermaid diagram",
			"                          of its entities, to commit. Writing deletes the pages",
			"                          that eogen wrote for models that it no longer",
			"                          documents. With --check, write nothing, and exit with",
			"                          status 1 if a page would change, or an orphan would",
			"                          be deleted.",
			"  --doc-format <format>   With --doc, markdown, or html for EOModelDoc's pages:",
			"                          an index, and a page for each model, entity and",
			"                          stored procedure. Default: markdown.",
			"  --output <folder>       With --doc, the folder of the pages. Default:",
			"                          <project>/docs/models, or for HTML,",
			"                          <project>/target/site/eomodeldoc.",
			"  --doc-templates <folder>",
			"                          With --doc, a folder of templates and files, which",
			"                          come before the bundled ones.",
			"  --entity-url <template> With --doc, a Velocity template for the link from",
			"                          each entity's class name, relative to its page.",
			"  --delete-orphans        With --all, delete the orphaned superclasses. Their",
			"                          subclasses are only reported.",
			"  --force                 Write the classes even when the model group or a",
			"                          template has errors, as WOLips does. Without it, errors",
			"                          mean nothing is written, and the exit status is 2.",
			"  --strict                Make the lint's findings in the models, such as a",
			"                          misspelt key, errors rather than warnings.",
			"  --json                  Report in JSON on standard output, for scripts and",
			"                          agents.",
			"  --debug                 Show stack traces.",
			"  --help                  Show this help.",
			"  --version               Show eogen's version.");

	private File project = new File(".");

	private List<File> models;

	private List<File> classpath = Collections.emptyList();

	private List<File> templates = Collections.emptyList();

	private String rename;

	private String to;

	private boolean renameClass;

	private String remove;

	private boolean migration;

	private String from;

	private List<File> sources;

	private String packageName;

	private String template;

	private List<String> migrationModels = Collections.emptyList();

	private boolean doc;

	private File output;

	private DocOptions.Format docFormat;

	private File docTemplates;

	private String entityURL;

	private boolean all;

	private boolean check;

	private boolean verify;

	private boolean format;

	private boolean includeDependencies;

	private boolean deleteOrphans;

	private boolean force;

	private boolean strict;

	private boolean json;

	private boolean debug;

	private final List<File> eogenFiles = new ArrayList<>();

	private final PrintStream out;

	private final PrintStream err;

	/**
	 * The JSON report, when {@code --json} is given.
	 */
	private final Map<String, Object> report = new LinkedHashMap<>();

	private Main(PrintStream out, PrintStream err) {
		this.out = out;
		this.err = err;
	}

	/**
	 * Runs eogen, and exits with its status.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		String level = Arrays.asList(args).contains("--debug") ? "debug" : "warn";
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", System.getProperty("org.slf4j.simpleLogger.defaultLogLevel", level));
		System.exit(run(args, System.out, System.err));
	}

	/**
	 * Runs eogen.
	 *
	 * @param args command-line arguments
	 * @param out where to report what was generated, or the JSON report
	 * @param err where to report problems
	 * @return exit status: {@link #OK}, {@link #STALE} or {@link #FAILED}
	 */
	static int run(String[] args, PrintStream out, PrintStream err) {
		Main main = new Main(out, err);
		Integer status = main.parse(args);
		if (status != null) {
			return status;
		}
		status = main.run();
		if (main.json) {
			main.report.put("status", status == OK ? "ok" : status == STALE ? "stale" : "failed");
			Map<String, Object> ordered = new LinkedHashMap<>();
			ordered.put("status", main.report.remove("status"));
			ordered.putAll(main.report);
			out.println(Json.write(ordered));
		}
		return status;
	}

	/**
	 * Reads the arguments.
	 *
	 * @return an exit status if eogen should stop, or null
	 */
	private Integer parse(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if ("--help".equals(arg) || "-h".equals(arg)) {
				out.println(USAGE);
				return OK;
			} else if ("--version".equals(arg)) {
				String version = Main.class.getPackage().getImplementationVersion();
				out.println("eogen " + (version == null ? "(development)" : version));
				return OK;
			} else if ("--all".equals(arg)) {
				all = true;
			} else if ("--check".equals(arg)) {
				check = true;
			} else if ("--verify".equals(arg)) {
				verify = true;
			} else if ("--rename-class".equals(arg)) {
				renameClass = true;
			} else if ("--format".equals(arg)) {
				format = true;
			} else if ("--migration".equals(arg)) {
				migration = true;
			} else if ("--doc".equals(arg)) {
				doc = true;
			} else if ("--include-dependencies".equals(arg)) {
				includeDependencies = true;
			} else if ("--delete-orphans".equals(arg)) {
				deleteOrphans = true;
			} else if ("--force".equals(arg)) {
				force = true;
			} else if ("--strict".equals(arg)) {
				strict = true;
			} else if ("--json".equals(arg)) {
				json = true;
			} else if ("--debug".equals(arg)) {
				debug = true;
			} else if ("--project".equals(arg) || "--models".equals(arg) || "--classpath".equals(arg) || "--templates".equals(arg) || "--rename".equals(arg)
					|| "--to".equals(arg) || "--remove".equals(arg) || "--from".equals(arg) || "--sources".equals(arg) || "--package".equals(arg) || "--template".equals(arg)
					|| "--migration-models".equals(arg) || "--output".equals(arg) || "--doc-templates".equals(arg) || "--entity-url".equals(arg) || "--doc-format".equals(arg)) {
				if (i + 1 == args.length) {
					return usage(arg + " needs a value.");
				}
				String value = args[++i];
				if ("--project".equals(arg)) {
					project = new File(value);
				} else if ("--models".equals(arg)) {
					models = files(value);
				} else if ("--templates".equals(arg)) {
					templates = files(value);
				} else if ("--rename".equals(arg)) {
					rename = value;
				} else if ("--to".equals(arg)) {
					to = value;
				} else if ("--remove".equals(arg)) {
					remove = value;
				} else if ("--from".equals(arg)) {
					from = value;
				} else if ("--sources".equals(arg)) {
					sources = files(value);
				} else if ("--package".equals(arg)) {
					packageName = value;
				} else if ("--template".equals(arg)) {
					template = value;
				} else if ("--output".equals(arg)) {
					output = new File(value);
				} else if ("--doc-templates".equals(arg)) {
					docTemplates = new File(value);
				} else if ("--doc-format".equals(arg)) {
					if ("html".equalsIgnoreCase(value)) {
						docFormat = DocOptions.Format.HTML;
					} else if ("markdown".equalsIgnoreCase(value)) {
						docFormat = DocOptions.Format.MARKDOWN;
					} else {
						return usage("--doc-format is html or markdown, not " + value + ".");
					}
				} else if ("--entity-url".equals(arg)) {
					entityURL = value;
				} else if ("--migration-models".equals(arg)) {
					migrationModels = new ArrayList<>();
					for (String name : value.split(",")) {
						if (!name.trim().isEmpty()) {
							migrationModels.add(name.trim());
						}
					}
				} else {
					classpath = files(value);
				}
			} else if (arg.startsWith("-")) {
				return usage("Unknown option " + arg + ".");
			} else {
				eogenFiles.add(new File(arg));
			}
		}
		if (doc && (verify || format || migration || all || deleteOrphans || rename != null || remove != null || !eogenFiles.isEmpty())) {
			return usage("--doc takes no .eogen files, and goes only with --check, --force, --include-dependencies and its own options.");
		}
		if (!doc && (output != null || docFormat != null || docTemplates != null || entityURL != null)) {
			return usage("--output, --doc-format, --doc-templates and --entity-url go with --doc.");
		}
		if (migration && (verify || format || all || deleteOrphans || rename != null || remove != null || !eogenFiles.isEmpty())) {
			return usage("--migration takes no .eogen files, and goes only with --check and its own options.");
		}
		if (!migration && (from != null || sources != null || packageName != null || template != null || !migrationModels.isEmpty())) {
			return usage("--from, --sources, --package, --template and --migration-models go with --migration.");
		}
		if (format && (verify || all || deleteOrphans || !eogenFiles.isEmpty())) {
			return usage("--format takes no .eogen files, and goes only with --check.");
		}
		if (verify && (check || all || deleteOrphans || !eogenFiles.isEmpty())) {
			return usage("--verify takes no .eogen files, and doesn't go with --check, --all or --delete-orphans.");
		}
		if (all && !eogenFiles.isEmpty()) {
			return usage("Give .eogen files or --all, not both.");
		}
		if (deleteOrphans && (!all || check)) {
			return usage("--delete-orphans needs --all, and doesn't go with --check.");
		}
		if (includeDependencies && !verify && !doc) {
			return usage("--include-dependencies goes with --verify or --doc.");
		}
		if (!verify && !format && !migration && !doc && !all && rename == null && remove == null && eogenFiles.isEmpty()) {
			return usage("Give an .eogen file.");
		}
		for (File eogenFile : eogenFiles) {
			if (!eogenFile.isFile()) {
				return failed(eogenFile + " does not exist.");
			}
		}
		if (!project.isDirectory()) {
			return failed("the project folder " + project + " does not exist.");
		}
		if (models == null) {
			models = Collections.singletonList(new File(project, EOGenerator.DEFAULT_MODEL_FOLDER));
		}
		return null;
	}

	private int run() {
		EOGenerator generator = new EOGenerator(project, models, classpath);
		generator.setForce(force);
		generator.setStrict(strict);
		generator.setTemplatePath(templates);
		if (rename != null) {
			if (to == null) {
				return usage("--rename needs --to.");
			}
			try {
				return refactor(generator.rename(rename, to, renameClass, !check), "Renaming " + rename + " to " + to);
			} catch (EOGeneratorException e) {
				return failed(e.getMessage(), e);
			}
		}
		if (remove != null) {
			try {
				return refactor(generator.remove(remove, !check), "Removing " + remove);
			} catch (EOGeneratorException e) {
				return failed(e.getMessage(), e);
			}
		}
		if (verify) {
			return verify(generator);
		}
		if (migration) {
			return migration(generator);
		}
		if (doc) {
			return doc(generator);
		}
		if (format) {
			return format(generator);
		}
		if (all) {
			try {
				eogenFiles.addAll(EOGenerator.findEOGenFiles(models));
			} catch (IOException e) {
				return failed("failed to find the .eogen files: " + e.getMessage(), e);
			}
			if (eogenFiles.isEmpty()) {
				warn("no .eogen files in " + models + ".");
			}
		}
		int status = OK;
		List<GenerationResult> results = new ArrayList<>();
		List<Object> eogenReports = new ArrayList<>();
		report.put("eogenFiles", eogenReports);
		boolean prefix = eogenFiles.size() > 1 || all;
		for (File eogenFile : eogenFiles) {
			String name = prefix ? relative(eogenFile) + ": " : "";
			Map<String, Object> eogenReport = new LinkedHashMap<>();
			eogenReport.put("file", relative(eogenFile));
			eogenReports.add(eogenReport);
			GenerationResult result;
			try {
				result = check ? generator.check(eogenFile) : generator.generate(eogenFile);
			} catch (EOGeneratorException e) {
				eogenReport.put("errors", findings(e.getFindings(), true));
				failed(name + e.getMessage(), e);
				status = FAILED;
				continue;
			}
			results.add(result);
			status = Math.max(status, report(name, eogenFile, result, eogenReport));
		}
		if (all && status != FAILED) {
			status = Math.max(status, orphans(generator, results));
		}
		return status;
	}

	/**
	 * Reports one .eogen file's result.
	 *
	 * @return exit status for it
	 */
	private int report(String name, File eogenFile, GenerationResult result, Map<String, Object> eogenReport) {
		eogenReport.put("written", result.isWritten());
		eogenReport.put("errors", findings(result.getFindings(), true));
		eogenReport.put("warnings", findings(result.getFindings(), false));
		List<Object> files = new ArrayList<>();
		for (GeneratedFile file : result.getFiles()) {
			files.add(describe(file));
		}
		eogenReport.put("files", files);
		for (String warning : result.getWarnings()) {
			warn(name + warning);
		}
		for (String error : result.getErrors()) {
			problem(name + error);
		}
		if (result.hasErrors() && !force) {
			int errors = result.getErrors().size();
			problem(name + errors + (errors == 1 ? " error" : " errors") + (check ? "." : ", so nothing was written. Fix them, or use --force to write anyway."));
			return FAILED;
		}
		if (check) {
			for (GeneratedFile file : result.getStaleFiles()) {
				say(relative(file.getFile()) + (file.getStatus() == Status.CREATED ? " is missing." : " is out of date."));
			}
			if (result.isStale()) {
				return STALE;
			}
			say(name + "All " + result.count(Kind.SUPERCLASS, Status.UNCHANGED) + " generated superclasses are up to date.");
			return OK;
		}
		for (GeneratedFile file : result.getStaleFiles()) {
			say((file.getStatus() == Status.CREATED ? "Created " : "Updated ") + relative(file.getFile()));
		}
		say(eogenFile.getName() + ": " + result.count(Kind.SUPERCLASS, Status.CREATED) + " superclasses created, " + result.count(Kind.SUPERCLASS, Status.UPDATED)
				+ " updated, " + result.count(Kind.SUPERCLASS, Status.UNCHANGED) + " unchanged; " + result.count(Kind.SUBCLASS, Status.CREATED) + " subclasses created.");
		return OK;
	}

	/**
	 * Reports, and with {@code --delete-orphans} deletes, the orphans.
	 *
	 * @return exit status for them
	 */
	private int orphans(EOGenerator generator, List<GenerationResult> results) {
		List<GeneratedFile> orphans;
		List<GeneratedFile> deleted = Collections.emptyList();
		try {
			orphans = generator.findOrphans(results);
			if (deleteOrphans) {
				deleted = EOGenerator.deleteOrphans(orphans);
			}
		} catch (EOGeneratorException e) {
			return failed(e.getMessage(), e);
		}
		List<Object> orphanReports = new ArrayList<>();
		for (GeneratedFile orphan : orphans) {
			Map<String, Object> orphanReport = describe(orphan);
			orphanReport.put("deleted", deleted.contains(orphan));
			orphanReports.add(orphanReport);
			if (deleted.contains(orphan)) {
				say("Deleted " + relative(orphan.getFile()) + ", which no .eogen file generates any more.");
			} else if (orphan.getKind() == Kind.SUBCLASS) {
				say(relative(orphan.getFile()) + " is the subclass of an orphaned superclass" + (check ? "." : ". Delete it by hand if its entity is gone."));
			} else {
				say(relative(orphan.getFile()) + " is an orphan: no .eogen file generates it any more.");
			}
		}
		report.put("orphans", orphanReports);
		return check && !orphans.isEmpty() ? STALE : OK;
	}

	private int verify(EOGenerator generator) {
		VerificationResult result;
		try {
			result = generator.verify(includeDependencies);
		} catch (EOGeneratorException e) {
			return failed(e.getMessage(), e);
		}
		Map<String, Object> verification = new LinkedHashMap<>();
		verification.put("models", result.getModelNames());
		verification.put("errors", findings(result.getFindings(), true));
		verification.put("warnings", findings(result.getFindings(), false));
		report.put("verification", verification);
		if (result.getModelNames().isEmpty()) {
			warn("no models in " + models + ".");
			return OK;
		}
		for (EOModelVerificationFailure warning : result.getWarnings()) {
			warn(describe(warning));
		}
		for (EOModelVerificationFailure error : result.getErrors()) {
			problem(describe(error));
		}
		int errors = result.getErrors().size();
		int warnings = result.getWarnings().size();
		say("Verified " + String.join(", ", result.getModelNames()) + ": " + errors + (errors == 1 ? " error, " : " errors, ") + warnings
				+ (warnings == 1 ? " warning." : " warnings."));
		return errors > 0 ? FAILED : OK;
	}

	private int format(EOGenerator generator) {
		FormatResult result;
		try {
			result = generator.format(!check);
		} catch (EOGeneratorException e) {
			return failed(e.getMessage(), e);
		}
		Map<String, Object> formatReport = new LinkedHashMap<>();
		formatReport.put("errors", findings(result.getFindings(), true));
		List<Object> files = new ArrayList<>();
		for (FormattedFile file : result.getRefusedFiles()) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("file", relative(file.getFile()));
			map.put("status", "refused");
			map.put("reason", file.getReason());
			files.add(map);
		}
		for (FormattedFile file : result.getFormattedFiles()) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("file", relative(file.getFile()));
			map.put("status", "formatted");
			files.add(map);
		}
		formatReport.put("files", files);
		report.put("format", formatReport);
		for (String error : result.getErrors()) {
			problem(error);
		}
		if (!result.getErrors().isEmpty()) {
			problem("the model group has errors, so no model was formatted.");
			return FAILED;
		}
		for (FormattedFile file : result.getRefusedFiles()) {
			problem(relative(file.getFile()) + ": its model is left alone, since saving it would change what it says: " + file.getReason() + ".");
		}
		for (FormattedFile file : result.getFormattedFiles()) {
			say(check ? relative(file.getFile()) + " is not formatted as Entity Modeler writes it." : "Formatted " + relative(file.getFile()));
		}
		if (!result.getRefusedFiles().isEmpty()) {
			return FAILED;
		}
		if (check && !result.getFormattedFiles().isEmpty()) {
			return STALE;
		}
		say(result.getFiles().size() + " model files checked, " + result.getFormattedFiles().size() + (check ? " to format." : " formatted."));
		return OK;
	}

	/**
	 * Writes EOModelDoc's pages, and reports them.
	 *
	 * @return exit status for them
	 */
	private int doc(EOGenerator generator) {
		DocOptions options = new DocOptions();
		options.setFormat(docFormat);
		// The core resolves a relative folder against the project, but the command
		// line's are relative to the current folder, as the project's is.
		options.setOutput((output == null ? new File(project, options.getOutput().getPath()) : output).getAbsoluteFile());
		options.setTemplateFolder(docTemplates == null ? null : docTemplates.getAbsoluteFile());
		options.setEntityURL(entityURL);
		options.setIncludeDependencies(includeDependencies);
		DocResult result;
		try {
			result = generator.generateDoc(options, !check);
		} catch (EOGeneratorException e) {
			return failed(e.getMessage(), e);
		}
		Map<String, Object> docReport = new LinkedHashMap<>();
		docReport.put("written", result.isWritten());
		docReport.put("models", result.getModels());
		List<Object> files = new ArrayList<>();
		for (DocResult.Page page : result.getPages()) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("file", relative(page.getFile()));
			map.put("status", page.getStatus().name().toLowerCase(Locale.ROOT));
			files.add(map);
		}
		for (File orphan : result.getOrphans()) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("file", relative(orphan));
			map.put("status", "orphaned");
			files.add(map);
		}
		docReport.put("files", files);
		docReport.put("errors", findings(result.getFindings(), true));
		report.put("doc", docReport);
		for (Finding finding : result.getFindings()) {
			if (force) {
				warn(finding.getMessage());
			} else {
				problem(finding.getMessage());
			}
		}
		if (result.hasErrors() && !force) {
			problem("there are errors, so no page was " + (check ? "checked" : "written") + ". Fix them, or use --force to " + (check ? "check" : "write") + " anyway.");
			return FAILED;
		}
		String folder = relative(options.getOutput());
		int pages = result.getPages().size();
		if (check) {
			for (DocResult.Page page : result.getStalePages()) {
				say(relative(page.getFile()) + (page.getStatus() == DocResult.Status.CREATED ? " is missing." : " is out of date."));
			}
			for (File orphan : result.getOrphans()) {
				say(relative(orphan) + " is an orphan: eogen wrote it for a model that it no longer documents.");
			}
			if (result.isStale()) {
				return STALE;
			}
			say((pages == 1 ? "The page" : "All " + pages + " pages") + " of " + String.join(", ", result.getModels()) + " in " + folder + (pages == 1 ? " is" : " are") + " up to date.");
			return OK;
		}
		for (File orphan : result.getOrphans()) {
			say("Deleted " + relative(orphan) + ", which eogen wrote for a model that it no longer documents.");
		}
		say("Documented " + String.join(", ", result.getModels()) + " in " + folder + ": " + pages + (pages == 1 ? " file, " : " files, ") + result.getStalePages().size()
				+ " written.");
		return OK;
	}

	/**
	 * Writes the migrations, and reports them.
	 *
	 * @return exit status for them
	 */
	private int migration(EOGenerator generator) {
		MigrationOptions options = new MigrationOptions();
		options.setFrom(from);
		options.setPackageName(packageName);
		options.setTemplate(template);
		options.setModels(migrationModels);
		options.setSourceFolders(sources == null ? Collections.singletonList(new File(project, MigrationOptions.DEFAULT_SOURCE_FOLDER)) : sources);
		MigrationResult result;
		try {
			result = generator.generateMigrations(options, !check);
		} catch (EOGeneratorException e) {
			return failed(e.getMessage(), e);
		}
		Map<String, Object> migrationReport = new LinkedHashMap<>();
		migrationReport.put("since", result.getSince());
		migrationReport.put("written", result.isWritten());
		List<Object> migrations = new ArrayList<>();
		for (ModelMigration model : result.getMigrations()) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("model", model.getModel());
			map.put("status", model.getStatus().name().toLowerCase(Locale.ROOT));
			if (model.getFile() != null) {
				map.put("file", relative(model.getFile()));
				map.put("class", model.getClassName());
				map.put("number", model.getNumber());
				map.put("first", model.isFirst());
				map.put("gaps", model.getGaps());
			}
			put(map, "since", model.getSince());
			if (!model.getChanges().isEmpty()) {
				map.put("changes", model.getChanges());
			}
			put(map, "reason", model.getReason());
			migrations.add(map);
		}
		migrationReport.put("migrations", migrations);
		migrationReport.put("errors", findings(result.getFindings(), true));
		migrationReport.put("warnings", findings(result.getFindings(), false));
		report.put("migration", migrationReport);
		for (Finding finding : result.getFindings()) {
			if (finding.isError()) {
				problem(finding.getMessage());
			}
		}
		if (result.hasErrors()) {
			problem("there are errors, so no migration was written.");
			return FAILED;
		}
		List<ModelMigration> missing = result.getMissing();
		int created = 0;
		for (ModelMigration model : result.getMigrations()) {
			if (check && missing.contains(model)) {
				say(model.describeMissing());
			} else if (model.getStatus() == MigrationResult.Status.CREATED) {
				created++;
				int gaps = model.getGaps();
				say((check ? "Would create " : "Created ") + relative(model.getFile()) + (gaps == 0 ? "." : ", with " + gaps + (gaps == 1 ? " gap" : " gaps") + " to fill in."));
			} else {
				say(model.getModel() + ": " + model.getReason());
			}
		}
		for (Finding finding : result.getFindings()) {
			if (!finding.isError()) {
				warn(finding.getMessage());
			}
		}
		if (check && !missing.isEmpty()) {
			say(missing.size() + (missing.size() == 1 ? " model has" : " models have") + " changed with no migration.");
			String keeping = result.describeKeeping();
			if (keeping != null) {
				say(keeping);
			}
			return STALE;
		}
		say((check ? "Would write " : "Wrote ") + created + (created == 1 ? " migration" : " migrations") + " for the changes since " + result.getSince() + ".");
		return OK;
	}

	/**
	 * Reports a rename or a removal.
	 *
	 * @return exit status for it
	 */
	private int refactor(RefactorResult result, String what) {
		Map<String, Object> refactorReport = new LinkedHashMap<>();
		List<Object> files = new ArrayList<>();
		for (RefactorResult.ChangedFile file : result.getFiles()) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("file", relative(file.getFile()));
			map.put("status", file.getStatus().name().toLowerCase(Locale.ROOT));
			files.add(map);
		}
		refactorReport.put("written", result.isWritten());
		refactorReport.put("files", files);
		refactorReport.put("errors", findings(result.getFindings(), true));
		refactorReport.put("warnings", findings(result.getFindings(), false));
		report.put("refactor", refactorReport);
		for (Finding finding : result.getFindings()) {
			if (finding.isError()) {
				problem(finding.getMessage());
			}
		}
		if (result.isRefused()) {
			problem(what + " was refused, so nothing changed.");
			return FAILED;
		}
		for (RefactorResult.ChangedFile file : result.getFiles()) {
			say(verb(file.getStatus()) + " " + relative(file.getFile()));
		}
		for (Finding finding : result.getFindings()) {
			if (!finding.isError()) {
				warn(finding.getMessage());
			}
		}
		say(what + (check ? " would change " : " changed ") + result.getFiles().size() + (result.getFiles().size() == 1 ? " model file." : " model files."));
		return OK;
	}

	private String verb(RefactorResult.Status status) {
		switch (status) {
		case CREATED:
			return check ? "Would create" : "Created";
		case DELETED:
			return check ? "Would delete" : "Deleted";
		default:
			return check ? "Would change" : "Changed";
		}
	}

	/**
	 * Returns the errors, or the warnings, among findings, as JSON objects.
	 */
	private static List<Object> findings(List<Finding> findings, boolean errors) {
		List<Object> list = new ArrayList<>();
		for (Finding finding : findings) {
			if (finding.isError() == errors) {
				list.add(describe(finding));
			}
		}
		return list;
	}

	/**
	 * Returns a finding as a JSON object, with only the fields eogen knows.
	 */
	private static Map<String, Object> describe(Finding finding) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("severity", finding.getSeverity().name().toLowerCase(Locale.ROOT));
		map.put("code", finding.getCode());
		map.put("message", finding.getMessage());
		put(map, "file", finding.getFile());
		put(map, "line", finding.getLine());
		put(map, "column", finding.getColumn());
		put(map, "model", finding.getModel());
		put(map, "entity", finding.getEntity());
		put(map, "object", finding.getObject());
		put(map, "key", finding.getKey());
		put(map, "suggestion", finding.getSuggestion());
		put(map, "template", finding.getTemplate());
		return map;
	}

	private static void put(Map<String, Object> map, String key, Object value) {
		if (value != null) {
			map.put(key, value);
		}
	}

	private Map<String, Object> describe(GeneratedFile file) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("file", relative(file.getFile()));
		map.put("kind", file.getKind().name().toLowerCase(Locale.ROOT));
		map.put("status", file.getStatus().name().toLowerCase(Locale.ROOT));
		return map;
	}

	private static String describe(EOModelVerificationFailure failure) {
		return (failure.getModel() == null ? "" : failure.getModel().getName() + ": ") + failure.getMessage();
	}

	/**
	 * Returns a file's path relative to the project, with / separators on every
	 * platform, or its absolute path if it is outside the project.
	 */
	private String relative(File file) {
		Path projectPath = project.getAbsoluteFile().toPath().normalize();
		Path filePath = file.getAbsoluteFile().toPath().normalize();
		return filePath.startsWith(projectPath) ? projectPath.relativize(filePath).toString().replace(File.separatorChar, '/') : filePath.toString();
	}

	private void say(String message) {
		if (!json) {
			out.println(message);
		}
	}

	private void warn(String message) {
		err.println("eogen: warning: " + message);
	}

	private void problem(String message) {
		err.println("eogen: " + message);
	}

	private int failed(String message) {
		return failed(message, null);
	}

	private int failed(String message, Throwable cause) {
		problem(message);
		if (debug && cause != null) {
			StringWriter trace = new StringWriter();
			cause.printStackTrace(new PrintWriter(trace));
			err.print(trace);
		}
		@SuppressWarnings("unchecked")
		List<Object> problems = (List<Object>) report.computeIfAbsent("problems", key -> new ArrayList<>());
		if (cause instanceof EOGeneratorException) {
			problems.addAll(findings(((EOGeneratorException) cause).getFindings(), true));
		} else {
			problems.add(describe(Finding.error(Finding.FAILURE, message).build()));
		}
		return FAILED;
	}

	private int usage(String problem) {
		err.println("eogen: " + problem);
		err.println(USAGE);
		return FAILED;
	}

	private static List<File> files(String path) {
		List<File> files = new ArrayList<>();
		for (String entry : path.split(File.pathSeparator)) {
			if (!entry.isEmpty()) {
				files.add(new File(entry));
			}
		}
		return files;
	}
}

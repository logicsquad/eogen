package net.logicsquad.eogen.migration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOModel;

/**
 * Names a model's next migration as Project Wonder's {@code ERXMigrator} looks
 * for it: the model's {@code MigrationClassPrefix}, which is its name unless
 * the project's {@code Properties} say otherwise, then the number after the
 * highest of its migrations in the source folders, or after its
 * {@code InitialMigrationVersion}, which is -1 by default.
 * <p>
 * A prefix with a package, such as {@code com.example.migration.Shop}, puts
 * the migration in that package, where {@code ERXMigrator} looks for it.
 * Otherwise it finds a migration by its simple name, in any package, and the
 * new one goes in a package given as an option, or else in that of the
 * model's latest migration. A model's first migration goes where most of the
 * project's other models' latest migrations are, or else in the package of
 * most of the model's entity classes. A vendor-specific migration, such as
 * {@code ShopPostgreSQL2},
 * counts too, but only if its source names a migration class, so that an
 * entity class such as {@code ShopItem2} doesn't.
 * </p>
 */
final class MigrationNaming {
	private final String prefix;

	private final String packageName;

	private final int number;

	private final boolean first;

	private final File sourceFolder;

	private final boolean placed;

	private MigrationNaming(String prefix, String packageName, int number, boolean first, File sourceFolder, boolean placed) {
		this.prefix = prefix;
		this.packageName = packageName;
		this.number = number;
		this.first = first;
		this.sourceFolder = sourceFolder;
		this.placed = placed;
	}

	/**
	 * Names a model's next migration.
	 *
	 * @param model the model
	 * @param properties the project's properties
	 * @param sourceFolders the folders of the project's Java sources, which
	 *        must not be empty
	 * @param packageOption the package for a migration, or null
	 * @return its name
	 * @throws IOException if a source folder can't be searched
	 */
	static MigrationNaming of(EOModel model, Properties properties, List<File> sourceFolders, String packageOption) throws IOException {
		String fullPrefix = properties.getProperty(model.getName() + ".MigrationClassPrefix", model.getName()).trim();
		int dot = fullPrefix.lastIndexOf('.');
		String prefix = fullPrefix.substring(dot + 1);
		String prefixPackage = dot < 0 ? null : fullPrefix.substring(0, dot);
		Pattern plain = Pattern.compile(Pattern.quote(prefix) + "(\\d+)\\.java");
		Pattern vendor = Pattern.compile(Pattern.quote(prefix) + "[A-Z][A-Za-z]*(\\d+)\\.java");
		int highest = -1;
		boolean found = false;
		File latestFolder = null;
		String latestPackage = null;
		for (File sourceFolder : sourceFolders) {
			if (!sourceFolder.isDirectory()) {
				continue;
			}
			List<Path> files;
			try (Stream<Path> paths = Files.walk(sourceFolder.toPath())) {
				files = paths.filter(path -> path.getFileName().toString().startsWith(prefix) && path.getFileName().toString().endsWith(".java") && Files.isRegularFile(path))
						.sorted().collect(Collectors.toList());
			}
			for (Path file : files) {
				String name = file.getFileName().toString();
				Path parent = sourceFolder.toPath().relativize(file).getParent();
				String filePackage = parent == null ? null : parent.toString().replace(File.separatorChar, '.');
				if (prefixPackage != null && !prefixPackage.equals(filePackage)) {
					continue;
				}
				Matcher matcher = plain.matcher(name);
				if (!matcher.matches()) {
					matcher = vendor.matcher(name);
					if (!matcher.matches() || !namesMigration(file)) {
						continue;
					}
				}
				int number;
				try {
					number = Integer.parseInt(matcher.group(1));
				} catch (NumberFormatException e) {
					continue;
				}
				found = true;
				if (number > highest || latestFolder == null) {
					highest = Math.max(highest, number);
					latestFolder = sourceFolder;
					latestPackage = filePackage;
				}
			}
		}
		int next;
		if (found) {
			next = highest + 1;
		} else {
			int initial = -1;
			try {
				initial = Integer.parseInt(properties.getProperty(model.getName() + ".InitialMigrationVersion", "-1").trim());
			} catch (NumberFormatException e) {
				// ERXMigrator would fail on it: the first migration is the 0th.
			}
			next = initial + 1;
		}
		String packageName;
		boolean placed = true;
		if (prefixPackage != null) {
			packageName = prefixPackage;
		} else if (packageOption != null && !packageOption.trim().isEmpty()) {
			packageName = packageOption.trim();
		} else if (found) {
			packageName = latestPackage;
		} else {
			packageName = entityPackage(model);
			placed = false;
		}
		return new MigrationNaming(prefix, packageName, next, !found, found ? latestFolder : sourceFolders.get(0), placed);
	}

	/**
	 * Returns the names of some models' next migrations, with each first
	 * migration that nothing else places, in the package and source folder
	 * where most of the other models' latest migrations are, if they have
	 * any.
	 *
	 * @param namings models' names, by model name
	 * @return names, by model name
	 */
	static Map<String, MigrationNaming> place(Map<String, MigrationNaming> namings) {
		Map<String, Integer> counts = new TreeMap<>();
		Map<String, File> folders = new TreeMap<>();
		for (MigrationNaming naming : namings.values()) {
			if (!naming.first) {
				String key = naming.packageName == null ? "" : naming.packageName;
				counts.merge(key, 1, Integer::sum);
				folders.putIfAbsent(key, naming.sourceFolder);
			}
		}
		String best = null;
		for (Map.Entry<String, Integer> count : counts.entrySet()) {
			if (best == null || count.getValue() > counts.get(best)) {
				best = count.getKey();
			}
		}
		Map<String, MigrationNaming> placed = new TreeMap<>();
		for (Map.Entry<String, MigrationNaming> entry : namings.entrySet()) {
			MigrationNaming naming = entry.getValue();
			if (!naming.placed && best != null) {
				naming = new MigrationNaming(naming.prefix, best.isEmpty() ? null : best, naming.number, naming.first, folders.get(best), true);
			}
			placed.put(entry.getKey(), naming);
		}
		return placed;
	}

	/**
	 * Returns whether a Java file names a migration class, as a vendor-specific
	 * migration does.
	 */
	private static boolean namesMigration(Path file) {
		try {
			return new String(Files.readAllBytes(file), StandardCharsets.UTF_8).contains("Migration");
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Returns the package that most of a model's entity classes are in, or
	 * null.
	 */
	private static String entityPackage(EOModel model) {
		Map<String, Integer> counts = new TreeMap<>();
		for (EOEntity entity : model.getEntities()) {
			String className = entity.getClassName();
			if (entity.isPrototype() || className == null || className.lastIndexOf('.') < 1) {
				continue;
			}
			counts.merge(className.substring(0, className.lastIndexOf('.')), 1, Integer::sum);
		}
		String best = null;
		for (Map.Entry<String, Integer> count : counts.entrySet()) {
			if (best == null || count.getValue() > counts.get(best)) {
				best = count.getKey();
			}
		}
		return best;
	}

	/**
	 * Reads the Wonder properties that name migrations from the
	 * {@code Properties} file in each of the project's model folders, where a
	 * Maven project, or Resources in an older one, keeps it. A property in an
	 * earlier folder wins.
	 *
	 * @param modelFolders the project's model folders
	 * @return properties
	 * @throws IOException if a file can't be read
	 */
	static Properties properties(List<File> modelFolders) throws IOException {
		Properties properties = new Properties();
		List<File> folders = new ArrayList<>(modelFolders);
		Collections.reverse(folders);
		for (File folder : folders) {
			File file = new File(folder, "Properties");
			if (file.isFile()) {
				try (InputStream in = Files.newInputStream(file.toPath())) {
					properties.load(in);
				}
			}
		}
		return properties;
	}

	/**
	 * Returns the highest of the migrations of a model in a dependency, among
	 * the classes in jars and folders, which {@code ERXMigrator} finds by their
	 * simple names: the model's {@code MigrationClassPrefix}, from the
	 * project's properties, or else from the {@code Properties} beside the
	 * model, then a number, as with a vendor's name between them. A class
	 * counts only if it names Wonder's migration classes, so that an entity
	 * class doesn't. With none, it is the model's
	 * {@code InitialMigrationVersion}, if one is set.
	 *
	 * @param model a model from a dependency
	 * @param properties the project's properties
	 * @param classpath jars and folders of classes
	 * @return the highest version, or -1 if eogen can't tell one
	 * @throws IOException if a jar or folder can't be read
	 */
	static int highestOnClasspath(EOModel model, Properties properties, List<File> classpath) throws IOException {
		Properties beside = besideModel(model);
		String fullPrefix = properties.getProperty(model.getName() + ".MigrationClassPrefix", beside.getProperty(model.getName() + ".MigrationClassPrefix", model.getName())).trim();
		int dot = fullPrefix.lastIndexOf('.');
		String prefix = fullPrefix.substring(dot + 1);
		String folder = dot < 0 ? null : fullPrefix.substring(0, dot).replace('.', '/');
		Pattern name = Pattern.compile("(?:(.*)/)?" + Pattern.quote(prefix) + "(?:[A-Z][A-Za-z]*)?(\\d+)\\.class");
		int highest = -1;
		for (File entry : classpath) {
			if (entry.isDirectory()) {
				List<Path> files;
				try (Stream<Path> paths = Files.walk(entry.toPath())) {
					files = paths.filter(path -> path.getFileName().toString().startsWith(prefix) && Files.isRegularFile(path)).collect(Collectors.toList());
				}
				for (Path file : files) {
					int number = number(name, folder, entry.toPath().relativize(file).toString().replace(File.separatorChar, '/'));
					if (number > highest && referencesMigrations(Files.readAllBytes(file))) {
						highest = number;
					}
				}
			} else if (entry.isFile() && entry.getName().endsWith(".jar")) {
				try (ZipFile jar = new ZipFile(entry)) {
					for (ZipEntry jarEntry : Collections.list(jar.entries())) {
						int number = number(name, folder, jarEntry.getName());
						if (number > highest) {
							try (InputStream in = jar.getInputStream(jarEntry)) {
								if (referencesMigrations(in.readAllBytes())) {
									highest = number;
								}
							}
						}
					}
				}
			}
		}
		if (highest < 0) {
			String initial = properties.getProperty(model.getName() + ".InitialMigrationVersion", beside.getProperty(model.getName() + ".InitialMigrationVersion", "-1"));
			try {
				highest = Math.max(-1, Integer.parseInt(initial.trim()));
			} catch (NumberFormatException e) {
				// As with the project's own models, the first migration is the 0th.
			}
		}
		return highest;
	}

	/**
	 * Returns the number of a class file that a migration's name matches, in
	 * its package if it must have one, or -1.
	 */
	private static int number(Pattern name, String folder, String path) {
		Matcher matcher = name.matcher(path);
		if (!matcher.matches() || folder != null && !folder.equals(matcher.group(1))) {
			return -1;
		}
		try {
			return Integer.parseInt(matcher.group(2));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Returns whether a class file names a class of Wonder's migration package,
	 * as a migration's superclass or interface does.
	 */
	private static boolean referencesMigrations(byte[] classFile) {
		return new String(classFile, StandardCharsets.ISO_8859_1).contains("er/extensions/migration/");
	}

	/**
	 * Reads the {@code Properties} file in the folder that has a model, as a
	 * framework's {@code Resources} does, if there is one.
	 */
	private static Properties besideModel(EOModel model) {
		Properties properties = new Properties();
		if (model.getModelURL() == null) {
			return properties;
		}
		String spec = model.getModelURL().toExternalForm();
		while (spec.endsWith("/")) {
			spec = spec.substring(0, spec.length() - 1);
		}
		int slash = spec.lastIndexOf('/');
		if (slash < 0) {
			return properties;
		}
		try {
			URLConnection connection = new URL(spec.substring(0, slash + 1) + "Properties").openConnection();
			// So that a jar isn't left open.
			connection.setUseCaches(false);
			try (InputStream in = connection.getInputStream()) {
				properties.load(in);
			}
		} catch (IOException e) {
			// The model's folder has no Properties.
		}
		return properties;
	}

	/**
	 * Returns the models that Wonder's {@code er.migration.skipModelNames}
	 * names, which {@code ERXMigrator} doesn't migrate.
	 *
	 * @param properties the project's properties
	 * @return model names
	 */
	static List<String> skippedModels(Properties properties) {
		String names = properties.getProperty("er.migration.skipModelNames");
		if (names == null) {
			return Collections.emptyList();
		}
		return Arrays.stream(names.split(",")).map(String::trim).filter(name -> !name.isEmpty()).collect(Collectors.toList());
	}

	/**
	 * Returns the class name, without its package.
	 */
	String getClassName() {
		return prefix + number;
	}

	/**
	 * Returns the package, or null for none.
	 */
	String getPackageName() {
		return packageName;
	}

	int getNumber() {
		return number;
	}

	/**
	 * Returns whether it is the model's first migration.
	 */
	boolean isFirst() {
		return first;
	}

	/**
	 * Returns its file.
	 */
	File getFile() {
		File folder = packageName == null ? sourceFolder : new File(sourceFolder, packageName.replace('.', File.separatorChar));
		return new File(folder, getClassName() + ".java");
	}
}

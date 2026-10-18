package net.logicsquad.eogen.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A migration, as its template sees it, as {@code $migration}: its class's
 * name and package, the lines of its {@code upgrade} method, which use its
 * {@code database} argument, an {@code ERXMigrationDatabase}, and the other
 * models that it depends on, with the lines of its {@code modelDependencies}
 * method.
 */
public final class GeneratedMigration {
	/**
	 * Another model whose tables a migration needs, which it declares in
	 * {@code modelDependencies()}, so that {@code ERXMigrator} migrates that
	 * model first, up to a version.
	 */
	public static final class Dependency {
		private final String modelName;

		private final int version;

		private final List<String> tables;

		Dependency(String modelName, int version, Collection<String> tables) {
			this.modelName = modelName;
			this.version = version;
			this.tables = Collections.unmodifiableList(new ArrayList<>(tables));
		}

		/**
		 * Returns the model's name.
		 *
		 * @return model name
		 */
		public String getModelName() {
			return modelName;
		}

		/**
		 * Returns the version of the model that the migration needs, or -1 if
		 * eogen can't tell it.
		 *
		 * @return version
		 */
		public int getVersion() {
			return version;
		}

		/**
		 * Returns whether eogen can tell the version, or leaves a gap for it.
		 *
		 * @return {@code true} if known
		 */
		public boolean isKnown() {
			return version >= 0;
		}

		/**
		 * Returns the names of the model's tables that the migration needs.
		 *
		 * @return table names
		 */
		public List<String> getTables() {
			return tables;
		}
	}

	/**
	 * The classes that {@code upgrade}'s arguments need, whatever a migration
	 * extends.
	 */
	private static final List<String> BASE_IMPORTS = Collections.unmodifiableList(Arrays.asList("com.webobjects.eocontrol.EOEditingContext", "er.extensions.migration.ERXMigrationDatabase"));

	private static final String INDENT = "\t\t";

	private final String modelName;

	private final String className;

	private final String packageName;

	private final int number;

	private final boolean first;

	private final String since;

	private final List<String> changes;

	private final List<String> upgradeLines;

	private final Set<String> imports;

	private final List<Dependency> dependencies;

	private final List<String> dependencyLines = new ArrayList<>();

	private final List<MigrationWriter.Gap> dependencyGaps = new ArrayList<>();

	private final int gapCount;

	GeneratedMigration(String modelName, String className, String packageName, int number, boolean first, String since, MigrationWriter writer, List<Dependency> dependencies) {
		this.modelName = modelName;
		this.className = className;
		this.packageName = packageName;
		this.number = number;
		this.first = first;
		this.since = since;
		this.changes = Collections.unmodifiableList(new ArrayList<>(writer.getChanges()));
		this.upgradeLines = Collections.unmodifiableList(new ArrayList<>(writer.getLines()));
		Set<String> imports = new TreeSet<>(writer.getImports());
		if (!dependencies.isEmpty()) {
			imports.add("com.webobjects.foundation.NSArray");
			imports.add("er.extensions.migration.ERXModelVersion");
		}
		this.imports = Collections.unmodifiableSet(imports);
		this.dependencies = Collections.unmodifiableList(new ArrayList<>(dependencies));
		writeDependencies();
		this.gapCount = dependencyGaps.size() + writer.getGaps().size();
	}

	/**
	 * Writes the body of {@code modelDependencies()}, with a gap for each
	 * version that eogen can't tell, its comment before the {@code return}.
	 */
	private void writeDependencies() {
		if (dependencies.isEmpty()) {
			return;
		}
		List<String> versions = new ArrayList<>();
		for (Dependency dependency : dependencies) {
			String model = dependency.getModelName();
			if (!dependency.isKnown()) {
				boolean one = dependency.getTables().size() == 1;
				String message = "This migration needs " + model + "'s " + (one ? "table " : "tables ") + list(dependency.getTables()) + ", but eogen can't find a migration of " + model
						+ ", so it can't tell which version of " + model + " to depend on.";
				for (String line : MigrationWriter.commentLines(message + " Replace " + MigrationWriter.GAP + " with the version whose migration creates " + (one ? "it" : "them")
						+ ", or drop the dependency if " + (one ? "it comes" : "they come") + " from elsewhere.")) {
					dependencyLines.add(INDENT + line);
				}
				dependencyGaps.add(new MigrationWriter.Gap(dependency.getTables().get(0), null, "dependency on " + model, message));
			}
			versions.add("new ERXModelVersion(" + MigrationWriter.literal(model) + ", " + (dependency.isKnown() ? String.valueOf(dependency.getVersion()) : MigrationWriter.GAP) + ")");
		}
		if (versions.size() == 1) {
			dependencyLines.add(INDENT + "return new NSArray<ERXModelVersion>(" + versions.get(0) + ");");
			return;
		}
		dependencyLines.add(INDENT + "return new NSArray<ERXModelVersion>(new ERXModelVersion[] {");
		for (int i = 0; i < versions.size(); i++) {
			dependencyLines.add(INDENT + "\t\t" + versions.get(i) + (i < versions.size() - 1 ? "," : " });"));
		}
	}

	/**
	 * Returns names as a list in a sentence: {@code a}, {@code a and b}, or
	 * {@code a, b and c}.
	 */
	private static String list(List<String> names) {
		if (names.size() == 1) {
			return names.get(0);
		}
		return String.join(", ", names.subList(0, names.size() - 1)) + " and " + names.get(names.size() - 1);
	}

	/**
	 * Returns the model's name.
	 *
	 * @return model name
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * Returns the class's simple name, such as {@code Shop3}.
	 *
	 * @return class name
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Returns the class's simple name, for messages.
	 *
	 * @return class name
	 */
	public String getName() {
		return className;
	}

	/**
	 * Returns the class's package, or null for none.
	 *
	 * @return package name, or null
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Returns the migration's number, which {@code ERXMigrator} runs it in the
	 * order of.
	 *
	 * @return number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Returns whether it is the model's first migration, which creates its
	 * tables.
	 *
	 * @return {@code true} if first
	 */
	public boolean isFirst() {
		return first;
	}

	/**
	 * Returns the earlier version of the models that it changes the tables
	 * from, such as {@code commit 1a2b3c4}.
	 *
	 * @return description
	 */
	public String getSince() {
		return since;
	}

	/**
	 * Returns a sentence for each change it makes, such as
	 * {@code Adds isbn to book.}
	 *
	 * @return changes
	 */
	public List<String> getChanges() {
		return changes;
	}

	/**
	 * Returns the lines of the {@code upgrade} method's body, each indented
	 * with two tabs, or empty.
	 *
	 * @return lines
	 */
	public List<String> getUpgradeLines() {
		return upgradeLines;
	}

	/**
	 * Returns the other models whose tables it needs, by name, each with the
	 * version that it depends on, or empty.
	 *
	 * @return dependencies
	 */
	public List<Dependency> getDependencies() {
		return dependencies;
	}

	/**
	 * Returns the lines of the {@code modelDependencies} method's body, each
	 * indented with two tabs, which return an {@code NSArray} of
	 * {@code ERXModelVersion}, or empty if it has no dependencies.
	 *
	 * @return lines
	 */
	public List<String> getDependencyLines() {
		return Collections.unmodifiableList(dependencyLines);
	}

	/**
	 * Returns the gaps in the dependencies' lines, in their order.
	 */
	List<MigrationWriter.Gap> getDependencyGaps() {
		return Collections.unmodifiableList(dependencyGaps);
	}

	/**
	 * Returns how many gaps it leaves to fill in.
	 *
	 * @return number of gaps
	 */
	public int getGapCount() {
		return gapCount;
	}

	/**
	 * Returns the import statements that the {@code upgrade} method needs,
	 * sorted, with an empty line between top-level packages.
	 *
	 * @return lines
	 */
	public List<String> getImportLines() {
		return importLines(Collections.<String>emptyList());
	}

	/**
	 * Returns the import statements that the {@code upgrade} method needs, and
	 * those of other classes that a template uses, sorted, with an empty line
	 * between top-level packages.
	 *
	 * @param more the other classes' names
	 * @return lines
	 */
	public List<String> importLines(List<?> more) {
		Set<String> names = new TreeSet<>(BASE_IMPORTS);
		names.addAll(imports);
		for (Object name : more) {
			if (name != null) {
				names.add(name.toString());
			}
		}
		List<String> lines = new ArrayList<>();
		String group = null;
		for (String name : names) {
			String top = name.indexOf('.') < 0 ? name : name.substring(0, name.indexOf('.'));
			if (group != null && !group.equals(top)) {
				lines.add("");
			}
			group = top;
			lines.add("import " + name + ";");
		}
		return lines;
	}

	@Override
	public String toString() {
		return className;
	}
}

package net.logicsquad.eogen.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A migration, as its template sees it, as {@code $migration}: its class's
 * name and package, and the lines of its {@code upgrade} method, which use
 * its {@code database} argument, an {@code ERXMigrationDatabase}.
 */
public final class GeneratedMigration {
	/**
	 * The classes that {@code upgrade}'s arguments need, whatever a migration
	 * extends.
	 */
	private static final List<String> BASE_IMPORTS = Collections.unmodifiableList(Arrays.asList("com.webobjects.eocontrol.EOEditingContext", "er.extensions.migration.ERXMigrationDatabase"));

	private final String modelName;

	private final String className;

	private final String packageName;

	private final int number;

	private final boolean first;

	private final String since;

	private final List<String> changes;

	private final List<String> upgradeLines;

	private final Set<String> imports;

	private final int gapCount;

	GeneratedMigration(String modelName, String className, String packageName, int number, boolean first, String since, MigrationWriter writer) {
		this.modelName = modelName;
		this.className = className;
		this.packageName = packageName;
		this.number = number;
		this.first = first;
		this.since = since;
		this.changes = Collections.unmodifiableList(new ArrayList<>(writer.getChanges()));
		this.upgradeLines = Collections.unmodifiableList(new ArrayList<>(writer.getLines()));
		this.imports = Collections.unmodifiableSet(new TreeSet<>(writer.getImports()));
		this.gapCount = writer.getGaps().size();
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

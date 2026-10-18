package net.logicsquad.eogen.migration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.logicsquad.eogen.migration.Schema.Column;
import net.logicsquad.eogen.migration.Schema.ForeignKey;
import net.logicsquad.eogen.migration.Schema.Index;
import net.logicsquad.eogen.migration.Schema.Table;

/**
 * Writes the body of a migration's {@code upgrade} method: the calls to
 * Project Wonder's migration API that change a model's tables from one
 * version of the model to another.
 * <p>
 * Where it can't decide, it writes a gap: a {@code // TODO eogen:} comment
 * that says what to decide, and the identifier {@link #GAP}, which doesn't
 * compile, so that the migration can't run until someone fills it in. A gap
 * is left for the value that a new column that doesn't allow null needs in
 * the rows a table already has, unless its attribute has a {@code default};
 * for the rows where a column is null when it no longer allows null; for a
 * column, or a table, that is dropped while another is added, which may be a
 * rename; for a column whose type changes; for a primary key that changes;
 * and for a foreign key or an index that is dropped, for which Wonder has no
 * call. An attribute that keeps its name while its column's changes renames
 * the column, and an entity that keeps its name while its table's changes
 * renames the table.
 * </p>
 * <p>
 * The calls come in this order: for each table that both versions have, in
 * name order, the gaps for its dropped foreign keys and indexes, its renamed,
 * added, changed and dropped columns, and its new indexes; then the new
 * tables, with their columns, primary keys and indexes; then the new foreign
 * keys, once every table they need exists; and last the dropped tables.
 * </p>
 * <p>
 * A table or a column that the model keeps, though it no longer maps it, is
 * neither dropped nor changed, and nor are its foreign keys and indexes, or
 * the foreign keys into a kept table. One that the earlier version kept, and
 * that the model now neither maps nor keeps, is dropped. A kept column that
 * doesn't allow null leaves a gap, since a row can't be inserted without it.
 * </p>
 * <p>
 * It also notes the other models whose tables the calls need, for the
 * migration to depend on: the models that create the destinations of the
 * foreign keys it adds, and the model of a table that it adds a partial
 * entity's columns to.
 * </p>
 */
final class MigrationWriter {
	/**
	 * The identifier that marks a gap, which doesn't compile.
	 */
	static final String GAP = "EOGEN_GAP";

	/**
	 * How wide a gap's comment is, counting a tab as four columns.
	 */
	private static final int COMMENT_WIDTH = 110;

	private static final String INDENT = "\t\t";

	/**
	 * What a migration leaves to decide.
	 */
	static final class Gap {
		private final String table;

		private final String entity;

		private final String object;

		private final String message;

		Gap(String table, String entity, String object, String message) {
			this.table = table;
			this.entity = entity;
			this.object = object;
			this.message = message;
		}

		/**
		 * Returns the table it is in.
		 */
		String getTable() {
			return table;
		}

		/**
		 * Returns the first entity that maps the table, or null.
		 */
		String getEntity() {
			return entity;
		}

		/**
		 * Returns what it is about, such as {@code column isbn}.
		 */
		String getObject() {
			return object;
		}

		/**
		 * Returns what is left to decide.
		 */
		String getMessage() {
			return message;
		}
	}

	private final Schema before;

	private final Schema after;

	private final List<String> lines = new ArrayList<>();

	private final List<Gap> gaps = new ArrayList<>();

	private final Set<String> imports = new TreeSet<>();

	private final List<String> changes = new ArrayList<>();

	/**
	 * The tables and columns that the calls drop, as {@code table} or
	 * {@code table.column}.
	 */
	private final List<String> droppedNames = new ArrayList<>();

	/**
	 * Why each name in the model's keep list that does nothing does nothing.
	 */
	private final List<String> keepWarnings = new ArrayList<>();

	/**
	 * The other models whose tables the calls need, with those tables' names.
	 */
	private final Map<String, Set<String>> needed = new TreeMap<>();

	/**
	 * Each table's variable, by the table's name without case, once declared.
	 */
	private final Map<String, String> declared = new HashMap<>();

	private final Set<String> variables = new HashSet<>();

	/**
	 * The earlier version's tables that an entity renamed, and the names they
	 * now have, by the old name without case.
	 */
	private final Map<String, String> renamedTables = new TreeMap<>();

	/**
	 * The renamed columns of each table, by the table's name now and the
	 * column's old name, without case.
	 */
	private final Map<String, Map<String, String>> renamedColumns = new HashMap<>();

	/**
	 * The entity of the table whose calls are being written, for gaps.
	 */
	private String entity;

	/**
	 * Constructor, which writes the calls.
	 *
	 * @param before the earlier version's tables
	 * @param after the tables now
	 */
	MigrationWriter(Schema before, Schema after) {
		this.before = before;
		this.after = after;
		write();
	}

	/**
	 * Returns the lines of the method's body, each indented for a method in a
	 * class, or empty.
	 */
	List<String> getLines() {
		return lines;
	}

	/**
	 * Returns the gaps, in the order the lines have them.
	 */
	List<Gap> getGaps() {
		return gaps;
	}

	/**
	 * Returns the classes the lines use, which a migration imports.
	 */
	Set<String> getImports() {
		return imports;
	}

	/**
	 * Returns a sentence for each change, in the order of the lines.
	 */
	List<String> getChanges() {
		return changes;
	}

	/**
	 * Returns whether the tables changed.
	 */
	boolean hasChanges() {
		return !changes.isEmpty();
	}

	/**
	 * Returns the tables and columns that the calls drop, as {@code table} or
	 * {@code table.column}, which the model could keep instead.
	 */
	List<String> getDropped() {
		return droppedNames;
	}

	/**
	 * Returns why each name in the model's {@code eogen.keep} list that does
	 * nothing does nothing: the model still maps it, or the earlier version
	 * neither maps nor keeps it.
	 */
	List<String> getKeepWarnings() {
		return keepWarnings;
	}

	/**
	 * Returns the other models whose tables the calls need, by name, with the
	 * names of those tables.
	 */
	Map<String, Set<String>> getNeededModels() {
		return needed;
	}

	private void write() {
		Map<String, Table> beforeTables = before.getTables();
		Map<String, Table> afterTables = after.getTables();
		Set<String> created = new TreeSet<>(afterTables.keySet());
		created.removeAll(beforeTables.keySet());
		Set<String> dropped = new TreeSet<>(beforeTables.keySet());
		dropped.removeAll(afterTables.keySet());
		// A table that the earlier version kept, and that an entity maps again,
		// is there already, and one that the model keeps stays as it is.
		created.removeIf(before::keepsTable);
		dropped.removeIf(after::keepsTable);
		warnOfKeeps();
		for (Map.Entry<String, String> entity : new TreeMap<>(after.getEntityTables()).entrySet()) {
			String oldKey = before.getEntityTables().get(entity.getKey());
			String newKey = entity.getValue();
			if (oldKey != null && !oldKey.equals(newKey) && dropped.contains(oldKey) && created.contains(newKey) && beforeTables.get(oldKey).isOwned() && afterTables.get(newKey).isOwned()) {
				renamedTables.put(oldKey, afterTables.get(newKey).getName());
				dropped.remove(oldKey);
				created.remove(newKey);
			}
		}
		// Each table that changes, with its earlier version: an empty one for a
		// partial entity's columns that are new, and the other way round.
		Map<String, Table[]> pairs = new TreeMap<>();
		for (Map.Entry<String, Table> table : afterTables.entrySet()) {
			String key = table.getKey();
			if (beforeTables.containsKey(key)) {
				pairs.put(key, new Table[] { beforeTables.get(key), table.getValue() });
			} else if (renamedTables.containsValue(table.getValue().getName())) {
				for (Map.Entry<String, String> rename : renamedTables.entrySet()) {
					if (rename.getValue().equals(table.getValue().getName())) {
						pairs.put(key, new Table[] { beforeTables.get(rename.getKey()), table.getValue() });
					}
				}
			} else if (!table.getValue().isOwned()) {
				pairs.put(key, new Table[] { new Table(table.getValue().getName(), table.getValue().getVariable()), table.getValue() });
				created.remove(key);
			}
		}
		for (String key : new ArrayList<>(dropped)) {
			Table table = beforeTables.get(key);
			if (!table.isOwned()) {
				pairs.put(key, new Table[] { table, new Table(table.getName(), table.getVariable()) });
				dropped.remove(key);
			}
		}
		for (Map.Entry<String, Table[]> pair : pairs.entrySet()) {
			renamedColumns.put(pair.getKey(), renameColumns(pair.getValue()[0], pair.getValue()[1]));
		}
		Map<String, List<ForeignKey>> foreignKeys = new TreeMap<>();
		for (Map.Entry<String, Table[]> pair : pairs.entrySet()) {
			List<ForeignKey> added = alter(pair.getKey(), pair.getValue()[0], pair.getValue()[1]);
			if (!added.isEmpty()) {
				foreignKeys.put(pair.getKey(), added);
			}
		}
		for (String key : created) {
			Table table = afterTables.get(key);
			create(key, table);
			if (!table.getForeignKeys().isEmpty()) {
				foreignKeys.put(key, new ArrayList<>(table.getForeignKeys()));
			}
		}
		for (Map.Entry<String, List<ForeignKey>> table : foreignKeys.entrySet()) {
			addForeignKeys(table.getKey(), afterTables.get(table.getKey()), table.getValue());
		}
		// The tables that go: the earlier version's, and those that it kept and
		// the model now neither maps nor keeps, which go by name.
		Map<String, Table> drops = new TreeMap<>();
		for (String key : dropped) {
			drops.put(key, beforeTables.get(key));
		}
		for (Schema.Kept kept : before.getKept().values()) {
			String key = Schema.key(kept.getTable());
			if (kept.getColumn() == null && !afterTables.containsKey(key) && !after.keepsTable(key)) {
				drops.put(key, new Table(kept.getTable(), null));
			}
		}
		for (Table table : drops.values()) {
			drop(table, created);
		}
		if (!lines.isEmpty() && lines.get(lines.size() - 1).isEmpty()) {
			lines.remove(lines.size() - 1);
		}
		// A partial entity's columns change another model's table.
		for (String key : declared.keySet()) {
			Table table = afterTables.containsKey(key) ? afterTables.get(key) : beforeTables.get(key);
			if (table != null && !table.isOwned()) {
				need(table.getCreator(), table.getName());
			}
		}
	}

	/**
	 * Notes that the calls need a table of a model, unless it is this one.
	 */
	private void need(String model, String table) {
		if (model != null && !model.equals(after.getModel())) {
			needed.computeIfAbsent(model, name -> new TreeSet<>()).add(table);
		}
	}

	/**
	 * Returns the columns of a table that an attribute keeps while its column's
	 * name changes, unless the model keeps the old column: by the old name
	 * without case, the new name.
	 */
	private Map<String, String> renameColumns(Table before, Table after) {
		Map<String, String> renames = new LinkedHashMap<>();
		for (Map.Entry<String, String> attribute : new TreeMap<>(after.getAttributeColumns()).entrySet()) {
			String oldKey = before.getAttributeColumns().get(attribute.getKey());
			String newKey = attribute.getValue();
			if (oldKey != null && !oldKey.equals(newKey) && !after.getColumns().containsKey(oldKey) && !before.getColumns().containsKey(newKey) && !renames.containsKey(oldKey)
					&& !renames.containsValue(after.getColumns().get(newKey).getName()) && !this.after.keepsColumn(after.getName(), oldKey)) {
				renames.put(oldKey, after.getColumns().get(newKey).getName());
			}
		}
		return renames;
	}

	/**
	 * Notes each name in the model's keep list that does nothing: one that the
	 * model still maps, or that the earlier version neither maps nor keeps, so
	 * that eogen can't tell what it is.
	 */
	private void warnOfKeeps() {
		String names = after.getModel() + "'s " + Schema.KEEP + " names ";
		for (Map.Entry<String, String> mapped : after.getMappedKeeps().entrySet()) {
			keepWarnings.add(names + mapped.getKey() + ", but " + mapped.getValue() + " still maps it, so it does nothing.");
		}
		if (before.getTables().isEmpty() && before.getKept().isEmpty()) {
			// There is no earlier version to know them by, as for a model's
			// first migration.
			return;
		}
		for (Schema.Kept kept : after.getKept().values()) {
			Table table = before.getTables().get(Schema.key(kept.getTable()));
			boolean known = kept.getColumn() == null ? table != null || before.keepsTable(kept.getTable())
					: table != null && table.getColumns().containsKey(Schema.key(kept.getColumn())) || before.keepsColumn(kept.getTable(), kept.getColumn());
			if (!known) {
				keepWarnings.add(names + kept.getName() + ", but the earlier version of " + after.getModel() + " neither maps nor keeps it, so it does nothing.");
			}
		}
	}

	/**
	 * Returns whether a foreign key from a table stays because the model keeps
	 * its destination, or one of its columns.
	 */
	private boolean kept(String table, ForeignKey foreignKey) {
		return after.keepsTable(foreignKey.getDestinationTable()) || keepsAny(table, foreignKey.getSourceColumns());
	}

	/**
	 * Returns whether the model keeps any of a table's columns.
	 */
	private boolean keepsAny(String table, List<String> columns) {
		for (String column : columns) {
			if (after.keepsColumn(table, column)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Writes the changes to a table that both versions have.
	 *
	 * @return the foreign keys it gains, which come once every table exists
	 */
	private List<ForeignKey> alter(String key, Table before, Table after) {
		List<Runnable> statements = new ArrayList<>();
		String oldName = before.getName();
		boolean renamed = !Schema.key(oldName).equals(key);
		Map<String, String> columnRenames = renamedColumns.get(key);
		String variable = variable(key, after.getVariable());
		entity = firstEntity(after, before);

		Set<ForeignKey> oldForeignKeys = new LinkedHashSet<>();
		for (ForeignKey foreignKey : before.getForeignKeys()) {
			String destination = renamedTables.getOrDefault(Schema.key(foreignKey.getDestinationTable()), foreignKey.getDestinationTable());
			Map<String, String> destinationRenames = renamedColumns.getOrDefault(Schema.key(destination), Collections.<String, String>emptyMap());
			oldForeignKeys.add(foreignKey.renamed(renamedTables, columnRenames, destinationRenames));
		}
		for (ForeignKey foreignKey : oldForeignKeys) {
			if (!after.getForeignKeys().contains(foreignKey) && !kept(key, foreignKey)) {
				statements.add(() -> {
					change("Drops the foreign key from " + describe(after.getName(), foreignKey.getSourceColumns()) + " to " + describe(foreignKey.getDestinationTable(), foreignKey.getDestinationColumns()) + ".");
					gap(after.getName(), "foreign key " + foreignKey.getRelationship(), "Wonder has no call to drop the foreign key from " + describe(after.getName(), foreignKey.getSourceColumns()) + " to "
							+ describe(foreignKey.getDestinationTable(), foreignKey.getDestinationColumns()) + ".", "Drop it with SQL, or delete this line if the database has none, or drops it with its column.");
				});
			}
		}
		List<Index> newIndexes = new ArrayList<>();
		for (Index index : after.getIndexes().values()) {
			Index old = before.getIndexes().get(Schema.key(index.getName()));
			if (old == null || !renamedIndex(old, columnRenames).same(index)) {
				newIndexes.add(index);
			}
		}
		for (Index index : before.getIndexes().values()) {
			Index current = after.getIndexes().get(Schema.key(index.getName()));
			// An index of a kept column stays with it.
			if (current == null ? !keepsAny(key, index.getColumns()) : !renamedIndex(index, columnRenames).same(current)) {
				statements.add(() -> {
					change("Drops the index " + index.getName() + " from " + after.getName() + ".");
					gap(after.getName(), "index " + index.getName(), "Wonder has no call to drop the index " + index.getName() + " from " + after.getName() + ".",
							"Drop it with SQL, or delete this line if the database has none.");
				});
			}
		}
		for (Map.Entry<String, String> rename : columnRenames.entrySet()) {
			String old = before.getColumns().get(rename.getKey()).getName();
			statements.add(() -> {
				change("Renames " + old + " in " + after.getName() + " to " + rename.getValue() + ".");
				statement(variable + ".existingColumnNamed(" + literal(old) + ").renameTo(" + literal(rename.getValue()) + ");");
			});
		}
		// A column that the earlier version kept, and that an attribute maps
		// again, is there already.
		List<Column> added = new ArrayList<>();
		for (Map.Entry<String, Column> column : after.getColumns().entrySet()) {
			if (!before.getColumns().containsKey(column.getKey()) && !columnRenames.containsValue(column.getValue().getName()) && !this.before.keepsColumn(oldName, column.getKey())) {
				added.add(column.getValue());
			}
		}
		// A column that the model keeps stays, but one that doesn't allow null
		// needs a value in every row inserted, which the application no longer
		// gives it.
		List<Column> removed = new ArrayList<>();
		List<Column> keptNotNull = new ArrayList<>();
		for (Map.Entry<String, Column> column : before.getColumns().entrySet()) {
			if (!after.getColumns().containsKey(column.getKey()) && !columnRenames.containsKey(column.getKey())) {
				if (!this.after.keepsColumn(key, column.getKey())) {
					removed.add(column.getValue());
				} else if (!column.getValue().isAllowsNull() && column.getValue().getDefaultValue() == null) {
					keptNotNull.add(column.getValue());
				}
			}
		}
		// One that the earlier version kept, and that the model now neither
		// maps nor keeps, goes by name.
		for (Schema.Kept kept : this.before.getKept().values()) {
			if (kept.getColumn() != null && Schema.key(kept.getTable()).equals(Schema.key(oldName)) && !after.getColumns().containsKey(Schema.key(kept.getColumn()))
					&& !this.after.keepsColumn(key, kept.getColumn())) {
				removed.add(new Column(kept.getColumn(), null, null, null, null, null, null, null, true, null));
			}
		}
		for (Column column : added) {
			statements.add(() -> {
				change("Adds " + column.getName() + " to " + after.getName() + ".");
				addColumn(variable, after.getName(), column);
			});
		}
		for (Map.Entry<String, Column> column : after.getColumns().entrySet()) {
			Column old = before.getColumns().get(column.getKey());
			if (old == null) {
				for (Map.Entry<String, String> rename : columnRenames.entrySet()) {
					if (rename.getValue().equals(column.getValue().getName())) {
						old = before.getColumns().get(rename.getKey());
					}
				}
			}
			if (old != null && (old.getType() != column.getValue().getType() || !old.sameType(column.getValue()) || old.isAllowsNull() != column.getValue().isAllowsNull())) {
				Column previous = old;
				statements.add(() -> changeColumn(variable, after.getName(), previous, column.getValue()));
			}
		}
		for (Column column : removed) {
			statements.add(() -> {
				change("Removes " + column.getName() + " from " + after.getName() + ".");
				droppedNames.add(after.getName() + "." + column.getName());
				if (!added.isEmpty()) {
					List<String> names = new ArrayList<>();
					for (Column candidate : added) {
						names.add(candidate.getName());
					}
					String renameTo = names.size() == 1 ? names.get(0) : "<new>";
					gap(after.getName(), "column " + column.getName(), (names.size() == 1 ? "Is " + names.get(0) + ", a new column in " + after.getName() + ", "
							: "Is one of " + String.join(", ", names) + ", new columns in " + after.getName() + ", ") + column.getName() + " renamed?",
							"Then rename it, with " + variable + ".existingColumnNamed(" + literal(column.getName()) + ").renameTo(" + literal(renameTo) + "), rather than delete it and add "
									+ (names.size() == 1 ? names.get(0) : "the other") + ". Either way, delete this line.");
				}
				statement(variable + ".existingColumnNamed(" + literal(column.getName()) + ").delete();");
			});
		}
		String model = this.after.getModel();
		for (Column column : keptNotNull) {
			statements.add(() -> {
				change("Lets " + column.getName() + " in " + after.getName() + " be null, as " + model + " keeps it.");
				gap(after.getName(), "column " + column.getName(), model + " keeps " + column.getName() + " in " + after.getName() + ", which doesn't allow null, so the application can't insert a row into "
						+ after.getName() + " unless the column has a default.", "Let it allow null, as the next line does, or give it a default instead. Either way, delete this line.");
				statement(variable + ".existingColumnNamed(" + literal(column.getName()) + ").setAllowsNull(ALLOWS_NULL);");
			});
		}
		if (before.isOwned() && after.isOwned() && !before.getPrimaryKey().isEmpty() && !after.getPrimaryKey().isEmpty()) {
			List<String> oldKey = new ArrayList<>();
			for (String column : before.getPrimaryKey()) {
				oldKey.add(columnRenames.getOrDefault(Schema.key(column), column));
			}
			if (!Schema.keys(oldKey).equals(Schema.keys(after.getPrimaryKey()))) {
				statements.add(() -> {
					change("Changes the primary key of " + after.getName() + ".");
					gap(after.getName(), "primary key", "The primary key of " + after.getName() + " changes from (" + String.join(", ", before.getPrimaryKey()) + ") to ("
							+ String.join(", ", after.getPrimaryKey()) + ").", "Change it, then delete this line.");
				});
			}
		}
		for (Index index : newIndexes) {
			statements.add(() -> addIndex(variable, after.getName(), index));
		}
		if (!statements.isEmpty() || renamed) {
			declare(key, variable, "database.existingTableNamed(" + literal(oldName) + ")");
			if (renamed) {
				change("Renames " + oldName + " to " + after.getName() + ".");
				statement(variable + ".renameTo(" + literal(after.getName()) + ");");
			}
			for (Runnable statement : statements) {
				statement.run();
			}
			lines.add("");
		}
		List<ForeignKey> newForeignKeys = new ArrayList<>();
		for (ForeignKey foreignKey : after.getForeignKeys()) {
			if (!oldForeignKeys.contains(foreignKey)) {
				newForeignKeys.add(foreignKey);
			}
		}
		return newForeignKeys;
	}

	private static Index renamedIndex(Index index, Map<String, String> columnRenames) {
		List<String> columns = new ArrayList<>();
		for (String column : index.getColumns()) {
			columns.add(columnRenames.getOrDefault(Schema.key(column), column));
		}
		return new Index(index.getName(), index.getConstraint(), columns);
	}

	/**
	 * Writes a new table, with its columns, primary key and indexes.
	 */
	private void create(String key, Table table) {
		change("Creates " + table.getName() + ".");
		entity = firstEntity(table, table);
		String variable = variable(key, table.getVariable());
		declare(key, variable, "database.newTableNamed(" + literal(table.getName()) + ")");
		for (Column column : table.getColumns().values()) {
			if (column.getType() == null) {
				gap(table.getName(), "column " + column.getName(), "eogen can't tell which column to make for " + describeAttribute(column) + ".",
						"Make " + column.getName() + ", then delete this line.");
			} else {
				statement(variable + "." + call(column, column.isAllowsNull(), column.getType().defaultValue(column.getDefaultValue(), imports)) + ";");
			}
		}
		statement(variable + ".create();");
		if (!table.getPrimaryKey().isEmpty()) {
			List<String> columns = new ArrayList<>();
			for (String column : table.getPrimaryKey()) {
				columns.add(literal(column));
			}
			statement(variable + ".setPrimaryKey(" + String.join(", ", columns) + ");");
		}
		for (Index index : table.getIndexes().values()) {
			addIndex(variable, table.getName(), index);
		}
		lines.add("");
	}

	/**
	 * Writes a column for a table that may have rows.
	 */
	private void addColumn(String variable, String table, Column column) {
		ColumnType type = column.getType();
		if (type == null) {
			gap(table, "column " + column.getName(), "eogen can't tell which column to make for " + describeAttribute(column) + ".",
					"Add " + column.getName() + ", then delete this line.");
			return;
		}
		String defaultValue = type.defaultValue(column.getDefaultValue(), imports);
		if (column.isAllowsNull() || defaultValue != null) {
			statement(variable + "." + call(column, column.isAllowsNull(), defaultValue) + ";");
		} else if (type.takesDefault()) {
			comment(table + " may have rows, which need a value for " + column.getName() + ", a new column that doesn't allow null. Replace " + GAP + " with it.");
			gaps.add(new Gap(table, entity, "column " + column.getName(), table + " may have rows, which need a value for " + column.getName() + ", a new column that doesn't allow null."));
			statement(variable + "." + call(column, false, GAP) + ";");
		} else {
			statement(variable + "." + call(column, true, null) + ";");
			gap(table, "column " + column.getName(), table + " may have rows, which need a value for " + column.getName() + ", a new column that doesn't allow null.",
					"Set it, then delete this line.");
			statement(variable + ".existingColumnNamed(" + literal(column.getName()) + ").setAllowsNull(NOT_NULL);");
		}
	}

	/**
	 * Writes the changes to a column: its type, its width, precision and scale,
	 * and whether it allows null.
	 */
	private void changeColumn(String variable, String table, Column before, Column after) {
		String existing = variable + ".existingColumnNamed(" + literal(after.getName()) + ")";
		if (before.getType() != after.getType() || (after.getType() == null && !before.sameType(after))) {
			change("Changes the type of " + after.getName() + " in " + table + ".");
			String suggestion = "";
			if (after.getType() != null && after.getType().getJdbcType() != null) {
				imports.add("java.sql.Types");
				suggestion = ", such as with " + existing + ".setDataType(Types." + after.getType().getJdbcType() + ", " + after.getScale() + ", " + after.getPrecision() + ", "
						+ after.getWidth() + ", null)";
			}
			gap(table, "column " + after.getName(), after.getName() + " in " + table + " changes from " + describe(before) + " to " + describe(after) + ".",
					"Change it" + suggestion + ", converting its values if need be, then delete this line.");
		} else if (!before.sameType(after)) {
			ColumnType type = after.getType();
			imports.add("java.sql.Types");
			if (type.getArguments() == ColumnType.Arguments.WIDTH) {
				change("Changes the width of " + after.getName() + " in " + table + " to " + after.getWidth() + ".");
				statement(existing + ".setWidthType(Types." + type.getJdbcType() + ", " + after.getWidth() + ", null);");
			} else {
				change("Changes the precision and scale of " + after.getName() + " in " + table + " to " + after.getPrecision() + ", " + after.getScale() + ".");
				statement(existing + ".setNumericType(Types." + type.getJdbcType() + ", " + after.getScale() + ", " + after.getPrecision() + ", null);");
			}
		}
		if (before.isAllowsNull() && !after.isAllowsNull()) {
			change("Makes " + after.getName() + " in " + table + " not allow null.");
			gap(table, "column " + after.getName(), "The rows of " + table + " where " + after.getName() + " is null need a value before it can be made not to allow null.",
					"Set it, or delete this line if there are none.");
			statement(existing + ".setAllowsNull(NOT_NULL);");
		} else if (!before.isAllowsNull() && after.isAllowsNull()) {
			change("Lets " + after.getName() + " in " + table + " be null.");
			statement(existing + ".setAllowsNull(ALLOWS_NULL);");
		}
	}

	/**
	 * Writes an index, or a gap for one that Wonder can't make.
	 */
	private void addIndex(String variable, String table, Index index) {
		change("Adds the index " + index.getName() + " to " + table + ".");
		if (!index.isPlain()) {
			gap(table, "index " + index.getName(), "Wonder can't make the " + index.getConstraint().getExternalName() + " index " + index.getName() + " on " + describe(table, index.getColumns()) + ".",
					"Make it with SQL, then delete this line.");
			return;
		}
		String method = index.isUnique() ? "addUniqueIndex" : "addIndex";
		if (index.getColumns().size() == 1) {
			statement(variable + "." + method + "(" + literal(index.getName()) + ", " + literal(index.getColumns().get(0)) + ");");
		} else {
			imports.add("er.extensions.jdbc.ERXSQLHelper.ColumnIndex");
			List<String> columns = new ArrayList<>();
			for (String column : index.getColumns()) {
				columns.add("new ColumnIndex(" + literal(column) + ")");
			}
			statement(variable + "." + method + "(" + literal(index.getName()) + ", " + String.join(", ", columns) + ");");
		}
	}

	/**
	 * Writes the foreign keys that a table gains.
	 */
	private void addForeignKeys(String key, Table table, List<ForeignKey> foreignKeys) {
		entity = firstEntity(table, table);
		String variable = declared.get(key);
		if (variable == null) {
			variable = variable(key, table.getVariable());
			declare(key, variable, "database.existingTableNamed(" + literal(table.getName()) + ")");
		}
		for (ForeignKey foreignKey : foreignKeys) {
			need(foreignKey.getDestinationModel(), foreignKey.getDestinationTable());
			change("Adds a foreign key from " + describe(table.getName(), foreignKey.getSourceColumns()) + " to " + describe(foreignKey.getDestinationTable(), foreignKey.getDestinationColumns()) + ".");
			if (foreignKey.getSourceColumns().size() == 1) {
				statement(variable + ".addForeignKey(" + literal(foreignKey.getSourceColumns().get(0)) + ", " + literal(foreignKey.getDestinationTable()) + ", "
						+ literal(foreignKey.getDestinationColumns().get(0)) + ");");
			} else {
				imports.add("er.extensions.migration.ERXMigrationColumn");
				List<String> sources = new ArrayList<>();
				for (String column : foreignKey.getSourceColumns()) {
					sources.add(variable + ".existingColumnNamed(" + literal(column) + ")");
				}
				List<String> destinations = new ArrayList<>();
				for (String column : foreignKey.getDestinationColumns()) {
					destinations.add("database.existingColumnNamed(" + literal(foreignKey.getDestinationTable()) + ", " + literal(column) + ")");
				}
				statement(variable + ".addForeignKey(new ERXMigrationColumn[] { " + String.join(", ", sources) + " }, new ERXMigrationColumn[] { " + String.join(", ", destinations) + " });");
			}
		}
		lines.add("");
	}

	/**
	 * Writes a dropped table, after a gap if a new table may be it renamed.
	 */
	private void drop(Table table, Set<String> created) {
		change("Drops " + table.getName() + ".");
		droppedNames.add(table.getName());
		entity = firstEntity(table, table);
		List<String> owned = new ArrayList<>();
		for (String key : created) {
			owned.add(after.getTables().get(key).getName());
		}
		if (!owned.isEmpty()) {
			String renameTo = owned.size() == 1 ? owned.get(0) : "<new>";
			gap(table.getName(), "table " + table.getName(), (owned.size() == 1 ? "Is " + owned.get(0) + ", a new table, " : "Is one of " + String.join(", ", owned) + ", new tables, ") + table.getName() + " renamed?",
					"Then rename it, with database.existingTableNamed(" + literal(table.getName()) + ").renameTo(" + literal(renameTo) + "), and change its columns, rather than drop it and create "
							+ (owned.size() == 1 ? owned.get(0) : "the other") + ". Either way, delete this line.");
		}
		statement("database.existingTableNamed(" + literal(table.getName()) + ").drop();");
	}

	/**
	 * Returns the call that makes a column, without the table.
	 */
	private String call(Column column, boolean allowsNull, String defaultValue) {
		ColumnType type = column.getType();
		String nullRule = allowsNull ? "ALLOWS_NULL" : "NOT_NULL";
		String name = literal(column.getName());
		if ((type == ColumnType.INTEGER || type == ColumnType.BIG_INTEGER) && column.getPrecision() > 0) {
			imports.add("java.sql.Types");
			return "newColumn(" + name + ", Types." + type.getJdbcType() + ", 0, " + column.getPrecision() + ", 0, " + nullRule + ", null" + (defaultValue == null ? "" : ", " + defaultValue) + ")";
		}
		String method = type == ColumnType.LARGE_STRING && defaultValue != null ? "newStringColumn" : type.getMethod();
		StringBuilder call = new StringBuilder(method).append('(').append(name);
		switch (type.getArguments()) {
		case WIDTH:
			call.append(", ").append(column.getWidth());
			break;
		case PRECISION_SCALE:
			call.append(", ").append(column.getPrecision()).append(", ").append(column.getScale());
			break;
		default:
			break;
		}
		call.append(", ").append(nullRule);
		if (defaultValue != null) {
			call.append(", ").append(defaultValue);
		}
		return call.append(')').toString();
	}

	/**
	 * Returns a column's call, with its arguments, for a gap.
	 */
	private static String describe(Column column) {
		ColumnType type = column.getType();
		if (type == null) {
			return "a column for " + column.getClassName();
		}
		switch (type.getArguments()) {
		case WIDTH:
			return type.getMethod() + "(" + column.getWidth() + ")";
		case PRECISION_SCALE:
			return type.getMethod() + "(" + column.getPrecision() + ", " + column.getScale() + ")";
		default:
			return type.getMethod() + (column.getPrecision() > 0 ? "(" + column.getPrecision() + ")" : "");
		}
	}

	private static String firstEntity(Table table, Table otherwise) {
		if (!table.getEntities().isEmpty()) {
			return table.getEntities().get(0);
		}
		return otherwise.getEntities().isEmpty() ? null : otherwise.getEntities().get(0);
	}

	/**
	 * Returns a column's attribute, with its class and external type, for a
	 * gap.
	 */
	private static String describeAttribute(Column column) {
		String externalType = column.getExternalType() == null || column.getExternalType().trim().isEmpty() ? "" : " and external type " + column.getExternalType();
		return column.getAttribute() + ", whose class is " + column.getClassName() + externalType;
	}

	private static String describe(String table, List<String> columns) {
		return table + "(" + String.join(", ", columns) + ")";
	}

	/**
	 * Returns a unique variable for a table.
	 */
	private String variable(String key, String name) {
		String variable = declared.get(key);
		if (variable != null) {
			return variable;
		}
		variable = name;
		for (int i = 2; variables.contains(variable); i++) {
			variable = name + i;
		}
		return variable;
	}

	private void declare(String key, String variable, String table) {
		imports.add("er.extensions.migration.ERXMigrationTable");
		declared.put(key, variable);
		variables.add(variable);
		statement("ERXMigrationTable " + variable + " = " + table + ";");
	}

	private void change(String change) {
		changes.add(change);
	}

	private void statement(String statement) {
		lines.add(INDENT + statement);
	}

	/**
	 * Writes a gap: a comment that says what is left to decide, and what to do,
	 * and a line that doesn't compile.
	 */
	private void gap(String table, String object, String message, String instruction) {
		comment(message + " " + instruction);
		gaps.add(new Gap(table, entity, object, message));
		statement(GAP + ";");
	}

	/**
	 * Writes a {@code // TODO eogen:} comment, wrapped as
	 * {@link #commentLines(String)} wraps it.
	 */
	private void comment(String text) {
		for (String line : commentLines(text)) {
			statement(line);
		}
	}

	/**
	 * Returns the lines of a {@code // TODO eogen:} comment, without their
	 * indentation, wrapped, but not inside parentheses, so that a call it
	 * suggests stays on one line.
	 *
	 * @param text what it says
	 * @return lines
	 */
	static List<String> commentLines(String text) {
		List<String> lines = new ArrayList<>();
		List<String> words = new ArrayList<>();
		StringBuilder word = new StringBuilder();
		int depth = 0;
		for (String part : text.split(" ")) {
			if (word.length() > 0) {
				word.append(' ');
			}
			word.append(part);
			for (char c : part.toCharArray()) {
				depth += c == '(' ? 1 : c == ')' ? -1 : 0;
			}
			if (depth <= 0) {
				words.add(word.toString());
				word.setLength(0);
				depth = 0;
			}
		}
		if (word.length() > 0) {
			words.add(word.toString());
		}
		StringBuilder line = new StringBuilder("// TODO eogen:");
		boolean empty = true;
		for (String next : words) {
			if (!empty && line.length() + 1 + next.length() > COMMENT_WIDTH - 8) {
				lines.add(line.toString());
				line = new StringBuilder("//");
			}
			line.append(' ').append(next);
			empty = false;
		}
		lines.add(line.toString());
		return lines;
	}

	/**
	 * Returns a Java string literal.
	 *
	 * @param value a string
	 * @return the literal, in quotes
	 */
	static String literal(String value) {
		StringBuilder literal = new StringBuilder("\"");
		for (char c : value.toCharArray()) {
			switch (c) {
			case '"':
				literal.append("\\\"");
				break;
			case '\\':
				literal.append("\\\\");
				break;
			case '\n':
				literal.append("\\n");
				break;
			case '\r':
				literal.append("\\r");
				break;
			case '\t':
				literal.append("\\t");
				break;
			default:
				if (c < 0x20) {
					literal.append(String.format("\\u%04x", (int) c));
				} else {
					literal.append(c);
				}
			}
		}
		return literal.append('"').toString();
	}
}

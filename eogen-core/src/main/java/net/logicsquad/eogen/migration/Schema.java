package net.logicsquad.eogen.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import net.logicsquad.eogen.model.EOAttribute;
import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOEntityIndex;
import net.logicsquad.eogen.model.EOJoin;
import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EORelationship;
import net.logicsquad.eogen.utils.BooleanUtils;

/**
 * The tables that a model's entities map, with their columns, primary keys,
 * foreign keys and indexes, as a migration makes them. Names compare without
 * case, as most databases compare them, and as Wonder's
 * {@code existingColumnNamed} finds a column.
 * <p>
 * A table has the columns of every entity that maps it, so that a
 * subentity's columns are in its parent's table in single-table inheritance.
 * Only the attributes that Entity Modeler would make a column for count: not
 * flattened or derived ones, nor, but in horizontal inheritance, those that
 * an entity inherits. A subentity's own columns allow null in single-table
 * inheritance, since its parent's rows have none. A partial entity
 * ({@code ERXPartial}) adds its columns to its base entity's table, which
 * belongs to another model: the migration adds them, but the table isn't its
 * to create.
 * </p>
 * <p>
 * A foreign key comes from a to-one relationship whose joins end at its
 * destination's primary key, as Entity Modeler's own migrations make them,
 * but only then: a to-one whose joins end elsewhere, such as the inverse of a
 * to-one that holds the key, has no foreign key, and nor has one that
 * propagates its primary key, whose destination's foreign key is back to it.
 * </p>
 * <p>
 * A table knows the model that creates it, and a foreign key the model that
 * creates its destination, so that a migration can depend on another model
 * whose table it needs.
 * </p>
 * <p>
 * A model's {@code userInfo} can list under {@link #KEEP} the tables and
 * columns that its database keeps although the model no longer maps them:
 * each a table, or a table and a column with a dot between them. A schema
 * knows them only by name, since the model no longer says what they are.
 * </p>
 */
final class Schema {
	/**
	 * The key in a model's {@code userInfo} whose value lists the tables and
	 * columns that the model keeps: an array, or a string with the names
	 * separated by commas.
	 */
	static final String KEEP = "eogen.keep";

	/**
	 * A table or a column that a model keeps, though it no longer maps it.
	 */
	static final class Kept {
		private final String table;

		private final String column;

		Kept(String table, String column) {
			this.table = table;
			this.column = column;
		}

		String getTable() {
			return table;
		}

		/**
		 * Returns the column, or null for a table.
		 */
		String getColumn() {
			return column;
		}

		/**
		 * Returns its name as the list has it: {@code table}, or
		 * {@code table.column}.
		 */
		String getName() {
			return column == null ? table : table + "." + column;
		}
	}

	/**
	 * A column.
	 */
	static final class Column {
		private final String name;

		private final String attribute;

		private final ColumnType type;

		private final String className;

		private final String externalType;

		private final Integer width;

		private final Integer precision;

		private final Integer scale;

		private boolean allowsNull;

		private final String defaultValue;

		Column(String name, String attribute, ColumnType type, String className, String externalType, Integer width, Integer precision, Integer scale, boolean allowsNull,
				String defaultValue) {
			this.name = name;
			this.attribute = attribute;
			this.type = type;
			this.className = className;
			this.externalType = externalType;
			this.width = width;
			this.precision = precision;
			this.scale = scale;
			this.allowsNull = allowsNull;
			this.defaultValue = defaultValue;
		}

		String getName() {
			return name;
		}

		/**
		 * Returns the attribute it is for, as {@code Entity.attribute}.
		 */
		String getAttribute() {
			return attribute;
		}

		/**
		 * Returns the call that makes it, or null if eogen can't tell.
		 */
		ColumnType getType() {
			return type;
		}

		/**
		 * Returns its attribute's class, for when eogen can't tell its call.
		 */
		String getClassName() {
			return className;
		}

		/**
		 * Returns its attribute's external type, for when eogen can't tell its
		 * call, or null.
		 */
		String getExternalType() {
			return externalType;
		}

		/**
		 * Returns the width, or 0.
		 */
		int getWidth() {
			return width == null ? 0 : width.intValue();
		}

		/**
		 * Returns the precision, or 0.
		 */
		int getPrecision() {
			return precision == null ? 0 : precision.intValue();
		}

		/**
		 * Returns the scale, or 0.
		 */
		int getScale() {
			return scale == null ? 0 : scale.intValue();
		}

		boolean isAllowsNull() {
			return allowsNull;
		}

		/**
		 * Returns its attribute's {@code default}, or null.
		 */
		String getDefaultValue() {
			return defaultValue;
		}

		/**
		 * Returns whether another column is made by the same call, with the same
		 * arguments, apart from whether it allows null.
		 */
		boolean sameType(Column other) {
			if (type != other.type) {
				return false;
			}
			if (type == null) {
				return Objects.equals(className, other.className) && key(externalType).equals(key(other.externalType));
			}
			switch (type.getArguments()) {
			case WIDTH:
				return getWidth() == other.getWidth();
			case PRECISION_SCALE:
				return getPrecision() == other.getPrecision() && getScale() == other.getScale();
			default:
				// An integer's precision goes to the general call.
				return getPrecision() == other.getPrecision() || (type != ColumnType.INTEGER && type != ColumnType.BIG_INTEGER);
			}
		}
	}

	/**
	 * An index.
	 */
	static final class Index {
		private final String name;

		private final boolean unique;

		private final EOEntityIndex.Constraint constraint;

		private final List<String> columns;

		Index(String name, EOEntityIndex.Constraint constraint, List<String> columns) {
			this.name = name;
			this.constraint = constraint;
			this.unique = constraint == EOEntityIndex.Constraint.Distinct;
			this.columns = Collections.unmodifiableList(new ArrayList<>(columns));
		}

		String getName() {
			return name;
		}

		boolean isUnique() {
			return unique;
		}

		/**
		 * Returns whether Wonder can make it: not a full-text or a spatial one.
		 */
		boolean isPlain() {
			return constraint == EOEntityIndex.Constraint.Distinct || constraint == EOEntityIndex.Constraint.None;
		}

		EOEntityIndex.Constraint getConstraint() {
			return constraint;
		}

		List<String> getColumns() {
			return columns;
		}

		boolean same(Index other) {
			return constraint == other.constraint && keys(columns).equals(keys(other.columns));
		}
	}

	/**
	 * A foreign key, which compares by its columns and tables.
	 */
	static final class ForeignKey {
		private final List<String> sourceColumns;

		private final String destinationTable;

		private final List<String> destinationColumns;

		private final String relationship;

		private final String destinationModel;

		ForeignKey(List<String> sourceColumns, String destinationTable, List<String> destinationColumns, String relationship, String destinationModel) {
			this.sourceColumns = Collections.unmodifiableList(new ArrayList<>(sourceColumns));
			this.destinationTable = destinationTable;
			this.destinationColumns = Collections.unmodifiableList(new ArrayList<>(destinationColumns));
			this.relationship = relationship;
			this.destinationModel = destinationModel;
		}

		List<String> getSourceColumns() {
			return sourceColumns;
		}

		String getDestinationTable() {
			return destinationTable;
		}

		List<String> getDestinationColumns() {
			return destinationColumns;
		}

		/**
		 * Returns the relationship it is for, as {@code Entity.relationship}.
		 */
		String getRelationship() {
			return relationship;
		}

		/**
		 * Returns the name of the model that creates its destination table, as
		 * {@link Table#getCreator()} does, or null.
		 */
		String getDestinationModel() {
			return destinationModel;
		}

		/**
		 * Returns it with tables and columns renamed: those of the earlier
		 * version, in the names they now have.
		 */
		ForeignKey renamed(Map<String, String> tables, Map<String, String> sourceColumns, Map<String, String> destinationColumns) {
			List<String> source = new ArrayList<>();
			for (String column : this.sourceColumns) {
				source.add(sourceColumns.getOrDefault(key(column), column));
			}
			List<String> destination = new ArrayList<>();
			for (String column : this.destinationColumns) {
				destination.add(destinationColumns.getOrDefault(key(column), column));
			}
			return new ForeignKey(source, tables.getOrDefault(key(destinationTable), destinationTable), destination, relationship, destinationModel);
		}

		@Override
		public boolean equals(Object object) {
			if (!(object instanceof ForeignKey)) {
				return false;
			}
			ForeignKey other = (ForeignKey) object;
			return keys(sourceColumns).equals(keys(other.sourceColumns)) && key(destinationTable).equals(key(other.destinationTable))
					&& keys(destinationColumns).equals(keys(other.destinationColumns));
		}

		@Override
		public int hashCode() {
			return Objects.hash(keys(sourceColumns), key(destinationTable), keys(destinationColumns));
		}
	}

	/**
	 * A table.
	 */
	static final class Table {
		private final String name;

		private final String variable;

		private final List<String> entities = new ArrayList<>();

		private boolean owned;

		private String creator;

		private final Map<String, Column> columns = new LinkedHashMap<>();

		private final List<String> primaryKey = new ArrayList<>();

		private final Map<String, Index> indexes = new LinkedHashMap<>();

		private final Set<ForeignKey> foreignKeys = new LinkedHashSet<>();

		private final Map<String, String> attributeColumns = new HashMap<>();

		Table(String name, String variable) {
			this.name = name;
			this.variable = variable;
		}

		String getName() {
			return name;
		}

		/**
		 * Returns the name of a migration's variable for it, from its first
		 * entity's: {@code bookTable} for {@code Book}.
		 */
		String getVariable() {
			return variable;
		}

		/**
		 * Returns the names of the entities that map it, parents first.
		 */
		List<String> getEntities() {
			return entities;
		}

		/**
		 * Returns whether the model creates it: whether an entity maps it that
		 * isn't a partial entity, whose base entity's model creates it.
		 */
		boolean isOwned() {
			return owned;
		}

		/**
		 * Returns the name of the model that creates it: this one, if it owns
		 * it, or else the model of the partial entity's base entity, or null.
		 */
		String getCreator() {
			return creator;
		}

		/**
		 * Returns its columns by their names without case, in the order a new
		 * table has them.
		 */
		Map<String, Column> getColumns() {
			return columns;
		}

		List<String> getPrimaryKey() {
			return primaryKey;
		}

		/**
		 * Returns its indexes by their names without case.
		 */
		Map<String, Index> getIndexes() {
			return indexes;
		}

		Set<ForeignKey> getForeignKeys() {
			return foreignKeys;
		}

		/**
		 * Returns the column, without case, of each attribute that has one, by
		 * {@code Entity.attribute}, which tells a column that an attribute keeps
		 * from one that is new.
		 */
		Map<String, String> getAttributeColumns() {
			return attributeColumns;
		}
	}

	private final String model;

	private final Map<String, Table> tables = new TreeMap<>();

	private final Map<String, String> entityTables = new HashMap<>();

	private final Map<String, Kept> kept = new TreeMap<>();

	private final Map<String, String> mappedKeeps = new LinkedHashMap<>();

	private Schema(String model) {
		this.model = model;
	}

	/**
	 * Returns an empty schema, for a model that has no tables yet.
	 *
	 * @param model the model's name
	 * @return schema
	 */
	static Schema empty(String model) {
		return new Schema(model);
	}

	/**
	 * Returns the tables of a model, which must be resolved in its model group.
	 *
	 * @param model a model
	 * @return its schema
	 */
	static Schema of(EOModel model) {
		Schema schema = new Schema(model.getName());
		List<EOEntity> entities = new ArrayList<>();
		for (EOEntity entity : model.getEntities()) {
			if (!entity.isPrototype() && !isBlank(entity.getExternalName())) {
				entities.add(entity);
			}
		}
		entities.sort(Comparator.comparingInt(Schema::depth).thenComparing(EOEntity::getName));
		for (EOEntity entity : entities) {
			String key = key(entity.getExternalName());
			Table table = schema.tables.get(key);
			if (table == null) {
				// A partial entity's table is its base entity's.
				EOEntity base = entity.isPartialEntitySet() ? entity.getPartialEntity() : entity;
				table = new Table(entity.getExternalName(), base.getInitialLowercaseName() + "Table");
				table.creator = creator(entity);
				schema.tables.put(key, table);
			}
			table.entities.add(entity.getName());
			schema.entityTables.put(entity.getName(), key);
			boolean partial = entity.isPartialEntitySet();
			if (!partial && !table.owned) {
				table.owned = true;
				table.creator = model.getName();
				for (EOAttribute attribute : entity.getPrimaryKeyAttributes()) {
					if (!isBlank(attribute.getColumnName())) {
						table.primaryKey.add(attribute.getColumnName());
					}
				}
			}
			for (EOAttribute attribute : entity.getSortedAttributes()) {
				String columnName = attribute.getColumnName();
				if (!attribute.getSqlGenerationCreateProperty() || isBlank(columnName)) {
					continue;
				}
				if (partial && BooleanUtils.isTrue(attribute.isPrimaryKey())) {
					// The base entity's table has it.
					continue;
				}
				String columnKey = key(columnName);
				table.attributeColumns.put(entity.getName() + "." + attribute.getName(), columnKey);
				Column existing = table.columns.get(columnKey);
				if (existing != null) {
					existing.allowsNull |= attribute.getSqlGenerationAllowsNull();
					continue;
				}
				Object defaultValue = attribute.getUserInfo() == null ? null : attribute.getUserInfo().get("default");
				table.columns.put(columnKey, new Column(columnName, entity.getName() + "." + attribute.getName(), ColumnType.of(attribute), attribute.getJavaClassName(), attribute.getExternalType(), attribute.getWidth(),
						attribute.getPrecision(), attribute.getScale(), attribute.getSqlGenerationAllowsNull(), defaultValue == null ? null : defaultValue.toString()));
			}
			for (EOEntityIndex index : entity.getSortedEntityIndexes()) {
				List<String> columns = new ArrayList<>();
				List<EOAttribute> attributes = new ArrayList<>(index.getAttributes());
				// The model classes keep an index's attributes in no order, and read
				// them sorted.
				attributes.sort(Comparator.comparing(attribute -> attribute == null ? "" : attribute.getName()));
				for (EOAttribute attribute : attributes) {
					if (attribute != null && !isBlank(attribute.getColumnName())) {
						columns.add(attribute.getColumnName());
					}
				}
				if (!columns.isEmpty() && !isBlank(index.getName())) {
					table.indexes.putIfAbsent(key(index.getName()), new Index(index.getName(), index.getConstraint(), columns));
				}
			}
			for (EORelationship relationship : entity.getSortedToOneRelationships()) {
				ForeignKey foreignKey = foreignKey(entity, relationship);
				if (foreignKey != null && table.columns.keySet().containsAll(keys(foreignKey.getSourceColumns()))) {
					table.foreignKeys.add(foreignKey);
				}
			}
		}
		schema.keep(model.getUserInfo() == null ? null : model.getUserInfo().get(KEEP));
		return schema;
	}

	/**
	 * Reads a model's {@link #KEEP} list. A name with a dot is a column, after
	 * its last dot, and its table. A name that the model still maps is noted
	 * with what maps it, and kept otherwise.
	 */
	private void keep(Object list) {
		List<String> names = new ArrayList<>();
		if (list instanceof Collection) {
			for (Object name : (Collection<?>) list) {
				if (name != null) {
					names.add(name.toString());
				}
			}
		} else if (list != null) {
			names.addAll(Arrays.asList(list.toString().split(",")));
		}
		for (String name : names) {
			name = name.trim();
			int dot = name.lastIndexOf('.');
			if (name.isEmpty() || dot == 0 || dot == name.length() - 1) {
				continue;
			}
			Kept entry = dot < 0 ? new Kept(name, null) : new Kept(name.substring(0, dot), name.substring(dot + 1));
			Table table = tables.get(key(entry.getTable()));
			if (table != null && entry.getColumn() == null) {
				mappedKeeps.put(name, table.getEntities().get(0));
			} else if (table != null && table.columns.containsKey(key(entry.getColumn()))) {
				mappedKeeps.put(name, table.columns.get(key(entry.getColumn())).getAttribute());
			} else {
				kept.put(key(name), entry);
			}
		}
	}

	/**
	 * Returns the foreign key that a to-one relationship has, or null.
	 */
	private static ForeignKey foreignKey(EOEntity entity, EORelationship relationship) {
		if (!relationship.getSqlGenerationCreateProperty() || relationship.isFlattened() || !isBlank(relationship.getDefinition())) {
			return null;
		}
		if (BooleanUtils.isTrue(relationship.isPropagatesPrimaryKey())) {
			// Its destination's primary key is its own, as for a dependent
			// one-to-one, so the foreign key is the destination's, back.
			return null;
		}
		EOEntity destination = relationship.getDestination();
		List<EOJoin> joins = relationship.getJoins();
		if (destination == null || destination.isPrototype() || isBlank(destination.getExternalName()) || joins == null || joins.isEmpty()) {
			return null;
		}
		List<String> sourceColumns = new ArrayList<>();
		List<String> destinationColumns = new ArrayList<>();
		for (EOJoin join : joins) {
			EOAttribute source = join.getSourceAttribute();
			EOAttribute target = join.getDestinationAttribute();
			if (source == null || target == null || isBlank(source.getColumnName()) || isBlank(target.getColumnName()) || source.isFlattened() || target.isFlattened()) {
				return null;
			}
			sourceColumns.add(source.getColumnName());
			destinationColumns.add(target.getColumnName());
		}
		List<String> primaryKey = new ArrayList<>();
		for (EOAttribute attribute : destination.getPrimaryKeyAttributes()) {
			primaryKey.add(attribute.getColumnName());
		}
		if (primaryKey.size() != destinationColumns.size() || !new LinkedHashSet<>(keys(primaryKey)).equals(new LinkedHashSet<>(keys(destinationColumns)))) {
			return null;
		}
		return new ForeignKey(sourceColumns, destination.getExternalName(), destinationColumns, entity.getName() + "." + relationship.getName(), creator(destination));
	}

	/**
	 * Returns the name of the model that creates an entity's table: the model
	 * of the entity at the top of the table's single-table inheritance, through
	 * a partial entity's base entity, or null.
	 */
	private static String creator(EOEntity entity) {
		EOEntity top = entity.isPartialEntitySet() && entity.getPartialEntity() != null ? entity.getPartialEntity() : entity;
		String table = key(top.getExternalName());
		for (int depth = 0; top.getParent() != null && key(top.getParent().getExternalName()).equals(table) && depth < 100; depth++) {
			top = top.getParent();
		}
		return top.getModel() == null ? null : top.getModel().getName();
	}

	/**
	 * Returns how many parents an entity has.
	 */
	private static int depth(EOEntity entity) {
		int depth = 0;
		for (EOEntity parent = entity.getParent(); parent != null && depth < 100; parent = parent.getParent()) {
			depth++;
		}
		return depth;
	}

	String getModel() {
		return model;
	}

	/**
	 * Returns the tables by their names without case, in that order.
	 */
	Map<String, Table> getTables() {
		return tables;
	}

	/**
	 * Returns the table, without case, that each entity maps, which tells a
	 * table that an entity keeps, under a new name, from one that is new.
	 */
	Map<String, String> getEntityTables() {
		return entityTables;
	}

	/**
	 * Returns the tables and columns that the model keeps, though it no longer
	 * maps them, by their names without case, in that order.
	 */
	Map<String, Kept> getKept() {
		return kept;
	}

	/**
	 * Returns whether the model keeps a table, by its name without case.
	 */
	boolean keepsTable(String table) {
		Kept entry = kept.get(key(table));
		return entry != null && entry.getColumn() == null;
	}

	/**
	 * Returns whether the model keeps a column, or its table, by their names
	 * without case.
	 */
	boolean keepsColumn(String table, String column) {
		return kept.containsKey(key(table) + "." + key(column)) || keepsTable(table);
	}

	/**
	 * Returns the names in the model's {@link #KEEP} list that it still maps,
	 * each with the entity, or the attribute as {@code Entity.attribute}, that
	 * maps it.
	 */
	Map<String, String> getMappedKeeps() {
		return mappedKeeps;
	}

	/**
	 * Returns a name without case, as tables, columns and indexes compare.
	 */
	static String key(String name) {
		return name == null ? "" : name.toLowerCase(Locale.ROOT);
	}

	static List<String> keys(List<String> names) {
		List<String> keys = new ArrayList<>();
		for (String name : names) {
			keys.add(key(name));
		}
		return keys;
	}

	private static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}

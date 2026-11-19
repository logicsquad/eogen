package er.extensions.migration;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eoaccess.EOSynchronizationFactory;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.jdbc.ERXJDBCUtilities;
import er.extensions.jdbc.ERXSQLHelper;

public class ERXMigrationTable {
	private final ERXMigrationDatabase _database;

	private final NSMutableArray<ERXMigrationColumn> _columns = new NSMutableArray<ERXMigrationColumn>();

	private String _name;

	private boolean _new = true;

	protected ERXMigrationTable(ERXMigrationDatabase database, String name) {
		_database = database;
		_name = name;
	}

	public String name() {
		return _name;
	}

	public void _setNew(boolean isNew) {
		_new = isNew;
	}

	public EOEntity _blankEntity() {
		EOEntity entity = new EOEntity();
		entity.setExternalName(_name);
		entity.setName("ERXMigrationTable_" + _name);
		return entity;
	}

	public EOEntity _newEntity() {
		EOEntity entity = _blankEntity();
		NSMutableArray<EOAttribute> primaryKeyAttributes = new NSMutableArray<EOAttribute>();
		for (ERXMigrationColumn column : _columns) {
			EOAttribute attribute = column._newAttribute(entity);
			if (column.isPrimaryKey()) {
				primaryKeyAttributes.addObject(attribute);
			}
		}
		entity.setPrimaryKeyAttributes(primaryKeyAttributes);
		return entity;
	}

	public ERXMigrationColumn existingColumnNamed(String name) {
		for (ERXMigrationColumn column : _columns) {
			if (column.name().equalsIgnoreCase(name)) {
				return column;
			}
		}
		if (_new) {
			throw new IllegalStateException("You requested the column named '" + name + "' in the table '" + _name + "', but that column hasn't been created yet.");
		}
		return _newColumn(name);
	}

	private ERXMigrationColumn _newColumn(String name) {
		ERXMigrationColumn column = new ERXMigrationColumn(this, name);
		_columns.addObject(column);
		return column;
	}

	private void _execute(NSArray<EOSQLExpression> expressions) throws SQLException {
		ERXJDBCUtilities.executeUpdateScript(_database.adaptorChannel(), ERXMigrationDatabase._stringsForExpressions(expressions));
	}

	public ERXMigrationColumn newColumn(String name, int jdbcType, int width, int precision, int scale, boolean allowsNull, String overrideValueType) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newColumn(String name, int jdbcType, int width, int precision, int scale, boolean allowsNull, String overrideValueType, Object defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newStringColumn(String name, int width, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newStringColumn(String name, int width, boolean allowsNull, String defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newStringColumn(String name, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newStringColumn(String name, boolean allowsNull, String defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newLargeStringColumn(String name, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newIntegerColumn(String name, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newIntegerColumn(String name, boolean allowsNull, Integer defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newIntegerColumn(String name, int scale, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newIntegerColumn(String name, int scale, boolean allowsNull, Integer defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newIntegerColumn(String name, int scale, int precision, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newIntegerColumn(String name, int scale, int precision, boolean allowsNull, Object defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newSmallIntegerColumn(String name, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newSmallIntegerColumn(String name, boolean allowsNull, Short defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newBigIntegerColumn(String name, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newBigIntegerColumn(String name, boolean allowsNull, Long defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newFloatColumn(String name, int precision, int scale, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newFloatColumn(String name, int precision, int scale, boolean allowsNull, Float defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newDoubleColumn(String name, int precision, int scale, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newDoubleColumn(String name, int precision, int scale, boolean allowsNull, Double defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newBigDecimalColumn(String name, int precision, int scale, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newBigDecimalColumn(String name, int precision, int scale, boolean allowsNull, BigDecimal defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newBooleanColumn(String name, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newBooleanColumn(String name, boolean allowsNull, Boolean defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newIntBooleanColumn(String name, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newIntBooleanColumn(String name, boolean allowsNull, Boolean defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newFlagBooleanColumn(String name, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newFlagBooleanColumn(String name, boolean allowsNull, Boolean defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newClobColumn(String name, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newBlobColumn(String name, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newBlobColumn(String name, int width, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newBlobColumn(String name, int width, boolean allowsNull, NSData defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newTimestampColumn(String name, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newTimestampColumn(String name, boolean allowsNull, NSTimestamp defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newDateColumn(String name, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newDateColumn(String name, boolean allowsNull, NSTimestamp defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newTimeColumn(String name, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newIpAddressColumn(String name, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newIpAddressColumn(String name, boolean allowsNull, String defaultValue) throws SQLException {
		return _newColumn(name);
	}

	public ERXMigrationColumn newUuidColumn(String name, boolean allowsNull) throws SQLException {
		return _newColumn(name);
	}

	public void create() throws SQLException {
		if (_new) {
			_execute(_database.synchronizationFactory().createTableStatementsForEntityGroup(new NSArray<EOEntity>(_newEntity())));
			_new = false;
		}
	}

	public void drop() throws SQLException {
		_execute(_database.synchronizationFactory().dropTableStatementsForEntityGroup(new NSArray<EOEntity>(_blankEntity())));
		_database._tableDropped(this);
	}

	public void renameTo(String newName) throws SQLException {
	}

	public void setPrimaryKey(String columnName) throws SQLException {
		setPrimaryKey(existingColumnNamed(columnName));
	}

	public void setPrimaryKey(String... columnNames) throws SQLException {
		ERXMigrationColumn[] columns = new ERXMigrationColumn[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			columns[i] = existingColumnNamed(columnNames[i]);
		}
		setPrimaryKey(columns);
	}

	public void setPrimaryKey(ERXMigrationColumn... columns) throws SQLException {
		setPrimaryKey(!_new, columns);
	}

	/**
	 * Adds the primary key's constraint, and then its support, as Wonder's
	 * does.
	 */
	public void setPrimaryKey(boolean create, ERXMigrationColumn... columns) throws SQLException {
		for (ERXMigrationColumn column : _columns) {
			column._setPrimaryKey(false);
		}
		for (ERXMigrationColumn column : columns) {
			column._setPrimaryKey(true);
		}
		if (create) {
			EOSynchronizationFactory schemaGeneration = _database.synchronizationFactory();
			EOEntity entity = _blankEntity();
			NSMutableArray<EOAttribute> attributes = new NSMutableArray<EOAttribute>();
			for (ERXMigrationColumn column : columns) {
				attributes.addObject(column._newAttribute(entity));
			}
			entity.setPrimaryKeyAttributes(attributes);
			NSArray<EOSQLExpression> expressions = schemaGeneration.primaryKeyConstraintStatementsForEntityGroup(new NSArray<EOEntity>(entity));
			ERXMigrationDatabase._ensureNotEmpty(expressions, "add primary key", true);
			_execute(expressions.arrayByAddingObjectsFromArray(schemaGeneration.primaryKeySupportStatementsForEntityGroup(new NSArray<EOEntity>(entity))));
		}
	}

	public void dropPrimaryKey(ERXMigrationColumn... columns) throws SQLException {
	}

	public void addUniqueIndex(String columnName) throws SQLException {
	}

	public void addUniqueIndex(String indexName, String columnName) throws SQLException {
	}

	public void addUniqueIndex(String indexName, String columnName, int width) throws SQLException {
	}

	public void addUniqueIndex(String indexName, ERXMigrationColumn... columns) throws SQLException {
	}

	public void addUniqueIndex(String indexName, ERXSQLHelper.ColumnIndex... columnIndexes) throws SQLException {
	}

	public void addUniqueIndex(boolean create, String indexName, ERXSQLHelper.ColumnIndex... columnIndexes) throws SQLException {
	}

	public void addIndex(String columnName) throws SQLException {
	}

	public void addIndex(String indexName, String columnName) throws SQLException {
	}

	public void addIndex(String indexName, String columnName, int width) throws SQLException {
	}

	public void addIndex(String indexName, ERXMigrationColumn... columns) throws SQLException {
	}

	public void addIndex(String indexName, ERXSQLHelper.ColumnIndex... columnIndexes) throws SQLException {
	}

	public void addIndex(boolean create, String indexName, ERXSQLHelper.ColumnIndex... columnIndexes) throws SQLException {
	}

	public void addIndex(ERXMigrationIndex index) throws SQLException {
	}

	public void addIndex(boolean create, ERXMigrationIndex index) throws SQLException {
	}

	public void addForeignKey(String sourceColumnName, String destinationTableName, String destinationColumnName) throws SQLException {
	}

	public void addForeignKey(String sourceColumnName, ERXMigrationColumn destinationColumn) throws SQLException {
	}

	public void addForeignKey(ERXMigrationColumn sourceColumn, ERXMigrationColumn destinationColumn) throws SQLException {
	}

	public void addForeignKey(ERXMigrationColumn[] sourceColumns, ERXMigrationColumn[] destinationColumns) throws SQLException {
	}

	public void addForeignKey(boolean create, ERXMigrationColumn sourceColumn, ERXMigrationColumn destinationColumn) throws SQLException {
	}

	public void addForeignKey(boolean create, ERXMigrationColumn[] sourceColumns, ERXMigrationColumn[] destinationColumns) throws SQLException {
	}
}

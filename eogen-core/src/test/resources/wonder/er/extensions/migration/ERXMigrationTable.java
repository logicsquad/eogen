package er.extensions.migration;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.jdbc.ERXSQLHelper;

public class ERXMigrationTable {
	public ERXMigrationColumn existingColumnNamed(String name) {
		return null;
	}

	public ERXMigrationColumn newColumn(String name, int jdbcType, int width, int precision, int scale, boolean allowsNull, String overrideValueType) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newColumn(String name, int jdbcType, int width, int precision, int scale, boolean allowsNull, String overrideValueType, Object defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newStringColumn(String name, int width, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newStringColumn(String name, int width, boolean allowsNull, String defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newStringColumn(String name, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newStringColumn(String name, boolean allowsNull, String defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newLargeStringColumn(String name, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newIntegerColumn(String name, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newIntegerColumn(String name, boolean allowsNull, Integer defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newIntegerColumn(String name, int scale, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newIntegerColumn(String name, int scale, boolean allowsNull, Integer defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newIntegerColumn(String name, int scale, int precision, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newIntegerColumn(String name, int scale, int precision, boolean allowsNull, Object defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newSmallIntegerColumn(String name, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newSmallIntegerColumn(String name, boolean allowsNull, Short defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newBigIntegerColumn(String name, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newBigIntegerColumn(String name, boolean allowsNull, Long defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newFloatColumn(String name, int precision, int scale, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newFloatColumn(String name, int precision, int scale, boolean allowsNull, Float defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newDoubleColumn(String name, int precision, int scale, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newDoubleColumn(String name, int precision, int scale, boolean allowsNull, Double defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newBigDecimalColumn(String name, int precision, int scale, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newBigDecimalColumn(String name, int precision, int scale, boolean allowsNull, BigDecimal defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newBooleanColumn(String name, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newBooleanColumn(String name, boolean allowsNull, Boolean defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newIntBooleanColumn(String name, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newIntBooleanColumn(String name, boolean allowsNull, Boolean defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newFlagBooleanColumn(String name, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newFlagBooleanColumn(String name, boolean allowsNull, Boolean defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newClobColumn(String name, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newBlobColumn(String name, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newBlobColumn(String name, int width, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newBlobColumn(String name, int width, boolean allowsNull, NSData defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newTimestampColumn(String name, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newTimestampColumn(String name, boolean allowsNull, NSTimestamp defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newDateColumn(String name, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newDateColumn(String name, boolean allowsNull, NSTimestamp defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newTimeColumn(String name, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newIpAddressColumn(String name, boolean allowsNull) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newIpAddressColumn(String name, boolean allowsNull, String defaultValue) throws SQLException {
		return null;
	}

	public ERXMigrationColumn newUuidColumn(String name, boolean allowsNull) throws SQLException {
		return null;
	}

	public void create() throws SQLException {
	}

	public void drop() throws SQLException {
	}

	public void renameTo(String newName) throws SQLException {
	}

	public void setPrimaryKey(String columnName) throws SQLException {
	}

	public void setPrimaryKey(String... columnNames) throws SQLException {
	}

	public void setPrimaryKey(ERXMigrationColumn... columns) throws SQLException {
	}

	public void setPrimaryKey(boolean create, ERXMigrationColumn... columns) throws SQLException {
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

package er.extensions.migration;

import java.sql.SQLException;

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.foundation.NSDictionary;

public class ERXMigrationColumn {
	private final ERXMigrationTable _table;

	private final String _name;

	private boolean _primaryKey;

	protected ERXMigrationColumn(ERXMigrationTable table, String name) {
		_table = table;
		_name = name;
	}

	public ERXMigrationTable table() {
		return _table;
	}

	public String name() {
		return _name;
	}

	public boolean isPrimaryKey() {
		return _primaryKey;
	}

	public void _setPrimaryKey(boolean primaryKey) {
		_primaryKey = primaryKey;
	}

	public EOAttribute _newAttribute() {
		return _newAttribute(_table._blankEntity());
	}

	public EOAttribute _newAttribute(EOEntity entity) {
		EOAttribute attribute = new EOAttribute();
		attribute.setName(_name);
		attribute.setColumnName(_name);
		entity.addAttribute(attribute);
		return attribute;
	}

	public void setDefaultValue(Object defaultValue) {
	}

	public void create() throws SQLException {
	}

	public void delete() throws SQLException {
	}

	public void renameTo(String newName) throws SQLException {
	}

	public void setAllowsNull(boolean allowsNull) throws SQLException {
	}

	public void setDataType(int jdbcType, int scale, int precision, int width, NSDictionary options) throws SQLException {
	}

	public void setWidthType(int jdbcType, int width, NSDictionary options) throws SQLException {
	}

	public void setNumericType(int jdbcType, int scale, int precision, NSDictionary options) throws SQLException {
	}
}

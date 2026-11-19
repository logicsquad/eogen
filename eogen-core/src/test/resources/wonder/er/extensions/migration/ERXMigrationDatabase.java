package er.extensions.migration;

import com.webobjects.eoaccess.EOAdaptor;
import com.webobjects.eoaccess.EOAdaptorChannel;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eoaccess.EOSynchronizationFactory;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

public class ERXMigrationDatabase {
	private final EOAdaptorChannel _adaptorChannel;

	private final NSMutableArray<ERXMigrationTable> _tables = new NSMutableArray<ERXMigrationTable>();

	private ERXMigrationDatabase(EOAdaptorChannel adaptorChannel) {
		_adaptorChannel = adaptorChannel;
	}

	public static ERXMigrationDatabase database(EOAdaptorChannel adaptorChannel) {
		return new ERXMigrationDatabase(adaptorChannel);
	}

	public EOSynchronizationFactory synchronizationFactory() {
		return adaptor().synchronizationFactory();
	}

	public EOAdaptor adaptor() {
		return _adaptorChannel.adaptorContext().adaptor();
	}

	public EOAdaptorChannel adaptorChannel() {
		return _adaptorChannel;
	}

	public ERXMigrationTable existingTableNamed(String name) {
		for (ERXMigrationTable table : _tables) {
			if (table.name().equalsIgnoreCase(name)) {
				return table;
			}
		}
		ERXMigrationTable table = new ERXMigrationTable(this, name);
		table._setNew(false);
		_tables.addObject(table);
		return table;
	}

	public ERXMigrationColumn existingColumnNamed(String tableName, String columnName) {
		return existingTableNamed(tableName).existingColumnNamed(columnName);
	}

	public ERXMigrationTable newTableNamed(String name) {
		for (ERXMigrationTable table : _tables) {
			if (table.name().equalsIgnoreCase(name)) {
				throw new IllegalArgumentException("You've already referenced a table named '" + name + "'.");
			}
		}
		ERXMigrationTable newTable = new ERXMigrationTable(this, name);
		_tables.addObject(newTable);
		return newTable;
	}

	public void _tableDropped(ERXMigrationTable table) {
		_tables.removeObject(table);
	}

	public static void _ensureNotEmpty(NSArray<EOSQLExpression> expressions, String operationName, boolean required) {
		if ((expressions == null || expressions.count() == 0) && required) {
			throw new ERXMigrationFailedException("Your EOSynchronizationFactory does not support the required '" + operationName + "' operation.");
		}
	}

	public static NSArray<String> _stringsForExpressions(NSArray<EOSQLExpression> expressions) {
		NSMutableArray<String> strings = new NSMutableArray<String>();
		for (EOSQLExpression expression : expressions) {
			strings.addObject(expression.statement());
		}
		return strings;
	}

	public static abstract class Migration {
		public static final boolean ALLOWS_NULL = true;

		public static final boolean NOT_NULL = false;

		public NSArray<ERXModelVersion> modelDependencies() {
			return null;
		}

		public abstract void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable;

		public abstract void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable;
	}
}

package er.extensions.migration;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

public class ERXMigrationDatabase {
	public ERXMigrationTable existingTableNamed(String name) {
		return null;
	}

	public ERXMigrationColumn existingColumnNamed(String tableName, String columnName) {
		return null;
	}

	public ERXMigrationTable newTableNamed(String name) {
		return null;
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

package er.taggable.model;

import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationFailedException;
import er.extensions.migration.ERXMigrationTable;

/**
 * Creates the tables of the ERTaggable model.
 */
public class ERTaggable0 extends ERXMigrationDatabase.Migration {
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		throw new ERXMigrationFailedException("ERTaggable0 can't be undone.");
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable erTagTable = database.newTableNamed("ERTag");
		erTagTable.newIntegerColumn("id", NOT_NULL);
		erTagTable.newStringColumn("name", 255, ALLOWS_NULL);
		erTagTable.create();
		erTagTable.setPrimaryKey("id");
	}
}

package er.example.erxpartials.model;

import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationFailedException;
import er.extensions.migration.ERXMigrationTable;

/**
 * Creates the tables of the ERXPartialBase model.
 */
public class ERXPartialBase0 extends ERXMigrationDatabase.Migration {
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		throw new ERXMigrationFailedException("ERXPartialBase0 can't be undone.");
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable genderTypeTable = database.newTableNamed("GenderType");
		genderTypeTable.newIntegerColumn("id", NOT_NULL);
		genderTypeTable.newStringColumn("name", 255, NOT_NULL);
		genderTypeTable.create();
		genderTypeTable.setPrimaryKey("id");

		ERXMigrationTable personTable = database.newTableNamed("Person");
		personTable.newStringColumn("firstName", 255, NOT_NULL);
		personTable.newIntegerColumn("genderTypeID", NOT_NULL);
		personTable.newIntegerColumn("id", NOT_NULL);
		personTable.newStringColumn("lastName", 255, NOT_NULL);
		personTable.create();
		personTable.setPrimaryKey("id");

		personTable.addForeignKey("genderTypeID", "GenderType", "id");
	}
}

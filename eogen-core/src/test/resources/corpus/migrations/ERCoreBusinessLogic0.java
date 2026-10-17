package er.corebusinesslogic;

import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationFailedException;
import er.extensions.migration.ERXMigrationTable;

/**
 * Creates the tables of the ERCoreBusinessLogic model.
 */
public class ERCoreBusinessLogic0 extends ERXMigrationDatabase.Migration {
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		throw new ERXMigrationFailedException("ERCoreBusinessLogic0 can't be undone.");
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable ercAuditBlobTable = database.newTableNamed("ERCAuditBlob");
		ercAuditBlobTable.newBlobColumn("BLOB_VALUE", NOT_NULL);
		ercAuditBlobTable.newIntegerColumn("id", NOT_NULL);
		ercAuditBlobTable.create();
		ercAuditBlobTable.setPrimaryKey("id");

		ERXMigrationTable ercAuditTrailTable = database.newTableNamed("ERCAuditTrail");
		ercAuditTrailTable.newStringColumn("GID", 255, NOT_NULL);
		ercAuditTrailTable.newIntegerColumn("id", NOT_NULL);
		ercAuditTrailTable.newIntBooleanColumn("IS_DELETED", NOT_NULL);
		ercAuditTrailTable.create();
		ercAuditTrailTable.setPrimaryKey("id");

		ERXMigrationTable ercAuditTrailEntryTable = database.newTableNamed("ERCAuditTrailEntry");
		ercAuditTrailEntryTable.newTimestampColumn("CREATED", NOT_NULL);
		ercAuditTrailEntryTable.newIntegerColumn("id", NOT_NULL);
		ercAuditTrailEntryTable.newStringColumn("KEY_PATH", 100, ALLOWS_NULL);
		ercAuditTrailEntryTable.newIntegerColumn("NEW_BLOB_VALUE_ID", ALLOWS_NULL);
		ercAuditTrailEntryTable.newStringColumn("NEW_VALUES", 1000, ALLOWS_NULL);
		ercAuditTrailEntryTable.newIntegerColumn("OLD_BLOB_VALUE_ID", ALLOWS_NULL);
		ercAuditTrailEntryTable.newStringColumn("OLD_VALUES", 1000, ALLOWS_NULL);
		ercAuditTrailEntryTable.newIntegerColumn("TRAIL_ID", NOT_NULL);
		ercAuditTrailEntryTable.newStringColumn("TYPE", 50, NOT_NULL);
		ercAuditTrailEntryTable.newStringColumn("USER_GLOBAL_ID", 255, NOT_NULL);
		ercAuditTrailEntryTable.newBlobColumn("USER_INFO", ALLOWS_NULL);
		ercAuditTrailEntryTable.create();
		ercAuditTrailEntryTable.setPrimaryKey("id");

		ERXMigrationTable ercHelpTextTable = database.newTableNamed("ERCHELP_TEXT");
		ercHelpTextTable.newIntegerColumn("ID", NOT_NULL);
		ercHelpTextTable.newStringColumn("KEY_", 100, NOT_NULL);
		ercHelpTextTable.newLargeStringColumn("VALUE_", ALLOWS_NULL);
		ercHelpTextTable.create();
		ercHelpTextTable.setPrimaryKey("ID");

		ERXMigrationTable ercLogEntryTable = database.newTableNamed("ERCLOG_ENTRY");
		ercLogEntryTable.newTimestampColumn("CREATED", NOT_NULL);
		ercLogEntryTable.newIntegerColumn("ID", NOT_NULL);
		ercLogEntryTable.newLargeStringColumn("TEXT_", NOT_NULL);
		ercLogEntryTable.newIntegerColumn("USER_ID", NOT_NULL);
		ercLogEntryTable.create();
		ercLogEntryTable.setPrimaryKey("ID");

		ERXMigrationTable ercPreferenceTable = database.newTableNamed("ERCPREFER");
		ercPreferenceTable.newIntegerColumn("ID", NOT_NULL);
		ercPreferenceTable.newStringColumn("KEY_", 100, NOT_NULL);
		ercPreferenceTable.newIntegerColumn("USER_ID", ALLOWS_NULL);
		ercPreferenceTable.newLargeStringColumn("VALUE_", ALLOWS_NULL);
		ercPreferenceTable.create();
		ercPreferenceTable.setPrimaryKey("ID");

		ERXMigrationTable ercStaticTable = database.newTableNamed("ERCSTATIC");
		ercStaticTable.newIntegerColumn("ID", NOT_NULL);
		ercStaticTable.newStringColumn("KEY_", 100, NOT_NULL);
		ercStaticTable.newStringColumn("VALUE_", 1000, ALLOWS_NULL);
		ercStaticTable.create();
		ercStaticTable.setPrimaryKey("ID");

		ercAuditTrailEntryTable.addForeignKey("NEW_BLOB_VALUE_ID", "ERCAuditBlob", "id");
		ercAuditTrailEntryTable.addForeignKey("OLD_BLOB_VALUE_ID", "ERCAuditBlob", "id");
		ercAuditTrailEntryTable.addForeignKey("TRAIL_ID", "ERCAuditTrail", "id");
	}
}

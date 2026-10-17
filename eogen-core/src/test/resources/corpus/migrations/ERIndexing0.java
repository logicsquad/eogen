package er.indexing.attributes;

import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationFailedException;
import er.extensions.migration.ERXMigrationTable;

/**
 * Creates the tables of the ERIndexing model.
 */
public class ERIndexing0 extends ERXMigrationDatabase.Migration {
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		throw new ERXMigrationFailedException("ERIndexing0 can't be undone.");
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable eriAttributeTable = database.newTableNamed("ERIAttribute");
		eriAttributeTable.newIntegerColumn("attributeGroupId", NOT_NULL);
		eriAttributeTable.newIntegerColumn("attributeTypeId", NOT_NULL);
		eriAttributeTable.newIntegerColumn("id", NOT_NULL);
		eriAttributeTable.newStringColumn("name", 100, NOT_NULL);
		eriAttributeTable.newBlobColumn("possibleValues", ALLOWS_NULL);
		eriAttributeTable.newIntegerColumn("storageType", NOT_NULL);
		eriAttributeTable.create();
		eriAttributeTable.setPrimaryKey("id");

		ERXMigrationTable eriAttributeGroupTable = database.newTableNamed("ERIAttributeGroup");
		eriAttributeGroupTable.newIntegerColumn("id", NOT_NULL);
		eriAttributeGroupTable.newStringColumn("name", 100, NOT_NULL);
		eriAttributeGroupTable.newIntegerColumn("parentId", ALLOWS_NULL);
		eriAttributeGroupTable.create();
		eriAttributeGroupTable.setPrimaryKey("id");

		ERXMigrationTable eriAttributeTypeTable = database.newTableNamed("ERIAttributeType");
		eriAttributeTypeTable.newStringColumn("format", 100, ALLOWS_NULL);
		eriAttributeTypeTable.newIntegerColumn("id", NOT_NULL);
		eriAttributeTypeTable.newStringColumn("name", 100, NOT_NULL);
		eriAttributeTypeTable.newIntegerColumn("valueType", NOT_NULL);
		eriAttributeTypeTable.create();
		eriAttributeTypeTable.setPrimaryKey("id");

		ERXMigrationTable eriAttributeValidationRuleTable = database.newTableNamed("ERIAttributeValidationRule");
		eriAttributeValidationRuleTable.newIntegerColumn("attributeId", NOT_NULL);
		eriAttributeValidationRuleTable.newIntegerColumn("validationRuleId", NOT_NULL);
		eriAttributeValidationRuleTable.create();
		eriAttributeValidationRuleTable.setPrimaryKey("attributeId", "validationRuleId");

		ERXMigrationTable eriDirectoryTable = database.newTableNamed("ERIDirectory");
		eriDirectoryTable.newIntegerColumn("id", NOT_NULL);
		eriDirectoryTable.newStringColumn("name", 100, NOT_NULL);
		eriDirectoryTable.create();
		eriDirectoryTable.setPrimaryKey("id");

		ERXMigrationTable eriFileTable = database.newTableNamed("ERIFile");
		eriFileTable.newIntegerColumn("contentId", NOT_NULL);
		eriFileTable.newIntegerColumn("directoryId", NOT_NULL);
		eriFileTable.newIntegerColumn("id", NOT_NULL);
		eriFileTable.newTimestampColumn("lastModified", NOT_NULL);
		eriFileTable.newBigIntegerColumn("length", NOT_NULL);
		eriFileTable.newStringColumn("name", 100, NOT_NULL);
		eriFileTable.create();
		eriFileTable.setPrimaryKey("id");

		ERXMigrationTable eriFileContentTable = database.newTableNamed("ERIFileContent");
		eriFileContentTable.newBlobColumn("content", NOT_NULL);
		eriFileContentTable.newIntegerColumn("id", NOT_NULL);
		eriFileContentTable.create();
		eriFileContentTable.setPrimaryKey("id");

		ERXMigrationTable eriValidationRuleTable = database.newTableNamed("ERIValidationRule");
		eriValidationRuleTable.newStringColumn("definition", 1000, NOT_NULL);
		eriValidationRuleTable.newIntegerColumn("id", NOT_NULL);
		eriValidationRuleTable.newStringColumn("name", 100, NOT_NULL);
		eriValidationRuleTable.create();
		eriValidationRuleTable.setPrimaryKey("id");

		eriAttributeTable.addForeignKey("attributeGroupId", "ERIAttributeGroup", "id");
		eriAttributeTable.addForeignKey("attributeTypeId", "ERIAttributeType", "id");

		eriAttributeGroupTable.addForeignKey("parentId", "ERIAttributeGroup", "id");

		eriAttributeValidationRuleTable.addForeignKey("attributeId", "ERIAttribute", "id");
		eriAttributeValidationRuleTable.addForeignKey("validationRuleId", "ERIValidationRule", "id");

		eriFileTable.addForeignKey("contentId", "ERIFileContent", "id");
		eriFileTable.addForeignKey("directoryId", "ERIDirectory", "id");
	}
}

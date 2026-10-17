package er.attachment.model;

import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationFailedException;
import er.extensions.migration.ERXMigrationTable;

/**
 * Creates the tables of the ERAttachment model.
 */
public class ERAttachment0 extends ERXMigrationDatabase.Migration {
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		throw new ERXMigrationFailedException("ERAttachment0 can't be undone.");
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable erAttachmentTable = database.newTableNamed("ERAttachment");
		erAttachmentTable.newBooleanColumn("available", NOT_NULL);
		erAttachmentTable.newStringColumn("configurationName", 100, ALLOWS_NULL);
		erAttachmentTable.newTimestampColumn("creationDate", NOT_NULL);
		erAttachmentTable.newIntegerColumn("height", ALLOWS_NULL);
		erAttachmentTable.newIntegerColumn("id", NOT_NULL);
		erAttachmentTable.newStringColumn("mimeType", 100, NOT_NULL);
		erAttachmentTable.newStringColumn("originalFileName", 255, NOT_NULL);
		erAttachmentTable.newStringColumn("ownerID", 16, ALLOWS_NULL);
		erAttachmentTable.newIntegerColumn("parentID", ALLOWS_NULL);
		erAttachmentTable.newBooleanColumn("proxied", NOT_NULL);
		erAttachmentTable.newIntegerColumn("size", NOT_NULL);
		erAttachmentTable.newStringColumn("storageType", 10, ALLOWS_NULL);
		erAttachmentTable.newStringColumn("thumbnail", 10, ALLOWS_NULL);
		erAttachmentTable.newStringColumn("webPath", 1000, NOT_NULL);
		erAttachmentTable.newIntegerColumn("width", ALLOWS_NULL);
		erAttachmentTable.newStringColumn("cfPath", 1000, ALLOWS_NULL);
		erAttachmentTable.newIntegerColumn("attachmentDataID", ALLOWS_NULL);
		erAttachmentTable.newBlobColumn("smallData", ALLOWS_NULL);
		erAttachmentTable.newStringColumn("filesystemPath", 255, ALLOWS_NULL);
		erAttachmentTable.newStringColumn("s3Path", 1000, ALLOWS_NULL);
		erAttachmentTable.create();
		erAttachmentTable.setPrimaryKey("id");

		ERXMigrationTable erAttachmentDataTable = database.newTableNamed("ERAttachmentData");
		erAttachmentDataTable.newBlobColumn("data", ALLOWS_NULL);
		erAttachmentDataTable.newIntegerColumn("id", NOT_NULL);
		erAttachmentDataTable.create();
		erAttachmentDataTable.setPrimaryKey("id");

		erAttachmentTable.addForeignKey("parentID", "ERAttachment", "id");
		erAttachmentTable.addForeignKey("attachmentDataID", "ERAttachmentData", "id");
	}
}

package er.corebusinesslogic;

import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationFailedException;
import er.extensions.migration.ERXMigrationTable;

/**
 * Creates the tables of the ERMail model.
 */
public class ERMail0 extends ERXMigrationDatabase.Migration {
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		throw new ERXMigrationFailedException("ERMail0 can't be undone.");
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable ercMailMessageTable = database.newTableNamed("ERCMAIL_MESSAG");
		ercMailMessageTable.newStringColumn("BCC_ADDR", 1000, ALLOWS_NULL);
		ercMailMessageTable.newStringColumn("CC_ADDR", 1000, ALLOWS_NULL);
		ercMailMessageTable.newIntBooleanColumn("CONTENT_GZIPPED", NOT_NULL);
		ercMailMessageTable.newTimestampColumn("CREATED", NOT_NULL);
		ercMailMessageTable.newTimestampColumn("DATE_SENT", ALLOWS_NULL);
		ercMailMessageTable.newStringColumn("EXCEPTION_REASON", 1000, ALLOWS_NULL);
		ercMailMessageTable.newStringColumn("FROM_ADDR", 255, NOT_NULL);
		ercMailMessageTable.newIntegerColumn("ID", NOT_NULL);
		ercMailMessageTable.newIntBooleanColumn("IS_READ", NOT_NULL);
		ercMailMessageTable.newTimestampColumn("LAST_MODIFIED", NOT_NULL);
		ercMailMessageTable.newLargeStringColumn("PLAIN_TEXT_", ALLOWS_NULL);
		ercMailMessageTable.newBlobColumn("plain_text_compressed", ALLOWS_NULL);
		ercMailMessageTable.newStringColumn("REPLY_TO_ADDR", 1000, ALLOWS_NULL);
		ercMailMessageTable.newIntBooleanColumn("SHOULD_ARCHIVE_SENT_MAIL", NOT_NULL);
		ercMailMessageTable.newStringColumn("MAIL_STATE_ID", 4, NOT_NULL);
		ercMailMessageTable.newLargeStringColumn("TEXT_", ALLOWS_NULL);
		ercMailMessageTable.newBlobColumn("TEXT_COMPRESSED", ALLOWS_NULL);
		ercMailMessageTable.newStringColumn("TITLE", 255, NOT_NULL);
		ercMailMessageTable.newStringColumn("TO_ADDR", 1000, NOT_NULL);
		ercMailMessageTable.newStringColumn("X_MAILER", 255, ALLOWS_NULL);
		ercMailMessageTable.create();
		ercMailMessageTable.setPrimaryKey("ID");

		ERXMigrationTable ercMailMessageArchiveTable = database.newTableNamed("ERCMAIL_MESSAG_ARCHIVE");
		ercMailMessageArchiveTable.newStringColumn("BCC_ADDR", 1000, ALLOWS_NULL);
		ercMailMessageArchiveTable.newStringColumn("CC_ADDR", 1000, ALLOWS_NULL);
		ercMailMessageArchiveTable.newIntBooleanColumn("CONTENT_GZIPPED", NOT_NULL);
		ercMailMessageArchiveTable.newTimestampColumn("CREATED", NOT_NULL);
		ercMailMessageArchiveTable.newTimestampColumn("DATE_SENT", ALLOWS_NULL);
		ercMailMessageArchiveTable.newStringColumn("EXCEPTION_REASON", 1000, NOT_NULL);
		ercMailMessageArchiveTable.newStringColumn("FROM_ADDR", 255, NOT_NULL);
		ercMailMessageArchiveTable.newIntegerColumn("ID", NOT_NULL);
		ercMailMessageArchiveTable.newIntBooleanColumn("IS_READ", NOT_NULL);
		ercMailMessageArchiveTable.newTimestampColumn("LAST_MODIFIED", NOT_NULL);
		ercMailMessageArchiveTable.newLargeStringColumn("PLAIN_TEXT_", ALLOWS_NULL);
		ercMailMessageArchiveTable.newBlobColumn("plain_text_compressed", ALLOWS_NULL);
		ercMailMessageArchiveTable.newStringColumn("REPLY_TO_ADDR", 1000, ALLOWS_NULL);
		ercMailMessageArchiveTable.newIntBooleanColumn("SHOULD_ARCHIVE_SENT_MAIL", NOT_NULL);
		ercMailMessageArchiveTable.newStringColumn("MAIL_STATE_ID", 4, NOT_NULL);
		ercMailMessageArchiveTable.newLargeStringColumn("TEXT_", NOT_NULL);
		ercMailMessageArchiveTable.newBlobColumn("TEXT_COMPRESSED", ALLOWS_NULL);
		ercMailMessageArchiveTable.newStringColumn("TITLE", 255, NOT_NULL);
		ercMailMessageArchiveTable.newStringColumn("TO_ADDR", 1000, NOT_NULL);
		ercMailMessageArchiveTable.newStringColumn("X_MAILER", 255, ALLOWS_NULL);
		ercMailMessageArchiveTable.create();
		ercMailMessageArchiveTable.setPrimaryKey("ID");

		ERXMigrationTable ercMessageAttachmentTable = database.newTableNamed("ERCMESSAG_ATTACH");
		ercMessageAttachmentTable.newStringColumn("FILE_PATH", 1000, NOT_NULL);
		ercMessageAttachmentTable.newIntegerColumn("ID", NOT_NULL);
		ercMessageAttachmentTable.newIntegerColumn("MAIL_MESSAG_ID", NOT_NULL);
		ercMessageAttachmentTable.newStringColumn("MIME_TYPE", 255, NOT_NULL);
		ercMessageAttachmentTable.create();
		ercMessageAttachmentTable.setPrimaryKey("ID");

		ercMessageAttachmentTable.addForeignKey("MAIL_MESSAG_ID", "ERCMAIL_MESSAG", "ID");
	}
}

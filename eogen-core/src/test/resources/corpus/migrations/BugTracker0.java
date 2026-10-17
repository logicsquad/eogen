package er.bugtracker;

import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationFailedException;
import er.extensions.migration.ERXMigrationTable;

/**
 * Creates the tables of the BugTracker model.
 */
public class BugTracker0 extends ERXMigrationDatabase.Migration {
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		throw new ERXMigrationFailedException("BugTracker0 can't be undone.");
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable bugTable = database.newTableNamed("BUG");
		bugTable.newIntegerColumn("COMP_ID", NOT_NULL);
		bugTable.newTimestampColumn("DATE_MODIFIED", ALLOWS_NULL);
		bugTable.newTimestampColumn("DATE_SUBMITTED", ALLOWS_NULL);
		bugTable.newIntegerColumn("ID", NOT_NULL);
		bugTable.newIntBooleanColumn("IS_FEATURE_REQUEST", NOT_NULL);
		bugTable.newIntBooleanColumn("IS_READ", NOT_NULL);
		bugTable.newIntegerColumn("ORIGINATOR_ID", NOT_NULL);
		bugTable.newIntegerColumn("PEOPLE_ID", NOT_NULL);
		bugTable.newIntegerColumn("PREVIOUS_OWNER_ID", ALLOWS_NULL);
		bugTable.newStringColumn("PR_ID", 4, NOT_NULL);
		bugTable.newIntegerColumn("RELEASE_ID", NOT_NULL);
		bugTable.newStringColumn("STATE_ID", 4, NOT_NULL);
		bugTable.newStringColumn("SUBJECT", 50, NOT_NULL);
		bugTable.newStringColumn("type", 1, ALLOWS_NULL);
		bugTable.newIntegerColumn("DIFFICULTY_ID", ALLOWS_NULL);
		bugTable.newIntegerColumn("SUB_TYPE_ID", ALLOWS_NULL);
		bugTable.newIntegerColumn("REQ_TYPE_ID", ALLOWS_NULL);
		bugTable.create();
		bugTable.setPrimaryKey("ID");

		ERXMigrationTable xBugTestItemTable = database.newTableNamed("BUG_TEST_ITEM");
		xBugTestItemTable.newIntegerColumn("BUG_ID", NOT_NULL);
		xBugTestItemTable.newIntegerColumn("ID", NOT_NULL);
		xBugTestItemTable.create();
		xBugTestItemTable.setPrimaryKey("BUG_ID", "ID");

		ERXMigrationTable commentTable = database.newTableNamed("Comment");
		commentTable.newIntegerColumn("BUG_ID", NOT_NULL);
		commentTable.newTimestampColumn("DATE_SUBMITTED", ALLOWS_NULL);
		commentTable.newIntegerColumn("id", NOT_NULL);
		commentTable.newIntegerColumn("ORIGINATOR_ID", NOT_NULL);
		commentTable.newIntegerColumn("PARENT_ID", ALLOWS_NULL);
		commentTable.newLargeStringColumn("DESCRIPTION", ALLOWS_NULL);
		commentTable.create();
		commentTable.setPrimaryKey("id");

		ERXMigrationTable componentTable = database.newTableNamed("COMPONENT");
		componentTable.newIntegerColumn("ID", NOT_NULL);
		componentTable.newIntegerColumn("PEOPLE_ID", NOT_NULL);
		componentTable.newIntegerColumn("PARENT_ID", ALLOWS_NULL);
		componentTable.newStringColumn("DESCRIPTION", 1000, NOT_NULL);
		componentTable.create();
		componentTable.setPrimaryKey("ID");

		ERXMigrationTable difficultyTable = database.newTableNamed("DIFFICULTY");
		difficultyTable.newStringColumn("DESCRIPTION", 50, NOT_NULL);
		difficultyTable.newIntegerColumn("ID", NOT_NULL);
		difficultyTable.create();
		difficultyTable.setPrimaryKey("ID");

		ERXMigrationTable frameworkTable = database.newTableNamed("FRAMEWORK");
		frameworkTable.newIntegerColumn("ID", NOT_NULL);
		frameworkTable.newStringColumn("NAME", 50, NOT_NULL);
		frameworkTable.newIntegerColumn("ORDERING", NOT_NULL);
		frameworkTable.newTimestampColumn("OWNED_SINCE", ALLOWS_NULL);
		frameworkTable.newIntegerColumn("USER_ID", ALLOWS_NULL);
		frameworkTable.create();
		frameworkTable.setPrimaryKey("ID");

		ERXMigrationTable peopleTable = database.newTableNamed("PEOPLE");
		peopleTable.newStringColumn("EMAIL", 50, ALLOWS_NULL);
		peopleTable.newIntegerColumn("ID", NOT_NULL);
		peopleTable.newIntBooleanColumn("IS_ACTIVE", NOT_NULL);
		peopleTable.newIntBooleanColumn("IS_ADMIN", NOT_NULL);
		peopleTable.newIntBooleanColumn("IS_CUSTOMER_SERVICE", NOT_NULL);
		peopleTable.newIntBooleanColumn("IS_ENGINEERING", NOT_NULL);
		peopleTable.newStringColumn("LOGIN", 16, NOT_NULL);
		peopleTable.newStringColumn("NAME", 50, ALLOWS_NULL);
		peopleTable.newStringColumn("PASSWORD", 16, NOT_NULL);
		peopleTable.newStringColumn("TEAM", 16, ALLOWS_NULL);
		peopleTable.create();
		peopleTable.setPrimaryKey("ID");

		ERXMigrationTable priorityTable = database.newTableNamed("PRIORITY");
		priorityTable.newStringColumn("ID", 4, NOT_NULL);
		priorityTable.newIntegerColumn("SORT_ORDER", NOT_NULL);
		priorityTable.newStringColumn("DESCRIPTION", 50, NOT_NULL);
		priorityTable.create();
		priorityTable.setPrimaryKey("ID");

		ERXMigrationTable releaseTable = database.newTableNamed("RELEASE");
		releaseTable.newTimestampColumn("DATE_DUE", NOT_NULL);
		releaseTable.newIntegerColumn("ID", NOT_NULL);
		releaseTable.newIntBooleanColumn("IS_OPEN", NOT_NULL);
		releaseTable.newStringColumn("NAME", 50, NOT_NULL);
		releaseTable.create();
		releaseTable.setPrimaryKey("ID");

		ERXMigrationTable requirementSubTypeTable = database.newTableNamed("REQ_SUB_TYPE");
		requirementSubTypeTable.newIntegerColumn("REQ_SUB_TYPE_ID", NOT_NULL);
		requirementSubTypeTable.newStringColumn("SUB_TYPE_DESC", 50, NOT_NULL);
		requirementSubTypeTable.create();
		requirementSubTypeTable.setPrimaryKey("REQ_SUB_TYPE_ID");

		ERXMigrationTable xRequirementTestItemTable = database.newTableNamed("REQ_TEST_ITEM");
		xRequirementTestItemTable.newIntegerColumn("BUG_ID", NOT_NULL);
		xRequirementTestItemTable.newIntegerColumn("ID", NOT_NULL);
		xRequirementTestItemTable.create();
		xRequirementTestItemTable.setPrimaryKey("BUG_ID", "ID");

		ERXMigrationTable requirementTypeTable = database.newTableNamed("REQ_TYPE");
		requirementTypeTable.newIntegerColumn("REQ_TYPE_ID", NOT_NULL);
		requirementTypeTable.newStringColumn("TYPE_DESCRIPTION", 50, NOT_NULL);
		requirementTypeTable.create();
		requirementTypeTable.setPrimaryKey("REQ_TYPE_ID");

		ERXMigrationTable testItemTable = database.newTableNamed("TEST_ITEM");
		testItemTable.newLargeStringColumn("COMMENTS", ALLOWS_NULL);
		testItemTable.newIntegerColumn("MODULE_ID", NOT_NULL);
		testItemTable.newStringColumn("CONTROLLED", 50, NOT_NULL);
		testItemTable.newTimestampColumn("DATE_CREATED", NOT_NULL);
		testItemTable.newIntegerColumn("ID", NOT_NULL);
		testItemTable.newStringColumn("STATE_ID", 4, NOT_NULL);
		testItemTable.newIntegerColumn("TESTED_BY_ID", ALLOWS_NULL);
		testItemTable.newLargeStringColumn("DESCRIPTION", ALLOWS_NULL);
		testItemTable.newStringColumn("TITLE", 100, NOT_NULL);
		testItemTable.create();
		testItemTable.setPrimaryKey("ID");

		ERXMigrationTable testItemStateTable = database.newTableNamed("TEST_ITEM_STATE");
		testItemStateTable.newStringColumn("NAME", 50, NOT_NULL);
		testItemStateTable.newStringColumn("ID", 4, NOT_NULL);
		testItemStateTable.newIntegerColumn("SORT_ORDER", NOT_NULL);
		testItemStateTable.create();
		testItemStateTable.setPrimaryKey("ID");

		ERXMigrationTable xCommentAttachmentTable = database.newTableNamed("XCommentAttachment");
		xCommentAttachmentTable.newIntegerColumn("commentId", NOT_NULL);
		xCommentAttachmentTable.newIntegerColumn("eRAttachmentId", NOT_NULL);
		xCommentAttachmentTable.create();
		xCommentAttachmentTable.setPrimaryKey("commentId", "eRAttachmentId");

		bugTable.addForeignKey("COMP_ID", "COMPONENT", "ID");
		bugTable.addForeignKey("ORIGINATOR_ID", "PEOPLE", "ID");
		bugTable.addForeignKey("PEOPLE_ID", "PEOPLE", "ID");
		bugTable.addForeignKey("PREVIOUS_OWNER_ID", "PEOPLE", "ID");
		bugTable.addForeignKey("PR_ID", "PRIORITY", "ID");
		bugTable.addForeignKey("RELEASE_ID", "RELEASE", "ID");
		bugTable.addForeignKey("DIFFICULTY_ID", "DIFFICULTY", "ID");
		bugTable.addForeignKey("SUB_TYPE_ID", "REQ_SUB_TYPE", "REQ_SUB_TYPE_ID");
		bugTable.addForeignKey("REQ_TYPE_ID", "REQ_TYPE", "REQ_TYPE_ID");

		xBugTestItemTable.addForeignKey("BUG_ID", "BUG", "ID");
		xBugTestItemTable.addForeignKey("ID", "TEST_ITEM", "ID");

		commentTable.addForeignKey("BUG_ID", "BUG", "ID");
		commentTable.addForeignKey("ORIGINATOR_ID", "PEOPLE", "ID");
		commentTable.addForeignKey("PARENT_ID", "Comment", "id");

		componentTable.addForeignKey("PEOPLE_ID", "PEOPLE", "ID");
		componentTable.addForeignKey("PARENT_ID", "COMPONENT", "ID");

		frameworkTable.addForeignKey("USER_ID", "PEOPLE", "ID");

		xRequirementTestItemTable.addForeignKey("BUG_ID", "BUG", "ID");
		xRequirementTestItemTable.addForeignKey("ID", "TEST_ITEM", "ID");

		testItemTable.addForeignKey("MODULE_ID", "COMPONENT", "ID");
		testItemTable.addForeignKey("TESTED_BY_ID", "PEOPLE", "ID");
		testItemTable.addForeignKey("STATE_ID", "TEST_ITEM_STATE", "ID");

		xCommentAttachmentTable.addForeignKey("commentId", "Comment", "id");
		xCommentAttachmentTable.addForeignKey("eRAttachmentId", "ERAttachment", "id");
	}
}

package com.example.ledger;

import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationFailedException;
import er.extensions.migration.ERXMigrationTable;

/**
 * Creates the tables of the Ledger model.
 */
public class Ledger0 extends ERXMigrationDatabase.Migration {
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		throw new ERXMigrationFailedException("Ledger0 can't be undone.");
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable accountTable = database.newTableNamed("account");
		accountTable.newIntegerColumn("id", NOT_NULL);
		accountTable.newStringColumn("name", 100, NOT_NULL);
		accountTable.newStringColumn("number", 16, NOT_NULL);
		accountTable.newDateColumn("opened_on", ALLOWS_NULL);
		accountTable.create();
		accountTable.setPrimaryKey("id");
		accountTable.addUniqueIndex("account_number", "number");

		ERXMigrationTable postingTable = database.newTableNamed("posting");
		postingTable.newIntegerColumn("account_id", NOT_NULL);
		postingTable.newBigDecimalColumn("amount", 38, 4, NOT_NULL);
		postingTable.newIntegerColumn("id", NOT_NULL);
		postingTable.newTimestampColumn("posted_at", NOT_NULL);
		postingTable.create();
		postingTable.setPrimaryKey("id");

		postingTable.addForeignKey("account_id", "account", "id");
	}
}

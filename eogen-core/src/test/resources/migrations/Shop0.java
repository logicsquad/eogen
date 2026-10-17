package com.example.shop;

import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationFailedException;
import er.extensions.migration.ERXMigrationTable;

/**
 * Creates the tables of the Shop model.
 */
public class Shop0 extends ERXMigrationDatabase.Migration {
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		throw new ERXMigrationFailedException("Shop0 can't be undone.");
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable orderLineTable = database.newTableNamed("order_line");
		orderLineTable.newIntegerColumn("id", NOT_NULL);
		orderLineTable.newIntegerColumn("order_id", NOT_NULL);
		orderLineTable.newBigDecimalColumn("price", 12, 2, NOT_NULL);
		orderLineTable.newIntegerColumn("product_id", NOT_NULL);
		orderLineTable.newIntegerColumn("quantity", NOT_NULL);
		orderLineTable.create();
		orderLineTable.setPrimaryKey("id");

		ERXMigrationTable partyTable = database.newTableNamed("party");
		partyTable.newStringColumn("email", 100, NOT_NULL);
		partyTable.newIntegerColumn("id", NOT_NULL);
		partyTable.newStringColumn("name", 100, NOT_NULL);
		partyTable.newStringColumn("status", 50, NOT_NULL);
		partyTable.newStringColumn("type", 100, NOT_NULL);
		partyTable.newStringColumn("tax_number", 100, ALLOWS_NULL);
		partyTable.newTimestampColumn("date_of_birth", ALLOWS_NULL);
		partyTable.create();
		partyTable.setPrimaryKey("id");

		ERXMigrationTable productTable = database.newTableNamed("product");
		productTable.newBooleanColumn("active", NOT_NULL);
		productTable.newIntegerColumn("category", NOT_NULL);
		productTable.newIntegerColumn("id", NOT_NULL);
		productTable.newStringColumn("name", 100, NOT_NULL);
		productTable.newBigDecimalColumn("price", 12, 2, NOT_NULL);
		productTable.create();
		productTable.setPrimaryKey("id");

		ERXMigrationTable productTagTable = database.newTableNamed("product_tag");
		productTagTable.newIntegerColumn("product_id", NOT_NULL);
		productTagTable.newIntegerColumn("tag_id", NOT_NULL);
		productTagTable.create();
		productTagTable.setPrimaryKey("product_id", "tag_id");

		ERXMigrationTable orderTable = database.newTableNamed("shop_order");
		orderTable.newStringColumn("currency_code", 100, NOT_NULL);
		orderTable.newIntegerColumn("customer_id", NOT_NULL);
		orderTable.newIntegerColumn("id", NOT_NULL);
		orderTable.newLargeStringColumn("note", NOT_NULL);
		orderTable.newTimestampColumn("placed", NOT_NULL);
		orderTable.newBigDecimalColumn("total", 12, 2, NOT_NULL);
		orderTable.create();
		orderTable.setPrimaryKey("id");

		ERXMigrationTable tagTable = database.newTableNamed("tag");
		tagTable.newIntegerColumn("id", NOT_NULL);
		tagTable.newStringColumn("name", 100, NOT_NULL);
		tagTable.create();
		tagTable.setPrimaryKey("id");

		orderLineTable.addForeignKey("order_id", "shop_order", "id");
		orderLineTable.addForeignKey("product_id", "product", "id");

		productTagTable.addForeignKey("product_id", "product", "id");
		productTagTable.addForeignKey("tag_id", "tag", "id");

		orderTable.addForeignKey("currency_code", "currency", "code");
		orderTable.addForeignKey("customer_id", "party", "id");
	}
}

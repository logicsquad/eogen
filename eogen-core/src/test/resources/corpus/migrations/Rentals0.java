package webobjectsexamples.businesslogic.rentals.common;

import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationFailedException;
import er.extensions.migration.ERXMigrationTable;

import java.sql.Types;

/**
 * Creates the tables of the Rentals model.
 */
public class Rentals0 extends ERXMigrationDatabase.Migration {
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		throw new ERXMigrationFailedException("Rentals0 can't be undone.");
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable creditCardTable = database.newTableNamed("creditcard");
		creditCardTable.newTimestampColumn("AUTHORIZATION_DATE", NOT_NULL);
		creditCardTable.newStringColumn("AUTHORIZATION_NUM", 10, NOT_NULL);
		creditCardTable.newStringColumn("CARD_NUMBER", 25, NOT_NULL);
		creditCardTable.newIntegerColumn("CUSTOMER_ID", NOT_NULL);
		creditCardTable.newTimestampColumn("EXPIRATION_DATE", NOT_NULL);
		creditCardTable.newBigDecimalColumn("CLIMIT", 10, 2, NOT_NULL);
		creditCardTable.create();
		creditCardTable.setPrimaryKey("CUSTOMER_ID");

		ERXMigrationTable customerTable = database.newTableNamed("customer");
		customerTable.newStringColumn("CITY", 20, NOT_NULL);
		customerTable.newIntegerColumn("CUSTOMER_ID", NOT_NULL);
		customerTable.newStringColumn("FIRST_NAME", 20, NOT_NULL);
		customerTable.newStringColumn("LAST_NAME", 30, NOT_NULL);
		customerTable.newTimestampColumn("MEMBER_SINCE", ALLOWS_NULL);
		customerTable.newStringColumn("PHONE", 10, ALLOWS_NULL);
		customerTable.newStringColumn("STATE", 2, ALLOWS_NULL);
		customerTable.newStringColumn("STREET_ADDRESS", 50, ALLOWS_NULL);
		customerTable.newStringColumn("ZIP", 10, ALLOWS_NULL);
		customerTable.create();
		customerTable.setPrimaryKey("CUSTOMER_ID");

		ERXMigrationTable feeTable = database.newTableNamed("fee");
		feeTable.newBigDecimalColumn("AMOUNT", 10, 2, NOT_NULL);
		feeTable.newTimestampColumn("DATE_PAID", ALLOWS_NULL);
		feeTable.newIntegerColumn("FEE_ID", NOT_NULL);
		feeTable.newIntegerColumn("FEE_TYPE_ID", NOT_NULL);
		feeTable.newIntegerColumn("RENTAL_ID", NOT_NULL);
		feeTable.create();
		feeTable.setPrimaryKey("FEE_ID");

		ERXMigrationTable feeTypeTable = database.newTableNamed("feetype");
		feeTypeTable.newIntegerColumn("ENABLED", NOT_NULL);
		feeTypeTable.newStringColumn("FEE_TYPE", 15, NOT_NULL);
		feeTypeTable.newIntegerColumn("FEE_TYPE_ID", NOT_NULL);
		feeTypeTable.newColumn("ORDER_BY", Types.INTEGER, 0, 9, 0, NOT_NULL, null);
		feeTypeTable.create();
		feeTypeTable.setPrimaryKey("FEE_TYPE_ID");

		ERXMigrationTable rentalTable = database.newTableNamed("rental");
		rentalTable.newIntegerColumn("CUSTOMER_ID", NOT_NULL);
		rentalTable.newTimestampColumn("DATE_OUT", NOT_NULL);
		rentalTable.newTimestampColumn("DATE_RETURNED", ALLOWS_NULL);
		rentalTable.newIntegerColumn("RENTAL_ID", NOT_NULL);
		rentalTable.newIntegerColumn("UNIT_ID", NOT_NULL);
		rentalTable.create();
		rentalTable.setPrimaryKey("RENTAL_ID");

		ERXMigrationTable userTable = database.newTableNamed("rentalsuser");
		userTable.newIntegerColumn("ACCESS_LEVEL", NOT_NULL);
		userTable.newIntegerColumn("CUSTOMER_ID", ALLOWS_NULL);
		userTable.newStringColumn("PASSWORD", 20, NOT_NULL);
		userTable.newIntegerColumn("USER_ID", NOT_NULL);
		userTable.newStringColumn("LOGIN", 20, NOT_NULL);
		userTable.create();
		userTable.setPrimaryKey("USER_ID");

		ERXMigrationTable rentalTermsTable = database.newTableNamed("rentalterms");
		rentalTermsTable.newIntegerColumn("CHECK_OUT_LENGTH", NOT_NULL);
		rentalTermsTable.newBigDecimalColumn("COST", 10, 2, NOT_NULL);
		rentalTermsTable.newBigDecimalColumn("DEPOSIT_AMOUNT", 10, 2, NOT_NULL);
		rentalTermsTable.newStringColumn("NAME", 20, NOT_NULL);
		rentalTermsTable.newIntegerColumn("RENTAL_TERMS_ID", NOT_NULL);
		rentalTermsTable.create();
		rentalTermsTable.setPrimaryKey("RENTAL_TERMS_ID");

		ERXMigrationTable unitTable = database.newTableNamed("unit");
		unitTable.newTimestampColumn("DATE_ACQUIRED", NOT_NULL);
		unitTable.newStringColumn("NOTES", 100, ALLOWS_NULL);
		unitTable.newIntegerColumn("UNIT_ID", NOT_NULL);
		unitTable.newIntegerColumn("VIDEO_ID", NOT_NULL);
		unitTable.create();
		unitTable.setPrimaryKey("UNIT_ID");

		ERXMigrationTable videoTable = database.newTableNamed("video");
		videoTable.newIntegerColumn("MOVIE_ID", NOT_NULL);
		videoTable.newIntegerColumn("RENTAL_TERMS_ID", NOT_NULL);
		videoTable.newIntegerColumn("VIDEO_ID", NOT_NULL);
		videoTable.create();
		videoTable.setPrimaryKey("VIDEO_ID");

		creditCardTable.addForeignKey("CUSTOMER_ID", "customer", "CUSTOMER_ID");

		feeTable.addForeignKey("FEE_TYPE_ID", "feetype", "FEE_TYPE_ID");
		feeTable.addForeignKey("RENTAL_ID", "rental", "RENTAL_ID");

		rentalTable.addForeignKey("CUSTOMER_ID", "customer", "CUSTOMER_ID");
		rentalTable.addForeignKey("UNIT_ID", "unit", "UNIT_ID");

		userTable.addForeignKey("CUSTOMER_ID", "customer", "CUSTOMER_ID");

		unitTable.addForeignKey("VIDEO_ID", "video", "VIDEO_ID");

		videoTable.addForeignKey("MOVIE_ID", "movie", "MOVIE_ID");
		videoTable.addForeignKey("RENTAL_TERMS_ID", "rentalterms", "RENTAL_TERMS_ID");
	}
}

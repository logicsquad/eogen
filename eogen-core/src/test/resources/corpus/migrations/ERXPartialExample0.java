package er.example.erxpartials.model;

import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationFailedException;
import er.extensions.migration.ERXMigrationTable;

/**
 * Creates the tables of the ERXPartialExample model.
 */
public class ERXPartialExample0 extends ERXMigrationDatabase.Migration {
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		throw new ERXMigrationFailedException("ERXPartialExample0 can't be undone.");
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable personTable = database.existingTableNamed("Person");
		personTable.newTimestampColumn("lastLoginDate", ALLOWS_NULL);
		personTable.newStringColumn("password", 255, ALLOWS_NULL);
		personTable.newStringColumn("username", 255, ALLOWS_NULL);
		personTable.newIntegerColumn("departmentID", ALLOWS_NULL);
		personTable.newStringColumn("employeeNumber", 255, ALLOWS_NULL);
		personTable.newIntegerColumn("employeeTypeID", ALLOWS_NULL);
		personTable.newBigDecimalColumn("salary", 38, 2, ALLOWS_NULL);

		ERXMigrationTable companyTable = database.newTableNamed("Company");
		companyTable.newStringColumn("address1", 255, ALLOWS_NULL);
		companyTable.newStringColumn("address2", 255, ALLOWS_NULL);
		companyTable.newStringColumn("city", 255, ALLOWS_NULL);
		companyTable.newIntegerColumn("id", NOT_NULL);
		companyTable.newStringColumn("name", 255, NOT_NULL);
		companyTable.newStringColumn("state", 255, ALLOWS_NULL);
		companyTable.newStringColumn("zipcode", 255, ALLOWS_NULL);
		companyTable.create();
		companyTable.setPrimaryKey("id");

		ERXMigrationTable departmentTable = database.newTableNamed("Department");
		departmentTable.newIntegerColumn("companyID", NOT_NULL);
		departmentTable.newStringColumn("departmentCode", 255, NOT_NULL);
		departmentTable.newIntegerColumn("id", NOT_NULL);
		departmentTable.newStringColumn("name", 255, NOT_NULL);
		departmentTable.create();
		departmentTable.setPrimaryKey("id");

		ERXMigrationTable employeeTypeTable = database.newTableNamed("EmployeeType");
		employeeTypeTable.newIntegerColumn("id", NOT_NULL);
		employeeTypeTable.newStringColumn("name", 255, NOT_NULL);
		employeeTypeTable.create();
		employeeTypeTable.setPrimaryKey("id");

		departmentTable.addForeignKey("companyID", "Company", "id");

		personTable.addForeignKey("departmentID", "Department", "id");
		personTable.addForeignKey("employeeTypeID", "EmployeeType", "id");
	}
}

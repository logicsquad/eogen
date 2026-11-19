package com.webobjects.jdbcadaptor;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eoaccess.EOSynchronizationFactory;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * The primary key support of Wonder's PostgreSQL plugin, as of Wonder 7.4: a
 * sequence named after the table, which it makes only for a primary key of
 * one column, but drops whatever the primary key is, with no IF EXISTS.
 */
public class PostgresqlSynchronizationFactory extends EOSynchronizationFactory {
	@Override
	public NSArray<EOSQLExpression> primaryKeySupportStatementsForEntityGroup(NSArray<EOEntity> entityGroup) {
		NSMutableArray<EOSQLExpression> results = new NSMutableArray<EOSQLExpression>();
		for (EOEntity entity : entityGroup) {
			if (entity.primaryKeyAttributes().count() == 1) {
				String sequenceName = entity.primaryKeyRootName() + "_seq";
				results.addObject(_expressionForString("CREATE SEQUENCE " + sequenceName));
				results.addObject(_expressionForString("ALTER TABLE " + entity.externalName() + " ALTER COLUMN " + entity.primaryKeyAttributes().objectAtIndex(0).columnName() + " SET DEFAULT nextval( '"
						+ sequenceName + "' )"));
			}
		}
		return results;
	}

	@Override
	public NSArray<EOSQLExpression> dropPrimaryKeySupportStatementsForEntityGroup(NSArray<EOEntity> entityGroup) {
		if (entityGroup == null) {
			return new NSArray<EOSQLExpression>();
		}
		NSMutableArray<EOSQLExpression> results = new NSMutableArray<EOSQLExpression>();
		for (EOEntity entity : entityGroup) {
			results.addObject(_expressionForString("DROP SEQUENCE " + entity.primaryKeyRootName() + "_seq CASCADE"));
		}
		return results;
	}
}

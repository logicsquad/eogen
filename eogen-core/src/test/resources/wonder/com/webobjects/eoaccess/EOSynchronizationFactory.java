package com.webobjects.eoaccess;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * The statements of a database's plugin that has none of its own, as
 * WebObjects 5.4.3 has them: in particular, none for primary key support,
 * for which it returns null.
 */
public class EOSynchronizationFactory {
	public NSArray<EOSQLExpression> createTableStatementsForEntityGroup(NSArray<EOEntity> entityGroup) {
		EOEntity entity = entityGroup.objectAtIndex(0);
		return new NSArray<EOSQLExpression>(_expressionForString("CREATE TABLE " + entity.externalName() + " (" + _columns(entity.attributes()) + ")"));
	}

	public NSArray<EOSQLExpression> dropTableStatementsForEntityGroup(NSArray<EOEntity> entityGroup) {
		return new NSArray<EOSQLExpression>(_expressionForString("DROP TABLE " + entityGroup.objectAtIndex(0).externalName() + " CASCADE"));
	}

	public NSArray<EOSQLExpression> primaryKeyConstraintStatementsForEntityGroup(NSArray<EOEntity> entityGroup) {
		EOEntity entity = entityGroup.objectAtIndex(0);
		return new NSArray<EOSQLExpression>(_expressionForString("ALTER TABLE " + entity.externalName() + " ADD PRIMARY KEY (" + _columns(entity.primaryKeyAttributes()) + ")"));
	}

	public NSArray<EOSQLExpression> primaryKeySupportStatementsForEntityGroup(NSArray<EOEntity> entityGroup) {
		return null;
	}

	public NSArray<EOSQLExpression> dropPrimaryKeySupportStatementsForEntityGroup(NSArray<EOEntity> entityGroup) {
		return null;
	}

	public EOSQLExpression _expressionForString(String statement) {
		EOSQLExpression expression = new EOSQLExpression(null);
		expression.setStatement(statement);
		return expression;
	}

	private static String _columns(NSArray<EOAttribute> attributes) {
		NSMutableArray<String> names = new NSMutableArray<String>();
		for (EOAttribute attribute : attributes) {
			names.addObject(attribute.columnName());
		}
		return String.join(", ", names);
	}
}

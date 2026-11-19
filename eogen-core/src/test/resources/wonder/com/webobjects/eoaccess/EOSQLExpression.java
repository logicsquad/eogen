package com.webobjects.eoaccess;

public class EOSQLExpression {
	private String _statement;

	public EOSQLExpression(EOEntity entity) {
	}

	public String statement() {
		return _statement;
	}

	public void setStatement(String statement) {
		_statement = statement;
	}
}

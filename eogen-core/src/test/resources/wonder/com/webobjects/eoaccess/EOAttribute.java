package com.webobjects.eoaccess;

public class EOAttribute {
	private String _name;

	private String _columnName;

	public String name() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String columnName() {
		return _columnName;
	}

	public void setColumnName(String columnName) {
		_columnName = columnName;
	}
}

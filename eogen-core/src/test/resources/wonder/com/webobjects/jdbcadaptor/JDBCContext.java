package com.webobjects.jdbcadaptor;

import java.sql.Connection;

import com.webobjects.eoaccess.EOAdaptor;
import com.webobjects.eoaccess.EOAdaptorContext;

public class JDBCContext extends EOAdaptorContext {
	private final Connection _connection;

	/**
	 * Constructor, for the tests: Wonder's connects to its adaptor's database.
	 */
	public JDBCContext(EOAdaptor adaptor, Connection connection) {
		super(adaptor);
		_connection = connection;
	}

	public Connection connection() {
		return _connection;
	}
}

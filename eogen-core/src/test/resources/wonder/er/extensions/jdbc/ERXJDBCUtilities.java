package er.extensions.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.webobjects.eoaccess.EOAdaptorChannel;
import com.webobjects.eoaccess.EOAdaptorContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.jdbcadaptor.JDBCContext;

public class ERXJDBCUtilities {
	public static int executeUpdateScript(EOAdaptorChannel channel, String sqlScript) throws SQLException {
		return executeUpdateScript(channel, sqlScript, false);
	}

	public static int executeUpdateScript(EOAdaptorChannel channel, String sqlScript, boolean ignoreFailures) throws SQLException {
		return executeUpdateScript(channel, new NSArray<String>(sqlScript.split(";")), ignoreFailures);
	}

	public static int executeUpdateScript(EOAdaptorChannel channel, NSArray<String> sqlStatements) throws SQLException {
		return executeUpdateScript(channel, sqlStatements, false);
	}

	/**
	 * Runs each statement, as Wonder's does: on the context's connection, and
	 * throwing a RuntimeException, not an SQLException, for one that fails.
	 */
	public static int executeUpdateScript(EOAdaptorChannel channel, NSArray<String> sqlStatements, boolean ignoreFailures) throws SQLException {
		EOAdaptorContext adaptorContext = channel.adaptorContext();
		if (!(adaptorContext instanceof JDBCContext)) {
			return 0;
		}
		int rowsUpdated = 0;
		Connection conn = ((JDBCContext) adaptorContext).connection();
		try (Statement stmt = conn.createStatement()) {
			for (String sql : sqlStatements) {
				try {
					rowsUpdated += stmt.executeUpdate(sql);
				} catch (Throwable t) {
					if (!ignoreFailures) {
						throw new RuntimeException("Failed to execute '" + sql + "'.", t);
					}
				}
			}
		}
		return rowsUpdated;
	}
}

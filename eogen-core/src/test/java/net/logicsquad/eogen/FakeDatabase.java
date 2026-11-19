package net.logicsquad.eogen;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * A database that keeps only the names of its tables and sequences, for a
 * migration to run on, and that fails as PostgreSQL does: a statement that
 * creates what exists, or drops or alters what doesn't, fails, and then so
 * does every statement of the transaction, until it rolls back to a savepoint
 * set before the one that failed.
 * <p>
 * Or, {@link #endingTheTransactionWithEachStatement() if asked}, as H2 does:
 * each statement ends the transaction, whether or not it fails, so that none
 * fails for an earlier one, but a savepoint set before it is no longer valid.
 * </p>
 * <p>
 * It knows the statements that the stubs of Wonder's API run to create and
 * drop a table and its primary key support, and no others.
 * </p>
 */
final class FakeDatabase {
	/**
	 * A savepoint, with the tables and sequences as they were at it.
	 */
	private static final class Saved implements Savepoint {
		private final Set<String> tables;

		private final Set<String> sequences;

		private final int transaction;

		Saved(Set<String> tables, Set<String> sequences, int transaction) {
			this.tables = new TreeSet<>(tables);
			this.sequences = new TreeSet<>(sequences);
			this.transaction = transaction;
		}

		@Override
		public int getSavepointId() {
			return 0;
		}

		@Override
		public String getSavepointName() {
			return null;
		}
	}

	private final Set<String> tables = new TreeSet<>();

	private final Set<String> sequences = new TreeSet<>();

	private final List<String> statements = new ArrayList<>();

	private boolean aborted;

	private boolean endsWithEachStatement;

	/**
	 * How many transactions have ended.
	 */
	private int transaction;

	private final Connection connection = proxy(Connection.class, this::connection);

	/**
	 * Adds a table, as if an earlier migration had created it.
	 */
	FakeDatabase table(String name) {
		tables.add(name);
		return this;
	}

	/**
	 * Adds a sequence, as if an earlier migration had created it.
	 */
	FakeDatabase sequence(String name) {
		sequences.add(name);
		return this;
	}

	/**
	 * Makes it fail as H2 does, and not as PostgreSQL does.
	 */
	FakeDatabase endingTheTransactionWithEachStatement() {
		endsWithEachStatement = true;
		return this;
	}

	/**
	 * Returns a connection in a transaction, with auto-commit off.
	 */
	Connection getConnection() {
		return connection;
	}

	Set<String> getTables() {
		return tables;
	}

	Set<String> getSequences() {
		return sequences;
	}

	/**
	 * Returns the statements that ran, without those that failed, since the
	 * last commit.
	 */
	List<String> getStatements() {
		return statements;
	}

	/**
	 * Ends the transaction as {@code ERXMigrator} ends a migration's: with an
	 * update of the model's version, which fails if a statement failed, and a
	 * commit.
	 */
	void commit() throws SQLException {
		execute("UPDATE _dbupdater SET version = version + 1");
		statements.clear();
	}

	private Object connection(Method method, Object[] arguments) throws SQLException {
		switch (method.getName()) {
		case "getAutoCommit":
			return false;
		case "getMetaData":
			return proxy(DatabaseMetaData.class, (metaDataMethod, metaDataArguments) -> {
				if (metaDataMethod.getName().equals("supportsSavepoints")) {
					return true;
				}
				throw new UnsupportedOperationException(metaDataMethod.toString());
			});
		case "createStatement":
			return proxy(Statement.class, (statementMethod, statementArguments) -> {
				switch (statementMethod.getName()) {
				case "executeUpdate":
					return execute((String) statementArguments[0]);
				case "close":
					return null;
				default:
					throw new UnsupportedOperationException(statementMethod.toString());
				}
			});
		case "setSavepoint":
			if (aborted) {
				throw aborted();
			}
			return new Saved(tables, sequences, transaction);
		case "rollback":
			if (arguments == null) {
				throw new UnsupportedOperationException(method.toString());
			}
			Saved saved = (Saved) arguments[0];
			if (saved.transaction != transaction) {
				throw new SQLException("Savepoint is invalid", "90063");
			}
			tables.clear();
			tables.addAll(saved.tables);
			sequences.clear();
			sequences.addAll(saved.sequences);
			aborted = false;
			return null;
		default:
			throw new UnsupportedOperationException(method.toString());
		}
	}

	private int execute(String sql) throws SQLException {
		if (aborted) {
			throw aborted();
		}
		String[] words = sql.trim().split("\\s+");
		String kind = words.length < 3 ? "" : (words[0] + " " + words[1]).toUpperCase(Locale.ROOT);
		String name = words.length < 3 ? "" : words[2];
		try {
			switch (kind) {
			case "CREATE TABLE":
				create(tables, name);
				break;
			case "DROP TABLE":
				drop(tables, name, "table");
				break;
			case "CREATE SEQUENCE":
				create(sequences, name);
				break;
			case "DROP SEQUENCE":
				drop(sequences, name, "sequence");
				break;
			case "ALTER TABLE":
				if (!tables.contains(name)) {
					throw new SQLException("ERROR: relation \"" + name + "\" does not exist", "42P01");
				}
				break;
			case "UPDATE _DBUPDATER":
				break;
			default:
				throw new SQLException("ERROR: syntax error at or near \"" + words[0] + "\"", "42601");
			}
		} catch (SQLException e) {
			aborted = !endsWithEachStatement;
			throw e;
		} finally {
			if (endsWithEachStatement) {
				transaction++;
			}
		}
		statements.add(sql);
		return 0;
	}

	private static void create(Set<String> names, String name) throws SQLException {
		if (!names.add(name)) {
			throw new SQLException("ERROR: relation \"" + name + "\" already exists", "42P07");
		}
	}

	private static void drop(Set<String> names, String name, String kind) throws SQLException {
		if (!names.remove(name)) {
			throw new SQLException("ERROR: " + kind + " \"" + name + "\" does not exist", "42P01");
		}
	}

	private static SQLException aborted() {
		return new SQLException("ERROR: current transaction is aborted, commands ignored until end of transaction block", "25P02");
	}

	/**
	 * What a connection, a statement or the connection's metadata does for
	 * a method.
	 */
	private interface Behaviour {
		Object invoke(Method method, Object[] arguments) throws SQLException;
	}

	private static <T> T proxy(Class<T> type, Behaviour behaviour) {
		InvocationHandler handler = (proxy, method, arguments) -> {
			if (method.getDeclaringClass() == Object.class) {
				try {
					return method.invoke(behaviour, arguments);
				} catch (InvocationTargetException e) {
					throw e.getCause();
				}
			}
			return behaviour.invoke(method, arguments);
		};
		return type.cast(Proxy.newProxyInstance(FakeDatabase.class.getClassLoader(), new Class<?>[] { type }, handler));
	}
}

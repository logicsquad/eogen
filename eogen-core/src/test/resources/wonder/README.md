# Stubs of Project Wonder's migration API

The signatures of the Project Wonder and WebObjects classes that a generated
migration uses, so that `MigrationTest` can compile the migrations that eogen
generates and check that every call exists. They are written from Wonder's
API, and every overload of a method that a migration calls is here, so that
the compiler picks among the same ones.

Most have no bodies. Those that creating a table, setting its primary key
and dropping it go through do in miniature what Wonder 7.4's do, so that
`MigrationTest` can also run a migration, on `FakeDatabase`, a connection
that keeps the tables and sequences that the statements make, and fails a
transaction as PostgreSQL does. `EOSynchronizationFactory` has the statements
of a plugin with none of its own, and `PostgresqlSynchronizationFactory`
those of Wonder's PostgreSQL plugin. The constructors of `EOAdaptor` and
`JDBCContext` are for the tests.

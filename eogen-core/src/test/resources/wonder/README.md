# Stubs of Project Wonder's migration API

The signatures of the Project Wonder and WebObjects classes that a generated
migration uses, with no bodies, so that `MigrationTest` can compile the
migrations that eogen generates and check that every call exists. They are
written from Wonder's API, and every overload of a method that a migration
calls is here, so that the compiler picks among the same ones.

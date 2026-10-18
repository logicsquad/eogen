---
name: eogen
description: Edit WebObjects EOModels (.eomodeld folders) safely, and keep their generated entity classes in sync with eogen. Use when adding, renaming or removing an entity, attribute, relationship or fetch specification, when a _Entity.java superclass is out of date, or when mvn eogen:check fails.
---

# Editing EOModels with eogen

This describes eogen 0.5. Check the project's eogen version, in its POM
or its parent's: an older one lacks some of what follows.

An EOModel is a folder, `<Model>.eomodeld`, of property lists in the
NeXT format:

- `index.eomodeld`: the model. Its `entities` list names every entity,
  with `name`, `className`, and `parent` for a subclass, and
  `storedProcedures` names the stored procedures.
- `<Entity>.plist`: one entity, with its `attributes`, `relationships`,
  `classProperties`, `primaryKeyAttributes`, `attributesUsedForLocking`,
  `className`, `externalName`, and any `parent`, `restrictingQualifier`
  and `userInfo`.
- `<Entity>.fspec`: the entity's fetch specifications, by name.
- `<Name>.storedProcedure`: a stored procedure.

eogen generates a superclass, `_<Entity>.java`, for each entity, every
time, and its subclass, `<Entity>.java`, once. Never edit a `_*.java`
file, since the next generation overwrites it. Put code in the subclass.

## The loop

After changing a model, run these from the project's folder:

    mvn eogen:format eogen:verify eogen:generate -Deogen.strict
    mvn compile

Maven stops at the first goal that fails.

1. `format` rewrites the model files as Entity Modeler saves them, so
   that a diff shows only what you changed. If it rewrites files you
   didn't touch, they weren't formatted before: commit that on its own.
   If it says a model is "left alone", saving it would change what a
   file says, such as a `YES` that Entity Modeler writes as `Y`, and it
   names the first such change. Fix that by hand, and run it again.
2. `verify` checks the model group as Entity Modeler does, and lints
   the models. The lint finds misspelt keys, names in `classProperties`
   and the other lists that match nothing, entity files that
   `index.eomodeld` doesn't list, and keys in fetch specifications and
   restricting qualifiers that match nothing. With `-Deogen.strict`,
   these are errors.
3. `generate` writes the superclasses that changed, and any missing
   subclass. If the model group or a template has an error, it writes
   nothing, and says why. Fix the model rather than reaching for
   `-Deogen.force`.
4. If the project binds `check` to its build, as eogen's README
   suggests, `mvn verify` then confirms that nothing is stale. Otherwise
   run `mvn eogen:check`.

If the project commits Markdown pages of its models, such as
`docs/models/Library.md`, whose first line says that eogen wrote them,
write them again too, and commit them with the model:

    mvn eogen:doc -Deogen.doc.format=markdown

Without the plugin in the POM, give the goal in full:
`mvn net.logicsquad:eogen-maven-plugin:<version>:<goal>`. The command
line, `eogen-cli`, does the same with `--format`, `--verify`, `--all`,
`--check` and `--strict`, and `--json` reports in JSON. There, each
finding has a `code` for its kind, such as `unknown-key`, and, where
eogen knows them, the `file`, `line`, `object`, `key` and the
`suggestion` it most likely meant.

In a multi-module build, run `mvn install` in the folder of any
framework whose models a module uses before running the loop in the
module's folder, so that Maven can find the framework's models.

## Editing the property lists

- Write each value as `key = value;`, with `Y` and `N` for booleans.
  Quote a value that isn't a plain word: `columnName = "posted_at";`.
- An attribute usually names a `prototypeName`, such as `id`,
  `varchar100`, `intNumber`, `amount`, `boolean` or `dateTime` from
  ERPrototypes, rather than an `externalType` and a `valueClassName`.
- To add an attribute, add its dictionary to `attributes`. Add its name
  to `classProperties` if the class should have it, and to
  `attributesUsedForLocking` if optimistic locking should compare it.
- To add a relationship, give it a `name`, `destination`, `isToMany`,
  `joinSemantic = EOInnerJoin;`, and `joins` of `sourceAttribute` and
  `destinationAttribute`. Set `isMandatory = Y;` for a mandatory to-one,
  and add it to `classProperties`.
- To add an entity, write `<Entity>.plist`, and add
  `{className = "..."; name = <Entity>; }` to the `entities` in
  `index.eomodeld`. The model ignores an entity file that the index
  doesn't list.
- Read the documentation of what you change first: it says what the
  model's authors meant. Entity Modeler keeps it in each object's
  `userInfo`, as `userInfo = {"_EntityModeler" = {documentation =
  "..."; }; };`, and eogen puts it in the superclasses' Javadoc.
- Document each entity, attribute and relationship that you add, there.
  Documentation is HTML: put names in `<code>`, such as
  `<code>Book</code>`, and write `&amp;` and `&lt;` for `&` and `<`.
  Say what the thing means and why it is there, rather than its type,
  which the model already says. If the request doesn't say what it
  means, ask. When a change alters what something means, change its
  documentation too.
- Don't rename or remove by hand: a name appears in many places, such
  as joins in other entities, `classProperties`, flattened definitions
  and fetch specifications. Use the goals, which change them all:
  - `mvn eogen:rename -Deogen.rename=Book.titel -Deogen.to=title` for an
    attribute or relationship, or `-Deogen.rename=Book -Deogen.to=Volume`
    for an entity, with `-Deogen.renameClass` to rename its class too.
    Columns and tables keep their names.
  - `mvn eogen:remove -Deogen.remove=Book.notes`. It refuses while
    anything else uses what it would remove, and names each use: remove
    those first.
  Both are refused, as `format` leaves a model alone, if a file they
  would write says something that Entity Modeler's writer would drop
  or change, and they name it: fix that by hand, and run them again.
  Add `-Deogen.dryRun` to see what would change. Then do what they
  report as left to do by hand, such as renaming a subclass, or changing
  documentation that names the old name, and run the loop.
  `mvn eogen:generate -Deogen.deleteOrphans` deletes an old superclass.
- The plugin looks for orphans only when it uses every `.eogen` file
  under the model folders, as it does unless `eogenFiles` names some.
  Otherwise, delete the old `_<Entity>.java` by hand.

## Migrations

If the project has Project Wonder migrations, classes such as
`Library3.java` that extend `ERXMigrationDatabase.Migration`, a change
to a model's tables needs one too. After the loop, before committing:

    mvn eogen:migration

It writes the next migration for each model whose tables changed since
its latest migration was committed, or last committed with a change to
the model, beside it, or for a model with none, since the last commit;
`-Deogen.migration.from=<commit>` compares every model with another.
`mvn eogen:migration -Deogen.checkMigrations` writes nothing, and fails
if a model with migrations has changed without one. Where it can't
decide, it leaves a gap, a `// TODO eogen:` comment and `EOGEN_GAP`,
which doesn't compile, and reports each one. Fill each in as its comment
says, and ask the user when the answer depends on their data, such as
the value that existing rows get in a new column that doesn't allow
null. Then run `mvn compile`. Never edit a migration that is already
committed: a database may have run it. If a table or a column must stay
after its entity or attribute goes, for its rows, list it in the
model's `userInfo` under `eogen.keep`, rather than deleting the drops
from a migration.

## Reading what eogen reports

- `$attribute.javaClassName has no value for attribute price
  (_WonderEntity.java)`: the template couldn't render that reference
  for that attribute. Usually its prototype, or its class, is missing:
  check its `prototypeName`.
- `unknown key prototypeNam (did you mean prototypeName?)`: a misspelt
  key, which the model classes ignore.
- `classProperties names titel, which is not an attribute or
  relationship (did you mean title?)`: the class would silently lack
  `title`.
- `fetch specification byAuthor: qualifier key autor matches nothing in
  Book (did you mean author?)`: a key in a fetch specification, or in a
  restricting qualifier, that no attribute or relationship has, often
  left behind by a rename. The fetch would fail when it runs. Fix the
  key in the `.fspec` or the entity's `.plist`.
- `${binding.attributePath.childClassName} has no value for binding
  username`: a fetch specification's qualifier compares a key that
  matches nothing with that variable. Fix the key, which eogen reports
  first.
- `entity Book: documentation has <codwe>, which isn't an HTML tag (did
  you mean <code>?)`: documentation that a Javadoc build would fail on,
  now that it can go in Javadoc comments. Fix the HTML in its
  `documentation`. If you didn't write it, it was wrong before: fix it
  in a commit of its own.
- `The documentation of Loan's relationship book names Book. Change it
  to Volume by hand.`: after a rename, documentation that still names
  the old name in `<code>`. Read it, and change the name where it means
  what you renamed.
- `Library failed to load: Library.eomodeld/Book.plist:12:58: ...`: a
  syntax error, at that line and column.
- `_Book.java is an orphan: no .eogen file generates it any more.`: its
  entity was deleted or renamed, and
  `mvn eogen:generate -Deogen.deleteOrphans` deletes it.
- `_Book.java is out of date.`, from `check`: run `generate`, and commit
  the result.
- `src/main/java/com/example/Library3.java:27: book may have rows, which
  need a value for isbn, a new column that doesn't allow null.`: a gap in
  a new migration, at that line. Replace `EOGEN_GAP` with the value. Or
  delete the migration, give the attribute a `default` in its
  `userInfo`, and run `mvn eogen:migration` again: while the migration
  is there, the next run writes the one after it.
- `src/main/java/com/example/Library3.java:18: This migration needs
  Reference's table currency, but eogen can't find a migration of
  Reference, so it can't tell which version of Reference to depend
  on.`: a gap in `modelDependencies()`. Ask the user which migration of
  Reference creates the table, and replace `EOGEN_GAP` with its number,
  or drop the dependency if the table doesn't come from a migration.

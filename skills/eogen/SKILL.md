---
name: eogen
description: Edit WebObjects EOModels (.eomodeld folders) safely, and keep their generated entity classes in sync with eogen. Use when adding, renaming or removing an entity, attribute, relationship or fetch specification, when a _Entity.java superclass is out of date, or when mvn eogen:check fails.
---

# Editing EOModels with eogen

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

Without the plugin in the POM, give the goal in full:
`mvn net.logicsquad:eogen-maven-plugin:<version>:<goal>`. The command
line, `eogen-cli`, does the same with `--format`, `--verify`, `--all`,
`--check` and `--strict`, and `--json` reports in JSON.

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
- To rename an entity, rename its `.plist`, and its `.fspec` if it has
  one. Change its `name` and `className` there and in `index.eomodeld`,
  and every `destination` that names it. Then run
  `mvn eogen:generate -Deogen.deleteOrphans` to delete its old
  superclass, and rename its subclass by hand.
- To remove an entity, delete its `.plist` and any `.fspec`, its entry
  in the index, and every relationship to it, with their names in
  `classProperties`. Then run
  `mvn eogen:generate -Deogen.deleteOrphans`, and delete its subclass by
  hand.
- The plugin looks for orphans only when it uses every `.eogen` file
  under the model folders, as it does unless `eogenFiles` names some.
  Otherwise, delete the old `_<Entity>.java` by hand.

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
- `Library failed to load: Library.eomodeld/Book.plist:12:58: ...`: a
  syntax error, at that line and column.
- `_Book.java is an orphan: no .eogen file generates it any more.`: its
  entity was deleted or renamed, and
  `mvn eogen:generate -Deogen.deleteOrphans` deletes it.
- `_Book.java is out of date.`, from `check`: run `generate`, and commit
  the result.

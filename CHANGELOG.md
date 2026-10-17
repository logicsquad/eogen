# Changelog

The format here is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- `migration` (the goal, `--migration`, and
  `EOGenerator.generateMigrations`) writes the next Project Wonder
  migration for each model whose tables changed since an earlier version
  of the models: the last commit by default, or another, or a folder. It
  creates and drops tables, with their primary keys; adds, drops and
  renames columns, with their types, widths, precision and scale, and
  whether they allow null; and adds foreign keys and entity indexes,
  with single-table inheritance and partial entities. Where a change
  doesn't say what to do, such as the value that a new column that
  doesn't allow null needs in the rows a table has, the migration has a
  gap that doesn't compile, which a warning reports. A migration is
  named and placed as `ERXMigrator` looks for it, and rendered from a
  template, which a project can replace. #24

## [0.3] - 2026-10-18
### Changed
- In `--json`, every error, warning and problem is an object with its
  `severity`, its `message`, and a `code` for its kind, such as
  `unknown-key` or `syntax`, that keeps its meaning once released. Where
  eogen knows them, it also has the `file`, `line` and `column`, the
  `model`, `entity` and `object`, the `key` that is wrong and the
  `suggestion`, and the `template`. They were strings, or objects with
  only a `model` and a `message`. The text output is the same. The
  library has the same findings, as `Finding`. #20

### Added
- The lint reports keys that match nothing in a fetch specification's
  qualifier, sort orderings, prefetching or raw-row key paths, or in an
  entity's restricting qualifier, following relationships into the
  model group's other models. A rename or a typo leaves them behind,
  and the fetch fails only when it runs. With `strict`, the lint's
  errors come before the template errors that they cause. #19

- Shared templates: the Maven plugin finds templates in its
  dependencies, at `eogen/templates/<name>` in a jar, so that a team can
  publish its templates once rather than copy them into every project.
  The command line takes jars and folders of templates with
  `--templates`, and the library with `EOGenerator.setTemplatePath`.
  They come after the project's own templates, and before the WOLips
  folders and the bundled templates. #23

- The repository is a Claude Code plugin marketplace, whose one plugin
  has the skill, so that teams can install it, with
  `/plugin install eogen --marketplace logicsquad/eogen`, or declare it
  in a project's `.claude/settings.json`, rather than copy it. The
  plugin has eogen's version, and the skill says which eogen it
  describes. #22

- `rename` and `remove` (the goals, `--rename` with `--to`, `--remove`,
  and `EOGenerator.rename` and `remove`) rename or remove an entity,
  attribute or relationship, through Entity Modeler's model classes,
  which carry a rename into subentities, flattened definitions, joins,
  entity indexes and the property lists. A rename also rewrites the keys
  in fetch specifications and restricting qualifiers, and renames names
  only: columns and tables stay as they are, and an entity's class
  changes only with `renameClass`. A renamed entity's files are renamed
  too, and the rename is refused if another file in the model's folder
  already has one of their names. A removal is refused while anything
  else uses what it would remove. Both write only the model files they
  change, and are refused, as `format` leaves a model alone, if saving
  one of those would lose anything else that it says, such as a
  misspelt key that Entity Modeler's writer drops. They report what is
  left to do by hand, and with `dryRun` (`--check`) change nothing. #21

### Fixed
- A to-many relationship to a partial entity (`ERXPartial`) is fetched
  with the inverse relationship's key from the partial's class, which
  declares it, so the superclass compiles. eogen's templates, like
  WOLips's, named the class of the entity that the partial extends. #18
- `format` formats each entity's fetch specifications, in its `.fspec`
  file, too. It skipped them, and `checkFormat` passed with them
  unformatted, since an entity writes its `.fspec` only once its fetch
  specifications change. As with the other files, a model is left alone
  if saving would change what its `.fspec` says, such as a qualifier's
  `isEqualTo`, which Entity Modeler writes as `isEqualTo:`. The reason
  names the entry that would change, such as
  `qualifiers[0].selectorName`, rather than saying that the list would
  lose an entry.
- Where the file system ignores case, as macOS's and Windows's do by
  default, generating writes a superclass whose name changed only in
  case, such as `_Tag.java` to `_TAG.java`, under its new name. It was
  written into the old file, which kept its old name and counted as an
  orphan, so that `deleteOrphans` deleted the new superclass.

## [0.2] - 2026-10-17
### Changed
- Generating writes nothing, and fails, when loading or verifying the
  model group finds errors, or when a template renders a reference that
  has no value, since the classes would not compile. `check` fails on
  errors too. The new `force` option (`eogen.force`, `--force`,
  `EOGenerator.setForce`) writes the classes anyway, as WOLips does.
  #1, #2
- A generated file that differs from the project's only in its line
  endings is unchanged, and an updated file keeps its line endings, so a
  Windows checkout with `core.autocrlf` doesn't look stale. #8
- CI builds with JDKs 11, 17, 21 and 25 on Linux, and 17 on Windows and
  macOS. Its JDK 17 build on Linux keeps a coverage report, which
  `-Dcoverage` writes, as an artifact. #9, #14
- Messages and `--json` give paths in the project with `/`, and list
  files in the same order, on every platform. #9

### Added
- A lint for the mistakes that loading a model hides: unknown keys, with
  the key most likely meant; names in `classProperties`,
  `primaryKeyAttributes` and `attributesUsedForLocking` that match
  nothing; and entity files that `index.eomodeld` doesn't match. Its
  findings are warnings, or errors with the new `strict` option
  (`eogen.strict`, `--strict`, `EOGenerator.setStrict`). #3, #4

- Orphans: superclasses that no `.eogen` file generates any more, such
  as those of deleted or renamed entities, and their subclasses. `check`
  fails on them, unless `checkOrphans` (`eogen.check.orphans`) is false,
  and `generate` reports them, deleting the superclasses with the new
  `deleteOrphans` option (`eogen.deleteOrphans`).
  `EOGenerator.findOrphans` and `deleteOrphans` do the same in the
  library. #7

- The command line takes several `.eogen` files, or `--all` to use
  every one under the model folders, which also reports orphans and,
  with `--delete-orphans`, deletes them. It can `--verify` the model
  group, with `--include-dependencies`, and report in JSON with
  `--json`. `--debug` shows stack traces. #15

- A test corpus of eleven models, ten of them Project Wonder's, which
  CI runs: eogen must generate the 108 superclasses that WOLips 5.0.3
  does, byte for byte, with no errors and no lint findings. It covers
  inheritance, restricting qualifiers, raw-row fetch specifications,
  flattened relationships, partial entities, `ERXConstantClassName`,
  entity indexes and stored procedures. #10, #11

- `format` (the goal, `--format` and `EOGenerator.format`) formats the
  project's models as Entity Modeler saves them, for models edited by
  hand or by a coding agent. It only changes their layout: if saving a
  model would drop or change anything it says, the model is left alone.
  `checkFormat` (`eogen.checkFormat`, or `--format --check`) only
  reports. #16

- `skills/eogen/SKILL.md`, a Claude Code skill for editing EOModels and
  keeping their classes in sync with eogen. #17

### Fixed
- A model that fails to load is an error, reported in one line with the
  file and the position the parser stopped at. It was a warning, with a
  stack trace, and `verify` passed. #6
- The Maven plugin finds models in runtime-scoped dependencies, such as
  a framework of prototypes, as WOLips does through Eclipse's classpath.
  #5

## [0.1] - 2026-10-17
### Added
- `eogen-core`: WOLips's Velocity EOGenerator and Entity Modeler's model
  classes, extracted from WOLips 5.0.3 (a325dd3e) to run without
  Eclipse, with an `EOGenerator` API that generates, checks and verifies
  a project's `.eogen` files.
- `eogen-maven-plugin`, with the goals `generate`, `check` and `verify`.
- `eogen-cli`, a command line that generates from one `.eogen` file.
- Golden tests, which regenerate the classes of real projects on copies
  of them, when `eogen.golden.projects` names their folder and
  `eogen.golden.names` the projects.

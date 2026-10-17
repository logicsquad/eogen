# Changelog

The format here is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Changed
- Generating writes nothing, and fails, when loading or verifying the
  model group finds errors, or when a template renders a reference that
  has no value, since the classes would not compile. `check` fails on
  errors too. The new `force` option (`eogen.force`, `--force`,
  `EOGenerator.setForce`) writes the classes anyway, as WOLips does.
  #1, #2

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

### Fixed
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

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

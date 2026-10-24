![](https://github.com/logicsquad/eogen/workflows/build/badge.svg)
[![License](https://img.shields.io/badge/License-BSD-blue.svg)](https://opensource.org/licenses/BSD-2-Clause)

eogen
=====

What is this?
-------------
eogen generates WebObjects EOF entity classes from EOModels and
`.eogen` files, without Eclipse. It is WOLips's Velocity EOGenerator,
extracted from WOLips with Entity Modeler's model classes and made to
run on its own, so that a build, a script or a coding agent can
regenerate a project's classes after changing its models.

eogen is not a rewrite. The model classes, the `.eogen` parser and the
generator loop are WOLips's own code, with only the Eclipse parts
replaced, so prototypes, inheritance, flattened attributes and
relationships, qualifiers and `javaClassName` behave as they do in
Entity Modeler. On eleven real projects, eogen generates every
committed superclass byte for byte, 208 in all.

It also:

* checks that the classes are up to date, so that a build fails when
  someone changes a model without regenerating them;
* writes Project Wonder migrations for model changes, and fails a build
  when a model has changed without one (see
  [Writing migrations](#writing-migrations));
* writes the documentation that Entity Modeler keeps for each model,
  entity and attribute as Markdown pages with a diagram of each model,
  to commit, or as WOLips's EOModelDoc pages, and puts it in the
  generated classes' Javadoc (see
  [Documenting models](#documenting-models));
* lints the keys in qualifiers, sort orderings and prefetching, which
  otherwise fail only when a fetch runs (see [Lint](#lint));
* formats models as Entity Modeler writes them, and renames and removes
  entities, attributes and relationships through Entity Modeler's own
  classes (see [Formatting models](#formatting-models) and
  [Renaming and removing](#renaming-and-removing));
* comes with a Claude Code skill, which a team can install as a plugin,
  that shows a coding agent how to edit models with eogen (see
  [Using eogen with a coding agent](#using-eogen-with-a-coding-agent)).

There are three modules:

* `eogen-core`: the library.
* `eogen-maven-plugin`: the `generate`, `check`, `verify`, `format`,
  `rename`, `remove`, `migration` and `doc` goals.
* `eogen-cli`: a command line that does what the plugin does.

Using the Maven plugin
----------------------
Add the plugin to the `build/plugins` section of your project's POM.
This binds `check` to `verify`, so that a build fails if anyone changes
a model without regenerating its classes:

    <plugin>
      <groupId>net.logicsquad</groupId>
      <artifactId>eogen-maven-plugin</artifactId>
      <version>0.5</version>
      <executions>
        <execution>
          <goals>
            <goal>check</goal>
          </goals>
        </execution>
      </executions>
    </plugin>

Then regenerate the classes after changing a model with:

    mvn eogen:generate

The goals are:

* `generate` (default phase `generate-sources`): generates from the
  project's `.eogen` files. Like WOLips, it writes every superclass
  whose contents have changed, and every subclass that is missing, and
  never touches a subclass that exists. It reports each file it
  writes. Unlike WOLips, it writes nothing for an `.eogen` file with
  errors, and fails the build (see [Errors](#errors)). It reports
  orphans (see [Orphans](#orphans)), and with `deleteOrphans` deletes
  their superclasses.
* `check` (default phase `verify`): generates into a scratch folder,
  and fails the build if `generate` would change anything: a
  superclass that differs, or a missing superclass or subclass. It
  names each stale file, and changes nothing in the project. Errors
  and orphans fail it too, unless `checkOrphans` is false.
* `verify` (default phase `verify`): loads the project's model group,
  resolves and verifies it as Entity Modeler does, reports errors and
  warnings, and fails the build on errors.
* `format` (no default phase): formats the project's models as Entity
  Modeler saves them (see [Formatting models](#formatting-models)).
  With `checkFormat` (`eogen.checkFormat`), it changes nothing, and
  fails if any model needs formatting.
* `rename` and `remove` (no default phase): rename or remove an entity,
  attribute or relationship in the project's models (see
  [Renaming and removing](#renaming-and-removing)).
* `migration` (no default phase): writes the next Project Wonder
  migration for each model whose tables changed (see
  [Writing migrations](#writing-migrations)).
* `doc` (default phase `pre-site`): writes a Markdown page for each of
  the project's models, or EOModelDoc's HTML pages, and with
  `checkDoc` fails the build if the pages are stale (see
  [Documenting models](#documenting-models)).

They take these parameters:

* `modelDirectories` (`eogen.modelDirectories`): the folders whose
  models make up the project's model group. The default is
  `src/main/resources`, where a WebObjects Maven project keeps its
  models.
* `eogenFiles` (`eogen.eogenFiles`): the `.eogen` files to generate
  from. The default is every `.eogen` file under the model
  directories.
* `skip` (`eogen.skip`): skips the goal.
* `force` (`eogen.force`): generates despite errors, as WOLips does.
* `strict` (`eogen.strict`): makes the lint's findings errors (see
  [Lint](#lint)).
* For `generate`, `deleteOrphans` (`eogen.deleteOrphans`) deletes
  orphaned superclasses.
* For `check`, `checkOrphans` (`eogen.check.orphans`): set it to false,
  and `check` doesn't look for orphans.
* `generatedMarker` (`eogen.generatedMarker`): the text that marks a
  superclass as generated. The default is `DO NOT EDIT`.
* For `verify`, `includeDependencies` (`eogen.verify.includeDependencies`)
  also reports failures in models from dependencies, and
  `failOnWarning` (`eogen.verify.failOnWarning`) fails the build on
  warnings too.

`mvn eogen:generate` works in any project whose POM declares the
plugin. Elsewhere, use the full name,
`mvn net.logicsquad:eogen-maven-plugin:0.5:generate`, or add
`net.logicsquad` to the `pluginGroups` in `~/.m2/settings.xml`.

Using the command line
----------------------
`eogen-cli`'s jar contains everything it needs:

    java -jar eogen-cli-0.5.jar [options] <file.eogen>...
    java -jar eogen-cli-0.5.jar [options] --all
    java -jar eogen-cli-0.5.jar [options] --verify
    java -jar eogen-cli-0.5.jar [options] --migration
    java -jar eogen-cli-0.5.jar [options] --doc

    --project <folder>      The folder that the .eogen files' paths are
                            relative to. Default: the current folder.
    --models <folders>      Folders whose models make up the model
                            group, separated by ':' (';' on Windows).
                            Default: <project>/src/main/resources.
    --classpath <path>      Jars and folders whose Resources folders add
                            their models to the model group, separated
                            by ':' (';' on Windows).
    --templates <path>      Jars, with templates in eogen/templates, and
                            folders of templates, separated by ':'
                            (';' on Windows). Their templates come after
                            the project's own and before WOLips's.
    --all                   Use every .eogen file under the model
                            folders, and report orphans.
    --check                 Write nothing. List the files that
                            generating would change, and the orphans,
                            and exit with status 1 if there are any.
    --verify                Load, resolve and verify the model group as
                            Entity Modeler does, and lint the project's
                            models. Exit with status 2 on errors.
    --include-dependencies  With --verify, report on the dependencies'
                            models too. With --doc, document them too.
    --format                Format the project's models as Entity
                            Modeler saves them. With --check, write
                            nothing, and exit with status 1 if any
                            model needs formatting.
    --migration             Write the next Project Wonder migration for
                            each model whose tables changed since an
                            earlier version of the models. With
                            --check, write nothing, and exit with
                            status 1 if a model with migrations has
                            changed without one.
    --from <commit|folder>  With --migration, the earlier version of
                            every model: a git commit, or a folder of
                            models. Default: for a model with
                            migrations, the last commit that changed
                            its latest migration with the model, or
                            else added it, and for one with none,
                            HEAD.
    --sources <folders>     With --migration, the folders of the Java
                            sources. Default: <project>/src/main/java.
    --package <name>        With --migration, the package for new
                            migrations.
    --template <name>       With --migration, the migration template.
    --migration-models <names>
                            With --migration, the models to write
                            migrations for, separated by ','.
    --doc                   Write a Markdown page for each of the
                            project's models, with a Mermaid diagram,
                            to commit. With --check, write nothing, and
                            exit with status 1 if a page would change,
                            or an orphan would be deleted.
    --doc-format <format>   With --doc, markdown, or html for
                            EOModelDoc's pages. Default: markdown.
    --output <folder>       With --doc, the folder of the pages.
                            Default: <project>/docs/models, or for
                            HTML, <project>/target/site/eomodeldoc.
    --doc-templates <folder>
                            With --doc, a folder of templates and files,
                            which come before the bundled ones.
    --entity-url <template> With --doc, a Velocity template for the link
                            from each entity's class name.
    --model-diagram-limit <n>
                            With --doc in Markdown, the most entities a
                            model can have and still have a diagram of
                            them all: 0 for none. Default: 10.
    --delete-orphans        With --all, delete the orphaned superclasses.
    --force                 Write the classes even when there are
                            errors, as WOLips does. Without it, errors
                            mean nothing is written, and the exit
                            status is 2.
    --strict                Make the lint's findings in the models
                            errors rather than warnings.
    --json                  Report in JSON on standard output.
    --debug                 Show stack traces.

The exit status is 0 when all is well, 1 when `--check` finds stale
files or orphans, and 2 for errors.

To give it a Maven project's dependencies, run it from the project's
folder with the classpath that Maven resolves:

    mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
    java -jar eogen-cli-0.5.jar --classpath "$(cat cp.txt)" --all

With `--json`, it writes one JSON object to standard output, which a
script or a coding agent can read instead of the text. `status` is
`ok`, `stale` or `failed`; `eogenFiles` has each `.eogen` file's
`errors`, `warnings`, whether it was `written`, and its `files`, each
with a `kind` (`superclass` or `subclass`) and a `status` (`created`,
`updated`, `unchanged` or `orphaned`); `orphans` and `verification` are
there when `--all` and `--verify` are; `migration`, with `--migration`,
has the earlier version it compared with, as `since`, whether it was
`written`, its `errors` and `warnings`, and `migrations`, each with its
`model`, a `status` (`created`, `unchanged` or `skipped`), the `since`
it was compared with, and for a created one its `file`, `class`,
`number`, whether it is the `first`, how many `gaps` it has, and its
`changes`, or else the `reason`; `doc`, with `--doc`, has whether it was
`written`, the `models` documented, the `files`, each with its `file`
and a `status` (`created`, `updated`, `unchanged` or `orphaned`), and
its `errors`; and
`problems` has any failure that stopped it. Problems still go to
standard error, as text.

Each error, warning and problem is a finding, the same object wherever
it appears:

    {
      "severity": "warning",
      "code": "unknown-key",
      "message": "Library.eomodeld/Book.plist: attribute price: unknown key prototypeNam (did you mean prototypeName?).",
      "file": "src/main/resources/Library.eomodeld/Book.plist",
      "model": "Library",
      "entity": "Book",
      "object": "attribute price",
      "key": "prototypeNam",
      "suggestion": "prototypeName"
    }

`severity` is `error` or `warning`, and `message` is the text eogen
prints. `file` is relative to the project, with `/`. The other fields
are there only where eogen knows them: `line` and `column`, `model`,
`entity`, `object`, which names what the finding is about as the message
does, `key`, the key, name, key path or template reference that is
wrong, `suggestion`, what it most likely meant, and `template`. `code`
names the kind of finding, and keeps its meaning once released:

| Code | Finding |
|---|---|
| `syntax` | a model file that doesn't parse, at a `line` and `column` |
| `load` | a model that fails to load for another reason |
| `verifier` | what Entity Modeler's own verifier finds, which it describes only in text |
| `unknown-key` | a key in a model file that the model classes don't read |
| `unmatched-name` | a name in `classProperties`, `primaryKeyAttributes` or `attributesUsedForLocking` that matches nothing |
| `unlisted-file` | a model file that `index.eomodeld` doesn't list, so the model ignores it |
| `missing-file` | an entity that `index.eomodeld` lists, but that has no file |
| `misnamed-entity` | an entity whose `name` isn't its file's |
| `unmatched-key` | a key in a fetch specification or a restricting qualifier that matches nothing |
| `malformed-html` | documentation whose HTML a Javadoc build would fail on, such as a tag that HTML5 doesn't have |
| `unresolved-reference` | a template reference that rendered with no value |
| `format-loss` | a model that formatting leaves alone, or a rename or removal that is refused, since saving a file would change what it says |
| `eogen-file` | an `.eogen` file that eogen can't use, such as one with no `-destination` |
| `not-found` | a rename or removal whose target names nothing |
| `name-taken` | a rename to a name that something already has |
| `in-use` | a removal of something that something else uses |
| `in-dependency` | a rename or removal in a model that isn't one of the project's |
| `by-hand` | what a rename or removal leaves to do by hand |
| `migration-gap` | what a generated migration leaves to decide, at a gap that doesn't compile |
| `earlier-version` | an earlier version of the models that can't be read, such as a commit that doesn't exist |
| `not-compared` | a model with migrations that the earlier version lacks, so that it gets none |
| `unused-keep` | a name in a model's `eogen.keep` that does nothing, since the model still maps it, or the earlier version neither maps nor keeps it |
| `failure` | anything else that stops a run |

The library has the same findings, as `Finding`, in
`GenerationResult.getFindings()`, `VerificationResult.getFindings()`,
`FormatResult.getFindings()`, `RefactorResult.getFindings()`,
`MigrationResult.getFindings()` and `EOGeneratorException.getFindings()`.

Using eogen with a coding agent
-------------------------------
[`skills/eogen/SKILL.md`](skills/eogen/SKILL.md) is a
[Claude Code](https://claude.com/claude-code) skill for editing
EOModels with eogen. It covers the model files, how to add, rename and
remove entities, attributes and relationships, and document them, the
loop of `format`, `verify` and `generate` with `strict` on, and what
eogen's messages mean.

The repository is also a Claude Code plugin marketplace, whose one
plugin has the skill, so you can install it rather than copy it. In
Claude Code:

    /plugin install eogen --marketplace logicsquad/eogen

or, before Claude Code 2.1.275:

    /plugin marketplace add logicsquad/eogen
    /plugin install eogen@eogen

To give it to everyone who works on a project, declare it in the
project's committed `.claude/settings.json`, pinned to the release of
eogen that the project uses:

    {
      "extraKnownMarketplaces": {
        "eogen": {
          "source": {
            "source": "github",
            "repo": "logicsquad/eogen",
            "ref": "release/0.5"
          }
        }
      },
      "enabledPlugins": {
        "eogen@eogen": true
      }
    }

The plugin's version is eogen's, so an installed skill changes only
when eogen does. Its skill is `/eogen:eogen`. Claude Code on the web
doesn't load plugins that a repository declares. Copying the
`skills/eogen` folder into a project's `.claude/skills` folder still
works too.

Agents should run with `strict`, so that the lint's findings are
errors. Scripts can read the command line's `--json` report instead of
its text.

Using the library
-----------------

    EOGenerator generator = new EOGenerator(projectFolder,
            List.of(new File(projectFolder, "src/main/resources")), classpath);
    GenerationResult result = generator.generate(eogenFile);
    for (GeneratedFile file : result.getStaleFiles()) {
        System.out.println(file);
    }

`check(eogenFile)` returns the same result without writing anything,
and `verify(includeDependencies)` returns the model group's failures.
`getErrors()` lists a result's errors; when there are any, `generate`
writes nothing, and `isWritten()` is false, unless
`setForce(true)` was called. `getWarnings()` lists the lint's
findings, which `setStrict(true)` makes errors instead.
`format`, `rename` and `remove` do what the goals of those names do,
`generateMigrations` what `migration` does, and `generateDoc` what
`doc` does.
Below `EOGenerator`, the extracted WOLips classes are available as
they were: `EOModelGroup`, `EOModel`, `EOEntity` and the rest in
`net.logicsquad.eogen.model`, and `EOGeneratorModel` and
`VelocityEOGeneratorRunner` in `net.logicsquad.eogen.generator`.

How eogen finds models
----------------------
For `-loadModelGroup`, WOLips asks Eclipse's JDT and m2e for the
project's classpath. eogen searches the same places, in the same order,
without them:

1. every `.eomodeld` under the model directories, at any depth, in name
   order, skipping hidden folders, `.wo` folders and `Java` folders;
2. for each jar or folder on the classpath, in order, the models in its
   `Resources/` and `META-INF/webobjects/Resources/` folders;
3. last, the model that the `.eogen` file names, in case it is somewhere
   else.

When two models have the same name, the first one found wins. When a
model declares an entity that an earlier model declares, the earlier
model is removed, so loading the `.eogen` file's model last means it
survives a dependency's model that shares one of its entities. The
Maven plugin's classpath is the project's resolved compile, provided,
runtime and system dependencies, in Maven's order: every scope but
`test`, as in Eclipse. Without `-loadModelGroup`,
eogen loads the `-model` and `-refmodel` models, as WOLips does.

When a model fails to load, such as a property list with a syntax
error, WOLips prints a stack trace and reports a warning. eogen reports
an error, in one line that names the file and where the parser stopped:

    Library failed to load: Library.eomodeld/Book.plist:12:58: Property list parsing failed while attempting to read dictionary. Read key and value with no terminating semicolon.

Errors
------
WOLips reports the errors that loading and verifying the model group
finds, such as a missing prototype or entity, and then generates
anyway. The classes it writes then have unresolved template references
in them, such as `$attribute.javaClassName`, and don't compile.

eogen treats these as errors, along with any reference that a template
renders without a value, such as an attribute's Java class when a
misspelt key in the model left it without a prototype. It names each
reference, the object it belongs to and the template:

    com/example/shop/_Product.java: $attribute.javaClassName has no value for attribute price (_WonderEntity.java).

When there are errors, `generate` writes nothing for that `.eogen`
file and fails, and so does `check`. `force` writes the classes
anyway, as WOLips does.

A reference counts only if it is a property or method of a variable
that the template has. A bare reference to a variable that doesn't
exist is often not meant as one, such as the Subversion keyword
`$LastChangedRevision$` in WOLips's own `_Entity.java`, or the Java
class name `Outer$Inner`. Quiet references, such as `$!entity.parent`,
and references that only a `#if` or `#set` reads, don't count either.

Lint
----
Loading a model ignores whatever the model classes don't expect, so
some mistakes have no effect except a wrong class. eogen lints the
models that an `.eogen` file names when it generates or checks, and the
project's models when it verifies. It reports:

* a key it doesn't know, with the key that was most likely meant, such
  as `attribute price: unknown key prototypeNam (did you mean
  prototypeName?)`. That mistake leaves the attribute without its
  prototype;
* a name in `classProperties`, `primaryKeyAttributes` or
  `attributesUsedForLocking` that is not one of the entity's attributes
  or relationships, which silently drops it from the class;
* an entity file that `index.eomodeld` doesn't list, or one it lists
  that doesn't exist, and an entity whose `name` isn't its file's;
* a key in a fetch specification's qualifier, sort orderings,
  prefetching or raw-row key paths, or in an entity's restricting
  qualifier, that matches nothing, following relationships into the
  model group's other models, such as `fetch specification
  RawFetchAllMovies: sort key titel matches nothing in Movie (did you
  mean title?)`. These name keys as text, so a rename or a typo leaves
  them behind, and the fetch fails only when it runs. A qualifier's
  variables, such as `$username`, are values, and aren't checked;
* documentation whose HTML a Javadoc build would fail on, since the
  templates can put it in Javadoc comments (see
  [In the generated classes](#in-the-generated-classes)): a tag that
  HTML5 doesn't have, such as `entity Party: documentation has <codwe>,
  which isn't an HTML tag (did you mean <code>?)`, an end tag that
  closes nothing, an element such as `<code>` without its end tag, and
  an `&` or a `<` that starts nothing, which should be `&amp;` or
  `&lt;`.

The keys it knows are the ones the model classes read and write, and
the ones found in 116 real models, Project Wonder's among them. It
doesn't look inside `userInfo` or `internalInfo`, apart from Entity
Modeler's documentation.

Its findings are warnings. `strict` (`eogen.strict`, `--strict`) makes
them errors, which stop `generate` as other errors do. Use `strict`
when a script or a coding agent edits the models.

Orphans
-------
When an entity is deleted or renamed, WOLips leaves its superclass
behind, and the superclass's hand-edited subclass, and nothing notices.
eogen calls a superclass an orphan if it is in an `.eogen` file's
destination, has that file's prefix and extension, has the generated
marker (`DO NOT EDIT`) in its first five lines, and no `.eogen` file
in the project generates it.

`check` fails on orphans and on their subclasses, unless
`checkOrphans` is false. `generate` reports them, and with
`deleteOrphans` deletes the orphaned superclasses. It never deletes a
subclass, since a subclass is edited by hand, so it leaves those to
you.

Several `.eogen` files often share a destination, so the goals only
look for orphans when they use every `.eogen` file in the model
directories, and not when `eogenFiles` names some of them.

Formatting models
-----------------
A model edited by hand, or by a coding agent, rarely looks as Entity
Modeler would write it, so the next save in Entity Modeler rewrites it,
and its diff is noise. `format` saves each of the project's models with
Entity Modeler's own writer, in a scratch folder, and writes back the
files that only change in layout.

It must lose nothing that a file says. No value may be dropped or
changed, and no entry removed from a list. It may drop an empty value,
such as `fetchSpecificationDictionary = {}`, and add what Entity
Modeler writes for the model as it loaded it, such as an inherited
entity's `parent` in `index.eomodeld`. If it would lose anything from
any file, such as changing `isMandatory = YES` to `Y`, or a qualifier's
`isEqualTo` to `isEqualTo:`, it leaves the whole model alone and fails,
naming the first loss:

    Library.eomodeld/Book.plist: its model is left alone, since saving it would change what it says: relationships[author].isMandatory would change from YES to Y.

Entity Modeler's writer keeps keys it doesn't know, except in a fetch
specification's qualifier and sort orderings, which it writes afresh,
and there formatting refuses to drop them. Either way, formatting
doesn't hide a misspelt key: the lint still reports it. Files that a
model never loads, such as an entity file that `index.eomodeld`
doesn't list, are left alone. Formatting needs the model group, and
does nothing if it has errors.

Renaming and removing
---------------------
A name in a model appears in many places: an attribute's, for example,
in its entity's `attributes`, `classProperties`,
`attributesUsedForLocking` and `primaryKeyAttributes`, in joins in other
entities, in flattened definitions, in entity indexes, in subentities,
and in qualifiers and sort orderings. `rename` changes them all:

    mvn eogen:rename -Deogen.rename=Book.titel -Deogen.to=title
    mvn eogen:rename -Deogen.rename=Book -Deogen.to=Volume -Deogen.renameClass

It renames through Entity Modeler's model classes, which carry the
change into subentities, flattened definitions, joins, entity indexes
and the property lists, and it rewrites the keys that fetch
specifications and restricting qualifiers name as text. It renames
names only. Entity Modeler's own rename also renames the column, the
table or the class; `rename` keeps them, so that the database needs no
change, and an entity's class changes only with `renameClass`
(`eogen.renameClass`). It saves the models with Entity Modeler's writer,
and writes back only the files that the rename changes. A renamed
entity's files are renamed too, so the rename is refused if another
file in the model's folder already has one of their names, such as an
entity file that `index.eomodeld` doesn't list, which the rename would
replace, or a stale `.fspec`, which the renamed entity would load:

    There is already a file at src/main/resources/Library.eomodeld/Volume.plist, so Book can't be renamed to Volume. Move it away first.

`remove` removes an entity, attribute or relationship:

    mvn eogen:remove -Deogen.remove=Book.notes

It refuses while anything else uses what it would remove, and names
each use: a join, a flattened definition, an index, a relationship to
an entity, a subentity, or a key in a fetch specification or restricting
qualifier. Nothing is removed by cascade, so remove those uses first.
A column or table stays in the database.

Both write each file they change as Entity Modeler's writer saves it,
which would take with it anything else in the file that the writer
drops or changes: a misspelt key in a sort ordering, which it drops,
or `isMandatory = YES`, which it writes as `Y`. So, as `format` does,
they compare each file they would write with Entity Modeler's save of
it from before the change (for a renamed entity's new file, its old
one), and if saving it would lose anything, they are refused, and name
the file and the loss:

    src/main/resources/Library.eomodeld/Book.fspec: nothing is changed, since saving it would change what it says: byAuthor.sortOrderings[0].selector would be dropped. Fix that by hand first; format names the same loss.

Fix that by hand, and run them again. A change that doesn't write the
file goes ahead, and leaves it as it is.

Both report what is left to do by hand, as warnings with the code
`by-hand`: renaming a subclass, since eogen never edits one; deleting an
orphaned superclass, which `generate` with `deleteOrphans` does; code
that uses the old accessors, which compiling finds; text in the models
that still names the old name, such as custom SQL; documentation that
names it in a `<code>` element, which eogen leaves as it is, since only
the words around a name say whether it means what was renamed: an
entity's name anywhere in the project's models, and a property's in
the documentation of its entity and its properties, or after its
entity's name and a dot, as in `<code>Book.title</code>`; and, when
only the case of an entity's name changes, renaming its files, and its
classes' if they are renamed too, in version control, which may not
see the change where the file system ignores case, as macOS's and
Windows's do by default. With `dryRun`
(`eogen.dryRun`), they change nothing and only report. The command line
does the same with `--rename <target> --to <name>`, `--rename-class`
and `--remove <target>`, and `--check` for a dry run.

Writing migrations
------------------
Project Wonder's `ERXMigrator` brings a database up to date with its
models by running migrations, which are written by hand: for each
model, classes numbered from 0, each of which changes the model's
tables. Most of what one says, the model change committed with it says
already. `migration` writes it:

    mvn eogen:migration

It compares each of the project's models with an earlier version of
it, and writes the next migration for each model whose tables changed.
By default, a model with migrations is compared with the commit that
its latest migration is up to date with: the last that changed the
migration and the model together, as when a migration that isn't
released yet is edited with its model, or else the one that added it.
The next migration is then for every change since, however many
commits it took. A model with none is compared with the last commit.
After `Library2.java`, it writes `Library3.java` beside it:

    public class Library3 extends ERXMigrationDatabase.Migration {
        ...
        @Override
        public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
            ERXMigrationTable bookTable = database.existingTableNamed("book");
            bookTable.newStringColumn("isbn", 13, ALLOWS_NULL);
            bookTable.existingColumnNamed("title").setWidthType(Types.VARCHAR, 200, null);
        }
    }

It covers tables created, with their primary keys, and dropped;
columns added, dropped and renamed, with their types, widths, precision
and scale, and whether they allow null; the foreign keys of to-one
relationships, which come once every table they need exists; and
entity indexes. A table has the columns of every entity that maps it, so
that a subentity's columns are in its parent's table in single-table
inheritance, where they allow null, and a partial entity's are in its
base entity's. A flattened or derived attribute has no column. A
foreign key comes from a to-one whose joins end at its destination's
primary key, and not from one that propagates its primary key, whose
destination has the key back. Column types follow Entity Modeler's own
migrations, from an attribute's prototype, value class and value type,
with their mismatches with Wonder's API fixed. As for a migration
written by hand, the SQL comes from the database's EOF plugin when the
migration runs.

Where a model change doesn't say what to do, the migration has a gap: a
`// TODO eogen:` comment that says what to decide, and `EOGEN_GAP`,
which doesn't compile, so that the migration can't run until someone
fills it in. The goal reports each gap, with its line, as a warning
with the code `migration-gap`. There is a gap for:

* the value that a new column that doesn't allow null needs in the rows
  its table already has, unless the attribute has a `default` in its
  `userInfo`, which Wonder gives new objects too;
* the rows where a column is null, when it stops allowing null;
* a column dropped while another is added to its table, and a table
  dropped while another is created, either of which may be a rename. An
  attribute that keeps its name while its column's changes renames the
  column, and an entity that keeps its name while its table's changes
  renames the table;
* a column whose type changes, whose values may need converting;
* a primary key that changes;
* a foreign key or an index that goes, since Wonder has no call to drop
  one;
* a column that the model keeps, but no longer maps, and that doesn't
  allow null, since a new row has no value for it (see below);
* the version of another model to depend on, when eogen finds no
  migration of it.

A migration is named as `ERXMigrator` looks for it: the model's
`MigrationClassPrefix`, which is its name unless the project's
`Properties`, in its model folder, say otherwise, then the number after
the highest of the model's migrations in the project's sources,
vendor-specific ones such as `LibraryPostgreSQL2` included. It goes in
the package that the prefix names, if it names one, or else in the one
that `package` gives, or else in that of the model's latest migration.
A model with no migrations yet gets its first, which creates its
tables, numbered after its `InitialMigrationVersion`, beside most of the
other models' latest migrations, or else in the package of most of its
entity classes. A model that `models` names gets its first
even if its tables didn't change, and a model that Wonder's
`er.migration.skipModelNames` names gets none. Nothing changes an
existing migration, and nothing is written if there are errors. To
write a migration again, after changing the model, delete it first:
while it is there, the next run writes the one after it.

`ERXMigrator` migrates the models in the order of
`er.migration.modelNames`, or of the model group if that isn't set, and
before a migration, it migrates each model in its `modelDependencies()`
up to the version given. So a migration that needs another model's
table, for a foreign key to it, or for a partial entity's columns in
its base entity's table, depends on that model:

    @Override
    public NSArray<ERXModelVersion> modelDependencies() {
        return new NSArray<ERXModelVersion>(new ERXModelVersion("Reference", 3));
    }

The version is that model's latest migration: the one written in the
same run, if there is one, or else the highest in the project's
sources, or, for a model in a dependency, among the dependency's
classes, named with its `MigrationClassPrefix` from the project's
`Properties` or the dependency's own. `ERXMigrator` stops quietly at a
version that doesn't exist, so the latest is safe, even when the table
came earlier. A model that `er.migration.skipModelNames` names isn't a
dependency.

The parameters are:

* `from` (`eogen.migration.from`): the earlier version of every model,
  a git commit, such as `HEAD~1`, a branch or a tag, or a folder of
  models, relative to the project. By default, a model with migrations
  is compared with the last commit that changed its latest migration
  with the model, or else the one that added it at the path it has, and
  a model with none with `HEAD`. A commit that changes only migrations,
  such as one that tidies the Javadoc of every one, doesn't count. A
  model whose latest migration isn't committed yet isn't compared, and
  gets a `not-compared` warning.
* `packageName` (`eogen.migration.package`): the package for new
  migrations.
* `template` (`eogen.migration.template`): the migration template. The
  default, `Migration.java`, extends `ERXMigrationDatabase.Migration`,
  and its `downgrade` throws `ERXMigrationFailedException`, as Wonder
  asks of a migration that can't be undone.
* `models` (`eogen.migration.models`): the models to write migrations
  for. The default is every model whose tables changed.
* `dryRun` (`eogen.dryRun`): writes nothing, and only reports.
* `checkMigrations` (`eogen.checkMigrations`): writes nothing, and
  fails the build if a model with migrations has changed without one.

So that a build fails when a model changes with no migration, as it
does with `check` when a class is stale, bind `migration` to `verify`
with `checkMigrations`:

    <execution>
      <id>check-migrations</id>
      <phase>verify</phase>
      <goals>
        <goal>migration</goal>
      </goals>
      <configuration>
        <checkMigrations>true</checkMigrations>
      </configuration>
    </execution>

It names each such model, and what changed:

    Library changed since Library3 (commit 1a2b3c4), with no migration: Adds isbn to book.

Sometimes the rows have to outlive the code: an entity or an attribute
leaves the model, but its table or column stays, for a client's data
retention, say, or an audit. The model lists those in its `userInfo`,
under `eogen.keep`, each a table, or a table and a column with a dot
between them, which Entity Modeler's User Info editor enters as an
array:

    userInfo = {"eogen.keep" = (fine, "member.fines_owed"); };

`migration` then neither drops nor changes them, and leaves their
foreign keys, and those into a kept table, as they are. So a change
that only stops mapping kept tables and columns needs no migration, and
`checkMigrations` passes on it. When the data can go, taking its names
off the list makes `migration` write the drops. A kept column that
doesn't allow null, on a table that rows are still inserted into, gets
a gap. A name that the model still maps, or that the earlier version
neither maps nor keeps, does nothing, and gets an `unused-keep`
warning. When `checkMigrations` fails on a change that drops a table or
a column, its message says how to keep it instead.

A project changes what its migrations extend, and their comments and
imports, with its own template, which eogen finds as it finds the entity
templates (see [How eogen finds templates](#how-eogen-finds-templates)),
so that a team can share one in a jar. A template sees `$migration`,
with its `className`, `packageName`, `modelName` and `number`, whether
it is the `first`, `since`, the earlier version, such as
`Library2 (commit 1a2b3c4)`, `changes`, a sentence for each change,
`upgradeLines`, the body of `upgrade`, which uses its `database`
argument, `dependencies`, each with its `modelName`, its `version`,
whether it is `known`, and its `tables`, `dependencyLines`, the body of
`modelDependencies`, or none, and `importLines`, the imports that the
bodies need, or with `importLines(["com.example.Base"])`, those and
others; and `$model`, the model. A reference with no value is an error,
as it is for the classes.
The command line does the same with `--migration`, `--from`,
`--sources`, `--package`, `--template`, `--migration-models`, and
`--check`, which writes nothing, and exits with status 1 if a model with
migrations has changed without one.

In a real project's recent history, 42 migrations came with a model
change. Regenerated from that change, eogen wrote 28 of them whole, 4
of them with gaps where the person had filled in a value, and most of 5
more. Of the rest, some have gaps that ask what the person decided, such
as whether a column was renamed, and some move data with SQL.

Documenting models
------------------
Entity Modeler keeps documentation for each model, entity, attribute,
relationship, fetch specification, entity index and stored procedure,
as HTML in its `userInfo`. `doc` writes a page for each model,
`<Model>.md`, in `docs/models`, to commit beside the code:

    mvn eogen:doc

GitHub and GitLab render it, and a pull request that changes a model
shows the change in its page:

    Shop
    ====

    A small shop, for testing eogen.

    ```mermaid
    erDiagram
        Order
        Party
        ...
        Party ||--o{ Order : "orders / customer"
        Order }o--|| Currency : "currency"
    ```

    Order
    -----

    - Table: `shop_order`
    - Class: `com.example.shop.Order`

    | Attribute | Column | Column type | Java type | Allows null | Documentation |
    |---|---|---|---|---|---|
    | `placed` | `placed` | `timestamp` | `NSTimestamp` | no | When the order was placed. |

A page has the model's documentation, a
[Mermaid](https://mermaid.js.org) diagram of its entities and their
relationships, and a section for each entity, with its documentation,
a diagram of the entities that it reaches and that reach it, its table
and class, and tables of its attributes and relationships, fetch
specifications and entity indexes, each with its documentation.
An attribute has its column's type and the Java class of its values, as
the bundled templates write it in its accessors, such as an `enum` for
a `varchar(50)`: its `ERXConstantClassName`, or else its value class,
and for a flattened attribute, the class of the one that it flattens.
A diagram draws a relationship and its inverse as one line, whose ends
say how many of each entity the other reaches. It has the entities of
other models that relationships reach, but no attributes or flattened
relationships. GitHub and GitLab draw it from its text, so a page has no
date or layout in it, and changes only when its model does. They lay a
diagram out in rows, so that one of a large model's entities is too
wide to read: a model with more than 10 entities has only the
diagrams of its entities, unless `modelDiagramLimit`
(`eogen.doc.modelDiagramLimit`, `--model-diagram-limit`) allows more,
or 0 none.
Documentation stays the HTML it is, which Markdown keeps, but goes on
one line in a table, with `|` escaped.

`doc` also writes an index of the pages, `README.md`, which GitHub and
GitLab show with the folder: a table of the models, each with a link to
its page, its number of entities, and the first paragraph of its
documentation. If the folder has a `README.md` of its own, which eogen
didn't write, `doc` leaves it alone, and writes no index.

With `checkDoc` (`eogen.checkDoc`, `--doc --check`), `doc` writes
nothing, and fails if a page would change, or if the folder has a page
that eogen wrote for a model that it no longer documents, which writing
the pages deletes. A page's first line marks it as eogen's, so `doc`
leaves the folder's other files alone, and a template of a project's
own must keep it. Bind the check to `verify`, so
that a build fails when someone changes a model without writing its
page again:

    <execution>
      <id>check-doc</id>
      <phase>verify</phase>
      <goals>
        <goal>doc</goal>
      </goals>
      <configuration>
        <checkDoc>true</checkDoc>
      </configuration>
    </execution>

It documents the project's own models, and with `includeDependencies`
(`eogen.doc.includeDependencies`) its dependencies' too, but no
prototype entity. A page links only to the pages of the models that are
documented. The other parameters are:

* `outputDirectory` (`eogen.doc.output`): the folder of the pages.
* `templateDirectory` (`eogen.doc.templates`): a folder of templates,
  which come before the bundled ones: a page's, such as `model.md.vm`,
  `index.md.vm` or `entityContent.html.vm`, or a file that is copied,
  such as `eomodeldoc.css`.
* `entityURL` (`eogen.doc.entityURL`): a Velocity template for the link
  from each entity's class name, such as to its Javadoc, which sees
  `$entity` and `$model`. It is relative to the page that links it.

As for the classes, a reference that renders with no value is an
error, and nothing is written unless `force` is set. The command line
does the same with `--doc`, `--check`, `--output`, `--doc-templates`,
`--entity-url` and `--include-dependencies`.

### In HTML

With `format` (`eogen.doc.format`, `--doc-format`) set to `html`, `doc`
writes the documentation as WOLips's EOModelDoc did, as a set of HTML
pages: an index of every model and entity, in frames, and a page for
each model, entity and stored procedure, which has its attributes,
relationships, fetch specifications and entity indexes. The pages go in
`target/site/eomodeldoc`, and the goal's default phase is `pre-site`,
so that a project that binds it has them in its Maven site:

    <execution>
      <goals>
        <goal>doc</goal>
      </goals>
      <configuration>
        <format>html</format>
      </configuration>
    </execution>

An entity's page is `<Model>/entities/<Entity>.html`, so an `entityURL`
to the Javadoc is
`../../../apidocs/${entity.className.replace(".", "/")}.html`. Unlike
WOLips's EOModelDoc, `doc` has no OmniGraffle export, whose diagrams
placed entities at random.

The HTML pages are frozen. They reproduce WOLips's EOModelDoc pages,
and get bug fixes, but no new content: what eogen adds to its
documentation goes in the Markdown pages. A project that wants more in
its HTML pages can put its own templates, such as
`entityContent.html.vm`, in `templateDirectory`.

### In the generated classes

The bundled templates put the documentation in the superclasses too,
as Javadoc comments on the class and on the getter of each attribute
and relationship, where an IDE shows it, and a coding agent reads it
with the class:

    /**
     * The <code>Party</code> who placed the order.
     */
    public com.example.shop.Party customer() {

The documentation stays the HTML it is, apart from what would end the
comment or break the source: `*/`; a backslash, since a backslash and a
`u` start a Unicode escape even in a comment; and an `@` at the start
of a line, which would start a block tag. Each becomes a character
reference. A template of your own does the same with `$javadoc`,
wherever it wants a comment:

    #if ($javadoc.has($attribute))
    $javadoc.comment($attribute, "  ")
    #end

`$javadoc.has` says whether a model, entity, attribute, relationship or
other object has documentation. `$javadoc.comment` returns it as a
comment, with each line after the indent that its second argument
gives, and `$javadoc.lines` returns only the comment's lines, for a
template that already has a comment of its own:

    /**
    #if ($javadoc.has($entity))
    $javadoc.lines($entity, "")
     *
    #end
     * Generated from the model. DO NOT EDIT.
     */

WOLips has no `$javadoc`, so there the `#if` is false, and the same
template generates the classes without the comments, which `check` then
catches. The lint reports documentation that a Javadoc build would fail
on (see [Lint](#lint)).

How eogen finds templates
-------------------------
eogen looks for each template in:

1. the project folder (WOLips looks in its working directory, which
   outside Eclipse could be anywhere);
2. the `.eogen` file's `-templatedir`, relative to the project folder;
3. shared templates, which WOLips doesn't have: the jars and folders
   that `--templates` names (`EOGenerator.setTemplatePath`), then
   `eogen/templates/` on eogen's classpath, which for the Maven plugin
   means in its dependencies (see [Sharing templates](#sharing-templates));
4. `/Library/Application Support/WOLips/EOGenerator`, and the
   `WOLips/EOGenerator` folders under the user's home folder that
   WOLips searches;
5. the templates bundled with WOLips: `_Entity.java` and `Entity.java`,
   which are the defaults, `_WonderEntity.java` and `WonderEntity.java`,
   and the Java 1.4 versions; and eogen's own, `Migration.java`, for
   migrations. EOModelDoc's templates are bundled too, and a folder of
   them comes before the shared templates.

### Sharing templates

A team with its own templates can publish them in a jar, at
`eogen/templates/<name>`, and make it a dependency of the plugin, once,
in a parent POM, rather than copy them into each project's
`-templatedir`:

    <plugin>
      <groupId>net.logicsquad</groupId>
      <artifactId>eogen-maven-plugin</artifactId>
      <version>0.5</version>
      <dependencies>
        <dependency>
          <groupId>com.example</groupId>
          <artifactId>example-eogen-templates</artifactId>
          <version>1.0</version>
        </dependency>
      </dependencies>
    </plugin>

Then an `.eogen` file names them with `-javaTemplate` and
`-subclassJavaTemplate`, and needs no `-templatedir`. A project can
still override one with its own copy in its `-templatedir`, and shared
templates come before the WOLips folders, so that a build doesn't
depend on what a machine has installed. The command line takes the
same jars, or folders of templates, with `--templates`.

WOLips in Eclipse can't read templates from a jar, so a project that
relies on them generates with eogen. A project that generates from
Eclipse gets WOLips's own templates instead, which `check` then
catches.

Differences from WOLips
-----------------------
eogen reads `.eogen` files as WOLips does, and supports all of their
options, with these differences:

* A missing `-extension` means `java`, as it does in Eclipse. Without
  Eclipse's preferences, a missing `-javaTemplate` means `_Entity.java`,
  and a missing `-subclassJavaTemplate` means `Entity.java`. In Eclipse,
  your WOLips preferences can name other templates.
* A missing `-subclassDestination` means the `-destination`, as the
  old `veogen` command line had it. WOLips fails.
* A backslash at the end of a line continues it, as in a shell, and a
  line break without one separates two values. WOLips ran the two lines
  together.
* An `.eogen` file with no `-destination` or `-model`, or a `-model`
  that does not exist, is an error. WOLips failed with a
  `NullPointerException`, or generated nothing.
* `-filenameTemplate` is read but ignored, as it is in WOLips.
* Errors mean nothing is written, unless forced. WOLips writes classes
  that don't compile; see [Errors](#errors).
* A file that differs only in its line endings is unchanged, so a
  Windows checkout with Git's `core.autocrlf` is not all stale, and an
  updated file keeps the line endings it had. WOLips compares bytes.
* The bundled templates fetch a to-many relationship to a partial
  entity (`ERXPartial`) with the inverse relationship's key from the
  partial's class, which declares it. WOLips's templates name the class
  of the entity that the partial extends, so the superclass doesn't
  compile. To fix a template of your own, change
  `${relationship.actualDestination.classNameWithDefault}` to
  `${relationship.destination.classNameWithDefault}` in the line that
  sets `inverseQualifier` from the inverse relationship's `_KEY`.
* The bundled templates give the superclass, and the getter of each
  attribute and relationship, their documentation from the model as a
  Javadoc comment (see
  [In the generated classes](#in-the-generated-classes)). WOLips's
  leave it out.

WOLips renders templates with Velocity 1.5, and eogen with Velocity
2.4.1. eogen sets Velocity 2 up to behave as 1.5 does: `bc` space
gobbling, no empty check in `#if`, hyphens allowed in identifiers,
Velocity 1 macro behaviour, and no conversion of method arguments. On
top of those settings:

* `$object.property` finds `getProperty()`, `get("property")` or
  `isProperty()`, as Velocity 1.5 does. Velocity 2 also calls a method
  named `property()`. That made WOLips's own `_WonderEntity.java`
  template call `EOFetchSpecification.fetchEnterpriseObjects()`, which
  changes the fetch specification, instead of
  `isFetchEnterpriseObjects()`, and generate the wrong return type for
  every fetch specification's methods.
* `$velocityCount` counts `#foreach` iterations.
* `#set` with a null value leaves the reference as it was.
* A template file's byte order mark is kept, and so appears in the
  generated file, as it does with WOLips.

`src/test/resources/velocity` in `eogen-core` checks 31 templates
against Velocity 1.5's own output. These still differ:

* methods on arrays, such as `$array.size()`, which Velocity 1.5 prints
  as they stand;
* `#foreach` over an `Iterable` that is not a `Collection`, which
  Velocity 1.5 skips;
* a macro called with too few arguments, which Velocity 1.5 skips;
* `==` between two null references, and `!=` between a null and a
  value, which Velocity 1.5 treats as errors and evaluates as false;
* `#set` of a property to null, such as `#set($map.key = $null)`, which
  Velocity 1.5 skips;
* `#set` with a formal reference such as `${x}` on its left, which
  Velocity 1.5 ignores.

None of them occurs in the templates that WOLips bundles.

Testing
-------
`mvn install` builds everything and runs:

* `eogen-core`'s tests, which compare eogen's output for a test model
  with WOLips's, apart from where eogen's templates differ on purpose.
  The model has inheritance, flattened attributes and relationships, a
  fetch specification with bindings and prototypes from a jar.
  `src/test/oracle/run.sh` produced the expected output with WOLips
  5.0.3's own jars and Velocity 1.5, and can produce it again from an
  Eclipse install with WOLips.
* `eogen-core`'s corpus tests, which do the same for eleven models, ten
  of them Project Wonder's: every superclass must be byte for byte what
  WOLips generates, with no errors and no lint findings, apart from
  where eogen's templates differ on purpose, as
  `src/test/oracle/differences.patch` records. See
  `src/test/resources/corpus/README.md`.
* `eogen-maven-plugin`'s integration tests, which run each goal in
  Maven with [ITF](https://github.com/khmarbaise/maven-it-extension).
* `eogen-cli`'s tests.

The golden tests run the installed plugin on copies of real projects,
and only when you name the projects and the folder that holds them:

    mvn install -Deogen.golden.projects=<folder> \
        -Deogen.golden.names=<project>,<project>

For each project, they copy `pom.xml` and `src/main` into
`eogen-golden-tests/target/golden`, delete every `_*.java`, and check
that `eogen:generate` recreates each one byte for byte, that
`eogen:check` passes and then catches a stale file, and that
`eogen:verify` passes. They never write to the projects themselves.

With `-Dcoverage`, the build writes a JaCoCo report of each module's
coverage by its unit tests to its `target/site/jacoco`. CI keeps the
reports from its JDK 17 build on Linux as the `coverage` artifact. The
plugin's integration tests run in a separate Maven, so its report shows
no coverage.

Building
--------
eogen needs Java 11 or later. The plugin is tested with Maven 3.9. CI
builds it with JDKs 11, 17, 21 and 25 on Linux, and 17 on Windows and
macOS.

    mvn install

Licence
-------
Logic Squad's code is distributed under the BSD 2-Clause licence in
[LICENSE](LICENSE).

The code extracted from [WOLips](https://github.com/wocommunity/wolips)
is distributed under the ObjectStyle Group Software License, Version
1.0, and each of its files keeps that licence's header.
[NOTICE](NOTICE) lists those files.

This product includes software developed by the ObjectStyle Group
(http://objectstyle.org/).

`ListTool` and `SetTool` come from Apache Velocity Tools, through
WOLips, and are distributed under the
[Apache License, Version 2.0](licenses/Apache-2.0.txt).

<details>
<summary>The ObjectStyle Group Software License, Version 1.0</summary>

    Copyright (c) 2006 The ObjectStyle Group and individual authors of the
    software. All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are
    met:

    1. Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.

    3. The end-user documentation included with the redistribution, if any,
       must include the following acknowlegement: "This product includes
       software developed by the ObjectStyle Group
       (http://objectstyle.org/)." Alternately, this acknowlegement may
       appear in the software itself, if and wherever such third-party
       acknowlegements normally appear.

    4. The names "ObjectStyle Group" and "Cayenne" must not be used to
       endorse or promote products derived from this software without prior
       written permission. For written permission, please contact
       andrus@objectstyle.org.

    5. Products derived from this software may not be called "ObjectStyle"
       nor may "ObjectStyle" appear in their names without prior written
       permission of the ObjectStyle Group.

    THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
    WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
    MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
    NO EVENT SHALL THE OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR
    ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
    DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
    OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
    HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
    STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
    ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    POSSIBILITY OF SUCH DAMAGE.

    This software consists of voluntary contributions made by many
    individuals on behalf of the ObjectStyle Group. For more information on
    the ObjectStyle Group, please see <http://objectstyle.org/>.

</details>

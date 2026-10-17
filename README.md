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
Entity Modeler. On three real projects, eogen generates every committed
superclass byte for byte, 125 in all.

There are three modules:

* `eogen-core`: the library.
* `eogen-maven-plugin`: the `generate`, `check` and `verify` goals.
* `eogen-cli`: a command line that generates from one `.eogen` file.

Using the Maven plugin
----------------------
Add the plugin to the `build/plugins` section of your project's POM.
This binds `check` to `verify`, so that a build fails if anyone changes
a model without regenerating its classes:

    <plugin>
      <groupId>net.logicsquad</groupId>
      <artifactId>eogen-maven-plugin</artifactId>
      <version>0.1</version>
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
  errors, and fails the build (see [Errors](#errors)).
* `check` (default phase `verify`): generates into a scratch folder,
  and fails the build if `generate` would change anything: a
  superclass that differs, or a missing superclass or subclass. It
  names each stale file, and changes nothing in the project. Errors
  fail it too.
* `verify` (default phase `verify`): loads the project's model group,
  resolves and verifies it as Entity Modeler does, reports errors and
  warnings, and fails the build on errors.

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
* For `verify`, `includeDependencies` (`eogen.verify.includeDependencies`)
  also reports failures in models from dependencies, and
  `failOnWarning` (`eogen.verify.failOnWarning`) fails the build on
  warnings too.

`mvn eogen:generate` works in any project whose POM declares the
plugin. Elsewhere, use the full name,
`mvn net.logicsquad:eogen-maven-plugin:0.1:generate`, or add
`net.logicsquad` to the `pluginGroups` in `~/.m2/settings.xml`.

Using the command line
----------------------
`eogen-cli`'s jar contains everything it needs:

    java -jar eogen-cli-0.1.jar [options] <file.eogen>

    --project <folder>    The folder that the .eogen file's paths are
                          relative to. Default: the current folder.
    --models <folders>    Folders whose models make up the model group,
                          separated by ':' (';' on Windows). Default:
                          <project>/src/main/resources.
    --classpath <path>    Jars and folders whose Resources folders add
                          their models to the model group, separated by
                          ':' (';' on Windows).
    --check               Write nothing. List the files that generating
                          would change, and exit with status 1 if there
                          are any.
    --force               Write the classes even when there are errors,
                          as WOLips does. Without it, errors mean
                          nothing is written, and the exit status is 2.

To give it a Maven project's dependencies, run it from the project's
folder with the classpath that Maven resolves:

    mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
    java -jar eogen-cli-0.1.jar --classpath "$(cat cp.txt)" \
        src/main/resources/Library.eogen

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
`setForce(true)` was called.
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

How eogen finds templates
-------------------------
As WOLips does, eogen looks for each template in:

1. the project folder (WOLips looks in its working directory, which
   outside Eclipse could be anywhere);
2. the `.eogen` file's `-templatedir`, relative to the project folder;
3. `/Library/Application Support/WOLips/EOGenerator`, and the
   `WOLips/EOGenerator` folders under the user's home folder that
   WOLips searches;
4. the templates bundled with WOLips: `_Entity.java` and `Entity.java`,
   which are the defaults, `_WonderEntity.java` and `WonderEntity.java`,
   and the Java 1.4 versions.

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
  with WOLips's. The model has inheritance, flattened attributes and
  relationships, a fetch specification with bindings and prototypes
  from a jar. `src/test/oracle/run.sh` produced the expected output
  with WOLips 5.0.3's own jars and Velocity 1.5, and can produce it
  again from an Eclipse install with WOLips.
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

Building
--------
eogen needs Java 11 or later. The plugin is tested with Maven 3.9.

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

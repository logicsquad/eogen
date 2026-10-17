The test corpus
===============

`CorpusTest` generates from every `.eogen` file in
`project/src/main/resources`, and compares each superclass with the one
that WOLips 5.0.3 generated from the same file, in `expected/<name of
the .eogen file>`. `src/test/oracle/run.sh` produces `expected` with
WOLips's own jars and Velocity 1.5, then applies
`src/test/oracle/differences.patch` where eogen's bundled templates
differ from WOLips's on purpose.

The models are Project Wonder's, from
https://github.com/wocommunity/wonder at commit bd44b6ce2 (2022-10-23),
under the NetStruxr Public Software License in `LICENSE.NPL`:

| Model | From | What it has |
|---|---|---|
| Movies, Rentals | ERMoviesLogic | raw-row fetch specifications, flattened relationships, a dependency between models |
| BugTracker | BTBusinessLogic | inheritance, restricting qualifiers, factory methods |
| ERAttachment | ERAttachment | single-table inheritance, an abstract entity |
| ERCoreBusinessLogic, ERMail | ERCoreBusinessLogic | inheritance, fetch specifications |
| ERIndexing | ERIndexing | `ERXConstantClassName` |
| ERXPartialBase, ERXPartialExample | ERXPartials | partial entities |
| ERTaggable | ERTaggable | a model of one entity |
| erprototypes (in `framework`) | ERPrototypes | the prototypes, as a dependency |

`project/src/main/templates/wonder` holds Project Wonder's own
`_WonderEntity.java` and `WonderEntity.java`, from its `EOTemplates`
folders.

`Ledger` is eogen's own, written as Entity Modeler writes a model, for
an entity index and stored procedures, which Project Wonder's models
don't have.

Each model has a `<Model>Wonder.eogen`, which uses the
`_WonderEntity.java` that WOLips bundles. Some also have a
`<Model>Classic.eogen`, which uses WOLips's `_Entity.java`, or a
`<Model>WonderTemplates.eogen`, which uses Project Wonder's own.

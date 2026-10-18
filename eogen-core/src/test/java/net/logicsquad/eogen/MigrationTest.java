package net.logicsquad.eogen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.logicsquad.eogen.migration.MigrationOptions;
import net.logicsquad.eogen.migration.MigrationResult;
import net.logicsquad.eogen.migration.MigrationResult.ModelMigration;
import net.logicsquad.eogen.migration.MigrationResult.Status;

/**
 * Generates migrations for a copy of the fixture project, whose Shop model
 * has one migration, {@code Shop0}, after changing its model in one way at a
 * time, and compares them with the earlier version of the model, a copy of
 * the fixture's. Each migration must compile against stubs of Project
 * Wonder's API, but at its gaps.
 */
class MigrationTest {
	private static final String ORDER = "Shop.eomodeld/Order.plist";

	private static final String ATTRIBUTES = "    attributes = (\n";

	@TempDir
	Path temp;

	private FixtureProject fixture;

	private Path earlier;

	@BeforeEach
	void copyFixture() throws IOException {
		fixture = FixtureProject.copyTo(temp);
		earlier = temp.resolve("earlier");
		FixtureProject.copyTree(fixture.resolve("src/main/resources"), earlier);
		Path existing = fixture.resolve("src/main/java/com/example/shop/migrations/Shop0.java");
		Files.createDirectories(existing.getParent());
		Files.write(existing, Arrays.asList("package com.example.shop.migrations;", "", "public class Shop0 {", "}"));
	}

	@AfterEach
	void releaseJars() throws IOException {
		Jars.release(temp);
	}

	@Test
	void writesNothingWhenNoTableChanged() throws Exception {
		MigrationResult result = generate();
		assertThat(result.getFindings()).isEmpty();
		assertThat(result.getMigrations()).extracting(ModelMigration::getModel, ModelMigration::getStatus).containsExactly(tuple("Shop", Status.UNCHANGED));
		assertThat(result.getSince()).isEqualTo("the models in " + earlier.toString().replace(File.separatorChar, '/'));
		assertThat(fixture.resolve("src/main/java/com/example/shop/migrations/Shop1.java")).doesNotExist();
	}

	@Test
	void writesNothingForAChangeThatNoTableHas() throws Exception {
		// A class property, a fetch specification or a rename of an attribute
		// alone leaves the tables as they are.
		edit(ORDER, "classProperties = (currency, customer, customerName, lines, note, placed, total);", "classProperties = (currency, customer, lines, note, placed, total);");
		edit(ORDER, "name = currencyCode;", "name = currencyCodeString;");
		edit(ORDER, "attributesUsedForLocking = (currencyCode,", "attributesUsedForLocking = (currencyCodeString,");
		edit(ORDER, "sourceAttribute = currencyCode;", "sourceAttribute = currencyCodeString;");
		assertThat(generate().getMigrations()).extracting(ModelMigration::getStatus).containsExactly(Status.UNCHANGED);
	}

	@Test
	void addsAColumnThatAllowsNull() throws Exception {
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = \"gift_message\"; name = giftMessage; prototypeName = varchar100; }, \n");
		MigrationResult result = generate();
		ModelMigration migration = created(result);
		assertThat(migration.getClassName()).isEqualTo("com.example.shop.migrations.Shop1");
		assertThat(migration.getNumber()).isEqualTo(1);
		assertThat(migration.isFirst()).isFalse();
		assertThat(migration.getFile()).isEqualTo(fixture.resolve("src/main/java/com/example/shop/migrations/Shop1.java").toFile());
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"orderTable.newStringColumn(\"gift_message\", 100, ALLOWS_NULL);");
		assertThat(read(result)).contains("package com.example.shop.migrations;", "public class Shop1 extends ERXMigrationDatabase.Migration {",
				" * <li>Adds gift_message to shop_order.</li>");
		assertCompilesButAtGaps(result);
	}

	@Test
	void leavesAGapForTheValueOfANewColumnThatDoesNotAllowNull() throws Exception {
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = N; columnName = channel; name = channel; prototypeName = varchar100; }, \n");
		MigrationResult result = generate();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"// TODO eogen: shop_order may have rows, which need a value for channel, a new column that doesn't",
				"// allow null. Replace EOGEN_GAP with it.",
				"orderTable.newStringColumn(\"channel\", 100, NOT_NULL, EOGEN_GAP);");
		assertThat(result.getFindings()).hasSize(1);
		Finding gap = result.getFindings().get(0);
		assertThat(gap.getSeverity()).isEqualTo(Finding.Severity.WARNING);
		assertThat(gap.getCode()).isEqualTo(Finding.MIGRATION_GAP);
		assertThat(gap.getFile()).isEqualTo("src/main/java/com/example/shop/migrations/Shop1.java");
		assertThat(gap.getModel()).isEqualTo("Shop");
		assertThat(gap.getEntity()).isEqualTo("Order");
		assertThat(gap.getObject()).isEqualTo("column channel");
		assertThat(gap.getMessage()).isEqualTo("src/main/java/com/example/shop/migrations/Shop1.java:" + gap.getLine()
				+ ": shop_order may have rows, which need a value for channel, a new column that doesn't allow null.");
		assertThat(lines(result).get(gap.getLine() - 1)).contains("EOGEN_GAP);");
		assertThat(created(result).getGaps()).isEqualTo(1);
		assertCompilesButAtGaps(result);
	}

	@Test
	void givesANewColumnItsAttributesDefault() throws Exception {
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = N; columnName = channel; name = channel; prototypeName = varchar100; userInfo = {default = web; }; }, \n"
				+ "        {allowsNull = N; columnName = gift; name = gift; prototypeName = boolean; userInfo = {default = false; }; }, \n"
				+ "        {allowsNull = N; columnName = discount; name = discount; prototypeName = amount; userInfo = {default = \"0.00\"; }; }, \n"
				+ "        {allowsNull = N; columnName = updated; name = updated; prototypeName = dateTime; userInfo = {default = now; }; }, \n"
				+ "        {allowsNull = N; columnName = priority; name = priority; prototypeName = intNumber; userInfo = {default = 3; }; }, \n");
		MigrationResult result = generate();
		assertThat(result.getFindings()).isEmpty();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"orderTable.newStringColumn(\"channel\", 100, NOT_NULL, \"web\");",
				"orderTable.newBigDecimalColumn(\"discount\", 12, 2, NOT_NULL, new BigDecimal(\"0.00\"));",
				"orderTable.newBooleanColumn(\"gift\", NOT_NULL, false);",
				"orderTable.newIntegerColumn(\"priority\", NOT_NULL, 3);",
				"orderTable.newTimestampColumn(\"updated\", NOT_NULL, ERXTimestampUtilities.timestampForString(\"now\"));");
		assertThat(read(result)).contains("import java.math.BigDecimal;", "import er.extensions.foundation.ERXTimestampUtilities;");
		assertCompilesButAtGaps(result);
	}

	@Test
	void deletesARemovedColumn() throws Exception {
		edit(ORDER, "        {columnName = note; name = note; prototypeName = longText; }, \n", "");
		edit(ORDER, "lines, note, placed", "lines, placed");
		MigrationResult result = generate();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"orderTable.existingColumnNamed(\"note\").delete();");
		assertCompilesButAtGaps(result);
	}

	@Test
	void renamesTheColumnOfAnAttributeThatKeepsItsName() throws Exception {
		edit(ORDER, "{columnName = note; name = note;", "{columnName = remarks; name = note;");
		MigrationResult result = generate();
		assertThat(result.getFindings()).isEmpty();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"orderTable.existingColumnNamed(\"note\").renameTo(\"remarks\");");
		assertCompilesButAtGaps(result);
	}

	@Test
	void asksWhetherAColumnDroppedWhileAnotherIsAddedIsRenamed() throws Exception {
		edit(ORDER, "{columnName = note; name = note; prototypeName = longText; }", "{allowsNull = Y; columnName = remarks; name = remarks; prototypeName = longText; }");
		edit(ORDER, "lines, note, placed", "lines, placed, remarks");
		MigrationResult result = generate();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"orderTable.newLargeStringColumn(\"remarks\", ALLOWS_NULL);",
				"// TODO eogen: Is remarks, a new column in shop_order, note renamed? Then rename it, with",
				"// orderTable.existingColumnNamed(\"note\").renameTo(\"remarks\"), rather than delete it and add remarks.",
				"// Either way, delete this line.",
				"EOGEN_GAP;",
				"orderTable.existingColumnNamed(\"note\").delete();");
		assertThat(result.getFindings()).extracting(Finding::getObject).containsExactly("column note");
		assertCompilesButAtGaps(result);
	}

	@Test
	void changesWhetherAColumnAllowsNull() throws Exception {
		edit(ORDER, "{columnName = note; name = note;", "{allowsNull = Y; columnName = note; name = note;");
		editEarlier(ORDER, "{allowsNull = N; columnName = placed;", "{allowsNull = Y; columnName = placed;");
		MigrationResult result = generate();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"orderTable.existingColumnNamed(\"note\").setAllowsNull(ALLOWS_NULL);",
				"// TODO eogen: The rows of shop_order where placed is null need a value before it can be made not to",
				"// allow null. Set it, or delete this line if there are none.",
				"EOGEN_GAP;",
				"orderTable.existingColumnNamed(\"placed\").setAllowsNull(NOT_NULL);");
		assertCompilesButAtGaps(result);
	}

	@Test
	void changesAColumnsWidthPrecisionAndScale() throws Exception {
		edit(ORDER, "name = currencyCode; \n", "name = currencyCode; \n            width = 3; \n");
		edit(ORDER, "{allowsNull = N; columnName = total; name = total; prototypeName = amount; }", "{allowsNull = N; columnName = total; name = total; precision = 14; prototypeName = amount; scale = 4; }");
		MigrationResult result = generate();
		assertThat(result.getFindings()).isEmpty();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"orderTable.existingColumnNamed(\"currency_code\").setWidthType(Types.VARCHAR, 3, null);",
				"orderTable.existingColumnNamed(\"total\").setNumericType(Types.DECIMAL, 4, 14, null);");
		assertThat(read(result)).contains("import java.sql.Types;");
		assertCompilesButAtGaps(result);
	}

	@Test
	void leavesAGapForAColumnWhoseTypeChanges() throws Exception {
		edit("Shop.eomodeld/Product.plist", "            prototypeName = intNumber; \n", "            prototypeName = varchar100; \n");
		MigrationResult result = generate();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable productTable = database.existingTableNamed(\"product\");",
				"// TODO eogen: category in product changes from newIntegerColumn to newStringColumn(100). Change it,",
				"// such as with",
				"// productTable.existingColumnNamed(\"category\").setDataType(Types.VARCHAR, 0, 0, 100, null),",
				"// converting its values if need be, then delete this line.",
				"EOGEN_GAP;");
		assertCompilesButAtGaps(result);
	}

	@Test
	void leavesAGapForAColumnWhoseTypeHasNoCall() throws Exception {
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = gid; externalType = uuid; name = gid; valueClassName = NSString; }, \n");
		MigrationResult result = generate();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"// TODO eogen: eogen can't tell which column to make for Order.gid, whose class is String and external",
				"// type uuid. Add gid, then delete this line.",
				"EOGEN_GAP;");
		assertCompilesButAtGaps(result);
	}

	@Test
	void createsANewTableWithItsForeignKeysAndIndexes() throws Exception {
		addShipment(fixture.resolve("src/main/resources"));
		MigrationResult result = generate();
		assertThat(result.getFindings()).isEmpty();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable shipmentTable = database.newTableNamed(\"shipment\");",
				"shipmentTable.newStringColumn(\"carrier\", 100, ALLOWS_NULL);",
				"shipmentTable.newIntegerColumn(\"id\", NOT_NULL);",
				"shipmentTable.newIntegerColumn(\"order_id\", NOT_NULL);",
				"shipmentTable.newTimestampColumn(\"shipped_at\", NOT_NULL);",
				"shipmentTable.create();",
				"shipmentTable.setPrimaryKey(\"id\");",
				"shipmentTable.addIndex(\"shipment_order\", new ColumnIndex(\"order_id\"), new ColumnIndex(\"shipped_at\"));",
				"",
				"shipmentTable.addForeignKey(\"order_id\", \"shop_order\", \"id\");");
		assertThat(read(result)).contains("import er.extensions.jdbc.ERXSQLHelper.ColumnIndex;");
		assertCompilesButAtGaps(result);
	}

	@Test
	void dropsARemovedTable() throws Exception {
		addShipment(earlier);
		MigrationResult result = generate();
		assertThat(result.getFindings()).isEmpty();
		assertThat(upgrade(result)).containsExactly("database.existingTableNamed(\"shipment\").drop();");
		assertCompilesButAtGaps(result);
	}

	@Test
	void renamesTheTableOfAnEntityThatKeepsItsName() throws Exception {
		edit("Shop.eomodeld/Tag.plist", "externalName = tag;", "externalName = label;");
		MigrationResult result = generate();
		// The join table's foreign key to it stays as it is.
		assertThat(result.getFindings()).isEmpty();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable tagTable = database.existingTableNamed(\"tag\");",
				"tagTable.renameTo(\"label\");");
		assertCompilesButAtGaps(result);
	}

	@Test
	void addsTheForeignKeyOfANewToOneRelationshipOnceItsColumnExists() throws Exception {
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = \"referrer_id\"; name = referrerID; prototypeName = id; }, \n");
		edit(ORDER, "    relationships = (\n", "    relationships = (\n        {destination = Party; isToMany = N; joinSemantic = EOInnerJoin; joins = ({destinationAttribute = id; sourceAttribute = referrerID; }); name = referrer; }, \n");
		MigrationResult result = generate();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"orderTable.newIntegerColumn(\"referrer_id\", ALLOWS_NULL);",
				"",
				"orderTable.addForeignKey(\"referrer_id\", \"party\", \"id\");");
		assertCompilesButAtGaps(result);
	}

	@Test
	void leavesAGapForAForeignKeyThatWonderCannotDrop() throws Exception {
		editEarlier(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = \"referrer_id\"; name = referrerID; prototypeName = id; }, \n");
		editEarlier(ORDER, "    relationships = (\n", "    relationships = (\n        {destination = Party; isToMany = N; joinSemantic = EOInnerJoin; joins = ({destinationAttribute = id; sourceAttribute = referrerID; }); name = referrer; }, \n");
		MigrationResult result = generate();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"// TODO eogen: Wonder has no call to drop the foreign key from shop_order(referrer_id) to party(id).",
				"// Drop it with SQL, or delete this line if the database has none, or drops it with its column.",
				"EOGEN_GAP;",
				"orderTable.existingColumnNamed(\"referrer_id\").delete();");
		assertThat(result.getFindings()).extracting(Finding::getObject).containsExactly("foreign key Order.referrer");
		assertCompilesButAtGaps(result);
	}

	@Test
	void addsIndexesAndLeavesAGapForOneThatIsDropped() throws Exception {
		String index = "    entityIndexes = ({attributes = (name); constraint = distinct; indexType = clustered; name = \"product_name\"; order = asc; }); \n";
		edit("Shop.eomodeld/Product.plist", "    externalName = product; \n", index + "    externalName = product; \n");
		editEarlier("Shop.eomodeld/Tag.plist", "    externalName = tag; \n", index.replace("product_name", "tag_name") + "    externalName = tag; \n");
		MigrationResult result = generate();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable productTable = database.existingTableNamed(\"product\");",
				"productTable.addUniqueIndex(\"product_name\", \"name\");",
				"",
				"ERXMigrationTable tagTable = database.existingTableNamed(\"tag\");",
				"// TODO eogen: Wonder has no call to drop the index tag_name from tag. Drop it with SQL, or delete",
				"// this line if the database has none.",
				"EOGEN_GAP;");
		assertCompilesButAtGaps(result);
	}

	@Test
	void addsASubentitysColumnToItsParentsTableAllowingNull() throws Exception {
		edit("Shop.eomodeld/Person.plist", ATTRIBUTES, ATTRIBUTES + "        {allowsNull = N; columnName = nickname; name = nickname; prototypeName = varchar100; }, \n");
		MigrationResult result = generate();
		assertThat(result.getFindings()).isEmpty();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable partyTable = database.existingTableNamed(\"party\");",
				"partyTable.newStringColumn(\"nickname\", 100, ALLOWS_NULL);");
		assertCompilesButAtGaps(result);
	}

	@Test
	void writesTheFirstMigrationOfAModelThatHasNone() throws Exception {
		Files.delete(fixture.resolve("src/main/java/com/example/shop/migrations/Shop0.java"));
		MigrationOptions options = options();
		options.setModels(Collections.singletonList("Shop"));
		MigrationResult result = fixture.generator().generateMigrations(options, true);
		ModelMigration migration = created(result);
		assertThat(migration.isFirst()).isTrue();
		// With no migration to follow, it goes in the package of the entity
		// classes.
		assertThat(migration.getClassName()).isEqualTo("com.example.shop.Shop0");
		assertThat(read(result)).isEqualTo(new String(Files.readAllBytes(expected().resolve("Shop0.java")), StandardCharsets.UTF_8));
		assertCompilesButAtGaps(result);
	}

	@Test
	void putsAModelsFirstMigrationBesideTheOtherModelsMigrations() throws Exception {
		Path notes = Files.createDirectories(fixture.resolve("src/main/resources/Notes.eomodeld"));
		Files.write(notes.resolve("index.eomodeld"), Arrays.asList("{", "    EOModelVersion = \"2.1\"; ", "    adaptorName = JDBC; ", "    connectionDictionary = {}; ",
				"    entities = ({className = \"com.example.notes.Note\"; name = Note; }); ", "}"));
		Files.write(notes.resolve("Note.plist"), Arrays.asList("{",
				"    attributes = ({allowsNull = N; columnName = id; name = id; prototypeName = id; }, {allowsNull = Y; columnName = text; name = text; prototypeName = longText; }); ",
				"    attributesUsedForLocking = (id); ", "    className = \"com.example.notes.Note\"; ", "    classProperties = (text); ", "    externalName = note; ", "    name = Note; ",
				"    primaryKeyAttributes = (id); ", "}"));
		MigrationOptions options = options();
		options.setModels(Arrays.asList("Notes", "Shop"));
		MigrationResult result = fixture.generator().generateMigrations(options, true);
		assertThat(result.getMigrations()).extracting(ModelMigration::getModel, ModelMigration::getStatus, ModelMigration::getClassName)
				.containsExactly(tuple("Notes", Status.CREATED, "com.example.shop.migrations.Notes0"), tuple("Shop", Status.UNCHANGED, null));
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable noteTable = database.newTableNamed(\"note\");",
				"noteTable.newIntegerColumn(\"id\", NOT_NULL);",
				"noteTable.newLargeStringColumn(\"text\", ALLOWS_NULL);",
				"noteTable.create();",
				"noteTable.setPrimaryKey(\"id\");");
		assertCompilesButAtGaps(result);
	}

	/**
	 * Shop's first migration needs Reference's currency table, but the
	 * Reference jar has no migrations, so the dependency is a gap, as
	 * {@code Shop0.java} shows. A jar with them gives the highest.
	 */
	@Test
	void dependsOnTheLatestMigrationOfAModelInADependency() throws Exception {
		Files.delete(fixture.resolve("src/main/java/com/example/shop/migrations/Shop0.java"));
		Path reference = temp.resolve("reference");
		Files.write(reference.resolve("Resources/Properties"), Collections.singletonList("Reference.MigrationClassPrefix=RefSchema"));
		Path sources = Files.createDirectories(temp.resolve("reference-sources/com/example/reference"));
		Files.write(sources.resolve("RefSchema3.java"), Arrays.asList("package com.example.reference;", "", "import com.webobjects.eocontrol.EOEditingContext;", "",
				"import er.extensions.migration.ERXMigrationDatabase;", "", "public class RefSchema3 extends ERXMigrationDatabase.Migration {",
				"\tpublic void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) {", "\t}", "",
				"\tpublic void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) {", "\t}", "}"));
		// Not a migration, though its name looks like one.
		Files.write(sources.resolve("RefSchema9.java"), Arrays.asList("package com.example.reference;", "", "public class RefSchema9 {", "}"));
		compile(reference, sources.resolve("RefSchema3.java"), sources.resolve("RefSchema9.java"));
		FixtureProject.jar(reference, temp.resolve("reference.jar"));
		MigrationOptions options = options();
		options.setModels(Collections.singletonList("Shop"));
		MigrationResult result = fixture.generator().generateMigrations(options, true);
		assertThat(result.getFindings()).isEmpty();
		assertThat(read(result)).contains("import com.webobjects.foundation.NSArray;", "import er.extensions.migration.ERXModelVersion;",
				"\t@Override\n\tpublic NSArray<ERXModelVersion> modelDependencies() {\n\t\treturn new NSArray<ERXModelVersion>(new ERXModelVersion(\"Reference\", 3));\n\t}\n");
		assertCompilesButAtGaps(result);
	}

	@Test
	void dependsOnTheLatestMigrationOfAnotherModelOfTheProject() throws Exception {
		addNotes(fixture.resolve("src/main/resources"));
		addNotes(earlier);
		Files.write(fixture.resolve("src/main/java/com/example/shop/migrations/Notes4.java"), Arrays.asList("package com.example.shop.migrations;", "", "public class Notes4 {", "}"));
		addMemo();
		MigrationResult result = generate();
		assertThat(result.getMigrations()).extracting(ModelMigration::getModel, ModelMigration::getStatus).containsExactly(tuple("Notes", Status.UNCHANGED), tuple("Shop", Status.CREATED));
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"orderTable.newIntegerColumn(\"memo_id\", ALLOWS_NULL);",
				"",
				"orderTable.addForeignKey(\"memo_id\", \"note\", \"id\");");
		assertThat(read(result)).contains("\t\treturn new NSArray<ERXModelVersion>(new ERXModelVersion(\"Notes\", 4));\n");
		assertCompilesButAtGaps(result);
	}

	/**
	 * Shop's first migration needs Notes's note table, and Reference's
	 * currency table, whose version is a gap.
	 */
	@Test
	void dependsOnEachModelWhoseTablesItNeeds() throws Exception {
		Files.delete(fixture.resolve("src/main/java/com/example/shop/migrations/Shop0.java"));
		addNotes(fixture.resolve("src/main/resources"));
		Files.write(fixture.resolve("src/main/java/com/example/shop/migrations/Notes4.java"), Arrays.asList("package com.example.shop.migrations;", "", "public class Notes4 {", "}"));
		addMemo();
		MigrationOptions options = options();
		options.setModels(Collections.singletonList("Shop"));
		MigrationResult result = fixture.generator().generateMigrations(options, true);
		assertThat(result.getFindings()).extracting(Finding::getCode, Finding::getObject).containsExactly(tuple(Finding.MIGRATION_GAP, "dependency on Reference"));
		assertThat(read(result)).contains("\t\treturn new NSArray<ERXModelVersion>(new ERXModelVersion[] {\n\t\t\t\tnew ERXModelVersion(\"Notes\", 4),\n"
				+ "\t\t\t\tnew ERXModelVersion(\"Reference\", EOGEN_GAP) });\n");
		assertThat(created(result).getGaps()).isEqualTo(1);
		assertCompilesButAtGaps(result);
	}

	@Test
	void dependsOnNoModelThatWonderDoesNotMigrate() throws Exception {
		Files.delete(fixture.resolve("src/main/java/com/example/shop/migrations/Shop0.java"));
		Files.write(fixture.resolve("src/main/resources/Properties"), Collections.singletonList("er.migration.skipModelNames=Reference"));
		MigrationOptions options = options();
		options.setModels(Collections.singletonList("Shop"));
		MigrationResult result = fixture.generator().generateMigrations(options, true);
		assertThat(result.getFindings()).isEmpty();
		assertThat(read(result)).doesNotContain("modelDependencies", "ERXModelVersion").contains("orderTable.addForeignKey(\"currency_code\", \"currency\", \"code\");");
	}

	@Test
	void writesNoFirstMigrationForAModelThatIsNotNamedAndHasNotChanged() throws Exception {
		Files.delete(fixture.resolve("src/main/java/com/example/shop/migrations/Shop0.java"));
		MigrationResult result = generate();
		assertThat(result.getMigrations()).extracting(ModelMigration::getStatus, ModelMigration::getReason).containsExactly(tuple(Status.UNCHANGED,
				"Its tables haven't changed since " + result.getSince() + ", and it has no migrations: name it to write its first, Shop0, which creates them."));
	}

	@Test
	void numbersAfterTheHighestMigrationAndAVendorSpecificOne() throws Exception {
		Path migrations = fixture.resolve("src/main/java/com/example/shop/migrations");
		Files.write(migrations.resolve("Shop1.java"), Arrays.asList("package com.example.shop.migrations;", "public class Shop1 {", "}"));
		Files.write(migrations.resolve("ShopPostgreSQL2.java"), Arrays.asList("package com.example.shop.migrations;", "public class ShopPostgreSQL2 extends ERXMigrationDatabase.Migration {", "}"));
		// Not a migration, though its name looks like one.
		Files.write(fixture.resolve("src/main/java/com/example/shop/ShopItem7.java"), Arrays.asList("package com.example.shop;", "public class ShopItem7 {", "}"));
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = \"gift_message\"; name = giftMessage; prototypeName = varchar100; }, \n");
		assertThat(created(generate()).getClassName()).isEqualTo("com.example.shop.migrations.Shop3");
	}

	@Test
	void namesMigrationsAsTheProjectsPropertiesSay() throws Exception {
		Files.write(fixture.resolve("src/main/resources/Properties"), Arrays.asList("Shop.MigrationClassPrefix=com.example.migration.ShopMigration", "Shop.InitialMigrationVersion=4"));
		MigrationOptions options = options();
		options.setModels(Collections.singletonList("Shop"));
		ModelMigration migration = created(fixture.generator().generateMigrations(options, true));
		// Shop0 isn't one of its migrations, which ERXMigrator looks for as
		// com.example.migration.ShopMigration5 and on.
		assertThat(migration.getClassName()).isEqualTo("com.example.migration.ShopMigration5");
		assertThat(migration.isFirst()).isTrue();
		assertThat(migration.getFile()).isEqualTo(fixture.resolve("src/main/java/com/example/migration/ShopMigration5.java").toFile());
	}

	@Test
	void skipsAModelThatWonderDoesNotMigrate() throws Exception {
		Files.write(fixture.resolve("src/main/resources/Properties"), Collections.singletonList("er.migration.skipModelNames=Reference, Shop"));
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = \"gift_message\"; name = giftMessage; prototypeName = varchar100; }, \n");
		assertThat(generate().getMigrations()).extracting(ModelMigration::getStatus, ModelMigration::getReason)
				.containsExactly(tuple(Status.SKIPPED, "er.migration.skipModelNames names it."));
	}

	@Test
	void putsMigrationsInTheGivenPackage() throws Exception {
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = \"gift_message\"; name = giftMessage; prototypeName = varchar100; }, \n");
		MigrationOptions options = options();
		options.setPackageName("com.example.db");
		assertThat(created(fixture.generator().generateMigrations(options, true)).getFile()).isEqualTo(fixture.resolve("src/main/java/com/example/db/Shop1.java").toFile());
	}

	@Test
	void writesNothingInADryRun() throws Exception {
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = N; columnName = channel; name = channel; prototypeName = varchar100; }, \n");
		MigrationResult result = fixture.generator().generateMigrations(options(), false);
		assertThat(result.isWritten()).isFalse();
		assertThat(created(result).getFile()).doesNotExist();
		// The gap still has its line.
		assertThat(result.getFindings()).extracting(Finding::getLine).doesNotContainNull();
	}

	@Test
	void comparesWithTheLastCommit() throws Exception {
		Path project = fixture.resolve(".");
		git(project, "init", "-q");
		git(project, "add", ".");
		git(project, "-c", "user.name=eogen", "-c", "user.email=eogen@example.com", "commit", "-q", "-m", "Shop.");
		String commit = git(project, "rev-parse", "--short", "HEAD").trim();
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = \"gift_message\"; name = giftMessage; prototypeName = varchar100; }, \n");
		MigrationOptions options = new MigrationOptions();
		MigrationResult result = fixture.generator().generateMigrations(options, true);
		assertThat(result.getFindings()).isEmpty();
		assertThat(result.getSince()).isEqualTo("commit " + commit);
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"orderTable.newStringColumn(\"gift_message\", 100, ALLOWS_NULL);");
		assertThat(read(result)).contains(" * Changes the tables of the Shop model as it changed since commit " + commit + ":");

		options.setFrom("no-such-branch");
		Finding error = fixture.generator().generateMigrations(options, true).getFindings().get(0);
		assertThat(error.getCode()).isEqualTo(Finding.EARLIER_VERSION);
		assertThat(error.getMessage()).startsWith("no-such-branch is neither a folder nor a commit in ");
	}

	@Test
	void reportsAModelWithMigrationsThatTheEarlierVersionLacks() throws Exception {
		Path empty = Files.createDirectories(temp.resolve("empty"));
		MigrationOptions options = options();
		options.setFrom(empty.toString());
		MigrationResult result = fixture.generator().generateMigrations(options, true);
		assertThat(result.getMigrations()).extracting(ModelMigration::getStatus).containsExactly(Status.SKIPPED);
		assertThat(result.getFindings()).extracting(Finding::getSeverity, Finding::getCode, Finding::getMessage).containsExactly(tuple(Finding.Severity.WARNING, Finding.NOT_COMPARED,
				"Shop has migrations, but the models in " + empty.toString().replace(File.separatorChar, '/') + " has no Shop model, so eogen can't tell what changed."));
	}

	@Test
	void reportsAnEarlierVersionThatFailsToLoad() throws Exception {
		editEarlier(ORDER, "name = Order;", "name = Order");
		MigrationResult result = generate();
		assertThat(result.hasErrors()).isTrue();
		assertThat(result.getMigrations()).isEmpty();
		assertThat(result.getFindings().get(0).getMessage()).startsWith("In the models in ");
	}

	@Test
	void rendersAProjectsOwnTemplate() throws Exception {
		Path template = fixture.resolve("src/main/templates/ShopMigration.java");
		Files.write(template, Arrays.asList("package ${migration.packageName};", "", "#foreach ($line in $migration.importLines([\"com.example.shop.Base\"]))", "$line", "#end", "",
				"public class ${migration.className} extends Base {", "\t@Override", "\tpublic void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {",
				"#foreach ($line in $migration.upgradeLines)", "$line", "#end", "\t}", "", "\t// $migration.gapCount gaps, $migration.notAProperty", "}"));
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = \"gift_message\"; name = giftMessage; prototypeName = varchar100; }, \n");
		MigrationOptions options = options();
		options.setTemplate("src/main/templates/ShopMigration.java");
		MigrationResult result = fixture.generator().generateMigrations(options, true);
		// An unresolved reference is an error, so nothing is written.
		assertThat(result.isWritten()).isFalse();
		assertThat(result.getFindings()).extracting(Finding::getCode).containsExactly(Finding.UNRESOLVED_REFERENCE);
		Files.write(template, new String(Files.readAllBytes(template), StandardCharsets.UTF_8).replace(", $migration.notAProperty", "").getBytes(StandardCharsets.UTF_8));
		result = fixture.generator().generateMigrations(options, true);
		assertThat(result.getFindings()).isEmpty();
		assertThat(read(result)).contains("import com.example.shop.Base;\nimport com.webobjects.eocontrol.EOEditingContext;\n\nimport er.extensions.migration.ERXMigrationDatabase;",
				"public class Shop1 extends Base {", "\t// 0 gaps\n");
	}

	@Test
	void writesTheFirstMigrationsOfTheCorpusModels() throws Exception {
		Path project = temp.resolve("corpus");
		FixtureProject.copyTree(CorpusTest.corpus().resolve("project"), project);
		Path prototypes = temp.resolve("erprototypes.jar");
		FixtureProject.jar(CorpusTest.corpus().resolve("framework"), prototypes);
		EOGenerator generator = new EOGenerator(project.toFile(), Collections.singletonList(project.resolve("src/main/resources").toFile()), Collections.singletonList(prototypes.toFile()));
		MigrationOptions options = new MigrationOptions();
		options.setFrom(Files.createDirectories(temp.resolve("empty")).toString());
		options.setModels(Arrays.asList("BugTracker", "ERAttachment", "ERCoreBusinessLogic", "ERIndexing", "ERMail", "ERTaggable", "ERXPartialBase", "ERXPartialExample", "Ledger", "Movies",
				"Rentals"));
		MigrationResult result = generator.generateMigrations(options, true);
		assertThat(result.getFindings()).isEmpty();
		assertThat(result.getCreated()).hasSize(11);
		for (ModelMigration migration : result.getCreated()) {
			Path expected = CorpusTest.corpus().resolve("migrations/" + migration.getModel() + "0.java");
			assertThat(new String(Files.readAllBytes(migration.getFile().toPath()), StandardCharsets.UTF_8)).as(expected.toString())
					.isEqualTo(new String(Files.readAllBytes(expected), StandardCharsets.UTF_8));
		}
		assertCompilesButAtGaps(result);
	}

	/**
	 * Adds a Shipment entity, whose table has a foreign key to the order's and
	 * an index of two columns, to the Shop model in a folder.
	 */
	private static void addShipment(Path resources) throws IOException {
		Path model = resources.resolve("Shop.eomodeld");
		replace(model.resolve("index.eomodeld"), "        {className = \"com.example.shop.Tag\"; name = Tag; }\n",
				"        {className = \"com.example.shop.Shipment\"; name = Shipment; }, \n        {className = \"com.example.shop.Tag\"; name = Tag; }\n");
		Files.write(model.resolve("Shipment.plist"), Arrays.asList("{",
				"    attributes = (",
				"        {allowsNull = Y; columnName = carrier; name = carrier; prototypeName = varchar100; }, ",
				"        {allowsNull = N; columnName = id; name = id; prototypeName = id; }, ",
				"        {allowsNull = N; columnName = \"order_id\"; name = orderID; prototypeName = id; }, ",
				"        {allowsNull = N; columnName = \"shipped_at\"; name = shippedAt; prototypeName = dateTime; }",
				"    ); ",
				"    attributesUsedForLocking = (id, orderID); ",
				"    className = \"com.example.shop.Shipment\"; ",
				"    classProperties = (carrier, order, shippedAt); ",
				"    entityIndexes = ({attributes = (orderID, shippedAt); constraint = none; indexType = clustered; name = \"shipment_order\"; order = asc; }); ",
				"    externalName = shipment; ",
				"    name = Shipment; ",
				"    primaryKeyAttributes = (id); ",
				"    relationships = ({destination = Order; isMandatory = Y; isToMany = N; joinSemantic = EOInnerJoin; joins = ({destinationAttribute = id; sourceAttribute = orderID; }); name = order; }); ",
				"}"));
	}

	/**
	 * Adds a Notes model, whose one entity, Note, has the table note, to a
	 * folder.
	 */
	private static void addNotes(Path resources) throws IOException {
		Path notes = Files.createDirectories(resources.resolve("Notes.eomodeld"));
		Files.write(notes.resolve("index.eomodeld"), Arrays.asList("{", "    EOModelVersion = \"2.1\"; ", "    adaptorName = JDBC; ", "    connectionDictionary = {}; ",
				"    entities = ({className = \"com.example.notes.Note\"; name = Note; }); ", "}"));
		Files.write(notes.resolve("Note.plist"), Arrays.asList("{", "    attributes = ({allowsNull = N; columnName = id; name = id; prototypeName = id; }); ",
				"    attributesUsedForLocking = (id); ", "    className = \"com.example.notes.Note\"; ", "    classProperties = (); ", "    externalName = note; ", "    name = Note; ",
				"    primaryKeyAttributes = (id); ", "}"));
	}

	/**
	 * Gives the fixture's Order a to-one relationship, memo, to Notes's Note.
	 */
	private void addMemo() throws IOException {
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = \"memo_id\"; name = memoID; prototypeName = id; }, \n");
		edit(ORDER, "    relationships = (\n",
				"    relationships = (\n        {destination = Note; isToMany = N; joinSemantic = EOInnerJoin; joins = ({destinationAttribute = id; sourceAttribute = memoID; }); name = memo; }, \n");
	}

	/**
	 * Returns the folder of the migrations that eogen is expected to write for
	 * the fixture. Those for the corpus models are in the corpus.
	 */
	private static Path expected() throws URISyntaxException {
		return Paths.get(MigrationTest.class.getResource("/migrations").toURI());
	}

	private MigrationOptions options() {
		MigrationOptions options = new MigrationOptions();
		options.setFrom(earlier.toString());
		return options;
	}

	private MigrationResult generate() throws EOGeneratorException {
		return fixture.generator().generateMigrations(options(), true);
	}

	private static ModelMigration created(MigrationResult result) {
		assertThat(result.getCreated()).as("migrations, with %s", result.getFindings()).hasSize(1);
		return result.getCreated().get(0);
	}

	private static String read(MigrationResult result) throws IOException {
		return new String(Files.readAllBytes(created(result).getFile().toPath()), StandardCharsets.UTF_8);
	}

	private static List<String> lines(MigrationResult result) throws IOException {
		return Arrays.asList(read(result).split("\n", -1));
	}

	/**
	 * Returns the lines of a migration's upgrade method, without their
	 * indentation.
	 */
	private static List<String> upgrade(MigrationResult result) throws IOException {
		List<String> lines = lines(result);
		int start = lines.indexOf("\tpublic void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {");
		assertThat(start).isNotNegative();
		List<String> body = new ArrayList<>();
		for (String line : lines.subList(start + 1, lines.size())) {
			if (line.equals("\t}")) {
				break;
			}
			assertThat(line.isEmpty() || line.startsWith("\t\t")).as(line).isTrue();
			body.add(line.isEmpty() ? line : line.substring(2));
		}
		return body;
	}

	private void edit(String file, String from, String to) throws IOException {
		replace(fixture.resolve("src/main/resources").resolve(file), from, to);
	}

	private void editEarlier(String file, String from, String to) throws IOException {
		replace(earlier.resolve(file), from, to);
	}

	private static void replace(Path file, String from, String to) throws IOException {
		String contents = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
		assertThat(contents).as(file.toString()).containsOnlyOnce(from);
		Files.write(file, contents.replace(from, to).getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Compiles classes against stubs of Wonder's API into a folder, with the
	 * stubs' classes, and checks that they compile.
	 */
	private static void compile(Path classes, Path... files) throws IOException, URISyntaxException {
		List<File> sources = stubs();
		for (Path file : files) {
			sources.add(file.toFile());
		}
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		boolean compiled;
		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.ROOT, StandardCharsets.UTF_8)) {
			compiled = compiler.getTask(null, fileManager, diagnostics, Arrays.asList("-d", classes.toString(), "-proc:none", "-encoding", "UTF-8"), null,
					fileManager.getJavaFileObjectsFromFiles(sources)).call();
		}
		assertThat(compiled).as("%s", diagnostics.getDiagnostics()).isTrue();
	}

	private static List<File> stubs() throws IOException, URISyntaxException {
		try (Stream<Path> stubs = Files.walk(Paths.get(MigrationTest.class.getResource("/wonder").toURI()))) {
			return stubs.filter(path -> path.toString().endsWith(".java")).map(Path::toFile).collect(Collectors.toCollection(ArrayList::new));
		}
	}

	/**
	 * Compiles the migrations that a result created against stubs of Wonder's
	 * API, and checks that the only errors are at their gaps.
	 */
	private void assertCompilesButAtGaps(MigrationResult result) throws IOException, URISyntaxException {
		List<File> sources = stubs();
		List<String> gaps = new ArrayList<>();
		for (ModelMigration migration : result.getCreated()) {
			sources.add(migration.getFile());
		}
		for (Finding finding : result.getFindings()) {
			if (Finding.MIGRATION_GAP.equals(finding.getCode())) {
				gaps.add(new File(finding.getFile()).getName() + ":" + finding.getLine());
			}
		}
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		Path classes = Files.createTempDirectory(temp, "classes");
		try (StandardJavaFileManager files = compiler.getStandardFileManager(diagnostics, Locale.ROOT, StandardCharsets.UTF_8)) {
			compiler.getTask(null, files, diagnostics, Arrays.asList("-d", classes.toString(), "-proc:none", "-encoding", "UTF-8"), null, files.getJavaFileObjectsFromFiles(sources)).call();
		}
		List<String> errors = new ArrayList<>();
		for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
			if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
				errors.add(new File(diagnostic.getSource().toUri()).getName() + ":" + diagnostic.getLineNumber());
			}
		}
		assertThat(errors).as("%s", diagnostics.getDiagnostics()).containsExactlyInAnyOrderElementsOf(gaps);
	}

	private static String git(Path folder, String... arguments) throws IOException, InterruptedException {
		List<String> command = new ArrayList<>();
		command.add("git");
		command.addAll(Arrays.asList(arguments));
		Process process = new ProcessBuilder(command).directory(folder.toFile()).redirectErrorStream(true).start();
		String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
		assertThat(process.waitFor()).as(output).isZero();
		return output;
	}
}

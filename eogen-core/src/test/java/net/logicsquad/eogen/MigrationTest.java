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
import java.nio.file.StandardOpenOption;
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
		editEarlier(ORDER, "allowsNull = N; \n            columnName = placed; ", "allowsNull = Y; \n            columnName = placed; ");
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
		assertThat(created(result).getDropped()).containsExactly("shipment");
		assertThat(result.describeKeeping()).isEqualTo("To keep a table or a column that a model no longer maps, such as Shop's shipment, list it in the model's userInfo under eogen.keep.");
		assertCompilesButAtGaps(result);
	}

	/**
	 * The earlier version has Shipment, whose table has a foreign key to the
	 * order's, and an index. The model deletes Shipment, but keeps its table.
	 */
	@Test
	void keepsATableThatTheModelNoLongerMaps() throws Exception {
		addShipment(earlier);
		keep(fixture.resolve("src/main/resources"), "shipment");
		MigrationResult result = generate();
		assertThat(result.getFindings()).isEmpty();
		assertThat(result.getMigrations()).extracting(ModelMigration::getStatus).containsExactly(Status.UNCHANGED);

		// A later change gets a migration without the drop.
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = \"gift_message\"; name = giftMessage; prototypeName = varchar100; }, \n");
		result = generate();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"orderTable.newStringColumn(\"gift_message\", 100, ALLOWS_NULL);");
		assertThat(result.describeKeeping()).isNull();
	}

	/**
	 * The earlier version keeps shipment, and a column of the order's table,
	 * which the model now neither maps nor keeps, so they go.
	 */
	@Test
	void dropsWhatTheEarlierVersionKeptAndTheModelNoLongerKeeps() throws Exception {
		keep(earlier, "shipment", "shop_order.legacy_code");
		MigrationResult result = generate();
		assertThat(result.getFindings()).isEmpty();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"orderTable.existingColumnNamed(\"legacy_code\").delete();",
				"",
				"database.existingTableNamed(\"shipment\").drop();");
		assertThat(created(result).getChanges()).containsExactly("Removes legacy_code from shop_order.", "Drops shipment.");
		assertThat(created(result).getDropped()).containsExactly("shop_order.legacy_code", "shipment");
		assertCompilesButAtGaps(result);
	}

	/**
	 * The model keeps the order's note, and the earlier version's index of it
	 * stays with it.
	 */
	@Test
	void keepsAColumnThatTheModelNoLongerMaps() throws Exception {
		editEarlier(ORDER, "{columnName = note; name = note;", "{allowsNull = Y; columnName = note; name = note;");
		editEarlier(ORDER, "    externalName = \"shop_order\"; \n",
				"    entityIndexes = ({attributes = (note); constraint = none; indexType = clustered; name = \"order_note\"; order = asc; }); \n    externalName = \"shop_order\"; \n");
		edit(ORDER, "        {columnName = note; name = note; prototypeName = longText; }, \n", "");
		edit(ORDER, "lines, note, placed", "lines, placed");
		keep(fixture.resolve("src/main/resources"), "shop_order.note");
		MigrationResult result = generate();
		assertThat(result.getFindings()).isEmpty();
		assertThat(result.getMigrations()).extracting(ModelMigration::getStatus).containsExactly(Status.UNCHANGED);
	}

	/**
	 * Order.note maps a new column, but the model keeps the old one, so the
	 * new column is added rather than the old one renamed.
	 */
	@Test
	void keepsTheOldColumnOfAnAttributeThatMapsANewOne() throws Exception {
		editEarlier(ORDER, "{columnName = note; name = note;", "{allowsNull = Y; columnName = note; name = note;");
		edit(ORDER, "{columnName = note; name = note;", "{allowsNull = Y; columnName = remarks; name = note;");
		keep(fixture.resolve("src/main/resources"), "shop_order.note");
		MigrationResult result = generate();
		assertThat(result.getFindings()).isEmpty();
		assertThat(created(result).getChanges()).containsExactly("Adds remarks to shop_order.");
		assertCompilesButAtGaps(result);
	}

	/**
	 * The earlier version keeps shipment and a column of the order's table,
	 * which the model maps again: they are there already.
	 */
	@Test
	void mapsAKeptTableAndColumnAgain() throws Exception {
		keep(earlier, "shipment", "shop_order.legacy_code");
		addShipment(fixture.resolve("src/main/resources"));
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = \"legacy_code\"; name = legacyCode; prototypeName = varchar100; }, \n");
		MigrationResult result = generate();
		assertThat(result.getFindings()).isEmpty();
		assertThat(result.getMigrations()).extracting(ModelMigration::getStatus).containsExactly(Status.UNCHANGED);
	}

	/**
	 * A kept column that doesn't allow null needs a value in each row that the
	 * application inserts, which it no longer gives it.
	 */
	@Test
	void leavesAGapForAKeptColumnThatDoesNotAllowNull() throws Exception {
		editEarlier(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = N; columnName = \"legacy_code\"; name = legacyCode; prototypeName = varchar100; }, \n");
		keep(fixture.resolve("src/main/resources"), "shop_order.legacy_code");
		MigrationResult result = generate();
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"// TODO eogen: Shop keeps legacy_code in shop_order, which doesn't allow null, so the application",
				"// can't insert a row into shop_order unless the column has a default. Let it allow null, as the next",
				"// line does, or give it a default instead. Either way, delete this line.",
				"EOGEN_GAP;",
				"orderTable.existingColumnNamed(\"legacy_code\").setAllowsNull(ALLOWS_NULL);");
		assertThat(result.getFindings()).extracting(Finding::getCode, Finding::getObject).containsExactly(tuple(Finding.MIGRATION_GAP, "column legacy_code"));
		assertThat(created(result).getChanges()).containsExactly("Lets legacy_code in shop_order be null, as Shop keeps it.");
		assertThat(created(result).getDropped()).isEmpty();
		assertCompilesButAtGaps(result);
	}

	/**
	 * The earlier version's order has two to-ones to Shipment. The model
	 * deletes Shipment and both, but keeps its table and one's column, and
	 * still maps the other's: both foreign keys stay, as does the kept table's
	 * own, to the order's.
	 */
	@Test
	void keepsTheForeignKeysOfAndIntoAKeptTable() throws Exception {
		addShipment(earlier);
		String lastShipment = "        {allowsNull = Y; columnName = \"last_shipment_id\"; name = lastShipmentID; prototypeName = id; }, \n";
		String returnShipment = "        {allowsNull = Y; columnName = \"return_shipment_id\"; name = returnShipmentID; prototypeName = id; }, \n";
		editEarlier(ORDER, ATTRIBUTES, ATTRIBUTES + lastShipment + returnShipment);
		editEarlier(ORDER, "    relationships = (\n", "    relationships = (\n"
				+ "        {destination = Shipment; isToMany = N; joinSemantic = EOInnerJoin; joins = ({destinationAttribute = id; sourceAttribute = lastShipmentID; }); name = lastShipment; }, \n"
				+ "        {destination = Shipment; isToMany = N; joinSemantic = EOInnerJoin; joins = ({destinationAttribute = id; sourceAttribute = returnShipmentID; }); name = returnShipment; }, \n");
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + returnShipment);
		keep(fixture.resolve("src/main/resources"), "shipment", "shop_order.last_shipment_id");
		MigrationResult result = generate();
		assertThat(result.getFindings()).isEmpty();
		assertThat(result.getMigrations()).extracting(ModelMigration::getStatus).containsExactly(Status.UNCHANGED);
	}

	@Test
	void warnsOfAKeptNameThatDoesNothing() throws Exception {
		keep(fixture.resolve("src/main/resources"), "tag", "shop_order.note", "shipmnt");
		MigrationResult result = generate();
		assertThat(result.getMigrations()).extracting(ModelMigration::getStatus).containsExactly(Status.UNCHANGED);
		assertThat(result.getFindings()).extracting(Finding::getSeverity, Finding::getCode, Finding::getMessage).containsExactly(
				tuple(Finding.Severity.WARNING, Finding.UNUSED_KEEP, "Shop's eogen.keep names tag, but Tag still maps it, so it does nothing."),
				tuple(Finding.Severity.WARNING, Finding.UNUSED_KEEP, "Shop's eogen.keep names shop_order.note, but Order.note still maps it, so it does nothing."),
				tuple(Finding.Severity.WARNING, Finding.UNUSED_KEEP, "Shop's eogen.keep names shipmnt, but the earlier version of Shop neither maps nor keeps it, so it does nothing."));
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

	/**
	 * By default, a model with migrations is compared with the commit that
	 * added its latest, unless a later one changed it with the model, here the
	 * last commit, which has Shop0.
	 */
	@Test
	void comparesWithTheCommitOfTheLatestMigration() throws Exception {
		String commit = commit("Shop.");
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = \"gift_message\"; name = giftMessage; prototypeName = varchar100; }, \n");
		MigrationOptions options = new MigrationOptions();
		MigrationResult result = fixture.generator().generateMigrations(options, true);
		assertThat(result.getFindings()).isEmpty();
		assertThat(result.getSince()).isEqualTo("each model's latest migration");
		assertThat(created(result).getSince()).isEqualTo("Shop0 (commit " + commit + ")");
		assertThat(upgrade(result)).containsExactly(
				"ERXMigrationTable orderTable = database.existingTableNamed(\"shop_order\");",
				"orderTable.newStringColumn(\"gift_message\", 100, ALLOWS_NULL);");
		assertThat(read(result)).contains(" * Changes the tables of the Shop model as it changed since Shop0 (commit " + commit + "):");

		options.setFrom("no-such-branch");
		Finding error = fixture.generator().generateMigrations(options, true).getFindings().get(0);
		assertThat(error.getCode()).isEqualTo(Finding.EARLIER_VERSION);
		assertThat(error.getMessage()).startsWith("no-such-branch is neither a folder nor a commit in ");
	}

	/**
	 * A model change committed with no migration, and then another commit,
	 * which a comparison with HEAD can't see.
	 */
	@Test
	void findsAModelChangeCommittedWithNoMigration() throws Exception {
		String shop0 = commit("Shop.");
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = \"gift_message\"; name = giftMessage; prototypeName = varchar100; }, \n");
		commit("Gift messages.");
		Files.write(fixture.resolve("NOTES.txt"), Collections.singletonList("Something else."));
		commit("Something else.");
		MigrationOptions options = new MigrationOptions();
		MigrationResult check = fixture.generator().generateMigrations(options, false);
		assertThat(check.getMissing()).extracting(ModelMigration::describeMissing)
				.containsExactly("Shop changed since Shop0 (commit " + shop0 + "), with no migration: Adds gift_message to shop_order.");

		ModelMigration shop1 = created(fixture.generator().generateMigrations(options, true));
		assertThat(shop1.getClassName()).isEqualTo("com.example.shop.migrations.Shop1");
		// Until Shop1 is committed, eogen can't tell what has changed since it.
		check = fixture.generator().generateMigrations(options, false);
		assertThat(check.getMissing()).isEmpty();
		assertThat(check.getFindings()).extracting(Finding::getCode, Finding::getMessage)
				.containsExactly(tuple(Finding.NOT_COMPARED, "Shop's latest migration, Shop1, isn't committed yet, so eogen can't tell what changed since it."));

		String commit = commit("Shop1.");
		check = fixture.generator().generateMigrations(options, false);
		assertThat(check.getFindings()).isEmpty();
		assertThat(check.getMigrations()).extracting(ModelMigration::getStatus, ModelMigration::getReason)
				.containsExactly(tuple(Status.UNCHANGED, "Its tables haven't changed since Shop1 (commit " + commit + ")."));
	}

	/**
	 * A migration that isn't released yet may be edited with a change to its
	 * model, so the last commit that changed both is the one to compare with,
	 * and not one that changes only the migration, as a commit that tidies the
	 * Javadoc of every migration does.
	 */
	@Test
	void comparesWithTheLastCommitThatChangedTheLatestMigrationWithItsModel() throws Exception {
		commit("Shop.");
		Path shop0 = fixture.resolve("src/main/java/com/example/shop/migrations/Shop0.java");
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = \"gift_message\"; name = giftMessage; prototypeName = varchar100; }, \n");
		Files.write(shop0, Collections.singletonList("// Adds gift_message to shop_order."), StandardOpenOption.APPEND);
		String both = commit("Gift messages, in Shop0.");
		Files.write(shop0, Collections.singletonList("// Tidied."), StandardOpenOption.APPEND);
		commit("Tidies Shop0.");
		MigrationOptions options = new MigrationOptions();
		MigrationResult check = fixture.generator().generateMigrations(options, false);
		assertThat(check.getFindings()).isEmpty();
		assertThat(check.getMigrations()).extracting(ModelMigration::getStatus, ModelMigration::getReason)
				.containsExactly(tuple(Status.UNCHANGED, "Its tables haven't changed since Shop0 (commit " + both + ")."));

		edit(ORDER, "{allowsNull = Y; columnName = \"gift_message\";", "{allowsNull = N; columnName = \"gift_message\";");
		commit("Gift messages are mandatory.");
		Files.write(shop0, Collections.singletonList("// Tidied again."), StandardOpenOption.APPEND);
		commit("Tidies Shop0 again.");
		check = fixture.generator().generateMigrations(options, false);
		assertThat(check.getMissing()).extracting(ModelMigration::describeMissing)
				.containsExactly("Shop changed since Shop0 (commit " + both + "), with no migration: Makes gift_message in shop_order not allow null.");
	}

	/**
	 * Shop0 and Notes0 came in different commits, and each model changed after
	 * its own. Notes0 is so like Shop0 that git takes it for a copy, which eogen
	 * mustn't follow back to Shop0's commit. Labels has no migrations, so it is
	 * compared with the last commit.
	 */
	@Test
	void comparesEachModelWithTheCommitOfItsOwnLatestMigration() throws Exception {
		String shop0 = commit("Shop.");
		addNotes(fixture.resolve("src/main/resources"));
		Files.write(fixture.resolve("src/main/java/com/example/shop/migrations/Notes0.java"), Arrays.asList("package com.example.shop.migrations;", "", "public class Notes0 {", "}"));
		String notes0 = commit("Notes.");
		edit(ORDER, ATTRIBUTES, ATTRIBUTES + "        {allowsNull = Y; columnName = \"gift_message\"; name = giftMessage; prototypeName = varchar100; }, \n");
		addModel(fixture.resolve("src/main/resources"), "Labels", "Label", "label", "");
		commit("Gift messages, and labels.");
		replace(fixture.resolve("src/main/resources/Notes.eomodeld/Note.plist"), "attributes = ({",
				"attributes = ({allowsNull = Y; columnName = body; name = body; prototypeName = longText; }, {");
		String last = commit("Note bodies.");
		MigrationResult result = fixture.generator().generateMigrations(new MigrationOptions(), false);
		assertThat(result.getFindings()).isEmpty();
		assertThat(result.getMigrations()).extracting(ModelMigration::getModel, ModelMigration::getStatus, ModelMigration::getSince).containsExactly(
				tuple("Labels", Status.UNCHANGED, "commit " + last),
				tuple("Notes", Status.CREATED, "Notes0 (commit " + notes0 + ")"),
				tuple("Shop", Status.CREATED, "Shop0 (commit " + shop0 + ")"));
		assertThat(result.getMissing()).extracting(ModelMigration::getModel, ModelMigration::getChanges).containsExactly(
				tuple("Notes", Collections.singletonList("Adds body to note.")),
				tuple("Shop", Collections.singletonList("Adds gift_message to shop_order.")));
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
		// With LF line endings, as .gitattributes checks out the bundled
		// templates, on Windows too.
		Files.write(template, (String.join("\n", "package ${migration.packageName};", "", "#foreach ($line in $migration.importLines([\"com.example.shop.Base\"]))", "$line", "#end", "",
				"public class ${migration.className} extends Base {", "\t@Override", "\tpublic void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {",
				"#foreach ($line in $migration.upgradeLines)", "$line", "#end", "\t}", "", "\t// $migration.gapCount gaps, $migration.notAProperty", "}") + "\n").getBytes(StandardCharsets.UTF_8));
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
	 * Gives the Shop model in a folder a list of the tables and columns that it
	 * keeps.
	 */
	private static void keep(Path resources, String... names) throws IOException {
		List<String> quoted = new ArrayList<>();
		for (String name : names) {
			quoted.add("\"" + name + "\"");
		}
		replace(resources.resolve("Shop.eomodeld/index.eomodeld"), "    userInfo = {", "    userInfo = {\"eogen.keep\" = (" + String.join(", ", quoted) + "); ");
	}

	/**
	 * Adds a Notes model, whose one entity, Note, has the table note, to a
	 * folder.
	 */
	private static void addNotes(Path resources) throws IOException {
		addModel(resources, "Notes", "Note", "note", "");
	}

	/**
	 * Adds a model with one entity, whose table has an id column, and others,
	 * to a folder.
	 */
	private static void addModel(Path resources, String model, String entity, String table, String attributes) throws IOException {
		Path folder = Files.createDirectories(resources.resolve(model + ".eomodeld"));
		String className = "com.example." + model.toLowerCase(Locale.ROOT) + "." + entity;
		Files.write(folder.resolve("index.eomodeld"), Arrays.asList("{", "    EOModelVersion = \"2.1\"; ", "    adaptorName = JDBC; ", "    connectionDictionary = {}; ",
				"    entities = ({className = \"" + className + "\"; name = " + entity + "; }); ", "}"));
		Files.write(folder.resolve(entity + ".plist"), Arrays.asList("{", "    attributes = (" + attributes + "{allowsNull = N; columnName = id; name = id; prototypeName = id; }); ",
				"    attributesUsedForLocking = (id); ", "    className = \"" + className + "\"; ", "    classProperties = (); ", "    externalName = " + table + "; ", "    name = " + entity + "; ",
				"    primaryKeyAttributes = (id); ", "}"));
	}

	/**
	 * Commits everything in the fixture's copy, in a new repository the first
	 * time.
	 *
	 * @return the commit's short name
	 */
	private String commit(String message) throws IOException, InterruptedException {
		Path project = fixture.resolve(".");
		if (!Files.isDirectory(project.resolve(".git"))) {
			git(project, "init", "-q");
		}
		git(project, "add", "-A");
		git(project, "-c", "user.name=eogen", "-c", "user.email=eogen@example.com", "commit", "-q", "-m", message);
		return git(project, "rev-parse", "--short", "HEAD").trim();
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

package net.logicsquad.eogen;

import static net.logicsquad.eogen.GeneratedFile.Kind.SUBCLASS;
import static net.logicsquad.eogen.GeneratedFile.Kind.SUPERCLASS;
import static net.logicsquad.eogen.GeneratedFile.Status.CREATED;
import static net.logicsquad.eogen.GeneratedFile.Status.ORPHANED;
import static net.logicsquad.eogen.GeneratedFile.Status.UNCHANGED;
import static net.logicsquad.eogen.GeneratedFile.Status.UPDATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.tuple;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import net.logicsquad.eogen.model.EOModelVerificationFailure;

/**
 * Generates, checks and verifies a copy of the fixture project, and compares
 * the output with what WOLips 5.0.3 generated from the same files. WOLips's
 * output has unresolved references in it, which eogen reports as errors, so
 * the comparisons use {@link FixtureProject#forcedGenerator()}.
 */
class EOGeneratorTest {
	private static final String PACKAGE = "com/example/shop/";

	@TempDir
	Path temp;

	@ParameterizedTest
	@CsvSource({ "ShopWonder, src/main/java", "ShopClassic, generated/classic", "ShopCustom, generated/custom" })
	void generatesWhatWOLipsGenerates(String name, String destination) throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		GenerationResult result = project.forcedGenerator().generate(project.eogen(name));
		assertThat(modelErrors(result)).isEmpty();
		assertThat(result.isWritten()).isTrue();
		assertThat(result.count(SUPERCLASS, CREATED)).isEqualTo(7);
		assertThat(result.count(SUBCLASS, CREATED)).isEqualTo(7);
		assertSameFiles(FixtureProject.expected(name), project.resolve(destination));
	}

	@Test
	void generatingAgainChangesNothing() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		EOGenerator generator = project.forcedGenerator();
		generator.generate(project.eogen("ShopWonder"));
		GenerationResult again = generator.generate(project.eogen("ShopWonder"));
		assertThat(again.count(SUPERCLASS, UNCHANGED)).isEqualTo(7);
		assertThat(again.getFiles()).allMatch(file -> file.getKind() == SUPERCLASS);
		assertThat(again.isStale()).isFalse();
		assertSameFiles(FixtureProject.expected("ShopWonder"), project.resolve("src/main/java"));
	}

	@Test
	void superclassesAreAlwaysWrittenButSubclassesOnlyWhenMissing() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		EOGenerator generator = project.forcedGenerator();
		generator.generate(project.eogen("ShopWonder"));
		Path superclass = project.resolve("src/main/java/" + PACKAGE + "_Product.java");
		Path subclass = project.resolve("src/main/java/" + PACKAGE + "Product.java");
		Path missingSubclass = project.resolve("src/main/java/" + PACKAGE + "Order.java");
		append(superclass, "// edited\n");
		append(subclass, "// edited\n");
		Files.delete(missingSubclass);

		GenerationResult result = generator.generate(project.eogen("ShopWonder"));

		assertThat(statuses(result)).containsEntry("_Product.java", UPDATED).containsEntry("Order.java", CREATED).doesNotContainKey("Product.java");
		assertThat(superclass).hasSameBinaryContentAs(FixtureProject.expected("ShopWonder").resolve(PACKAGE + "_Product.java"));
		assertThat(new String(Files.readAllBytes(subclass), StandardCharsets.UTF_8)).endsWith("// edited\n");
		assertThat(missingSubclass).hasSameBinaryContentAs(FixtureProject.expected("ShopWonder").resolve(PACKAGE + "Order.java"));
	}

	@Test
	void unchangedSuperclassesAreNotRewritten() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		EOGenerator generator = project.forcedGenerator();
		generator.generate(project.eogen("ShopWonder"));
		Path superclass = project.resolve("src/main/java/" + PACKAGE + "_Product.java");
		FileTime old = FileTime.fromMillis(1_000_000_000_000L);
		Files.setLastModifiedTime(superclass, old);
		generator.generate(project.eogen("ShopWonder"));
		assertThat(Files.getLastModifiedTime(superclass)).isEqualTo(old);
	}

	@Test
	void checkWritesNothing() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		Map<String, String> before = snapshot(project.root);
		GenerationResult result = project.generator().check(project.eogen("ShopWonder"));
		assertThat(result.isWritten()).isFalse();
		assertThat(result.getStaleFiles()).hasSize(14).allMatch(file -> file.getStatus() == CREATED);
		assertThat(snapshot(project.root)).isEqualTo(before);
	}

	@Test
	void checkFindsStaleSuperclassesAndMissingSubclasses() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		EOGenerator generator = project.forcedGenerator();
		generator.generate(project.eogen("ShopWonder"));
		assertThat(generator.check(project.eogen("ShopWonder")).isStale()).isFalse();

		append(project.resolve("src/main/java/" + PACKAGE + "_Order.java"), "// edited\n");
		append(project.resolve("src/main/java/" + PACKAGE + "Product.java"), "// edited\n");
		Files.delete(project.resolve("src/main/java/" + PACKAGE + "Tag.java"));
		Map<String, String> before = snapshot(project.root);

		GenerationResult result = generator.check(project.eogen("ShopWonder"));

		Map<String, GeneratedFile.Status> stale = new LinkedHashMap<>();
		for (GeneratedFile file : result.getStaleFiles()) {
			stale.put(file.getFile().getName(), file.getStatus());
		}
		assertThat(stale).containsOnly(entry("_Order.java", UPDATED), entry("Tag.java", CREATED));
		assertThat(snapshot(project.root)).isEqualTo(before);
	}

	@Test
	void subclassDestinationDefaultsToDestination() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		File eogen = project.eogen("ShopClassic");
		write(eogen.toPath(), " -destination generated/one \\\n -model src/main/resources/Shop.eomodeld \\\n -packagedirs \\\n -loadModelGroup\n");
		GenerationResult result = project.forcedGenerator().generate(eogen);
		assertThat(result.count(SUBCLASS, CREATED)).isEqualTo(7);
		assertThat(FixtureProject.files(project.resolve("generated/one"))).contains(PACKAGE + "Product.java", PACKAGE + "_Product.java");
	}

	@Test
	void eogenFileWithoutDestinationFails() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		File eogen = project.eogen("ShopClassic");
		write(eogen.toPath(), " -model src/main/resources/Shop.eomodeld \\\n -loadModelGroup\n");
		assertThatThrownBy(() -> project.generator().generate(eogen)).isInstanceOf(EOGeneratorException.class).hasMessageContaining("no -destination");
	}

	@Test
	void eogenFileWithoutModelFails() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		File eogen = project.eogen("ShopClassic");
		write(eogen.toPath(), " -destination generated \\\n -loadModelGroup\n");
		assertThatThrownBy(() -> project.generator().generate(eogen)).isInstanceOf(EOGeneratorException.class).hasMessageContaining("no -model");
	}

	@Test
	void eogenFileNamingAMissingModelFails() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		File eogen = project.eogen("ShopClassic");
		write(eogen.toPath(), " -destination generated \\\n -model src/main/resources/Missing.eomodeld \\\n -loadModelGroup\n");
		assertThatThrownBy(() -> project.generator().generate(eogen)).isInstanceOf(EOGeneratorException.class).hasMessageContaining("Missing.eomodeld");
	}

	@Test
	void modelCanBeNamedByItsIndexFile() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		File eogen = project.eogen("ShopClassic");
		write(eogen.toPath(), read(eogen.toPath()).replace("Shop.eomodeld \\", "Shop.eomodeld/index.eomodeld \\"));
		assertThat(read(eogen.toPath())).contains("Shop.eomodeld/index.eomodeld");
		GenerationResult result = project.forcedGenerator().generate(eogen);
		assertThat(modelErrors(result)).isEmpty();
		assertSameFiles(FixtureProject.expected("ShopClassic"), project.resolve("generated/classic"));
	}

	@Test
	void generatesInAFolderWhosePathHasSpaces() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp.resolve("My Projects"));
		GenerationResult fromJar = project.forcedGenerator().generate(project.eogen("ShopWonder"));
		assertThat(modelErrors(fromJar)).isEmpty();
		assertSameFiles(FixtureProject.expected("ShopWonder"), project.resolve("src/main/java"));
		EOGenerator folderGenerator = project.generatorWithFolderOnClasspath();
		folderGenerator.setForce(true);
		GenerationResult fromFolder = folderGenerator.generate(project.eogen("ShopCustom"));
		assertThat(modelErrors(fromFolder)).isEmpty();
		assertSameFiles(FixtureProject.expected("ShopCustom"), project.resolve("generated/custom"));
	}

	/**
	 * A dependency's model that declares one of Shop's entities would evict
	 * Shop, but Shop is loaded last and evicts it instead, as in Eclipse.
	 */
	@Test
	void generatesDespiteADependencyThatSharesAnEntity() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		Path old = temp.resolve("old");
		Path model = Files.createDirectories(old.resolve("Resources/ShopOld.eomodeld"));
		write(model.resolve("index.eomodeld"), "{\n    EOModelVersion = \"2.1\"; \n    adaptorName = JDBC; \n    entities = ({className = \"com.example.old.Tag\"; name = Tag; }); \n}\n");
		write(model.resolve("Tag.plist"), "{\n    attributes = ({allowsNull = N; columnName = id; name = id; prototypeName = id; }); \n    className = \"com.example.old.Tag\"; \n"
				+ "    classProperties = (); \n    externalName = tag; \n    name = Tag; \n    primaryKeyAttributes = (id); \n}\n");
		Path oldJar = temp.resolve("old.jar");
		FixtureProject.jar(old, oldJar);
		EOGenerator generator = new EOGenerator(project.root.toFile(), Collections.singletonList(project.resolve("src/main/resources").toFile()),
				java.util.Arrays.asList(oldJar.toFile(), project.referenceJar.toFile()));
		generator.setForce(true);
		GenerationResult result = generator.generate(project.eogen("ShopClassic"));
		assertThat(modelErrors(result)).isEmpty();
		assertSameFiles(FixtureProject.expected("ShopClassic"), project.resolve("generated/classic"));
	}

	/**
	 * Without the Reference jar, the Shop model's prototypes and Currency are
	 * missing. WOLips would write classes with unresolved references in them.
	 */
	@Test
	void modelErrorsMeanNothingIsWritten() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		GenerationResult result = project.generatorWithoutDependencies().generate(project.eogen("ShopWonder"));
		assertThat(result.hasErrors()).isTrue();
		assertThat(result.getErrors()).anyMatch(error -> error.contains("Currency")).anyMatch(error -> error.contains("prototype"));
		assertThat(result.isWritten()).isFalse();
		assertThat(result.getStaleFiles()).hasSize(14);
		assertThat(FixtureProject.files(project.resolve("src/main/java"))).isEmpty();
	}

	@Test
	void forceWritesDespiteModelErrors() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		EOGenerator generator = project.generatorWithoutDependencies();
		generator.setForce(true);
		GenerationResult result = generator.generate(project.eogen("ShopWonder"));
		assertThat(result.hasErrors()).isTrue();
		assertThat(result.isWritten()).isTrue();
		assertThat(FixtureProject.files(project.resolve("src/main/java"))).contains(PACKAGE + "_Order.java", PACKAGE + "Order.java");
	}

	/**
	 * WOLips gives the flattened attribute Order.customerName no Java class,
	 * and writes {@code $attribute.javaClassName} into Order's superclass.
	 */
	@Test
	void unresolvedReferenceIsAnError() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		GenerationResult result = project.generator().generate(project.eogen("ShopWonder"));
		assertThat(result.getErrors()).containsExactlyInAnyOrder(
				"com/example/shop/_Order.java: $attribute.javaClassName has no value for attribute customerName (_WonderEntity.java).",
				"com/example/shop/_Order.java: ${attribute.javaClassName} has no value for attribute customerName (_WonderEntity.java).");
		assertThat(result.isWritten()).isFalse();
		assertThat(FixtureProject.files(project.resolve("src/main/java"))).isEmpty();
	}

	/**
	 * A misspelt key in a model is ignored, which leaves an attribute with no
	 * prototype, and so no Java class.
	 */
	@Test
	void misspeltModelKeyIsAnError() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		Path product = project.resolve("src/main/resources/Shop.eomodeld/Product.plist");
		write(product, read(product).replace("name = price; prototypeName = amount;", "name = price; prototypeNam = amount;"));
		GenerationResult result = project.generator().generate(project.eogen("ShopWonder"));
		assertThat(result.getErrors()).anyMatch(error -> error.startsWith("com/example/shop/_Product.java: ") && error.contains("for attribute price"));
		assertThat(result.isWritten()).isFalse();
	}

	/**
	 * Only a property or method of a variable that the template has counts: a
	 * bare reference to nothing is often not meant as one, and quiet references,
	 * tests and #set values don't render.
	 */
	@Test
	void otherUnrenderedReferencesAreNotErrors() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		write(project.resolve("src/main/templates/_Custom.java"), String.join("\n",
				"// $LastChangedRevision$ ${entity.name}",
				"// Outer$Inner $!entity.noSuchProperty $!{entity.noSuchProperty}",
				"#if ($entity.noSuchProperty)never#end",
				"#set ($missing = $entity.noSuchProperty)",
				"public class $entity.prefixClassNameWithoutPackage {}",
				""));
		write(project.resolve("src/main/templates/Custom.java"), "public class $entity.classNameWithoutPackage {}\n");
		GenerationResult result = project.generator().generate(project.eogen("ShopCustom"));
		assertThat(result.getErrors()).isEmpty();
		assertThat(result.isWritten()).isTrue();
		assertThat(read(project.resolve("generated/custom/" + PACKAGE + "_Product.java"))).contains("// $LastChangedRevision$ Product", "// Outer$Inner ", "public class _Product {}")
				.doesNotContain("noSuchProperty", "never");
	}

	/**
	 * A class property that matches nothing silently drops it from the class,
	 * so the lint reports it: as a warning, or as an error when strict.
	 */
	@Test
	void lintFindingsAreWarningsOrErrorsWhenStrict() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		Path product = project.resolve("src/main/resources/Shop.eomodeld/Product.plist");
		write(product, read(product).replace("classProperties = (active, category, name, price, tags);", "classProperties = (active, category, name, prise, tags);"));
		String finding = "Shop.eomodeld/Product.plist: entity Product: classProperties names prise, which is not an attribute or relationship (did you mean price?).";

		GenerationResult lenient = project.forcedGenerator().generate(project.eogen("ShopWonder"));
		assertThat(lenient.getWarnings()).containsExactly(finding);
		assertThat(lenient.getErrors()).doesNotContain(finding);

		EOGenerator strict = project.forcedGenerator();
		strict.setStrict(true);
		assertThat(strict.check(project.eogen("ShopWonder")).getErrors()).contains(finding);

		assertThat(project.generator().verify(false).getWarnings()).extracting(EOModelVerificationFailure::getMessage).containsExactly(finding);
		EOGenerator strictVerifier = project.generator();
		strictVerifier.setStrict(true);
		VerificationResult verification = strictVerifier.verify(false);
		assertThat(verification.getErrors()).extracting(EOModelVerificationFailure::getMessage).containsExactly(finding);
		assertThat(verification.getErrors().get(0).getModel().getName()).isEqualTo("Shop");
	}

	/**
	 * A deleted or renamed entity leaves its superclass behind, and its
	 * hand-edited subclass. Only a superclass with the generated marker counts.
	 */
	@Test
	void findsOrphansAndTheirSubclasses() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		EOGenerator generator = project.forcedGenerator();
		GenerationResult result = generator.generate(project.eogen("ShopWonder"));
		Path shop = project.resolve("src/main/java/" + PACKAGE);
		write(shop.resolve("_Coupon.java"), "// DO NOT EDIT.  Make changes to com.example.shop.Coupon.java instead.\nclass _Coupon {}\n");
		write(shop.resolve("Coupon.java"), "class Coupon extends _Coupon {}\n");
		write(shop.resolve("_Handwritten.java"), "class _Handwritten {}\n");
		write(project.resolve("src/main/java/_Old.java"), "/*\n * DO NOT EDIT.\n */\nclass _Old {}\n");

		List<GeneratedFile> orphans = generator.findOrphans(Collections.singletonList(result));

		assertThat(orphans).extracting(file -> relative(project, file), GeneratedFile::getKind).containsExactly(tuple("src/main/java/_Old.java", SUPERCLASS),
				tuple("src/main/java/" + PACKAGE + "_Coupon.java", SUPERCLASS), tuple("src/main/java/" + PACKAGE + "Coupon.java", SUBCLASS));
		assertThat(orphans).allMatch(file -> file.getStatus() == ORPHANED && file.isStale());

		assertThat(EOGenerator.deleteOrphans(orphans)).extracting(file -> relative(project, file)).containsExactly("src/main/java/_Old.java",
				"src/main/java/" + PACKAGE + "_Coupon.java");
		assertThat(shop.resolve("_Coupon.java")).doesNotExist();
		assertThat(shop.resolve("Coupon.java")).exists();
		assertThat(shop.resolve("_Handwritten.java")).exists();
		assertThat(generator.findOrphans(Collections.singletonList(result))).isEmpty();
	}

	/**
	 * Two .eogen files can write to one destination, so orphans are only those
	 * that none of them generates.
	 */
	@Test
	void anotherEOGenFilesSuperclassesAreNotOrphans() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		Path extra = Files.createDirectories(project.resolve("src/main/resources/Extra.eomodeld"));
		write(extra.resolve("index.eomodeld"), "{\n    EOModelVersion = \"2.1\"; \n    adaptorName = JDBC; \n    entities = ({className = \"com.example.shop.Coupon\"; name = Coupon; }); \n}\n");
		write(extra.resolve("Coupon.plist"), "{\n    attributes = ({allowsNull = N; columnName = id; name = id; prototypeName = id; }); \n    attributesUsedForLocking = (id); \n"
				+ "    className = \"com.example.shop.Coupon\"; \n    classProperties = (); \n    externalName = coupon; \n    name = Coupon; \n    primaryKeyAttributes = (id); \n}\n");
		File extraEOGen = project.resolve("src/main/resources/Extra.eogen").toFile();
		write(extraEOGen.toPath(), " -destination src/main/java \\\n -javaTemplate _WonderEntity.java \\\n -model src/main/resources/Extra.eomodeld \\\n -packagedirs \\\n"
				+ " -subclassJavaTemplate WonderEntity.java \\\n -loadModelGroup\n");
		EOGenerator generator = project.forcedGenerator();
		GenerationResult shop = generator.generate(project.eogen("ShopWonder"));
		GenerationResult coupon = generator.generate(extraEOGen);
		assertThat(coupon.getErrors()).isEmpty();

		assertThat(generator.findOrphans(Collections.singletonList(shop))).extracting(file -> relative(project, file))
				.containsExactly("src/main/java/" + PACKAGE + "_Coupon.java", "src/main/java/" + PACKAGE + "Coupon.java");
		assertThat(generator.findOrphans(Arrays.asList(shop, coupon))).isEmpty();
	}

	@Test
	void modelThatFailsToLoadIsAnError() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		write(project.resolve("src/main/resources/Shop.eomodeld/index.eomodeld"), "{ entities = ( ");
		assertThatThrownBy(() -> project.generator().generate(project.eogen("ShopClassic"))).isInstanceOf(EOGeneratorException.class)
				.hasMessageContaining("Failed to load the model");
	}

	@Test
	void findsEOGenFilesInNameOrder() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		List<File> found = EOGenerator.findEOGenFiles(Collections.singletonList(project.resolve("src/main/resources").toFile()));
		assertThat(found).extracting(File::getName).containsExactly("ShopClassic.eogen", "ShopCustom.eogen", "ShopWonder.eogen");
	}

	@Test
	void verifyFindsNoFailuresInTheFixture() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		VerificationResult result = project.generator().verify(false);
		assertThat(result.getModelNames()).containsExactly("Shop");
		assertThat(result.getFailures()).isEmpty();
		assertThat(project.generator().verify(true).getModelNames()).containsExactly("Reference", "Shop");
	}

	@Test
	void verifyReportsErrors() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		Path order = project.resolve("src/main/resources/Shop.eomodeld/Order.plist");
		write(order, read(order).replace("destination = Currency;", "destination = Nowhere;"));
		VerificationResult result = project.generator().verify(false);
		assertThat(result.hasErrors()).isTrue();
		assertThat(result.getErrors()).extracting(EOModelVerificationFailure::getMessage).anyMatch(message -> message.contains("Nowhere"));
	}

	@Test
	void modelsComeFromTheClasspath() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		VerificationResult result = project.generatorWithoutDependencies().verify(false);
		assertThat(result.hasErrors()).isTrue();
		assertThat(result.getErrors()).extracting(EOModelVerificationFailure::getMessage).anyMatch(message -> message.contains("Currency"));
	}

	/**
	 * Returns the errors that come from the model group rather than from
	 * unresolved references in the templates' output.
	 */
	private static List<String> modelErrors(GenerationResult result) {
		return result.getErrors().stream().filter(error -> !error.contains(" has no value")).collect(Collectors.toList());
	}

	private static String relative(FixtureProject project, GeneratedFile file) {
		return project.root.relativize(file.getFile().toPath()).toString().replace(File.separatorChar, '/');
	}

	private static Map<String, GeneratedFile.Status> statuses(GenerationResult result) {
		Map<String, GeneratedFile.Status> statuses = new LinkedHashMap<>();
		for (GeneratedFile file : result.getFiles()) {
			statuses.put(file.getFile().getName(), file.getStatus());
		}
		return statuses;
	}

	private static void assertSameFiles(Path expected, Path actual) throws IOException {
		List<String> expectedFiles = FixtureProject.files(expected);
		assertThat(FixtureProject.files(actual)).containsExactlyElementsOf(expectedFiles);
		for (String file : expectedFiles) {
			assertThat(actual.resolve(file)).as(file).hasSameBinaryContentAs(expected.resolve(file));
		}
	}

	/**
	 * Returns every file under a folder with its contents and modification time.
	 */
	private static Map<String, String> snapshot(Path folder) throws IOException {
		Map<String, String> snapshot = new LinkedHashMap<>();
		List<Path> paths;
		try (Stream<Path> walk = Files.walk(folder)) {
			paths = walk.sorted().collect(Collectors.toList());
		}
		for (Path path : paths) {
			String key = folder.relativize(path).toString();
			snapshot.put(key, Files.isDirectory(path) ? "folder" : Files.getLastModifiedTime(path) + " " + new String(Files.readAllBytes(path), StandardCharsets.UTF_8));
		}
		return snapshot;
	}

	private static void append(Path path, String text) throws IOException {
		write(path, read(path) + text);
	}

	private static String read(Path path) throws IOException {
		return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
	}

	private static void write(Path path, String text) throws IOException {
		Files.write(path, text.getBytes(StandardCharsets.UTF_8));
	}
}

package net.logicsquad.eogen;

import static net.logicsquad.eogen.GeneratedFile.Kind.SUBCLASS;
import static net.logicsquad.eogen.GeneratedFile.Kind.SUPERCLASS;
import static net.logicsquad.eogen.GeneratedFile.Status.CREATED;
import static net.logicsquad.eogen.GeneratedFile.Status.UNCHANGED;
import static net.logicsquad.eogen.GeneratedFile.Status.UPDATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
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
 * the output with what WOLips 5.0.3 generated from the same files.
 */
class EOGeneratorTest {
	private static final String PACKAGE = "com/example/shop/";

	@TempDir
	Path temp;

	@ParameterizedTest
	@CsvSource({ "ShopWonder, src/main/java", "ShopClassic, generated/classic", "ShopCustom, generated/custom" })
	void generatesWhatWOLipsGenerates(String name, String destination) throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		GenerationResult result = project.generator().generate(project.eogen(name));
		assertThat(result.getErrors()).isEmpty();
		assertThat(result.isWritten()).isTrue();
		assertThat(result.count(SUPERCLASS, CREATED)).isEqualTo(7);
		assertThat(result.count(SUBCLASS, CREATED)).isEqualTo(7);
		assertSameFiles(FixtureProject.expected(name), project.resolve(destination));
	}

	@Test
	void generatingAgainChangesNothing() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		EOGenerator generator = project.generator();
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
		EOGenerator generator = project.generator();
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
		EOGenerator generator = project.generator();
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
		EOGenerator generator = project.generator();
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
		GenerationResult result = project.generator().generate(eogen);
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
		GenerationResult result = project.generator().generate(eogen);
		assertThat(result.getErrors()).isEmpty();
		assertSameFiles(FixtureProject.expected("ShopClassic"), project.resolve("generated/classic"));
	}

	@Test
	void generatesInAFolderWhosePathHasSpaces() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp.resolve("My Projects"));
		GenerationResult fromJar = project.generator().generate(project.eogen("ShopWonder"));
		assertThat(fromJar.getErrors()).isEmpty();
		assertSameFiles(FixtureProject.expected("ShopWonder"), project.resolve("src/main/java"));
		GenerationResult fromFolder = project.generatorWithFolderOnClasspath().generate(project.eogen("ShopCustom"));
		assertThat(fromFolder.getErrors()).isEmpty();
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
		GenerationResult result = generator.generate(project.eogen("ShopClassic"));
		assertThat(result.getErrors()).isEmpty();
		assertSameFiles(FixtureProject.expected("ShopClassic"), project.resolve("generated/classic"));
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

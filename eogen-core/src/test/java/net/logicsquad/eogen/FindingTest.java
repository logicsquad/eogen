package net.logicsquad.eogen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.logicsquad.eogen.Finding.Severity;
import net.logicsquad.eogen.format.FormatResult;

/**
 * Each kind of finding has its code, and says where it is, on copies of the
 * fixture project with mistakes made in them.
 */
class FindingTest {
	@TempDir
	Path temp;

	@AfterEach
	void releaseJars() throws IOException {
		Jars.release(temp);
	}

	@Test
	void anUnresolvedReferenceNamesItsFileTemplateAndObject() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		List<Finding> errors = errors(project.generator().generate(project.eogen("ShopWonder")).getFindings());
		assertThat(errors).hasSize(2).allSatisfy(finding -> {
			assertThat(finding.getCode()).isEqualTo(Finding.UNRESOLVED_REFERENCE);
			assertThat(finding.getFile()).isEqualTo("src/main/java/com/example/shop/_Order.java");
			assertThat(finding.getEntity()).isEqualTo("Order");
			assertThat(finding.getObject()).isEqualTo("attribute customerName");
			assertThat(finding.getTemplate()).isEqualTo("_WonderEntity.java");
		});
		assertThat(errors).extracting(Finding::getKey).containsExactlyInAnyOrder("$attribute.javaClassName", "${attribute.javaClassName}");
		assertThat(errors.get(0).getMessage()).startsWith("com/example/shop/_Order.java: ");
	}

	@Test
	void aLintFindingNamesItsKeyAndSuggestion() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		Path product = project.resolve("src/main/resources/Shop.eomodeld/Product.plist");
		write(product, read(product).replace("classProperties = (active, category, name, price, tags);", "classProperties = (active, category, name, prise, tags);"));
		String message = "Shop.eomodeld/Product.plist: entity Product: classProperties names prise, which is not an attribute or relationship (did you mean price?).";

		List<Finding> generated = project.forcedGenerator().generate(project.eogen("ShopWonder")).getFindings();
		Finding warning = generated.stream().filter(finding -> finding.getMessage().equals(message)).findFirst().get();
		assertThat(warning.getSeverity()).isEqualTo(Severity.WARNING);
		assertThat(warning.getCode()).isEqualTo(Finding.UNMATCHED_NAME);
		assertThat(warning.getFile()).isEqualTo("src/main/resources/Shop.eomodeld/Product.plist");
		assertThat(warning.getModel()).isEqualTo("Shop");
		assertThat(warning.getEntity()).isEqualTo("Product");
		assertThat(warning.getObject()).isEqualTo("entity Product");
		assertThat(warning.getKey()).isEqualTo("prise");
		assertThat(warning.getSuggestion()).isEqualTo("price");

		EOGenerator strict = project.generator();
		strict.setStrict(true);
		List<Finding> verified = strict.verify(false).getFindings();
		assertThat(verified).hasSize(1);
		assertThat(verified.get(0).getSeverity()).isEqualTo(Severity.ERROR);
		assertThat(verified.get(0).getCode()).isEqualTo(Finding.UNMATCHED_NAME);
		assertThat(verified.get(0).getFile()).isEqualTo("src/main/resources/Shop.eomodeld/Product.plist");
	}

	@Test
	void aSyntaxErrorNamesItsFileLineAndColumn() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		Path tag = project.resolve("src/main/resources/Shop.eomodeld/Tag.plist");
		write(tag, read(tag).replace("name = Tag;", "name = Tag"));
		VerificationResult result = project.generator().verify(false);
		Finding finding = errors(result.getFindings()).get(0);
		assertThat(finding.getCode()).isEqualTo(Finding.SYNTAX);
		assertThat(finding.getFile()).isEqualTo("src/main/resources/Shop.eomodeld/Tag.plist");
		assertThat(finding.getLine()).isPositive();
		assertThat(finding.getColumn()).isPositive();
		assertThat(finding.getModel()).isEqualTo("Shop");
		assertThat(finding.getMessage()).isEqualTo(result.getErrors().get(0).getMessage());
	}

	@Test
	void aModelThatFailsToLoadWhileGeneratingIsASyntaxFinding() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		write(project.resolve("src/main/resources/Shop.eomodeld/index.eomodeld"), "{ entities = ( ");
		EOGeneratorException e = catchThrowableOfType(() -> project.generator().generate(project.eogen("ShopClassic")), EOGeneratorException.class);
		assertThat(e.getFindings()).hasSize(1);
		Finding finding = e.getFindings().get(0);
		assertThat(finding.getCode()).isEqualTo(Finding.SYNTAX);
		assertThat(finding.getFile()).isEqualTo("src/main/resources/Shop.eomodeld/index.eomodeld");
		assertThat(finding.getLine()).isEqualTo(1);
		assertThat(finding.getColumn()).isNull();
	}

	@Test
	void aVerifierFailureNamesWhatItIsAbout() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		Path product = project.resolve("src/main/resources/Shop.eomodeld/Product.plist");
		write(product, read(product).replace("name = price; prototypeName = amount;", "name = price; prototypeNam = amount;"));
		Finding finding = project.generator().verify(false).getFindings().stream().filter(warning -> warning.getCode().equals(Finding.VERIFIER)).findFirst().get();
		assertThat(finding.getSeverity()).isEqualTo(Severity.WARNING);
		assertThat(finding.getMessage()).isEqualTo("The attribute price does not have a value class name or a class name.");
		assertThat(finding.getModel()).isEqualTo("Shop");
		assertThat(finding.getEntity()).isEqualTo("Product");
		assertThat(finding.getObject()).isEqualTo("attribute price");
	}

	/**
	 * Entity Modeler's verifier describes some failures only in text.
	 */
	@Test
	void aVerifierFailureWithOnlyTextNamesItsModel() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		Path order = project.resolve("src/main/resources/Shop.eomodeld/Order.plist");
		write(order, read(order).replace("destination = Currency;", "destination = Nowhere;"));
		Finding finding = errors(project.generator().verify(false).getFindings()).stream().filter(error -> error.getMessage().contains("Nowhere")).findFirst().get();
		assertThat(finding.getCode()).isEqualTo(Finding.VERIFIER);
		assertThat(finding.getMessage()).isEqualTo("Missing entity named 'Nowhere'.");
		assertThat(finding.getModel()).isEqualTo("Shop");
		assertThat(finding.getObject()).isNull();
	}

	@Test
	void aBrokenEOGenFileIsAnEOGenFileFinding() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		File eogen = project.eogen("ShopClassic");
		write(eogen.toPath(), " -model src/main/resources/Shop.eomodeld \\\n -loadModelGroup\n");
		EOGeneratorException e = catchThrowableOfType(() -> project.generator().generate(eogen), EOGeneratorException.class);
		assertThat(e.getFindings()).singleElement().satisfies(finding -> {
			assertThat(finding.getCode()).isEqualTo(Finding.EOGEN_FILE);
			assertThat(finding.getFile()).isEqualTo("src/main/resources/ShopClassic.eogen");
			assertThat(finding.getMessage()).isEqualTo(e.getMessage());
		});
	}

	@Test
	void aModelThatFormattingLeavesAloneIsAFormatLoss() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		Path order = project.resolve("src/main/resources/Shop.eomodeld/Order.plist");
		write(order, read(order).replaceFirst("isMandatory = Y;", "isMandatory = YES;"));
		FormatResult result = project.generator().format(false);
		assertThat(result.getFindings()).singleElement().satisfies(finding -> {
			assertThat(finding.getCode()).isEqualTo(Finding.FORMAT_LOSS);
			assertThat(finding.getFile()).isEqualTo("src/main/resources/Shop.eomodeld/Order.plist");
			assertThat(finding.getModel()).isEqualTo("Shop");
			assertThat(finding.getMessage()).startsWith("src/main/resources/Shop.eomodeld/Order.plist: its model is left alone, since saving it would change what it says: ")
					.endsWith(".isMandatory would change from YES to Y.");
		});
	}

	private static List<Finding> errors(List<Finding> findings) {
		return findings.stream().filter(Finding::isError).collect(Collectors.toList());
	}

	private static String read(Path path) throws IOException {
		return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
	}

	private static void write(Path path, String text) throws IOException {
		Files.write(path, text.getBytes(StandardCharsets.UTF_8));
	}
}

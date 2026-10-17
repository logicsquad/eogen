package net.logicsquad.eogen;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.logicsquad.eogen.format.FormatResult;
import net.logicsquad.eogen.format.FormatResult.FormattedFile;
import net.logicsquad.eogen.format.FormatResult.Status;

/**
 * Formats copies of the fixture's Shop model.
 */
class FormatTest {
	@TempDir
	Path temp;

	@AfterEach
	void releaseJars() throws IOException {
		Jars.release(temp);
	}

	@Test
	void restoresTheLayoutEntityModelerWrites() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		EOGenerator generator = project.generator();
		assertThat(generator.format(true).getRefusedFiles()).isEmpty();
		Path tag = project.resolve("src/main/resources/Shop.eomodeld/Tag.plist");
		String formatted = read(tag);
		assertThat(generator.format(false).getFormattedFiles()).isEmpty();

		String edited = formatted.replace("    ", "  ").replace("; \n", ";\n");
		assertThat(edited).isNotEqualTo(formatted);
		write(tag, edited);
		FormatResult check = generator.format(false);
		assertThat(check.isWritten()).isFalse();
		assertThat(check.getFormattedFiles()).extracting(file -> file.getFile().getName()).containsExactly("Tag.plist");
		assertThat(read(tag)).isEqualTo(edited);

		FormatResult result = generator.format(true);
		assertThat(result.isWritten()).isTrue();
		assertThat(result.getFormattedFiles()).extracting(file -> file.getFile().getName()).containsExactly("Tag.plist");
		assertThat(read(tag)).isEqualTo(formatted);
	}

	/**
	 * Entity Modeler writes a relationship's isMandatory as Y, so YES would
	 * change: the model is left alone, including a file whose layout alone
	 * would change.
	 */
	@Test
	void leavesAModelAloneIfFormattingWouldChangeWhatItSays() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		EOGenerator generator = project.generator();
		generator.format(true);
		Path order = project.resolve("src/main/resources/Shop.eomodeld/Order.plist");
		Path tag = project.resolve("src/main/resources/Shop.eomodeld/Tag.plist");
		String orderText = read(order);
		assertThat(orderText).contains("isMandatory = Y;");
		write(order, orderText.replaceFirst("isMandatory = Y;", "isMandatory = YES;"));
		String tagText = read(tag).replace("    ", "  ");
		write(tag, tagText);

		FormatResult result = generator.format(true);

		assertThat(result.getRefusedFiles()).hasSize(1);
		FormattedFile refused = result.getRefusedFiles().get(0);
		assertThat(refused.getFile().getName()).isEqualTo("Order.plist");
		assertThat(refused.getStatus()).isEqualTo(Status.REFUSED);
		assertThat(refused.getReason()).endsWith(".isMandatory would change from YES to Y");
		assertThat(result.getFormattedFiles()).isEmpty();
		assertThat(read(tag)).isEqualTo(tagText);
	}

	@Test
	void formatsNothingWhenTheModelGroupHasErrors() throws Exception {
		FixtureProject project = FixtureProject.copyTo(temp);
		FormatResult result = project.generatorWithoutDependencies().format(true);
		assertThat(result.getErrors()).anyMatch(error -> error.contains("prototype"));
		assertThat(result.getFiles()).isEmpty();
	}

	private static String read(Path path) throws IOException {
		return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
	}

	private static void write(Path path, String text) throws IOException {
		Files.write(path, text.getBytes(StandardCharsets.UTF_8));
	}
}

package net.logicsquad.eogen.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Reads .eogen files, which are saved EOGenerator command lines.
 */
class EOGeneratorModelTest {
	@TempDir
	Path temp;

	@Test
	void readsAnEOGenFile() throws Exception {
		File file = Paths.get(EOGeneratorModelTest.class.getResource("/fixtures/project/src/main/resources/ShopCustom.eogen").toURI()).toFile();
		EOGeneratorModel model = EOGeneratorModel.createModelFromFile(file, temp);
		assertThat(model.getProjectPath()).isEqualTo(temp);
		assertThat(model.getDestination()).isEqualTo("generated/custom");
		assertThat(model.getSubclassDestination()).isEqualTo("generated/custom");
		assertThat(model.getJavaTemplate()).isEqualTo("_Custom.java");
		assertThat(model.getSubclassJavaTemplate()).isEqualTo("Custom.java");
		assertThat(model.getTemplateDir()).isEqualTo("src/main/templates");
		assertThat(model.getExtension()).isEqualTo("java");
		assertThat(model.getModels()).extracting(EOModelReference::getName).containsExactly("Shop");
		assertThat(model.getModels().get(0).getPath(temp)).isEqualTo("src/main/resources/Shop.eomodeld");
		assertThat(model.isJava()).isTrue();
		assertThat(model.isPackageDirs()).isTrue();
		assertThat(model.isVerbose()).isTrue();
		assertThat(model.isLoadModelGroup()).isTrue();
		assertThat(model.getDefineValueNamed("Greeting")).isEqualTo("Hello from a define");
	}

	@Test
	void extensionDefaultsToJava() throws Exception {
		assertThat(read(" -destination src \\\n -model A.eomodeld\n").getExtension()).isEqualTo("java");
		assertThat(read(" -destination src -extension groovy -model A.eomodeld\n").getExtension()).isEqualTo("groovy");
	}

	@Test
	void backslashContinuesALine() throws Exception {
		assertThat(read(" -destination src/ma\\\nin/java\n").getDestination()).isEqualTo("src/main/java");
	}

	@Test
	void lineBreakWithoutBackslashSeparatesTokens() throws Exception {
		EOGeneratorModel model = read("-verbose\n-java\n-destination src\n");
		assertThat(model.isVerbose()).isTrue();
		assertThat(model.isJava()).isTrue();
		assertThat(model.getDestination()).isEqualTo("src");
	}

	@Test
	void blankFileHasNoSettings() throws Exception {
		EOGeneratorModel model = read("\n  \n\n");
		assertThat(model.getDestination()).isNull();
		assertThat(model.getModels()).isEmpty();
	}

	@Test
	void quotedAndEscapedValuesKeepTheirSpaces() throws Exception {
		EOGeneratorModel model = read(" -define-Text \"two words\" \\\n -templatedir My\\ Templates \\\n -model \"Some Folder/A.eomodeld\"\n");
		assertThat(model.getDefineValueNamed("Text")).isEqualTo("two words");
		assertThat(model.getTemplateDir()).isEqualTo("My Templates");
		assertThat(model.getModels()).extracting(EOModelReference::getName).containsExactly("A");
	}

	@Test
	void firstBareTokenIsTheEOGeneratorPath() throws Exception {
		EOGeneratorModel model = read("/Developer/EOGenerator -destination src -model A.eomodeld\n");
		assertThat(model.getEOGeneratorPath()).isEqualTo("/Developer/EOGenerator");
		assertThat(model.getDestination()).isEqualTo("src");
	}

	@Test
	void templateDirInsideTheProjectIsRelative() throws Exception {
		EOGeneratorModel model = read(" -templatedir " + temp.resolve("templates/wonder") + "\n");
		assertThat(model.getTemplateDir()).isEqualTo("templates/wonder");
	}

	@Test
	void writesWhatItReads() throws Exception {
		EOGeneratorModel model = read(" -destination src/main/java \\\n -javaTemplate _WonderEntity.java \\\n -model src/main/resources/A.eomodeld \\\n -packagedirs \\\n -loadModelGroup \\\n -define-EOGenericRecord com.example.Record\n");
		File file = temp.resolve("copy.eogen").toFile();
		model.writeToFile(file);
		EOGeneratorModel copy = EOGeneratorModel.createModelFromFile(file, temp);
		assertThat(copy.getDestination()).isEqualTo("src/main/java");
		assertThat(copy.getJavaTemplate()).isEqualTo("_WonderEntity.java");
		assertThat(copy.getModels()).extracting(reference -> reference.getPath(temp)).containsExactly("src/main/resources/A.eomodeld");
		assertThat(copy.isPackageDirs()).isTrue();
		assertThat(copy.isLoadModelGroup()).isTrue();
		assertThat(copy.getDefineValueNamed("EOGenericRecord")).isEqualTo("com.example.Record");
	}

	private EOGeneratorModel read(String contents) throws IOException, ParseException {
		Path file = temp.resolve("test.eogen");
		Files.write(file, contents.getBytes(StandardCharsets.UTF_8));
		return EOGeneratorModel.createModelFromFile(file.toFile(), temp);
	}
}

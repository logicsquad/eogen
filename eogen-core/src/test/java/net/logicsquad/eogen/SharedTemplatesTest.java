package net.logicsquad.eogen;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Generates with templates from a template path: a jar with them in
 * eogen/templates, or a folder of them.
 */
class SharedTemplatesTest {
	private static final String SHARED = String.join("\n", " -destination generated/shared \\", " -extension java \\", " -java \\", " -javaTemplate _Shared.java \\",
			" -model src/main/resources/Shop.eomodeld \\", " -packagedirs \\", " -subclassDestination generated/shared \\", " -subclassJavaTemplate Shared.java \\",
			" -loadModelGroup", "");

	private static final String PRODUCT = "generated/shared/com/example/shop/_Product.java";

	@TempDir
	Path temp;

	private FixtureProject project;

	@BeforeEach
	void copyProject() throws IOException {
		project = FixtureProject.copyTo(temp.resolve("project"));
		write(project.resolve("src/main/resources/ShopShared.eogen"), SHARED);
	}

	@AfterEach
	void releaseJars() throws IOException {
		Jars.release(temp);
	}

	@Test
	void findsTemplatesInAJar() throws Exception {
		EOGenerator generator = project.generator();
		generator.setTemplatePath(Collections.singletonList(templatesJar("from a jar").toFile()));
		GenerationResult result = generator.generate(project.eogen("ShopShared"));
		assertThat(result.getErrors()).isEmpty();
		assertThat(read(project.resolve(PRODUCT))).isEqualTo("// from a jar\npublic class _Product {}\n");
		assertThat(read(project.resolve("generated/shared/com/example/shop/Product.java"))).isEqualTo("public class Product extends _Product {}\n");
	}

	@Test
	void findsTemplatesInAFolder() throws Exception {
		Path folder = Files.createDirectories(temp.resolve("templates"));
		write(folder.resolve("_Shared.java"), "// from a folder\npublic class $entity.prefixClassNameWithoutPackage {}\n");
		write(folder.resolve("Shared.java"), "public class $entity.classNameWithoutPackage extends $entity.prefixClassNameWithoutPackage {}\n");
		EOGenerator generator = project.generator();
		generator.setTemplatePath(Collections.singletonList(folder.toFile()));
		assertThat(generator.generate(project.eogen("ShopShared")).getErrors()).isEmpty();
		assertThat(read(project.resolve(PRODUCT))).isEqualTo("// from a folder\npublic class _Product {}\n");
	}

	/**
	 * The template path comes after the .eogen file's -templatedir, so a
	 * project can override a shared template with its own.
	 */
	@Test
	void aProjectsOwnTemplateComesFirst() throws Exception {
		write(project.resolve("src/main/resources/ShopShared.eogen"), SHARED.replace(" -loadModelGroup", " -templatedir src/main/templates \\\n -loadModelGroup"));
		write(project.resolve("src/main/templates/_Shared.java"), "// the project's own\npublic class $entity.prefixClassNameWithoutPackage {}\n");
		EOGenerator generator = project.generator();
		generator.setTemplatePath(Collections.singletonList(templatesJar("from a jar").toFile()));
		assertThat(generator.generate(project.eogen("ShopShared")).getErrors()).isEmpty();
		assertThat(read(project.resolve(PRODUCT))).isEqualTo("// the project's own\npublic class _Product {}\n");
		// The jar still has the subclass template, which the project doesn't.
		assertThat(read(project.resolve("generated/shared/com/example/shop/Product.java"))).isEqualTo("public class Product extends _Product {}\n");
	}

	/**
	 * The earlier entries of a template path come first, and the bundled
	 * templates last.
	 */
	@Test
	void theFirstEntryWithATemplateWinsAndTheBundledOnesStillWork() throws Exception {
		EOGenerator generator = project.generator();
		generator.setTemplatePath(Arrays.asList(templatesJar("first", "from the first jar").toFile(), templatesJar("second", "from the second jar").toFile()));
		assertThat(generator.generate(project.eogen("ShopShared")).getErrors()).isEmpty();
		assertThat(read(project.resolve(PRODUCT))).isEqualTo("// from the first jar\npublic class _Product {}\n");

		// The fixture's flattened customerName has no Java class, which the
		// bundled templates report, so only a forced generator writes.
		EOGenerator forced = project.forcedGenerator();
		forced.setTemplatePath(generator.getTemplatePath());
		GenerationResult classic = forced.generate(project.eogen("ShopClassic"));
		assertThat(classic.isWritten()).isTrue();
		assertThat(classic.getErrors()).isNotEmpty().allMatch(error -> error.contains("for attribute customerName (_Entity.java)"));
	}

	private Path templatesJar(String comment) throws IOException {
		return templatesJar("shared", comment);
	}

	/**
	 * Makes a jar of shared templates, as a team would publish them.
	 */
	private Path templatesJar(String name, String comment) throws IOException {
		Path root = temp.resolve(name);
		Path folder = Files.createDirectories(root.resolve("eogen/templates"));
		write(folder.resolve("_Shared.java"), "// " + comment + "\npublic class $entity.prefixClassNameWithoutPackage {}\n");
		write(folder.resolve("Shared.java"), "public class $entity.classNameWithoutPackage extends $entity.prefixClassNameWithoutPackage {}\n");
		Path jar = temp.resolve(name + ".jar");
		FixtureProject.jar(root, jar);
		return jar;
	}

	private static String read(Path path) throws IOException {
		return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
	}

	private static void write(Path path, String text) throws IOException {
		Files.createDirectories(path.getParent());
		Files.write(path, text.getBytes(StandardCharsets.UTF_8));
	}
}

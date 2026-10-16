package net.logicsquad.eogen.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EOModelGroup;
import net.logicsquad.eogen.model.EOModelVerificationFailure;
import net.logicsquad.eogen.runtime.NullProgressMonitor;

/**
 * Loads model groups from model folders and classpath entries, as
 * -loadModelGroup does.
 */
class ClasspathEOModelGroupFactoryTest {
	@TempDir
	Path temp;

	@Test
	void loadsEveryModelUnderTheModelFolders() throws Exception {
		Path resources = temp.resolve("resources");
		writeModel(resources.resolve("a"), "One", "OneEntity");
		writeModel(resources.resolve("b/c"), "Two", "TwoEntity");
		EOModelGroup group = load(Collections.singletonList(resources.toFile()), Collections.<File>emptyList(), null);
		assertThat(modelNames(group)).containsExactlyInAnyOrder("One", "Two");
	}

	@Test
	void firstModelFoundWithANameWins() throws Exception {
		Path resources = temp.resolve("resources");
		writeModel(resources.resolve("b"), "Dup", "FromB");
		writeModel(resources.resolve("a"), "Dup", "FromA");
		EOModelGroup group = load(Collections.singletonList(resources.toFile()), Collections.<File>emptyList(), null);
		assertThat(entityNames(group.getModelNamed("Dup"))).containsExactly("FromA");
	}

	@Test
	void skipsHiddenComponentAndJavaFolders() throws Exception {
		Path resources = temp.resolve("resources");
		writeModel(resources.resolve(".hidden"), "Hidden", "HiddenEntity");
		writeModel(resources.resolve("Main.wo"), "Component", "ComponentEntity");
		writeModel(resources.resolve("Java"), "Classes", "ClassesEntity");
		writeModel(resources, "Visible", "VisibleEntity");
		EOModelGroup group = load(Collections.singletonList(resources.toFile()), Collections.<File>emptyList(), null);
		assertThat(modelNames(group)).containsExactly("Visible");
	}

	@Test
	void loadsModelsFromJarsAndFoldersOnTheClasspath() throws Exception {
		Path framework = temp.resolve("framework");
		writeModel(framework.resolve("Resources"), "Framework", "FrameworkEntity");
		writeModel(framework.resolve("META-INF/webobjects/Resources"), "Module", "ModuleEntity");
		Path jar = jar(framework, temp.resolve("framework.jar"));
		Path classes = temp.resolve("classes");
		writeModel(classes.resolve("Resources"), "Classes", "ClassesEntity");
		Path plainJar = jar(Files.createDirectories(temp.resolve("plain")), temp.resolve("plain.jar"));
		List<File> classpath = Arrays.asList(jar.toFile(), classes.toFile(), plainJar.toFile(), temp.resolve("missing.jar").toFile());
		EOModelGroup group = load(Collections.<File>emptyList(), classpath, null);
		assertThat(modelNames(group)).containsExactlyInAnyOrder("Framework", "Module", "Classes");
	}

	@Test
	void projectModelsComeBeforeClasspathModels() throws Exception {
		Path resources = temp.resolve("resources");
		writeModel(resources, "Shared", "FromProject");
		Path framework = temp.resolve("framework");
		writeModel(framework.resolve("Resources"), "Shared", "FromJar");
		Path jar = jar(framework, temp.resolve("framework.jar"));
		EOModelGroup group = load(Collections.singletonList(resources.toFile()), Collections.singletonList(jar.toFile()), null);
		assertThat(entityNames(group.getModelNamed("Shared"))).containsExactly("FromProject");
	}

	/**
	 * As in Eclipse, the model the group is for is loaded last, so a model of
	 * the same name found earlier wins.
	 */
	@Test
	void modelTheGroupIsForComesAfterClasspathModels() throws Exception {
		Path elsewhere = temp.resolve("elsewhere");
		writeModel(elsewhere, "Edited", "FromElsewhere");
		Path framework = temp.resolve("framework");
		writeModel(framework.resolve("Resources"), "Edited", "FromJar");
		Path jar = jar(framework, temp.resolve("framework.jar"));
		EOModelGroup group = load(Collections.<File>emptyList(), Collections.singletonList(jar.toFile()), elsewhere.resolve("Edited.eomodeld").toFile());
		assertThat(entityNames(group.getModelNamed("Edited"))).containsExactly("FromJar");
	}

	/**
	 * A dependency's model that declares one of the project model's entities
	 * evicts it, but loading the model the group is for last evicts the
	 * dependency's model in turn, as in Eclipse.
	 */
	@Test
	void modelTheGroupIsForSurvivesADependencyThatSharesAnEntity() throws Exception {
		Path resources = temp.resolve("resources");
		writeModel(resources, "Shop", "Person");
		Path framework = temp.resolve("framework");
		writeModel(framework.resolve("Resources"), "ShopOld", "Person");
		Path jar = jar(framework, temp.resolve("framework.jar"));
		EOModelGroup group = new EOModelGroup();
		Set<EOModelVerificationFailure> failures = new HashSet<>();
		new ClasspathEOModelGroupFactory(Collections.singletonList(resources.toFile()), Collections.singletonList(jar.toFile()))
				.loadModelGroup(resources.resolve("Shop.eomodeld").toFile().toURI().toURL(), group, failures, true, new NullProgressMonitor());
		assertThat(modelNames(group)).containsExactly("Shop");
		assertThat(failures).allMatch(EOModelVerificationFailure::isWarning).isNotEmpty();
	}

	@Test
	void loadsModelsWhosePathsHaveSpaces() throws Exception {
		Path spaced = temp.resolve("My Projects");
		writeModel(spaced.resolve("resources"), "Local", "LocalEntity");
		Path classes = spaced.resolve("framework classes");
		writeModel(classes.resolve("Resources"), "Classes", "ClassesEntity");
		Path framework = spaced.resolve("framework");
		writeModel(framework.resolve("Resources"), "Framework", "FrameworkEntity");
		Path jar = jar(framework, spaced.resolve("framework 1.0.jar"));
		EOModelGroup group = load(Collections.singletonList(spaced.resolve("resources").toFile()), Arrays.asList(classes.toFile(), jar.toFile()), null);
		assertThat(modelNames(group)).containsExactlyInAnyOrder("Local", "Classes", "Framework");
	}

	private static EOModelGroup load(List<File> modelFolders, List<File> classpath, File modelFile) throws Exception {
		EOModelGroup group = new EOModelGroup();
		Set<EOModelVerificationFailure> failures = new HashSet<>();
		Object resource = modelFile == null ? null : modelFile.toURI().toURL();
		new ClasspathEOModelGroupFactory(modelFolders, classpath).loadModelGroup(resource, group, failures, true, new NullProgressMonitor());
		assertThat(failures).isEmpty();
		return group;
	}

	private static List<String> modelNames(EOModelGroup group) {
		return group.getModels().stream().map(EOModel::getName).sorted().collect(Collectors.toList());
	}

	private static List<String> entityNames(EOModel model) {
		return model.getEntities().stream().map(entity -> entity.getName()).sorted().collect(Collectors.toList());
	}

	/**
	 * Writes a model with one entity into a folder.
	 */
	static void writeModel(Path folder, String modelName, String entityName) throws IOException {
		Path model = Files.createDirectories(folder.resolve(modelName + ".eomodeld"));
		write(model.resolve("index.eomodeld"), "{\n    EOModelVersion = \"2.1\"; \n    adaptorName = JDBC; \n    entities = ({className = \"com.example." + entityName
				+ "\"; name = " + entityName + "; }); \n}\n");
		write(model.resolve(entityName + ".plist"), "{\n    attributes = ({allowsNull = N; columnName = id; externalType = integer; name = id; valueClassName = NSNumber; valueType = i; }); \n"
				+ "    className = \"com.example." + entityName + "\"; \n    classProperties = (); \n    externalName = " + entityName.toLowerCase()
				+ "; \n    name = " + entityName + "; \n    primaryKeyAttributes = (id); \n}\n");
	}

	private static void write(Path file, String text) throws IOException {
		Files.write(file, text.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Jars a folder, with folder entries, as Maven's jar plugin does.
	 */
	private static Path jar(Path folder, Path jar) throws IOException {
		List<Path> paths;
		try (Stream<Path> walk = Files.walk(folder)) {
			paths = walk.filter(path -> !path.equals(folder)).sorted().collect(Collectors.toList());
		}
		try (OutputStream out = Files.newOutputStream(jar); JarOutputStream jarOut = new JarOutputStream(out)) {
			for (Path path : paths) {
				String name = folder.relativize(path).toString().replace(File.separatorChar, '/');
				jarOut.putNextEntry(new JarEntry(Files.isDirectory(path) ? name + "/" : name));
				if (!Files.isDirectory(path)) {
					jarOut.write(Files.readAllBytes(path));
				}
				jarOut.closeEntry();
			}
		}
		return jar;
	}
}

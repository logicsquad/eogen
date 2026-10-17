package net.logicsquad.eogen;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import net.logicsquad.eogen.generator.ClasspathEOModelGroupFactory;
import net.logicsquad.eogen.lint.ModelLinter;
import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOEntityIndex;
import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EOModelGroup;
import net.logicsquad.eogen.model.EOModelVerificationFailure;
import net.logicsquad.eogen.runtime.NullProgressMonitor;

/**
 * Generates from the corpus, Project Wonder's models and a ledger with what
 * theirs lack, in {@code src/test/resources/corpus}, and compares every
 * superclass with what WOLips 5.0.3 generated from the same .eogen file with
 * {@code src/test/oracle/run.sh}. The models are as Entity Modeler writes
 * them, so eogen must find no errors in them, and its lint nothing.
 */
class CorpusTest {
	@TempDir
	Path temp;

	private Path project;

	private EOGenerator generator;

	@BeforeEach
	void copyCorpus() throws IOException, URISyntaxException {
		project = temp.resolve("project");
		FixtureProject.copyTree(corpus().resolve("project"), project);
		Path prototypes = temp.resolve("erprototypes.jar");
		FixtureProject.jar(corpus().resolve("framework"), prototypes);
		generator = new EOGenerator(project.toFile(), Collections.singletonList(project.resolve("src/main/resources").toFile()),
				Collections.singletonList(prototypes.toFile()));
	}

	static List<String> eogenFiles() throws IOException, URISyntaxException {
		try (Stream<Path> files = Files.list(corpus().resolve("project/src/main/resources"))) {
			return files.map(path -> path.getFileName().toString()).filter(name -> name.endsWith(".eogen")).map(name -> name.substring(0, name.length() - ".eogen".length()))
					.sorted().collect(Collectors.toList());
		}
	}

	@ParameterizedTest
	@MethodSource("eogenFiles")
	void generatesWhatWOLipsGenerates(String name) throws Exception {
		GenerationResult result = generator.generate(project.resolve("src/main/resources/" + name + ".eogen").toFile());
		assertThat(result.getErrors()).isEmpty();
		assertThat(result.getWarnings()).isEmpty();
		assertThat(result.isWritten()).isTrue();

		Path expected = corpus().resolve("expected/" + name);
		Path generated = project.resolve("generated/" + name);
		List<String> superclasses = superclasses(expected);
		assertThat(superclasses).isNotEmpty();
		assertThat(superclasses(generated)).containsExactlyElementsOf(superclasses);
		for (String superclass : superclasses) {
			assertThat(generated.resolve(superclass)).as(superclass).hasSameBinaryContentAs(expected.resolve(superclass));
		}
		assertThat(generator.check(project.resolve("src/main/resources/" + name + ".eogen").toFile()).isStale()).isFalse();
	}

	@Test
	void verifiesAndLintsWithoutAProblem() throws Exception {
		VerificationResult result = generator.verify(false);
		assertThat(result.getModelNames()).hasSize(11);
		assertThat(result.getFailures()).isEmpty();
		for (File model : corpus().resolve("project/src/main/resources").toFile().listFiles(file -> file.getName().endsWith(".eomodeld"))) {
			assertThat(ModelLinter.lint(model)).as(model.getName()).isEmpty();
		}
		assertThat(ModelLinter.lint(corpus().resolve("framework/Resources/erprototypes.eomodeld").toFile())).isEmpty();
	}

	/**
	 * The ledger has the parts of a model that templates rarely touch, but that
	 * loading must read.
	 */
	@Test
	void loadsStoredProceduresAndEntityIndexes() throws Exception {
		java.util.Set<EOModelVerificationFailure> failures = new java.util.LinkedHashSet<>();
		EOModelGroup group = new EOModelGroup();
		new ClasspathEOModelGroupFactory(Collections.singletonList(project.resolve("src/main/resources").toFile()), Collections.singletonList(temp.resolve("erprototypes.jar").toFile()))
				.loadModelGroup(null, group, failures, true, new NullProgressMonitor());
		group.resolve(failures);
		assertThat(failures).isEmpty();
		EOModel ledger = group.getModelNamed("Ledger");
		assertThat(ledger.getStoredProcedures()).extracting(procedure -> procedure.getName()).containsExactlyInAnyOrder("closeAccounts", "fetchAccounts");
		assertThat(ledger.getStoredProcedureNamed("closeAccounts").getExternalName()).isEqualTo("close_accounts");
		assertThat(ledger.getStoredProcedureNamed("closeAccounts").getArguments()).extracting(argument -> argument.getName()).containsExactly("before", "closed");
		EOEntity account = ledger.getEntityNamed("Account");
		assertThat(account.getFetchAllProcedure().getName()).isEqualTo("fetchAccounts");
		assertThat(account.getEntityIndexes()).hasSize(1);
		EOEntityIndex index = account.getEntityIndexes().iterator().next();
		assertThat(index.getName()).isEqualTo("account_number");
		assertThat(index.getAttributes()).extracting(attribute -> attribute.getName()).containsExactly("number");
	}

	/**
	 * Entity Modeler wrote these models, so saving them changes nothing they
	 * say.
	 */
	@Test
	void formattingChangesNothingTheModelsSay() throws Exception {
		assertThat(generator.format(false).getRefusedFiles()).isEmpty();
	}

	@Test
	void findsNoOrphans() throws Exception {
		List<GenerationResult> results = new java.util.ArrayList<>();
		for (String name : eogenFiles()) {
			results.add(generator.generate(project.resolve("src/main/resources/" + name + ".eogen").toFile()));
		}
		assertThat(generator.findOrphans(results)).isEmpty();
	}

	private static List<String> superclasses(Path folder) throws IOException {
		return FixtureProject.files(folder).stream().filter(file -> Paths.get(file).getFileName().toString().startsWith("_")).collect(Collectors.toList());
	}

	static Path corpus() throws URISyntaxException {
		return Paths.get(CorpusTest.class.getResource("/corpus").toURI());
	}
}

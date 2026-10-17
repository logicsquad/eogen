package net.logicsquad.eogen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.logicsquad.eogen.refactor.RefactorResult;
import net.logicsquad.eogen.refactor.RefactorResult.ChangedFile;
import net.logicsquad.eogen.refactor.RefactorResult.Status;

/**
 * Renames and removes entities, attributes and relationships in a copy of the
 * corpus, formatted first so that its files are as Entity Modeler writes them.
 */
class RefactorTest {
	@TempDir
	Path temp;

	private Path project;

	private Path resources;

	private EOGenerator generator;

	@AfterEach
	void releaseJars() throws IOException {
		Jars.release(temp);
	}

	@BeforeEach
	void copyCorpus() throws Exception {
		project = temp.resolve("project");
		FixtureProject.copyTree(CorpusTest.corpus().resolve("project"), project);
		resources = project.resolve("src/main/resources");
		Path prototypes = temp.resolve("erprototypes.jar");
		FixtureProject.jar(CorpusTest.corpus().resolve("framework"), prototypes);
		generator = new EOGenerator(project.toFile(), Collections.singletonList(resources.toFile()), Collections.singletonList(prototypes.toFile()));
		assertThat(generator.format(true).getRefusedFiles()).isEmpty();
	}

	@Test
	void renamesAnAttributeAndTheKeysThatNameIt() throws Exception {
		Map<String, String> original = modelFiles();
		RefactorResult result = generator.rename("Movie.title", "name", false, true);
		assertThat(result.isRefused()).isFalse();
		assertThat(result.isWritten()).isTrue();
		assertThat(files(result)).contains(tuple("Movies.eomodeld/Movie.plist", Status.CHANGED), tuple("Movies.eomodeld/Movie.fspec", Status.CHANGED));
		String movie = read("Movies.eomodeld/Movie.plist");
		assertThat(movie).contains("name = name;").doesNotContain("name = title;");
		// The column stays as it was.
		assertThat(movie).contains("columnName = TITLE;");
		String fetchSpecifications = read("Movies.eomodeld/Movie.fspec");
		assertThat(fetchSpecifications).contains("rawRowKeyPaths = (movieID, name);", "key = name; selectorName = \"compareAscending:\";")
				.doesNotContain("key = title;");
		assertWorks();

		assertThat(generator.rename("Movie.name", "title", false, true).isRefused()).isFalse();
		assertThat(differences(original, modelFiles())).isEmpty();
	}

	@Test
	void renamesAJoinsAttributeInBothEntities() throws Exception {
		RefactorResult result = generator.rename("Movie.studioID", "studioIdentifier", false, true);
		assertThat(files(result)).contains(tuple("Movies.eomodeld/Movie.plist", Status.CHANGED), tuple("Movies.eomodeld/Studio.plist", Status.CHANGED));
		assertThat(read("Movies.eomodeld/Movie.plist")).contains("joins = ({destinationAttribute = studioID; sourceAttribute = studioIdentifier; });");
		assertThat(read("Movies.eomodeld/Studio.plist")).contains("joins = ({destinationAttribute = studioIdentifier; sourceAttribute = studioID; });");
		assertWorks();
	}

	@Test
	void renamesARelationshipAndTheKeyPathsAndDefinitionsThroughIt() throws Exception {
		assertThat(generator.rename("Movie.studio", "company", false, true).isRefused()).isFalse();
		String fetchSpecifications = read("Movies.eomodeld/Movie.fspec");
		assertThat(fetchSpecifications).contains("key = company;", "key = \"company.name\";").doesNotContain("key = studio;", "\"studio.name\"");

		assertThat(generator.rename("Movie.toDirector", "directorships", false, true).isRefused()).isFalse();
		assertThat(read("Movies.eomodeld/Movie.plist")).contains("definition = \"directorships.toTalent\";");
		// Talent's own toDirector is another relationship.
		assertThat(read("Movies.eomodeld/Talent.plist")).contains("definition = \"toDirector.toMovie\";");
		assertWorks();
	}

	@Test
	void renamesAnEntityAndItsFiles() throws Exception {
		Map<String, String> original = modelFiles();
		RefactorResult result = generator.rename("Studio", "Distributor", true, true);
		assertThat(result.isRefused()).isFalse();
		assertThat(files(result)).containsExactlyInAnyOrder(tuple("Movies.eomodeld/Distributor.fspec", Status.CREATED), tuple("Movies.eomodeld/Distributor.plist", Status.CREATED),
				tuple("Movies.eomodeld/Movie.plist", Status.CHANGED), tuple("Movies.eomodeld/Studio.fspec", Status.DELETED),
				tuple("Movies.eomodeld/Studio.plist", Status.DELETED), tuple("Movies.eomodeld/index.eomodeld", Status.CHANGED));
		assertThat(Files.exists(resources.resolve("Movies.eomodeld/Studio.plist"))).isFalse();
		String company = read("Movies.eomodeld/Distributor.plist");
		assertThat(company).contains("name = Distributor;", "className = \"webobjectsexamples.businesslogic.movies.server.Distributor\";", "externalName = studio;");
		assertThat(read("Movies.eomodeld/Movie.plist")).contains("destination = Distributor;");
		assertThat(read("Movies.eomodeld/index.eomodeld")).doesNotContain("_deletedEntityNamesInObjectStore");
		assertThat(result.getFindings()).extracting(Finding::getCode).containsOnly(Finding.BY_HAND);
		assertThat(result.getFindings()).anyMatch(finding -> finding.getMessage()
				.startsWith("Rename the subclass webobjectsexamples.businesslogic.movies.server.Studio to webobjectsexamples.businesslogic.movies.server.Distributor by hand"));
		assertWorks();

		assertThat(generator.rename("Distributor", "Studio", true, true).isRefused()).isFalse();
		assertThat(differences(original, modelFiles())).isEmpty();
	}

	@Test
	void anEntitysClassStaysUnlessAsked() throws Exception {
		RefactorResult result = generator.rename("Studio", "Distributor", false, true);
		assertThat(result.isRefused()).as(String.valueOf(result.getFindings())).isFalse();
		assertThat(read("Movies.eomodeld/Distributor.plist")).contains("className = \"webobjectsexamples.businesslogic.movies.server.Studio\";");
	}

	@Test
	void writesNothingWhenOnlyReporting() throws Exception {
		Map<String, String> original = modelFiles();
		RefactorResult result = generator.rename("Movie.title", "name", false, false);
		assertThat(result.isWritten()).isFalse();
		assertThat(result.getFiles()).isNotEmpty();
		assertThat(differences(original, modelFiles())).isEmpty();
	}

	@Test
	void refusesNamesThatAreTakenAndTargetsItCantFind() throws Exception {
		Map<String, String> original = modelFiles();
		assertThat(refusal(generator.rename("Movie.title", "category", false, true))).isEqualTo(tuple(Finding.NAME_TAKEN,
				"Movie already has a property named category, so Movie.title can't be renamed to it."));
		assertThat(refusal(generator.rename("Studio", "Movie", false, true))).isEqualTo(tuple(Finding.NAME_TAKEN,
				"There is already an entity named Movie, so Studio can't be renamed to it."));
		assertThat(refusal(generator.rename("Movie.nothing", "something", false, true))).isEqualTo(tuple(Finding.NOT_FOUND,
				"Movie has no attribute or relationship named nothing."));
		assertThat(refusal(generator.remove("Nothing", true))).isEqualTo(tuple(Finding.NOT_FOUND, "There is no entity named Nothing."));
		assertThat(refusal(generator.rename("EOJDBCAccessPrototypes", "Prototypes", false, true))).isEqualTo(tuple(Finding.IN_DEPENDENCY,
				"EOJDBCAccessPrototypes is in erprototypes, which isn't one of the project's models, so eogen doesn't change it."));
		assertThat(differences(original, modelFiles())).isEmpty();
	}

	@Test
	void refusesToRemoveWhatSomethingUses() throws Exception {
		Map<String, String> original = modelFiles();
		assertThat(refusals(generator.remove("Movie.studioID", true))).containsExactlyInAnyOrder(
				"Movie.studioID is in use, so it isn't removed: studioID is referenced by the relationship movies.",
				"Movie.studioID is in use, so it isn't removed: studioID is referenced by the relationship studio.");
		assertThat(refusals(generator.remove("Movie.studio", true)))
				.contains("Movie.studio is in use, so it isn't removed: studio is in Movie's fetch specification QualifierVariable: qualifier key studio.name.",
						"Movie.studio is in use, so it isn't removed: studio is in Movie's fetch specification DeepFetchOneMovie: prefetching key path studio.");
		assertThat(refusals(generator.remove("Account", true))).singleElement().asString().startsWith("Account is in use, so it isn't removed: ");
		assertThat(differences(original, modelFiles())).isEmpty();
	}

	@Test
	void removesWhatNothingUses() throws Exception {
		RefactorResult attribute = generator.remove("Movie.posterName", true);
		assertThat(attribute.isRefused()).isFalse();
		assertThat(read("Movies.eomodeld/Movie.plist")).doesNotContain("posterName");

		RefactorResult entity = generator.remove("ERTag", true);
		assertThat(entity.isRefused()).isFalse();
		assertThat(files(entity)).contains(tuple("ERTaggable.eomodeld/ERTag.plist", Status.DELETED), tuple("ERTaggable.eomodeld/index.eomodeld", Status.CHANGED));
		assertThat(read("ERTaggable.eomodeld/index.eomodeld")).doesNotContain("ERTag;");
		assertThat(entity.getFindings()).singleElement().satisfies(finding -> assertThat(finding.getMessage()).startsWith("Generate with deleteOrphans"));
		assertThat(generator.verify(false).getErrors()).isEmpty();
	}

	/**
	 * After a change, the models load, verify and lint with no findings, and
	 * generate without errors.
	 */
	private void assertWorks() throws Exception {
		assertThat(generator.verify(false).getFailures()).isEmpty();
		for (String name : CorpusTest.eogenFiles()) {
			assertThat(generator.generate(resources.resolve(name + ".eogen").toFile()).getFindings()).as(name).isEmpty();
		}
	}

	private Object refusal(RefactorResult result) {
		assertThat(result.isRefused()).isTrue();
		assertThat(result.getFindings()).hasSize(1);
		Finding finding = result.getFindings().get(0);
		return tuple(finding.getCode(), finding.getMessage());
	}

	private List<String> refusals(RefactorResult result) {
		assertThat(result.isRefused()).isTrue();
		assertThat(result.getFindings()).extracting(Finding::getCode).containsOnly(Finding.IN_USE);
		return result.getFindings().stream().map(Finding::getMessage).collect(Collectors.toList());
	}

	private List<Object> files(RefactorResult result) {
		return result.getFiles().stream().map((ChangedFile file) -> (Object) tuple(resources.relativize(file.getFile().toPath()).toString().replace(File.separatorChar, '/'), file.getStatus()))
				.collect(Collectors.toList());
	}

	private Map<String, String> modelFiles() throws IOException {
		Map<String, String> files = new TreeMap<>();
		try (Stream<Path> paths = Files.walk(resources)) {
			for (Path path : paths.filter(Files::isRegularFile).filter(path -> path.toString().contains(".eomodeld")).collect(Collectors.toList())) {
				files.put(resources.relativize(path).toString().replace(File.separatorChar, '/'), new String(Files.readAllBytes(path), StandardCharsets.UTF_8));
			}
		}
		return files;
	}

	/**
	 * Returns the files that differ between two snapshots, with the first line
	 * that differs in each.
	 */
	private static List<String> differences(Map<String, String> before, Map<String, String> after) {
		List<String> differences = new java.util.ArrayList<>();
		java.util.Set<String> names = new java.util.TreeSet<>(before.keySet());
		names.addAll(after.keySet());
		for (String name : names) {
			String old = before.get(name);
			String now = after.get(name);
			if (old == null || now == null) {
				differences.add(name + (old == null ? " is new" : " is gone"));
			} else if (!old.equals(now)) {
				String[] oldLines = old.split("\n", -1);
				String[] newLines = now.split("\n", -1);
				int line = 0;
				while (line < Math.min(oldLines.length, newLines.length) && oldLines[line].equals(newLines[line])) {
					line++;
				}
				differences.add(name + ":" + (line + 1) + ": " + (line < oldLines.length ? oldLines[line].trim() : "") + " | " + (line < newLines.length ? newLines[line].trim() : ""));
			}
		}
		return differences;
	}

	private String read(String path) throws IOException {
		return new String(Files.readAllBytes(resources.resolve(path)), StandardCharsets.UTF_8);
	}
}

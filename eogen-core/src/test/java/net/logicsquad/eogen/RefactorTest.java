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

import net.logicsquad.eogen.format.FormatResult.FormattedFile;
import net.logicsquad.eogen.refactor.RefactorResult;
import net.logicsquad.eogen.refactor.RefactorResult.ChangedFile;
import net.logicsquad.eogen.refactor.RefactorResult.Status;

/**
 * Renames and removes entities, attributes and relationships in a copy of the
 * corpus, formatted first so that its files are as Entity Modeler writes them,
 * but for the two models that formatting leaves alone.
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
		// Formatting leaves BugTracker and ERMail alone, as CorpusTest shows.
		assertThat(generator.format(true).getRefusedFiles()).extracting(file -> file.getFile().getName()).containsExactlyInAnyOrder("People.fspec", "ERCMailMessage.fspec");
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

	/**
	 * A rename that changes only the case of an entity's name renames its
	 * files, even where the file system ignores case, as macOS's and Windows's
	 * do unless told otherwise.
	 */
	@Test
	void renamesAnEntitysFilesWhenOnlyTheCaseChanges() throws Exception {
		Map<String, String> original = modelFiles();
		RefactorResult result = generator.rename("Studio", "STUDIO", false, true);
		assertThat(result.isRefused()).as(String.valueOf(result.getFindings())).isFalse();
		assertThat(files(result)).contains(tuple("Movies.eomodeld/STUDIO.fspec", Status.CREATED), tuple("Movies.eomodeld/STUDIO.plist", Status.CREATED),
				tuple("Movies.eomodeld/Studio.fspec", Status.DELETED), tuple("Movies.eomodeld/Studio.plist", Status.DELETED));
		assertThat(modelFiles().keySet()).contains("Movies.eomodeld/STUDIO.fspec", "Movies.eomodeld/STUDIO.plist").doesNotContain("Movies.eomodeld/Studio.fspec",
				"Movies.eomodeld/Studio.plist");
		assertThat(read("Movies.eomodeld/STUDIO.plist")).contains("name = STUDIO;");
		assertThat(result.getFindings()).extracting(Finding::getMessage).contains("Studio and STUDIO differ only in case, so where the file system ignores case,"
				+ " version control may keep the entity's files under their old names. Rename them there too, as with git mv Studio.plist STUDIO.plist.");
		assertWorks();

		assertThat(generator.rename("STUDIO", "Studio", false, true).isRefused()).isFalse();
		assertThat(differences(original, modelFiles())).isEmpty();

		// With the class, which is named for the entity, its files too.
		assertThat(generator.rename("Studio", "STUDIO", true, false).getFindings()).extracting(Finding::getMessage).contains("Studio and STUDIO differ only in case,"
				+ " so where the file system ignores case, version control may keep the entity's files, and its classes', such as Studio.java and _Studio.java, under"
				+ " their old names. Rename them there too, as with git mv Studio.plist STUDIO.plist.");
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
	 * Entity Modeler writes a sort ordering with only its class, key and
	 * selectorName, so writing Movie.fspec would drop a misspelt selectorName.
	 * A change that would write it is refused, even when only reporting, and
	 * format names the same loss.
	 */
	@Test
	void refusesAChangeThatWouldDropPartOfAFile() throws Exception {
		misspellASortOrderingsSelector();
		Map<String, String> original = modelFiles();
		String loss = "RawFetchAllMovies.sortOrderings[0].selector would be dropped";
		for (boolean write : new boolean[] { false, true }) {
			RefactorResult result = generator.rename("Movie.title", "name", false, write);
			assertThat(result.isWritten()).isFalse();
			assertThat(result.getFiles()).isEmpty();
			assertThat(refusal(result)).isEqualTo(tuple(Finding.FORMAT_LOSS, "src/main/resources/Movies.eomodeld/Movie.fspec: nothing is changed, since saving it would change what it says: "
					+ loss + ". Fix that by hand first; format names the same loss."));
			assertThat(result.getFindings().get(0)).extracting(Finding::getFile, Finding::getModel).containsExactly("src/main/resources/Movies.eomodeld/Movie.fspec", "Movies");
		}
		assertThat(differences(original, modelFiles())).isEmpty();
		assertThat(generator.format(false).getRefusedFiles()).extracting(file -> file.getFile().getName(), FormattedFile::getReason).contains(tuple("Movie.fspec", loss));
	}

	/**
	 * A change that doesn't write Movie.fspec goes ahead, and leaves its
	 * misspelt key as it is.
	 */
	@Test
	void goesAheadIfItDoesntWriteTheFile() throws Exception {
		misspellASortOrderingsSelector();
		RefactorResult result = generator.rename("Movie.posterName", "posterFileName", false, true);
		assertThat(result.isRefused()).as(String.valueOf(result.getFindings())).isFalse();
		assertThat(files(result)).containsExactly(tuple("Movies.eomodeld/Movie.plist", Status.CHANGED));
		assertThat(read("Movies.eomodeld/Movie.plist")).contains("name = posterFileName;");
		assertThat(read("Movies.eomodeld/Movie.fspec")).contains("selector = \"compareAscending:\";");
	}

	/**
	 * Entity Modeler writes a relationship's isMandatory as Y, so a removal
	 * that would write an entity that says YES is refused too.
	 */
	@Test
	void refusesARemovalThatWouldChangeWhatAFileSays() throws Exception {
		Path movie = resources.resolve("Movies.eomodeld/Movie.plist");
		write(movie, read("Movies.eomodeld/Movie.plist").replaceFirst("isMandatory = Y;", "isMandatory = YES;"));
		Map<String, String> original = modelFiles();
		assertThat(refusal(generator.remove("Movie.posterName", true))).isEqualTo(tuple(Finding.FORMAT_LOSS,
				"src/main/resources/Movies.eomodeld/Movie.plist: nothing is changed, since saving it would change what it says: relationships[plotSummary].isMandatory would change from YES to Y. Fix that by hand first; format names the same loss."));
		assertThat(differences(original, modelFiles())).isEmpty();
	}

	/**
	 * Renaming Studio to Distributor would write Distributor.plist over a file
	 * that index.eomodeld doesn't list, so it is refused, even when only
	 * reporting.
	 */
	@Test
	void refusesToRenameOverAFileThatIsntTheEntitys() throws Exception {
		write(resources.resolve("Movies.eomodeld/Distributor.plist"), "{\n    name = Distributor; \n}\n");
		Map<String, String> original = modelFiles();
		Object refusal = tuple(Finding.NAME_TAKEN,
				"There is already a file at src/main/resources/Movies.eomodeld/Distributor.plist, so Studio can't be renamed to Distributor. Move it away first.");
		for (boolean write : new boolean[] { false, true }) {
			RefactorResult result = generator.rename("Studio", "Distributor", true, write);
			assertThat(refusal(result)).isEqualTo(refusal);
			assertThat(result.getFindings().get(0)).extracting(Finding::getFile, Finding::getModel, Finding::getEntity)
					.containsExactly("src/main/resources/Movies.eomodeld/Distributor.plist", "Movies", "Studio");
		}
		assertThat(differences(original, modelFiles())).isEmpty();
	}

	/**
	 * Review has no fetch specifications, so renaming it to Critique writes no
	 * Critique.fspec, and the model would load a stale one as Critique's. Once
	 * it is moved away, the rename goes ahead.
	 */
	@Test
	void refusesToRenameOntoAStaleFile() throws Exception {
		Path stale = resources.resolve("Movies.eomodeld/Critique.fspec");
		write(stale, read("Movies.eomodeld/Studio.fspec"));
		Map<String, String> original = modelFiles();
		assertThat(refusal(generator.rename("Review", "Critique", false, true))).isEqualTo(tuple(Finding.NAME_TAKEN,
				"There is already a file at src/main/resources/Movies.eomodeld/Critique.fspec, so Review can't be renamed to Critique. Move it away first."));
		assertThat(differences(original, modelFiles())).isEmpty();

		Files.delete(stale);
		RefactorResult result = generator.rename("Review", "Critique", false, true);
		assertThat(result.isRefused()).as(String.valueOf(result.getFindings())).isFalse();
		assertThat(files(result)).contains(tuple("Movies.eomodeld/Critique.plist", Status.CREATED), tuple("Movies.eomodeld/Review.plist", Status.DELETED));
		assertThat(Files.exists(stale)).isFalse();
		assertWorks();
	}

	/**
	 * Project Wonder's BugTracker names the selectors in People's login fetch
	 * specification without their colon, as isEqualTo, which Entity Modeler
	 * writes as isEqualTo:. As format does, a change that would write People's
	 * fetch specifications, as People.fspec or as a renamed entity's file, is
	 * refused until the colons are added by hand.
	 */
	@Test
	void refusesToWriteSelectorsWithoutTheirColonUntilTheyAreFixed() throws Exception {
		Map<String, String> original = modelFiles();
		Object refusal = tuple(Finding.FORMAT_LOSS,
				"src/main/resources/BugTracker.eomodeld/People.fspec: nothing is changed, since saving it would change what it says: login.qualifier.qualifiers[0].selectorName would change from isEqualTo to isEqualTo:. Fix that by hand first; format names the same loss.");
		assertThat(refusal(generator.rename("People.login", "username", false, true))).isEqualTo(refusal);
		assertThat(refusal(generator.rename("People", "Member", false, true))).isEqualTo(refusal);
		assertThat(differences(original, modelFiles())).isEmpty();

		RefactorResult email = generator.rename("People.email", "emailAddress", false, true);
		assertThat(email.isRefused()).as(String.valueOf(email.getFindings())).isFalse();
		assertThat(files(email)).containsExactly(tuple("BugTracker.eomodeld/People.plist", Status.CHANGED));

		Path people = resources.resolve("BugTracker.eomodeld/People.fspec");
		write(people, read("BugTracker.eomodeld/People.fspec").replace("selectorName = isEqualTo;", "selectorName = \"isEqualTo:\";"));
		RefactorResult login = generator.rename("People.login", "username", false, true);
		assertThat(login.isRefused()).as(String.valueOf(login.getFindings())).isFalse();
		assertThat(read("BugTracker.eomodeld/People.fspec")).contains("key = username;", "selectorName = \"isEqualTo:\";");
		RefactorResult entity = generator.rename("People", "Member", false, true);
		assertThat(entity.isRefused()).as(String.valueOf(entity.getFindings())).isFalse();
		assertThat(files(entity)).contains(tuple("BugTracker.eomodeld/Member.fspec", Status.CREATED), tuple("BugTracker.eomodeld/People.fspec", Status.DELETED));
		assertWorks();
	}

	/**
	 * Misspells selectorName as selector in the sort ordering of Movie's
	 * RawFetchAllMovies.
	 */
	private void misspellASortOrderingsSelector() throws IOException {
		String fetchSpecifications = read("Movies.eomodeld/Movie.fspec");
		String sortOrdering = "sortOrderings = ({class = EOSortOrdering; key = title; selectorName = \"compareAscending:\"; });";
		assertThat(fetchSpecifications).contains(sortOrdering);
		write(resources.resolve("Movies.eomodeld/Movie.fspec"), fetchSpecifications.replace(sortOrdering, sortOrdering.replace("selectorName", "selector")));
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

	private static void write(Path path, String text) throws IOException {
		Files.write(path, text.getBytes(StandardCharsets.UTF_8));
	}
}

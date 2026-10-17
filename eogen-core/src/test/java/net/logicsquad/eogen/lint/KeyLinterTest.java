package net.logicsquad.eogen.lint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.logicsquad.eogen.Finding;
import net.logicsquad.eogen.generator.ClasspathEOModelGroupFactory;
import net.logicsquad.eogen.model.EOModelGroup;
import net.logicsquad.eogen.model.EOModelVerificationFailure;
import net.logicsquad.eogen.runtime.NullProgressMonitor;

/**
 * Lints the keys of copies of the corpus's models, with mistakes made in them.
 */
class KeyLinterTest {
	@TempDir
	Path temp;

	private Path models;

	@BeforeEach
	void copyCorpus() throws IOException, URISyntaxException {
		Path corpus = Paths.get(KeyLinterTest.class.getResource("/corpus").toURI());
		models = temp.resolve("resources");
		copyTree(corpus.resolve("project/src/main/resources"), models);
		copyTree(corpus.resolve("framework"), temp.resolve("framework"));
	}

	@Test
	void findsNothingInTheCorpus() throws Exception {
		assertThat(messages()).isEmpty();
	}

	@Test
	void findsAQualifierKeyThatARenameLeftBehind() throws Exception {
		Path people = models.resolve("BugTracker.eomodeld/People.plist");
		edit(people, "name = login;", "name = username;", 1);
		edit(people, "(?m)^(\\s+)login, $", "$1username, ", 3);
		// The qualifier compares login with the variable $username, which isn't a key.
		assertThat(messages()).containsExactly("BugTracker.eomodeld/People.fspec: fetch specification login: qualifier key login matches nothing in People.");
	}

	@Test
	void findsASortKeyWithWhatItProbablyMeant() throws Exception {
		edit(models.resolve("Movies.eomodeld/Movie.fspec"), "key = title; selectorName = \"compareAscending:\";", "key = titel; selectorName = \"compareAscending:\";", 1);
		assertThat(messages()).containsExactly(
				"Movies.eomodeld/Movie.fspec: fetch specification RawFetchAllMovies: sort key titel matches nothing in Movie (did you mean title?).");
	}

	@Test
	void followsKeyPathsThroughRelationshipsIntoOtherModels() throws Exception {
		edit(models.resolve("Rentals.eomodeld/Unit.fspec"), "\"video.movie\"", "\"video.movie.stuido\"", 1);
		edit(models.resolve("Movies.eomodeld/Movie.fspec"), "rawRowKeyPaths = (movieID, title);", "rawRowKeyPaths = (movieID, title.length);", 1);
		assertThat(messages()).containsExactly(
				"Movies.eomodeld/Movie.fspec: fetch specification RawFetchAllMovies: raw-row key path title.length matches nothing: title is an attribute of Movie, not a relationship.",
				"Rentals.eomodeld/Unit.fspec: fetch specification prefetchVideoMovie: prefetching key path video.movie.stuido matches nothing: Movie has no stuido (did you mean video.movie.studio?).");
	}

	@Test
	void namesTheKeyAndTheKeyPathMostLikelyMeant() throws Exception {
		edit(models.resolve("Rentals.eomodeld/Unit.fspec"), "\"video.movie\"", "\"video.movie.stuido\"", 1);
		assertThat(findings()).extracting(LintFinding::getCode, LintFinding::getPath, LintFinding::getEntity, LintFinding::getObject, LintFinding::getKey,
				LintFinding::getSuggestion).containsExactly(tuple(Finding.UNMATCHED_KEY, "Rentals.eomodeld/Unit.fspec", "Unit", "fetch specification prefetchVideoMovie",
						"video.movie.stuido", "video.movie.studio"));
	}

	@Test
	void findsRestrictingQualifierKeys() throws Exception {
		edit(models.resolve("ERAttachment.eomodeld/ERDatabaseAttachment.plist"), "restrictingQualifier = \"storageType = \\\"db\\\"\";",
				"restrictingQualifier = \"storagetype = \\\"db\\\"\";", 1);
		assertThat(messages()).containsExactly(
				"ERAttachment.eomodeld/ERDatabaseAttachment.plist: entity ERDatabaseAttachment: restrictingQualifier key storagetype matches nothing in ERDatabaseAttachment (did you mean storageType?).");
	}

	/**
	 * Loads and resolves the model group, and lints the keys of each of the
	 * copied models.
	 */
	private List<String> messages() throws Exception {
		List<String> messages = new ArrayList<>();
		for (LintFinding finding : findings()) {
			messages.add(finding.toString());
		}
		return messages;
	}

	private List<LintFinding> findings() throws Exception {
		EOModelGroup group = new EOModelGroup();
		Set<EOModelVerificationFailure> failures = new LinkedHashSet<>();
		new ClasspathEOModelGroupFactory(Arrays.asList(models.toFile(), temp.resolve("framework").toFile()), new ArrayList<>()).loadModelGroup(null, group,
				failures, true, new NullProgressMonitor());
		group.resolve(failures);
		List<LintFinding> findings = new ArrayList<>();
		File[] folders = models.toFile().listFiles(file -> file.getName().endsWith(".eomodeld"));
		Arrays.sort(folders);
		for (File folder : folders) {
			findings.addAll(KeyLinter.lint(group.getModelNamed(EOModelGroup.getModelNameForFile(folder)), folder));
		}
		return findings;
	}

	private static void edit(Path file, String regex, String replacement, int times) throws IOException {
		String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
		Matcher matcher = Pattern.compile(regex.startsWith("(?m)") ? regex : Pattern.quote(regex)).matcher(text);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		assertThat(count).as(regex).isEqualTo(times);
		String edited = regex.startsWith("(?m)") ? text.replaceAll(regex, replacement) : text.replace(regex, replacement);
		Files.write(file, edited.getBytes(StandardCharsets.UTF_8));
	}

	private static void copyTree(Path source, Path target) throws IOException {
		try (Stream<Path> paths = Files.walk(source)) {
			for (Path path : paths.collect(Collectors.toList())) {
				Path copy = target.resolve(source.relativize(path).toString());
				if (Files.isDirectory(path)) {
					Files.createDirectories(copy);
				} else {
					Files.copy(path, copy);
				}
			}
		}
	}
}

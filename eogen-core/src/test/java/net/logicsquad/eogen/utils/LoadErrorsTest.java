package net.logicsquad.eogen.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.Test;

import net.logicsquad.eogen.model.EOModelException;

class LoadErrorsTest {
	private static final File MODEL = new File("/projects/library/src/main/resources/Library.eomodeld");

	@Test
	void namesTheFileAndWhereTheParserStopped() {
		Exception failure = new EOModelException("Failed to load entity from '" + new File(MODEL, "Book.plist").getAbsolutePath() + "'.",
				new IllegalArgumentException("Property list parsing failed while attempting to read dictionary. Read key and value with no terminating semicolon. At line number: 12, column: 58."));
		assertThat(LoadErrors.describe(failure, MODEL))
				.isEqualTo("Library.eomodeld/Book.plist:12:58: Property list parsing failed while attempting to read dictionary. Read key and value with no terminating semicolon.");
	}

	/**
	 * The model classes quote an entity file as its URL's path, which has %20
	 * for a space, and on Windows is /C:/...
	 */
	@Test
	void readsAFileGivenAsItsURLsPath() {
		File model = new File("/projects/my library/src/main/resources/Library.eomodeld");
		Exception failure = new EOModelException("Failed to load entity from '" + new File(model, "Book.plist").toURI().getRawPath() + "'.",
				new IllegalArgumentException("Property list parsing failed while attempting to read dictionary. Read key and value with no terminating semicolon. At line number: 12, column: 58."));
		assertThat(LoadErrors.describe(failure, model))
				.isEqualTo("Library.eomodeld/Book.plist:12:58: Property list parsing failed while attempting to read dictionary. Read key and value with no terminating semicolon.");
	}

	@Test
	void leavesOutAnUnknownColumn() {
		Exception failure = new EOModelException("Failed to load entity from '" + new File(MODEL, "Book.plist").getAbsolutePath() + "'.",
				new IllegalArgumentException("Property list parsing failed while attempting to read array. Input exhausted before end of array was found. At line number: 1, column: -1."));
		assertThat(LoadErrors.describe(failure, MODEL))
				.isEqualTo("Library.eomodeld/Book.plist:1: Property list parsing failed while attempting to read array. Input exhausted before end of array was found.");
	}

	@Test
	void namesTheIndexOfAModel() {
		Exception failure = new EOModelException("index.eomodeld is corrupted.",
				new IllegalArgumentException("Property list parsing failed while attempting to read array. Input exhausted before end of array was found. At line number: 4, column: -48."));
		assertThat(LoadErrors.describe(failure, MODEL))
				.isEqualTo("Library.eomodeld/index.eomodeld:4: Property list parsing failed while attempting to read array. Input exhausted before end of array was found.");
	}

	@Test
	void fallsBackToTheDeepestMessage() {
		Exception failure = new RuntimeException("outer", new IllegalStateException("the real problem"));
		assertThat(LoadErrors.describe(failure, MODEL)).isEqualTo("the real problem");
		assertThat(LoadErrors.describe(new RuntimeException(), null)).isEqualTo("java.lang.RuntimeException");
	}
}

package net.logicsquad.eogen.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

/**
 * PathUtils follows Eclipse's IPath rules for relative paths, on
 * java.nio.file.Path.
 */
@DisabledOnOs(OS.WINDOWS)
class PathUtilsTest {
	private static final Path PROJECT = Paths.get("/work/projects/Shop");

	@Test
	void pathInsideTheProjectIsRelativeToIt() {
		assertThat(PathUtils.getRelativePath(PROJECT, Paths.get("/work/projects/Shop/src/main/java"))).isEqualTo("src/main/java");
	}

	@Test
	void projectItselfIsEmpty() {
		assertThat(PathUtils.getRelativePath(PROJECT, PROJECT)).isEmpty();
	}

	@Test
	void pathBesideTheProjectClimbsOutOfIt() {
		assertThat(PathUtils.getRelativePath(PROJECT, Paths.get("/work/projects/Common/Resources/Common.eomodeld"))).isEqualTo("../Common/Resources/Common.eomodeld");
	}

	@Test
	void pathWithNothingInCommonStaysAbsolute() {
		assertThat(PathUtils.getRelativePath(PROJECT, Paths.get("/Library/Frameworks/ERPrototypes.framework"))).isEqualTo("/Library/Frameworks/ERPrototypes.framework");
	}

	@Test
	void relativePathStaysRelative() {
		assertThat(PathUtils.getRelativePath(PROJECT, Paths.get("src/main/resources/Shop.eomodeld"))).isEqualTo("src/main/resources/Shop.eomodeld");
		assertThat(PathUtils.getRelativePath(null, Paths.get("/work/projects/Shop/src"))).isEqualTo("/work/projects/Shop/src");
	}

	@Test
	void toPathNormalisesAsEclipseDid() {
		assertThat(PathUtils.toPath("./src/../models/Shop.eomodeld").toString()).isEqualTo(Paths.get("models/Shop.eomodeld").toString());
		assertThat(PathUtils.toPath("../Common/A.eomodeld").toString()).isEqualTo(Paths.get("../Common/A.eomodeld").toString());
	}

	@Test
	void absolutePathIsResolvedAgainstABaseOnlyWhenRelative() {
		assertThat(PathUtils.getAbsolutePath("src", new java.io.File("/work"))).isEqualTo(new java.io.File("/work/src").getAbsolutePath());
		assertThat(PathUtils.getAbsolutePath("/tmp/src", new java.io.File("/work"))).isEqualTo(new java.io.File("/tmp/src").getAbsolutePath());
	}
}

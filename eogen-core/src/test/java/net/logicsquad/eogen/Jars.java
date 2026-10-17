package net.logicsquad.eogen;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Releases the jars that loading models through {@code jar:} URLs leaves
 * open. The JDK caches a jar that a {@code jar:} URL opens, for the life of the
 * JVM, and Windows won't delete an open file, so a test that loads models from
 * a jar in its temporary folder releases the jar before JUnit deletes the
 * folder.
 */
public final class Jars {
	private Jars() {
	}

	/**
	 * Closes the JDK's cached copy of every jar under a folder, which also drops
	 * it from the cache.
	 *
	 * @param folder a folder
	 * @throws IOException if the folder can't be searched, or a jar closed
	 */
	public static void release(Path folder) throws IOException {
		if (!Files.isDirectory(folder)) {
			return;
		}
		List<Path> jars;
		try (Stream<Path> paths = Files.walk(folder)) {
			jars = paths.filter(path -> path.getFileName().toString().endsWith(".jar") && Files.isRegularFile(path)).collect(Collectors.toList());
		}
		for (Path jar : jars) {
			JarURLConnection connection = (JarURLConnection) new URL("jar:" + jar.toFile().toURI().toURL() + "!/").openConnection();
			connection.setUseCaches(true);
			connection.getJarFile().close();
		}
	}
}

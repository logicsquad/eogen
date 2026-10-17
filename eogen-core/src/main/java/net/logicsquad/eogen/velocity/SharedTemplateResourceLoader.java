package net.logicsquad.eogen.velocity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.util.ExtProperties;

/**
 * Finds the templates that projects share: in the jars and folders of a
 * template path, then at {@code eogen/templates/<name>} on eogen's own
 * classpath, where the Maven plugin's dependencies are. A jar has its
 * templates at {@code eogen/templates/}; a folder has them at the top.
 * <p>
 * Like {@link VelocityOneFileResourceLoader}, it keeps a template's byte
 * order mark, as Velocity 1.5 did.
 * </p>
 */
public class SharedTemplateResourceLoader extends ResourceLoader {
	/**
	 * The folder of templates in a jar, and on the classpath.
	 */
	public static final String FOLDER = "eogen/templates/";

	/**
	 * The property with the template path's jars and folders, separated by
	 * commas.
	 */
	public static final String PATH = "path";

	private final List<File> path = new ArrayList<>();

	@Override
	public void init(ExtProperties configuration) {
		for (String entry : configuration.getStringArray(PATH)) {
			if (!entry.trim().isEmpty()) {
				path.add(new File(entry.trim()));
			}
		}
	}

	@Override
	public Reader getResourceReader(String name, String encoding) throws ResourceNotFoundException {
		try {
			byte[] template = find(name);
			if (template == null) {
				throw new ResourceNotFoundException(name);
			}
			String charset = encoding != null ? encoding : rsvc.getString(RuntimeConstants.INPUT_ENCODING);
			return new StringReader(new String(template, Charset.forName(charset)));
		} catch (IOException e) {
			throw new ResourceNotFoundException("Unable to read the template '" + name + "': " + e.getMessage(), e);
		}
	}

	/**
	 * Returns a template's bytes, from the first place that has it.
	 *
	 * @return bytes, or null if no place has it
	 */
	private byte[] find(String name) throws IOException {
		for (File entry : path) {
			if (entry.isDirectory()) {
				File file = new File(entry, name);
				if (file.isFile()) {
					return Files.readAllBytes(file.toPath());
				}
			} else if (entry.isFile()) {
				// Read the jar itself, and close it, so that it isn't held open.
				try (JarFile jar = new JarFile(entry)) {
					ZipEntry jarEntry = jar.getEntry(FOLDER + name);
					if (jarEntry != null) {
						try (InputStream stream = jar.getInputStream(jarEntry)) {
							return read(stream);
						}
					}
				}
			}
		}
		ClassLoader classLoader = SharedTemplateResourceLoader.class.getClassLoader();
		try (InputStream stream = classLoader == null ? null : classLoader.getResourceAsStream(FOLDER + name)) {
			return stream == null ? null : read(stream);
		}
	}

	private static byte[] read(InputStream stream) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		for (int count = stream.read(buffer); count != -1; count = stream.read(buffer)) {
			bytes.write(buffer, 0, count);
		}
		return bytes.toByteArray();
	}

	@Override
	public boolean isSourceModified(Resource resource) {
		return false;
	}

	@Override
	public long getLastModified(Resource resource) {
		return 0;
	}
}

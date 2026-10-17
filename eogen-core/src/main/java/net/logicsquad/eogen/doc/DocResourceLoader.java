package net.logicsquad.eogen.doc;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.util.ExtProperties;

/**
 * Loads the bundled templates of EOModelDoc's pages, from
 * {@code net/logicsquad/eogen/doc/} on eogen's classpath.
 */
public class DocResourceLoader extends ResourceLoader {
	/**
	 * Where the bundled templates and files are.
	 */
	static final String FOLDER = "/net/logicsquad/eogen/doc/";

	@Override
	public void init(ExtProperties configuration) {
		// Nothing to configure.
	}

	@Override
	public Reader getResourceReader(String name, String encoding) throws ResourceNotFoundException {
		InputStream in = DocResourceLoader.class.getResourceAsStream(FOLDER + name);
		if (in == null) {
			throw new ResourceNotFoundException(name);
		}
		try {
			return new InputStreamReader(in, encoding != null ? encoding : "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ResourceNotFoundException("Unable to read the template '" + name + "'.", e);
		}
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

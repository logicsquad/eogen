package oracle;

import java.io.InputStream;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * Finds the templates bundled in WOLips's eogenerator.core jar, as the
 * legacy veogen's VeogenResourceLoader did.
 */
public class ClasspathTemplateLoader extends ClasspathResourceLoader {
	@Override
	public InputStream getResourceStream(String name) throws ResourceNotFoundException {
		return super.getResourceStream("templates/" + name);
	}
}

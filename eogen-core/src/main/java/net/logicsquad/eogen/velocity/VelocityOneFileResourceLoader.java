package net.logicsquad.eogen.velocity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

/**
 * Reads template files as Velocity 1.5's {@code FileResourceLoader} did with
 * WOLips's settings: in the template encoding, keeping any byte order mark,
 * which then appears in the generated file. Velocity 2 always removes it.
 */
public class VelocityOneFileResourceLoader extends FileResourceLoader {
	@Override
	protected Reader buildReader(InputStream rawStream, String encoding) throws IOException {
		return new InputStreamReader(rawStream, encoding != null ? encoding : rsvc.getString(RuntimeConstants.INPUT_ENCODING));
	}
}

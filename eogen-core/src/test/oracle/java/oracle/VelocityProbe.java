package oracle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogSystem;

import net.logicsquad.eogen.velocity.ProbeContext;

/**
 * Renders each template in a folder with Velocity 1.5, as WOLips does, and
 * writes the output next to it as <template>.v15.
 *
 * Usage: VelocityProbe <folder>
 */
public class VelocityProbe {
	public static void main(String[] args) throws Exception {
		File folder = new File(args[0]);
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, NullLogSystem.class.getName());
		engine.setProperty("resource.loader", "file");
		engine.setProperty("file.resource.loader.path", folder.getAbsolutePath());
		engine.init();
		for (String name : folder.list()) {
			if (!name.endsWith(".vm") || name.equals("parsed.vm")) {
				continue;
			}
			VelocityContext context = new VelocityContext();
			// WOLips's own ListTool and SetTool, from its thirdparty.velocity bundle.
			ProbeContext.fill(context, Class.forName("org.apache.velocity.tools.generic.ListTool").newInstance(),
					Class.forName("org.apache.velocity.tools.generic.SetTool").newInstance());
			StringWriter writer = new StringWriter();
			engine.getTemplate(name, "UTF-8").merge(context, writer);
			try (OutputStream out = new FileOutputStream(new File(folder, name + ".v15"))) {
				out.write(writer.toString().getBytes("UTF-8"));
			}
		}
	}
}

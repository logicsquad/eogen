package oracle;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.objectstyle.wolips.baseforplugins.util.URLUtils;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel;
import org.objectstyle.wolips.eogenerator.core.runner.VelocityEOGeneratorRunner;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

/**
 * Generates the expected output for eogen's tests with WOLips's own,
 * unmodified EOGenerator code and Velocity 1.5, outside Eclipse.
 *
 * Usage: WOLipsOracle <project dir> <.eogen file> <classpath> <output dir>
 *
 * The model group holds every model under the project's src/main/resources,
 * then the models in Resources/ and META-INF/webobjects/Resources/ of each
 * jar on the classpath, as WOLips loads it for -loadModelGroup in a Maven
 * project. Superclasses and subclasses are both written to the output dir.
 */
public class WOLipsOracle {
	public static void main(String[] args) throws Exception {
		File projectDir = new File(args[0]).getCanonicalFile();
		File eogenFile = new File(args[1]).getCanonicalFile();
		String classpath = args[2];
		File outputDir = new File(args[3]).getCanonicalFile();

		// As EOGeneratorModel.createModelFromFile reads an IFile.
		StringBuilder config = new StringBuilder();
		for (String line : Files.readAllLines(eogenFile.toPath())) {
			config.append(line.replaceFirst("\\\\$", ""));
		}
		EOGeneratorModel eogen = new EOGeneratorModel(new Path(projectDir.getAbsolutePath()));
		// Outside Eclipse, WOLips never sets its default extension.
		eogen.setExtension("java");
		eogen.readFromString(config.toString());
		eogen.setDestination(outputDir.getAbsolutePath());
		eogen.setSubclassDestination(outputDir.getAbsolutePath());

		Set<EOModelVerificationFailure> failures = new LinkedHashSet<>();
		NullProgressMonitor monitor = new NullProgressMonitor();
		EOModelGroup modelGroup = new EOModelGroup();
		modelGroup.loadModelsFromURL(new File(projectDir, "src/main/resources").toURI().toURL(), -1, failures, true, monitor);
		for (String entry : classpath.split(File.pathSeparator)) {
			File jar = new File(entry);
			if (!jar.isFile() || !jar.getName().endsWith(".jar")) {
				continue;
			}
			for (String folder : new String[] { "Resources", "META-INF/webobjects/Resources" }) {
				URL url = new URL("jar:" + jar.toURI().toURL() + "!/" + folder);
				if (URLUtils.exists(url)) {
					modelGroup.loadModelsFromURL(url, 1, failures, true, monitor);
				}
			}
		}
		modelGroup.resolve(failures);
		modelGroup.verify(failures);
		for (EOModelVerificationFailure failure : failures) {
			System.out.println((failure.isWarning() ? "  warning: " : "  error: ") + failure.getMessage());
		}

		StringBuffer results = new StringBuffer();
		new VelocityEOGeneratorRunner(false).generate(eogen, results, modelGroup, null, ClasspathTemplateLoader.class, monitor);
		if (results.length() > 0) {
			System.out.println(results);
		}
	}
}

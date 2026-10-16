package net.logicsquad.eogen.velocity;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.velocity.app.VelocityEngine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Renders each template in {@code src/test/resources/velocity} with eogen's
 * Velocity 2 engine, and compares the output with Velocity 1.5's, which
 * {@code src/test/oracle/run.sh} recorded in {@code <template>.v15}.
 */
class VelocityCompatibilityTest {
	/**
	 * Templates whose output knowingly differs from Velocity 1.5's, recorded in
	 * {@code <template>.v2}. In each, Velocity 1.5 printed the reference as it
	 * stood, or skipped the directive, and the README lists them.
	 */
	private static final Set<String> KNOWN_DIFFERENCES = new HashSet<>(Arrays.asList("array-methods.vm", "equality.vm", "foreach-iterable.vm",
			"macro-arguments.vm", "null-comparison.vm", "set-null-property.vm"));

	static Stream<String> templates() throws IOException, URISyntaxException {
		try (Stream<Path> files = Files.list(folder())) {
			List<String> names = files.map(path -> path.getFileName().toString()).filter(name -> name.endsWith(".vm") && !name.equals("parsed.vm"))
					.sorted().collect(Collectors.toList());
			return names.stream();
		}
	}

	@ParameterizedTest
	@MethodSource("templates")
	void rendersAsVelocity15Did(String template) throws Exception {
		String expected = KNOWN_DIFFERENCES.contains(template) ? template + ".v2" : template + ".v15";
		assertThat(render(template)).isEqualTo(read(expected));
	}

	@Test
	void knownDifferencesAreStillDifferent() throws Exception {
		for (String template : KNOWN_DIFFERENCES) {
			assertThat(read(template + ".v2")).as(template).isNotEqualTo(read(template + ".v15"));
		}
	}

	@Test
	void propertiesDoNotCallMethodsOfTheSameName() throws Exception {
		VelocityOneContext context = context();
		ProbeContext.Bean bean = (ProbeContext.Bean) context.get("b");
		render("property-lookup.vm", context);
		assertThat(bean.calls).isZero();
	}

	@Test
	void velocityCountIsOnlyDefinedInsideForeach() throws Exception {
		assertThat(render("velocity-count-nested.vm")).endsWith("|after:$velocityCount\n");
	}

	private static String render(String template) throws Exception {
		return render(template, context());
	}

	private static VelocityOneContext context() {
		VelocityOneContext context = new VelocityOneContext();
		ProbeContext.fill(context, new ListTool(), new SetTool());
		return context;
	}

	private static String render(String template, VelocityOneContext context) throws Exception {
		Path folder = folder();
		VelocityEngine engine = WOLipsVelocityUtils.createVelocityEngine("EOGenerator", folder.toString(), folder, null);
		StringWriter writer = new StringWriter();
		engine.getTemplate(template, "UTF-8").merge(context, writer);
		return writer.toString();
	}

	private static String read(String name) throws IOException, URISyntaxException {
		return new String(Files.readAllBytes(folder().resolve(name)), StandardCharsets.UTF_8);
	}

	private static Path folder() throws URISyntaxException {
		return Paths.get(VelocityCompatibilityTest.class.getResource("/velocity").toURI());
	}
}

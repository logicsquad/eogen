#!/bin/bash
# Regenerates eogen-core's expected test output with WOLips's own EOGenerator,
# and the Velocity 1.5 output of the templates in src/test/resources/velocity.
#
# Usage: src/test/oracle/run.sh    (from eogen-core)
#
# It runs WOLips's unmodified eomodeler.core and eogenerator.core jars, and
# Velocity 1.5, from an Eclipse install's ~/.p2/pool/plugins, on each .eogen
# file in the test fixture project. Each file's output replaces
# src/test/resources/fixtures/expected/<name of the .eogen file>.
set -euo pipefail

here=$(cd "$(dirname "$0")" && pwd)
core=$(cd "$here/../../.." && pwd)
fixtures=$core/src/test/resources/fixtures
pool=${WOLIPS_POOL:-$HOME/.p2/pool/plugins}
build=$core/target/oracle

latest() { ls "$pool"/"$1"_*.jar 2>/dev/null | sort -V | tail -1; }

rm -rf "$build" && mkdir -p "$build/lib" "$build/classes"
cp=()
for bundle in org.objectstyle.wolips.eomodeler.core org.objectstyle.wolips.eogenerator.core \
	org.objectstyle.wolips.baseforplugins org.objectstyle.wolips.thirdparty.velocity \
	org.eclipse.equinox.common org.eclipse.core.runtime org.eclipse.core.commands \
	org.eclipse.core.jobs org.eclipse.osgi org.eclipse.equinox.registry org.eclipse.core.resources \
	org.eclipse.equinox.preferences org.eclipse.core.contenttype; do
	jar=$(latest "$bundle")
	[ -n "$jar" ] || { echo "No $bundle in $pool" >&2; exit 1; }
	cp+=("$jar")
done
unzip -o -q -j "$(latest org.objectstyle.wolips.woproject)" lib/woenvironment.jar -d "$build/lib"
unzip -o -q -j "$(latest org.objectstyle.wolips.thirdparty.commonscollections)" lib/commons-collections-3.1.jar -d "$build/lib"
unzip -o -q -j "$(latest org.objectstyle.wolips.thirdparty.velocity)" lib/velocity-1.5.jar \
	lib/velocity-tools-generic-1.4.jar lib/commons-lang-2.1.jar lib/oro-2.0.8.jar -d "$build/lib"
for jar in woenvironment commons-collections-3.1 velocity-1.5 velocity-tools-generic-1.4 commons-lang-2.1 oro-2.0.8; do
	cp+=("$build/lib/$jar.jar")
done
classpath=$(IFS=:; echo "${cp[*]}")
javac -nowarn -d "$build/classes" -cp "$classpath" "$here"/java/oracle/*.java \
	"$core"/src/test/java/net/logicsquad/eogen/velocity/ProbeContext.java

# The framework fixture becomes a jar, as a dependency would be.
jar cf "$build/reference.jar" -C "$fixtures/framework" Resources

echo "WOLips: $(basename "$(latest org.objectstyle.wolips.eogenerator.core)" .jar)"
for eogen in "$fixtures"/project/src/main/resources/*.eogen; do
	name=$(basename "$eogen" .eogen)
	out=$fixtures/expected/$name
	rm -rf "$out"
	echo "$name"
	# Run from the project, since Velocity 1.5 looks for templates in "." first.
	(cd "$fixtures/project" && java -cp "$build/classes:$classpath" oracle.WOLipsOracle \
		"$fixtures/project" "$eogen" "$build/reference.jar" "$out")
done

# Velocity 1.5's output for each compatibility template, as <template>.v15.
echo "Velocity 1.5 compatibility templates"
java -cp "$build/classes:$classpath" oracle.VelocityProbe "$core/src/test/resources/velocity"

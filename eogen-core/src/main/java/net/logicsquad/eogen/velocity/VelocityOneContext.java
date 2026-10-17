package net.logicsquad.eogen.velocity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.directive.ForeachScope;

/**
 * A {@link VelocityContext} that behaves like Velocity 1.5's, which WOLips
 * used, in the two ways a template can notice:
 * <ul>
 * <li>{@code $velocityCount} is the count of the innermost {@code #foreach},
 * starting from 1. Velocity 2 has only {@code $foreach.count}.</li>
 * <li>{@code #set} with a null value leaves the reference as it was, where
 * Velocity 2 sets it to null. This needs {@link VelocityOneEventHandler},
 * which tells the context when a {@code #set} has a null value.</li>
 * </ul>
 * <p>
 * It also collects the references that render without a value, which
 * {@link UnresolvedReferenceHandler} reports, so that the generator can treat
 * output with an unresolved reference in it as an error.
 * </p>
 */
public class VelocityOneContext extends VelocityContext {
	/**
	 * Velocity 1.5's name for the {@code #foreach} counter.
	 */
	private static final String VELOCITY_COUNT = "velocityCount";

	/**
	 * The scope Velocity 2 puts in the context during a {@code #foreach}.
	 */
	private static final String FOREACH = "foreach";

	/**
	 * Whether a {@code #set} is about to set a reference to null.
	 */
	private boolean nullSet;

	/**
	 * The references rendered without a value since the last
	 * {@link #takeUnresolvedReferences()}.
	 */
	private final Set<UnresolvedReference> unresolvedReferences = new LinkedHashSet<>();

	/**
	 * Notes that a {@code #set} is about to set a reference to null, so that the
	 * {@link #internalPut(String, Object)} that follows leaves it as it is.
	 * Velocity puts the value straight after it raises the event, without
	 * getting anything first. The event names {@code ${x}} only as
	 * <code>{</code>, so the put is not matched by name.
	 */
	void nullSet() {
		nullSet = true;
	}

	/**
	 * Notes a reference that rendered without a value.
	 *
	 * @param reference what the reference was and where
	 */
	void unresolved(UnresolvedReference reference) {
		unresolvedReferences.add(reference);
	}

	/**
	 * Returns the references that rendered without a value since the last call,
	 * once each, and forgets them.
	 *
	 * @return the unresolved references
	 */
	public List<UnresolvedReference> takeUnresolvedReferences() {
		if (unresolvedReferences.isEmpty()) {
			return Collections.emptyList();
		}
		List<UnresolvedReference> references = new ArrayList<>(unresolvedReferences);
		unresolvedReferences.clear();
		return references;
	}

	@Override
	public Object internalGet(String key) {
		// A #set of a plain reference puts without getting first. Any get means
		// this #set sets a property instead, which Velocity 1.5 also skipped,
		// but which the context can't stop.
		nullSet = false;
		Object value = super.internalGet(key);
		if (value == null && VELOCITY_COUNT.equals(key)) {
			Object scope = super.internalGet(FOREACH);
			if (scope instanceof ForeachScope) {
				value = ((ForeachScope) scope).getCount();
			}
		}
		return value;
	}

	@Override
	public Object internalPut(String key, Object value) {
		boolean ignore = nullSet && value == null;
		nullSet = false;
		if (ignore) {
			return super.internalGet(key);
		}
		return super.internalPut(key, value);
	}
}

package net.logicsquad.eogen.velocity;

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
	 * The reference a {@code #set} is about to set to null, or {@code null}.
	 */
	private String nullSetReference;

	/**
	 * Notes that a {@code #set} is about to set a reference to null, so that the
	 * {@link #internalPut(String, Object)} that follows leaves it as it is.
	 *
	 * @param reference the name of the reference, without {@code $}
	 */
	void nullSet(String reference) {
		nullSetReference = reference;
	}

	@Override
	public Object internalGet(String key) {
		// A #set of a plain reference puts without getting first. Any get means
		// this #set sets a property instead, which Velocity 1.5 also skipped,
		// but which the context can't stop.
		nullSetReference = null;
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
		String reference = nullSetReference;
		nullSetReference = null;
		if (value == null && key.equals(reference)) {
			return super.internalGet(key);
		}
		return super.internalPut(key, value);
	}
}

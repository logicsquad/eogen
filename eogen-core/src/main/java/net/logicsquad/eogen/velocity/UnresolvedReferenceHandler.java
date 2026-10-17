package net.logicsquad.eogen.velocity;

import java.lang.reflect.Method;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.context.InternalWrapperContext;

/**
 * Tells a {@link VelocityOneContext} about each reference that a template
 * renders without a value, which Velocity writes into the output as it stands,
 * such as {@code $attribute.javaClassName} for an attribute with no value
 * class.
 * <p>
 * Only a property or method of a variable that the template has is reported.
 * A bare reference to a variable that doesn't exist is usually not meant as
 * one: {@code $LastChangedRevision$} in WOLips's own {@code _Entity.java} is a
 * Subversion keyword, and {@code Outer$Inner} is a Java class name. Quiet
 * references, such as {@code $!entity.parent}, are meant to render as
 * nothing, and references that a {@code #if} or {@code #set} only reads are
 * never rendered, so neither is reported either.
 * </p>
 */
public class UnresolvedReferenceHandler implements ReferenceInsertionEventHandler {
	@Override
	public Object referenceInsert(Context context, String reference, Object value) {
		if (value == null && !isQuiet(reference)) {
			String root = root(reference);
			Object rootValue = root == null ? null : context.get(root);
			Context userContext = context instanceof InternalWrapperContext ? ((InternalWrapperContext) context).getInternalUserContext() : context;
			if (rootValue != null && userContext instanceof VelocityOneContext) {
				String template = context instanceof InternalContextAdapter ? ((InternalContextAdapter) context).getCurrentTemplateName() : null;
				((VelocityOneContext) userContext).unresolved(describe(reference, root, rootValue, template));
			}
		}
		return value;
	}

	/**
	 * Describes an unresolved reference: the reference, the name of the object
	 * it starts from when that has one, such as the attribute in
	 * {@code $attribute.javaClassName}, and the template.
	 *
	 * @param reference the reference as the template has it
	 * @param root the variable it starts from
	 * @param rootValue the variable's value
	 * @param template the template's name, or null
	 * @return description
	 */
	static String describe(String reference, String root, Object rootValue, String template) {
		StringBuilder description = new StringBuilder(reference).append(" has no value");
		String name = name(rootValue);
		if (name != null) {
			description.append(" for ").append(root).append(' ').append(name);
		}
		if (template != null) {
			description.append(" (").append(template).append(')');
		}
		return description.append('.').toString();
	}

	private static boolean isQuiet(String reference) {
		return reference.startsWith("$!");
	}

	/**
	 * Returns the variable a reference starts from: {@code attribute} for
	 * {@code $attribute.javaClassName} or <code>${attribute.javaClassName}</code>,
	 * or null if the reference is the variable itself.
	 */
	static String root(String reference) {
		String body = reference.startsWith("${") ? reference.substring(2) : reference.substring(1);
		int end = 0;
		while (end < body.length() && (Character.isLetterOrDigit(body.charAt(end)) || body.charAt(end) == '_' || body.charAt(end) == '-')) {
			end++;
		}
		return end > 0 && end < body.length() && body.charAt(end) == '.' ? body.substring(0, end) : null;
	}

	/**
	 * Returns an object's {@code getName()}, as the model's entities, attributes,
	 * relationships and fetch specifications have, or null.
	 */
	private static String name(Object object) {
		if (object == null) {
			return null;
		}
		try {
			Method getName = object.getClass().getMethod("getName");
			Object name = getName.invoke(object);
			return name instanceof String ? (String) name : null;
		} catch (ReflectiveOperationException | RuntimeException e) {
			return null;
		}
	}
}

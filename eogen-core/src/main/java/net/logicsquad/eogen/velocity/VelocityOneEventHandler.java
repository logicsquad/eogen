package net.logicsquad.eogen.velocity;

import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalWrapperContext;
import org.apache.velocity.util.introspection.Info;

/**
 * Tells a {@link VelocityOneContext} when a {@code #set} is about to set a
 * reference to null, so that the context can ignore it as Velocity 1.5 did. It
 * leaves invalid references and method calls to Velocity's usual handling.
 */
public class VelocityOneEventHandler implements InvalidReferenceEventHandler {
	@Override
	public Object invalidGetMethod(Context context, String reference, Object object, String property, Info info) {
		return null;
	}

	@Override
	public boolean invalidSetMethod(Context context, String leftreference, String rightreference, Info info) {
		Context userContext = context instanceof InternalWrapperContext ? ((InternalWrapperContext) context).getInternalUserContext() : context;
		if (userContext instanceof VelocityOneContext && leftreference != null) {
			((VelocityOneContext) userContext).nullSet(leftreference.replaceFirst("^[!{]+", ""));
		}
		return false;
	}

	@Override
	public Object invalidMethod(Context context, String reference, Object object, String method, Info info) {
		return null;
	}
}

package net.logicsquad.eogen.velocity;

import org.apache.velocity.runtime.parser.node.AbstractExecutor;
import org.apache.velocity.runtime.parser.node.BooleanPropertyExecutor;
import org.apache.velocity.runtime.parser.node.GetExecutor;
import org.apache.velocity.runtime.parser.node.MapGetExecutor;
import org.apache.velocity.runtime.parser.node.PropertyExecutor;
import org.apache.velocity.util.ArrayListWrapper;
import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.Introspector;
import org.apache.velocity.util.introspection.UberspectImpl;
import org.apache.velocity.util.introspection.VelPropertyGet;
import org.slf4j.Logger;

/**
 * Resolves {@code $object.property} as Velocity 1.5 did: {@code getProperty()},
 * then {@code getproperty()}, then {@code get("property")}, then
 * {@code isProperty()}.
 * <p>
 * Velocity 2 also tries a method named {@code property()}, before
 * {@code get("property")} and {@code isProperty()}. The EOModel classes have
 * methods like that: {@code $fetchSpecification.fetchEnterpriseObjects}, which
 * WOLips's templates use, would call the void mutator
 * {@code fetchEnterpriseObjects()} instead of
 * {@code isFetchEnterpriseObjects()}, and generate the wrong code.
 * </p>
 */
public class VelocityOneUberspector extends UberspectImpl {
	@Override
	public VelPropertyGet getPropertyGet(Object obj, String identifier, Info i) {
		if (obj == null) {
			return null;
		}
		Class<?> claz = obj.getClass();
		AbstractExecutor executor = new VelocityOnePropertyExecutor(log, introspector, claz, identifier);
		if (!executor.isAlive()) {
			executor = new MapGetExecutor(log, obj, identifier);
		}
		if (!executor.isAlive()) {
			executor = new GetExecutor(log, introspector, claz, identifier);
		}
		if (!executor.isAlive()) {
			executor = new BooleanPropertyExecutor(log, introspector, claz, identifier);
		}
		if (!executor.isAlive() && obj.getClass().isArray()) {
			executor = new BooleanPropertyExecutor(log, introspector, ArrayListWrapper.class, identifier, true);
		}
		return executor.isAlive() ? new VelGetterImpl(executor) : null;
	}

	/**
	 * Finds {@code getProperty()} or {@code getproperty()}, as Velocity 1.5's
	 * {@code PropertyExecutor} did.
	 */
	static class VelocityOnePropertyExecutor extends PropertyExecutor {
		VelocityOnePropertyExecutor(Logger log, Introspector introspector, Class<?> clazz, String property) {
			super(log, introspector, clazz, property);
		}

		@Override
		protected void discover(Class<?> clazz, String property) {
			Object[] params = {};
			StringBuilder name = new StringBuilder("get").append(property);
			setMethod(getIntrospector().getMethod(clazz, name.toString(), params));
			if (!isAlive()) {
				char c = name.charAt(3);
				name.setCharAt(3, Character.isLowerCase(c) ? Character.toUpperCase(c) : Character.toLowerCase(c));
				setMethod(getIntrospector().getMethod(clazz, name.toString(), params));
			}
		}
	}
}

package net.logicsquad.eogen.velocity;

/**
 * A template reference that rendered with no value, such as
 * {@code $attribute.javaClassName} for an attribute with no class.
 */
public final class UnresolvedReference {
	private final String reference;

	private final String root;

	private final String name;

	private final String template;

	private final String description;

	/**
	 * Constructor.
	 *
	 * @param reference the reference as the template has it
	 * @param root the variable it starts from, such as {@code attribute}
	 * @param name the name of the variable's value, such as {@code price}, or
	 *        null
	 * @param template the template's name, or null
	 */
	public UnresolvedReference(String reference, String root, String name, String template) {
		this.reference = reference;
		this.root = root;
		this.name = name;
		this.template = template;
		StringBuilder text = new StringBuilder(reference).append(" has no value");
		if (name != null) {
			text.append(" for ").append(root).append(' ').append(name);
		}
		if (template != null) {
			text.append(" (").append(template).append(')');
		}
		this.description = text.append('.').toString();
	}

	/**
	 * Returns the reference as the template has it.
	 *
	 * @return reference, such as {@code $attribute.javaClassName}
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * Returns what the reference starts from, as eogen names it in messages.
	 *
	 * @return object, such as {@code attribute price}, or null if its value has
	 *         no name
	 */
	public String getObject() {
		return name == null ? null : root + " " + name;
	}

	/**
	 * Returns the template the reference is in.
	 *
	 * @return template, or null
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * Describes the reference, such as {@code $attribute.javaClassName has no
	 * value for attribute price (_WonderEntity.java).}
	 *
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof UnresolvedReference && description.equals(((UnresolvedReference) object).description);
	}

	@Override
	public int hashCode() {
		return description.hashCode();
	}

	@Override
	public String toString() {
		return description;
	}
}

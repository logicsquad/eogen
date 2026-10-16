package net.logicsquad.eogen.model.qualifier;

public class EONamedQualifierVariable extends EOQualifierVariable {
	public EONamedQualifierVariable(String name) {
		super(name);
	}

	public String toString() {
		return "$" + getName();
	}
}

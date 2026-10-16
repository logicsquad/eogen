package net.logicsquad.eogen.model;

public interface ISortableEOModelObject {
	public String getName();
	
	public void setName(String name) throws DuplicateNameException;
}

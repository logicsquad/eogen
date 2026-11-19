package com.webobjects.eoaccess;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

public class EOEntity {
	private String _name;

	private String _externalName;

	private final NSMutableArray<EOAttribute> _attributes = new NSMutableArray<EOAttribute>();

	private NSArray<EOAttribute> _primaryKeyAttributes = new NSArray<EOAttribute>();

	public String name() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String externalName() {
		return _externalName;
	}

	public void setExternalName(String externalName) {
		_externalName = externalName;
	}

	/**
	 * Returns what the database's plugin names an entity's primary key support
	 * after: the table of its root entity, which a migration's entity is.
	 */
	public String primaryKeyRootName() {
		return _externalName;
	}

	public NSArray<EOAttribute> attributes() {
		return _attributes;
	}

	public void addAttribute(EOAttribute attribute) {
		_attributes.addObject(attribute);
	}

	public NSArray<EOAttribute> primaryKeyAttributes() {
		return _primaryKeyAttributes;
	}

	public void setPrimaryKeyAttributes(NSArray<EOAttribute> attributes) {
		_primaryKeyAttributes = attributes;
	}
}

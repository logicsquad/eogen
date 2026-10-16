// $LastChangedRevision$ DO NOT EDIT.  Make changes to Tag.java instead.
package com.example.shop;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Tag extends  EOGenericRecord {
	public static final String ENTITY_NAME = "Tag";

	// Attributes
	public static final String NAME_KEY = "name";

	// Relationships

  private static Logger LOG = Logger.getLogger(_Tag.class);

  public Tag localInstanceIn(EOEditingContext editingContext) {
    Tag localInstance = (Tag)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String name() {
    return (String) storedValueForKey("name");
  }

  public void setName(String value) {
    if (_Tag.LOG.isDebugEnabled()) {
    	_Tag.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, "name");
  }


  public static Tag createTag(EOEditingContext editingContext, String name
) {
    Tag eo = (Tag) EOUtilities.createAndInsertInstance(editingContext, _Tag.ENTITY_NAME);    
		eo.setName(name);
    return eo;
  }

  public static NSArray<Tag> fetchAllTags(EOEditingContext editingContext) {
    return _Tag.fetchAllTags(editingContext, null);
  }

  public static NSArray<Tag> fetchAllTags(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Tag.fetchTags(editingContext, null, sortOrderings);
  }

  public static NSArray<Tag> fetchTags(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Tag.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Tag> eoObjects = (NSArray<Tag>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static Tag fetchTag(EOEditingContext editingContext, String keyName, Object value) {
    return _Tag.fetchTag(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Tag fetchTag(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Tag> eoObjects = _Tag.fetchTags(editingContext, qualifier, null);
    Tag eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (Tag)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Tag that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Tag fetchRequiredTag(EOEditingContext editingContext, String keyName, Object value) {
    return _Tag.fetchRequiredTag(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Tag fetchRequiredTag(EOEditingContext editingContext, EOQualifier qualifier) {
    Tag eoObject = _Tag.fetchTag(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Tag that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Tag localInstanceIn(EOEditingContext editingContext, Tag eo) {
    Tag localInstance = (eo == null) ? null : (Tag)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

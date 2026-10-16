// DO NOT EDIT.  Make changes to Tag.java instead.
package com.example.shop;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;

import er.extensions.eof.*;
import er.extensions.eof.ERXKey.Type;
import er.extensions.foundation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public abstract class _Tag extends com.example.shop.ShopRecord {
  public static final String ENTITY_NAME = "Tag";

  // Attribute Keys
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String NAME_KEY = NAME.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_Tag.class);

  public Tag localInstanceIn(EOEditingContext editingContext) {
    Tag localInstance = (Tag)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String name() {
    return (String) storedValueForKey(_Tag.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _Tag.NAME_KEY);
  }


  public static Tag createTag(EOEditingContext editingContext, String name
) {
    Tag eo = (Tag) EOUtilities.createAndInsertInstance(editingContext, _Tag.ENTITY_NAME);
    eo.setName(name);
    return eo;
  }

  public static ERXFetchSpecification<Tag> fetchSpec() {
    return new ERXFetchSpecification<Tag>(_Tag.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Tag> fetchAllTags(EOEditingContext editingContext) {
    return _Tag.fetchAllTags(editingContext, null);
  }

  public static NSArray<Tag> fetchAllTags(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Tag.fetchTags(editingContext, null, sortOrderings);
  }

  public static NSArray<Tag> fetchTags(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Tag> fetchSpec = new ERXFetchSpecification<Tag>(_Tag.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Tag> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Tag fetchTag(EOEditingContext editingContext, String keyName, Object value) {
    return _Tag.fetchTag(editingContext, ERXQ.equals(keyName, value));
  }

  public static Tag fetchTag(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Tag> eoObjects = _Tag.fetchTags(editingContext, qualifier, null);
    Tag eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Tag that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Tag fetchRequiredTag(EOEditingContext editingContext, String keyName, Object value) {
    return _Tag.fetchRequiredTag(editingContext, ERXQ.equals(keyName, value));
  }

  public static Tag fetchRequiredTag(EOEditingContext editingContext, EOQualifier qualifier) {
    Tag eoObject = _Tag.fetchTag(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Tag that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Tag localInstanceIn(EOEditingContext editingContext, Tag eo) {
    Tag localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

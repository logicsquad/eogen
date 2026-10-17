// DO NOT EDIT.  Make changes to ERIAttributeGroup.java instead.
package er.indexing.attributes;

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
public abstract class _ERIAttributeGroup extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERIAttributeGroup";

  // Attribute Keys
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.indexing.attributes.ERIAttribute> ATTRIBUTES = new ERXKey<er.indexing.attributes.ERIAttribute>("attributes", Type.ToManyRelationship);
  public static final ERXKey<er.indexing.attributes.ERIAttributeGroup> CHILDREN = new ERXKey<er.indexing.attributes.ERIAttributeGroup>("children", Type.ToManyRelationship);
  public static final ERXKey<er.indexing.attributes.ERIAttributeGroup> PARENT = new ERXKey<er.indexing.attributes.ERIAttributeGroup>("parent", Type.ToOneRelationship);

  // Attributes
  public static final String NAME_KEY = NAME.key();

  // Relationships
  public static final String ATTRIBUTES_KEY = ATTRIBUTES.key();
  public static final String CHILDREN_KEY = CHILDREN.key();
  public static final String PARENT_KEY = PARENT.key();

  private static final Logger log = LoggerFactory.getLogger(_ERIAttributeGroup.class);

  public ERIAttributeGroup localInstanceIn(EOEditingContext editingContext) {
    ERIAttributeGroup localInstance = (ERIAttributeGroup)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String name() {
    return (String) storedValueForKey(_ERIAttributeGroup.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _ERIAttributeGroup.NAME_KEY);
  }

  public er.indexing.attributes.ERIAttributeGroup parent() {
    return (er.indexing.attributes.ERIAttributeGroup)storedValueForKey(_ERIAttributeGroup.PARENT_KEY);
  }

  public void setParent(er.indexing.attributes.ERIAttributeGroup value) {
    takeStoredValueForKey(value, _ERIAttributeGroup.PARENT_KEY);
  }

  public void setParentRelationship(er.indexing.attributes.ERIAttributeGroup value) {
    log.debug("updating parent from {} to {}", parent(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setParent(value);
    }
    else if (value == null) {
      er.indexing.attributes.ERIAttributeGroup oldValue = parent();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _ERIAttributeGroup.PARENT_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _ERIAttributeGroup.PARENT_KEY);
    }
  }

  public NSArray<er.indexing.attributes.ERIAttribute> attributes() {
    return (NSArray<er.indexing.attributes.ERIAttribute>)storedValueForKey(_ERIAttributeGroup.ATTRIBUTES_KEY);
  }

  public NSArray<er.indexing.attributes.ERIAttribute> attributes(EOQualifier qualifier) {
    return attributes(qualifier, null, false);
  }

  public NSArray<er.indexing.attributes.ERIAttribute> attributes(EOQualifier qualifier, boolean fetch) {
    return attributes(qualifier, null, fetch);
  }

  public NSArray<er.indexing.attributes.ERIAttribute> attributes(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.indexing.attributes.ERIAttribute> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(er.indexing.attributes.ERIAttribute.ATTRIBUTE_GROUP_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = er.indexing.attributes.ERIAttribute.fetchERIAttributes(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = attributes();
      if (qualifier != null) {
        results = (NSArray<er.indexing.attributes.ERIAttribute>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.indexing.attributes.ERIAttribute>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToAttributes(er.indexing.attributes.ERIAttribute object) {
    includeObjectIntoPropertyWithKey(object, _ERIAttributeGroup.ATTRIBUTES_KEY);
  }

  public void removeFromAttributes(er.indexing.attributes.ERIAttribute object) {
    excludeObjectFromPropertyWithKey(object, _ERIAttributeGroup.ATTRIBUTES_KEY);
  }

  public void addToAttributesRelationship(er.indexing.attributes.ERIAttribute object) {
    log.debug("adding {} to attributes relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToAttributes(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _ERIAttributeGroup.ATTRIBUTES_KEY);
    }
  }

  public void removeFromAttributesRelationship(er.indexing.attributes.ERIAttribute object) {
    log.debug("removing {} from attributes relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromAttributes(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _ERIAttributeGroup.ATTRIBUTES_KEY);
    }
  }

  public er.indexing.attributes.ERIAttribute createAttributesRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.indexing.attributes.ERIAttribute.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _ERIAttributeGroup.ATTRIBUTES_KEY);
    return (er.indexing.attributes.ERIAttribute) eo;
  }

  public void deleteAttributesRelationship(er.indexing.attributes.ERIAttribute object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _ERIAttributeGroup.ATTRIBUTES_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllAttributesRelationships() {
    Enumeration<er.indexing.attributes.ERIAttribute> objects = attributes().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteAttributesRelationship(objects.nextElement());
    }
  }

  public NSArray<er.indexing.attributes.ERIAttributeGroup> children() {
    return (NSArray<er.indexing.attributes.ERIAttributeGroup>)storedValueForKey(_ERIAttributeGroup.CHILDREN_KEY);
  }

  public NSArray<er.indexing.attributes.ERIAttributeGroup> children(EOQualifier qualifier) {
    return children(qualifier, null, false);
  }

  public NSArray<er.indexing.attributes.ERIAttributeGroup> children(EOQualifier qualifier, boolean fetch) {
    return children(qualifier, null, fetch);
  }

  public NSArray<er.indexing.attributes.ERIAttributeGroup> children(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.indexing.attributes.ERIAttributeGroup> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(er.indexing.attributes.ERIAttributeGroup.PARENT_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = er.indexing.attributes.ERIAttributeGroup.fetchERIAttributeGroups(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = children();
      if (qualifier != null) {
        results = (NSArray<er.indexing.attributes.ERIAttributeGroup>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.indexing.attributes.ERIAttributeGroup>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToChildren(er.indexing.attributes.ERIAttributeGroup object) {
    includeObjectIntoPropertyWithKey(object, _ERIAttributeGroup.CHILDREN_KEY);
  }

  public void removeFromChildren(er.indexing.attributes.ERIAttributeGroup object) {
    excludeObjectFromPropertyWithKey(object, _ERIAttributeGroup.CHILDREN_KEY);
  }

  public void addToChildrenRelationship(er.indexing.attributes.ERIAttributeGroup object) {
    log.debug("adding {} to children relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToChildren(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _ERIAttributeGroup.CHILDREN_KEY);
    }
  }

  public void removeFromChildrenRelationship(er.indexing.attributes.ERIAttributeGroup object) {
    log.debug("removing {} from children relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromChildren(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _ERIAttributeGroup.CHILDREN_KEY);
    }
  }

  public er.indexing.attributes.ERIAttributeGroup createChildrenRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.indexing.attributes.ERIAttributeGroup.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _ERIAttributeGroup.CHILDREN_KEY);
    return (er.indexing.attributes.ERIAttributeGroup) eo;
  }

  public void deleteChildrenRelationship(er.indexing.attributes.ERIAttributeGroup object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _ERIAttributeGroup.CHILDREN_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllChildrenRelationships() {
    Enumeration<er.indexing.attributes.ERIAttributeGroup> objects = children().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteChildrenRelationship(objects.nextElement());
    }
  }


  public static ERIAttributeGroup createERIAttributeGroup(EOEditingContext editingContext, String name
) {
    ERIAttributeGroup eo = (ERIAttributeGroup) EOUtilities.createAndInsertInstance(editingContext, _ERIAttributeGroup.ENTITY_NAME);
    eo.setName(name);
    return eo;
  }

  public static ERXFetchSpecification<ERIAttributeGroup> fetchSpec() {
    return new ERXFetchSpecification<ERIAttributeGroup>(_ERIAttributeGroup.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERIAttributeGroup> fetchAllERIAttributeGroups(EOEditingContext editingContext) {
    return _ERIAttributeGroup.fetchAllERIAttributeGroups(editingContext, null);
  }

  public static NSArray<ERIAttributeGroup> fetchAllERIAttributeGroups(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERIAttributeGroup.fetchERIAttributeGroups(editingContext, null, sortOrderings);
  }

  public static NSArray<ERIAttributeGroup> fetchERIAttributeGroups(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERIAttributeGroup> fetchSpec = new ERXFetchSpecification<ERIAttributeGroup>(_ERIAttributeGroup.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERIAttributeGroup> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERIAttributeGroup fetchERIAttributeGroup(EOEditingContext editingContext, String keyName, Object value) {
    return _ERIAttributeGroup.fetchERIAttributeGroup(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERIAttributeGroup fetchERIAttributeGroup(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERIAttributeGroup> eoObjects = _ERIAttributeGroup.fetchERIAttributeGroups(editingContext, qualifier, null);
    ERIAttributeGroup eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERIAttributeGroup that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERIAttributeGroup fetchRequiredERIAttributeGroup(EOEditingContext editingContext, String keyName, Object value) {
    return _ERIAttributeGroup.fetchRequiredERIAttributeGroup(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERIAttributeGroup fetchRequiredERIAttributeGroup(EOEditingContext editingContext, EOQualifier qualifier) {
    ERIAttributeGroup eoObject = _ERIAttributeGroup.fetchERIAttributeGroup(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERIAttributeGroup that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERIAttributeGroup localInstanceIn(EOEditingContext editingContext, ERIAttributeGroup eo) {
    ERIAttributeGroup localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

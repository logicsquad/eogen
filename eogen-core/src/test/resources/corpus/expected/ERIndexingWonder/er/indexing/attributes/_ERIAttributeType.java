// DO NOT EDIT.  Make changes to ERIAttributeType.java instead.
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
public abstract class _ERIAttributeType extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERIAttributeType";

  // Attribute Keys
  public static final ERXKey<String> FORMAT = new ERXKey<String>("format", Type.Attribute);
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);
  public static final ERXKey<Integer> VALUE_TYPE = new ERXKey<Integer>("valueType", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.indexing.attributes.ERIAttribute> ATTRIBUTES = new ERXKey<er.indexing.attributes.ERIAttribute>("attributes", Type.ToManyRelationship);

  // Attributes
  public static final String FORMAT_KEY = FORMAT.key();
  public static final String NAME_KEY = NAME.key();
  public static final String VALUE_TYPE_KEY = VALUE_TYPE.key();

  // Relationships
  public static final String ATTRIBUTES_KEY = ATTRIBUTES.key();

  private static final Logger log = LoggerFactory.getLogger(_ERIAttributeType.class);

  public ERIAttributeType localInstanceIn(EOEditingContext editingContext) {
    ERIAttributeType localInstance = (ERIAttributeType)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String format() {
    return (String) storedValueForKey(_ERIAttributeType.FORMAT_KEY);
  }

  public void setFormat(String value) {
    log.debug( "updating format from {} to {}", format(), value);
    takeStoredValueForKey(value, _ERIAttributeType.FORMAT_KEY);
  }

  public String name() {
    return (String) storedValueForKey(_ERIAttributeType.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _ERIAttributeType.NAME_KEY);
  }

  public er.indexing.attributes.ERIValueType valueType() {
    Number value = (Number)storedValueForKey(_ERIAttributeType.VALUE_TYPE_KEY);
    return (er.indexing.attributes.ERIValueType)value;
  }

  public void setValueType(er.indexing.attributes.ERIValueType value) {
    log.debug( "updating valueType from {} to {}", valueType(), value);
    takeStoredValueForKey(value, _ERIAttributeType.VALUE_TYPE_KEY);
  }

  public NSArray<er.indexing.attributes.ERIAttribute> attributes() {
    return (NSArray<er.indexing.attributes.ERIAttribute>)storedValueForKey(_ERIAttributeType.ATTRIBUTES_KEY);
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
      EOQualifier inverseQualifier = ERXQ.equals(er.indexing.attributes.ERIAttribute.ATTRIBUTE_TYPE_KEY, this);

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
    includeObjectIntoPropertyWithKey(object, _ERIAttributeType.ATTRIBUTES_KEY);
  }

  public void removeFromAttributes(er.indexing.attributes.ERIAttribute object) {
    excludeObjectFromPropertyWithKey(object, _ERIAttributeType.ATTRIBUTES_KEY);
  }

  public void addToAttributesRelationship(er.indexing.attributes.ERIAttribute object) {
    log.debug("adding {} to attributes relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToAttributes(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _ERIAttributeType.ATTRIBUTES_KEY);
    }
  }

  public void removeFromAttributesRelationship(er.indexing.attributes.ERIAttribute object) {
    log.debug("removing {} from attributes relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromAttributes(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _ERIAttributeType.ATTRIBUTES_KEY);
    }
  }

  public er.indexing.attributes.ERIAttribute createAttributesRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.indexing.attributes.ERIAttribute.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _ERIAttributeType.ATTRIBUTES_KEY);
    return (er.indexing.attributes.ERIAttribute) eo;
  }

  public void deleteAttributesRelationship(er.indexing.attributes.ERIAttribute object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _ERIAttributeType.ATTRIBUTES_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllAttributesRelationships() {
    Enumeration<er.indexing.attributes.ERIAttribute> objects = attributes().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteAttributesRelationship(objects.nextElement());
    }
  }


  public static ERIAttributeType createERIAttributeType(EOEditingContext editingContext, String name
, er.indexing.attributes.ERIValueType valueType
) {
    ERIAttributeType eo = (ERIAttributeType) EOUtilities.createAndInsertInstance(editingContext, _ERIAttributeType.ENTITY_NAME);
    eo.setName(name);
    eo.setValueType(valueType);
    return eo;
  }

  public static ERXFetchSpecification<ERIAttributeType> fetchSpec() {
    return new ERXFetchSpecification<ERIAttributeType>(_ERIAttributeType.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERIAttributeType> fetchAllERIAttributeTypes(EOEditingContext editingContext) {
    return _ERIAttributeType.fetchAllERIAttributeTypes(editingContext, null);
  }

  public static NSArray<ERIAttributeType> fetchAllERIAttributeTypes(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERIAttributeType.fetchERIAttributeTypes(editingContext, null, sortOrderings);
  }

  public static NSArray<ERIAttributeType> fetchERIAttributeTypes(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERIAttributeType> fetchSpec = new ERXFetchSpecification<ERIAttributeType>(_ERIAttributeType.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERIAttributeType> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERIAttributeType fetchERIAttributeType(EOEditingContext editingContext, String keyName, Object value) {
    return _ERIAttributeType.fetchERIAttributeType(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERIAttributeType fetchERIAttributeType(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERIAttributeType> eoObjects = _ERIAttributeType.fetchERIAttributeTypes(editingContext, qualifier, null);
    ERIAttributeType eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERIAttributeType that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERIAttributeType fetchRequiredERIAttributeType(EOEditingContext editingContext, String keyName, Object value) {
    return _ERIAttributeType.fetchRequiredERIAttributeType(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERIAttributeType fetchRequiredERIAttributeType(EOEditingContext editingContext, EOQualifier qualifier) {
    ERIAttributeType eoObject = _ERIAttributeType.fetchERIAttributeType(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERIAttributeType that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERIAttributeType localInstanceIn(EOEditingContext editingContext, ERIAttributeType eo) {
    ERIAttributeType localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

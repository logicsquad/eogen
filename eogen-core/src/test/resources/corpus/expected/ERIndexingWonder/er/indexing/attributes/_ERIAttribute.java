// DO NOT EDIT.  Make changes to ERIAttribute.java instead.
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
public abstract class _ERIAttribute extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERIAttribute";

  // Attribute Keys
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);
  public static final ERXKey<er.extensions.foundation.ERXMutableArray> POSSIBLE_VALUES = new ERXKey<er.extensions.foundation.ERXMutableArray>("possibleValues", Type.Attribute);
  public static final ERXKey<Integer> STORAGE_TYPE = new ERXKey<Integer>("storageType", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.indexing.attributes.ERIAttributeGroup> ATTRIBUTE_GROUP = new ERXKey<er.indexing.attributes.ERIAttributeGroup>("attributeGroup", Type.ToOneRelationship);
  public static final ERXKey<er.indexing.attributes.ERIAttributeType> ATTRIBUTE_TYPE = new ERXKey<er.indexing.attributes.ERIAttributeType>("attributeType", Type.ToOneRelationship);
  public static final ERXKey<er.indexing.attributes.ERIValidationRule> VALIDATION_RULES = new ERXKey<er.indexing.attributes.ERIValidationRule>("validationRules", Type.ToManyRelationship);

  // Attributes
  public static final String NAME_KEY = NAME.key();
  public static final String POSSIBLE_VALUES_KEY = POSSIBLE_VALUES.key();
  public static final String STORAGE_TYPE_KEY = STORAGE_TYPE.key();

  // Relationships
  public static final String ATTRIBUTE_GROUP_KEY = ATTRIBUTE_GROUP.key();
  public static final String ATTRIBUTE_TYPE_KEY = ATTRIBUTE_TYPE.key();
  public static final String VALIDATION_RULES_KEY = VALIDATION_RULES.key();

  private static final Logger log = LoggerFactory.getLogger(_ERIAttribute.class);

  public ERIAttribute localInstanceIn(EOEditingContext editingContext) {
    ERIAttribute localInstance = (ERIAttribute)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String name() {
    return (String) storedValueForKey(_ERIAttribute.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _ERIAttribute.NAME_KEY);
  }

  public er.extensions.foundation.ERXMutableArray possibleValues() {
    return (er.extensions.foundation.ERXMutableArray) storedValueForKey(_ERIAttribute.POSSIBLE_VALUES_KEY);
  }

  public void setPossibleValues(er.extensions.foundation.ERXMutableArray value) {
    log.debug( "updating possibleValues from {} to {}", possibleValues(), value);
    takeStoredValueForKey(value, _ERIAttribute.POSSIBLE_VALUES_KEY);
  }

  public er.indexing.attributes.ERIStorageType storageType() {
    Number value = (Number)storedValueForKey(_ERIAttribute.STORAGE_TYPE_KEY);
    return (er.indexing.attributes.ERIStorageType)value;
  }

  public void setStorageType(er.indexing.attributes.ERIStorageType value) {
    log.debug( "updating storageType from {} to {}", storageType(), value);
    takeStoredValueForKey(value, _ERIAttribute.STORAGE_TYPE_KEY);
  }

  public er.indexing.attributes.ERIAttributeGroup attributeGroup() {
    return (er.indexing.attributes.ERIAttributeGroup)storedValueForKey(_ERIAttribute.ATTRIBUTE_GROUP_KEY);
  }

  public void setAttributeGroup(er.indexing.attributes.ERIAttributeGroup value) {
    takeStoredValueForKey(value, _ERIAttribute.ATTRIBUTE_GROUP_KEY);
  }

  public void setAttributeGroupRelationship(er.indexing.attributes.ERIAttributeGroup value) {
    log.debug("updating attributeGroup from {} to {}", attributeGroup(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setAttributeGroup(value);
    }
    else if (value == null) {
      er.indexing.attributes.ERIAttributeGroup oldValue = attributeGroup();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _ERIAttribute.ATTRIBUTE_GROUP_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _ERIAttribute.ATTRIBUTE_GROUP_KEY);
    }
  }

  public er.indexing.attributes.ERIAttributeType attributeType() {
    return (er.indexing.attributes.ERIAttributeType)storedValueForKey(_ERIAttribute.ATTRIBUTE_TYPE_KEY);
  }

  public void setAttributeType(er.indexing.attributes.ERIAttributeType value) {
    takeStoredValueForKey(value, _ERIAttribute.ATTRIBUTE_TYPE_KEY);
  }

  public void setAttributeTypeRelationship(er.indexing.attributes.ERIAttributeType value) {
    log.debug("updating attributeType from {} to {}", attributeType(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setAttributeType(value);
    }
    else if (value == null) {
      er.indexing.attributes.ERIAttributeType oldValue = attributeType();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _ERIAttribute.ATTRIBUTE_TYPE_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _ERIAttribute.ATTRIBUTE_TYPE_KEY);
    }
  }

  public NSArray<er.indexing.attributes.ERIValidationRule> validationRules() {
    return (NSArray<er.indexing.attributes.ERIValidationRule>)storedValueForKey(_ERIAttribute.VALIDATION_RULES_KEY);
  }

  public NSArray<er.indexing.attributes.ERIValidationRule> validationRules(EOQualifier qualifier) {
    return validationRules(qualifier, null);
  }

  public NSArray<er.indexing.attributes.ERIValidationRule> validationRules(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    NSArray<er.indexing.attributes.ERIValidationRule> results;
      results = validationRules();
      if (qualifier != null) {
        results = (NSArray<er.indexing.attributes.ERIValidationRule>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.indexing.attributes.ERIValidationRule>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    return results;
  }

  public void addToValidationRules(er.indexing.attributes.ERIValidationRule object) {
    includeObjectIntoPropertyWithKey(object, _ERIAttribute.VALIDATION_RULES_KEY);
  }

  public void removeFromValidationRules(er.indexing.attributes.ERIValidationRule object) {
    excludeObjectFromPropertyWithKey(object, _ERIAttribute.VALIDATION_RULES_KEY);
  }

  public void addToValidationRulesRelationship(er.indexing.attributes.ERIValidationRule object) {
    log.debug("adding {} to validationRules relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToValidationRules(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _ERIAttribute.VALIDATION_RULES_KEY);
    }
  }

  public void removeFromValidationRulesRelationship(er.indexing.attributes.ERIValidationRule object) {
    log.debug("removing {} from validationRules relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromValidationRules(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _ERIAttribute.VALIDATION_RULES_KEY);
    }
  }

  public er.indexing.attributes.ERIValidationRule createValidationRulesRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.indexing.attributes.ERIValidationRule.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _ERIAttribute.VALIDATION_RULES_KEY);
    return (er.indexing.attributes.ERIValidationRule) eo;
  }

  public void deleteValidationRulesRelationship(er.indexing.attributes.ERIValidationRule object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _ERIAttribute.VALIDATION_RULES_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllValidationRulesRelationships() {
    Enumeration<er.indexing.attributes.ERIValidationRule> objects = validationRules().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteValidationRulesRelationship(objects.nextElement());
    }
  }


  public static ERIAttribute createERIAttribute(EOEditingContext editingContext, String name
, er.indexing.attributes.ERIStorageType storageType
, er.indexing.attributes.ERIAttributeGroup attributeGroup, er.indexing.attributes.ERIAttributeType attributeType) {
    ERIAttribute eo = (ERIAttribute) EOUtilities.createAndInsertInstance(editingContext, _ERIAttribute.ENTITY_NAME);
    eo.setName(name);
    eo.setStorageType(storageType);
    eo.setAttributeGroupRelationship(attributeGroup);
    eo.setAttributeTypeRelationship(attributeType);
    return eo;
  }

  public static ERXFetchSpecification<ERIAttribute> fetchSpec() {
    return new ERXFetchSpecification<ERIAttribute>(_ERIAttribute.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERIAttribute> fetchAllERIAttributes(EOEditingContext editingContext) {
    return _ERIAttribute.fetchAllERIAttributes(editingContext, null);
  }

  public static NSArray<ERIAttribute> fetchAllERIAttributes(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERIAttribute.fetchERIAttributes(editingContext, null, sortOrderings);
  }

  public static NSArray<ERIAttribute> fetchERIAttributes(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERIAttribute> fetchSpec = new ERXFetchSpecification<ERIAttribute>(_ERIAttribute.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERIAttribute> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERIAttribute fetchERIAttribute(EOEditingContext editingContext, String keyName, Object value) {
    return _ERIAttribute.fetchERIAttribute(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERIAttribute fetchERIAttribute(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERIAttribute> eoObjects = _ERIAttribute.fetchERIAttributes(editingContext, qualifier, null);
    ERIAttribute eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERIAttribute that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERIAttribute fetchRequiredERIAttribute(EOEditingContext editingContext, String keyName, Object value) {
    return _ERIAttribute.fetchRequiredERIAttribute(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERIAttribute fetchRequiredERIAttribute(EOEditingContext editingContext, EOQualifier qualifier) {
    ERIAttribute eoObject = _ERIAttribute.fetchERIAttribute(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERIAttribute that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERIAttribute localInstanceIn(EOEditingContext editingContext, ERIAttribute eo) {
    ERIAttribute localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

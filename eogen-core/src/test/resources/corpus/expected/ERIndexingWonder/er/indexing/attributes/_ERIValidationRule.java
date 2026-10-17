// DO NOT EDIT.  Make changes to ERIValidationRule.java instead.
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
public abstract class _ERIValidationRule extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERIValidationRule";

  // Attribute Keys
  public static final ERXKey<String> DEFINITION = new ERXKey<String>("definition", Type.Attribute);
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.indexing.attributes.ERIAttribute> ATTRIBUTES = new ERXKey<er.indexing.attributes.ERIAttribute>("attributes", Type.ToManyRelationship);

  // Attributes
  public static final String DEFINITION_KEY = DEFINITION.key();
  public static final String NAME_KEY = NAME.key();

  // Relationships
  public static final String ATTRIBUTES_KEY = ATTRIBUTES.key();

  private static final Logger log = LoggerFactory.getLogger(_ERIValidationRule.class);

  public ERIValidationRule localInstanceIn(EOEditingContext editingContext) {
    ERIValidationRule localInstance = (ERIValidationRule)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String definition() {
    return (String) storedValueForKey(_ERIValidationRule.DEFINITION_KEY);
  }

  public void setDefinition(String value) {
    log.debug( "updating definition from {} to {}", definition(), value);
    takeStoredValueForKey(value, _ERIValidationRule.DEFINITION_KEY);
  }

  public String name() {
    return (String) storedValueForKey(_ERIValidationRule.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _ERIValidationRule.NAME_KEY);
  }

  public NSArray<er.indexing.attributes.ERIAttribute> attributes() {
    return (NSArray<er.indexing.attributes.ERIAttribute>)storedValueForKey(_ERIValidationRule.ATTRIBUTES_KEY);
  }

  public NSArray<er.indexing.attributes.ERIAttribute> attributes(EOQualifier qualifier) {
    return attributes(qualifier, null);
  }

  public NSArray<er.indexing.attributes.ERIAttribute> attributes(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    NSArray<er.indexing.attributes.ERIAttribute> results;
      results = attributes();
      if (qualifier != null) {
        results = (NSArray<er.indexing.attributes.ERIAttribute>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.indexing.attributes.ERIAttribute>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    return results;
  }

  public void addToAttributes(er.indexing.attributes.ERIAttribute object) {
    includeObjectIntoPropertyWithKey(object, _ERIValidationRule.ATTRIBUTES_KEY);
  }

  public void removeFromAttributes(er.indexing.attributes.ERIAttribute object) {
    excludeObjectFromPropertyWithKey(object, _ERIValidationRule.ATTRIBUTES_KEY);
  }

  public void addToAttributesRelationship(er.indexing.attributes.ERIAttribute object) {
    log.debug("adding {} to attributes relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToAttributes(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _ERIValidationRule.ATTRIBUTES_KEY);
    }
  }

  public void removeFromAttributesRelationship(er.indexing.attributes.ERIAttribute object) {
    log.debug("removing {} from attributes relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromAttributes(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _ERIValidationRule.ATTRIBUTES_KEY);
    }
  }

  public er.indexing.attributes.ERIAttribute createAttributesRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.indexing.attributes.ERIAttribute.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _ERIValidationRule.ATTRIBUTES_KEY);
    return (er.indexing.attributes.ERIAttribute) eo;
  }

  public void deleteAttributesRelationship(er.indexing.attributes.ERIAttribute object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _ERIValidationRule.ATTRIBUTES_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllAttributesRelationships() {
    Enumeration<er.indexing.attributes.ERIAttribute> objects = attributes().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteAttributesRelationship(objects.nextElement());
    }
  }


  public static ERIValidationRule createERIValidationRule(EOEditingContext editingContext, String definition
, String name
) {
    ERIValidationRule eo = (ERIValidationRule) EOUtilities.createAndInsertInstance(editingContext, _ERIValidationRule.ENTITY_NAME);
    eo.setDefinition(definition);
    eo.setName(name);
    return eo;
  }

  public static ERXFetchSpecification<ERIValidationRule> fetchSpec() {
    return new ERXFetchSpecification<ERIValidationRule>(_ERIValidationRule.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERIValidationRule> fetchAllERIValidationRules(EOEditingContext editingContext) {
    return _ERIValidationRule.fetchAllERIValidationRules(editingContext, null);
  }

  public static NSArray<ERIValidationRule> fetchAllERIValidationRules(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERIValidationRule.fetchERIValidationRules(editingContext, null, sortOrderings);
  }

  public static NSArray<ERIValidationRule> fetchERIValidationRules(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERIValidationRule> fetchSpec = new ERXFetchSpecification<ERIValidationRule>(_ERIValidationRule.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERIValidationRule> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERIValidationRule fetchERIValidationRule(EOEditingContext editingContext, String keyName, Object value) {
    return _ERIValidationRule.fetchERIValidationRule(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERIValidationRule fetchERIValidationRule(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERIValidationRule> eoObjects = _ERIValidationRule.fetchERIValidationRules(editingContext, qualifier, null);
    ERIValidationRule eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERIValidationRule that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERIValidationRule fetchRequiredERIValidationRule(EOEditingContext editingContext, String keyName, Object value) {
    return _ERIValidationRule.fetchRequiredERIValidationRule(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERIValidationRule fetchRequiredERIValidationRule(EOEditingContext editingContext, EOQualifier qualifier) {
    ERIValidationRule eoObject = _ERIValidationRule.fetchERIValidationRule(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERIValidationRule that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERIValidationRule localInstanceIn(EOEditingContext editingContext, ERIValidationRule eo) {
    ERIValidationRule localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

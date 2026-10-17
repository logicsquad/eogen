// DO NOT EDIT.  Make changes to EmployeeType.java instead.
package er.example.erxpartials.model;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

import er.extensions.eof.*;
import er.extensions.foundation.*;

@SuppressWarnings("all")
public abstract class _EmployeeType extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "EmployeeType";

  // Attribute Keys
  public static final ERXKey<String> NAME = new ERXKey<String>("name");
  // Relationship Keys
  public static final ERXKey<er.example.erxpartials.model.Person> PARTIAL__EMPLOYEE_PERSONS = new ERXKey<er.example.erxpartials.model.Person>("partial_EmployeePersons");

  // Attributes
  public static final String NAME_KEY = NAME.key();
  // Relationships
  public static final String PARTIAL__EMPLOYEE_PERSONS_KEY = PARTIAL__EMPLOYEE_PERSONS.key();

  private static Logger LOG = Logger.getLogger(_EmployeeType.class);

  public EmployeeType localInstanceIn(EOEditingContext editingContext) {
    EmployeeType localInstance = (EmployeeType)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String name() {
    return (String) storedValueForKey(_EmployeeType.NAME_KEY);
  }

  public void setName(String value) {
    if (_EmployeeType.LOG.isDebugEnabled()) {
    	_EmployeeType.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, _EmployeeType.NAME_KEY);
  }

  public NSArray<er.example.erxpartials.model.Person> partial_EmployeePersons() {
    return (NSArray<er.example.erxpartials.model.Person>)storedValueForKey(_EmployeeType.PARTIAL__EMPLOYEE_PERSONS_KEY);
  }

  public NSArray<er.example.erxpartials.model.Person> partial_EmployeePersons(EOQualifier qualifier) {
    return partial_EmployeePersons(qualifier, null, false);
  }

  public NSArray<er.example.erxpartials.model.Person> partial_EmployeePersons(EOQualifier qualifier, boolean fetch) {
    return partial_EmployeePersons(qualifier, null, fetch);
  }

  public NSArray<er.example.erxpartials.model.Person> partial_EmployeePersons(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.example.erxpartials.model.Person> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = new EOKeyValueQualifier(er.example.erxpartials.model.Person.EMPLOYEE_TYPE_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        NSMutableArray<EOQualifier> qualifiers = new NSMutableArray<EOQualifier>();
        qualifiers.addObject(qualifier);
        qualifiers.addObject(inverseQualifier);
        fullQualifier = new EOAndQualifier(qualifiers);
      }

      results = er.example.erxpartials.model.Person.fetchPersons(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = partial_EmployeePersons();
      if (qualifier != null) {
        results = (NSArray<er.example.erxpartials.model.Person>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.example.erxpartials.model.Person>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }
  
  public void addToPartial_EmployeePersons(er.example.erxpartials.model.Person object) {
    includeObjectIntoPropertyWithKey(object, _EmployeeType.PARTIAL__EMPLOYEE_PERSONS_KEY);
  }

  public void removeFromPartial_EmployeePersons(er.example.erxpartials.model.Person object) {
    excludeObjectFromPropertyWithKey(object, _EmployeeType.PARTIAL__EMPLOYEE_PERSONS_KEY);
  }

  public void addToPartial_EmployeePersonsRelationship(er.example.erxpartials.model.Person object) {
    if (_EmployeeType.LOG.isDebugEnabled()) {
      _EmployeeType.LOG.debug("adding " + object + " to partial_EmployeePersons relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	addToPartial_EmployeePersons(object);
    }
    else {
    	addObjectToBothSidesOfRelationshipWithKey(object, _EmployeeType.PARTIAL__EMPLOYEE_PERSONS_KEY);
    }
  }

  public void removeFromPartial_EmployeePersonsRelationship(er.example.erxpartials.model.Person object) {
    if (_EmployeeType.LOG.isDebugEnabled()) {
      _EmployeeType.LOG.debug("removing " + object + " from partial_EmployeePersons relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	removeFromPartial_EmployeePersons(object);
    }
    else {
    	removeObjectFromBothSidesOfRelationshipWithKey(object, _EmployeeType.PARTIAL__EMPLOYEE_PERSONS_KEY);
    }
  }

  public er.example.erxpartials.model.Person createPartial_EmployeePersonsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName( er.example.erxpartials.model.Person.ENTITY_NAME );
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, _EmployeeType.PARTIAL__EMPLOYEE_PERSONS_KEY);
    return (er.example.erxpartials.model.Person) eo;
  }

  public void deletePartial_EmployeePersonsRelationship(er.example.erxpartials.model.Person object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _EmployeeType.PARTIAL__EMPLOYEE_PERSONS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllPartial_EmployeePersonsRelationships() {
    Enumeration<er.example.erxpartials.model.Person> objects = partial_EmployeePersons().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deletePartial_EmployeePersonsRelationship(objects.nextElement());
    }
  }


  public static EmployeeType createEmployeeType(EOEditingContext editingContext, String name
) {
    EmployeeType eo = (EmployeeType) EOUtilities.createAndInsertInstance(editingContext, _EmployeeType.ENTITY_NAME);    
		eo.setName(name);
    return eo;
  }

  public static ERXFetchSpecification<EmployeeType> fetchSpec() {
    return new ERXFetchSpecification<EmployeeType>(_EmployeeType.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<EmployeeType> fetchAllEmployeeTypes(EOEditingContext editingContext) {
    return _EmployeeType.fetchAllEmployeeTypes(editingContext, null);
  }

  public static NSArray<EmployeeType> fetchAllEmployeeTypes(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _EmployeeType.fetchEmployeeTypes(editingContext, null, sortOrderings);
  }

  public static NSArray<EmployeeType> fetchEmployeeTypes(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<EmployeeType> fetchSpec = new ERXFetchSpecification<EmployeeType>(_EmployeeType.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<EmployeeType> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static EmployeeType fetchEmployeeType(EOEditingContext editingContext, String keyName, Object value) {
    return _EmployeeType.fetchEmployeeType(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static EmployeeType fetchEmployeeType(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<EmployeeType> eoObjects = _EmployeeType.fetchEmployeeTypes(editingContext, qualifier, null);
    EmployeeType eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one EmployeeType that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static EmployeeType fetchRequiredEmployeeType(EOEditingContext editingContext, String keyName, Object value) {
    return _EmployeeType.fetchRequiredEmployeeType(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static EmployeeType fetchRequiredEmployeeType(EOEditingContext editingContext, EOQualifier qualifier) {
    EmployeeType eoObject = _EmployeeType.fetchEmployeeType(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no EmployeeType that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static EmployeeType localInstanceIn(EOEditingContext editingContext, EmployeeType eo) {
    EmployeeType localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

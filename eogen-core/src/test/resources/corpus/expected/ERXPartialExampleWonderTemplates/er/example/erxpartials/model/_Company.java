// DO NOT EDIT.  Make changes to Company.java instead.
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
public abstract class _Company extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Company";

  // Attribute Keys
  public static final ERXKey<String> ADDRESS1 = new ERXKey<String>("address1");
  public static final ERXKey<String> ADDRESS2 = new ERXKey<String>("address2");
  public static final ERXKey<String> CITY = new ERXKey<String>("city");
  public static final ERXKey<String> NAME = new ERXKey<String>("name");
  public static final ERXKey<String> STATE = new ERXKey<String>("state");
  public static final ERXKey<String> ZIPCODE = new ERXKey<String>("zipcode");
  // Relationship Keys
  public static final ERXKey<er.example.erxpartials.model.Department> DEPARTMENTS = new ERXKey<er.example.erxpartials.model.Department>("departments");

  // Attributes
  public static final String ADDRESS1_KEY = ADDRESS1.key();
  public static final String ADDRESS2_KEY = ADDRESS2.key();
  public static final String CITY_KEY = CITY.key();
  public static final String NAME_KEY = NAME.key();
  public static final String STATE_KEY = STATE.key();
  public static final String ZIPCODE_KEY = ZIPCODE.key();
  // Relationships
  public static final String DEPARTMENTS_KEY = DEPARTMENTS.key();

  private static Logger LOG = Logger.getLogger(_Company.class);

  public Company localInstanceIn(EOEditingContext editingContext) {
    Company localInstance = (Company)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String address1() {
    return (String) storedValueForKey(_Company.ADDRESS1_KEY);
  }

  public void setAddress1(String value) {
    if (_Company.LOG.isDebugEnabled()) {
    	_Company.LOG.debug( "updating address1 from " + address1() + " to " + value);
    }
    takeStoredValueForKey(value, _Company.ADDRESS1_KEY);
  }

  public String address2() {
    return (String) storedValueForKey(_Company.ADDRESS2_KEY);
  }

  public void setAddress2(String value) {
    if (_Company.LOG.isDebugEnabled()) {
    	_Company.LOG.debug( "updating address2 from " + address2() + " to " + value);
    }
    takeStoredValueForKey(value, _Company.ADDRESS2_KEY);
  }

  public String city() {
    return (String) storedValueForKey(_Company.CITY_KEY);
  }

  public void setCity(String value) {
    if (_Company.LOG.isDebugEnabled()) {
    	_Company.LOG.debug( "updating city from " + city() + " to " + value);
    }
    takeStoredValueForKey(value, _Company.CITY_KEY);
  }

  public String name() {
    return (String) storedValueForKey(_Company.NAME_KEY);
  }

  public void setName(String value) {
    if (_Company.LOG.isDebugEnabled()) {
    	_Company.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, _Company.NAME_KEY);
  }

  public String state() {
    return (String) storedValueForKey(_Company.STATE_KEY);
  }

  public void setState(String value) {
    if (_Company.LOG.isDebugEnabled()) {
    	_Company.LOG.debug( "updating state from " + state() + " to " + value);
    }
    takeStoredValueForKey(value, _Company.STATE_KEY);
  }

  public String zipcode() {
    return (String) storedValueForKey(_Company.ZIPCODE_KEY);
  }

  public void setZipcode(String value) {
    if (_Company.LOG.isDebugEnabled()) {
    	_Company.LOG.debug( "updating zipcode from " + zipcode() + " to " + value);
    }
    takeStoredValueForKey(value, _Company.ZIPCODE_KEY);
  }

  public NSArray<er.example.erxpartials.model.Department> departments() {
    return (NSArray<er.example.erxpartials.model.Department>)storedValueForKey(_Company.DEPARTMENTS_KEY);
  }

  public NSArray<er.example.erxpartials.model.Department> departments(EOQualifier qualifier) {
    return departments(qualifier, null, false);
  }

  public NSArray<er.example.erxpartials.model.Department> departments(EOQualifier qualifier, boolean fetch) {
    return departments(qualifier, null, fetch);
  }

  public NSArray<er.example.erxpartials.model.Department> departments(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.example.erxpartials.model.Department> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = new EOKeyValueQualifier(er.example.erxpartials.model.Department.COMPANY_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        NSMutableArray<EOQualifier> qualifiers = new NSMutableArray<EOQualifier>();
        qualifiers.addObject(qualifier);
        qualifiers.addObject(inverseQualifier);
        fullQualifier = new EOAndQualifier(qualifiers);
      }

      results = er.example.erxpartials.model.Department.fetchDepartments(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = departments();
      if (qualifier != null) {
        results = (NSArray<er.example.erxpartials.model.Department>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.example.erxpartials.model.Department>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }
  
  public void addToDepartments(er.example.erxpartials.model.Department object) {
    includeObjectIntoPropertyWithKey(object, _Company.DEPARTMENTS_KEY);
  }

  public void removeFromDepartments(er.example.erxpartials.model.Department object) {
    excludeObjectFromPropertyWithKey(object, _Company.DEPARTMENTS_KEY);
  }

  public void addToDepartmentsRelationship(er.example.erxpartials.model.Department object) {
    if (_Company.LOG.isDebugEnabled()) {
      _Company.LOG.debug("adding " + object + " to departments relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	addToDepartments(object);
    }
    else {
    	addObjectToBothSidesOfRelationshipWithKey(object, _Company.DEPARTMENTS_KEY);
    }
  }

  public void removeFromDepartmentsRelationship(er.example.erxpartials.model.Department object) {
    if (_Company.LOG.isDebugEnabled()) {
      _Company.LOG.debug("removing " + object + " from departments relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	removeFromDepartments(object);
    }
    else {
    	removeObjectFromBothSidesOfRelationshipWithKey(object, _Company.DEPARTMENTS_KEY);
    }
  }

  public er.example.erxpartials.model.Department createDepartmentsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName( er.example.erxpartials.model.Department.ENTITY_NAME );
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, _Company.DEPARTMENTS_KEY);
    return (er.example.erxpartials.model.Department) eo;
  }

  public void deleteDepartmentsRelationship(er.example.erxpartials.model.Department object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Company.DEPARTMENTS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllDepartmentsRelationships() {
    Enumeration<er.example.erxpartials.model.Department> objects = departments().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteDepartmentsRelationship(objects.nextElement());
    }
  }


  public static Company createCompany(EOEditingContext editingContext, String name
) {
    Company eo = (Company) EOUtilities.createAndInsertInstance(editingContext, _Company.ENTITY_NAME);    
		eo.setName(name);
    return eo;
  }

  public static ERXFetchSpecification<Company> fetchSpec() {
    return new ERXFetchSpecification<Company>(_Company.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Company> fetchAllCompanies(EOEditingContext editingContext) {
    return _Company.fetchAllCompanies(editingContext, null);
  }

  public static NSArray<Company> fetchAllCompanies(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Company.fetchCompanies(editingContext, null, sortOrderings);
  }

  public static NSArray<Company> fetchCompanies(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Company> fetchSpec = new ERXFetchSpecification<Company>(_Company.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Company> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Company fetchCompany(EOEditingContext editingContext, String keyName, Object value) {
    return _Company.fetchCompany(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Company fetchCompany(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Company> eoObjects = _Company.fetchCompanies(editingContext, qualifier, null);
    Company eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Company that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Company fetchRequiredCompany(EOEditingContext editingContext, String keyName, Object value) {
    return _Company.fetchRequiredCompany(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Company fetchRequiredCompany(EOEditingContext editingContext, EOQualifier qualifier) {
    Company eoObject = _Company.fetchCompany(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Company that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Company localInstanceIn(EOEditingContext editingContext, Company eo) {
    Company localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

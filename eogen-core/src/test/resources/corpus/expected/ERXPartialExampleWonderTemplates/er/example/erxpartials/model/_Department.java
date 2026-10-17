// DO NOT EDIT.  Make changes to Department.java instead.
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
public abstract class _Department extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Department";

  // Attribute Keys
  public static final ERXKey<String> DEPARTMENT_CODE = new ERXKey<String>("departmentCode");
  public static final ERXKey<String> NAME = new ERXKey<String>("name");
  // Relationship Keys
  public static final ERXKey<er.example.erxpartials.model.Company> COMPANY = new ERXKey<er.example.erxpartials.model.Company>("company");
  public static final ERXKey<er.example.erxpartials.model.Person> PARTIAL__EMPLOYEE_PERSONS = new ERXKey<er.example.erxpartials.model.Person>("partial_EmployeePersons");

  // Attributes
  public static final String DEPARTMENT_CODE_KEY = DEPARTMENT_CODE.key();
  public static final String NAME_KEY = NAME.key();
  // Relationships
  public static final String COMPANY_KEY = COMPANY.key();
  public static final String PARTIAL__EMPLOYEE_PERSONS_KEY = PARTIAL__EMPLOYEE_PERSONS.key();

  private static Logger LOG = Logger.getLogger(_Department.class);

  public Department localInstanceIn(EOEditingContext editingContext) {
    Department localInstance = (Department)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String departmentCode() {
    return (String) storedValueForKey(_Department.DEPARTMENT_CODE_KEY);
  }

  public void setDepartmentCode(String value) {
    if (_Department.LOG.isDebugEnabled()) {
    	_Department.LOG.debug( "updating departmentCode from " + departmentCode() + " to " + value);
    }
    takeStoredValueForKey(value, _Department.DEPARTMENT_CODE_KEY);
  }

  public String name() {
    return (String) storedValueForKey(_Department.NAME_KEY);
  }

  public void setName(String value) {
    if (_Department.LOG.isDebugEnabled()) {
    	_Department.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, _Department.NAME_KEY);
  }

  public er.example.erxpartials.model.Company company() {
    return (er.example.erxpartials.model.Company)storedValueForKey(_Department.COMPANY_KEY);
  }
  
  public void setCompany(er.example.erxpartials.model.Company value) {
    takeStoredValueForKey(value, _Department.COMPANY_KEY);
  }

  public void setCompanyRelationship(er.example.erxpartials.model.Company value) {
    if (_Department.LOG.isDebugEnabled()) {
      _Department.LOG.debug("updating company from " + company() + " to " + value);
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	setCompany(value);
    }
    else if (value == null) {
    	er.example.erxpartials.model.Company oldValue = company();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Department.COMPANY_KEY);
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, _Department.COMPANY_KEY);
    }
  }
  
  public NSArray<er.example.erxpartials.model.Person> partial_EmployeePersons() {
    return (NSArray<er.example.erxpartials.model.Person>)storedValueForKey(_Department.PARTIAL__EMPLOYEE_PERSONS_KEY);
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
      EOQualifier inverseQualifier = new EOKeyValueQualifier(er.example.erxpartials.model.Person.DEPARTMENT_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
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
    includeObjectIntoPropertyWithKey(object, _Department.PARTIAL__EMPLOYEE_PERSONS_KEY);
  }

  public void removeFromPartial_EmployeePersons(er.example.erxpartials.model.Person object) {
    excludeObjectFromPropertyWithKey(object, _Department.PARTIAL__EMPLOYEE_PERSONS_KEY);
  }

  public void addToPartial_EmployeePersonsRelationship(er.example.erxpartials.model.Person object) {
    if (_Department.LOG.isDebugEnabled()) {
      _Department.LOG.debug("adding " + object + " to partial_EmployeePersons relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	addToPartial_EmployeePersons(object);
    }
    else {
    	addObjectToBothSidesOfRelationshipWithKey(object, _Department.PARTIAL__EMPLOYEE_PERSONS_KEY);
    }
  }

  public void removeFromPartial_EmployeePersonsRelationship(er.example.erxpartials.model.Person object) {
    if (_Department.LOG.isDebugEnabled()) {
      _Department.LOG.debug("removing " + object + " from partial_EmployeePersons relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	removeFromPartial_EmployeePersons(object);
    }
    else {
    	removeObjectFromBothSidesOfRelationshipWithKey(object, _Department.PARTIAL__EMPLOYEE_PERSONS_KEY);
    }
  }

  public er.example.erxpartials.model.Person createPartial_EmployeePersonsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName( er.example.erxpartials.model.Person.ENTITY_NAME );
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, _Department.PARTIAL__EMPLOYEE_PERSONS_KEY);
    return (er.example.erxpartials.model.Person) eo;
  }

  public void deletePartial_EmployeePersonsRelationship(er.example.erxpartials.model.Person object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Department.PARTIAL__EMPLOYEE_PERSONS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllPartial_EmployeePersonsRelationships() {
    Enumeration<er.example.erxpartials.model.Person> objects = partial_EmployeePersons().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deletePartial_EmployeePersonsRelationship(objects.nextElement());
    }
  }


  public static Department createDepartment(EOEditingContext editingContext, String departmentCode
, String name
, er.example.erxpartials.model.Company company) {
    Department eo = (Department) EOUtilities.createAndInsertInstance(editingContext, _Department.ENTITY_NAME);    
		eo.setDepartmentCode(departmentCode);
		eo.setName(name);
    eo.setCompanyRelationship(company);
    return eo;
  }

  public static ERXFetchSpecification<Department> fetchSpec() {
    return new ERXFetchSpecification<Department>(_Department.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Department> fetchAllDepartments(EOEditingContext editingContext) {
    return _Department.fetchAllDepartments(editingContext, null);
  }

  public static NSArray<Department> fetchAllDepartments(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Department.fetchDepartments(editingContext, null, sortOrderings);
  }

  public static NSArray<Department> fetchDepartments(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Department> fetchSpec = new ERXFetchSpecification<Department>(_Department.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Department> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Department fetchDepartment(EOEditingContext editingContext, String keyName, Object value) {
    return _Department.fetchDepartment(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Department fetchDepartment(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Department> eoObjects = _Department.fetchDepartments(editingContext, qualifier, null);
    Department eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Department that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Department fetchRequiredDepartment(EOEditingContext editingContext, String keyName, Object value) {
    return _Department.fetchRequiredDepartment(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Department fetchRequiredDepartment(EOEditingContext editingContext, EOQualifier qualifier) {
    Department eoObject = _Department.fetchDepartment(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Department that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Department localInstanceIn(EOEditingContext editingContext, Department eo) {
    Department localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

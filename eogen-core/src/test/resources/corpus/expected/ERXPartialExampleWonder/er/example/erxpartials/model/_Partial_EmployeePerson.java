// DO NOT EDIT.  Make changes to Partial_EmployeePerson.java instead.
package er.example.erxpartials.model;

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
public abstract class _Partial_EmployeePerson extends er.extensions.partials.ERXPartial<er.example.erxpartials.model.Person> {
  public static final String ENTITY_NAME = "Person";

  // Attribute Keys
  public static final ERXKey<String> EMPLOYEE_NUMBER = new ERXKey<String>("employeeNumber", Type.Attribute);
  public static final ERXKey<java.math.BigDecimal> SALARY = new ERXKey<java.math.BigDecimal>("salary", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.example.erxpartials.model.Department> DEPARTMENT = new ERXKey<er.example.erxpartials.model.Department>("department", Type.ToOneRelationship);
  public static final ERXKey<er.example.erxpartials.model.EmployeeType> EMPLOYEE_TYPE = new ERXKey<er.example.erxpartials.model.EmployeeType>("employeeType", Type.ToOneRelationship);

  // Attributes
  public static final String EMPLOYEE_NUMBER_KEY = EMPLOYEE_NUMBER.key();
  public static final String SALARY_KEY = SALARY.key();

  // Relationships
  public static final String DEPARTMENT_KEY = DEPARTMENT.key();
  public static final String EMPLOYEE_TYPE_KEY = EMPLOYEE_TYPE.key();

  private static final Logger log = LoggerFactory.getLogger(_Partial_EmployeePerson.class);

  public String employeeNumber() {
    return (String) storedValueForKey(_Partial_EmployeePerson.EMPLOYEE_NUMBER_KEY);
  }

  public void setEmployeeNumber(String value) {
    log.debug( "updating employeeNumber from {} to {}", employeeNumber(), value);
    takeStoredValueForKey(value, _Partial_EmployeePerson.EMPLOYEE_NUMBER_KEY);
  }

  public java.math.BigDecimal salary() {
    return (java.math.BigDecimal) storedValueForKey(_Partial_EmployeePerson.SALARY_KEY);
  }

  public void setSalary(java.math.BigDecimal value) {
    log.debug( "updating salary from {} to {}", salary(), value);
    takeStoredValueForKey(value, _Partial_EmployeePerson.SALARY_KEY);
  }

  public er.example.erxpartials.model.Department department() {
    return (er.example.erxpartials.model.Department)storedValueForKey(_Partial_EmployeePerson.DEPARTMENT_KEY);
  }

  public void setDepartment(er.example.erxpartials.model.Department value) {
    takeStoredValueForKey(value, _Partial_EmployeePerson.DEPARTMENT_KEY);
  }

  public void setDepartmentRelationship(er.example.erxpartials.model.Department value) {
    log.debug("updating department from {} to {}", department(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setDepartment(value);
    }
    else if (value == null) {
      er.example.erxpartials.model.Department oldValue = department();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Partial_EmployeePerson.DEPARTMENT_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Partial_EmployeePerson.DEPARTMENT_KEY);
    }
  }

  public er.example.erxpartials.model.EmployeeType employeeType() {
    return (er.example.erxpartials.model.EmployeeType)storedValueForKey(_Partial_EmployeePerson.EMPLOYEE_TYPE_KEY);
  }

  public void setEmployeeType(er.example.erxpartials.model.EmployeeType value) {
    takeStoredValueForKey(value, _Partial_EmployeePerson.EMPLOYEE_TYPE_KEY);
  }

  public void setEmployeeTypeRelationship(er.example.erxpartials.model.EmployeeType value) {
    log.debug("updating employeeType from {} to {}", employeeType(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setEmployeeType(value);
    }
    else if (value == null) {
      er.example.erxpartials.model.EmployeeType oldValue = employeeType();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Partial_EmployeePerson.EMPLOYEE_TYPE_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Partial_EmployeePerson.EMPLOYEE_TYPE_KEY);
    }
  }


  public Partial_EmployeePerson initPartial_EmployeePerson(EOEditingContext editingContext) {
    Partial_EmployeePerson eo = (Partial_EmployeePerson)this;
    return eo;
  }
}

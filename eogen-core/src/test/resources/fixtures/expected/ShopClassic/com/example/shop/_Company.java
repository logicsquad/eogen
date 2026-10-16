// $LastChangedRevision$ DO NOT EDIT.  Make changes to Company.java instead.
package com.example.shop;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Company extends com.example.shop.Party {
	public static final String ENTITY_NAME = "Company";

	// Attributes
	public static final String EMAIL_KEY = "email";
	public static final String NAME_KEY = "name";
	public static final String STATUS_KEY = "status";
	public static final String TAX_NUMBER_KEY = "taxNumber";

	// Relationships
	public static final String ORDERS_KEY = "orders";

  private static Logger LOG = Logger.getLogger(_Company.class);

  public Company localInstanceIn(EOEditingContext editingContext) {
    Company localInstance = (Company)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String taxNumber() {
    return (String) storedValueForKey("taxNumber");
  }

  public void setTaxNumber(String value) {
    if (_Company.LOG.isDebugEnabled()) {
    	_Company.LOG.debug( "updating taxNumber from " + taxNumber() + " to " + value);
    }
    takeStoredValueForKey(value, "taxNumber");
  }


  public static Company createCompany(EOEditingContext editingContext, String email
, String name
, com.example.shop.Status status
, String taxNumber
) {
    Company eo = (Company) EOUtilities.createAndInsertInstance(editingContext, _Company.ENTITY_NAME);    
		eo.setEmail(email);
		eo.setName(name);
		eo.setStatus(status);
		eo.setTaxNumber(taxNumber);
    return eo;
  }

  public static NSArray<Company> fetchAllCompanies(EOEditingContext editingContext) {
    return _Company.fetchAllCompanies(editingContext, null);
  }

  public static NSArray<Company> fetchAllCompanies(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Company.fetchCompanies(editingContext, null, sortOrderings);
  }

  public static NSArray<Company> fetchCompanies(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Company.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Company> eoObjects = (NSArray<Company>)editingContext.objectsWithFetchSpecification(fetchSpec);
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
      eoObject = (Company)eoObjects.objectAtIndex(0);
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
    Company localInstance = (eo == null) ? null : (Company)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

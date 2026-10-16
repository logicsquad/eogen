// DO NOT EDIT.  Make changes to Company.java instead.
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
public abstract class _Company extends com.example.shop.Party {
  public static final String ENTITY_NAME = "Company";

  // Attribute Keys
  public static final ERXKey<String> TAX_NUMBER = new ERXKey<String>("taxNumber", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String TAX_NUMBER_KEY = TAX_NUMBER.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_Company.class);

  @Override
  public Company localInstanceIn(EOEditingContext editingContext) {
    Company localInstance = (Company)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String taxNumber() {
    return (String) storedValueForKey(_Company.TAX_NUMBER_KEY);
  }

  public void setTaxNumber(String value) {
    log.debug( "updating taxNumber from {} to {}", taxNumber(), value);
    takeStoredValueForKey(value, _Company.TAX_NUMBER_KEY);
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

  public static ERXFetchSpecification<Company> fetchSpecForCompany() {
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
    NSArray<Company> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Company fetchCompany(EOEditingContext editingContext, String keyName, Object value) {
    return _Company.fetchCompany(editingContext, ERXQ.equals(keyName, value));
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
    return _Company.fetchRequiredCompany(editingContext, ERXQ.equals(keyName, value));
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

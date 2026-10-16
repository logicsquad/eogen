// DO NOT EDIT.  Make changes to Person.java instead.
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
public abstract class _Person extends com.example.shop.Party {
  public static final String ENTITY_NAME = "Person";

  // Attribute Keys
  public static final ERXKey<NSTimestamp> DATE_OF_BIRTH = new ERXKey<NSTimestamp>("dateOfBirth", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String DATE_OF_BIRTH_KEY = DATE_OF_BIRTH.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_Person.class);

  @Override
  public Person localInstanceIn(EOEditingContext editingContext) {
    Person localInstance = (Person)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSTimestamp dateOfBirth() {
    return (NSTimestamp) storedValueForKey(_Person.DATE_OF_BIRTH_KEY);
  }

  public void setDateOfBirth(NSTimestamp value) {
    log.debug( "updating dateOfBirth from {} to {}", dateOfBirth(), value);
    takeStoredValueForKey(value, _Person.DATE_OF_BIRTH_KEY);
  }


  public static Person createPerson(EOEditingContext editingContext, NSTimestamp dateOfBirth
, String email
, String name
, com.example.shop.Status status
) {
    Person eo = (Person) EOUtilities.createAndInsertInstance(editingContext, _Person.ENTITY_NAME);
    eo.setDateOfBirth(dateOfBirth);
    eo.setEmail(email);
    eo.setName(name);
    eo.setStatus(status);
    return eo;
  }

  public static ERXFetchSpecification<Person> fetchSpecForPerson() {
    return new ERXFetchSpecification<Person>(_Person.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Person> fetchAllPersons(EOEditingContext editingContext) {
    return _Person.fetchAllPersons(editingContext, null);
  }

  public static NSArray<Person> fetchAllPersons(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Person.fetchPersons(editingContext, null, sortOrderings);
  }

  public static NSArray<Person> fetchPersons(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Person> fetchSpec = new ERXFetchSpecification<Person>(_Person.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Person> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Person fetchPerson(EOEditingContext editingContext, String keyName, Object value) {
    return _Person.fetchPerson(editingContext, ERXQ.equals(keyName, value));
  }

  public static Person fetchPerson(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Person> eoObjects = _Person.fetchPersons(editingContext, qualifier, null);
    Person eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Person that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Person fetchRequiredPerson(EOEditingContext editingContext, String keyName, Object value) {
    return _Person.fetchRequiredPerson(editingContext, ERXQ.equals(keyName, value));
  }

  public static Person fetchRequiredPerson(EOEditingContext editingContext, EOQualifier qualifier) {
    Person eoObject = _Person.fetchPerson(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Person that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Person localInstanceIn(EOEditingContext editingContext, Person eo) {
    Person localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

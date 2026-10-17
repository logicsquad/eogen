// DO NOT EDIT.  Make changes to Account.java instead.
package com.example.ledger;

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
public abstract class _Account extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Account";

  // Attribute Keys
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);
  public static final ERXKey<String> NUMBER = new ERXKey<String>("number", Type.Attribute);
  public static final ERXKey<java.time.LocalDate> OPENED_ON = new ERXKey<java.time.LocalDate>("openedOn", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<com.example.ledger.Posting> POSTINGS = new ERXKey<com.example.ledger.Posting>("postings", Type.ToManyRelationship);

  // Attributes
  public static final String NAME_KEY = NAME.key();
  public static final String NUMBER_KEY = NUMBER.key();
  public static final String OPENED_ON_KEY = OPENED_ON.key();

  // Relationships
  public static final String POSTINGS_KEY = POSTINGS.key();

  private static final Logger log = LoggerFactory.getLogger(_Account.class);

  public Account localInstanceIn(EOEditingContext editingContext) {
    Account localInstance = (Account)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String name() {
    return (String) storedValueForKey(_Account.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _Account.NAME_KEY);
  }

  public String number() {
    return (String) storedValueForKey(_Account.NUMBER_KEY);
  }

  public void setNumber(String value) {
    log.debug( "updating number from {} to {}", number(), value);
    takeStoredValueForKey(value, _Account.NUMBER_KEY);
  }

  public java.time.LocalDate openedOn() {
    return (java.time.LocalDate) storedValueForKey(_Account.OPENED_ON_KEY);
  }

  public void setOpenedOn(java.time.LocalDate value) {
    log.debug( "updating openedOn from {} to {}", openedOn(), value);
    takeStoredValueForKey(value, _Account.OPENED_ON_KEY);
  }

  public NSArray<com.example.ledger.Posting> postings() {
    return (NSArray<com.example.ledger.Posting>)storedValueForKey(_Account.POSTINGS_KEY);
  }

  public NSArray<com.example.ledger.Posting> postings(EOQualifier qualifier) {
    return postings(qualifier, null, false);
  }

  public NSArray<com.example.ledger.Posting> postings(EOQualifier qualifier, boolean fetch) {
    return postings(qualifier, null, fetch);
  }

  public NSArray<com.example.ledger.Posting> postings(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<com.example.ledger.Posting> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(com.example.ledger.Posting.ACCOUNT_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = com.example.ledger.Posting.fetchPostings(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = postings();
      if (qualifier != null) {
        results = (NSArray<com.example.ledger.Posting>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<com.example.ledger.Posting>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToPostings(com.example.ledger.Posting object) {
    includeObjectIntoPropertyWithKey(object, _Account.POSTINGS_KEY);
  }

  public void removeFromPostings(com.example.ledger.Posting object) {
    excludeObjectFromPropertyWithKey(object, _Account.POSTINGS_KEY);
  }

  public void addToPostingsRelationship(com.example.ledger.Posting object) {
    log.debug("adding {} to postings relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToPostings(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Account.POSTINGS_KEY);
    }
  }

  public void removeFromPostingsRelationship(com.example.ledger.Posting object) {
    log.debug("removing {} from postings relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromPostings(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Account.POSTINGS_KEY);
    }
  }

  public com.example.ledger.Posting createPostingsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  com.example.ledger.Posting.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Account.POSTINGS_KEY);
    return (com.example.ledger.Posting) eo;
  }

  public void deletePostingsRelationship(com.example.ledger.Posting object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Account.POSTINGS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllPostingsRelationships() {
    Enumeration<com.example.ledger.Posting> objects = postings().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deletePostingsRelationship(objects.nextElement());
    }
  }


  public static Account createAccount(EOEditingContext editingContext, String name
, String number
) {
    Account eo = (Account) EOUtilities.createAndInsertInstance(editingContext, _Account.ENTITY_NAME);
    eo.setName(name);
    eo.setNumber(number);
    return eo;
  }

  public static ERXFetchSpecification<Account> fetchSpec() {
    return new ERXFetchSpecification<Account>(_Account.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Account> fetchAllAccounts(EOEditingContext editingContext) {
    return _Account.fetchAllAccounts(editingContext, null);
  }

  public static NSArray<Account> fetchAllAccounts(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Account.fetchAccounts(editingContext, null, sortOrderings);
  }

  public static NSArray<Account> fetchAccounts(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Account> fetchSpec = new ERXFetchSpecification<Account>(_Account.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Account> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Account fetchAccount(EOEditingContext editingContext, String keyName, Object value) {
    return _Account.fetchAccount(editingContext, ERXQ.equals(keyName, value));
  }

  public static Account fetchAccount(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Account> eoObjects = _Account.fetchAccounts(editingContext, qualifier, null);
    Account eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Account that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Account fetchRequiredAccount(EOEditingContext editingContext, String keyName, Object value) {
    return _Account.fetchRequiredAccount(editingContext, ERXQ.equals(keyName, value));
  }

  public static Account fetchRequiredAccount(EOEditingContext editingContext, EOQualifier qualifier) {
    Account eoObject = _Account.fetchAccount(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Account that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Account localInstanceIn(EOEditingContext editingContext, Account eo) {
    Account localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

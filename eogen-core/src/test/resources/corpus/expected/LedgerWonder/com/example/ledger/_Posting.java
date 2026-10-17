// DO NOT EDIT.  Make changes to Posting.java instead.
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
public abstract class _Posting extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Posting";

  // Attribute Keys
  public static final ERXKey<java.math.BigDecimal> AMOUNT = new ERXKey<java.math.BigDecimal>("amount", Type.Attribute);
  public static final ERXKey<NSTimestamp> POSTED_AT = new ERXKey<NSTimestamp>("postedAt", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<com.example.ledger.Account> ACCOUNT = new ERXKey<com.example.ledger.Account>("account", Type.ToOneRelationship);

  // Attributes
  public static final String AMOUNT_KEY = AMOUNT.key();
  public static final String POSTED_AT_KEY = POSTED_AT.key();

  // Relationships
  public static final String ACCOUNT_KEY = ACCOUNT.key();

  private static final Logger log = LoggerFactory.getLogger(_Posting.class);

  public Posting localInstanceIn(EOEditingContext editingContext) {
    Posting localInstance = (Posting)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public java.math.BigDecimal amount() {
    return (java.math.BigDecimal) storedValueForKey(_Posting.AMOUNT_KEY);
  }

  public void setAmount(java.math.BigDecimal value) {
    log.debug( "updating amount from {} to {}", amount(), value);
    takeStoredValueForKey(value, _Posting.AMOUNT_KEY);
  }

  public NSTimestamp postedAt() {
    return (NSTimestamp) storedValueForKey(_Posting.POSTED_AT_KEY);
  }

  public void setPostedAt(NSTimestamp value) {
    log.debug( "updating postedAt from {} to {}", postedAt(), value);
    takeStoredValueForKey(value, _Posting.POSTED_AT_KEY);
  }

  public com.example.ledger.Account account() {
    return (com.example.ledger.Account)storedValueForKey(_Posting.ACCOUNT_KEY);
  }

  public void setAccount(com.example.ledger.Account value) {
    takeStoredValueForKey(value, _Posting.ACCOUNT_KEY);
  }

  public void setAccountRelationship(com.example.ledger.Account value) {
    log.debug("updating account from {} to {}", account(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setAccount(value);
    }
    else if (value == null) {
      com.example.ledger.Account oldValue = account();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Posting.ACCOUNT_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Posting.ACCOUNT_KEY);
    }
  }


  public static Posting createPosting(EOEditingContext editingContext, java.math.BigDecimal amount
, NSTimestamp postedAt
, com.example.ledger.Account account) {
    Posting eo = (Posting) EOUtilities.createAndInsertInstance(editingContext, _Posting.ENTITY_NAME);
    eo.setAmount(amount);
    eo.setPostedAt(postedAt);
    eo.setAccountRelationship(account);
    return eo;
  }

  public static ERXFetchSpecification<Posting> fetchSpec() {
    return new ERXFetchSpecification<Posting>(_Posting.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Posting> fetchAllPostings(EOEditingContext editingContext) {
    return _Posting.fetchAllPostings(editingContext, null);
  }

  public static NSArray<Posting> fetchAllPostings(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Posting.fetchPostings(editingContext, null, sortOrderings);
  }

  public static NSArray<Posting> fetchPostings(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Posting> fetchSpec = new ERXFetchSpecification<Posting>(_Posting.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Posting> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Posting fetchPosting(EOEditingContext editingContext, String keyName, Object value) {
    return _Posting.fetchPosting(editingContext, ERXQ.equals(keyName, value));
  }

  public static Posting fetchPosting(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Posting> eoObjects = _Posting.fetchPostings(editingContext, qualifier, null);
    Posting eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Posting that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Posting fetchRequiredPosting(EOEditingContext editingContext, String keyName, Object value) {
    return _Posting.fetchRequiredPosting(editingContext, ERXQ.equals(keyName, value));
  }

  public static Posting fetchRequiredPosting(EOEditingContext editingContext, EOQualifier qualifier) {
    Posting eoObject = _Posting.fetchPosting(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Posting that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Posting localInstanceIn(EOEditingContext editingContext, Posting eo) {
    Posting localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

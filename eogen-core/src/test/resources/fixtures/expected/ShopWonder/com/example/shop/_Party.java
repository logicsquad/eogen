// DO NOT EDIT.  Make changes to Party.java instead.
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
public abstract class _Party extends com.example.shop.ShopRecord {
  public static final String ENTITY_NAME = "Party";

  // Attribute Keys
  public static final ERXKey<String> EMAIL = new ERXKey<String>("email", Type.Attribute);
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);
  public static final ERXKey<com.example.shop.Status> STATUS = new ERXKey<com.example.shop.Status>("status", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<com.example.shop.Order> ORDERS = new ERXKey<com.example.shop.Order>("orders", Type.ToManyRelationship);

  // Attributes
  public static final String EMAIL_KEY = EMAIL.key();
  public static final String NAME_KEY = NAME.key();
  public static final String STATUS_KEY = STATUS.key();

  // Relationships
  public static final String ORDERS_KEY = ORDERS.key();

  private static final Logger log = LoggerFactory.getLogger(_Party.class);

  public Party localInstanceIn(EOEditingContext editingContext) {
    Party localInstance = (Party)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String email() {
    return (String) storedValueForKey(_Party.EMAIL_KEY);
  }

  public void setEmail(String value) {
    log.debug( "updating email from {} to {}", email(), value);
    takeStoredValueForKey(value, _Party.EMAIL_KEY);
  }

  public String name() {
    return (String) storedValueForKey(_Party.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _Party.NAME_KEY);
  }

  public com.example.shop.Status status() {
    return (com.example.shop.Status) storedValueForKey(_Party.STATUS_KEY);
  }

  public void setStatus(com.example.shop.Status value) {
    log.debug( "updating status from {} to {}", status(), value);
    takeStoredValueForKey(value, _Party.STATUS_KEY);
  }

  public NSArray<com.example.shop.Order> orders() {
    return (NSArray<com.example.shop.Order>)storedValueForKey(_Party.ORDERS_KEY);
  }

  public NSArray<com.example.shop.Order> orders(EOQualifier qualifier) {
    return orders(qualifier, null, false);
  }

  public NSArray<com.example.shop.Order> orders(EOQualifier qualifier, boolean fetch) {
    return orders(qualifier, null, fetch);
  }

  public NSArray<com.example.shop.Order> orders(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<com.example.shop.Order> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(com.example.shop.Order.CUSTOMER_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = com.example.shop.Order.fetchOrders(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = orders();
      if (qualifier != null) {
        results = (NSArray<com.example.shop.Order>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<com.example.shop.Order>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToOrders(com.example.shop.Order object) {
    includeObjectIntoPropertyWithKey(object, _Party.ORDERS_KEY);
  }

  public void removeFromOrders(com.example.shop.Order object) {
    excludeObjectFromPropertyWithKey(object, _Party.ORDERS_KEY);
  }

  public void addToOrdersRelationship(com.example.shop.Order object) {
    log.debug("adding {} to orders relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToOrders(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Party.ORDERS_KEY);
    }
  }

  public void removeFromOrdersRelationship(com.example.shop.Order object) {
    log.debug("removing {} from orders relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromOrders(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Party.ORDERS_KEY);
    }
  }

  public com.example.shop.Order createOrdersRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  com.example.shop.Order.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Party.ORDERS_KEY);
    return (com.example.shop.Order) eo;
  }

  public void deleteOrdersRelationship(com.example.shop.Order object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Party.ORDERS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllOrdersRelationships() {
    Enumeration<com.example.shop.Order> objects = orders().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteOrdersRelationship(objects.nextElement());
    }
  }


  public static Party createParty(EOEditingContext editingContext, String email
, String name
, com.example.shop.Status status
) {
    Party eo = (Party) EOUtilities.createAndInsertInstance(editingContext, _Party.ENTITY_NAME);
    eo.setEmail(email);
    eo.setName(name);
    eo.setStatus(status);
    return eo;
  }

  public static ERXFetchSpecification<Party> fetchSpec() {
    return new ERXFetchSpecification<Party>(_Party.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Party> fetchAllParties(EOEditingContext editingContext) {
    return _Party.fetchAllParties(editingContext, null);
  }

  public static NSArray<Party> fetchAllParties(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Party.fetchParties(editingContext, null, sortOrderings);
  }

  public static NSArray<Party> fetchParties(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Party> fetchSpec = new ERXFetchSpecification<Party>(_Party.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Party> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Party fetchParty(EOEditingContext editingContext, String keyName, Object value) {
    return _Party.fetchParty(editingContext, ERXQ.equals(keyName, value));
  }

  public static Party fetchParty(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Party> eoObjects = _Party.fetchParties(editingContext, qualifier, null);
    Party eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Party that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Party fetchRequiredParty(EOEditingContext editingContext, String keyName, Object value) {
    return _Party.fetchRequiredParty(editingContext, ERXQ.equals(keyName, value));
  }

  public static Party fetchRequiredParty(EOEditingContext editingContext, EOQualifier qualifier) {
    Party eoObject = _Party.fetchParty(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Party that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Party localInstanceIn(EOEditingContext editingContext, Party eo) {
    Party localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

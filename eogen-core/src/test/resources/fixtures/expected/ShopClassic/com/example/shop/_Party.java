// $LastChangedRevision$ DO NOT EDIT.  Make changes to Party.java instead.
package com.example.shop;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Party extends  EOGenericRecord {
	public static final String ENTITY_NAME = "Party";

	// Attributes
	public static final String EMAIL_KEY = "email";
	public static final String NAME_KEY = "name";
	public static final String STATUS_KEY = "status";

	// Relationships
	public static final String ORDERS_KEY = "orders";

  private static Logger LOG = Logger.getLogger(_Party.class);

  public Party localInstanceIn(EOEditingContext editingContext) {
    Party localInstance = (Party)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String email() {
    return (String) storedValueForKey("email");
  }

  public void setEmail(String value) {
    if (_Party.LOG.isDebugEnabled()) {
    	_Party.LOG.debug( "updating email from " + email() + " to " + value);
    }
    takeStoredValueForKey(value, "email");
  }

  public String name() {
    return (String) storedValueForKey("name");
  }

  public void setName(String value) {
    if (_Party.LOG.isDebugEnabled()) {
    	_Party.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, "name");
  }

  public com.example.shop.Status status() {
    return (com.example.shop.Status) storedValueForKey("status");
  }

  public void setStatus(com.example.shop.Status value) {
    if (_Party.LOG.isDebugEnabled()) {
    	_Party.LOG.debug( "updating status from " + status() + " to " + value);
    }
    takeStoredValueForKey(value, "status");
  }

  public NSArray<com.example.shop.Order> orders() {
    return (NSArray<com.example.shop.Order>)storedValueForKey("orders");
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
      EOQualifier inverseQualifier = new EOKeyValueQualifier(com.example.shop.Order.CUSTOMER_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        NSMutableArray qualifiers = new NSMutableArray();
        qualifiers.addObject(qualifier);
        qualifiers.addObject(inverseQualifier);
        fullQualifier = new EOAndQualifier(qualifiers);
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
  
  public void addToOrdersRelationship(com.example.shop.Order object) {
    if (_Party.LOG.isDebugEnabled()) {
      _Party.LOG.debug("adding " + object + " to orders relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "orders");
  }

  public void removeFromOrdersRelationship(com.example.shop.Order object) {
    if (_Party.LOG.isDebugEnabled()) {
      _Party.LOG.debug("removing " + object + " from orders relationship");
    }
    removeObjectFromBothSidesOfRelationshipWithKey(object, "orders");
  }

  public com.example.shop.Order createOrdersRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Order");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "orders");
    return (com.example.shop.Order) eo;
  }

  public void deleteOrdersRelationship(com.example.shop.Order object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "orders");
    editingContext().deleteObject(object);
  }

  public void deleteAllOrdersRelationships() {
    Enumeration objects = orders().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteOrdersRelationship((com.example.shop.Order)objects.nextElement());
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

  public static NSArray<Party> fetchAllParties(EOEditingContext editingContext) {
    return _Party.fetchAllParties(editingContext, null);
  }

  public static NSArray<Party> fetchAllParties(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Party.fetchParties(editingContext, null, sortOrderings);
  }

  public static NSArray<Party> fetchParties(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Party.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Party> eoObjects = (NSArray<Party>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static Party fetchParty(EOEditingContext editingContext, String keyName, Object value) {
    return _Party.fetchParty(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Party fetchParty(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Party> eoObjects = _Party.fetchParties(editingContext, qualifier, null);
    Party eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (Party)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Party that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Party fetchRequiredParty(EOEditingContext editingContext, String keyName, Object value) {
    return _Party.fetchRequiredParty(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Party fetchRequiredParty(EOEditingContext editingContext, EOQualifier qualifier) {
    Party eoObject = _Party.fetchParty(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Party that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Party localInstanceIn(EOEditingContext editingContext, Party eo) {
    Party localInstance = (eo == null) ? null : (Party)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

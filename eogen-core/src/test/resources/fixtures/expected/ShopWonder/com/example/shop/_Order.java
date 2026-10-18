// DO NOT EDIT.  Make changes to Order.java instead.
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
public abstract class _Order extends com.example.shop.ShopRecord {
  public static final String ENTITY_NAME = "Order";

  // Attribute Keys
  public static final ERXKey<$attribute.javaClassName> CUSTOMER_NAME = new ERXKey<$attribute.javaClassName>("customerName", Type.Attribute);
  public static final ERXKey<String> NOTE = new ERXKey<String>("note", Type.Attribute);
  public static final ERXKey<NSTimestamp> PLACED = new ERXKey<NSTimestamp>("placed", Type.Attribute);
  public static final ERXKey<java.math.BigDecimal> TOTAL = new ERXKey<java.math.BigDecimal>("total", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<com.example.reference.Currency> CURRENCY = new ERXKey<com.example.reference.Currency>("currency", Type.ToOneRelationship);
  public static final ERXKey<com.example.shop.Party> CUSTOMER = new ERXKey<com.example.shop.Party>("customer", Type.ToOneRelationship);
  public static final ERXKey<com.example.shop.OrderLine> LINES = new ERXKey<com.example.shop.OrderLine>("lines", Type.ToManyRelationship);

  // Attributes
  public static final String CUSTOMER_NAME_KEY = CUSTOMER_NAME.key();
  public static final String NOTE_KEY = NOTE.key();
  public static final String PLACED_KEY = PLACED.key();
  public static final String TOTAL_KEY = TOTAL.key();

  // Relationships
  public static final String CURRENCY_KEY = CURRENCY.key();
  public static final String CUSTOMER_KEY = CUSTOMER.key();
  public static final String LINES_KEY = LINES.key();

  private static final Logger log = LoggerFactory.getLogger(_Order.class);

  public Order localInstanceIn(EOEditingContext editingContext) {
    Order localInstance = (Order)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public $attribute.javaClassName customerName() {
    return ($attribute.javaClassName) storedValueForKey(_Order.CUSTOMER_NAME_KEY);
  }

  public void setCustomerName($attribute.javaClassName value) {
    log.debug( "updating customerName from {} to {}", customerName(), value);
    takeStoredValueForKey(value, _Order.CUSTOMER_NAME_KEY);
  }

  public String note() {
    return (String) storedValueForKey(_Order.NOTE_KEY);
  }

  public void setNote(String value) {
    log.debug( "updating note from {} to {}", note(), value);
    takeStoredValueForKey(value, _Order.NOTE_KEY);
  }

  /**
   * When the order was placed.
   */
  public NSTimestamp placed() {
    return (NSTimestamp) storedValueForKey(_Order.PLACED_KEY);
  }

  public void setPlaced(NSTimestamp value) {
    log.debug( "updating placed from {} to {}", placed(), value);
    takeStoredValueForKey(value, _Order.PLACED_KEY);
  }

  public java.math.BigDecimal total() {
    return (java.math.BigDecimal) storedValueForKey(_Order.TOTAL_KEY);
  }

  public void setTotal(java.math.BigDecimal value) {
    log.debug( "updating total from {} to {}", total(), value);
    takeStoredValueForKey(value, _Order.TOTAL_KEY);
  }

  public com.example.reference.Currency currency() {
    return (com.example.reference.Currency)storedValueForKey(_Order.CURRENCY_KEY);
  }

  public void setCurrency(com.example.reference.Currency value) {
    takeStoredValueForKey(value, _Order.CURRENCY_KEY);
  }

  public void setCurrencyRelationship(com.example.reference.Currency value) {
    log.debug("updating currency from {} to {}", currency(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setCurrency(value);
    }
    else if (value == null) {
      com.example.reference.Currency oldValue = currency();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Order.CURRENCY_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Order.CURRENCY_KEY);
    }
  }

  /**
   * The <code>Party</code> who placed the order.
   */
  public com.example.shop.Party customer() {
    return (com.example.shop.Party)storedValueForKey(_Order.CUSTOMER_KEY);
  }

  public void setCustomer(com.example.shop.Party value) {
    takeStoredValueForKey(value, _Order.CUSTOMER_KEY);
  }

  public void setCustomerRelationship(com.example.shop.Party value) {
    log.debug("updating customer from {} to {}", customer(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setCustomer(value);
    }
    else if (value == null) {
      com.example.shop.Party oldValue = customer();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Order.CUSTOMER_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Order.CUSTOMER_KEY);
    }
  }

  /**
   * What was ordered, one line for each <code>Product</code>.
   *
   * Deleting an order deletes its lines.
   */
  public NSArray<com.example.shop.OrderLine> lines() {
    return (NSArray<com.example.shop.OrderLine>)storedValueForKey(_Order.LINES_KEY);
  }

  public NSArray<com.example.shop.OrderLine> lines(EOQualifier qualifier) {
    return lines(qualifier, null, false);
  }

  public NSArray<com.example.shop.OrderLine> lines(EOQualifier qualifier, boolean fetch) {
    return lines(qualifier, null, fetch);
  }

  public NSArray<com.example.shop.OrderLine> lines(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<com.example.shop.OrderLine> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(com.example.shop.OrderLine.ORDER_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = com.example.shop.OrderLine.fetchOrderLines(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = lines();
      if (qualifier != null) {
        results = (NSArray<com.example.shop.OrderLine>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<com.example.shop.OrderLine>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToLines(com.example.shop.OrderLine object) {
    includeObjectIntoPropertyWithKey(object, _Order.LINES_KEY);
  }

  public void removeFromLines(com.example.shop.OrderLine object) {
    excludeObjectFromPropertyWithKey(object, _Order.LINES_KEY);
  }

  public void addToLinesRelationship(com.example.shop.OrderLine object) {
    log.debug("adding {} to lines relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToLines(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Order.LINES_KEY);
    }
  }

  public void removeFromLinesRelationship(com.example.shop.OrderLine object) {
    log.debug("removing {} from lines relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromLines(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Order.LINES_KEY);
    }
  }

  public com.example.shop.OrderLine createLinesRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  com.example.shop.OrderLine.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Order.LINES_KEY);
    return (com.example.shop.OrderLine) eo;
  }

  public void deleteLinesRelationship(com.example.shop.OrderLine object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Order.LINES_KEY);
  }

  public void deleteAllLinesRelationships() {
    Enumeration<com.example.shop.OrderLine> objects = lines().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteLinesRelationship(objects.nextElement());
    }
  }


  public static Order createOrder(EOEditingContext editingContext, ${attribute.javaClassName} customerName
, String note
, NSTimestamp placed
, java.math.BigDecimal total
, com.example.reference.Currency currency, com.example.shop.Party customer) {
    Order eo = (Order) EOUtilities.createAndInsertInstance(editingContext, _Order.ENTITY_NAME);
    eo.setCustomerName(customerName);
    eo.setNote(note);
    eo.setPlaced(placed);
    eo.setTotal(total);
    eo.setCurrencyRelationship(currency);
    eo.setCustomerRelationship(customer);
    return eo;
  }

  public static ERXFetchSpecification<Order> fetchSpec() {
    return new ERXFetchSpecification<Order>(_Order.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Order> fetchAllOrders(EOEditingContext editingContext) {
    return _Order.fetchAllOrders(editingContext, null);
  }

  public static NSArray<Order> fetchAllOrders(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Order.fetchOrders(editingContext, null, sortOrderings);
  }

  public static NSArray<Order> fetchOrders(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Order> fetchSpec = new ERXFetchSpecification<Order>(_Order.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Order> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Order fetchOrder(EOEditingContext editingContext, String keyName, Object value) {
    return _Order.fetchOrder(editingContext, ERXQ.equals(keyName, value));
  }

  public static Order fetchOrder(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Order> eoObjects = _Order.fetchOrders(editingContext, qualifier, null);
    Order eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Order that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Order fetchRequiredOrder(EOEditingContext editingContext, String keyName, Object value) {
    return _Order.fetchRequiredOrder(editingContext, ERXQ.equals(keyName, value));
  }

  public static Order fetchRequiredOrder(EOEditingContext editingContext, EOQualifier qualifier) {
    Order eoObject = _Order.fetchOrder(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Order that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Order localInstanceIn(EOEditingContext editingContext, Order eo) {
    Order localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
  public static NSArray<com.example.shop.Order> fetchByCustomerName(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("byCustomerName", _Order.ENTITY_NAME);
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<com.example.shop.Order>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }

  public static NSArray<com.example.shop.Order> fetchByCustomerName(EOEditingContext editingContext,
  java.math.BigDecimal minimumBinding,
  String nameBinding)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("byCustomerName", _Order.ENTITY_NAME);
    NSMutableDictionary<String, Object> bindings = new NSMutableDictionary<String, Object>();
    bindings.takeValueForKey(minimumBinding, "minimum");
    bindings.takeValueForKey(nameBinding, "name");
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<com.example.shop.Order>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }

  public static NSArray<com.example.shop.Order> fetchRecent(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("recent", _Order.ENTITY_NAME);
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<com.example.shop.Order>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }

  public static NSArray<com.example.shop.Order> fetchRecent(EOEditingContext editingContext)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("recent", _Order.ENTITY_NAME);
    return (NSArray<com.example.shop.Order>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }

}

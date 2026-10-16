// $LastChangedRevision$ DO NOT EDIT.  Make changes to Order.java instead.
package com.example.shop;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Order extends  EOGenericRecord {
	public static final String ENTITY_NAME = "Order";

	// Attributes
	public static final String CUSTOMER_NAME_KEY = "customerName";
	public static final String NOTE_KEY = "note";
	public static final String PLACED_KEY = "placed";
	public static final String TOTAL_KEY = "total";

	// Relationships
	public static final String CURRENCY_KEY = "currency";
	public static final String CUSTOMER_KEY = "customer";
	public static final String LINES_KEY = "lines";

  private static Logger LOG = Logger.getLogger(_Order.class);

  public Order localInstanceIn(EOEditingContext editingContext) {
    Order localInstance = (Order)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public $attribute.javaClassName customerName() {
    return ($attribute.javaClassName) storedValueForKey("customerName");
  }

  public void setCustomerName($attribute.javaClassName value) {
    if (_Order.LOG.isDebugEnabled()) {
    	_Order.LOG.debug( "updating customerName from " + customerName() + " to " + value);
    }
    takeStoredValueForKey(value, "customerName");
  }

  public String note() {
    return (String) storedValueForKey("note");
  }

  public void setNote(String value) {
    if (_Order.LOG.isDebugEnabled()) {
    	_Order.LOG.debug( "updating note from " + note() + " to " + value);
    }
    takeStoredValueForKey(value, "note");
  }

  public NSTimestamp placed() {
    return (NSTimestamp) storedValueForKey("placed");
  }

  public void setPlaced(NSTimestamp value) {
    if (_Order.LOG.isDebugEnabled()) {
    	_Order.LOG.debug( "updating placed from " + placed() + " to " + value);
    }
    takeStoredValueForKey(value, "placed");
  }

  public java.math.BigDecimal total() {
    return (java.math.BigDecimal) storedValueForKey("total");
  }

  public void setTotal(java.math.BigDecimal value) {
    if (_Order.LOG.isDebugEnabled()) {
    	_Order.LOG.debug( "updating total from " + total() + " to " + value);
    }
    takeStoredValueForKey(value, "total");
  }

  public com.example.reference.Currency currency() {
    return (com.example.reference.Currency)storedValueForKey("currency");
  }

  public void setCurrencyRelationship(com.example.reference.Currency value) {
    if (_Order.LOG.isDebugEnabled()) {
      _Order.LOG.debug("updating currency from " + currency() + " to " + value);
    }
    if (value == null) {
    	com.example.reference.Currency oldValue = currency();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "currency");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "currency");
    }
  }
  
  public com.example.shop.Party customer() {
    return (com.example.shop.Party)storedValueForKey("customer");
  }

  public void setCustomerRelationship(com.example.shop.Party value) {
    if (_Order.LOG.isDebugEnabled()) {
      _Order.LOG.debug("updating customer from " + customer() + " to " + value);
    }
    if (value == null) {
    	com.example.shop.Party oldValue = customer();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "customer");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "customer");
    }
  }
  
  public NSArray<com.example.shop.OrderLine> lines() {
    return (NSArray<com.example.shop.OrderLine>)storedValueForKey("lines");
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
      EOQualifier inverseQualifier = new EOKeyValueQualifier(com.example.shop.OrderLine.ORDER_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        NSMutableArray qualifiers = new NSMutableArray();
        qualifiers.addObject(qualifier);
        qualifiers.addObject(inverseQualifier);
        fullQualifier = new EOAndQualifier(qualifiers);
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
  
  public void addToLinesRelationship(com.example.shop.OrderLine object) {
    if (_Order.LOG.isDebugEnabled()) {
      _Order.LOG.debug("adding " + object + " to lines relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "lines");
  }

  public void removeFromLinesRelationship(com.example.shop.OrderLine object) {
    if (_Order.LOG.isDebugEnabled()) {
      _Order.LOG.debug("removing " + object + " from lines relationship");
    }
    removeObjectFromBothSidesOfRelationshipWithKey(object, "lines");
  }

  public com.example.shop.OrderLine createLinesRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("OrderLine");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "lines");
    return (com.example.shop.OrderLine) eo;
  }

  public void deleteLinesRelationship(com.example.shop.OrderLine object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "lines");
  }

  public void deleteAllLinesRelationships() {
    Enumeration objects = lines().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteLinesRelationship((com.example.shop.OrderLine)objects.nextElement());
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

  public static NSArray<Order> fetchAllOrders(EOEditingContext editingContext) {
    return _Order.fetchAllOrders(editingContext, null);
  }

  public static NSArray<Order> fetchAllOrders(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Order.fetchOrders(editingContext, null, sortOrderings);
  }

  public static NSArray<Order> fetchOrders(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Order.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Order> eoObjects = (NSArray<Order>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static Order fetchOrder(EOEditingContext editingContext, String keyName, Object value) {
    return _Order.fetchOrder(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Order fetchOrder(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Order> eoObjects = _Order.fetchOrders(editingContext, qualifier, null);
    Order eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (Order)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Order that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Order fetchRequiredOrder(EOEditingContext editingContext, String keyName, Object value) {
    return _Order.fetchRequiredOrder(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Order fetchRequiredOrder(EOEditingContext editingContext, EOQualifier qualifier) {
    Order eoObject = _Order.fetchOrder(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Order that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Order localInstanceIn(EOEditingContext editingContext, Order eo) {
    Order localInstance = (eo == null) ? null : (Order)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
  public static NSArray<com.example.shop.Order> fetchByCustomerName(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("byCustomerName", "Order");
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<com.example.shop.Order>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<com.example.shop.Order> fetchByCustomerName(EOEditingContext editingContext,
	java.math.BigDecimal minimumBinding,
	String nameBinding)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("byCustomerName", "Order");
    NSMutableDictionary<String, Object> bindings = new NSMutableDictionary<String, Object>();
    bindings.takeValueForKey(minimumBinding, "minimum");
    bindings.takeValueForKey(nameBinding, "name");
	fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<com.example.shop.Order>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<com.example.shop.Order> fetchRecent(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("recent", "Order");
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<com.example.shop.Order>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<com.example.shop.Order> fetchRecent(EOEditingContext editingContext)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("recent", "Order");
    return (NSArray<com.example.shop.Order>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
}

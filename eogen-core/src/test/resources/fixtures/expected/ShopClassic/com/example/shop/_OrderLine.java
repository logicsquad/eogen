// $LastChangedRevision$ DO NOT EDIT.  Make changes to OrderLine.java instead.
package com.example.shop;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _OrderLine extends  EOGenericRecord {
	public static final String ENTITY_NAME = "OrderLine";

	// Attributes
	public static final String PRICE_KEY = "price";
	public static final String QUANTITY_KEY = "quantity";

	// Relationships
	public static final String ORDER_KEY = "order";
	public static final String PRODUCT_KEY = "product";

  private static Logger LOG = Logger.getLogger(_OrderLine.class);

  public OrderLine localInstanceIn(EOEditingContext editingContext) {
    OrderLine localInstance = (OrderLine)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public java.math.BigDecimal price() {
    return (java.math.BigDecimal) storedValueForKey("price");
  }

  public void setPrice(java.math.BigDecimal value) {
    if (_OrderLine.LOG.isDebugEnabled()) {
    	_OrderLine.LOG.debug( "updating price from " + price() + " to " + value);
    }
    takeStoredValueForKey(value, "price");
  }

  public Integer quantity() {
    return (Integer) storedValueForKey("quantity");
  }

  public void setQuantity(Integer value) {
    if (_OrderLine.LOG.isDebugEnabled()) {
    	_OrderLine.LOG.debug( "updating quantity from " + quantity() + " to " + value);
    }
    takeStoredValueForKey(value, "quantity");
  }

  public com.example.shop.Order order() {
    return (com.example.shop.Order)storedValueForKey("order");
  }

  public void setOrderRelationship(com.example.shop.Order value) {
    if (_OrderLine.LOG.isDebugEnabled()) {
      _OrderLine.LOG.debug("updating order from " + order() + " to " + value);
    }
    if (value == null) {
    	com.example.shop.Order oldValue = order();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "order");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "order");
    }
  }
  
  public com.example.shop.Product product() {
    return (com.example.shop.Product)storedValueForKey("product");
  }

  public void setProductRelationship(com.example.shop.Product value) {
    if (_OrderLine.LOG.isDebugEnabled()) {
      _OrderLine.LOG.debug("updating product from " + product() + " to " + value);
    }
    if (value == null) {
    	com.example.shop.Product oldValue = product();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "product");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "product");
    }
  }
  

  public static OrderLine createOrderLine(EOEditingContext editingContext, java.math.BigDecimal price
, Integer quantity
, com.example.shop.Order order, com.example.shop.Product product) {
    OrderLine eo = (OrderLine) EOUtilities.createAndInsertInstance(editingContext, _OrderLine.ENTITY_NAME);    
		eo.setPrice(price);
		eo.setQuantity(quantity);
    eo.setOrderRelationship(order);
    eo.setProductRelationship(product);
    return eo;
  }

  public static NSArray<OrderLine> fetchAllOrderLines(EOEditingContext editingContext) {
    return _OrderLine.fetchAllOrderLines(editingContext, null);
  }

  public static NSArray<OrderLine> fetchAllOrderLines(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _OrderLine.fetchOrderLines(editingContext, null, sortOrderings);
  }

  public static NSArray<OrderLine> fetchOrderLines(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_OrderLine.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<OrderLine> eoObjects = (NSArray<OrderLine>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static OrderLine fetchOrderLine(EOEditingContext editingContext, String keyName, Object value) {
    return _OrderLine.fetchOrderLine(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static OrderLine fetchOrderLine(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<OrderLine> eoObjects = _OrderLine.fetchOrderLines(editingContext, qualifier, null);
    OrderLine eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (OrderLine)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one OrderLine that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static OrderLine fetchRequiredOrderLine(EOEditingContext editingContext, String keyName, Object value) {
    return _OrderLine.fetchRequiredOrderLine(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static OrderLine fetchRequiredOrderLine(EOEditingContext editingContext, EOQualifier qualifier) {
    OrderLine eoObject = _OrderLine.fetchOrderLine(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no OrderLine that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static OrderLine localInstanceIn(EOEditingContext editingContext, OrderLine eo) {
    OrderLine localInstance = (eo == null) ? null : (OrderLine)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

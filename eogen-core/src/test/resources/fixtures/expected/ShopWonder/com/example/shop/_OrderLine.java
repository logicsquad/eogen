// DO NOT EDIT.  Make changes to OrderLine.java instead.
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
public abstract class _OrderLine extends com.example.shop.ShopRecord {
  public static final String ENTITY_NAME = "OrderLine";

  // Attribute Keys
  public static final ERXKey<java.math.BigDecimal> PRICE = new ERXKey<java.math.BigDecimal>("price", Type.Attribute);
  public static final ERXKey<Integer> QUANTITY = new ERXKey<Integer>("quantity", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<com.example.shop.Order> ORDER = new ERXKey<com.example.shop.Order>("order", Type.ToOneRelationship);
  public static final ERXKey<com.example.shop.Product> PRODUCT = new ERXKey<com.example.shop.Product>("product", Type.ToOneRelationship);

  // Attributes
  public static final String PRICE_KEY = PRICE.key();
  public static final String QUANTITY_KEY = QUANTITY.key();

  // Relationships
  public static final String ORDER_KEY = ORDER.key();
  public static final String PRODUCT_KEY = PRODUCT.key();

  private static final Logger log = LoggerFactory.getLogger(_OrderLine.class);

  public OrderLine localInstanceIn(EOEditingContext editingContext) {
    OrderLine localInstance = (OrderLine)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public java.math.BigDecimal price() {
    return (java.math.BigDecimal) storedValueForKey(_OrderLine.PRICE_KEY);
  }

  public void setPrice(java.math.BigDecimal value) {
    log.debug( "updating price from {} to {}", price(), value);
    takeStoredValueForKey(value, _OrderLine.PRICE_KEY);
  }

  public Integer quantity() {
    return (Integer) storedValueForKey(_OrderLine.QUANTITY_KEY);
  }

  public void setQuantity(Integer value) {
    log.debug( "updating quantity from {} to {}", quantity(), value);
    takeStoredValueForKey(value, _OrderLine.QUANTITY_KEY);
  }

  public com.example.shop.Order order() {
    return (com.example.shop.Order)storedValueForKey(_OrderLine.ORDER_KEY);
  }

  public void setOrder(com.example.shop.Order value) {
    takeStoredValueForKey(value, _OrderLine.ORDER_KEY);
  }

  public void setOrderRelationship(com.example.shop.Order value) {
    log.debug("updating order from {} to {}", order(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setOrder(value);
    }
    else if (value == null) {
      com.example.shop.Order oldValue = order();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _OrderLine.ORDER_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _OrderLine.ORDER_KEY);
    }
  }

  public com.example.shop.Product product() {
    return (com.example.shop.Product)storedValueForKey(_OrderLine.PRODUCT_KEY);
  }

  public void setProduct(com.example.shop.Product value) {
    takeStoredValueForKey(value, _OrderLine.PRODUCT_KEY);
  }

  public void setProductRelationship(com.example.shop.Product value) {
    log.debug("updating product from {} to {}", product(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setProduct(value);
    }
    else if (value == null) {
      com.example.shop.Product oldValue = product();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _OrderLine.PRODUCT_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _OrderLine.PRODUCT_KEY);
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

  public static ERXFetchSpecification<OrderLine> fetchSpec() {
    return new ERXFetchSpecification<OrderLine>(_OrderLine.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<OrderLine> fetchAllOrderLines(EOEditingContext editingContext) {
    return _OrderLine.fetchAllOrderLines(editingContext, null);
  }

  public static NSArray<OrderLine> fetchAllOrderLines(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _OrderLine.fetchOrderLines(editingContext, null, sortOrderings);
  }

  public static NSArray<OrderLine> fetchOrderLines(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<OrderLine> fetchSpec = new ERXFetchSpecification<OrderLine>(_OrderLine.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<OrderLine> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static OrderLine fetchOrderLine(EOEditingContext editingContext, String keyName, Object value) {
    return _OrderLine.fetchOrderLine(editingContext, ERXQ.equals(keyName, value));
  }

  public static OrderLine fetchOrderLine(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<OrderLine> eoObjects = _OrderLine.fetchOrderLines(editingContext, qualifier, null);
    OrderLine eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one OrderLine that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static OrderLine fetchRequiredOrderLine(EOEditingContext editingContext, String keyName, Object value) {
    return _OrderLine.fetchRequiredOrderLine(editingContext, ERXQ.equals(keyName, value));
  }

  public static OrderLine fetchRequiredOrderLine(EOEditingContext editingContext, EOQualifier qualifier) {
    OrderLine eoObject = _OrderLine.fetchOrderLine(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no OrderLine that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static OrderLine localInstanceIn(EOEditingContext editingContext, OrderLine eo) {
    OrderLine localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

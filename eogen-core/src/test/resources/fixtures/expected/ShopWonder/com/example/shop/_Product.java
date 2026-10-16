// DO NOT EDIT.  Make changes to Product.java instead.
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
public abstract class _Product extends com.example.shop.ShopRecord {
  public static final String ENTITY_NAME = "Product";

  // Attribute Keys
  public static final ERXKey<Boolean> ACTIVE = new ERXKey<Boolean>("active", Type.Attribute);
  public static final ERXKey<Integer> CATEGORY = new ERXKey<Integer>("category", Type.Attribute);
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);
  public static final ERXKey<java.math.BigDecimal> PRICE = new ERXKey<java.math.BigDecimal>("price", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<com.example.shop.Tag> TAGS = new ERXKey<com.example.shop.Tag>("tags", Type.ToManyRelationship);

  // Attributes
  public static final String ACTIVE_KEY = ACTIVE.key();
  public static final String CATEGORY_KEY = CATEGORY.key();
  public static final String NAME_KEY = NAME.key();
  public static final String PRICE_KEY = PRICE.key();

  // Relationships
  public static final String TAGS_KEY = TAGS.key();

  private static final Logger log = LoggerFactory.getLogger(_Product.class);

  public Product localInstanceIn(EOEditingContext editingContext) {
    Product localInstance = (Product)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public Boolean active() {
    return (Boolean) storedValueForKey(_Product.ACTIVE_KEY);
  }

  public void setActive(Boolean value) {
    log.debug( "updating active from {} to {}", active(), value);
    takeStoredValueForKey(value, _Product.ACTIVE_KEY);
  }

  public com.example.shop.Category category() {
    Number value = (Number)storedValueForKey(_Product.CATEGORY_KEY);
    return (com.example.shop.Category)value;
  }

  public void setCategory(com.example.shop.Category value) {
    log.debug( "updating category from {} to {}", category(), value);
    takeStoredValueForKey(value, _Product.CATEGORY_KEY);
  }

  public String name() {
    return (String) storedValueForKey(_Product.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _Product.NAME_KEY);
  }

  public java.math.BigDecimal price() {
    return (java.math.BigDecimal) storedValueForKey(_Product.PRICE_KEY);
  }

  public void setPrice(java.math.BigDecimal value) {
    log.debug( "updating price from {} to {}", price(), value);
    takeStoredValueForKey(value, _Product.PRICE_KEY);
  }

  public NSArray<com.example.shop.Tag> tags() {
    return (NSArray<com.example.shop.Tag>)storedValueForKey(_Product.TAGS_KEY);
  }

  public NSArray<com.example.shop.Tag> tags(EOQualifier qualifier) {
    return tags(qualifier, null);
  }

  public NSArray<com.example.shop.Tag> tags(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    NSArray<com.example.shop.Tag> results;
      results = tags();
      if (qualifier != null) {
        results = (NSArray<com.example.shop.Tag>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<com.example.shop.Tag>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    return results;
  }

  public void addToTags(com.example.shop.Tag object) {
    includeObjectIntoPropertyWithKey(object, _Product.TAGS_KEY);
  }

  public void removeFromTags(com.example.shop.Tag object) {
    excludeObjectFromPropertyWithKey(object, _Product.TAGS_KEY);
  }

  public void addToTagsRelationship(com.example.shop.Tag object) {
    log.debug("adding {} to tags relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToTags(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Product.TAGS_KEY);
    }
  }

  public void removeFromTagsRelationship(com.example.shop.Tag object) {
    log.debug("removing {} from tags relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromTags(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Product.TAGS_KEY);
    }
  }

  public com.example.shop.Tag createTagsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  com.example.shop.Tag.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Product.TAGS_KEY);
    return (com.example.shop.Tag) eo;
  }

  public void deleteTagsRelationship(com.example.shop.Tag object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Product.TAGS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllTagsRelationships() {
    Enumeration<com.example.shop.Tag> objects = tags().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteTagsRelationship(objects.nextElement());
    }
  }


  public static Product createProduct(EOEditingContext editingContext, Boolean active
, com.example.shop.Category category
, String name
, java.math.BigDecimal price
) {
    Product eo = (Product) EOUtilities.createAndInsertInstance(editingContext, _Product.ENTITY_NAME);
    eo.setActive(active);
    eo.setCategory(category);
    eo.setName(name);
    eo.setPrice(price);
    return eo;
  }

  public static ERXFetchSpecification<Product> fetchSpec() {
    return new ERXFetchSpecification<Product>(_Product.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Product> fetchAllProducts(EOEditingContext editingContext) {
    return _Product.fetchAllProducts(editingContext, null);
  }

  public static NSArray<Product> fetchAllProducts(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Product.fetchProducts(editingContext, null, sortOrderings);
  }

  public static NSArray<Product> fetchProducts(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Product> fetchSpec = new ERXFetchSpecification<Product>(_Product.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Product> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Product fetchProduct(EOEditingContext editingContext, String keyName, Object value) {
    return _Product.fetchProduct(editingContext, ERXQ.equals(keyName, value));
  }

  public static Product fetchProduct(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Product> eoObjects = _Product.fetchProducts(editingContext, qualifier, null);
    Product eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Product that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Product fetchRequiredProduct(EOEditingContext editingContext, String keyName, Object value) {
    return _Product.fetchRequiredProduct(editingContext, ERXQ.equals(keyName, value));
  }

  public static Product fetchRequiredProduct(EOEditingContext editingContext, EOQualifier qualifier) {
    Product eoObject = _Product.fetchProduct(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Product that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Product localInstanceIn(EOEditingContext editingContext, Product eo) {
    Product localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

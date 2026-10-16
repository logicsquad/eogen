// $LastChangedRevision$ DO NOT EDIT.  Make changes to Product.java instead.
package com.example.shop;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Product extends  EOGenericRecord {
	public static final String ENTITY_NAME = "Product";

	// Attributes
	public static final String ACTIVE_KEY = "active";
	public static final String CATEGORY_KEY = "category";
	public static final String NAME_KEY = "name";
	public static final String PRICE_KEY = "price";

	// Relationships
	public static final String TAGS_KEY = "tags";

  private static Logger LOG = Logger.getLogger(_Product.class);

  public Product localInstanceIn(EOEditingContext editingContext) {
    Product localInstance = (Product)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public Boolean active() {
    return (Boolean) storedValueForKey("active");
  }

  public void setActive(Boolean value) {
    if (_Product.LOG.isDebugEnabled()) {
    	_Product.LOG.debug( "updating active from " + active() + " to " + value);
    }
    takeStoredValueForKey(value, "active");
  }

  public com.example.shop.Category category() {
    Number value = (Number)storedValueForKey("category");
    return (com.example.shop.Category)value;
  }

  public void setCategory(com.example.shop.Category value) {
    takeStoredValueForKey(value, "category");
  }

  public String name() {
    return (String) storedValueForKey("name");
  }

  public void setName(String value) {
    if (_Product.LOG.isDebugEnabled()) {
    	_Product.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, "name");
  }

  public java.math.BigDecimal price() {
    return (java.math.BigDecimal) storedValueForKey("price");
  }

  public void setPrice(java.math.BigDecimal value) {
    if (_Product.LOG.isDebugEnabled()) {
    	_Product.LOG.debug( "updating price from " + price() + " to " + value);
    }
    takeStoredValueForKey(value, "price");
  }

  public NSArray<com.example.shop.Tag> tags() {
    return (NSArray<com.example.shop.Tag>)storedValueForKey("tags");
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
  
  public void addToTagsRelationship(com.example.shop.Tag object) {
    if (_Product.LOG.isDebugEnabled()) {
      _Product.LOG.debug("adding " + object + " to tags relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "tags");
  }

  public void removeFromTagsRelationship(com.example.shop.Tag object) {
    if (_Product.LOG.isDebugEnabled()) {
      _Product.LOG.debug("removing " + object + " from tags relationship");
    }
    removeObjectFromBothSidesOfRelationshipWithKey(object, "tags");
  }

  public com.example.shop.Tag createTagsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Tag");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "tags");
    return (com.example.shop.Tag) eo;
  }

  public void deleteTagsRelationship(com.example.shop.Tag object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "tags");
    editingContext().deleteObject(object);
  }

  public void deleteAllTagsRelationships() {
    Enumeration objects = tags().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteTagsRelationship((com.example.shop.Tag)objects.nextElement());
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

  public static NSArray<Product> fetchAllProducts(EOEditingContext editingContext) {
    return _Product.fetchAllProducts(editingContext, null);
  }

  public static NSArray<Product> fetchAllProducts(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Product.fetchProducts(editingContext, null, sortOrderings);
  }

  public static NSArray<Product> fetchProducts(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Product.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Product> eoObjects = (NSArray<Product>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static Product fetchProduct(EOEditingContext editingContext, String keyName, Object value) {
    return _Product.fetchProduct(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Product fetchProduct(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Product> eoObjects = _Product.fetchProducts(editingContext, qualifier, null);
    Product eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (Product)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Product that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Product fetchRequiredProduct(EOEditingContext editingContext, String keyName, Object value) {
    return _Product.fetchRequiredProduct(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Product fetchRequiredProduct(EOEditingContext editingContext, EOQualifier qualifier) {
    Product eoObject = _Product.fetchProduct(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Product that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Product localInstanceIn(EOEditingContext editingContext, Product eo) {
    Product localInstance = (eo == null) ? null : (Product)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

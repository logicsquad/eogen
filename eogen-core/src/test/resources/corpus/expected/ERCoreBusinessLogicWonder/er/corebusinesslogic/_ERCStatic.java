// DO NOT EDIT.  Make changes to ERCStatic.java instead.
package er.corebusinesslogic;

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
public abstract class _ERCStatic extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERCStatic";

  // Attribute Keys
  public static final ERXKey<String> KEY = new ERXKey<String>("key", Type.Attribute);
  public static final ERXKey<String> VALUE = new ERXKey<String>("value", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String KEY_KEY = KEY.key();
  public static final String VALUE_KEY = VALUE.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_ERCStatic.class);

  public ERCStatic localInstanceIn(EOEditingContext editingContext) {
    ERCStatic localInstance = (ERCStatic)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String key() {
    return (String) storedValueForKey(_ERCStatic.KEY_KEY);
  }

  public void setKey(String value) {
    log.debug( "updating key from {} to {}", key(), value);
    takeStoredValueForKey(value, _ERCStatic.KEY_KEY);
  }

  public String value() {
    return (String) storedValueForKey(_ERCStatic.VALUE_KEY);
  }

  public void setValue(String value) {
    log.debug( "updating value from {} to {}", value(), value);
    takeStoredValueForKey(value, _ERCStatic.VALUE_KEY);
  }


  public static ERCStatic createERCStatic(EOEditingContext editingContext, String key
) {
    ERCStatic eo = (ERCStatic) EOUtilities.createAndInsertInstance(editingContext, _ERCStatic.ENTITY_NAME);
    eo.setKey(key);
    return eo;
  }

  public static ERXFetchSpecification<ERCStatic> fetchSpec() {
    return new ERXFetchSpecification<ERCStatic>(_ERCStatic.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERCStatic> fetchAllERCStatics(EOEditingContext editingContext) {
    return _ERCStatic.fetchAllERCStatics(editingContext, null);
  }

  public static NSArray<ERCStatic> fetchAllERCStatics(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERCStatic.fetchERCStatics(editingContext, null, sortOrderings);
  }

  public static NSArray<ERCStatic> fetchERCStatics(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERCStatic> fetchSpec = new ERXFetchSpecification<ERCStatic>(_ERCStatic.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERCStatic> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERCStatic fetchERCStatic(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCStatic.fetchERCStatic(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCStatic fetchERCStatic(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERCStatic> eoObjects = _ERCStatic.fetchERCStatics(editingContext, qualifier, null);
    ERCStatic eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERCStatic that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCStatic fetchRequiredERCStatic(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCStatic.fetchRequiredERCStatic(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCStatic fetchRequiredERCStatic(EOEditingContext editingContext, EOQualifier qualifier) {
    ERCStatic eoObject = _ERCStatic.fetchERCStatic(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERCStatic that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCStatic localInstanceIn(EOEditingContext editingContext, ERCStatic eo) {
    ERCStatic localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

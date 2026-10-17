// DO NOT EDIT.  Make changes to ERCPreference.java instead.
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
public abstract class _ERCPreference extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERCPreference";

  // Attribute Keys
  public static final ERXKey<String> KEY = new ERXKey<String>("key", Type.Attribute);
  public static final ERXKey<Integer> USER_ID = new ERXKey<Integer>("userID", Type.Attribute);
  public static final ERXKey<String> VALUE = new ERXKey<String>("value", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String KEY_KEY = KEY.key();
  public static final String USER_ID_KEY = USER_ID.key();
  public static final String VALUE_KEY = VALUE.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_ERCPreference.class);

  public ERCPreference localInstanceIn(EOEditingContext editingContext) {
    ERCPreference localInstance = (ERCPreference)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String key() {
    return (String) storedValueForKey(_ERCPreference.KEY_KEY);
  }

  public void setKey(String value) {
    log.debug( "updating key from {} to {}", key(), value);
    takeStoredValueForKey(value, _ERCPreference.KEY_KEY);
  }

  public Integer userID() {
    return (Integer) storedValueForKey(_ERCPreference.USER_ID_KEY);
  }

  public void setUserID(Integer value) {
    log.debug( "updating userID from {} to {}", userID(), value);
    takeStoredValueForKey(value, _ERCPreference.USER_ID_KEY);
  }

  public String value() {
    return (String) storedValueForKey(_ERCPreference.VALUE_KEY);
  }

  public void setValue(String value) {
    log.debug( "updating value from {} to {}", value(), value);
    takeStoredValueForKey(value, _ERCPreference.VALUE_KEY);
  }


  public static ERCPreference createERCPreference(EOEditingContext editingContext, String key
) {
    ERCPreference eo = (ERCPreference) EOUtilities.createAndInsertInstance(editingContext, _ERCPreference.ENTITY_NAME);
    eo.setKey(key);
    return eo;
  }

  public static ERXFetchSpecification<ERCPreference> fetchSpec() {
    return new ERXFetchSpecification<ERCPreference>(_ERCPreference.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERCPreference> fetchAllERCPreferences(EOEditingContext editingContext) {
    return _ERCPreference.fetchAllERCPreferences(editingContext, null);
  }

  public static NSArray<ERCPreference> fetchAllERCPreferences(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERCPreference.fetchERCPreferences(editingContext, null, sortOrderings);
  }

  public static NSArray<ERCPreference> fetchERCPreferences(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERCPreference> fetchSpec = new ERXFetchSpecification<ERCPreference>(_ERCPreference.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERCPreference> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERCPreference fetchERCPreference(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCPreference.fetchERCPreference(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCPreference fetchERCPreference(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERCPreference> eoObjects = _ERCPreference.fetchERCPreferences(editingContext, qualifier, null);
    ERCPreference eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERCPreference that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCPreference fetchRequiredERCPreference(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCPreference.fetchRequiredERCPreference(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCPreference fetchRequiredERCPreference(EOEditingContext editingContext, EOQualifier qualifier) {
    ERCPreference eoObject = _ERCPreference.fetchERCPreference(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERCPreference that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCPreference localInstanceIn(EOEditingContext editingContext, ERCPreference eo) {
    ERCPreference localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

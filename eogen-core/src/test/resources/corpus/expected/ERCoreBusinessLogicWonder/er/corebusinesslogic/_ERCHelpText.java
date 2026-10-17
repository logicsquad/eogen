// DO NOT EDIT.  Make changes to ERCHelpText.java instead.
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
public abstract class _ERCHelpText extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERCHelpText";

  // Attribute Keys
  public static final ERXKey<String> KEY = new ERXKey<String>("key", Type.Attribute);
  public static final ERXKey<String> VALUE = new ERXKey<String>("value", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String KEY_KEY = KEY.key();
  public static final String VALUE_KEY = VALUE.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_ERCHelpText.class);

  public ERCHelpText localInstanceIn(EOEditingContext editingContext) {
    ERCHelpText localInstance = (ERCHelpText)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String key() {
    return (String) storedValueForKey(_ERCHelpText.KEY_KEY);
  }

  public void setKey(String value) {
    log.debug( "updating key from {} to {}", key(), value);
    takeStoredValueForKey(value, _ERCHelpText.KEY_KEY);
  }

  public String value() {
    return (String) storedValueForKey(_ERCHelpText.VALUE_KEY);
  }

  public void setValue(String value) {
    log.debug( "updating value from {} to {}", value(), value);
    takeStoredValueForKey(value, _ERCHelpText.VALUE_KEY);
  }


  public static ERCHelpText createERCHelpText(EOEditingContext editingContext, String key
) {
    ERCHelpText eo = (ERCHelpText) EOUtilities.createAndInsertInstance(editingContext, _ERCHelpText.ENTITY_NAME);
    eo.setKey(key);
    return eo;
  }

  public static ERXFetchSpecification<ERCHelpText> fetchSpec() {
    return new ERXFetchSpecification<ERCHelpText>(_ERCHelpText.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERCHelpText> fetchAllERCHelpTexts(EOEditingContext editingContext) {
    return _ERCHelpText.fetchAllERCHelpTexts(editingContext, null);
  }

  public static NSArray<ERCHelpText> fetchAllERCHelpTexts(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERCHelpText.fetchERCHelpTexts(editingContext, null, sortOrderings);
  }

  public static NSArray<ERCHelpText> fetchERCHelpTexts(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERCHelpText> fetchSpec = new ERXFetchSpecification<ERCHelpText>(_ERCHelpText.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERCHelpText> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERCHelpText fetchERCHelpText(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCHelpText.fetchERCHelpText(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCHelpText fetchERCHelpText(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERCHelpText> eoObjects = _ERCHelpText.fetchERCHelpTexts(editingContext, qualifier, null);
    ERCHelpText eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERCHelpText that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCHelpText fetchRequiredERCHelpText(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCHelpText.fetchRequiredERCHelpText(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCHelpText fetchRequiredERCHelpText(EOEditingContext editingContext, EOQualifier qualifier) {
    ERCHelpText eoObject = _ERCHelpText.fetchERCHelpText(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERCHelpText that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCHelpText localInstanceIn(EOEditingContext editingContext, ERCHelpText eo) {
    ERCHelpText localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

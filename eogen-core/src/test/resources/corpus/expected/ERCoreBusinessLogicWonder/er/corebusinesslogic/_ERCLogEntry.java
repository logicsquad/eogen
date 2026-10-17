// DO NOT EDIT.  Make changes to ERCLogEntry.java instead.
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
public abstract class _ERCLogEntry extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERCLogEntry";

  // Attribute Keys
  public static final ERXKey<NSTimestamp> CREATED = new ERXKey<NSTimestamp>("created", Type.Attribute);
  public static final ERXKey<String> TEXT = new ERXKey<String>("text", Type.Attribute);
  public static final ERXKey<Integer> USER_ID = new ERXKey<Integer>("userID", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String CREATED_KEY = CREATED.key();
  public static final String TEXT_KEY = TEXT.key();
  public static final String USER_ID_KEY = USER_ID.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_ERCLogEntry.class);

  public ERCLogEntry localInstanceIn(EOEditingContext editingContext) {
    ERCLogEntry localInstance = (ERCLogEntry)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSTimestamp created() {
    return (NSTimestamp) storedValueForKey(_ERCLogEntry.CREATED_KEY);
  }

  public void setCreated(NSTimestamp value) {
    log.debug( "updating created from {} to {}", created(), value);
    takeStoredValueForKey(value, _ERCLogEntry.CREATED_KEY);
  }

  public String text() {
    return (String) storedValueForKey(_ERCLogEntry.TEXT_KEY);
  }

  public void setText(String value) {
    log.debug( "updating text from {} to {}", text(), value);
    takeStoredValueForKey(value, _ERCLogEntry.TEXT_KEY);
  }

  public Integer userID() {
    return (Integer) storedValueForKey(_ERCLogEntry.USER_ID_KEY);
  }

  public void setUserID(Integer value) {
    log.debug( "updating userID from {} to {}", userID(), value);
    takeStoredValueForKey(value, _ERCLogEntry.USER_ID_KEY);
  }


  public static ERCLogEntry createERCLogEntry(EOEditingContext editingContext, NSTimestamp created
, String text
, Integer userID
) {
    ERCLogEntry eo = (ERCLogEntry) EOUtilities.createAndInsertInstance(editingContext, _ERCLogEntry.ENTITY_NAME);
    eo.setCreated(created);
    eo.setText(text);
    eo.setUserID(userID);
    return eo;
  }

  public static ERXFetchSpecification<ERCLogEntry> fetchSpec() {
    return new ERXFetchSpecification<ERCLogEntry>(_ERCLogEntry.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERCLogEntry> fetchAllERCLogEntries(EOEditingContext editingContext) {
    return _ERCLogEntry.fetchAllERCLogEntries(editingContext, null);
  }

  public static NSArray<ERCLogEntry> fetchAllERCLogEntries(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERCLogEntry.fetchERCLogEntries(editingContext, null, sortOrderings);
  }

  public static NSArray<ERCLogEntry> fetchERCLogEntries(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERCLogEntry> fetchSpec = new ERXFetchSpecification<ERCLogEntry>(_ERCLogEntry.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERCLogEntry> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERCLogEntry fetchERCLogEntry(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCLogEntry.fetchERCLogEntry(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCLogEntry fetchERCLogEntry(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERCLogEntry> eoObjects = _ERCLogEntry.fetchERCLogEntries(editingContext, qualifier, null);
    ERCLogEntry eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERCLogEntry that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCLogEntry fetchRequiredERCLogEntry(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCLogEntry.fetchRequiredERCLogEntry(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCLogEntry fetchRequiredERCLogEntry(EOEditingContext editingContext, EOQualifier qualifier) {
    ERCLogEntry eoObject = _ERCLogEntry.fetchERCLogEntry(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERCLogEntry that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCLogEntry localInstanceIn(EOEditingContext editingContext, ERCLogEntry eo) {
    ERCLogEntry localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

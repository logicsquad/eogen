// DO NOT EDIT.  Make changes to ERCAuditTrailEntry.java instead.
package er.corebusinesslogic.audittrail;

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
public abstract class _ERCAuditTrailEntry extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERCAuditTrailEntry";

  // Attribute Keys
  public static final ERXKey<NSTimestamp> CREATED = new ERXKey<NSTimestamp>("created", Type.Attribute);
  public static final ERXKey<String> KEY_PATH = new ERXKey<String>("keyPath", Type.Attribute);
  public static final ERXKey<String> NEW_VALUES = new ERXKey<String>("newValues", Type.Attribute);
  public static final ERXKey<String> OLD_VALUES = new ERXKey<String>("oldValues", Type.Attribute);
  public static final ERXKey<er.corebusinesslogic.audittrail.ERCAuditTrailType> TYPE = new ERXKey<er.corebusinesslogic.audittrail.ERCAuditTrailType>("type", Type.Attribute);
  public static final ERXKey<er.extensions.eof.ERXKeyGlobalID> USER_GLOBAL_ID = new ERXKey<er.extensions.eof.ERXKeyGlobalID>("userGlobalID", Type.Attribute);
  public static final ERXKey<er.extensions.foundation.ERXMutableDictionary> USER_INFO = new ERXKey<er.extensions.foundation.ERXMutableDictionary>("userInfo", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.corebusinesslogic.audittrail.ERCAuditBlob> NEW_BLOB_VALUE = new ERXKey<er.corebusinesslogic.audittrail.ERCAuditBlob>("newBlobValue", Type.ToOneRelationship);
  public static final ERXKey<er.corebusinesslogic.audittrail.ERCAuditBlob> OLD_BLOB_VALUE = new ERXKey<er.corebusinesslogic.audittrail.ERCAuditBlob>("oldBlobValue", Type.ToOneRelationship);
  public static final ERXKey<er.corebusinesslogic.audittrail.ERCAuditTrail> TRAIL = new ERXKey<er.corebusinesslogic.audittrail.ERCAuditTrail>("trail", Type.ToOneRelationship);

  // Attributes
  public static final String CREATED_KEY = CREATED.key();
  public static final String KEY_PATH_KEY = KEY_PATH.key();
  public static final String NEW_VALUES_KEY = NEW_VALUES.key();
  public static final String OLD_VALUES_KEY = OLD_VALUES.key();
  public static final String TYPE_KEY = TYPE.key();
  public static final String USER_GLOBAL_ID_KEY = USER_GLOBAL_ID.key();
  public static final String USER_INFO_KEY = USER_INFO.key();

  // Relationships
  public static final String NEW_BLOB_VALUE_KEY = NEW_BLOB_VALUE.key();
  public static final String OLD_BLOB_VALUE_KEY = OLD_BLOB_VALUE.key();
  public static final String TRAIL_KEY = TRAIL.key();

  private static final Logger log = LoggerFactory.getLogger(_ERCAuditTrailEntry.class);

  public ERCAuditTrailEntry localInstanceIn(EOEditingContext editingContext) {
    ERCAuditTrailEntry localInstance = (ERCAuditTrailEntry)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSTimestamp created() {
    return (NSTimestamp) storedValueForKey(_ERCAuditTrailEntry.CREATED_KEY);
  }

  public void setCreated(NSTimestamp value) {
    log.debug( "updating created from {} to {}", created(), value);
    takeStoredValueForKey(value, _ERCAuditTrailEntry.CREATED_KEY);
  }

  public String keyPath() {
    return (String) storedValueForKey(_ERCAuditTrailEntry.KEY_PATH_KEY);
  }

  public void setKeyPath(String value) {
    log.debug( "updating keyPath from {} to {}", keyPath(), value);
    takeStoredValueForKey(value, _ERCAuditTrailEntry.KEY_PATH_KEY);
  }

  public String newValues() {
    return (String) storedValueForKey(_ERCAuditTrailEntry.NEW_VALUES_KEY);
  }

  public void setNewValues(String value) {
    log.debug( "updating newValues from {} to {}", newValues(), value);
    takeStoredValueForKey(value, _ERCAuditTrailEntry.NEW_VALUES_KEY);
  }

  public String oldValues() {
    return (String) storedValueForKey(_ERCAuditTrailEntry.OLD_VALUES_KEY);
  }

  public void setOldValues(String value) {
    log.debug( "updating oldValues from {} to {}", oldValues(), value);
    takeStoredValueForKey(value, _ERCAuditTrailEntry.OLD_VALUES_KEY);
  }

  public er.corebusinesslogic.audittrail.ERCAuditTrailType type() {
    return (er.corebusinesslogic.audittrail.ERCAuditTrailType) storedValueForKey(_ERCAuditTrailEntry.TYPE_KEY);
  }

  public void setType(er.corebusinesslogic.audittrail.ERCAuditTrailType value) {
    log.debug( "updating type from {} to {}", type(), value);
    takeStoredValueForKey(value, _ERCAuditTrailEntry.TYPE_KEY);
  }

  public er.extensions.eof.ERXKeyGlobalID userGlobalID() {
    return (er.extensions.eof.ERXKeyGlobalID) storedValueForKey(_ERCAuditTrailEntry.USER_GLOBAL_ID_KEY);
  }

  public void setUserGlobalID(er.extensions.eof.ERXKeyGlobalID value) {
    log.debug( "updating userGlobalID from {} to {}", userGlobalID(), value);
    takeStoredValueForKey(value, _ERCAuditTrailEntry.USER_GLOBAL_ID_KEY);
  }

  public er.extensions.foundation.ERXMutableDictionary userInfo() {
    return (er.extensions.foundation.ERXMutableDictionary) storedValueForKey(_ERCAuditTrailEntry.USER_INFO_KEY);
  }

  public void setUserInfo(er.extensions.foundation.ERXMutableDictionary value) {
    log.debug( "updating userInfo from {} to {}", userInfo(), value);
    takeStoredValueForKey(value, _ERCAuditTrailEntry.USER_INFO_KEY);
  }

  public er.corebusinesslogic.audittrail.ERCAuditBlob newBlobValue() {
    return (er.corebusinesslogic.audittrail.ERCAuditBlob)storedValueForKey(_ERCAuditTrailEntry.NEW_BLOB_VALUE_KEY);
  }

  public void setNewBlobValue(er.corebusinesslogic.audittrail.ERCAuditBlob value) {
    takeStoredValueForKey(value, _ERCAuditTrailEntry.NEW_BLOB_VALUE_KEY);
  }

  public void setNewBlobValueRelationship(er.corebusinesslogic.audittrail.ERCAuditBlob value) {
    log.debug("updating newBlobValue from {} to {}", newBlobValue(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setNewBlobValue(value);
    }
    else if (value == null) {
      er.corebusinesslogic.audittrail.ERCAuditBlob oldValue = newBlobValue();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _ERCAuditTrailEntry.NEW_BLOB_VALUE_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _ERCAuditTrailEntry.NEW_BLOB_VALUE_KEY);
    }
  }

  public er.corebusinesslogic.audittrail.ERCAuditBlob oldBlobValue() {
    return (er.corebusinesslogic.audittrail.ERCAuditBlob)storedValueForKey(_ERCAuditTrailEntry.OLD_BLOB_VALUE_KEY);
  }

  public void setOldBlobValue(er.corebusinesslogic.audittrail.ERCAuditBlob value) {
    takeStoredValueForKey(value, _ERCAuditTrailEntry.OLD_BLOB_VALUE_KEY);
  }

  public void setOldBlobValueRelationship(er.corebusinesslogic.audittrail.ERCAuditBlob value) {
    log.debug("updating oldBlobValue from {} to {}", oldBlobValue(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setOldBlobValue(value);
    }
    else if (value == null) {
      er.corebusinesslogic.audittrail.ERCAuditBlob oldValue = oldBlobValue();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _ERCAuditTrailEntry.OLD_BLOB_VALUE_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _ERCAuditTrailEntry.OLD_BLOB_VALUE_KEY);
    }
  }

  public er.corebusinesslogic.audittrail.ERCAuditTrail trail() {
    return (er.corebusinesslogic.audittrail.ERCAuditTrail)storedValueForKey(_ERCAuditTrailEntry.TRAIL_KEY);
  }

  public void setTrail(er.corebusinesslogic.audittrail.ERCAuditTrail value) {
    takeStoredValueForKey(value, _ERCAuditTrailEntry.TRAIL_KEY);
  }

  public void setTrailRelationship(er.corebusinesslogic.audittrail.ERCAuditTrail value) {
    log.debug("updating trail from {} to {}", trail(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setTrail(value);
    }
    else if (value == null) {
      er.corebusinesslogic.audittrail.ERCAuditTrail oldValue = trail();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _ERCAuditTrailEntry.TRAIL_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _ERCAuditTrailEntry.TRAIL_KEY);
    }
  }


  public static ERCAuditTrailEntry createERCAuditTrailEntry(EOEditingContext editingContext, NSTimestamp created
, er.corebusinesslogic.audittrail.ERCAuditTrailType type
, er.extensions.eof.ERXKeyGlobalID userGlobalID
, er.corebusinesslogic.audittrail.ERCAuditTrail trail) {
    ERCAuditTrailEntry eo = (ERCAuditTrailEntry) EOUtilities.createAndInsertInstance(editingContext, _ERCAuditTrailEntry.ENTITY_NAME);
    eo.setCreated(created);
    eo.setType(type);
    eo.setUserGlobalID(userGlobalID);
    eo.setTrailRelationship(trail);
    return eo;
  }

  public static ERXFetchSpecification<ERCAuditTrailEntry> fetchSpec() {
    return new ERXFetchSpecification<ERCAuditTrailEntry>(_ERCAuditTrailEntry.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERCAuditTrailEntry> fetchAllERCAuditTrailEntries(EOEditingContext editingContext) {
    return _ERCAuditTrailEntry.fetchAllERCAuditTrailEntries(editingContext, null);
  }

  public static NSArray<ERCAuditTrailEntry> fetchAllERCAuditTrailEntries(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERCAuditTrailEntry.fetchERCAuditTrailEntries(editingContext, null, sortOrderings);
  }

  public static NSArray<ERCAuditTrailEntry> fetchERCAuditTrailEntries(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERCAuditTrailEntry> fetchSpec = new ERXFetchSpecification<ERCAuditTrailEntry>(_ERCAuditTrailEntry.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERCAuditTrailEntry> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERCAuditTrailEntry fetchERCAuditTrailEntry(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCAuditTrailEntry.fetchERCAuditTrailEntry(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCAuditTrailEntry fetchERCAuditTrailEntry(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERCAuditTrailEntry> eoObjects = _ERCAuditTrailEntry.fetchERCAuditTrailEntries(editingContext, qualifier, null);
    ERCAuditTrailEntry eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERCAuditTrailEntry that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCAuditTrailEntry fetchRequiredERCAuditTrailEntry(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCAuditTrailEntry.fetchRequiredERCAuditTrailEntry(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCAuditTrailEntry fetchRequiredERCAuditTrailEntry(EOEditingContext editingContext, EOQualifier qualifier) {
    ERCAuditTrailEntry eoObject = _ERCAuditTrailEntry.fetchERCAuditTrailEntry(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERCAuditTrailEntry that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCAuditTrailEntry localInstanceIn(EOEditingContext editingContext, ERCAuditTrailEntry eo) {
    ERCAuditTrailEntry localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

// DO NOT EDIT.  Make changes to ERCAuditTrail.java instead.
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
public abstract class _ERCAuditTrail extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERCAuditTrail";

  // Attribute Keys
  public static final ERXKey<er.extensions.eof.ERXKeyGlobalID> GID = new ERXKey<er.extensions.eof.ERXKeyGlobalID>("gid", Type.Attribute);
  public static final ERXKey<Boolean> IS_DELETED = new ERXKey<Boolean>("isDeleted", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.corebusinesslogic.audittrail.ERCAuditTrailEntry> ENTRIES = new ERXKey<er.corebusinesslogic.audittrail.ERCAuditTrailEntry>("entries", Type.ToManyRelationship);

  // Attributes
  public static final String GID_KEY = GID.key();
  public static final String IS_DELETED_KEY = IS_DELETED.key();

  // Relationships
  public static final String ENTRIES_KEY = ENTRIES.key();

  private static final Logger log = LoggerFactory.getLogger(_ERCAuditTrail.class);

  public ERCAuditTrail localInstanceIn(EOEditingContext editingContext) {
    ERCAuditTrail localInstance = (ERCAuditTrail)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public er.extensions.eof.ERXKeyGlobalID gid() {
    return (er.extensions.eof.ERXKeyGlobalID) storedValueForKey(_ERCAuditTrail.GID_KEY);
  }

  public void setGid(er.extensions.eof.ERXKeyGlobalID value) {
    log.debug( "updating gid from {} to {}", gid(), value);
    takeStoredValueForKey(value, _ERCAuditTrail.GID_KEY);
  }

  public Boolean isDeleted() {
    return (Boolean) storedValueForKey(_ERCAuditTrail.IS_DELETED_KEY);
  }

  public void setIsDeleted(Boolean value) {
    log.debug( "updating isDeleted from {} to {}", isDeleted(), value);
    takeStoredValueForKey(value, _ERCAuditTrail.IS_DELETED_KEY);
  }

  public NSArray<er.corebusinesslogic.audittrail.ERCAuditTrailEntry> entries() {
    return (NSArray<er.corebusinesslogic.audittrail.ERCAuditTrailEntry>)storedValueForKey(_ERCAuditTrail.ENTRIES_KEY);
  }

  public NSArray<er.corebusinesslogic.audittrail.ERCAuditTrailEntry> entries(EOQualifier qualifier) {
    return entries(qualifier, null, false);
  }

  public NSArray<er.corebusinesslogic.audittrail.ERCAuditTrailEntry> entries(EOQualifier qualifier, boolean fetch) {
    return entries(qualifier, null, fetch);
  }

  public NSArray<er.corebusinesslogic.audittrail.ERCAuditTrailEntry> entries(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.corebusinesslogic.audittrail.ERCAuditTrailEntry> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(er.corebusinesslogic.audittrail.ERCAuditTrailEntry.TRAIL_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = er.corebusinesslogic.audittrail.ERCAuditTrailEntry.fetchERCAuditTrailEntries(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = entries();
      if (qualifier != null) {
        results = (NSArray<er.corebusinesslogic.audittrail.ERCAuditTrailEntry>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.corebusinesslogic.audittrail.ERCAuditTrailEntry>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToEntries(er.corebusinesslogic.audittrail.ERCAuditTrailEntry object) {
    includeObjectIntoPropertyWithKey(object, _ERCAuditTrail.ENTRIES_KEY);
  }

  public void removeFromEntries(er.corebusinesslogic.audittrail.ERCAuditTrailEntry object) {
    excludeObjectFromPropertyWithKey(object, _ERCAuditTrail.ENTRIES_KEY);
  }

  public void addToEntriesRelationship(er.corebusinesslogic.audittrail.ERCAuditTrailEntry object) {
    log.debug("adding {} to entries relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToEntries(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _ERCAuditTrail.ENTRIES_KEY);
    }
  }

  public void removeFromEntriesRelationship(er.corebusinesslogic.audittrail.ERCAuditTrailEntry object) {
    log.debug("removing {} from entries relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromEntries(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _ERCAuditTrail.ENTRIES_KEY);
    }
  }

  public er.corebusinesslogic.audittrail.ERCAuditTrailEntry createEntriesRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.corebusinesslogic.audittrail.ERCAuditTrailEntry.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _ERCAuditTrail.ENTRIES_KEY);
    return (er.corebusinesslogic.audittrail.ERCAuditTrailEntry) eo;
  }

  public void deleteEntriesRelationship(er.corebusinesslogic.audittrail.ERCAuditTrailEntry object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _ERCAuditTrail.ENTRIES_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllEntriesRelationships() {
    Enumeration<er.corebusinesslogic.audittrail.ERCAuditTrailEntry> objects = entries().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteEntriesRelationship(objects.nextElement());
    }
  }


  public static ERCAuditTrail createERCAuditTrail(EOEditingContext editingContext, er.extensions.eof.ERXKeyGlobalID gid
, Boolean isDeleted
) {
    ERCAuditTrail eo = (ERCAuditTrail) EOUtilities.createAndInsertInstance(editingContext, _ERCAuditTrail.ENTITY_NAME);
    eo.setGid(gid);
    eo.setIsDeleted(isDeleted);
    return eo;
  }

  public static ERXFetchSpecification<ERCAuditTrail> fetchSpec() {
    return new ERXFetchSpecification<ERCAuditTrail>(_ERCAuditTrail.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERCAuditTrail> fetchAllERCAuditTrails(EOEditingContext editingContext) {
    return _ERCAuditTrail.fetchAllERCAuditTrails(editingContext, null);
  }

  public static NSArray<ERCAuditTrail> fetchAllERCAuditTrails(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERCAuditTrail.fetchERCAuditTrails(editingContext, null, sortOrderings);
  }

  public static NSArray<ERCAuditTrail> fetchERCAuditTrails(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERCAuditTrail> fetchSpec = new ERXFetchSpecification<ERCAuditTrail>(_ERCAuditTrail.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERCAuditTrail> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERCAuditTrail fetchERCAuditTrail(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCAuditTrail.fetchERCAuditTrail(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCAuditTrail fetchERCAuditTrail(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERCAuditTrail> eoObjects = _ERCAuditTrail.fetchERCAuditTrails(editingContext, qualifier, null);
    ERCAuditTrail eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERCAuditTrail that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCAuditTrail fetchRequiredERCAuditTrail(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCAuditTrail.fetchRequiredERCAuditTrail(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCAuditTrail fetchRequiredERCAuditTrail(EOEditingContext editingContext, EOQualifier qualifier) {
    ERCAuditTrail eoObject = _ERCAuditTrail.fetchERCAuditTrail(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERCAuditTrail that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCAuditTrail localInstanceIn(EOEditingContext editingContext, ERCAuditTrail eo) {
    ERCAuditTrail localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

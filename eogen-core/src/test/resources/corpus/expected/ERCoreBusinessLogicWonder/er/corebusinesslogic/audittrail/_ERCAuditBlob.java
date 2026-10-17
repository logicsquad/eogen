// DO NOT EDIT.  Make changes to ERCAuditBlob.java instead.
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
public abstract class _ERCAuditBlob extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERCAuditBlob";

  // Attribute Keys
  public static final ERXKey<NSData> BLOB_VALUE = new ERXKey<NSData>("blobValue", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String BLOB_VALUE_KEY = BLOB_VALUE.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_ERCAuditBlob.class);

  public ERCAuditBlob localInstanceIn(EOEditingContext editingContext) {
    ERCAuditBlob localInstance = (ERCAuditBlob)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSData blobValue() {
    return (NSData) storedValueForKey(_ERCAuditBlob.BLOB_VALUE_KEY);
  }

  public void setBlobValue(NSData value) {
    log.debug( "updating blobValue from {} to {}", blobValue(), value);
    takeStoredValueForKey(value, _ERCAuditBlob.BLOB_VALUE_KEY);
  }


  public static ERCAuditBlob createERCAuditBlob(EOEditingContext editingContext, NSData blobValue
) {
    ERCAuditBlob eo = (ERCAuditBlob) EOUtilities.createAndInsertInstance(editingContext, _ERCAuditBlob.ENTITY_NAME);
    eo.setBlobValue(blobValue);
    return eo;
  }

  public static ERXFetchSpecification<ERCAuditBlob> fetchSpec() {
    return new ERXFetchSpecification<ERCAuditBlob>(_ERCAuditBlob.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERCAuditBlob> fetchAllERCAuditBlobs(EOEditingContext editingContext) {
    return _ERCAuditBlob.fetchAllERCAuditBlobs(editingContext, null);
  }

  public static NSArray<ERCAuditBlob> fetchAllERCAuditBlobs(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERCAuditBlob.fetchERCAuditBlobs(editingContext, null, sortOrderings);
  }

  public static NSArray<ERCAuditBlob> fetchERCAuditBlobs(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERCAuditBlob> fetchSpec = new ERXFetchSpecification<ERCAuditBlob>(_ERCAuditBlob.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERCAuditBlob> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERCAuditBlob fetchERCAuditBlob(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCAuditBlob.fetchERCAuditBlob(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCAuditBlob fetchERCAuditBlob(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERCAuditBlob> eoObjects = _ERCAuditBlob.fetchERCAuditBlobs(editingContext, qualifier, null);
    ERCAuditBlob eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERCAuditBlob that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCAuditBlob fetchRequiredERCAuditBlob(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCAuditBlob.fetchRequiredERCAuditBlob(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCAuditBlob fetchRequiredERCAuditBlob(EOEditingContext editingContext, EOQualifier qualifier) {
    ERCAuditBlob eoObject = _ERCAuditBlob.fetchERCAuditBlob(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERCAuditBlob that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCAuditBlob localInstanceIn(EOEditingContext editingContext, ERCAuditBlob eo) {
    ERCAuditBlob localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

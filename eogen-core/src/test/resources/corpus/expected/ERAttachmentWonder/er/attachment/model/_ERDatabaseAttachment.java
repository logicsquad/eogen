// DO NOT EDIT.  Make changes to ERDatabaseAttachment.java instead.
package er.attachment.model;

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
public abstract class _ERDatabaseAttachment extends er.attachment.model.ERAttachment {
  public static final String ENTITY_NAME = "ERDatabaseAttachment";

  // Attribute Keys
  public static final ERXKey<NSData> SMALL_DATA = new ERXKey<NSData>("smallData", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.attachment.model.ERAttachmentData> ATTACHMENT_DATA = new ERXKey<er.attachment.model.ERAttachmentData>("attachmentData", Type.ToOneRelationship);

  // Attributes
  public static final String SMALL_DATA_KEY = SMALL_DATA.key();

  // Relationships
  public static final String ATTACHMENT_DATA_KEY = ATTACHMENT_DATA.key();

  private static final Logger log = LoggerFactory.getLogger(_ERDatabaseAttachment.class);

  @Override
  public ERDatabaseAttachment localInstanceIn(EOEditingContext editingContext) {
    ERDatabaseAttachment localInstance = (ERDatabaseAttachment)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSData smallData() {
    return (NSData) storedValueForKey(_ERDatabaseAttachment.SMALL_DATA_KEY);
  }

  public void setSmallData(NSData value) {
    log.debug( "updating smallData from {} to {}", smallData(), value);
    takeStoredValueForKey(value, _ERDatabaseAttachment.SMALL_DATA_KEY);
  }

  public er.attachment.model.ERAttachmentData attachmentData() {
    return (er.attachment.model.ERAttachmentData)storedValueForKey(_ERDatabaseAttachment.ATTACHMENT_DATA_KEY);
  }

  public void setAttachmentData(er.attachment.model.ERAttachmentData value) {
    takeStoredValueForKey(value, _ERDatabaseAttachment.ATTACHMENT_DATA_KEY);
  }

  public void setAttachmentDataRelationship(er.attachment.model.ERAttachmentData value) {
    log.debug("updating attachmentData from {} to {}", attachmentData(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setAttachmentData(value);
    }
    else if (value == null) {
      er.attachment.model.ERAttachmentData oldValue = attachmentData();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _ERDatabaseAttachment.ATTACHMENT_DATA_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _ERDatabaseAttachment.ATTACHMENT_DATA_KEY);
    }
  }


  public static ERDatabaseAttachment createERDatabaseAttachment(EOEditingContext editingContext, Boolean available
, NSTimestamp creationDate
, String mimeType
, String originalFileName
, Boolean proxied
, Integer size
, String webPath
) {
    ERDatabaseAttachment eo = (ERDatabaseAttachment) EOUtilities.createAndInsertInstance(editingContext, _ERDatabaseAttachment.ENTITY_NAME);
    eo.setAvailable(available);
    eo.setCreationDate(creationDate);
    eo.setMimeType(mimeType);
    eo.setOriginalFileName(originalFileName);
    eo.setProxied(proxied);
    eo.setSize(size);
    eo.setWebPath(webPath);
    return eo;
  }

  public static ERXFetchSpecification<ERDatabaseAttachment> fetchSpecForERDatabaseAttachment() {
    return new ERXFetchSpecification<ERDatabaseAttachment>(_ERDatabaseAttachment.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERDatabaseAttachment> fetchAllERDatabaseAttachments(EOEditingContext editingContext) {
    return _ERDatabaseAttachment.fetchAllERDatabaseAttachments(editingContext, null);
  }

  public static NSArray<ERDatabaseAttachment> fetchAllERDatabaseAttachments(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERDatabaseAttachment.fetchERDatabaseAttachments(editingContext, null, sortOrderings);
  }

  public static NSArray<ERDatabaseAttachment> fetchERDatabaseAttachments(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERDatabaseAttachment> fetchSpec = new ERXFetchSpecification<ERDatabaseAttachment>(_ERDatabaseAttachment.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERDatabaseAttachment> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERDatabaseAttachment fetchERDatabaseAttachment(EOEditingContext editingContext, String keyName, Object value) {
    return _ERDatabaseAttachment.fetchERDatabaseAttachment(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERDatabaseAttachment fetchERDatabaseAttachment(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERDatabaseAttachment> eoObjects = _ERDatabaseAttachment.fetchERDatabaseAttachments(editingContext, qualifier, null);
    ERDatabaseAttachment eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERDatabaseAttachment that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERDatabaseAttachment fetchRequiredERDatabaseAttachment(EOEditingContext editingContext, String keyName, Object value) {
    return _ERDatabaseAttachment.fetchRequiredERDatabaseAttachment(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERDatabaseAttachment fetchRequiredERDatabaseAttachment(EOEditingContext editingContext, EOQualifier qualifier) {
    ERDatabaseAttachment eoObject = _ERDatabaseAttachment.fetchERDatabaseAttachment(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERDatabaseAttachment that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERDatabaseAttachment localInstanceIn(EOEditingContext editingContext, ERDatabaseAttachment eo) {
    ERDatabaseAttachment localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

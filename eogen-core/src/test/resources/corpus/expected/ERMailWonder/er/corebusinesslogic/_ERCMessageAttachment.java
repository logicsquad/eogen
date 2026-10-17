// DO NOT EDIT.  Make changes to ERCMessageAttachment.java instead.
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
public abstract class _ERCMessageAttachment extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERCMessageAttachment";

  // Attribute Keys
  public static final ERXKey<String> FILE_PATH = new ERXKey<String>("filePath", Type.Attribute);
  public static final ERXKey<String> MIME_TYPE = new ERXKey<String>("mimeType", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.corebusinesslogic.ERCMailMessage> MAIL_MESSAGE = new ERXKey<er.corebusinesslogic.ERCMailMessage>("mailMessage", Type.ToOneRelationship);

  // Attributes
  public static final String FILE_PATH_KEY = FILE_PATH.key();
  public static final String MIME_TYPE_KEY = MIME_TYPE.key();

  // Relationships
  public static final String MAIL_MESSAGE_KEY = MAIL_MESSAGE.key();

  private static final Logger log = LoggerFactory.getLogger(_ERCMessageAttachment.class);

  public ERCMessageAttachment localInstanceIn(EOEditingContext editingContext) {
    ERCMessageAttachment localInstance = (ERCMessageAttachment)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String filePath() {
    return (String) storedValueForKey(_ERCMessageAttachment.FILE_PATH_KEY);
  }

  public void setFilePath(String value) {
    log.debug( "updating filePath from {} to {}", filePath(), value);
    takeStoredValueForKey(value, _ERCMessageAttachment.FILE_PATH_KEY);
  }

  public String mimeType() {
    return (String) storedValueForKey(_ERCMessageAttachment.MIME_TYPE_KEY);
  }

  public void setMimeType(String value) {
    log.debug( "updating mimeType from {} to {}", mimeType(), value);
    takeStoredValueForKey(value, _ERCMessageAttachment.MIME_TYPE_KEY);
  }

  public er.corebusinesslogic.ERCMailMessage mailMessage() {
    return (er.corebusinesslogic.ERCMailMessage)storedValueForKey(_ERCMessageAttachment.MAIL_MESSAGE_KEY);
  }

  public void setMailMessage(er.corebusinesslogic.ERCMailMessage value) {
    takeStoredValueForKey(value, _ERCMessageAttachment.MAIL_MESSAGE_KEY);
  }

  public void setMailMessageRelationship(er.corebusinesslogic.ERCMailMessage value) {
    log.debug("updating mailMessage from {} to {}", mailMessage(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setMailMessage(value);
    }
    else if (value == null) {
      er.corebusinesslogic.ERCMailMessage oldValue = mailMessage();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _ERCMessageAttachment.MAIL_MESSAGE_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _ERCMessageAttachment.MAIL_MESSAGE_KEY);
    }
  }


  public static ERCMessageAttachment createERCMessageAttachment(EOEditingContext editingContext, String filePath
, String mimeType
, er.corebusinesslogic.ERCMailMessage mailMessage) {
    ERCMessageAttachment eo = (ERCMessageAttachment) EOUtilities.createAndInsertInstance(editingContext, _ERCMessageAttachment.ENTITY_NAME);
    eo.setFilePath(filePath);
    eo.setMimeType(mimeType);
    eo.setMailMessageRelationship(mailMessage);
    return eo;
  }

  public static ERXFetchSpecification<ERCMessageAttachment> fetchSpec() {
    return new ERXFetchSpecification<ERCMessageAttachment>(_ERCMessageAttachment.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERCMessageAttachment> fetchAllERCMessageAttachments(EOEditingContext editingContext) {
    return _ERCMessageAttachment.fetchAllERCMessageAttachments(editingContext, null);
  }

  public static NSArray<ERCMessageAttachment> fetchAllERCMessageAttachments(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERCMessageAttachment.fetchERCMessageAttachments(editingContext, null, sortOrderings);
  }

  public static NSArray<ERCMessageAttachment> fetchERCMessageAttachments(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERCMessageAttachment> fetchSpec = new ERXFetchSpecification<ERCMessageAttachment>(_ERCMessageAttachment.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERCMessageAttachment> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERCMessageAttachment fetchERCMessageAttachment(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCMessageAttachment.fetchERCMessageAttachment(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCMessageAttachment fetchERCMessageAttachment(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERCMessageAttachment> eoObjects = _ERCMessageAttachment.fetchERCMessageAttachments(editingContext, qualifier, null);
    ERCMessageAttachment eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERCMessageAttachment that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCMessageAttachment fetchRequiredERCMessageAttachment(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCMessageAttachment.fetchRequiredERCMessageAttachment(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCMessageAttachment fetchRequiredERCMessageAttachment(EOEditingContext editingContext, EOQualifier qualifier) {
    ERCMessageAttachment eoObject = _ERCMessageAttachment.fetchERCMessageAttachment(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERCMessageAttachment that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCMessageAttachment localInstanceIn(EOEditingContext editingContext, ERCMessageAttachment eo) {
    ERCMessageAttachment localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

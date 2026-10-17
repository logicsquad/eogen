// DO NOT EDIT.  Make changes to ERFileAttachment.java instead.
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
public abstract class _ERFileAttachment extends er.attachment.model.ERAttachment {
  public static final String ENTITY_NAME = "ERFileAttachment";

  // Attribute Keys
  public static final ERXKey<String> FILESYSTEM_PATH = new ERXKey<String>("filesystemPath", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String FILESYSTEM_PATH_KEY = FILESYSTEM_PATH.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_ERFileAttachment.class);

  @Override
  public ERFileAttachment localInstanceIn(EOEditingContext editingContext) {
    ERFileAttachment localInstance = (ERFileAttachment)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String filesystemPath() {
    return (String) storedValueForKey(_ERFileAttachment.FILESYSTEM_PATH_KEY);
  }

  public void setFilesystemPath(String value) {
    log.debug( "updating filesystemPath from {} to {}", filesystemPath(), value);
    takeStoredValueForKey(value, _ERFileAttachment.FILESYSTEM_PATH_KEY);
  }


  public static ERFileAttachment createERFileAttachment(EOEditingContext editingContext, Boolean available
, NSTimestamp creationDate
, String mimeType
, String originalFileName
, Boolean proxied
, Integer size
, String webPath
) {
    ERFileAttachment eo = (ERFileAttachment) EOUtilities.createAndInsertInstance(editingContext, _ERFileAttachment.ENTITY_NAME);
    eo.setAvailable(available);
    eo.setCreationDate(creationDate);
    eo.setMimeType(mimeType);
    eo.setOriginalFileName(originalFileName);
    eo.setProxied(proxied);
    eo.setSize(size);
    eo.setWebPath(webPath);
    return eo;
  }

  public static ERXFetchSpecification<ERFileAttachment> fetchSpecForERFileAttachment() {
    return new ERXFetchSpecification<ERFileAttachment>(_ERFileAttachment.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERFileAttachment> fetchAllERFileAttachments(EOEditingContext editingContext) {
    return _ERFileAttachment.fetchAllERFileAttachments(editingContext, null);
  }

  public static NSArray<ERFileAttachment> fetchAllERFileAttachments(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERFileAttachment.fetchERFileAttachments(editingContext, null, sortOrderings);
  }

  public static NSArray<ERFileAttachment> fetchERFileAttachments(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERFileAttachment> fetchSpec = new ERXFetchSpecification<ERFileAttachment>(_ERFileAttachment.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERFileAttachment> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERFileAttachment fetchERFileAttachment(EOEditingContext editingContext, String keyName, Object value) {
    return _ERFileAttachment.fetchERFileAttachment(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERFileAttachment fetchERFileAttachment(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERFileAttachment> eoObjects = _ERFileAttachment.fetchERFileAttachments(editingContext, qualifier, null);
    ERFileAttachment eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERFileAttachment that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERFileAttachment fetchRequiredERFileAttachment(EOEditingContext editingContext, String keyName, Object value) {
    return _ERFileAttachment.fetchRequiredERFileAttachment(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERFileAttachment fetchRequiredERFileAttachment(EOEditingContext editingContext, EOQualifier qualifier) {
    ERFileAttachment eoObject = _ERFileAttachment.fetchERFileAttachment(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERFileAttachment that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERFileAttachment localInstanceIn(EOEditingContext editingContext, ERFileAttachment eo) {
    ERFileAttachment localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

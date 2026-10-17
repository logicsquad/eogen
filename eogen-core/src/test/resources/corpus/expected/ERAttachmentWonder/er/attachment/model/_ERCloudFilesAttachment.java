// DO NOT EDIT.  Make changes to ERCloudFilesAttachment.java instead.
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
public abstract class _ERCloudFilesAttachment extends er.attachment.model.ERAttachment {
  public static final String ENTITY_NAME = "ERCloudFilesAttachment";

  // Attribute Keys
  public static final ERXKey<String> CF_PATH = new ERXKey<String>("cfPath", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String CF_PATH_KEY = CF_PATH.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_ERCloudFilesAttachment.class);

  @Override
  public ERCloudFilesAttachment localInstanceIn(EOEditingContext editingContext) {
    ERCloudFilesAttachment localInstance = (ERCloudFilesAttachment)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String cfPath() {
    return (String) storedValueForKey(_ERCloudFilesAttachment.CF_PATH_KEY);
  }

  public void setCfPath(String value) {
    log.debug( "updating cfPath from {} to {}", cfPath(), value);
    takeStoredValueForKey(value, _ERCloudFilesAttachment.CF_PATH_KEY);
  }


  public static ERCloudFilesAttachment createERCloudFilesAttachment(EOEditingContext editingContext, Boolean available
, NSTimestamp creationDate
, String mimeType
, String originalFileName
, Boolean proxied
, Integer size
, String webPath
) {
    ERCloudFilesAttachment eo = (ERCloudFilesAttachment) EOUtilities.createAndInsertInstance(editingContext, _ERCloudFilesAttachment.ENTITY_NAME);
    eo.setAvailable(available);
    eo.setCreationDate(creationDate);
    eo.setMimeType(mimeType);
    eo.setOriginalFileName(originalFileName);
    eo.setProxied(proxied);
    eo.setSize(size);
    eo.setWebPath(webPath);
    return eo;
  }

  public static ERXFetchSpecification<ERCloudFilesAttachment> fetchSpecForERCloudFilesAttachment() {
    return new ERXFetchSpecification<ERCloudFilesAttachment>(_ERCloudFilesAttachment.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERCloudFilesAttachment> fetchAllERCloudFilesAttachments(EOEditingContext editingContext) {
    return _ERCloudFilesAttachment.fetchAllERCloudFilesAttachments(editingContext, null);
  }

  public static NSArray<ERCloudFilesAttachment> fetchAllERCloudFilesAttachments(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERCloudFilesAttachment.fetchERCloudFilesAttachments(editingContext, null, sortOrderings);
  }

  public static NSArray<ERCloudFilesAttachment> fetchERCloudFilesAttachments(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERCloudFilesAttachment> fetchSpec = new ERXFetchSpecification<ERCloudFilesAttachment>(_ERCloudFilesAttachment.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERCloudFilesAttachment> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERCloudFilesAttachment fetchERCloudFilesAttachment(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCloudFilesAttachment.fetchERCloudFilesAttachment(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCloudFilesAttachment fetchERCloudFilesAttachment(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERCloudFilesAttachment> eoObjects = _ERCloudFilesAttachment.fetchERCloudFilesAttachments(editingContext, qualifier, null);
    ERCloudFilesAttachment eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERCloudFilesAttachment that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCloudFilesAttachment fetchRequiredERCloudFilesAttachment(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCloudFilesAttachment.fetchRequiredERCloudFilesAttachment(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCloudFilesAttachment fetchRequiredERCloudFilesAttachment(EOEditingContext editingContext, EOQualifier qualifier) {
    ERCloudFilesAttachment eoObject = _ERCloudFilesAttachment.fetchERCloudFilesAttachment(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERCloudFilesAttachment that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCloudFilesAttachment localInstanceIn(EOEditingContext editingContext, ERCloudFilesAttachment eo) {
    ERCloudFilesAttachment localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

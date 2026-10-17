// DO NOT EDIT.  Make changes to ERS3Attachment.java instead.
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
public abstract class _ERS3Attachment extends er.attachment.model.ERAttachment {
  public static final String ENTITY_NAME = "ERS3Attachment";

  // Attribute Keys
  public static final ERXKey<String> S3_PATH = new ERXKey<String>("s3Path", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String S3_PATH_KEY = S3_PATH.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_ERS3Attachment.class);

  @Override
  public ERS3Attachment localInstanceIn(EOEditingContext editingContext) {
    ERS3Attachment localInstance = (ERS3Attachment)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String s3Path() {
    return (String) storedValueForKey(_ERS3Attachment.S3_PATH_KEY);
  }

  public void setS3Path(String value) {
    log.debug( "updating s3Path from {} to {}", s3Path(), value);
    takeStoredValueForKey(value, _ERS3Attachment.S3_PATH_KEY);
  }


  public static ERS3Attachment createERS3Attachment(EOEditingContext editingContext, Boolean available
, NSTimestamp creationDate
, String mimeType
, String originalFileName
, Boolean proxied
, Integer size
, String webPath
) {
    ERS3Attachment eo = (ERS3Attachment) EOUtilities.createAndInsertInstance(editingContext, _ERS3Attachment.ENTITY_NAME);
    eo.setAvailable(available);
    eo.setCreationDate(creationDate);
    eo.setMimeType(mimeType);
    eo.setOriginalFileName(originalFileName);
    eo.setProxied(proxied);
    eo.setSize(size);
    eo.setWebPath(webPath);
    return eo;
  }

  public static ERXFetchSpecification<ERS3Attachment> fetchSpecForERS3Attachment() {
    return new ERXFetchSpecification<ERS3Attachment>(_ERS3Attachment.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERS3Attachment> fetchAllERS3Attachments(EOEditingContext editingContext) {
    return _ERS3Attachment.fetchAllERS3Attachments(editingContext, null);
  }

  public static NSArray<ERS3Attachment> fetchAllERS3Attachments(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERS3Attachment.fetchERS3Attachments(editingContext, null, sortOrderings);
  }

  public static NSArray<ERS3Attachment> fetchERS3Attachments(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERS3Attachment> fetchSpec = new ERXFetchSpecification<ERS3Attachment>(_ERS3Attachment.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERS3Attachment> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERS3Attachment fetchERS3Attachment(EOEditingContext editingContext, String keyName, Object value) {
    return _ERS3Attachment.fetchERS3Attachment(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERS3Attachment fetchERS3Attachment(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERS3Attachment> eoObjects = _ERS3Attachment.fetchERS3Attachments(editingContext, qualifier, null);
    ERS3Attachment eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERS3Attachment that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERS3Attachment fetchRequiredERS3Attachment(EOEditingContext editingContext, String keyName, Object value) {
    return _ERS3Attachment.fetchRequiredERS3Attachment(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERS3Attachment fetchRequiredERS3Attachment(EOEditingContext editingContext, EOQualifier qualifier) {
    ERS3Attachment eoObject = _ERS3Attachment.fetchERS3Attachment(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERS3Attachment that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERS3Attachment localInstanceIn(EOEditingContext editingContext, ERS3Attachment eo) {
    ERS3Attachment localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

// DO NOT EDIT.  Make changes to ERIFile.java instead.
package er.indexing.storage;

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
public abstract class _ERIFile extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERIFile";

  // Attribute Keys
  public static final ERXKey<NSTimestamp> LAST_MODIFIED = new ERXKey<NSTimestamp>("lastModified", Type.Attribute);
  public static final ERXKey<Long> LENGTH = new ERXKey<Long>("length", Type.Attribute);
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.indexing.storage.ERIFileContent> CONTENT = new ERXKey<er.indexing.storage.ERIFileContent>("content", Type.ToOneRelationship);
  public static final ERXKey<er.indexing.storage.ERIDirectory> DIRECTORY = new ERXKey<er.indexing.storage.ERIDirectory>("directory", Type.ToOneRelationship);

  // Attributes
  public static final String LAST_MODIFIED_KEY = LAST_MODIFIED.key();
  public static final String LENGTH_KEY = LENGTH.key();
  public static final String NAME_KEY = NAME.key();

  // Relationships
  public static final String CONTENT_KEY = CONTENT.key();
  public static final String DIRECTORY_KEY = DIRECTORY.key();

  private static final Logger log = LoggerFactory.getLogger(_ERIFile.class);

  public ERIFile localInstanceIn(EOEditingContext editingContext) {
    ERIFile localInstance = (ERIFile)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSTimestamp lastModified() {
    return (NSTimestamp) storedValueForKey(_ERIFile.LAST_MODIFIED_KEY);
  }

  public void setLastModified(NSTimestamp value) {
    log.debug( "updating lastModified from {} to {}", lastModified(), value);
    takeStoredValueForKey(value, _ERIFile.LAST_MODIFIED_KEY);
  }

  public Long length() {
    return (Long) storedValueForKey(_ERIFile.LENGTH_KEY);
  }

  public void setLength(Long value) {
    log.debug( "updating length from {} to {}", length(), value);
    takeStoredValueForKey(value, _ERIFile.LENGTH_KEY);
  }

  public String name() {
    return (String) storedValueForKey(_ERIFile.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _ERIFile.NAME_KEY);
  }

  public er.indexing.storage.ERIFileContent content() {
    return (er.indexing.storage.ERIFileContent)storedValueForKey(_ERIFile.CONTENT_KEY);
  }

  public void setContent(er.indexing.storage.ERIFileContent value) {
    takeStoredValueForKey(value, _ERIFile.CONTENT_KEY);
  }

  public void setContentRelationship(er.indexing.storage.ERIFileContent value) {
    log.debug("updating content from {} to {}", content(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setContent(value);
    }
    else if (value == null) {
      er.indexing.storage.ERIFileContent oldValue = content();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _ERIFile.CONTENT_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _ERIFile.CONTENT_KEY);
    }
  }

  public er.indexing.storage.ERIDirectory directory() {
    return (er.indexing.storage.ERIDirectory)storedValueForKey(_ERIFile.DIRECTORY_KEY);
  }

  public void setDirectory(er.indexing.storage.ERIDirectory value) {
    takeStoredValueForKey(value, _ERIFile.DIRECTORY_KEY);
  }

  public void setDirectoryRelationship(er.indexing.storage.ERIDirectory value) {
    log.debug("updating directory from {} to {}", directory(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setDirectory(value);
    }
    else if (value == null) {
      er.indexing.storage.ERIDirectory oldValue = directory();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _ERIFile.DIRECTORY_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _ERIFile.DIRECTORY_KEY);
    }
  }


  public static ERIFile createERIFile(EOEditingContext editingContext, NSTimestamp lastModified
, Long length
, String name
, er.indexing.storage.ERIFileContent content, er.indexing.storage.ERIDirectory directory) {
    ERIFile eo = (ERIFile) EOUtilities.createAndInsertInstance(editingContext, _ERIFile.ENTITY_NAME);
    eo.setLastModified(lastModified);
    eo.setLength(length);
    eo.setName(name);
    eo.setContentRelationship(content);
    eo.setDirectoryRelationship(directory);
    return eo;
  }

  public static ERXFetchSpecification<ERIFile> fetchSpec() {
    return new ERXFetchSpecification<ERIFile>(_ERIFile.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERIFile> fetchAllERIFiles(EOEditingContext editingContext) {
    return _ERIFile.fetchAllERIFiles(editingContext, null);
  }

  public static NSArray<ERIFile> fetchAllERIFiles(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERIFile.fetchERIFiles(editingContext, null, sortOrderings);
  }

  public static NSArray<ERIFile> fetchERIFiles(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERIFile> fetchSpec = new ERXFetchSpecification<ERIFile>(_ERIFile.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERIFile> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERIFile fetchERIFile(EOEditingContext editingContext, String keyName, Object value) {
    return _ERIFile.fetchERIFile(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERIFile fetchERIFile(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERIFile> eoObjects = _ERIFile.fetchERIFiles(editingContext, qualifier, null);
    ERIFile eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERIFile that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERIFile fetchRequiredERIFile(EOEditingContext editingContext, String keyName, Object value) {
    return _ERIFile.fetchRequiredERIFile(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERIFile fetchRequiredERIFile(EOEditingContext editingContext, EOQualifier qualifier) {
    ERIFile eoObject = _ERIFile.fetchERIFile(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERIFile that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERIFile localInstanceIn(EOEditingContext editingContext, ERIFile eo) {
    ERIFile localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

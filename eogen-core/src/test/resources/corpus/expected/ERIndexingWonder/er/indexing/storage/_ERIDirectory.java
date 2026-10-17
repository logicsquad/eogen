// DO NOT EDIT.  Make changes to ERIDirectory.java instead.
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
public abstract class _ERIDirectory extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERIDirectory";

  // Attribute Keys
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.indexing.storage.ERIFile> FILES = new ERXKey<er.indexing.storage.ERIFile>("files", Type.ToManyRelationship);

  // Attributes
  public static final String NAME_KEY = NAME.key();

  // Relationships
  public static final String FILES_KEY = FILES.key();

  private static final Logger log = LoggerFactory.getLogger(_ERIDirectory.class);

  public ERIDirectory localInstanceIn(EOEditingContext editingContext) {
    ERIDirectory localInstance = (ERIDirectory)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String name() {
    return (String) storedValueForKey(_ERIDirectory.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _ERIDirectory.NAME_KEY);
  }

  public NSArray<er.indexing.storage.ERIFile> files() {
    return (NSArray<er.indexing.storage.ERIFile>)storedValueForKey(_ERIDirectory.FILES_KEY);
  }

  public NSArray<er.indexing.storage.ERIFile> files(EOQualifier qualifier) {
    return files(qualifier, null, false);
  }

  public NSArray<er.indexing.storage.ERIFile> files(EOQualifier qualifier, boolean fetch) {
    return files(qualifier, null, fetch);
  }

  public NSArray<er.indexing.storage.ERIFile> files(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.indexing.storage.ERIFile> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(er.indexing.storage.ERIFile.DIRECTORY_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = er.indexing.storage.ERIFile.fetchERIFiles(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = files();
      if (qualifier != null) {
        results = (NSArray<er.indexing.storage.ERIFile>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.indexing.storage.ERIFile>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToFiles(er.indexing.storage.ERIFile object) {
    includeObjectIntoPropertyWithKey(object, _ERIDirectory.FILES_KEY);
  }

  public void removeFromFiles(er.indexing.storage.ERIFile object) {
    excludeObjectFromPropertyWithKey(object, _ERIDirectory.FILES_KEY);
  }

  public void addToFilesRelationship(er.indexing.storage.ERIFile object) {
    log.debug("adding {} to files relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToFiles(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _ERIDirectory.FILES_KEY);
    }
  }

  public void removeFromFilesRelationship(er.indexing.storage.ERIFile object) {
    log.debug("removing {} from files relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromFiles(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _ERIDirectory.FILES_KEY);
    }
  }

  public er.indexing.storage.ERIFile createFilesRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.indexing.storage.ERIFile.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _ERIDirectory.FILES_KEY);
    return (er.indexing.storage.ERIFile) eo;
  }

  public void deleteFilesRelationship(er.indexing.storage.ERIFile object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _ERIDirectory.FILES_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllFilesRelationships() {
    Enumeration<er.indexing.storage.ERIFile> objects = files().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteFilesRelationship(objects.nextElement());
    }
  }


  public static ERIDirectory createERIDirectory(EOEditingContext editingContext, String name
) {
    ERIDirectory eo = (ERIDirectory) EOUtilities.createAndInsertInstance(editingContext, _ERIDirectory.ENTITY_NAME);
    eo.setName(name);
    return eo;
  }

  public static ERXFetchSpecification<ERIDirectory> fetchSpec() {
    return new ERXFetchSpecification<ERIDirectory>(_ERIDirectory.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERIDirectory> fetchAllERIDirectories(EOEditingContext editingContext) {
    return _ERIDirectory.fetchAllERIDirectories(editingContext, null);
  }

  public static NSArray<ERIDirectory> fetchAllERIDirectories(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERIDirectory.fetchERIDirectories(editingContext, null, sortOrderings);
  }

  public static NSArray<ERIDirectory> fetchERIDirectories(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERIDirectory> fetchSpec = new ERXFetchSpecification<ERIDirectory>(_ERIDirectory.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERIDirectory> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERIDirectory fetchERIDirectory(EOEditingContext editingContext, String keyName, Object value) {
    return _ERIDirectory.fetchERIDirectory(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERIDirectory fetchERIDirectory(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERIDirectory> eoObjects = _ERIDirectory.fetchERIDirectories(editingContext, qualifier, null);
    ERIDirectory eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERIDirectory that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERIDirectory fetchRequiredERIDirectory(EOEditingContext editingContext, String keyName, Object value) {
    return _ERIDirectory.fetchRequiredERIDirectory(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERIDirectory fetchRequiredERIDirectory(EOEditingContext editingContext, EOQualifier qualifier) {
    ERIDirectory eoObject = _ERIDirectory.fetchERIDirectory(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERIDirectory that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERIDirectory localInstanceIn(EOEditingContext editingContext, ERIDirectory eo) {
    ERIDirectory localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

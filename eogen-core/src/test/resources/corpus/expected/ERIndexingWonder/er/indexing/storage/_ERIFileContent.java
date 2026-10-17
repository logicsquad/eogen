// DO NOT EDIT.  Make changes to ERIFileContent.java instead.
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
public abstract class _ERIFileContent extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERIFileContent";

  // Attribute Keys
  public static final ERXKey<NSData> CONTENT = new ERXKey<NSData>("content", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String CONTENT_KEY = CONTENT.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_ERIFileContent.class);

  public ERIFileContent localInstanceIn(EOEditingContext editingContext) {
    ERIFileContent localInstance = (ERIFileContent)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSData content() {
    return (NSData) storedValueForKey(_ERIFileContent.CONTENT_KEY);
  }

  public void setContent(NSData value) {
    log.debug( "updating content from {} to {}", content(), value);
    takeStoredValueForKey(value, _ERIFileContent.CONTENT_KEY);
  }


  public static ERIFileContent createERIFileContent(EOEditingContext editingContext, NSData content
) {
    ERIFileContent eo = (ERIFileContent) EOUtilities.createAndInsertInstance(editingContext, _ERIFileContent.ENTITY_NAME);
    eo.setContent(content);
    return eo;
  }

  public static ERXFetchSpecification<ERIFileContent> fetchSpec() {
    return new ERXFetchSpecification<ERIFileContent>(_ERIFileContent.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERIFileContent> fetchAllERIFileContents(EOEditingContext editingContext) {
    return _ERIFileContent.fetchAllERIFileContents(editingContext, null);
  }

  public static NSArray<ERIFileContent> fetchAllERIFileContents(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERIFileContent.fetchERIFileContents(editingContext, null, sortOrderings);
  }

  public static NSArray<ERIFileContent> fetchERIFileContents(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERIFileContent> fetchSpec = new ERXFetchSpecification<ERIFileContent>(_ERIFileContent.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERIFileContent> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERIFileContent fetchERIFileContent(EOEditingContext editingContext, String keyName, Object value) {
    return _ERIFileContent.fetchERIFileContent(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERIFileContent fetchERIFileContent(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERIFileContent> eoObjects = _ERIFileContent.fetchERIFileContents(editingContext, qualifier, null);
    ERIFileContent eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERIFileContent that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERIFileContent fetchRequiredERIFileContent(EOEditingContext editingContext, String keyName, Object value) {
    return _ERIFileContent.fetchRequiredERIFileContent(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERIFileContent fetchRequiredERIFileContent(EOEditingContext editingContext, EOQualifier qualifier) {
    ERIFileContent eoObject = _ERIFileContent.fetchERIFileContent(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERIFileContent that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERIFileContent localInstanceIn(EOEditingContext editingContext, ERIFileContent eo) {
    ERIFileContent localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

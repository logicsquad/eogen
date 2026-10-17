// DO NOT EDIT.  Make changes to Priority.java instead.
package er.bugtracker;

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
public abstract class _Priority extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Priority";

  // Attribute Keys
  public static final ERXKey<Integer> SORT_ORDER = new ERXKey<Integer>("sortOrder", Type.Attribute);
  public static final ERXKey<String> TEXT_DESCRIPTION = new ERXKey<String>("textDescription", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String SORT_ORDER_KEY = SORT_ORDER.key();
  public static final String TEXT_DESCRIPTION_KEY = TEXT_DESCRIPTION.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_Priority.class);

  public Priority localInstanceIn(EOEditingContext editingContext) {
    Priority localInstance = (Priority)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public Integer sortOrder() {
    return (Integer) storedValueForKey(_Priority.SORT_ORDER_KEY);
  }

  public void setSortOrder(Integer value) {
    log.debug( "updating sortOrder from {} to {}", sortOrder(), value);
    takeStoredValueForKey(value, _Priority.SORT_ORDER_KEY);
  }

  public String textDescription() {
    return (String) storedValueForKey(_Priority.TEXT_DESCRIPTION_KEY);
  }

  public void setTextDescription(String value) {
    log.debug( "updating textDescription from {} to {}", textDescription(), value);
    takeStoredValueForKey(value, _Priority.TEXT_DESCRIPTION_KEY);
  }


  public static Priority createPriority(EOEditingContext editingContext, Integer sortOrder
, String textDescription
) {
    Priority eo = (Priority) EOUtilities.createAndInsertInstance(editingContext, _Priority.ENTITY_NAME);
    eo.setSortOrder(sortOrder);
    eo.setTextDescription(textDescription);
    return eo;
  }

  public static ERXFetchSpecification<Priority> fetchSpec() {
    return new ERXFetchSpecification<Priority>(_Priority.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Priority> fetchAllPriorities(EOEditingContext editingContext) {
    return _Priority.fetchAllPriorities(editingContext, null);
  }

  public static NSArray<Priority> fetchAllPriorities(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Priority.fetchPriorities(editingContext, null, sortOrderings);
  }

  public static NSArray<Priority> fetchPriorities(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Priority> fetchSpec = new ERXFetchSpecification<Priority>(_Priority.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Priority> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Priority fetchPriority(EOEditingContext editingContext, String keyName, Object value) {
    return _Priority.fetchPriority(editingContext, ERXQ.equals(keyName, value));
  }

  public static Priority fetchPriority(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Priority> eoObjects = _Priority.fetchPriorities(editingContext, qualifier, null);
    Priority eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Priority that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Priority fetchRequiredPriority(EOEditingContext editingContext, String keyName, Object value) {
    return _Priority.fetchRequiredPriority(editingContext, ERXQ.equals(keyName, value));
  }

  public static Priority fetchRequiredPriority(EOEditingContext editingContext, EOQualifier qualifier) {
    Priority eoObject = _Priority.fetchPriority(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Priority that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Priority localInstanceIn(EOEditingContext editingContext, Priority eo) {
    Priority localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

// $LastChangedRevision$ DO NOT EDIT.  Make changes to Priority.java instead.
package er.bugtracker;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Priority extends er.extensions.eof.ERXGenericRecord {
	public static final String ENTITY_NAME = "Priority";

	// Attributes
	public static final String SORT_ORDER_KEY = "sortOrder";
	public static final String TEXT_DESCRIPTION_KEY = "textDescription";

	// Relationships

  private static Logger LOG = Logger.getLogger(_Priority.class);

  public Priority localInstanceIn(EOEditingContext editingContext) {
    Priority localInstance = (Priority)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public Integer sortOrder() {
    return (Integer) storedValueForKey("sortOrder");
  }

  public void setSortOrder(Integer value) {
    if (_Priority.LOG.isDebugEnabled()) {
    	_Priority.LOG.debug( "updating sortOrder from " + sortOrder() + " to " + value);
    }
    takeStoredValueForKey(value, "sortOrder");
  }

  public String textDescription() {
    return (String) storedValueForKey("textDescription");
  }

  public void setTextDescription(String value) {
    if (_Priority.LOG.isDebugEnabled()) {
    	_Priority.LOG.debug( "updating textDescription from " + textDescription() + " to " + value);
    }
    takeStoredValueForKey(value, "textDescription");
  }


  public static Priority createPriority(EOEditingContext editingContext, Integer sortOrder
, String textDescription
) {
    Priority eo = (Priority) EOUtilities.createAndInsertInstance(editingContext, _Priority.ENTITY_NAME);    
		eo.setSortOrder(sortOrder);
		eo.setTextDescription(textDescription);
    return eo;
  }

  public static NSArray<Priority> fetchAllPriorities(EOEditingContext editingContext) {
    return _Priority.fetchAllPriorities(editingContext, null);
  }

  public static NSArray<Priority> fetchAllPriorities(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Priority.fetchPriorities(editingContext, null, sortOrderings);
  }

  public static NSArray<Priority> fetchPriorities(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Priority.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Priority> eoObjects = (NSArray<Priority>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static Priority fetchPriority(EOEditingContext editingContext, String keyName, Object value) {
    return _Priority.fetchPriority(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Priority fetchPriority(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Priority> eoObjects = _Priority.fetchPriorities(editingContext, qualifier, null);
    Priority eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (Priority)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Priority that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Priority fetchRequiredPriority(EOEditingContext editingContext, String keyName, Object value) {
    return _Priority.fetchRequiredPriority(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Priority fetchRequiredPriority(EOEditingContext editingContext, EOQualifier qualifier) {
    Priority eoObject = _Priority.fetchPriority(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Priority that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Priority localInstanceIn(EOEditingContext editingContext, Priority eo) {
    Priority localInstance = (eo == null) ? null : (Priority)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

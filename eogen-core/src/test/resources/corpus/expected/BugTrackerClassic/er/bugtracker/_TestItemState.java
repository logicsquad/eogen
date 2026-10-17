// $LastChangedRevision$ DO NOT EDIT.  Make changes to TestItemState.java instead.
package er.bugtracker;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _TestItemState extends er.extensions.eof.ERXGenericRecord {
	public static final String ENTITY_NAME = "TestItemState";

	// Attributes
	public static final String NAME_KEY = "name";
	public static final String SORT_ORDER_KEY = "sortOrder";

	// Relationships

  private static Logger LOG = Logger.getLogger(_TestItemState.class);

  public TestItemState localInstanceIn(EOEditingContext editingContext) {
    TestItemState localInstance = (TestItemState)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String name() {
    return (String) storedValueForKey("name");
  }

  public void setName(String value) {
    if (_TestItemState.LOG.isDebugEnabled()) {
    	_TestItemState.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, "name");
  }

  public Integer sortOrder() {
    return (Integer) storedValueForKey("sortOrder");
  }

  public void setSortOrder(Integer value) {
    if (_TestItemState.LOG.isDebugEnabled()) {
    	_TestItemState.LOG.debug( "updating sortOrder from " + sortOrder() + " to " + value);
    }
    takeStoredValueForKey(value, "sortOrder");
  }


  public static TestItemState createTestItemState(EOEditingContext editingContext, String name
, Integer sortOrder
) {
    TestItemState eo = (TestItemState) EOUtilities.createAndInsertInstance(editingContext, _TestItemState.ENTITY_NAME);    
		eo.setName(name);
		eo.setSortOrder(sortOrder);
    return eo;
  }

  public static NSArray<TestItemState> fetchAllTestItemStates(EOEditingContext editingContext) {
    return _TestItemState.fetchAllTestItemStates(editingContext, null);
  }

  public static NSArray<TestItemState> fetchAllTestItemStates(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _TestItemState.fetchTestItemStates(editingContext, null, sortOrderings);
  }

  public static NSArray<TestItemState> fetchTestItemStates(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_TestItemState.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<TestItemState> eoObjects = (NSArray<TestItemState>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static TestItemState fetchTestItemState(EOEditingContext editingContext, String keyName, Object value) {
    return _TestItemState.fetchTestItemState(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static TestItemState fetchTestItemState(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<TestItemState> eoObjects = _TestItemState.fetchTestItemStates(editingContext, qualifier, null);
    TestItemState eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (TestItemState)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one TestItemState that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static TestItemState fetchRequiredTestItemState(EOEditingContext editingContext, String keyName, Object value) {
    return _TestItemState.fetchRequiredTestItemState(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static TestItemState fetchRequiredTestItemState(EOEditingContext editingContext, EOQualifier qualifier) {
    TestItemState eoObject = _TestItemState.fetchTestItemState(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no TestItemState that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static TestItemState localInstanceIn(EOEditingContext editingContext, TestItemState eo) {
    TestItemState localInstance = (eo == null) ? null : (TestItemState)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

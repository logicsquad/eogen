// DO NOT EDIT.  Make changes to TestItemState.java instead.
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
public abstract class _TestItemState extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "TestItemState";

  // Attribute Keys
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);
  public static final ERXKey<Integer> SORT_ORDER = new ERXKey<Integer>("sortOrder", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String NAME_KEY = NAME.key();
  public static final String SORT_ORDER_KEY = SORT_ORDER.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_TestItemState.class);

  public TestItemState localInstanceIn(EOEditingContext editingContext) {
    TestItemState localInstance = (TestItemState)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String name() {
    return (String) storedValueForKey(_TestItemState.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _TestItemState.NAME_KEY);
  }

  public Integer sortOrder() {
    return (Integer) storedValueForKey(_TestItemState.SORT_ORDER_KEY);
  }

  public void setSortOrder(Integer value) {
    log.debug( "updating sortOrder from {} to {}", sortOrder(), value);
    takeStoredValueForKey(value, _TestItemState.SORT_ORDER_KEY);
  }


  public static TestItemState createTestItemState(EOEditingContext editingContext, String name
, Integer sortOrder
) {
    TestItemState eo = (TestItemState) EOUtilities.createAndInsertInstance(editingContext, _TestItemState.ENTITY_NAME);
    eo.setName(name);
    eo.setSortOrder(sortOrder);
    return eo;
  }

  public static ERXFetchSpecification<TestItemState> fetchSpec() {
    return new ERXFetchSpecification<TestItemState>(_TestItemState.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<TestItemState> fetchAllTestItemStates(EOEditingContext editingContext) {
    return _TestItemState.fetchAllTestItemStates(editingContext, null);
  }

  public static NSArray<TestItemState> fetchAllTestItemStates(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _TestItemState.fetchTestItemStates(editingContext, null, sortOrderings);
  }

  public static NSArray<TestItemState> fetchTestItemStates(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<TestItemState> fetchSpec = new ERXFetchSpecification<TestItemState>(_TestItemState.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<TestItemState> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static TestItemState fetchTestItemState(EOEditingContext editingContext, String keyName, Object value) {
    return _TestItemState.fetchTestItemState(editingContext, ERXQ.equals(keyName, value));
  }

  public static TestItemState fetchTestItemState(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<TestItemState> eoObjects = _TestItemState.fetchTestItemStates(editingContext, qualifier, null);
    TestItemState eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one TestItemState that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static TestItemState fetchRequiredTestItemState(EOEditingContext editingContext, String keyName, Object value) {
    return _TestItemState.fetchRequiredTestItemState(editingContext, ERXQ.equals(keyName, value));
  }

  public static TestItemState fetchRequiredTestItemState(EOEditingContext editingContext, EOQualifier qualifier) {
    TestItemState eoObject = _TestItemState.fetchTestItemState(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no TestItemState that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static TestItemState localInstanceIn(EOEditingContext editingContext, TestItemState eo) {
    TestItemState localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

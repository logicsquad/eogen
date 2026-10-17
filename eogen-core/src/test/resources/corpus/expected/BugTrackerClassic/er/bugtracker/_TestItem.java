// $LastChangedRevision$ DO NOT EDIT.  Make changes to TestItem.java instead.
package er.bugtracker;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _TestItem extends er.extensions.eof.ERXGenericRecord {
	public static final String ENTITY_NAME = "TestItem";

	// Attributes
	public static final String COMMENTS_KEY = "comments";
	public static final String CONTROLLED_KEY = "controlled";
	public static final String DATE_CREATED_KEY = "dateCreated";
	public static final String ID_KEY = "id";
	public static final String TEXT_DESCRIPTION_KEY = "textDescription";
	public static final String TITLE_KEY = "title";

	// Relationships
	public static final String BUGS_KEY = "bugs";
	public static final String COMPONENT_KEY = "component";
	public static final String OWNER_KEY = "owner";
	public static final String REQUIREMENTS_KEY = "requirements";
	public static final String STATE_KEY = "state";

  private static Logger LOG = Logger.getLogger(_TestItem.class);

  public TestItem localInstanceIn(EOEditingContext editingContext) {
    TestItem localInstance = (TestItem)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String comments() {
    return (String) storedValueForKey("comments");
  }

  public void setComments(String value) {
    if (_TestItem.LOG.isDebugEnabled()) {
    	_TestItem.LOG.debug( "updating comments from " + comments() + " to " + value);
    }
    takeStoredValueForKey(value, "comments");
  }

  public String controlled() {
    return (String) storedValueForKey("controlled");
  }

  public void setControlled(String value) {
    if (_TestItem.LOG.isDebugEnabled()) {
    	_TestItem.LOG.debug( "updating controlled from " + controlled() + " to " + value);
    }
    takeStoredValueForKey(value, "controlled");
  }

  public NSTimestamp dateCreated() {
    return (NSTimestamp) storedValueForKey("dateCreated");
  }

  public void setDateCreated(NSTimestamp value) {
    if (_TestItem.LOG.isDebugEnabled()) {
    	_TestItem.LOG.debug( "updating dateCreated from " + dateCreated() + " to " + value);
    }
    takeStoredValueForKey(value, "dateCreated");
  }

  public Integer id() {
    return (Integer) storedValueForKey("id");
  }

  public void setId(Integer value) {
    if (_TestItem.LOG.isDebugEnabled()) {
    	_TestItem.LOG.debug( "updating id from " + id() + " to " + value);
    }
    takeStoredValueForKey(value, "id");
  }

  public String textDescription() {
    return (String) storedValueForKey("textDescription");
  }

  public void setTextDescription(String value) {
    if (_TestItem.LOG.isDebugEnabled()) {
    	_TestItem.LOG.debug( "updating textDescription from " + textDescription() + " to " + value);
    }
    takeStoredValueForKey(value, "textDescription");
  }

  public String title() {
    return (String) storedValueForKey("title");
  }

  public void setTitle(String value) {
    if (_TestItem.LOG.isDebugEnabled()) {
    	_TestItem.LOG.debug( "updating title from " + title() + " to " + value);
    }
    takeStoredValueForKey(value, "title");
  }

  public er.bugtracker.Component component() {
    return (er.bugtracker.Component)storedValueForKey("component");
  }

  public void setComponentRelationship(er.bugtracker.Component value) {
    if (_TestItem.LOG.isDebugEnabled()) {
      _TestItem.LOG.debug("updating component from " + component() + " to " + value);
    }
    if (value == null) {
    	er.bugtracker.Component oldValue = component();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "component");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "component");
    }
  }
  
  public er.bugtracker.People owner() {
    return (er.bugtracker.People)storedValueForKey("owner");
  }

  public void setOwnerRelationship(er.bugtracker.People value) {
    if (_TestItem.LOG.isDebugEnabled()) {
      _TestItem.LOG.debug("updating owner from " + owner() + " to " + value);
    }
    if (value == null) {
    	er.bugtracker.People oldValue = owner();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "owner");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "owner");
    }
  }
  
  public er.bugtracker.TestItemState state() {
    return (er.bugtracker.TestItemState)storedValueForKey("state");
  }

  public void setStateRelationship(er.bugtracker.TestItemState value) {
    if (_TestItem.LOG.isDebugEnabled()) {
      _TestItem.LOG.debug("updating state from " + state() + " to " + value);
    }
    if (value == null) {
    	er.bugtracker.TestItemState oldValue = state();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "state");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "state");
    }
  }
  
  public NSArray<er.bugtracker.Bug> bugs() {
    return (NSArray<er.bugtracker.Bug>)storedValueForKey("bugs");
  }

  public NSArray<er.bugtracker.Bug> bugs(EOQualifier qualifier) {
    return bugs(qualifier, null);
  }

  public NSArray<er.bugtracker.Bug> bugs(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    NSArray<er.bugtracker.Bug> results;
      results = bugs();
      if (qualifier != null) {
        results = (NSArray<er.bugtracker.Bug>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.bugtracker.Bug>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    return results;
  }
  
  public void addToBugsRelationship(er.bugtracker.Bug object) {
    if (_TestItem.LOG.isDebugEnabled()) {
      _TestItem.LOG.debug("adding " + object + " to bugs relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "bugs");
  }

  public void removeFromBugsRelationship(er.bugtracker.Bug object) {
    if (_TestItem.LOG.isDebugEnabled()) {
      _TestItem.LOG.debug("removing " + object + " from bugs relationship");
    }
    removeObjectFromBothSidesOfRelationshipWithKey(object, "bugs");
  }

  public er.bugtracker.Bug createBugsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Bug");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "bugs");
    return (er.bugtracker.Bug) eo;
  }

  public void deleteBugsRelationship(er.bugtracker.Bug object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "bugs");
    editingContext().deleteObject(object);
  }

  public void deleteAllBugsRelationships() {
    Enumeration objects = bugs().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteBugsRelationship((er.bugtracker.Bug)objects.nextElement());
    }
  }

  public NSArray<er.bugtracker.Requirement> requirements() {
    return (NSArray<er.bugtracker.Requirement>)storedValueForKey("requirements");
  }

  public NSArray<er.bugtracker.Requirement> requirements(EOQualifier qualifier) {
    return requirements(qualifier, null);
  }

  public NSArray<er.bugtracker.Requirement> requirements(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    NSArray<er.bugtracker.Requirement> results;
      results = requirements();
      if (qualifier != null) {
        results = (NSArray<er.bugtracker.Requirement>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.bugtracker.Requirement>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    return results;
  }
  
  public void addToRequirementsRelationship(er.bugtracker.Requirement object) {
    if (_TestItem.LOG.isDebugEnabled()) {
      _TestItem.LOG.debug("adding " + object + " to requirements relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "requirements");
  }

  public void removeFromRequirementsRelationship(er.bugtracker.Requirement object) {
    if (_TestItem.LOG.isDebugEnabled()) {
      _TestItem.LOG.debug("removing " + object + " from requirements relationship");
    }
    removeObjectFromBothSidesOfRelationshipWithKey(object, "requirements");
  }

  public er.bugtracker.Requirement createRequirementsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Requirement");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "requirements");
    return (er.bugtracker.Requirement) eo;
  }

  public void deleteRequirementsRelationship(er.bugtracker.Requirement object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "requirements");
    editingContext().deleteObject(object);
  }

  public void deleteAllRequirementsRelationships() {
    Enumeration objects = requirements().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteRequirementsRelationship((er.bugtracker.Requirement)objects.nextElement());
    }
  }


  public static TestItem createTestItem(EOEditingContext editingContext, String controlled
, NSTimestamp dateCreated
, Integer id
, String title
, er.bugtracker.Component component, er.bugtracker.TestItemState state) {
    TestItem eo = (TestItem) EOUtilities.createAndInsertInstance(editingContext, _TestItem.ENTITY_NAME);    
		eo.setControlled(controlled);
		eo.setDateCreated(dateCreated);
		eo.setId(id);
		eo.setTitle(title);
    eo.setComponentRelationship(component);
    eo.setStateRelationship(state);
    return eo;
  }

  public static NSArray<TestItem> fetchAllTestItems(EOEditingContext editingContext) {
    return _TestItem.fetchAllTestItems(editingContext, null);
  }

  public static NSArray<TestItem> fetchAllTestItems(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _TestItem.fetchTestItems(editingContext, null, sortOrderings);
  }

  public static NSArray<TestItem> fetchTestItems(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_TestItem.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<TestItem> eoObjects = (NSArray<TestItem>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static TestItem fetchTestItem(EOEditingContext editingContext, String keyName, Object value) {
    return _TestItem.fetchTestItem(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static TestItem fetchTestItem(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<TestItem> eoObjects = _TestItem.fetchTestItems(editingContext, qualifier, null);
    TestItem eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (TestItem)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one TestItem that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static TestItem fetchRequiredTestItem(EOEditingContext editingContext, String keyName, Object value) {
    return _TestItem.fetchRequiredTestItem(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static TestItem fetchRequiredTestItem(EOEditingContext editingContext, EOQualifier qualifier) {
    TestItem eoObject = _TestItem.fetchTestItem(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no TestItem that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static TestItem localInstanceIn(EOEditingContext editingContext, TestItem eo) {
    TestItem localInstance = (eo == null) ? null : (TestItem)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

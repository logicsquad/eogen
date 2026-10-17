// DO NOT EDIT.  Make changes to TestItem.java instead.
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
public abstract class _TestItem extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "TestItem";

  // Attribute Keys
  public static final ERXKey<String> COMMENTS = new ERXKey<String>("comments", Type.Attribute);
  public static final ERXKey<String> CONTROLLED = new ERXKey<String>("controlled", Type.Attribute);
  public static final ERXKey<NSTimestamp> DATE_CREATED = new ERXKey<NSTimestamp>("dateCreated", Type.Attribute);
  public static final ERXKey<Integer> ID = new ERXKey<Integer>("id", Type.Attribute);
  public static final ERXKey<String> TEXT_DESCRIPTION = new ERXKey<String>("textDescription", Type.Attribute);
  public static final ERXKey<String> TITLE = new ERXKey<String>("title", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.bugtracker.Bug> BUGS = new ERXKey<er.bugtracker.Bug>("bugs", Type.ToManyRelationship);
  public static final ERXKey<er.bugtracker.Component> COMPONENT = new ERXKey<er.bugtracker.Component>("component", Type.ToOneRelationship);
  public static final ERXKey<er.bugtracker.People> OWNER = new ERXKey<er.bugtracker.People>("owner", Type.ToOneRelationship);
  public static final ERXKey<er.bugtracker.Requirement> REQUIREMENTS = new ERXKey<er.bugtracker.Requirement>("requirements", Type.ToManyRelationship);
  public static final ERXKey<er.bugtracker.TestItemState> STATE = new ERXKey<er.bugtracker.TestItemState>("state", Type.ToOneRelationship);

  // Attributes
  public static final String COMMENTS_KEY = COMMENTS.key();
  public static final String CONTROLLED_KEY = CONTROLLED.key();
  public static final String DATE_CREATED_KEY = DATE_CREATED.key();
  public static final String ID_KEY = ID.key();
  public static final String TEXT_DESCRIPTION_KEY = TEXT_DESCRIPTION.key();
  public static final String TITLE_KEY = TITLE.key();

  // Relationships
  public static final String BUGS_KEY = BUGS.key();
  public static final String COMPONENT_KEY = COMPONENT.key();
  public static final String OWNER_KEY = OWNER.key();
  public static final String REQUIREMENTS_KEY = REQUIREMENTS.key();
  public static final String STATE_KEY = STATE.key();

  private static final Logger log = LoggerFactory.getLogger(_TestItem.class);

  public TestItem localInstanceIn(EOEditingContext editingContext) {
    TestItem localInstance = (TestItem)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String comments() {
    return (String) storedValueForKey(_TestItem.COMMENTS_KEY);
  }

  public void setComments(String value) {
    log.debug( "updating comments from {} to {}", comments(), value);
    takeStoredValueForKey(value, _TestItem.COMMENTS_KEY);
  }

  public String controlled() {
    return (String) storedValueForKey(_TestItem.CONTROLLED_KEY);
  }

  public void setControlled(String value) {
    log.debug( "updating controlled from {} to {}", controlled(), value);
    takeStoredValueForKey(value, _TestItem.CONTROLLED_KEY);
  }

  public NSTimestamp dateCreated() {
    return (NSTimestamp) storedValueForKey(_TestItem.DATE_CREATED_KEY);
  }

  public void setDateCreated(NSTimestamp value) {
    log.debug( "updating dateCreated from {} to {}", dateCreated(), value);
    takeStoredValueForKey(value, _TestItem.DATE_CREATED_KEY);
  }

  public Integer id() {
    return (Integer) storedValueForKey(_TestItem.ID_KEY);
  }

  public void setId(Integer value) {
    log.debug( "updating id from {} to {}", id(), value);
    takeStoredValueForKey(value, _TestItem.ID_KEY);
  }

  public String textDescription() {
    return (String) storedValueForKey(_TestItem.TEXT_DESCRIPTION_KEY);
  }

  public void setTextDescription(String value) {
    log.debug( "updating textDescription from {} to {}", textDescription(), value);
    takeStoredValueForKey(value, _TestItem.TEXT_DESCRIPTION_KEY);
  }

  public String title() {
    return (String) storedValueForKey(_TestItem.TITLE_KEY);
  }

  public void setTitle(String value) {
    log.debug( "updating title from {} to {}", title(), value);
    takeStoredValueForKey(value, _TestItem.TITLE_KEY);
  }

  public er.bugtracker.Component component() {
    return (er.bugtracker.Component)storedValueForKey(_TestItem.COMPONENT_KEY);
  }

  public void setComponent(er.bugtracker.Component value) {
    takeStoredValueForKey(value, _TestItem.COMPONENT_KEY);
  }

  public void setComponentRelationship(er.bugtracker.Component value) {
    log.debug("updating component from {} to {}", component(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setComponent(value);
    }
    else if (value == null) {
      er.bugtracker.Component oldValue = component();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _TestItem.COMPONENT_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _TestItem.COMPONENT_KEY);
    }
  }

  public er.bugtracker.People owner() {
    return (er.bugtracker.People)storedValueForKey(_TestItem.OWNER_KEY);
  }

  public void setOwner(er.bugtracker.People value) {
    takeStoredValueForKey(value, _TestItem.OWNER_KEY);
  }

  public void setOwnerRelationship(er.bugtracker.People value) {
    log.debug("updating owner from {} to {}", owner(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setOwner(value);
    }
    else if (value == null) {
      er.bugtracker.People oldValue = owner();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _TestItem.OWNER_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _TestItem.OWNER_KEY);
    }
  }

  public er.bugtracker.TestItemState state() {
    return (er.bugtracker.TestItemState)storedValueForKey(_TestItem.STATE_KEY);
  }

  public void setState(er.bugtracker.TestItemState value) {
    takeStoredValueForKey(value, _TestItem.STATE_KEY);
  }

  public void setStateRelationship(er.bugtracker.TestItemState value) {
    log.debug("updating state from {} to {}", state(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setState(value);
    }
    else if (value == null) {
      er.bugtracker.TestItemState oldValue = state();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _TestItem.STATE_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _TestItem.STATE_KEY);
    }
  }

  public NSArray<er.bugtracker.Bug> bugs() {
    return (NSArray<er.bugtracker.Bug>)storedValueForKey(_TestItem.BUGS_KEY);
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

  public void addToBugs(er.bugtracker.Bug object) {
    includeObjectIntoPropertyWithKey(object, _TestItem.BUGS_KEY);
  }

  public void removeFromBugs(er.bugtracker.Bug object) {
    excludeObjectFromPropertyWithKey(object, _TestItem.BUGS_KEY);
  }

  public void addToBugsRelationship(er.bugtracker.Bug object) {
    log.debug("adding {} to bugs relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToBugs(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _TestItem.BUGS_KEY);
    }
  }

  public void removeFromBugsRelationship(er.bugtracker.Bug object) {
    log.debug("removing {} from bugs relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromBugs(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _TestItem.BUGS_KEY);
    }
  }

  public er.bugtracker.Bug createBugsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.bugtracker.Bug.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _TestItem.BUGS_KEY);
    return (er.bugtracker.Bug) eo;
  }

  public void deleteBugsRelationship(er.bugtracker.Bug object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _TestItem.BUGS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllBugsRelationships() {
    Enumeration<er.bugtracker.Bug> objects = bugs().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteBugsRelationship(objects.nextElement());
    }
  }

  public NSArray<er.bugtracker.Requirement> requirements() {
    return (NSArray<er.bugtracker.Requirement>)storedValueForKey(_TestItem.REQUIREMENTS_KEY);
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

  public void addToRequirements(er.bugtracker.Requirement object) {
    includeObjectIntoPropertyWithKey(object, _TestItem.REQUIREMENTS_KEY);
  }

  public void removeFromRequirements(er.bugtracker.Requirement object) {
    excludeObjectFromPropertyWithKey(object, _TestItem.REQUIREMENTS_KEY);
  }

  public void addToRequirementsRelationship(er.bugtracker.Requirement object) {
    log.debug("adding {} to requirements relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToRequirements(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _TestItem.REQUIREMENTS_KEY);
    }
  }

  public void removeFromRequirementsRelationship(er.bugtracker.Requirement object) {
    log.debug("removing {} from requirements relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromRequirements(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _TestItem.REQUIREMENTS_KEY);
    }
  }

  public er.bugtracker.Requirement createRequirementsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.bugtracker.Requirement.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _TestItem.REQUIREMENTS_KEY);
    return (er.bugtracker.Requirement) eo;
  }

  public void deleteRequirementsRelationship(er.bugtracker.Requirement object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _TestItem.REQUIREMENTS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllRequirementsRelationships() {
    Enumeration<er.bugtracker.Requirement> objects = requirements().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteRequirementsRelationship(objects.nextElement());
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

  public static ERXFetchSpecification<TestItem> fetchSpec() {
    return new ERXFetchSpecification<TestItem>(_TestItem.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<TestItem> fetchAllTestItems(EOEditingContext editingContext) {
    return _TestItem.fetchAllTestItems(editingContext, null);
  }

  public static NSArray<TestItem> fetchAllTestItems(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _TestItem.fetchTestItems(editingContext, null, sortOrderings);
  }

  public static NSArray<TestItem> fetchTestItems(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<TestItem> fetchSpec = new ERXFetchSpecification<TestItem>(_TestItem.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<TestItem> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static TestItem fetchTestItem(EOEditingContext editingContext, String keyName, Object value) {
    return _TestItem.fetchTestItem(editingContext, ERXQ.equals(keyName, value));
  }

  public static TestItem fetchTestItem(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<TestItem> eoObjects = _TestItem.fetchTestItems(editingContext, qualifier, null);
    TestItem eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one TestItem that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static TestItem fetchRequiredTestItem(EOEditingContext editingContext, String keyName, Object value) {
    return _TestItem.fetchRequiredTestItem(editingContext, ERXQ.equals(keyName, value));
  }

  public static TestItem fetchRequiredTestItem(EOEditingContext editingContext, EOQualifier qualifier) {
    TestItem eoObject = _TestItem.fetchTestItem(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no TestItem that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static TestItem localInstanceIn(EOEditingContext editingContext, TestItem eo) {
    TestItem localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

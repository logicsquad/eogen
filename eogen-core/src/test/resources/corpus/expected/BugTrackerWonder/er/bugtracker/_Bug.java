// DO NOT EDIT.  Make changes to Bug.java instead.
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
public abstract class _Bug extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Bug";

  // Attribute Keys
  public static final ERXKey<NSTimestamp> DATE_MODIFIED = new ERXKey<NSTimestamp>("dateModified", Type.Attribute);
  public static final ERXKey<NSTimestamp> DATE_SUBMITTED = new ERXKey<NSTimestamp>("dateSubmitted", Type.Attribute);
  public static final ERXKey<Boolean> IS_FEATURE_REQUEST = new ERXKey<Boolean>("isFeatureRequest", Type.Attribute);
  public static final ERXKey<Boolean> IS_READ = new ERXKey<Boolean>("isRead", Type.Attribute);
  public static final ERXKey<er.bugtracker.State> STATE = new ERXKey<er.bugtracker.State>("state", Type.Attribute);
  public static final ERXKey<String> SUBJECT = new ERXKey<String>("subject", Type.Attribute);
  public static final ERXKey<String> TYPE = new ERXKey<String>("type", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.bugtracker.Comment> COMMENTS = new ERXKey<er.bugtracker.Comment>("comments", Type.ToManyRelationship);
  public static final ERXKey<er.bugtracker.Component> COMPONENT = new ERXKey<er.bugtracker.Component>("component", Type.ToOneRelationship);
  public static final ERXKey<er.bugtracker.People> ORIGINATOR = new ERXKey<er.bugtracker.People>("originator", Type.ToOneRelationship);
  public static final ERXKey<er.bugtracker.People> OWNER = new ERXKey<er.bugtracker.People>("owner", Type.ToOneRelationship);
  public static final ERXKey<er.bugtracker.People> PREVIOUS_OWNER = new ERXKey<er.bugtracker.People>("previousOwner", Type.ToOneRelationship);
  public static final ERXKey<er.bugtracker.Priority> PRIORITY = new ERXKey<er.bugtracker.Priority>("priority", Type.ToOneRelationship);
  public static final ERXKey<er.bugtracker.Release> TARGET_RELEASE = new ERXKey<er.bugtracker.Release>("targetRelease", Type.ToOneRelationship);
  public static final ERXKey<er.bugtracker.TestItem> TEST_ITEMS = new ERXKey<er.bugtracker.TestItem>("testItems", Type.ToManyRelationship);

  // Attributes
  public static final String DATE_MODIFIED_KEY = DATE_MODIFIED.key();
  public static final String DATE_SUBMITTED_KEY = DATE_SUBMITTED.key();
  public static final String IS_FEATURE_REQUEST_KEY = IS_FEATURE_REQUEST.key();
  public static final String IS_READ_KEY = IS_READ.key();
  public static final String STATE_KEY = STATE.key();
  public static final String SUBJECT_KEY = SUBJECT.key();
  public static final String TYPE_KEY = TYPE.key();

  // Relationships
  public static final String COMMENTS_KEY = COMMENTS.key();
  public static final String COMPONENT_KEY = COMPONENT.key();
  public static final String ORIGINATOR_KEY = ORIGINATOR.key();
  public static final String OWNER_KEY = OWNER.key();
  public static final String PREVIOUS_OWNER_KEY = PREVIOUS_OWNER.key();
  public static final String PRIORITY_KEY = PRIORITY.key();
  public static final String TARGET_RELEASE_KEY = TARGET_RELEASE.key();
  public static final String TEST_ITEMS_KEY = TEST_ITEMS.key();

  private static final Logger log = LoggerFactory.getLogger(_Bug.class);

  public Bug localInstanceIn(EOEditingContext editingContext) {
    Bug localInstance = (Bug)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSTimestamp dateModified() {
    return (NSTimestamp) storedValueForKey(_Bug.DATE_MODIFIED_KEY);
  }

  public void setDateModified(NSTimestamp value) {
    log.debug( "updating dateModified from {} to {}", dateModified(), value);
    takeStoredValueForKey(value, _Bug.DATE_MODIFIED_KEY);
  }

  public NSTimestamp dateSubmitted() {
    return (NSTimestamp) storedValueForKey(_Bug.DATE_SUBMITTED_KEY);
  }

  public void setDateSubmitted(NSTimestamp value) {
    log.debug( "updating dateSubmitted from {} to {}", dateSubmitted(), value);
    takeStoredValueForKey(value, _Bug.DATE_SUBMITTED_KEY);
  }

  public Boolean isFeatureRequest() {
    return (Boolean) storedValueForKey(_Bug.IS_FEATURE_REQUEST_KEY);
  }

  public void setIsFeatureRequest(Boolean value) {
    log.debug( "updating isFeatureRequest from {} to {}", isFeatureRequest(), value);
    takeStoredValueForKey(value, _Bug.IS_FEATURE_REQUEST_KEY);
  }

  public Boolean isRead() {
    return (Boolean) storedValueForKey(_Bug.IS_READ_KEY);
  }

  public void setIsRead(Boolean value) {
    log.debug( "updating isRead from {} to {}", isRead(), value);
    takeStoredValueForKey(value, _Bug.IS_READ_KEY);
  }

  public er.bugtracker.State state() {
    return (er.bugtracker.State) storedValueForKey(_Bug.STATE_KEY);
  }

  public void setState(er.bugtracker.State value) {
    log.debug( "updating state from {} to {}", state(), value);
    takeStoredValueForKey(value, _Bug.STATE_KEY);
  }

  public String subject() {
    return (String) storedValueForKey(_Bug.SUBJECT_KEY);
  }

  public void setSubject(String value) {
    log.debug( "updating subject from {} to {}", subject(), value);
    takeStoredValueForKey(value, _Bug.SUBJECT_KEY);
  }

  public String type() {
    return (String) storedValueForKey(_Bug.TYPE_KEY);
  }

  public void setType(String value) {
    log.debug( "updating type from {} to {}", type(), value);
    takeStoredValueForKey(value, _Bug.TYPE_KEY);
  }

  public er.bugtracker.Component component() {
    return (er.bugtracker.Component)storedValueForKey(_Bug.COMPONENT_KEY);
  }

  public void setComponent(er.bugtracker.Component value) {
    takeStoredValueForKey(value, _Bug.COMPONENT_KEY);
  }

  public void setComponentRelationship(er.bugtracker.Component value) {
    log.debug("updating component from {} to {}", component(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setComponent(value);
    }
    else if (value == null) {
      er.bugtracker.Component oldValue = component();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Bug.COMPONENT_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Bug.COMPONENT_KEY);
    }
  }

  public er.bugtracker.People originator() {
    return (er.bugtracker.People)storedValueForKey(_Bug.ORIGINATOR_KEY);
  }

  public void setOriginator(er.bugtracker.People value) {
    takeStoredValueForKey(value, _Bug.ORIGINATOR_KEY);
  }

  public void setOriginatorRelationship(er.bugtracker.People value) {
    log.debug("updating originator from {} to {}", originator(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setOriginator(value);
    }
    else if (value == null) {
      er.bugtracker.People oldValue = originator();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Bug.ORIGINATOR_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Bug.ORIGINATOR_KEY);
    }
  }

  public er.bugtracker.People owner() {
    return (er.bugtracker.People)storedValueForKey(_Bug.OWNER_KEY);
  }

  public void setOwner(er.bugtracker.People value) {
    takeStoredValueForKey(value, _Bug.OWNER_KEY);
  }

  public void setOwnerRelationship(er.bugtracker.People value) {
    log.debug("updating owner from {} to {}", owner(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setOwner(value);
    }
    else if (value == null) {
      er.bugtracker.People oldValue = owner();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Bug.OWNER_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Bug.OWNER_KEY);
    }
  }

  public er.bugtracker.People previousOwner() {
    return (er.bugtracker.People)storedValueForKey(_Bug.PREVIOUS_OWNER_KEY);
  }

  public void setPreviousOwner(er.bugtracker.People value) {
    takeStoredValueForKey(value, _Bug.PREVIOUS_OWNER_KEY);
  }

  public void setPreviousOwnerRelationship(er.bugtracker.People value) {
    log.debug("updating previousOwner from {} to {}", previousOwner(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setPreviousOwner(value);
    }
    else if (value == null) {
      er.bugtracker.People oldValue = previousOwner();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Bug.PREVIOUS_OWNER_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Bug.PREVIOUS_OWNER_KEY);
    }
  }

  public er.bugtracker.Priority priority() {
    return (er.bugtracker.Priority)storedValueForKey(_Bug.PRIORITY_KEY);
  }

  public void setPriority(er.bugtracker.Priority value) {
    takeStoredValueForKey(value, _Bug.PRIORITY_KEY);
  }

  public void setPriorityRelationship(er.bugtracker.Priority value) {
    log.debug("updating priority from {} to {}", priority(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setPriority(value);
    }
    else if (value == null) {
      er.bugtracker.Priority oldValue = priority();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Bug.PRIORITY_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Bug.PRIORITY_KEY);
    }
  }

  public er.bugtracker.Release targetRelease() {
    return (er.bugtracker.Release)storedValueForKey(_Bug.TARGET_RELEASE_KEY);
  }

  public void setTargetRelease(er.bugtracker.Release value) {
    takeStoredValueForKey(value, _Bug.TARGET_RELEASE_KEY);
  }

  public void setTargetReleaseRelationship(er.bugtracker.Release value) {
    log.debug("updating targetRelease from {} to {}", targetRelease(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setTargetRelease(value);
    }
    else if (value == null) {
      er.bugtracker.Release oldValue = targetRelease();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Bug.TARGET_RELEASE_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Bug.TARGET_RELEASE_KEY);
    }
  }

  public NSArray<er.bugtracker.Comment> comments() {
    return (NSArray<er.bugtracker.Comment>)storedValueForKey(_Bug.COMMENTS_KEY);
  }

  public NSArray<er.bugtracker.Comment> comments(EOQualifier qualifier) {
    return comments(qualifier, null, false);
  }

  public NSArray<er.bugtracker.Comment> comments(EOQualifier qualifier, boolean fetch) {
    return comments(qualifier, null, fetch);
  }

  public NSArray<er.bugtracker.Comment> comments(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.bugtracker.Comment> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(er.bugtracker.Comment.BUG_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = er.bugtracker.Comment.fetchComments(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = comments();
      if (qualifier != null) {
        results = (NSArray<er.bugtracker.Comment>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.bugtracker.Comment>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToComments(er.bugtracker.Comment object) {
    includeObjectIntoPropertyWithKey(object, _Bug.COMMENTS_KEY);
  }

  public void removeFromComments(er.bugtracker.Comment object) {
    excludeObjectFromPropertyWithKey(object, _Bug.COMMENTS_KEY);
  }

  public void addToCommentsRelationship(er.bugtracker.Comment object) {
    log.debug("adding {} to comments relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToComments(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Bug.COMMENTS_KEY);
    }
  }

  public void removeFromCommentsRelationship(er.bugtracker.Comment object) {
    log.debug("removing {} from comments relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromComments(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Bug.COMMENTS_KEY);
    }
  }

  public er.bugtracker.Comment createCommentsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.bugtracker.Comment.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Bug.COMMENTS_KEY);
    return (er.bugtracker.Comment) eo;
  }

  public void deleteCommentsRelationship(er.bugtracker.Comment object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Bug.COMMENTS_KEY);
  }

  public void deleteAllCommentsRelationships() {
    Enumeration<er.bugtracker.Comment> objects = comments().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteCommentsRelationship(objects.nextElement());
    }
  }

  public NSArray<er.bugtracker.TestItem> testItems() {
    return (NSArray<er.bugtracker.TestItem>)storedValueForKey(_Bug.TEST_ITEMS_KEY);
  }

  public NSArray<er.bugtracker.TestItem> testItems(EOQualifier qualifier) {
    return testItems(qualifier, null);
  }

  public NSArray<er.bugtracker.TestItem> testItems(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    NSArray<er.bugtracker.TestItem> results;
      results = testItems();
      if (qualifier != null) {
        results = (NSArray<er.bugtracker.TestItem>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.bugtracker.TestItem>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    return results;
  }

  public void addToTestItems(er.bugtracker.TestItem object) {
    includeObjectIntoPropertyWithKey(object, _Bug.TEST_ITEMS_KEY);
  }

  public void removeFromTestItems(er.bugtracker.TestItem object) {
    excludeObjectFromPropertyWithKey(object, _Bug.TEST_ITEMS_KEY);
  }

  public void addToTestItemsRelationship(er.bugtracker.TestItem object) {
    log.debug("adding {} to testItems relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToTestItems(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Bug.TEST_ITEMS_KEY);
    }
  }

  public void removeFromTestItemsRelationship(er.bugtracker.TestItem object) {
    log.debug("removing {} from testItems relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromTestItems(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Bug.TEST_ITEMS_KEY);
    }
  }

  public er.bugtracker.TestItem createTestItemsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.bugtracker.TestItem.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Bug.TEST_ITEMS_KEY);
    return (er.bugtracker.TestItem) eo;
  }

  public void deleteTestItemsRelationship(er.bugtracker.TestItem object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Bug.TEST_ITEMS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllTestItemsRelationships() {
    Enumeration<er.bugtracker.TestItem> objects = testItems().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteTestItemsRelationship(objects.nextElement());
    }
  }


  public static Bug createBug(EOEditingContext editingContext, Boolean isFeatureRequest
, Boolean isRead
, er.bugtracker.State state
, String subject
, er.bugtracker.Component component, er.bugtracker.People originator, er.bugtracker.People owner, er.bugtracker.Priority priority, er.bugtracker.Release targetRelease) {
    Bug eo = (Bug) EOUtilities.createAndInsertInstance(editingContext, _Bug.ENTITY_NAME);
    eo.setIsFeatureRequest(isFeatureRequest);
    eo.setIsRead(isRead);
    eo.setState(state);
    eo.setSubject(subject);
    eo.setComponentRelationship(component);
    eo.setOriginatorRelationship(originator);
    eo.setOwnerRelationship(owner);
    eo.setPriorityRelationship(priority);
    eo.setTargetReleaseRelationship(targetRelease);
    return eo;
  }

  public static ERXFetchSpecification<Bug> fetchSpec() {
    return new ERXFetchSpecification<Bug>(_Bug.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Bug> fetchAllBugs(EOEditingContext editingContext) {
    return _Bug.fetchAllBugs(editingContext, null);
  }

  public static NSArray<Bug> fetchAllBugs(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Bug.fetchBugs(editingContext, null, sortOrderings);
  }

  public static NSArray<Bug> fetchBugs(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Bug> fetchSpec = new ERXFetchSpecification<Bug>(_Bug.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Bug> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Bug fetchBug(EOEditingContext editingContext, String keyName, Object value) {
    return _Bug.fetchBug(editingContext, ERXQ.equals(keyName, value));
  }

  public static Bug fetchBug(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Bug> eoObjects = _Bug.fetchBugs(editingContext, qualifier, null);
    Bug eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Bug that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Bug fetchRequiredBug(EOEditingContext editingContext, String keyName, Object value) {
    return _Bug.fetchRequiredBug(editingContext, ERXQ.equals(keyName, value));
  }

  public static Bug fetchRequiredBug(EOEditingContext editingContext, EOQualifier qualifier) {
    Bug eoObject = _Bug.fetchBug(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Bug that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Bug localInstanceIn(EOEditingContext editingContext, Bug eo) {
    Bug localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

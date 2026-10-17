// $LastChangedRevision$ DO NOT EDIT.  Make changes to Bug.java instead.
package er.bugtracker;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Bug extends er.extensions.eof.ERXGenericRecord {
	public static final String ENTITY_NAME = "Bug";

	// Attributes
	public static final String DATE_MODIFIED_KEY = "dateModified";
	public static final String DATE_SUBMITTED_KEY = "dateSubmitted";
	public static final String IS_FEATURE_REQUEST_KEY = "isFeatureRequest";
	public static final String IS_READ_KEY = "isRead";
	public static final String STATE_KEY = "state";
	public static final String SUBJECT_KEY = "subject";
	public static final String TYPE_KEY = "type";

	// Relationships
	public static final String COMMENTS_KEY = "comments";
	public static final String COMPONENT_KEY = "component";
	public static final String ORIGINATOR_KEY = "originator";
	public static final String OWNER_KEY = "owner";
	public static final String PREVIOUS_OWNER_KEY = "previousOwner";
	public static final String PRIORITY_KEY = "priority";
	public static final String TARGET_RELEASE_KEY = "targetRelease";
	public static final String TEST_ITEMS_KEY = "testItems";

  private static Logger LOG = Logger.getLogger(_Bug.class);

  public Bug localInstanceIn(EOEditingContext editingContext) {
    Bug localInstance = (Bug)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSTimestamp dateModified() {
    return (NSTimestamp) storedValueForKey("dateModified");
  }

  public void setDateModified(NSTimestamp value) {
    if (_Bug.LOG.isDebugEnabled()) {
    	_Bug.LOG.debug( "updating dateModified from " + dateModified() + " to " + value);
    }
    takeStoredValueForKey(value, "dateModified");
  }

  public NSTimestamp dateSubmitted() {
    return (NSTimestamp) storedValueForKey("dateSubmitted");
  }

  public void setDateSubmitted(NSTimestamp value) {
    if (_Bug.LOG.isDebugEnabled()) {
    	_Bug.LOG.debug( "updating dateSubmitted from " + dateSubmitted() + " to " + value);
    }
    takeStoredValueForKey(value, "dateSubmitted");
  }

  public Boolean isFeatureRequest() {
    return (Boolean) storedValueForKey("isFeatureRequest");
  }

  public void setIsFeatureRequest(Boolean value) {
    if (_Bug.LOG.isDebugEnabled()) {
    	_Bug.LOG.debug( "updating isFeatureRequest from " + isFeatureRequest() + " to " + value);
    }
    takeStoredValueForKey(value, "isFeatureRequest");
  }

  public Boolean isRead() {
    return (Boolean) storedValueForKey("isRead");
  }

  public void setIsRead(Boolean value) {
    if (_Bug.LOG.isDebugEnabled()) {
    	_Bug.LOG.debug( "updating isRead from " + isRead() + " to " + value);
    }
    takeStoredValueForKey(value, "isRead");
  }

  public er.bugtracker.State state() {
    return (er.bugtracker.State) storedValueForKey("state");
  }

  public void setState(er.bugtracker.State value) {
    if (_Bug.LOG.isDebugEnabled()) {
    	_Bug.LOG.debug( "updating state from " + state() + " to " + value);
    }
    takeStoredValueForKey(value, "state");
  }

  public String subject() {
    return (String) storedValueForKey("subject");
  }

  public void setSubject(String value) {
    if (_Bug.LOG.isDebugEnabled()) {
    	_Bug.LOG.debug( "updating subject from " + subject() + " to " + value);
    }
    takeStoredValueForKey(value, "subject");
  }

  public String type() {
    return (String) storedValueForKey("type");
  }

  public void setType(String value) {
    if (_Bug.LOG.isDebugEnabled()) {
    	_Bug.LOG.debug( "updating type from " + type() + " to " + value);
    }
    takeStoredValueForKey(value, "type");
  }

  public er.bugtracker.Component component() {
    return (er.bugtracker.Component)storedValueForKey("component");
  }

  public void setComponentRelationship(er.bugtracker.Component value) {
    if (_Bug.LOG.isDebugEnabled()) {
      _Bug.LOG.debug("updating component from " + component() + " to " + value);
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
  
  public er.bugtracker.People originator() {
    return (er.bugtracker.People)storedValueForKey("originator");
  }

  public void setOriginatorRelationship(er.bugtracker.People value) {
    if (_Bug.LOG.isDebugEnabled()) {
      _Bug.LOG.debug("updating originator from " + originator() + " to " + value);
    }
    if (value == null) {
    	er.bugtracker.People oldValue = originator();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "originator");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "originator");
    }
  }
  
  public er.bugtracker.People owner() {
    return (er.bugtracker.People)storedValueForKey("owner");
  }

  public void setOwnerRelationship(er.bugtracker.People value) {
    if (_Bug.LOG.isDebugEnabled()) {
      _Bug.LOG.debug("updating owner from " + owner() + " to " + value);
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
  
  public er.bugtracker.People previousOwner() {
    return (er.bugtracker.People)storedValueForKey("previousOwner");
  }

  public void setPreviousOwnerRelationship(er.bugtracker.People value) {
    if (_Bug.LOG.isDebugEnabled()) {
      _Bug.LOG.debug("updating previousOwner from " + previousOwner() + " to " + value);
    }
    if (value == null) {
    	er.bugtracker.People oldValue = previousOwner();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "previousOwner");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "previousOwner");
    }
  }
  
  public er.bugtracker.Priority priority() {
    return (er.bugtracker.Priority)storedValueForKey("priority");
  }

  public void setPriorityRelationship(er.bugtracker.Priority value) {
    if (_Bug.LOG.isDebugEnabled()) {
      _Bug.LOG.debug("updating priority from " + priority() + " to " + value);
    }
    if (value == null) {
    	er.bugtracker.Priority oldValue = priority();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "priority");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "priority");
    }
  }
  
  public er.bugtracker.Release targetRelease() {
    return (er.bugtracker.Release)storedValueForKey("targetRelease");
  }

  public void setTargetReleaseRelationship(er.bugtracker.Release value) {
    if (_Bug.LOG.isDebugEnabled()) {
      _Bug.LOG.debug("updating targetRelease from " + targetRelease() + " to " + value);
    }
    if (value == null) {
    	er.bugtracker.Release oldValue = targetRelease();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "targetRelease");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "targetRelease");
    }
  }
  
  public NSArray<er.bugtracker.Comment> comments() {
    return (NSArray<er.bugtracker.Comment>)storedValueForKey("comments");
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
      EOQualifier inverseQualifier = new EOKeyValueQualifier(er.bugtracker.Comment.BUG_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        NSMutableArray qualifiers = new NSMutableArray();
        qualifiers.addObject(qualifier);
        qualifiers.addObject(inverseQualifier);
        fullQualifier = new EOAndQualifier(qualifiers);
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
  
  public void addToCommentsRelationship(er.bugtracker.Comment object) {
    if (_Bug.LOG.isDebugEnabled()) {
      _Bug.LOG.debug("adding " + object + " to comments relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "comments");
  }

  public void removeFromCommentsRelationship(er.bugtracker.Comment object) {
    if (_Bug.LOG.isDebugEnabled()) {
      _Bug.LOG.debug("removing " + object + " from comments relationship");
    }
    removeObjectFromBothSidesOfRelationshipWithKey(object, "comments");
  }

  public er.bugtracker.Comment createCommentsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Comment");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "comments");
    return (er.bugtracker.Comment) eo;
  }

  public void deleteCommentsRelationship(er.bugtracker.Comment object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "comments");
  }

  public void deleteAllCommentsRelationships() {
    Enumeration objects = comments().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteCommentsRelationship((er.bugtracker.Comment)objects.nextElement());
    }
  }

  public NSArray<er.bugtracker.TestItem> testItems() {
    return (NSArray<er.bugtracker.TestItem>)storedValueForKey("testItems");
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
  
  public void addToTestItemsRelationship(er.bugtracker.TestItem object) {
    if (_Bug.LOG.isDebugEnabled()) {
      _Bug.LOG.debug("adding " + object + " to testItems relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "testItems");
  }

  public void removeFromTestItemsRelationship(er.bugtracker.TestItem object) {
    if (_Bug.LOG.isDebugEnabled()) {
      _Bug.LOG.debug("removing " + object + " from testItems relationship");
    }
    removeObjectFromBothSidesOfRelationshipWithKey(object, "testItems");
  }

  public er.bugtracker.TestItem createTestItemsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("TestItem");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "testItems");
    return (er.bugtracker.TestItem) eo;
  }

  public void deleteTestItemsRelationship(er.bugtracker.TestItem object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "testItems");
    editingContext().deleteObject(object);
  }

  public void deleteAllTestItemsRelationships() {
    Enumeration objects = testItems().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteTestItemsRelationship((er.bugtracker.TestItem)objects.nextElement());
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

  public static NSArray<Bug> fetchAllBugs(EOEditingContext editingContext) {
    return _Bug.fetchAllBugs(editingContext, null);
  }

  public static NSArray<Bug> fetchAllBugs(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Bug.fetchBugs(editingContext, null, sortOrderings);
  }

  public static NSArray<Bug> fetchBugs(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Bug.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Bug> eoObjects = (NSArray<Bug>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static Bug fetchBug(EOEditingContext editingContext, String keyName, Object value) {
    return _Bug.fetchBug(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Bug fetchBug(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Bug> eoObjects = _Bug.fetchBugs(editingContext, qualifier, null);
    Bug eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (Bug)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Bug that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Bug fetchRequiredBug(EOEditingContext editingContext, String keyName, Object value) {
    return _Bug.fetchRequiredBug(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Bug fetchRequiredBug(EOEditingContext editingContext, EOQualifier qualifier) {
    Bug eoObject = _Bug.fetchBug(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Bug that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Bug localInstanceIn(EOEditingContext editingContext, Bug eo) {
    Bug localInstance = (eo == null) ? null : (Bug)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

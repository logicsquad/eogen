// $LastChangedRevision$ DO NOT EDIT.  Make changes to Comment.java instead.
package er.bugtracker;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Comment extends er.extensions.eof.ERXGenericRecord {
	public static final String ENTITY_NAME = "Comment";

	// Attributes
	public static final String DATE_SUBMITTED_KEY = "dateSubmitted";
	public static final String TEXT_DESCRIPTION_KEY = "textDescription";

	// Relationships
	public static final String ATTACHMENTS_KEY = "attachments";
	public static final String BUG_KEY = "bug";
	public static final String ORIGINATOR_KEY = "originator";
	public static final String PARENT_KEY = "parent";

  private static Logger LOG = Logger.getLogger(_Comment.class);

  public Comment localInstanceIn(EOEditingContext editingContext) {
    Comment localInstance = (Comment)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSTimestamp dateSubmitted() {
    return (NSTimestamp) storedValueForKey("dateSubmitted");
  }

  public void setDateSubmitted(NSTimestamp value) {
    if (_Comment.LOG.isDebugEnabled()) {
    	_Comment.LOG.debug( "updating dateSubmitted from " + dateSubmitted() + " to " + value);
    }
    takeStoredValueForKey(value, "dateSubmitted");
  }

  public String textDescription() {
    return (String) storedValueForKey("textDescription");
  }

  public void setTextDescription(String value) {
    if (_Comment.LOG.isDebugEnabled()) {
    	_Comment.LOG.debug( "updating textDescription from " + textDescription() + " to " + value);
    }
    takeStoredValueForKey(value, "textDescription");
  }

  public er.bugtracker.Bug bug() {
    return (er.bugtracker.Bug)storedValueForKey("bug");
  }

  public void setBugRelationship(er.bugtracker.Bug value) {
    if (_Comment.LOG.isDebugEnabled()) {
      _Comment.LOG.debug("updating bug from " + bug() + " to " + value);
    }
    if (value == null) {
    	er.bugtracker.Bug oldValue = bug();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "bug");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "bug");
    }
  }
  
  public er.bugtracker.People originator() {
    return (er.bugtracker.People)storedValueForKey("originator");
  }

  public void setOriginatorRelationship(er.bugtracker.People value) {
    if (_Comment.LOG.isDebugEnabled()) {
      _Comment.LOG.debug("updating originator from " + originator() + " to " + value);
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
  
  public er.bugtracker.Comment parent() {
    return (er.bugtracker.Comment)storedValueForKey("parent");
  }

  public void setParentRelationship(er.bugtracker.Comment value) {
    if (_Comment.LOG.isDebugEnabled()) {
      _Comment.LOG.debug("updating parent from " + parent() + " to " + value);
    }
    if (value == null) {
    	er.bugtracker.Comment oldValue = parent();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "parent");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "parent");
    }
  }
  
  public NSArray<er.attachment.model.ERAttachment> attachments() {
    return (NSArray<er.attachment.model.ERAttachment>)storedValueForKey("attachments");
  }

  public NSArray<er.attachment.model.ERAttachment> attachments(EOQualifier qualifier) {
    return attachments(qualifier, null);
  }

  public NSArray<er.attachment.model.ERAttachment> attachments(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    NSArray<er.attachment.model.ERAttachment> results;
      results = attachments();
      if (qualifier != null) {
        results = (NSArray<er.attachment.model.ERAttachment>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.attachment.model.ERAttachment>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    return results;
  }
  
  public void addToAttachmentsRelationship(er.attachment.model.ERAttachment object) {
    if (_Comment.LOG.isDebugEnabled()) {
      _Comment.LOG.debug("adding " + object + " to attachments relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "attachments");
  }

  public void removeFromAttachmentsRelationship(er.attachment.model.ERAttachment object) {
    if (_Comment.LOG.isDebugEnabled()) {
      _Comment.LOG.debug("removing " + object + " from attachments relationship");
    }
    removeObjectFromBothSidesOfRelationshipWithKey(object, "attachments");
  }

  public er.attachment.model.ERAttachment createAttachmentsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("ERAttachment");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "attachments");
    return (er.attachment.model.ERAttachment) eo;
  }

  public void deleteAttachmentsRelationship(er.attachment.model.ERAttachment object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "attachments");
    editingContext().deleteObject(object);
  }

  public void deleteAllAttachmentsRelationships() {
    Enumeration objects = attachments().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteAttachmentsRelationship((er.attachment.model.ERAttachment)objects.nextElement());
    }
  }


  public static Comment createComment(EOEditingContext editingContext, er.bugtracker.Bug bug, er.bugtracker.People originator) {
    Comment eo = (Comment) EOUtilities.createAndInsertInstance(editingContext, _Comment.ENTITY_NAME);    
    eo.setBugRelationship(bug);
    eo.setOriginatorRelationship(originator);
    return eo;
  }

  public static NSArray<Comment> fetchAllComments(EOEditingContext editingContext) {
    return _Comment.fetchAllComments(editingContext, null);
  }

  public static NSArray<Comment> fetchAllComments(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Comment.fetchComments(editingContext, null, sortOrderings);
  }

  public static NSArray<Comment> fetchComments(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Comment.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Comment> eoObjects = (NSArray<Comment>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static Comment fetchComment(EOEditingContext editingContext, String keyName, Object value) {
    return _Comment.fetchComment(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Comment fetchComment(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Comment> eoObjects = _Comment.fetchComments(editingContext, qualifier, null);
    Comment eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (Comment)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Comment that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Comment fetchRequiredComment(EOEditingContext editingContext, String keyName, Object value) {
    return _Comment.fetchRequiredComment(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Comment fetchRequiredComment(EOEditingContext editingContext, EOQualifier qualifier) {
    Comment eoObject = _Comment.fetchComment(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Comment that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Comment localInstanceIn(EOEditingContext editingContext, Comment eo) {
    Comment localInstance = (eo == null) ? null : (Comment)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

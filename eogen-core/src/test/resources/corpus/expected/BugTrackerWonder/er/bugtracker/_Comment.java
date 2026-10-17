// DO NOT EDIT.  Make changes to Comment.java instead.
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
public abstract class _Comment extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Comment";

  // Attribute Keys
  public static final ERXKey<NSTimestamp> DATE_SUBMITTED = new ERXKey<NSTimestamp>("dateSubmitted", Type.Attribute);
  public static final ERXKey<String> TEXT_DESCRIPTION = new ERXKey<String>("textDescription", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.attachment.model.ERAttachment> ATTACHMENTS = new ERXKey<er.attachment.model.ERAttachment>("attachments", Type.ToManyRelationship);
  public static final ERXKey<er.bugtracker.Bug> BUG = new ERXKey<er.bugtracker.Bug>("bug", Type.ToOneRelationship);
  public static final ERXKey<er.bugtracker.People> ORIGINATOR = new ERXKey<er.bugtracker.People>("originator", Type.ToOneRelationship);
  public static final ERXKey<er.bugtracker.Comment> PARENT = new ERXKey<er.bugtracker.Comment>("parent", Type.ToOneRelationship);

  // Attributes
  public static final String DATE_SUBMITTED_KEY = DATE_SUBMITTED.key();
  public static final String TEXT_DESCRIPTION_KEY = TEXT_DESCRIPTION.key();

  // Relationships
  public static final String ATTACHMENTS_KEY = ATTACHMENTS.key();
  public static final String BUG_KEY = BUG.key();
  public static final String ORIGINATOR_KEY = ORIGINATOR.key();
  public static final String PARENT_KEY = PARENT.key();

  private static final Logger log = LoggerFactory.getLogger(_Comment.class);

  public Comment localInstanceIn(EOEditingContext editingContext) {
    Comment localInstance = (Comment)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSTimestamp dateSubmitted() {
    return (NSTimestamp) storedValueForKey(_Comment.DATE_SUBMITTED_KEY);
  }

  public void setDateSubmitted(NSTimestamp value) {
    log.debug( "updating dateSubmitted from {} to {}", dateSubmitted(), value);
    takeStoredValueForKey(value, _Comment.DATE_SUBMITTED_KEY);
  }

  public String textDescription() {
    return (String) storedValueForKey(_Comment.TEXT_DESCRIPTION_KEY);
  }

  public void setTextDescription(String value) {
    log.debug( "updating textDescription from {} to {}", textDescription(), value);
    takeStoredValueForKey(value, _Comment.TEXT_DESCRIPTION_KEY);
  }

  public er.bugtracker.Bug bug() {
    return (er.bugtracker.Bug)storedValueForKey(_Comment.BUG_KEY);
  }

  public void setBug(er.bugtracker.Bug value) {
    takeStoredValueForKey(value, _Comment.BUG_KEY);
  }

  public void setBugRelationship(er.bugtracker.Bug value) {
    log.debug("updating bug from {} to {}", bug(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setBug(value);
    }
    else if (value == null) {
      er.bugtracker.Bug oldValue = bug();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Comment.BUG_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Comment.BUG_KEY);
    }
  }

  public er.bugtracker.People originator() {
    return (er.bugtracker.People)storedValueForKey(_Comment.ORIGINATOR_KEY);
  }

  public void setOriginator(er.bugtracker.People value) {
    takeStoredValueForKey(value, _Comment.ORIGINATOR_KEY);
  }

  public void setOriginatorRelationship(er.bugtracker.People value) {
    log.debug("updating originator from {} to {}", originator(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setOriginator(value);
    }
    else if (value == null) {
      er.bugtracker.People oldValue = originator();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Comment.ORIGINATOR_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Comment.ORIGINATOR_KEY);
    }
  }

  public er.bugtracker.Comment parent() {
    return (er.bugtracker.Comment)storedValueForKey(_Comment.PARENT_KEY);
  }

  public void setParent(er.bugtracker.Comment value) {
    takeStoredValueForKey(value, _Comment.PARENT_KEY);
  }

  public void setParentRelationship(er.bugtracker.Comment value) {
    log.debug("updating parent from {} to {}", parent(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setParent(value);
    }
    else if (value == null) {
      er.bugtracker.Comment oldValue = parent();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Comment.PARENT_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Comment.PARENT_KEY);
    }
  }

  public NSArray<er.attachment.model.ERAttachment> attachments() {
    return (NSArray<er.attachment.model.ERAttachment>)storedValueForKey(_Comment.ATTACHMENTS_KEY);
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

  public void addToAttachments(er.attachment.model.ERAttachment object) {
    includeObjectIntoPropertyWithKey(object, _Comment.ATTACHMENTS_KEY);
  }

  public void removeFromAttachments(er.attachment.model.ERAttachment object) {
    excludeObjectFromPropertyWithKey(object, _Comment.ATTACHMENTS_KEY);
  }

  public void addToAttachmentsRelationship(er.attachment.model.ERAttachment object) {
    log.debug("adding {} to attachments relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToAttachments(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Comment.ATTACHMENTS_KEY);
    }
  }

  public void removeFromAttachmentsRelationship(er.attachment.model.ERAttachment object) {
    log.debug("removing {} from attachments relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromAttachments(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Comment.ATTACHMENTS_KEY);
    }
  }

  public er.attachment.model.ERAttachment createAttachmentsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.attachment.model.ERAttachment.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Comment.ATTACHMENTS_KEY);
    return (er.attachment.model.ERAttachment) eo;
  }

  public void deleteAttachmentsRelationship(er.attachment.model.ERAttachment object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Comment.ATTACHMENTS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllAttachmentsRelationships() {
    Enumeration<er.attachment.model.ERAttachment> objects = attachments().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteAttachmentsRelationship(objects.nextElement());
    }
  }


  public static Comment createComment(EOEditingContext editingContext, er.bugtracker.Bug bug, er.bugtracker.People originator) {
    Comment eo = (Comment) EOUtilities.createAndInsertInstance(editingContext, _Comment.ENTITY_NAME);
    eo.setBugRelationship(bug);
    eo.setOriginatorRelationship(originator);
    return eo;
  }

  public static ERXFetchSpecification<Comment> fetchSpec() {
    return new ERXFetchSpecification<Comment>(_Comment.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Comment> fetchAllComments(EOEditingContext editingContext) {
    return _Comment.fetchAllComments(editingContext, null);
  }

  public static NSArray<Comment> fetchAllComments(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Comment.fetchComments(editingContext, null, sortOrderings);
  }

  public static NSArray<Comment> fetchComments(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Comment> fetchSpec = new ERXFetchSpecification<Comment>(_Comment.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Comment> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Comment fetchComment(EOEditingContext editingContext, String keyName, Object value) {
    return _Comment.fetchComment(editingContext, ERXQ.equals(keyName, value));
  }

  public static Comment fetchComment(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Comment> eoObjects = _Comment.fetchComments(editingContext, qualifier, null);
    Comment eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Comment that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Comment fetchRequiredComment(EOEditingContext editingContext, String keyName, Object value) {
    return _Comment.fetchRequiredComment(editingContext, ERXQ.equals(keyName, value));
  }

  public static Comment fetchRequiredComment(EOEditingContext editingContext, EOQualifier qualifier) {
    Comment eoObject = _Comment.fetchComment(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Comment that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Comment localInstanceIn(EOEditingContext editingContext, Comment eo) {
    Comment localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

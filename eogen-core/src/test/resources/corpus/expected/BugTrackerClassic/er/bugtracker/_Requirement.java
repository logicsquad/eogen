// $LastChangedRevision$ DO NOT EDIT.  Make changes to Requirement.java instead.
package er.bugtracker;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Requirement extends er.bugtracker.Bug {
	public static final String ENTITY_NAME = "Requirement";

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
	public static final String DIFFICULTY_KEY = "difficulty";
	public static final String ORIGINATOR_KEY = "originator";
	public static final String OWNER_KEY = "owner";
	public static final String PREVIOUS_OWNER_KEY = "previousOwner";
	public static final String PRIORITY_KEY = "priority";
	public static final String REQUIREMENT_SUB_TYPE_KEY = "requirementSubType";
	public static final String REQUIREMENT_TYPE_KEY = "requirementType";
	public static final String TARGET_RELEASE_KEY = "targetRelease";
	public static final String TEST_ITEMS_KEY = "testItems";

  private static Logger LOG = Logger.getLogger(_Requirement.class);

  public Requirement localInstanceIn(EOEditingContext editingContext) {
    Requirement localInstance = (Requirement)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public er.bugtracker.Difficulty difficulty() {
    return (er.bugtracker.Difficulty)storedValueForKey("difficulty");
  }

  public void setDifficultyRelationship(er.bugtracker.Difficulty value) {
    if (_Requirement.LOG.isDebugEnabled()) {
      _Requirement.LOG.debug("updating difficulty from " + difficulty() + " to " + value);
    }
    if (value == null) {
    	er.bugtracker.Difficulty oldValue = difficulty();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "difficulty");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "difficulty");
    }
  }
  
  public er.bugtracker.RequirementSubType requirementSubType() {
    return (er.bugtracker.RequirementSubType)storedValueForKey("requirementSubType");
  }

  public void setRequirementSubTypeRelationship(er.bugtracker.RequirementSubType value) {
    if (_Requirement.LOG.isDebugEnabled()) {
      _Requirement.LOG.debug("updating requirementSubType from " + requirementSubType() + " to " + value);
    }
    if (value == null) {
    	er.bugtracker.RequirementSubType oldValue = requirementSubType();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "requirementSubType");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "requirementSubType");
    }
  }
  
  public er.bugtracker.RequirementType requirementType() {
    return (er.bugtracker.RequirementType)storedValueForKey("requirementType");
  }

  public void setRequirementTypeRelationship(er.bugtracker.RequirementType value) {
    if (_Requirement.LOG.isDebugEnabled()) {
      _Requirement.LOG.debug("updating requirementType from " + requirementType() + " to " + value);
    }
    if (value == null) {
    	er.bugtracker.RequirementType oldValue = requirementType();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "requirementType");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "requirementType");
    }
  }
  

  public static Requirement createRequirement(EOEditingContext editingContext, NSTimestamp dateModified
, NSTimestamp dateSubmitted
, Boolean isFeatureRequest
, Boolean isRead
, er.bugtracker.State state
, String subject
, er.bugtracker.Component component, er.bugtracker.People originator, er.bugtracker.People owner, er.bugtracker.Priority priority, er.bugtracker.Release targetRelease) {
    Requirement eo = (Requirement) EOUtilities.createAndInsertInstance(editingContext, _Requirement.ENTITY_NAME);    
		eo.setDateModified(dateModified);
		eo.setDateSubmitted(dateSubmitted);
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

  public static NSArray<Requirement> fetchAllRequirements(EOEditingContext editingContext) {
    return _Requirement.fetchAllRequirements(editingContext, null);
  }

  public static NSArray<Requirement> fetchAllRequirements(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Requirement.fetchRequirements(editingContext, null, sortOrderings);
  }

  public static NSArray<Requirement> fetchRequirements(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Requirement.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Requirement> eoObjects = (NSArray<Requirement>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static Requirement fetchRequirement(EOEditingContext editingContext, String keyName, Object value) {
    return _Requirement.fetchRequirement(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Requirement fetchRequirement(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Requirement> eoObjects = _Requirement.fetchRequirements(editingContext, qualifier, null);
    Requirement eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (Requirement)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Requirement that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Requirement fetchRequiredRequirement(EOEditingContext editingContext, String keyName, Object value) {
    return _Requirement.fetchRequiredRequirement(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Requirement fetchRequiredRequirement(EOEditingContext editingContext, EOQualifier qualifier) {
    Requirement eoObject = _Requirement.fetchRequirement(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Requirement that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Requirement localInstanceIn(EOEditingContext editingContext, Requirement eo) {
    Requirement localInstance = (eo == null) ? null : (Requirement)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

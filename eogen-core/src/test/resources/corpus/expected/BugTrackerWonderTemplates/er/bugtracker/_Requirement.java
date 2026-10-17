// DO NOT EDIT.  Make changes to Requirement.java instead.
package er.bugtracker;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

import er.extensions.eof.*;
import er.extensions.foundation.*;

@SuppressWarnings("all")
public abstract class _Requirement extends er.bugtracker.Bug {
  public static final String ENTITY_NAME = "Requirement";

  // Attribute Keys
  public static final ERXKey<NSTimestamp> DATE_MODIFIED = new ERXKey<NSTimestamp>("dateModified");
  public static final ERXKey<NSTimestamp> DATE_SUBMITTED = new ERXKey<NSTimestamp>("dateSubmitted");
  public static final ERXKey<Boolean> IS_FEATURE_REQUEST = new ERXKey<Boolean>("isFeatureRequest");
  public static final ERXKey<Boolean> IS_READ = new ERXKey<Boolean>("isRead");
  public static final ERXKey<er.bugtracker.State> STATE = new ERXKey<er.bugtracker.State>("state");
  public static final ERXKey<String> SUBJECT = new ERXKey<String>("subject");
  public static final ERXKey<String> TYPE = new ERXKey<String>("type");
  // Relationship Keys
  public static final ERXKey<er.bugtracker.Comment> COMMENTS = new ERXKey<er.bugtracker.Comment>("comments");
  public static final ERXKey<er.bugtracker.Component> COMPONENT = new ERXKey<er.bugtracker.Component>("component");
  public static final ERXKey<er.bugtracker.Difficulty> DIFFICULTY = new ERXKey<er.bugtracker.Difficulty>("difficulty");
  public static final ERXKey<er.bugtracker.People> ORIGINATOR = new ERXKey<er.bugtracker.People>("originator");
  public static final ERXKey<er.bugtracker.People> OWNER = new ERXKey<er.bugtracker.People>("owner");
  public static final ERXKey<er.bugtracker.People> PREVIOUS_OWNER = new ERXKey<er.bugtracker.People>("previousOwner");
  public static final ERXKey<er.bugtracker.Priority> PRIORITY = new ERXKey<er.bugtracker.Priority>("priority");
  public static final ERXKey<er.bugtracker.RequirementSubType> REQUIREMENT_SUB_TYPE = new ERXKey<er.bugtracker.RequirementSubType>("requirementSubType");
  public static final ERXKey<er.bugtracker.RequirementType> REQUIREMENT_TYPE = new ERXKey<er.bugtracker.RequirementType>("requirementType");
  public static final ERXKey<er.bugtracker.Release> TARGET_RELEASE = new ERXKey<er.bugtracker.Release>("targetRelease");
  public static final ERXKey<er.bugtracker.TestItem> TEST_ITEMS = new ERXKey<er.bugtracker.TestItem>("testItems");

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
  public static final String DIFFICULTY_KEY = DIFFICULTY.key();
  public static final String ORIGINATOR_KEY = ORIGINATOR.key();
  public static final String OWNER_KEY = OWNER.key();
  public static final String PREVIOUS_OWNER_KEY = PREVIOUS_OWNER.key();
  public static final String PRIORITY_KEY = PRIORITY.key();
  public static final String REQUIREMENT_SUB_TYPE_KEY = REQUIREMENT_SUB_TYPE.key();
  public static final String REQUIREMENT_TYPE_KEY = REQUIREMENT_TYPE.key();
  public static final String TARGET_RELEASE_KEY = TARGET_RELEASE.key();
  public static final String TEST_ITEMS_KEY = TEST_ITEMS.key();

  private static Logger LOG = Logger.getLogger(_Requirement.class);

  public Requirement localInstanceIn(EOEditingContext editingContext) {
    Requirement localInstance = (Requirement)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public er.bugtracker.Difficulty difficulty() {
    return (er.bugtracker.Difficulty)storedValueForKey(_Requirement.DIFFICULTY_KEY);
  }
  
  public void setDifficulty(er.bugtracker.Difficulty value) {
    takeStoredValueForKey(value, _Requirement.DIFFICULTY_KEY);
  }

  public void setDifficultyRelationship(er.bugtracker.Difficulty value) {
    if (_Requirement.LOG.isDebugEnabled()) {
      _Requirement.LOG.debug("updating difficulty from " + difficulty() + " to " + value);
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	setDifficulty(value);
    }
    else if (value == null) {
    	er.bugtracker.Difficulty oldValue = difficulty();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Requirement.DIFFICULTY_KEY);
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, _Requirement.DIFFICULTY_KEY);
    }
  }
  
  public er.bugtracker.RequirementSubType requirementSubType() {
    return (er.bugtracker.RequirementSubType)storedValueForKey(_Requirement.REQUIREMENT_SUB_TYPE_KEY);
  }
  
  public void setRequirementSubType(er.bugtracker.RequirementSubType value) {
    takeStoredValueForKey(value, _Requirement.REQUIREMENT_SUB_TYPE_KEY);
  }

  public void setRequirementSubTypeRelationship(er.bugtracker.RequirementSubType value) {
    if (_Requirement.LOG.isDebugEnabled()) {
      _Requirement.LOG.debug("updating requirementSubType from " + requirementSubType() + " to " + value);
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	setRequirementSubType(value);
    }
    else if (value == null) {
    	er.bugtracker.RequirementSubType oldValue = requirementSubType();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Requirement.REQUIREMENT_SUB_TYPE_KEY);
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, _Requirement.REQUIREMENT_SUB_TYPE_KEY);
    }
  }
  
  public er.bugtracker.RequirementType requirementType() {
    return (er.bugtracker.RequirementType)storedValueForKey(_Requirement.REQUIREMENT_TYPE_KEY);
  }
  
  public void setRequirementType(er.bugtracker.RequirementType value) {
    takeStoredValueForKey(value, _Requirement.REQUIREMENT_TYPE_KEY);
  }

  public void setRequirementTypeRelationship(er.bugtracker.RequirementType value) {
    if (_Requirement.LOG.isDebugEnabled()) {
      _Requirement.LOG.debug("updating requirementType from " + requirementType() + " to " + value);
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	setRequirementType(value);
    }
    else if (value == null) {
    	er.bugtracker.RequirementType oldValue = requirementType();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Requirement.REQUIREMENT_TYPE_KEY);
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, _Requirement.REQUIREMENT_TYPE_KEY);
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

  public static ERXFetchSpecification<Requirement> fetchSpecForRequirement() {
    return new ERXFetchSpecification<Requirement>(_Requirement.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Requirement> fetchAllRequirements(EOEditingContext editingContext) {
    return _Requirement.fetchAllRequirements(editingContext, null);
  }

  public static NSArray<Requirement> fetchAllRequirements(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Requirement.fetchRequirements(editingContext, null, sortOrderings);
  }

  public static NSArray<Requirement> fetchRequirements(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Requirement> fetchSpec = new ERXFetchSpecification<Requirement>(_Requirement.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Requirement> eoObjects = fetchSpec.fetchObjects(editingContext);
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
      eoObject = eoObjects.objectAtIndex(0);
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
    Requirement localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

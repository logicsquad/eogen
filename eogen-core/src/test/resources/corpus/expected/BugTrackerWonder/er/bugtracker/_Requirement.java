// DO NOT EDIT.  Make changes to Requirement.java instead.
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
public abstract class _Requirement extends er.bugtracker.Bug {
  public static final String ENTITY_NAME = "Requirement";

  // Attribute Keys

  // Relationship Keys
  public static final ERXKey<er.bugtracker.Difficulty> DIFFICULTY = new ERXKey<er.bugtracker.Difficulty>("difficulty", Type.ToOneRelationship);
  public static final ERXKey<er.bugtracker.RequirementSubType> REQUIREMENT_SUB_TYPE = new ERXKey<er.bugtracker.RequirementSubType>("requirementSubType", Type.ToOneRelationship);
  public static final ERXKey<er.bugtracker.RequirementType> REQUIREMENT_TYPE = new ERXKey<er.bugtracker.RequirementType>("requirementType", Type.ToOneRelationship);

  // Attributes

  // Relationships
  public static final String DIFFICULTY_KEY = DIFFICULTY.key();
  public static final String REQUIREMENT_SUB_TYPE_KEY = REQUIREMENT_SUB_TYPE.key();
  public static final String REQUIREMENT_TYPE_KEY = REQUIREMENT_TYPE.key();

  private static final Logger log = LoggerFactory.getLogger(_Requirement.class);

  @Override
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
    log.debug("updating difficulty from {} to {}", difficulty(), value);
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
    log.debug("updating requirementSubType from {} to {}", requirementSubType(), value);
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
    log.debug("updating requirementType from {} to {}", requirementType(), value);
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
    NSArray<Requirement> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Requirement fetchRequirement(EOEditingContext editingContext, String keyName, Object value) {
    return _Requirement.fetchRequirement(editingContext, ERXQ.equals(keyName, value));
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
    return _Requirement.fetchRequiredRequirement(editingContext, ERXQ.equals(keyName, value));
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

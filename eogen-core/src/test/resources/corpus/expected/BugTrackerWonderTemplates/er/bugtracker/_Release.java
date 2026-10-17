// DO NOT EDIT.  Make changes to Release.java instead.
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
public abstract class _Release extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Release";

  // Attribute Keys
  public static final ERXKey<NSTimestamp> DATE_DUE = new ERXKey<NSTimestamp>("dateDue");
  public static final ERXKey<Boolean> IS_OPEN = new ERXKey<Boolean>("isOpen");
  public static final ERXKey<String> NAME = new ERXKey<String>("name");
  // Relationship Keys
  public static final ERXKey<er.bugtracker.Bug> BUGS = new ERXKey<er.bugtracker.Bug>("bugs");
  public static final ERXKey<er.bugtracker.Requirement> REQUIREMENTS = new ERXKey<er.bugtracker.Requirement>("requirements");

  // Attributes
  public static final String DATE_DUE_KEY = DATE_DUE.key();
  public static final String IS_OPEN_KEY = IS_OPEN.key();
  public static final String NAME_KEY = NAME.key();
  // Relationships
  public static final String BUGS_KEY = BUGS.key();
  public static final String REQUIREMENTS_KEY = REQUIREMENTS.key();

  private static Logger LOG = Logger.getLogger(_Release.class);

  public Release localInstanceIn(EOEditingContext editingContext) {
    Release localInstance = (Release)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSTimestamp dateDue() {
    return (NSTimestamp) storedValueForKey(_Release.DATE_DUE_KEY);
  }

  public void setDateDue(NSTimestamp value) {
    if (_Release.LOG.isDebugEnabled()) {
    	_Release.LOG.debug( "updating dateDue from " + dateDue() + " to " + value);
    }
    takeStoredValueForKey(value, _Release.DATE_DUE_KEY);
  }

  public Boolean isOpen() {
    return (Boolean) storedValueForKey(_Release.IS_OPEN_KEY);
  }

  public void setIsOpen(Boolean value) {
    if (_Release.LOG.isDebugEnabled()) {
    	_Release.LOG.debug( "updating isOpen from " + isOpen() + " to " + value);
    }
    takeStoredValueForKey(value, _Release.IS_OPEN_KEY);
  }

  public String name() {
    return (String) storedValueForKey(_Release.NAME_KEY);
  }

  public void setName(String value) {
    if (_Release.LOG.isDebugEnabled()) {
    	_Release.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, _Release.NAME_KEY);
  }

  public NSArray<er.bugtracker.Bug> bugs() {
    return (NSArray<er.bugtracker.Bug>)storedValueForKey(_Release.BUGS_KEY);
  }

  public NSArray<er.bugtracker.Bug> bugs(EOQualifier qualifier) {
    return bugs(qualifier, null, false);
  }

  public NSArray<er.bugtracker.Bug> bugs(EOQualifier qualifier, boolean fetch) {
    return bugs(qualifier, null, fetch);
  }

  public NSArray<er.bugtracker.Bug> bugs(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.bugtracker.Bug> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = new EOKeyValueQualifier(er.bugtracker.Bug.TARGET_RELEASE_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        NSMutableArray<EOQualifier> qualifiers = new NSMutableArray<EOQualifier>();
        qualifiers.addObject(qualifier);
        qualifiers.addObject(inverseQualifier);
        fullQualifier = new EOAndQualifier(qualifiers);
      }

      results = er.bugtracker.Bug.fetchBugs(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = bugs();
      if (qualifier != null) {
        results = (NSArray<er.bugtracker.Bug>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.bugtracker.Bug>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }
  
  public void addToBugs(er.bugtracker.Bug object) {
    includeObjectIntoPropertyWithKey(object, _Release.BUGS_KEY);
  }

  public void removeFromBugs(er.bugtracker.Bug object) {
    excludeObjectFromPropertyWithKey(object, _Release.BUGS_KEY);
  }

  public void addToBugsRelationship(er.bugtracker.Bug object) {
    if (_Release.LOG.isDebugEnabled()) {
      _Release.LOG.debug("adding " + object + " to bugs relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	addToBugs(object);
    }
    else {
    	addObjectToBothSidesOfRelationshipWithKey(object, _Release.BUGS_KEY);
    }
  }

  public void removeFromBugsRelationship(er.bugtracker.Bug object) {
    if (_Release.LOG.isDebugEnabled()) {
      _Release.LOG.debug("removing " + object + " from bugs relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	removeFromBugs(object);
    }
    else {
    	removeObjectFromBothSidesOfRelationshipWithKey(object, _Release.BUGS_KEY);
    }
  }

  public er.bugtracker.Bug createBugsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName( er.bugtracker.Bug.ENTITY_NAME );
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, _Release.BUGS_KEY);
    return (er.bugtracker.Bug) eo;
  }

  public void deleteBugsRelationship(er.bugtracker.Bug object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Release.BUGS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllBugsRelationships() {
    Enumeration<er.bugtracker.Bug> objects = bugs().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteBugsRelationship(objects.nextElement());
    }
  }

  public NSArray<er.bugtracker.Requirement> requirements() {
    return (NSArray<er.bugtracker.Requirement>)storedValueForKey(_Release.REQUIREMENTS_KEY);
  }

  public NSArray<er.bugtracker.Requirement> requirements(EOQualifier qualifier) {
    return requirements(qualifier, null, false);
  }

  public NSArray<er.bugtracker.Requirement> requirements(EOQualifier qualifier, boolean fetch) {
    return requirements(qualifier, null, fetch);
  }

  public NSArray<er.bugtracker.Requirement> requirements(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.bugtracker.Requirement> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = new EOKeyValueQualifier(er.bugtracker.Requirement.TARGET_RELEASE_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        NSMutableArray<EOQualifier> qualifiers = new NSMutableArray<EOQualifier>();
        qualifiers.addObject(qualifier);
        qualifiers.addObject(inverseQualifier);
        fullQualifier = new EOAndQualifier(qualifiers);
      }

      results = er.bugtracker.Requirement.fetchRequirements(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = requirements();
      if (qualifier != null) {
        results = (NSArray<er.bugtracker.Requirement>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.bugtracker.Requirement>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }
  
  public void addToRequirements(er.bugtracker.Requirement object) {
    includeObjectIntoPropertyWithKey(object, _Release.REQUIREMENTS_KEY);
  }

  public void removeFromRequirements(er.bugtracker.Requirement object) {
    excludeObjectFromPropertyWithKey(object, _Release.REQUIREMENTS_KEY);
  }

  public void addToRequirementsRelationship(er.bugtracker.Requirement object) {
    if (_Release.LOG.isDebugEnabled()) {
      _Release.LOG.debug("adding " + object + " to requirements relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	addToRequirements(object);
    }
    else {
    	addObjectToBothSidesOfRelationshipWithKey(object, _Release.REQUIREMENTS_KEY);
    }
  }

  public void removeFromRequirementsRelationship(er.bugtracker.Requirement object) {
    if (_Release.LOG.isDebugEnabled()) {
      _Release.LOG.debug("removing " + object + " from requirements relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	removeFromRequirements(object);
    }
    else {
    	removeObjectFromBothSidesOfRelationshipWithKey(object, _Release.REQUIREMENTS_KEY);
    }
  }

  public er.bugtracker.Requirement createRequirementsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName( er.bugtracker.Requirement.ENTITY_NAME );
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, _Release.REQUIREMENTS_KEY);
    return (er.bugtracker.Requirement) eo;
  }

  public void deleteRequirementsRelationship(er.bugtracker.Requirement object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Release.REQUIREMENTS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllRequirementsRelationships() {
    Enumeration<er.bugtracker.Requirement> objects = requirements().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteRequirementsRelationship(objects.nextElement());
    }
  }


  public static Release createRelease(EOEditingContext editingContext, NSTimestamp dateDue
, Boolean isOpen
, String name
) {
    Release eo = (Release) EOUtilities.createAndInsertInstance(editingContext, _Release.ENTITY_NAME);    
		eo.setDateDue(dateDue);
		eo.setIsOpen(isOpen);
		eo.setName(name);
    return eo;
  }

  public static ERXFetchSpecification<Release> fetchSpec() {
    return new ERXFetchSpecification<Release>(_Release.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Release> fetchAllReleases(EOEditingContext editingContext) {
    return _Release.fetchAllReleases(editingContext, null);
  }

  public static NSArray<Release> fetchAllReleases(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Release.fetchReleases(editingContext, null, sortOrderings);
  }

  public static NSArray<Release> fetchReleases(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Release> fetchSpec = new ERXFetchSpecification<Release>(_Release.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Release> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Release fetchRelease(EOEditingContext editingContext, String keyName, Object value) {
    return _Release.fetchRelease(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Release fetchRelease(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Release> eoObjects = _Release.fetchReleases(editingContext, qualifier, null);
    Release eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Release that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Release fetchRequiredRelease(EOEditingContext editingContext, String keyName, Object value) {
    return _Release.fetchRequiredRelease(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Release fetchRequiredRelease(EOEditingContext editingContext, EOQualifier qualifier) {
    Release eoObject = _Release.fetchRelease(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Release that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Release localInstanceIn(EOEditingContext editingContext, Release eo) {
    Release localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

// $LastChangedRevision$ DO NOT EDIT.  Make changes to Release.java instead.
package er.bugtracker;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Release extends er.extensions.eof.ERXGenericRecord {
	public static final String ENTITY_NAME = "Release";

	// Attributes
	public static final String DATE_DUE_KEY = "dateDue";
	public static final String IS_OPEN_KEY = "isOpen";
	public static final String NAME_KEY = "name";

	// Relationships
	public static final String BUGS_KEY = "bugs";
	public static final String REQUIREMENTS_KEY = "requirements";

  private static Logger LOG = Logger.getLogger(_Release.class);

  public Release localInstanceIn(EOEditingContext editingContext) {
    Release localInstance = (Release)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSTimestamp dateDue() {
    return (NSTimestamp) storedValueForKey("dateDue");
  }

  public void setDateDue(NSTimestamp value) {
    if (_Release.LOG.isDebugEnabled()) {
    	_Release.LOG.debug( "updating dateDue from " + dateDue() + " to " + value);
    }
    takeStoredValueForKey(value, "dateDue");
  }

  public Boolean isOpen() {
    return (Boolean) storedValueForKey("isOpen");
  }

  public void setIsOpen(Boolean value) {
    if (_Release.LOG.isDebugEnabled()) {
    	_Release.LOG.debug( "updating isOpen from " + isOpen() + " to " + value);
    }
    takeStoredValueForKey(value, "isOpen");
  }

  public String name() {
    return (String) storedValueForKey("name");
  }

  public void setName(String value) {
    if (_Release.LOG.isDebugEnabled()) {
    	_Release.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, "name");
  }

  public NSArray<er.bugtracker.Bug> bugs() {
    return (NSArray<er.bugtracker.Bug>)storedValueForKey("bugs");
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
        NSMutableArray qualifiers = new NSMutableArray();
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
  
  public void addToBugsRelationship(er.bugtracker.Bug object) {
    if (_Release.LOG.isDebugEnabled()) {
      _Release.LOG.debug("adding " + object + " to bugs relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "bugs");
  }

  public void removeFromBugsRelationship(er.bugtracker.Bug object) {
    if (_Release.LOG.isDebugEnabled()) {
      _Release.LOG.debug("removing " + object + " from bugs relationship");
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
        NSMutableArray qualifiers = new NSMutableArray();
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
  
  public void addToRequirementsRelationship(er.bugtracker.Requirement object) {
    if (_Release.LOG.isDebugEnabled()) {
      _Release.LOG.debug("adding " + object + " to requirements relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "requirements");
  }

  public void removeFromRequirementsRelationship(er.bugtracker.Requirement object) {
    if (_Release.LOG.isDebugEnabled()) {
      _Release.LOG.debug("removing " + object + " from requirements relationship");
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

  public static NSArray<Release> fetchAllReleases(EOEditingContext editingContext) {
    return _Release.fetchAllReleases(editingContext, null);
  }

  public static NSArray<Release> fetchAllReleases(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Release.fetchReleases(editingContext, null, sortOrderings);
  }

  public static NSArray<Release> fetchReleases(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Release.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Release> eoObjects = (NSArray<Release>)editingContext.objectsWithFetchSpecification(fetchSpec);
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
      eoObject = (Release)eoObjects.objectAtIndex(0);
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
    Release localInstance = (eo == null) ? null : (Release)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

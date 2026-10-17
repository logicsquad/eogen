// $LastChangedRevision$ DO NOT EDIT.  Make changes to Component.java instead.
package er.bugtracker;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Component extends er.extensions.eof.ERXGenericRecord {
	public static final String ENTITY_NAME = "Component";

	// Attributes
	public static final String TEXT_DESCRIPTION_KEY = "textDescription";

	// Relationships
	public static final String BUGS_KEY = "bugs";
	public static final String CHILDREN_KEY = "children";
	public static final String OWNER_KEY = "owner";
	public static final String PARENT_KEY = "parent";
	public static final String REQUIREMENTS_KEY = "requirements";
	public static final String TEST_ITEMS_KEY = "testItems";

  private static Logger LOG = Logger.getLogger(_Component.class);

  public Component localInstanceIn(EOEditingContext editingContext) {
    Component localInstance = (Component)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String textDescription() {
    return (String) storedValueForKey("textDescription");
  }

  public void setTextDescription(String value) {
    if (_Component.LOG.isDebugEnabled()) {
    	_Component.LOG.debug( "updating textDescription from " + textDescription() + " to " + value);
    }
    takeStoredValueForKey(value, "textDescription");
  }

  public er.bugtracker.People owner() {
    return (er.bugtracker.People)storedValueForKey("owner");
  }

  public void setOwnerRelationship(er.bugtracker.People value) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("updating owner from " + owner() + " to " + value);
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
  
  public er.bugtracker.Component parent() {
    return (er.bugtracker.Component)storedValueForKey("parent");
  }

  public void setParentRelationship(er.bugtracker.Component value) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("updating parent from " + parent() + " to " + value);
    }
    if (value == null) {
    	er.bugtracker.Component oldValue = parent();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "parent");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "parent");
    }
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
      EOQualifier inverseQualifier = new EOKeyValueQualifier(er.bugtracker.Bug.COMPONENT_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
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
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("adding " + object + " to bugs relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "bugs");
  }

  public void removeFromBugsRelationship(er.bugtracker.Bug object) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("removing " + object + " from bugs relationship");
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

  public NSArray<er.bugtracker.Component> children() {
    return (NSArray<er.bugtracker.Component>)storedValueForKey("children");
  }

  public NSArray<er.bugtracker.Component> children(EOQualifier qualifier) {
    return children(qualifier, null, false);
  }

  public NSArray<er.bugtracker.Component> children(EOQualifier qualifier, boolean fetch) {
    return children(qualifier, null, fetch);
  }

  public NSArray<er.bugtracker.Component> children(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.bugtracker.Component> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = new EOKeyValueQualifier(er.bugtracker.Component.PARENT_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        NSMutableArray qualifiers = new NSMutableArray();
        qualifiers.addObject(qualifier);
        qualifiers.addObject(inverseQualifier);
        fullQualifier = new EOAndQualifier(qualifiers);
      }

      results = er.bugtracker.Component.fetchComponents(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = children();
      if (qualifier != null) {
        results = (NSArray<er.bugtracker.Component>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.bugtracker.Component>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }
  
  public void addToChildrenRelationship(er.bugtracker.Component object) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("adding " + object + " to children relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "children");
  }

  public void removeFromChildrenRelationship(er.bugtracker.Component object) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("removing " + object + " from children relationship");
    }
    removeObjectFromBothSidesOfRelationshipWithKey(object, "children");
  }

  public er.bugtracker.Component createChildrenRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Component");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "children");
    return (er.bugtracker.Component) eo;
  }

  public void deleteChildrenRelationship(er.bugtracker.Component object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "children");
    editingContext().deleteObject(object);
  }

  public void deleteAllChildrenRelationships() {
    Enumeration objects = children().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteChildrenRelationship((er.bugtracker.Component)objects.nextElement());
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
      EOQualifier inverseQualifier = new EOKeyValueQualifier(er.bugtracker.Requirement.COMPONENT_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
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
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("adding " + object + " to requirements relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "requirements");
  }

  public void removeFromRequirementsRelationship(er.bugtracker.Requirement object) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("removing " + object + " from requirements relationship");
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

  public NSArray<er.bugtracker.TestItem> testItems() {
    return (NSArray<er.bugtracker.TestItem>)storedValueForKey("testItems");
  }

  public NSArray<er.bugtracker.TestItem> testItems(EOQualifier qualifier) {
    return testItems(qualifier, null, false);
  }

  public NSArray<er.bugtracker.TestItem> testItems(EOQualifier qualifier, boolean fetch) {
    return testItems(qualifier, null, fetch);
  }

  public NSArray<er.bugtracker.TestItem> testItems(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.bugtracker.TestItem> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = new EOKeyValueQualifier(er.bugtracker.TestItem.COMPONENT_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        NSMutableArray qualifiers = new NSMutableArray();
        qualifiers.addObject(qualifier);
        qualifiers.addObject(inverseQualifier);
        fullQualifier = new EOAndQualifier(qualifiers);
      }

      results = er.bugtracker.TestItem.fetchTestItems(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = testItems();
      if (qualifier != null) {
        results = (NSArray<er.bugtracker.TestItem>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.bugtracker.TestItem>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }
  
  public void addToTestItemsRelationship(er.bugtracker.TestItem object) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("adding " + object + " to testItems relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "testItems");
  }

  public void removeFromTestItemsRelationship(er.bugtracker.TestItem object) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("removing " + object + " from testItems relationship");
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


  public static Component createComponent(EOEditingContext editingContext, String textDescription
, er.bugtracker.People owner) {
    Component eo = (Component) EOUtilities.createAndInsertInstance(editingContext, _Component.ENTITY_NAME);    
		eo.setTextDescription(textDescription);
    eo.setOwnerRelationship(owner);
    return eo;
  }

  public static NSArray<Component> fetchAllComponents(EOEditingContext editingContext) {
    return _Component.fetchAllComponents(editingContext, null);
  }

  public static NSArray<Component> fetchAllComponents(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Component.fetchComponents(editingContext, null, sortOrderings);
  }

  public static NSArray<Component> fetchComponents(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Component.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Component> eoObjects = (NSArray<Component>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static Component fetchComponent(EOEditingContext editingContext, String keyName, Object value) {
    return _Component.fetchComponent(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Component fetchComponent(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Component> eoObjects = _Component.fetchComponents(editingContext, qualifier, null);
    Component eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (Component)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Component that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Component fetchRequiredComponent(EOEditingContext editingContext, String keyName, Object value) {
    return _Component.fetchRequiredComponent(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Component fetchRequiredComponent(EOEditingContext editingContext, EOQualifier qualifier) {
    Component eoObject = _Component.fetchComponent(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Component that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Component localInstanceIn(EOEditingContext editingContext, Component eo) {
    Component localInstance = (eo == null) ? null : (Component)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

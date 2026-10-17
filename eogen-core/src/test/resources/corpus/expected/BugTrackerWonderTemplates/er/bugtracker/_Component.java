// DO NOT EDIT.  Make changes to Component.java instead.
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
public abstract class _Component extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Component";

  // Attribute Keys
  public static final ERXKey<String> TEXT_DESCRIPTION = new ERXKey<String>("textDescription");
  // Relationship Keys
  public static final ERXKey<er.bugtracker.Bug> BUGS = new ERXKey<er.bugtracker.Bug>("bugs");
  public static final ERXKey<er.bugtracker.Component> CHILDREN = new ERXKey<er.bugtracker.Component>("children");
  public static final ERXKey<er.bugtracker.People> OWNER = new ERXKey<er.bugtracker.People>("owner");
  public static final ERXKey<er.bugtracker.Component> PARENT = new ERXKey<er.bugtracker.Component>("parent");
  public static final ERXKey<er.bugtracker.Requirement> REQUIREMENTS = new ERXKey<er.bugtracker.Requirement>("requirements");
  public static final ERXKey<er.bugtracker.TestItem> TEST_ITEMS = new ERXKey<er.bugtracker.TestItem>("testItems");

  // Attributes
  public static final String TEXT_DESCRIPTION_KEY = TEXT_DESCRIPTION.key();
  // Relationships
  public static final String BUGS_KEY = BUGS.key();
  public static final String CHILDREN_KEY = CHILDREN.key();
  public static final String OWNER_KEY = OWNER.key();
  public static final String PARENT_KEY = PARENT.key();
  public static final String REQUIREMENTS_KEY = REQUIREMENTS.key();
  public static final String TEST_ITEMS_KEY = TEST_ITEMS.key();

  private static Logger LOG = Logger.getLogger(_Component.class);

  public Component localInstanceIn(EOEditingContext editingContext) {
    Component localInstance = (Component)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String textDescription() {
    return (String) storedValueForKey(_Component.TEXT_DESCRIPTION_KEY);
  }

  public void setTextDescription(String value) {
    if (_Component.LOG.isDebugEnabled()) {
    	_Component.LOG.debug( "updating textDescription from " + textDescription() + " to " + value);
    }
    takeStoredValueForKey(value, _Component.TEXT_DESCRIPTION_KEY);
  }

  public er.bugtracker.People owner() {
    return (er.bugtracker.People)storedValueForKey(_Component.OWNER_KEY);
  }
  
  public void setOwner(er.bugtracker.People value) {
    takeStoredValueForKey(value, _Component.OWNER_KEY);
  }

  public void setOwnerRelationship(er.bugtracker.People value) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("updating owner from " + owner() + " to " + value);
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	setOwner(value);
    }
    else if (value == null) {
    	er.bugtracker.People oldValue = owner();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Component.OWNER_KEY);
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, _Component.OWNER_KEY);
    }
  }
  
  public er.bugtracker.Component parent() {
    return (er.bugtracker.Component)storedValueForKey(_Component.PARENT_KEY);
  }
  
  public void setParent(er.bugtracker.Component value) {
    takeStoredValueForKey(value, _Component.PARENT_KEY);
  }

  public void setParentRelationship(er.bugtracker.Component value) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("updating parent from " + parent() + " to " + value);
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	setParent(value);
    }
    else if (value == null) {
    	er.bugtracker.Component oldValue = parent();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Component.PARENT_KEY);
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, _Component.PARENT_KEY);
    }
  }
  
  public NSArray<er.bugtracker.Bug> bugs() {
    return (NSArray<er.bugtracker.Bug>)storedValueForKey(_Component.BUGS_KEY);
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
    includeObjectIntoPropertyWithKey(object, _Component.BUGS_KEY);
  }

  public void removeFromBugs(er.bugtracker.Bug object) {
    excludeObjectFromPropertyWithKey(object, _Component.BUGS_KEY);
  }

  public void addToBugsRelationship(er.bugtracker.Bug object) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("adding " + object + " to bugs relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	addToBugs(object);
    }
    else {
    	addObjectToBothSidesOfRelationshipWithKey(object, _Component.BUGS_KEY);
    }
  }

  public void removeFromBugsRelationship(er.bugtracker.Bug object) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("removing " + object + " from bugs relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	removeFromBugs(object);
    }
    else {
    	removeObjectFromBothSidesOfRelationshipWithKey(object, _Component.BUGS_KEY);
    }
  }

  public er.bugtracker.Bug createBugsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName( er.bugtracker.Bug.ENTITY_NAME );
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, _Component.BUGS_KEY);
    return (er.bugtracker.Bug) eo;
  }

  public void deleteBugsRelationship(er.bugtracker.Bug object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Component.BUGS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllBugsRelationships() {
    Enumeration<er.bugtracker.Bug> objects = bugs().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteBugsRelationship(objects.nextElement());
    }
  }

  public NSArray<er.bugtracker.Component> children() {
    return (NSArray<er.bugtracker.Component>)storedValueForKey(_Component.CHILDREN_KEY);
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
        NSMutableArray<EOQualifier> qualifiers = new NSMutableArray<EOQualifier>();
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
  
  public void addToChildren(er.bugtracker.Component object) {
    includeObjectIntoPropertyWithKey(object, _Component.CHILDREN_KEY);
  }

  public void removeFromChildren(er.bugtracker.Component object) {
    excludeObjectFromPropertyWithKey(object, _Component.CHILDREN_KEY);
  }

  public void addToChildrenRelationship(er.bugtracker.Component object) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("adding " + object + " to children relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	addToChildren(object);
    }
    else {
    	addObjectToBothSidesOfRelationshipWithKey(object, _Component.CHILDREN_KEY);
    }
  }

  public void removeFromChildrenRelationship(er.bugtracker.Component object) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("removing " + object + " from children relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	removeFromChildren(object);
    }
    else {
    	removeObjectFromBothSidesOfRelationshipWithKey(object, _Component.CHILDREN_KEY);
    }
  }

  public er.bugtracker.Component createChildrenRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName( er.bugtracker.Component.ENTITY_NAME );
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, _Component.CHILDREN_KEY);
    return (er.bugtracker.Component) eo;
  }

  public void deleteChildrenRelationship(er.bugtracker.Component object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Component.CHILDREN_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllChildrenRelationships() {
    Enumeration<er.bugtracker.Component> objects = children().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteChildrenRelationship(objects.nextElement());
    }
  }

  public NSArray<er.bugtracker.Requirement> requirements() {
    return (NSArray<er.bugtracker.Requirement>)storedValueForKey(_Component.REQUIREMENTS_KEY);
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
    includeObjectIntoPropertyWithKey(object, _Component.REQUIREMENTS_KEY);
  }

  public void removeFromRequirements(er.bugtracker.Requirement object) {
    excludeObjectFromPropertyWithKey(object, _Component.REQUIREMENTS_KEY);
  }

  public void addToRequirementsRelationship(er.bugtracker.Requirement object) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("adding " + object + " to requirements relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	addToRequirements(object);
    }
    else {
    	addObjectToBothSidesOfRelationshipWithKey(object, _Component.REQUIREMENTS_KEY);
    }
  }

  public void removeFromRequirementsRelationship(er.bugtracker.Requirement object) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("removing " + object + " from requirements relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	removeFromRequirements(object);
    }
    else {
    	removeObjectFromBothSidesOfRelationshipWithKey(object, _Component.REQUIREMENTS_KEY);
    }
  }

  public er.bugtracker.Requirement createRequirementsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName( er.bugtracker.Requirement.ENTITY_NAME );
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, _Component.REQUIREMENTS_KEY);
    return (er.bugtracker.Requirement) eo;
  }

  public void deleteRequirementsRelationship(er.bugtracker.Requirement object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Component.REQUIREMENTS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllRequirementsRelationships() {
    Enumeration<er.bugtracker.Requirement> objects = requirements().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteRequirementsRelationship(objects.nextElement());
    }
  }

  public NSArray<er.bugtracker.TestItem> testItems() {
    return (NSArray<er.bugtracker.TestItem>)storedValueForKey(_Component.TEST_ITEMS_KEY);
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
        NSMutableArray<EOQualifier> qualifiers = new NSMutableArray<EOQualifier>();
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
  
  public void addToTestItems(er.bugtracker.TestItem object) {
    includeObjectIntoPropertyWithKey(object, _Component.TEST_ITEMS_KEY);
  }

  public void removeFromTestItems(er.bugtracker.TestItem object) {
    excludeObjectFromPropertyWithKey(object, _Component.TEST_ITEMS_KEY);
  }

  public void addToTestItemsRelationship(er.bugtracker.TestItem object) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("adding " + object + " to testItems relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	addToTestItems(object);
    }
    else {
    	addObjectToBothSidesOfRelationshipWithKey(object, _Component.TEST_ITEMS_KEY);
    }
  }

  public void removeFromTestItemsRelationship(er.bugtracker.TestItem object) {
    if (_Component.LOG.isDebugEnabled()) {
      _Component.LOG.debug("removing " + object + " from testItems relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	removeFromTestItems(object);
    }
    else {
    	removeObjectFromBothSidesOfRelationshipWithKey(object, _Component.TEST_ITEMS_KEY);
    }
  }

  public er.bugtracker.TestItem createTestItemsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName( er.bugtracker.TestItem.ENTITY_NAME );
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, _Component.TEST_ITEMS_KEY);
    return (er.bugtracker.TestItem) eo;
  }

  public void deleteTestItemsRelationship(er.bugtracker.TestItem object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Component.TEST_ITEMS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllTestItemsRelationships() {
    Enumeration<er.bugtracker.TestItem> objects = testItems().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteTestItemsRelationship(objects.nextElement());
    }
  }


  public static Component createComponent(EOEditingContext editingContext, String textDescription
, er.bugtracker.People owner) {
    Component eo = (Component) EOUtilities.createAndInsertInstance(editingContext, _Component.ENTITY_NAME);    
		eo.setTextDescription(textDescription);
    eo.setOwnerRelationship(owner);
    return eo;
  }

  public static ERXFetchSpecification<Component> fetchSpec() {
    return new ERXFetchSpecification<Component>(_Component.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Component> fetchAllComponents(EOEditingContext editingContext) {
    return _Component.fetchAllComponents(editingContext, null);
  }

  public static NSArray<Component> fetchAllComponents(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Component.fetchComponents(editingContext, null, sortOrderings);
  }

  public static NSArray<Component> fetchComponents(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Component> fetchSpec = new ERXFetchSpecification<Component>(_Component.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Component> eoObjects = fetchSpec.fetchObjects(editingContext);
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
      eoObject = eoObjects.objectAtIndex(0);
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
    Component localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

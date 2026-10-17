// DO NOT EDIT.  Make changes to Framework.java instead.
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
public abstract class _Framework extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Framework";

  // Attribute Keys
  public static final ERXKey<String> NAME = new ERXKey<String>("name");
  public static final ERXKey<Integer> ORDERING = new ERXKey<Integer>("ordering");
  public static final ERXKey<NSTimestamp> OWNED_SINCE = new ERXKey<NSTimestamp>("ownedSince");
  // Relationship Keys
  public static final ERXKey<er.bugtracker.People> OWNER = new ERXKey<er.bugtracker.People>("owner");

  // Attributes
  public static final String NAME_KEY = NAME.key();
  public static final String ORDERING_KEY = ORDERING.key();
  public static final String OWNED_SINCE_KEY = OWNED_SINCE.key();
  // Relationships
  public static final String OWNER_KEY = OWNER.key();

  private static Logger LOG = Logger.getLogger(_Framework.class);

  public Framework localInstanceIn(EOEditingContext editingContext) {
    Framework localInstance = (Framework)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String name() {
    return (String) storedValueForKey(_Framework.NAME_KEY);
  }

  public void setName(String value) {
    if (_Framework.LOG.isDebugEnabled()) {
    	_Framework.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, _Framework.NAME_KEY);
  }

  public Integer ordering() {
    return (Integer) storedValueForKey(_Framework.ORDERING_KEY);
  }

  public void setOrdering(Integer value) {
    if (_Framework.LOG.isDebugEnabled()) {
    	_Framework.LOG.debug( "updating ordering from " + ordering() + " to " + value);
    }
    takeStoredValueForKey(value, _Framework.ORDERING_KEY);
  }

  public NSTimestamp ownedSince() {
    return (NSTimestamp) storedValueForKey(_Framework.OWNED_SINCE_KEY);
  }

  public void setOwnedSince(NSTimestamp value) {
    if (_Framework.LOG.isDebugEnabled()) {
    	_Framework.LOG.debug( "updating ownedSince from " + ownedSince() + " to " + value);
    }
    takeStoredValueForKey(value, _Framework.OWNED_SINCE_KEY);
  }

  public er.bugtracker.People owner() {
    return (er.bugtracker.People)storedValueForKey(_Framework.OWNER_KEY);
  }
  
  public void setOwner(er.bugtracker.People value) {
    takeStoredValueForKey(value, _Framework.OWNER_KEY);
  }

  public void setOwnerRelationship(er.bugtracker.People value) {
    if (_Framework.LOG.isDebugEnabled()) {
      _Framework.LOG.debug("updating owner from " + owner() + " to " + value);
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	setOwner(value);
    }
    else if (value == null) {
    	er.bugtracker.People oldValue = owner();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Framework.OWNER_KEY);
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, _Framework.OWNER_KEY);
    }
  }
  

  public static Framework createFramework(EOEditingContext editingContext, String name
, Integer ordering
) {
    Framework eo = (Framework) EOUtilities.createAndInsertInstance(editingContext, _Framework.ENTITY_NAME);    
		eo.setName(name);
		eo.setOrdering(ordering);
    return eo;
  }

  public static ERXFetchSpecification<Framework> fetchSpec() {
    return new ERXFetchSpecification<Framework>(_Framework.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Framework> fetchAllFrameworks(EOEditingContext editingContext) {
    return _Framework.fetchAllFrameworks(editingContext, null);
  }

  public static NSArray<Framework> fetchAllFrameworks(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Framework.fetchFrameworks(editingContext, null, sortOrderings);
  }

  public static NSArray<Framework> fetchFrameworks(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Framework> fetchSpec = new ERXFetchSpecification<Framework>(_Framework.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Framework> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Framework fetchFramework(EOEditingContext editingContext, String keyName, Object value) {
    return _Framework.fetchFramework(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Framework fetchFramework(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Framework> eoObjects = _Framework.fetchFrameworks(editingContext, qualifier, null);
    Framework eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Framework that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Framework fetchRequiredFramework(EOEditingContext editingContext, String keyName, Object value) {
    return _Framework.fetchRequiredFramework(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Framework fetchRequiredFramework(EOEditingContext editingContext, EOQualifier qualifier) {
    Framework eoObject = _Framework.fetchFramework(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Framework that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Framework localInstanceIn(EOEditingContext editingContext, Framework eo) {
    Framework localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

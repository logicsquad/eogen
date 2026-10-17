// $LastChangedRevision$ DO NOT EDIT.  Make changes to Framework.java instead.
package er.bugtracker;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Framework extends er.extensions.eof.ERXGenericRecord {
	public static final String ENTITY_NAME = "Framework";

	// Attributes
	public static final String NAME_KEY = "name";
	public static final String ORDERING_KEY = "ordering";
	public static final String OWNED_SINCE_KEY = "ownedSince";

	// Relationships
	public static final String OWNER_KEY = "owner";

  private static Logger LOG = Logger.getLogger(_Framework.class);

  public Framework localInstanceIn(EOEditingContext editingContext) {
    Framework localInstance = (Framework)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String name() {
    return (String) storedValueForKey("name");
  }

  public void setName(String value) {
    if (_Framework.LOG.isDebugEnabled()) {
    	_Framework.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, "name");
  }

  public Integer ordering() {
    return (Integer) storedValueForKey("ordering");
  }

  public void setOrdering(Integer value) {
    if (_Framework.LOG.isDebugEnabled()) {
    	_Framework.LOG.debug( "updating ordering from " + ordering() + " to " + value);
    }
    takeStoredValueForKey(value, "ordering");
  }

  public NSTimestamp ownedSince() {
    return (NSTimestamp) storedValueForKey("ownedSince");
  }

  public void setOwnedSince(NSTimestamp value) {
    if (_Framework.LOG.isDebugEnabled()) {
    	_Framework.LOG.debug( "updating ownedSince from " + ownedSince() + " to " + value);
    }
    takeStoredValueForKey(value, "ownedSince");
  }

  public er.bugtracker.People owner() {
    return (er.bugtracker.People)storedValueForKey("owner");
  }

  public void setOwnerRelationship(er.bugtracker.People value) {
    if (_Framework.LOG.isDebugEnabled()) {
      _Framework.LOG.debug("updating owner from " + owner() + " to " + value);
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
  

  public static Framework createFramework(EOEditingContext editingContext, String name
, Integer ordering
) {
    Framework eo = (Framework) EOUtilities.createAndInsertInstance(editingContext, _Framework.ENTITY_NAME);    
		eo.setName(name);
		eo.setOrdering(ordering);
    return eo;
  }

  public static NSArray<Framework> fetchAllFrameworks(EOEditingContext editingContext) {
    return _Framework.fetchAllFrameworks(editingContext, null);
  }

  public static NSArray<Framework> fetchAllFrameworks(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Framework.fetchFrameworks(editingContext, null, sortOrderings);
  }

  public static NSArray<Framework> fetchFrameworks(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Framework.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Framework> eoObjects = (NSArray<Framework>)editingContext.objectsWithFetchSpecification(fetchSpec);
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
      eoObject = (Framework)eoObjects.objectAtIndex(0);
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
    Framework localInstance = (eo == null) ? null : (Framework)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

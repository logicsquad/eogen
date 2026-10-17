// $LastChangedRevision$ DO NOT EDIT.  Make changes to RequirementSubType.java instead.
package er.bugtracker;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _RequirementSubType extends er.extensions.eof.ERXGenericRecord {
	public static final String ENTITY_NAME = "RequirementSubType";

	// Attributes
	public static final String SUB_TYPE_DESCRIPTION_KEY = "subTypeDescription";

	// Relationships

  private static Logger LOG = Logger.getLogger(_RequirementSubType.class);

  public RequirementSubType localInstanceIn(EOEditingContext editingContext) {
    RequirementSubType localInstance = (RequirementSubType)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String subTypeDescription() {
    return (String) storedValueForKey("subTypeDescription");
  }

  public void setSubTypeDescription(String value) {
    if (_RequirementSubType.LOG.isDebugEnabled()) {
    	_RequirementSubType.LOG.debug( "updating subTypeDescription from " + subTypeDescription() + " to " + value);
    }
    takeStoredValueForKey(value, "subTypeDescription");
  }


  public static RequirementSubType createRequirementSubType(EOEditingContext editingContext, String subTypeDescription
) {
    RequirementSubType eo = (RequirementSubType) EOUtilities.createAndInsertInstance(editingContext, _RequirementSubType.ENTITY_NAME);    
		eo.setSubTypeDescription(subTypeDescription);
    return eo;
  }

  public static NSArray<RequirementSubType> fetchAllRequirementSubTypes(EOEditingContext editingContext) {
    return _RequirementSubType.fetchAllRequirementSubTypes(editingContext, null);
  }

  public static NSArray<RequirementSubType> fetchAllRequirementSubTypes(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _RequirementSubType.fetchRequirementSubTypes(editingContext, null, sortOrderings);
  }

  public static NSArray<RequirementSubType> fetchRequirementSubTypes(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_RequirementSubType.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<RequirementSubType> eoObjects = (NSArray<RequirementSubType>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static RequirementSubType fetchRequirementSubType(EOEditingContext editingContext, String keyName, Object value) {
    return _RequirementSubType.fetchRequirementSubType(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static RequirementSubType fetchRequirementSubType(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<RequirementSubType> eoObjects = _RequirementSubType.fetchRequirementSubTypes(editingContext, qualifier, null);
    RequirementSubType eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (RequirementSubType)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one RequirementSubType that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static RequirementSubType fetchRequiredRequirementSubType(EOEditingContext editingContext, String keyName, Object value) {
    return _RequirementSubType.fetchRequiredRequirementSubType(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static RequirementSubType fetchRequiredRequirementSubType(EOEditingContext editingContext, EOQualifier qualifier) {
    RequirementSubType eoObject = _RequirementSubType.fetchRequirementSubType(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no RequirementSubType that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static RequirementSubType localInstanceIn(EOEditingContext editingContext, RequirementSubType eo) {
    RequirementSubType localInstance = (eo == null) ? null : (RequirementSubType)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

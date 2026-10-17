// $LastChangedRevision$ DO NOT EDIT.  Make changes to RequirementType.java instead.
package er.bugtracker;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _RequirementType extends er.extensions.eof.ERXGenericRecord {
	public static final String ENTITY_NAME = "RequirementType";

	// Attributes
	public static final String TYPE_DESCRIPTION_KEY = "typeDescription";

	// Relationships

  private static Logger LOG = Logger.getLogger(_RequirementType.class);

  public RequirementType localInstanceIn(EOEditingContext editingContext) {
    RequirementType localInstance = (RequirementType)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String typeDescription() {
    return (String) storedValueForKey("typeDescription");
  }

  public void setTypeDescription(String value) {
    if (_RequirementType.LOG.isDebugEnabled()) {
    	_RequirementType.LOG.debug( "updating typeDescription from " + typeDescription() + " to " + value);
    }
    takeStoredValueForKey(value, "typeDescription");
  }


  public static RequirementType createRequirementType(EOEditingContext editingContext, String typeDescription
) {
    RequirementType eo = (RequirementType) EOUtilities.createAndInsertInstance(editingContext, _RequirementType.ENTITY_NAME);    
		eo.setTypeDescription(typeDescription);
    return eo;
  }

  public static NSArray<RequirementType> fetchAllRequirementTypes(EOEditingContext editingContext) {
    return _RequirementType.fetchAllRequirementTypes(editingContext, null);
  }

  public static NSArray<RequirementType> fetchAllRequirementTypes(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _RequirementType.fetchRequirementTypes(editingContext, null, sortOrderings);
  }

  public static NSArray<RequirementType> fetchRequirementTypes(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_RequirementType.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<RequirementType> eoObjects = (NSArray<RequirementType>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static RequirementType fetchRequirementType(EOEditingContext editingContext, String keyName, Object value) {
    return _RequirementType.fetchRequirementType(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static RequirementType fetchRequirementType(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<RequirementType> eoObjects = _RequirementType.fetchRequirementTypes(editingContext, qualifier, null);
    RequirementType eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (RequirementType)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one RequirementType that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static RequirementType fetchRequiredRequirementType(EOEditingContext editingContext, String keyName, Object value) {
    return _RequirementType.fetchRequiredRequirementType(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static RequirementType fetchRequiredRequirementType(EOEditingContext editingContext, EOQualifier qualifier) {
    RequirementType eoObject = _RequirementType.fetchRequirementType(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no RequirementType that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static RequirementType localInstanceIn(EOEditingContext editingContext, RequirementType eo) {
    RequirementType localInstance = (eo == null) ? null : (RequirementType)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

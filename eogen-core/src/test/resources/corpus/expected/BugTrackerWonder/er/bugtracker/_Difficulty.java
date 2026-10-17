// DO NOT EDIT.  Make changes to Difficulty.java instead.
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
public abstract class _Difficulty extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Difficulty";

  // Attribute Keys
  public static final ERXKey<String> DIFFICULTY_DESCRIPTION = new ERXKey<String>("difficultyDescription", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String DIFFICULTY_DESCRIPTION_KEY = DIFFICULTY_DESCRIPTION.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_Difficulty.class);

  public Difficulty localInstanceIn(EOEditingContext editingContext) {
    Difficulty localInstance = (Difficulty)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String difficultyDescription() {
    return (String) storedValueForKey(_Difficulty.DIFFICULTY_DESCRIPTION_KEY);
  }

  public void setDifficultyDescription(String value) {
    log.debug( "updating difficultyDescription from {} to {}", difficultyDescription(), value);
    takeStoredValueForKey(value, _Difficulty.DIFFICULTY_DESCRIPTION_KEY);
  }


  public static Difficulty createDifficulty(EOEditingContext editingContext, String difficultyDescription
) {
    Difficulty eo = (Difficulty) EOUtilities.createAndInsertInstance(editingContext, _Difficulty.ENTITY_NAME);
    eo.setDifficultyDescription(difficultyDescription);
    return eo;
  }

  public static ERXFetchSpecification<Difficulty> fetchSpec() {
    return new ERXFetchSpecification<Difficulty>(_Difficulty.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Difficulty> fetchAllDifficulties(EOEditingContext editingContext) {
    return _Difficulty.fetchAllDifficulties(editingContext, null);
  }

  public static NSArray<Difficulty> fetchAllDifficulties(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Difficulty.fetchDifficulties(editingContext, null, sortOrderings);
  }

  public static NSArray<Difficulty> fetchDifficulties(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Difficulty> fetchSpec = new ERXFetchSpecification<Difficulty>(_Difficulty.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Difficulty> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Difficulty fetchDifficulty(EOEditingContext editingContext, String keyName, Object value) {
    return _Difficulty.fetchDifficulty(editingContext, ERXQ.equals(keyName, value));
  }

  public static Difficulty fetchDifficulty(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Difficulty> eoObjects = _Difficulty.fetchDifficulties(editingContext, qualifier, null);
    Difficulty eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Difficulty that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Difficulty fetchRequiredDifficulty(EOEditingContext editingContext, String keyName, Object value) {
    return _Difficulty.fetchRequiredDifficulty(editingContext, ERXQ.equals(keyName, value));
  }

  public static Difficulty fetchRequiredDifficulty(EOEditingContext editingContext, EOQualifier qualifier) {
    Difficulty eoObject = _Difficulty.fetchDifficulty(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Difficulty that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Difficulty localInstanceIn(EOEditingContext editingContext, Difficulty eo) {
    Difficulty localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

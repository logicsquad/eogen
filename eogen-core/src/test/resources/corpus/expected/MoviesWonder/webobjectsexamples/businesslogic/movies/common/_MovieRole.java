// DO NOT EDIT.  Make changes to MovieRole.java instead.
package webobjectsexamples.businesslogic.movies.common;

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
public abstract class _MovieRole extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "MovieRole";

  // Attribute Keys
  public static final ERXKey<String> ROLE_NAME = new ERXKey<String>("roleName", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<webobjectsexamples.businesslogic.movies.common.Movie> MOVIE = new ERXKey<webobjectsexamples.businesslogic.movies.common.Movie>("movie", Type.ToOneRelationship);
  public static final ERXKey<webobjectsexamples.businesslogic.movies.common.Talent> TALENT = new ERXKey<webobjectsexamples.businesslogic.movies.common.Talent>("talent", Type.ToOneRelationship);

  // Attributes
  public static final String ROLE_NAME_KEY = ROLE_NAME.key();

  // Relationships
  public static final String MOVIE_KEY = MOVIE.key();
  public static final String TALENT_KEY = TALENT.key();

  private static final Logger log = LoggerFactory.getLogger(_MovieRole.class);

  public MovieRole localInstanceIn(EOEditingContext editingContext) {
    MovieRole localInstance = (MovieRole)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String roleName() {
    return (String) storedValueForKey(_MovieRole.ROLE_NAME_KEY);
  }

  public void setRoleName(String value) {
    log.debug( "updating roleName from {} to {}", roleName(), value);
    takeStoredValueForKey(value, _MovieRole.ROLE_NAME_KEY);
  }

  public webobjectsexamples.businesslogic.movies.common.Movie movie() {
    return (webobjectsexamples.businesslogic.movies.common.Movie)storedValueForKey(_MovieRole.MOVIE_KEY);
  }

  public void setMovie(webobjectsexamples.businesslogic.movies.common.Movie value) {
    takeStoredValueForKey(value, _MovieRole.MOVIE_KEY);
  }

  public void setMovieRelationship(webobjectsexamples.businesslogic.movies.common.Movie value) {
    log.debug("updating movie from {} to {}", movie(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setMovie(value);
    }
    else if (value == null) {
      webobjectsexamples.businesslogic.movies.common.Movie oldValue = movie();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _MovieRole.MOVIE_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _MovieRole.MOVIE_KEY);
    }
  }

  public webobjectsexamples.businesslogic.movies.common.Talent talent() {
    return (webobjectsexamples.businesslogic.movies.common.Talent)storedValueForKey(_MovieRole.TALENT_KEY);
  }

  public void setTalent(webobjectsexamples.businesslogic.movies.common.Talent value) {
    takeStoredValueForKey(value, _MovieRole.TALENT_KEY);
  }

  public void setTalentRelationship(webobjectsexamples.businesslogic.movies.common.Talent value) {
    log.debug("updating talent from {} to {}", talent(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setTalent(value);
    }
    else if (value == null) {
      webobjectsexamples.businesslogic.movies.common.Talent oldValue = talent();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _MovieRole.TALENT_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _MovieRole.TALENT_KEY);
    }
  }


  public static MovieRole createMovieRole(EOEditingContext editingContext, webobjectsexamples.businesslogic.movies.common.Movie movie, webobjectsexamples.businesslogic.movies.common.Talent talent) {
    MovieRole eo = (MovieRole) EOUtilities.createAndInsertInstance(editingContext, _MovieRole.ENTITY_NAME);
    eo.setMovieRelationship(movie);
    eo.setTalentRelationship(talent);
    return eo;
  }

  public static ERXFetchSpecification<MovieRole> fetchSpec() {
    return new ERXFetchSpecification<MovieRole>(_MovieRole.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<MovieRole> fetchAllMovieRoles(EOEditingContext editingContext) {
    return _MovieRole.fetchAllMovieRoles(editingContext, null);
  }

  public static NSArray<MovieRole> fetchAllMovieRoles(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _MovieRole.fetchMovieRoles(editingContext, null, sortOrderings);
  }

  public static NSArray<MovieRole> fetchMovieRoles(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<MovieRole> fetchSpec = new ERXFetchSpecification<MovieRole>(_MovieRole.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<MovieRole> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static MovieRole fetchMovieRole(EOEditingContext editingContext, String keyName, Object value) {
    return _MovieRole.fetchMovieRole(editingContext, ERXQ.equals(keyName, value));
  }

  public static MovieRole fetchMovieRole(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<MovieRole> eoObjects = _MovieRole.fetchMovieRoles(editingContext, qualifier, null);
    MovieRole eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one MovieRole that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static MovieRole fetchRequiredMovieRole(EOEditingContext editingContext, String keyName, Object value) {
    return _MovieRole.fetchRequiredMovieRole(editingContext, ERXQ.equals(keyName, value));
  }

  public static MovieRole fetchRequiredMovieRole(EOEditingContext editingContext, EOQualifier qualifier) {
    MovieRole eoObject = _MovieRole.fetchMovieRole(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no MovieRole that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static MovieRole localInstanceIn(EOEditingContext editingContext, MovieRole eo) {
    MovieRole localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

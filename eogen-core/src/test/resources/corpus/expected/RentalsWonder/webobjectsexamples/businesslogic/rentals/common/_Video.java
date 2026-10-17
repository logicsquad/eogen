// DO NOT EDIT.  Make changes to Video.java instead.
package webobjectsexamples.businesslogic.rentals.common;

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
public abstract class _Video extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Video";

  // Attribute Keys

  // Relationship Keys
  public static final ERXKey<webobjectsexamples.businesslogic.movies.common.Movie> MOVIE = new ERXKey<webobjectsexamples.businesslogic.movies.common.Movie>("movie", Type.ToOneRelationship);
  public static final ERXKey<webobjectsexamples.businesslogic.rentals.common.RentalTerms> RENTAL_TERMS = new ERXKey<webobjectsexamples.businesslogic.rentals.common.RentalTerms>("rentalTerms", Type.ToOneRelationship);
  public static final ERXKey<webobjectsexamples.businesslogic.rentals.common.Unit> UNITS = new ERXKey<webobjectsexamples.businesslogic.rentals.common.Unit>("units", Type.ToManyRelationship);

  // Attributes

  // Relationships
  public static final String MOVIE_KEY = MOVIE.key();
  public static final String RENTAL_TERMS_KEY = RENTAL_TERMS.key();
  public static final String UNITS_KEY = UNITS.key();

  private static final Logger log = LoggerFactory.getLogger(_Video.class);

  public Video localInstanceIn(EOEditingContext editingContext) {
    Video localInstance = (Video)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public webobjectsexamples.businesslogic.movies.common.Movie movie() {
    return (webobjectsexamples.businesslogic.movies.common.Movie)storedValueForKey(_Video.MOVIE_KEY);
  }

  public void setMovie(webobjectsexamples.businesslogic.movies.common.Movie value) {
    takeStoredValueForKey(value, _Video.MOVIE_KEY);
  }

  public void setMovieRelationship(webobjectsexamples.businesslogic.movies.common.Movie value) {
    log.debug("updating movie from {} to {}", movie(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setMovie(value);
    }
    else if (value == null) {
      webobjectsexamples.businesslogic.movies.common.Movie oldValue = movie();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Video.MOVIE_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Video.MOVIE_KEY);
    }
  }

  public webobjectsexamples.businesslogic.rentals.common.RentalTerms rentalTerms() {
    return (webobjectsexamples.businesslogic.rentals.common.RentalTerms)storedValueForKey(_Video.RENTAL_TERMS_KEY);
  }

  public void setRentalTerms(webobjectsexamples.businesslogic.rentals.common.RentalTerms value) {
    takeStoredValueForKey(value, _Video.RENTAL_TERMS_KEY);
  }

  public void setRentalTermsRelationship(webobjectsexamples.businesslogic.rentals.common.RentalTerms value) {
    log.debug("updating rentalTerms from {} to {}", rentalTerms(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setRentalTerms(value);
    }
    else if (value == null) {
      webobjectsexamples.businesslogic.rentals.common.RentalTerms oldValue = rentalTerms();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Video.RENTAL_TERMS_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Video.RENTAL_TERMS_KEY);
    }
  }

  public NSArray<webobjectsexamples.businesslogic.rentals.common.Unit> units() {
    return (NSArray<webobjectsexamples.businesslogic.rentals.common.Unit>)storedValueForKey(_Video.UNITS_KEY);
  }

  public NSArray<webobjectsexamples.businesslogic.rentals.common.Unit> units(EOQualifier qualifier) {
    return units(qualifier, null, false);
  }

  public NSArray<webobjectsexamples.businesslogic.rentals.common.Unit> units(EOQualifier qualifier, boolean fetch) {
    return units(qualifier, null, fetch);
  }

  public NSArray<webobjectsexamples.businesslogic.rentals.common.Unit> units(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<webobjectsexamples.businesslogic.rentals.common.Unit> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(webobjectsexamples.businesslogic.rentals.common.Unit.VIDEO_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = webobjectsexamples.businesslogic.rentals.common.Unit.fetchUnits(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = units();
      if (qualifier != null) {
        results = (NSArray<webobjectsexamples.businesslogic.rentals.common.Unit>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<webobjectsexamples.businesslogic.rentals.common.Unit>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToUnits(webobjectsexamples.businesslogic.rentals.common.Unit object) {
    includeObjectIntoPropertyWithKey(object, _Video.UNITS_KEY);
  }

  public void removeFromUnits(webobjectsexamples.businesslogic.rentals.common.Unit object) {
    excludeObjectFromPropertyWithKey(object, _Video.UNITS_KEY);
  }

  public void addToUnitsRelationship(webobjectsexamples.businesslogic.rentals.common.Unit object) {
    log.debug("adding {} to units relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToUnits(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Video.UNITS_KEY);
    }
  }

  public void removeFromUnitsRelationship(webobjectsexamples.businesslogic.rentals.common.Unit object) {
    log.debug("removing {} from units relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromUnits(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Video.UNITS_KEY);
    }
  }

  public webobjectsexamples.businesslogic.rentals.common.Unit createUnitsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  webobjectsexamples.businesslogic.rentals.common.Unit.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Video.UNITS_KEY);
    return (webobjectsexamples.businesslogic.rentals.common.Unit) eo;
  }

  public void deleteUnitsRelationship(webobjectsexamples.businesslogic.rentals.common.Unit object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Video.UNITS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllUnitsRelationships() {
    Enumeration<webobjectsexamples.businesslogic.rentals.common.Unit> objects = units().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteUnitsRelationship(objects.nextElement());
    }
  }


  public static Video createVideo(EOEditingContext editingContext, webobjectsexamples.businesslogic.movies.common.Movie movie, webobjectsexamples.businesslogic.rentals.common.RentalTerms rentalTerms) {
    Video eo = (Video) EOUtilities.createAndInsertInstance(editingContext, _Video.ENTITY_NAME);
    eo.setMovieRelationship(movie);
    eo.setRentalTermsRelationship(rentalTerms);
    return eo;
  }

  public static ERXFetchSpecification<Video> fetchSpec() {
    return new ERXFetchSpecification<Video>(_Video.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Video> fetchAllVideos(EOEditingContext editingContext) {
    return _Video.fetchAllVideos(editingContext, null);
  }

  public static NSArray<Video> fetchAllVideos(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Video.fetchVideos(editingContext, null, sortOrderings);
  }

  public static NSArray<Video> fetchVideos(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Video> fetchSpec = new ERXFetchSpecification<Video>(_Video.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Video> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Video fetchVideo(EOEditingContext editingContext, String keyName, Object value) {
    return _Video.fetchVideo(editingContext, ERXQ.equals(keyName, value));
  }

  public static Video fetchVideo(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Video> eoObjects = _Video.fetchVideos(editingContext, qualifier, null);
    Video eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Video that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Video fetchRequiredVideo(EOEditingContext editingContext, String keyName, Object value) {
    return _Video.fetchRequiredVideo(editingContext, ERXQ.equals(keyName, value));
  }

  public static Video fetchRequiredVideo(EOEditingContext editingContext, EOQualifier qualifier) {
    Video eoObject = _Video.fetchVideo(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Video that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Video localInstanceIn(EOEditingContext editingContext, Video eo) {
    Video localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

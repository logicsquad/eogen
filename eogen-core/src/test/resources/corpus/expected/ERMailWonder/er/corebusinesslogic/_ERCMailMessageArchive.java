// DO NOT EDIT.  Make changes to ERCMailMessageArchive.java instead.
package er.corebusinesslogic;

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
public abstract class _ERCMailMessageArchive extends er.corebusinesslogic.ERCMailMessage {
  public static final String ENTITY_NAME = "ERCMailMessageArchive";

  // Attribute Keys

  // Relationship Keys

  // Attributes

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_ERCMailMessageArchive.class);

  @Override
  public ERCMailMessageArchive localInstanceIn(EOEditingContext editingContext) {
    ERCMailMessageArchive localInstance = (ERCMailMessageArchive)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }


  public static ERCMailMessageArchive createERCMailMessageArchive(EOEditingContext editingContext, Boolean contentGzipped
, NSTimestamp created
, String exceptionReason
, String fromAddress
, Boolean isRead
, NSTimestamp lastModified
, Boolean shouldArchiveSentMail
, String state
, String text
, String title
, String toAddresses
) {
    ERCMailMessageArchive eo = (ERCMailMessageArchive) EOUtilities.createAndInsertInstance(editingContext, _ERCMailMessageArchive.ENTITY_NAME);
    eo.setContentGzipped(contentGzipped);
    eo.setCreated(created);
    eo.setExceptionReason(exceptionReason);
    eo.setFromAddress(fromAddress);
    eo.setIsRead(isRead);
    eo.setLastModified(lastModified);
    eo.setShouldArchiveSentMail(shouldArchiveSentMail);
    eo.setState(state);
    eo.setText(text);
    eo.setTitle(title);
    eo.setToAddresses(toAddresses);
    return eo;
  }

  public static ERXFetchSpecification<ERCMailMessageArchive> fetchSpecForERCMailMessageArchive() {
    return new ERXFetchSpecification<ERCMailMessageArchive>(_ERCMailMessageArchive.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERCMailMessageArchive> fetchAllERCMailMessageArchives(EOEditingContext editingContext) {
    return _ERCMailMessageArchive.fetchAllERCMailMessageArchives(editingContext, null);
  }

  public static NSArray<ERCMailMessageArchive> fetchAllERCMailMessageArchives(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERCMailMessageArchive.fetchERCMailMessageArchives(editingContext, null, sortOrderings);
  }

  public static NSArray<ERCMailMessageArchive> fetchERCMailMessageArchives(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERCMailMessageArchive> fetchSpec = new ERXFetchSpecification<ERCMailMessageArchive>(_ERCMailMessageArchive.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERCMailMessageArchive> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERCMailMessageArchive fetchERCMailMessageArchive(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCMailMessageArchive.fetchERCMailMessageArchive(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCMailMessageArchive fetchERCMailMessageArchive(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERCMailMessageArchive> eoObjects = _ERCMailMessageArchive.fetchERCMailMessageArchives(editingContext, qualifier, null);
    ERCMailMessageArchive eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERCMailMessageArchive that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCMailMessageArchive fetchRequiredERCMailMessageArchive(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCMailMessageArchive.fetchRequiredERCMailMessageArchive(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCMailMessageArchive fetchRequiredERCMailMessageArchive(EOEditingContext editingContext, EOQualifier qualifier) {
    ERCMailMessageArchive eoObject = _ERCMailMessageArchive.fetchERCMailMessageArchive(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERCMailMessageArchive that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCMailMessageArchive localInstanceIn(EOEditingContext editingContext, ERCMailMessageArchive eo) {
    ERCMailMessageArchive localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

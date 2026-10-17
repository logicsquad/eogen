// DO NOT EDIT.  Make changes to ERCMailMessage.java instead.
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
public abstract class _ERCMailMessage extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "ERCMailMessage";

  // Attribute Keys
  public static final ERXKey<String> BCC_ADDRESSES = new ERXKey<String>("bccAddresses", Type.Attribute);
  public static final ERXKey<String> CC_ADDRESSES = new ERXKey<String>("ccAddresses", Type.Attribute);
  public static final ERXKey<Boolean> CONTENT_GZIPPED = new ERXKey<Boolean>("contentGzipped", Type.Attribute);
  public static final ERXKey<NSTimestamp> CREATED = new ERXKey<NSTimestamp>("created", Type.Attribute);
  public static final ERXKey<NSTimestamp> DATE_SENT = new ERXKey<NSTimestamp>("dateSent", Type.Attribute);
  public static final ERXKey<String> EXCEPTION_REASON = new ERXKey<String>("exceptionReason", Type.Attribute);
  public static final ERXKey<String> FROM_ADDRESS = new ERXKey<String>("fromAddress", Type.Attribute);
  public static final ERXKey<Boolean> IS_READ = new ERXKey<Boolean>("isRead", Type.Attribute);
  public static final ERXKey<NSTimestamp> LAST_MODIFIED = new ERXKey<NSTimestamp>("lastModified", Type.Attribute);
  public static final ERXKey<String> PLAIN_TEXT = new ERXKey<String>("plainText", Type.Attribute);
  public static final ERXKey<NSData> PLAIN_TEXT_COMPRESSED = new ERXKey<NSData>("plainTextCompressed", Type.Attribute);
  public static final ERXKey<String> REPLY_TO_ADDRESS = new ERXKey<String>("replyToAddress", Type.Attribute);
  public static final ERXKey<Boolean> SHOULD_ARCHIVE_SENT_MAIL = new ERXKey<Boolean>("shouldArchiveSentMail", Type.Attribute);
  public static final ERXKey<er.corebusinesslogic.ERCMailState> STATE = new ERXKey<er.corebusinesslogic.ERCMailState>("state", Type.Attribute);
  public static final ERXKey<String> TEXT = new ERXKey<String>("text", Type.Attribute);
  public static final ERXKey<NSData> TEXT_COMPRESSED = new ERXKey<NSData>("textCompressed", Type.Attribute);
  public static final ERXKey<String> TITLE = new ERXKey<String>("title", Type.Attribute);
  public static final ERXKey<String> TO_ADDRESSES = new ERXKey<String>("toAddresses", Type.Attribute);
  public static final ERXKey<String> X_MAILER = new ERXKey<String>("xMailer", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.corebusinesslogic.ERCMessageAttachment> ATTACHMENTS = new ERXKey<er.corebusinesslogic.ERCMessageAttachment>("attachments", Type.ToManyRelationship);

  // Attributes
  public static final String BCC_ADDRESSES_KEY = BCC_ADDRESSES.key();
  public static final String CC_ADDRESSES_KEY = CC_ADDRESSES.key();
  public static final String CONTENT_GZIPPED_KEY = CONTENT_GZIPPED.key();
  public static final String CREATED_KEY = CREATED.key();
  public static final String DATE_SENT_KEY = DATE_SENT.key();
  public static final String EXCEPTION_REASON_KEY = EXCEPTION_REASON.key();
  public static final String FROM_ADDRESS_KEY = FROM_ADDRESS.key();
  public static final String IS_READ_KEY = IS_READ.key();
  public static final String LAST_MODIFIED_KEY = LAST_MODIFIED.key();
  public static final String PLAIN_TEXT_KEY = PLAIN_TEXT.key();
  public static final String PLAIN_TEXT_COMPRESSED_KEY = PLAIN_TEXT_COMPRESSED.key();
  public static final String REPLY_TO_ADDRESS_KEY = REPLY_TO_ADDRESS.key();
  public static final String SHOULD_ARCHIVE_SENT_MAIL_KEY = SHOULD_ARCHIVE_SENT_MAIL.key();
  public static final String STATE_KEY = STATE.key();
  public static final String TEXT_KEY = TEXT.key();
  public static final String TEXT_COMPRESSED_KEY = TEXT_COMPRESSED.key();
  public static final String TITLE_KEY = TITLE.key();
  public static final String TO_ADDRESSES_KEY = TO_ADDRESSES.key();
  public static final String X_MAILER_KEY = X_MAILER.key();

  // Relationships
  public static final String ATTACHMENTS_KEY = ATTACHMENTS.key();

  private static final Logger log = LoggerFactory.getLogger(_ERCMailMessage.class);

  public ERCMailMessage localInstanceIn(EOEditingContext editingContext) {
    ERCMailMessage localInstance = (ERCMailMessage)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String bccAddresses() {
    return (String) storedValueForKey(_ERCMailMessage.BCC_ADDRESSES_KEY);
  }

  public void setBccAddresses(String value) {
    log.debug( "updating bccAddresses from {} to {}", bccAddresses(), value);
    takeStoredValueForKey(value, _ERCMailMessage.BCC_ADDRESSES_KEY);
  }

  public String ccAddresses() {
    return (String) storedValueForKey(_ERCMailMessage.CC_ADDRESSES_KEY);
  }

  public void setCcAddresses(String value) {
    log.debug( "updating ccAddresses from {} to {}", ccAddresses(), value);
    takeStoredValueForKey(value, _ERCMailMessage.CC_ADDRESSES_KEY);
  }

  public Boolean contentGzipped() {
    return (Boolean) storedValueForKey(_ERCMailMessage.CONTENT_GZIPPED_KEY);
  }

  public void setContentGzipped(Boolean value) {
    log.debug( "updating contentGzipped from {} to {}", contentGzipped(), value);
    takeStoredValueForKey(value, _ERCMailMessage.CONTENT_GZIPPED_KEY);
  }

  public NSTimestamp created() {
    return (NSTimestamp) storedValueForKey(_ERCMailMessage.CREATED_KEY);
  }

  public void setCreated(NSTimestamp value) {
    log.debug( "updating created from {} to {}", created(), value);
    takeStoredValueForKey(value, _ERCMailMessage.CREATED_KEY);
  }

  public NSTimestamp dateSent() {
    return (NSTimestamp) storedValueForKey(_ERCMailMessage.DATE_SENT_KEY);
  }

  public void setDateSent(NSTimestamp value) {
    log.debug( "updating dateSent from {} to {}", dateSent(), value);
    takeStoredValueForKey(value, _ERCMailMessage.DATE_SENT_KEY);
  }

  public String exceptionReason() {
    return (String) storedValueForKey(_ERCMailMessage.EXCEPTION_REASON_KEY);
  }

  public void setExceptionReason(String value) {
    log.debug( "updating exceptionReason from {} to {}", exceptionReason(), value);
    takeStoredValueForKey(value, _ERCMailMessage.EXCEPTION_REASON_KEY);
  }

  public String fromAddress() {
    return (String) storedValueForKey(_ERCMailMessage.FROM_ADDRESS_KEY);
  }

  public void setFromAddress(String value) {
    log.debug( "updating fromAddress from {} to {}", fromAddress(), value);
    takeStoredValueForKey(value, _ERCMailMessage.FROM_ADDRESS_KEY);
  }

  public Boolean isRead() {
    return (Boolean) storedValueForKey(_ERCMailMessage.IS_READ_KEY);
  }

  public void setIsRead(Boolean value) {
    log.debug( "updating isRead from {} to {}", isRead(), value);
    takeStoredValueForKey(value, _ERCMailMessage.IS_READ_KEY);
  }

  public NSTimestamp lastModified() {
    return (NSTimestamp) storedValueForKey(_ERCMailMessage.LAST_MODIFIED_KEY);
  }

  public void setLastModified(NSTimestamp value) {
    log.debug( "updating lastModified from {} to {}", lastModified(), value);
    takeStoredValueForKey(value, _ERCMailMessage.LAST_MODIFIED_KEY);
  }

  public String plainText() {
    return (String) storedValueForKey(_ERCMailMessage.PLAIN_TEXT_KEY);
  }

  public void setPlainText(String value) {
    log.debug( "updating plainText from {} to {}", plainText(), value);
    takeStoredValueForKey(value, _ERCMailMessage.PLAIN_TEXT_KEY);
  }

  public NSData plainTextCompressed() {
    return (NSData) storedValueForKey(_ERCMailMessage.PLAIN_TEXT_COMPRESSED_KEY);
  }

  public void setPlainTextCompressed(NSData value) {
    log.debug( "updating plainTextCompressed from {} to {}", plainTextCompressed(), value);
    takeStoredValueForKey(value, _ERCMailMessage.PLAIN_TEXT_COMPRESSED_KEY);
  }

  public String replyToAddress() {
    return (String) storedValueForKey(_ERCMailMessage.REPLY_TO_ADDRESS_KEY);
  }

  public void setReplyToAddress(String value) {
    log.debug( "updating replyToAddress from {} to {}", replyToAddress(), value);
    takeStoredValueForKey(value, _ERCMailMessage.REPLY_TO_ADDRESS_KEY);
  }

  public Boolean shouldArchiveSentMail() {
    return (Boolean) storedValueForKey(_ERCMailMessage.SHOULD_ARCHIVE_SENT_MAIL_KEY);
  }

  public void setShouldArchiveSentMail(Boolean value) {
    log.debug( "updating shouldArchiveSentMail from {} to {}", shouldArchiveSentMail(), value);
    takeStoredValueForKey(value, _ERCMailMessage.SHOULD_ARCHIVE_SENT_MAIL_KEY);
  }

  public er.corebusinesslogic.ERCMailState state() {
    return (er.corebusinesslogic.ERCMailState) storedValueForKey(_ERCMailMessage.STATE_KEY);
  }

  public void setState(er.corebusinesslogic.ERCMailState value) {
    log.debug( "updating state from {} to {}", state(), value);
    takeStoredValueForKey(value, _ERCMailMessage.STATE_KEY);
  }

  public String text() {
    return (String) storedValueForKey(_ERCMailMessage.TEXT_KEY);
  }

  public void setText(String value) {
    log.debug( "updating text from {} to {}", text(), value);
    takeStoredValueForKey(value, _ERCMailMessage.TEXT_KEY);
  }

  public NSData textCompressed() {
    return (NSData) storedValueForKey(_ERCMailMessage.TEXT_COMPRESSED_KEY);
  }

  public void setTextCompressed(NSData value) {
    log.debug( "updating textCompressed from {} to {}", textCompressed(), value);
    takeStoredValueForKey(value, _ERCMailMessage.TEXT_COMPRESSED_KEY);
  }

  public String title() {
    return (String) storedValueForKey(_ERCMailMessage.TITLE_KEY);
  }

  public void setTitle(String value) {
    log.debug( "updating title from {} to {}", title(), value);
    takeStoredValueForKey(value, _ERCMailMessage.TITLE_KEY);
  }

  public String toAddresses() {
    return (String) storedValueForKey(_ERCMailMessage.TO_ADDRESSES_KEY);
  }

  public void setToAddresses(String value) {
    log.debug( "updating toAddresses from {} to {}", toAddresses(), value);
    takeStoredValueForKey(value, _ERCMailMessage.TO_ADDRESSES_KEY);
  }

  public String xMailer() {
    return (String) storedValueForKey(_ERCMailMessage.X_MAILER_KEY);
  }

  public void setXMailer(String value) {
    log.debug( "updating xMailer from {} to {}", xMailer(), value);
    takeStoredValueForKey(value, _ERCMailMessage.X_MAILER_KEY);
  }

  public NSArray<er.corebusinesslogic.ERCMessageAttachment> attachments() {
    return (NSArray<er.corebusinesslogic.ERCMessageAttachment>)storedValueForKey(_ERCMailMessage.ATTACHMENTS_KEY);
  }

  public NSArray<er.corebusinesslogic.ERCMessageAttachment> attachments(EOQualifier qualifier) {
    return attachments(qualifier, null, false);
  }

  public NSArray<er.corebusinesslogic.ERCMessageAttachment> attachments(EOQualifier qualifier, boolean fetch) {
    return attachments(qualifier, null, fetch);
  }

  public NSArray<er.corebusinesslogic.ERCMessageAttachment> attachments(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.corebusinesslogic.ERCMessageAttachment> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(er.corebusinesslogic.ERCMessageAttachment.MAIL_MESSAGE_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = er.corebusinesslogic.ERCMessageAttachment.fetchERCMessageAttachments(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = attachments();
      if (qualifier != null) {
        results = (NSArray<er.corebusinesslogic.ERCMessageAttachment>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.corebusinesslogic.ERCMessageAttachment>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToAttachments(er.corebusinesslogic.ERCMessageAttachment object) {
    includeObjectIntoPropertyWithKey(object, _ERCMailMessage.ATTACHMENTS_KEY);
  }

  public void removeFromAttachments(er.corebusinesslogic.ERCMessageAttachment object) {
    excludeObjectFromPropertyWithKey(object, _ERCMailMessage.ATTACHMENTS_KEY);
  }

  public void addToAttachmentsRelationship(er.corebusinesslogic.ERCMessageAttachment object) {
    log.debug("adding {} to attachments relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToAttachments(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _ERCMailMessage.ATTACHMENTS_KEY);
    }
  }

  public void removeFromAttachmentsRelationship(er.corebusinesslogic.ERCMessageAttachment object) {
    log.debug("removing {} from attachments relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromAttachments(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _ERCMailMessage.ATTACHMENTS_KEY);
    }
  }

  public er.corebusinesslogic.ERCMessageAttachment createAttachmentsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.corebusinesslogic.ERCMessageAttachment.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _ERCMailMessage.ATTACHMENTS_KEY);
    return (er.corebusinesslogic.ERCMessageAttachment) eo;
  }

  public void deleteAttachmentsRelationship(er.corebusinesslogic.ERCMessageAttachment object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _ERCMailMessage.ATTACHMENTS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllAttachmentsRelationships() {
    Enumeration<er.corebusinesslogic.ERCMessageAttachment> objects = attachments().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteAttachmentsRelationship(objects.nextElement());
    }
  }


  public static ERCMailMessage createERCMailMessage(EOEditingContext editingContext, Boolean contentGzipped
, NSTimestamp created
, String fromAddress
, Boolean isRead
, NSTimestamp lastModified
, Boolean shouldArchiveSentMail
, er.corebusinesslogic.ERCMailState state
, String title
, String toAddresses
) {
    ERCMailMessage eo = (ERCMailMessage) EOUtilities.createAndInsertInstance(editingContext, _ERCMailMessage.ENTITY_NAME);
    eo.setContentGzipped(contentGzipped);
    eo.setCreated(created);
    eo.setFromAddress(fromAddress);
    eo.setIsRead(isRead);
    eo.setLastModified(lastModified);
    eo.setShouldArchiveSentMail(shouldArchiveSentMail);
    eo.setState(state);
    eo.setTitle(title);
    eo.setToAddresses(toAddresses);
    return eo;
  }

  public static ERXFetchSpecification<ERCMailMessage> fetchSpec() {
    return new ERXFetchSpecification<ERCMailMessage>(_ERCMailMessage.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<ERCMailMessage> fetchAllERCMailMessages(EOEditingContext editingContext) {
    return _ERCMailMessage.fetchAllERCMailMessages(editingContext, null);
  }

  public static NSArray<ERCMailMessage> fetchAllERCMailMessages(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _ERCMailMessage.fetchERCMailMessages(editingContext, null, sortOrderings);
  }

  public static NSArray<ERCMailMessage> fetchERCMailMessages(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<ERCMailMessage> fetchSpec = new ERXFetchSpecification<ERCMailMessage>(_ERCMailMessage.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<ERCMailMessage> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static ERCMailMessage fetchERCMailMessage(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCMailMessage.fetchERCMailMessage(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCMailMessage fetchERCMailMessage(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<ERCMailMessage> eoObjects = _ERCMailMessage.fetchERCMailMessages(editingContext, qualifier, null);
    ERCMailMessage eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ERCMailMessage that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCMailMessage fetchRequiredERCMailMessage(EOEditingContext editingContext, String keyName, Object value) {
    return _ERCMailMessage.fetchRequiredERCMailMessage(editingContext, ERXQ.equals(keyName, value));
  }

  public static ERCMailMessage fetchRequiredERCMailMessage(EOEditingContext editingContext, EOQualifier qualifier) {
    ERCMailMessage eoObject = _ERCMailMessage.fetchERCMailMessage(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ERCMailMessage that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ERCMailMessage localInstanceIn(EOEditingContext editingContext, ERCMailMessage eo) {
    ERCMailMessage localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
  public static NSArray<er.corebusinesslogic.ERCMailMessage> fetchMessagesToBeSent(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("messagesToBeSent", _ERCMailMessage.ENTITY_NAME);
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<er.corebusinesslogic.ERCMailMessage>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }

  public static NSArray<er.corebusinesslogic.ERCMailMessage> fetchMessagesToBeSent(EOEditingContext editingContext)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("messagesToBeSent", _ERCMailMessage.ENTITY_NAME);
    return (NSArray<er.corebusinesslogic.ERCMailMessage>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }

  public static NSArray<er.corebusinesslogic.ERCMailMessage> fetchRipeMessages(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("ripeMessages", _ERCMailMessage.ENTITY_NAME);
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<er.corebusinesslogic.ERCMailMessage>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }

  public static NSArray<er.corebusinesslogic.ERCMailMessage> fetchRipeMessages(EOEditingContext editingContext,
  NSTimestamp dateBinding)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("ripeMessages", _ERCMailMessage.ENTITY_NAME);
    NSMutableDictionary<String, Object> bindings = new NSMutableDictionary<String, Object>();
    bindings.takeValueForKey(dateBinding, "date");
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<er.corebusinesslogic.ERCMailMessage>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }

}

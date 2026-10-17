// DO NOT EDIT.  Make changes to Customer.java instead.
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
public abstract class _Customer extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Customer";

  // Attribute Keys
  public static final ERXKey<String> CITY = new ERXKey<String>("city", Type.Attribute);
  public static final ERXKey<String> FIRST_NAME = new ERXKey<String>("firstName", Type.Attribute);
  public static final ERXKey<String> LAST_NAME = new ERXKey<String>("lastName", Type.Attribute);
  public static final ERXKey<NSTimestamp> MEMBER_SINCE = new ERXKey<NSTimestamp>("memberSince", Type.Attribute);
  public static final ERXKey<String> PHONE = new ERXKey<String>("phone", Type.Attribute);
  public static final ERXKey<String> STATE = new ERXKey<String>("state", Type.Attribute);
  public static final ERXKey<String> STREET_ADDRESS = new ERXKey<String>("streetAddress", Type.Attribute);
  public static final ERXKey<String> ZIP = new ERXKey<String>("zip", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<webobjectsexamples.businesslogic.rentals.common.CreditCard> CREDIT_CARD = new ERXKey<webobjectsexamples.businesslogic.rentals.common.CreditCard>("creditCard", Type.ToOneRelationship);
  public static final ERXKey<webobjectsexamples.businesslogic.rentals.common.Rental> RENTALS = new ERXKey<webobjectsexamples.businesslogic.rentals.common.Rental>("rentals", Type.ToManyRelationship);

  // Attributes
  public static final String CITY_KEY = CITY.key();
  public static final String FIRST_NAME_KEY = FIRST_NAME.key();
  public static final String LAST_NAME_KEY = LAST_NAME.key();
  public static final String MEMBER_SINCE_KEY = MEMBER_SINCE.key();
  public static final String PHONE_KEY = PHONE.key();
  public static final String STATE_KEY = STATE.key();
  public static final String STREET_ADDRESS_KEY = STREET_ADDRESS.key();
  public static final String ZIP_KEY = ZIP.key();

  // Relationships
  public static final String CREDIT_CARD_KEY = CREDIT_CARD.key();
  public static final String RENTALS_KEY = RENTALS.key();

  private static final Logger log = LoggerFactory.getLogger(_Customer.class);

  public Customer localInstanceIn(EOEditingContext editingContext) {
    Customer localInstance = (Customer)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String city() {
    return (String) storedValueForKey(_Customer.CITY_KEY);
  }

  public void setCity(String value) {
    log.debug( "updating city from {} to {}", city(), value);
    takeStoredValueForKey(value, _Customer.CITY_KEY);
  }

  public String firstName() {
    return (String) storedValueForKey(_Customer.FIRST_NAME_KEY);
  }

  public void setFirstName(String value) {
    log.debug( "updating firstName from {} to {}", firstName(), value);
    takeStoredValueForKey(value, _Customer.FIRST_NAME_KEY);
  }

  public String lastName() {
    return (String) storedValueForKey(_Customer.LAST_NAME_KEY);
  }

  public void setLastName(String value) {
    log.debug( "updating lastName from {} to {}", lastName(), value);
    takeStoredValueForKey(value, _Customer.LAST_NAME_KEY);
  }

  public NSTimestamp memberSince() {
    return (NSTimestamp) storedValueForKey(_Customer.MEMBER_SINCE_KEY);
  }

  public void setMemberSince(NSTimestamp value) {
    log.debug( "updating memberSince from {} to {}", memberSince(), value);
    takeStoredValueForKey(value, _Customer.MEMBER_SINCE_KEY);
  }

  public String phone() {
    return (String) storedValueForKey(_Customer.PHONE_KEY);
  }

  public void setPhone(String value) {
    log.debug( "updating phone from {} to {}", phone(), value);
    takeStoredValueForKey(value, _Customer.PHONE_KEY);
  }

  public String state() {
    return (String) storedValueForKey(_Customer.STATE_KEY);
  }

  public void setState(String value) {
    log.debug( "updating state from {} to {}", state(), value);
    takeStoredValueForKey(value, _Customer.STATE_KEY);
  }

  public String streetAddress() {
    return (String) storedValueForKey(_Customer.STREET_ADDRESS_KEY);
  }

  public void setStreetAddress(String value) {
    log.debug( "updating streetAddress from {} to {}", streetAddress(), value);
    takeStoredValueForKey(value, _Customer.STREET_ADDRESS_KEY);
  }

  public String zip() {
    return (String) storedValueForKey(_Customer.ZIP_KEY);
  }

  public void setZip(String value) {
    log.debug( "updating zip from {} to {}", zip(), value);
    takeStoredValueForKey(value, _Customer.ZIP_KEY);
  }

  public webobjectsexamples.businesslogic.rentals.common.CreditCard creditCard() {
    return (webobjectsexamples.businesslogic.rentals.common.CreditCard)storedValueForKey(_Customer.CREDIT_CARD_KEY);
  }

  public void setCreditCard(webobjectsexamples.businesslogic.rentals.common.CreditCard value) {
    takeStoredValueForKey(value, _Customer.CREDIT_CARD_KEY);
  }

  public void setCreditCardRelationship(webobjectsexamples.businesslogic.rentals.common.CreditCard value) {
    log.debug("updating creditCard from {} to {}", creditCard(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setCreditCard(value);
    }
    else if (value == null) {
      webobjectsexamples.businesslogic.rentals.common.CreditCard oldValue = creditCard();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Customer.CREDIT_CARD_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Customer.CREDIT_CARD_KEY);
    }
  }

  public NSArray<webobjectsexamples.businesslogic.rentals.common.Rental> rentals() {
    return (NSArray<webobjectsexamples.businesslogic.rentals.common.Rental>)storedValueForKey(_Customer.RENTALS_KEY);
  }

  public NSArray<webobjectsexamples.businesslogic.rentals.common.Rental> rentals(EOQualifier qualifier) {
    return rentals(qualifier, null, false);
  }

  public NSArray<webobjectsexamples.businesslogic.rentals.common.Rental> rentals(EOQualifier qualifier, boolean fetch) {
    return rentals(qualifier, null, fetch);
  }

  public NSArray<webobjectsexamples.businesslogic.rentals.common.Rental> rentals(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<webobjectsexamples.businesslogic.rentals.common.Rental> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(webobjectsexamples.businesslogic.rentals.common.Rental.CUSTOMER_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = webobjectsexamples.businesslogic.rentals.common.Rental.fetchRentals(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = rentals();
      if (qualifier != null) {
        results = (NSArray<webobjectsexamples.businesslogic.rentals.common.Rental>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<webobjectsexamples.businesslogic.rentals.common.Rental>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToRentals(webobjectsexamples.businesslogic.rentals.common.Rental object) {
    includeObjectIntoPropertyWithKey(object, _Customer.RENTALS_KEY);
  }

  public void removeFromRentals(webobjectsexamples.businesslogic.rentals.common.Rental object) {
    excludeObjectFromPropertyWithKey(object, _Customer.RENTALS_KEY);
  }

  public void addToRentalsRelationship(webobjectsexamples.businesslogic.rentals.common.Rental object) {
    log.debug("adding {} to rentals relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToRentals(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Customer.RENTALS_KEY);
    }
  }

  public void removeFromRentalsRelationship(webobjectsexamples.businesslogic.rentals.common.Rental object) {
    log.debug("removing {} from rentals relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromRentals(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Customer.RENTALS_KEY);
    }
  }

  public webobjectsexamples.businesslogic.rentals.common.Rental createRentalsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  webobjectsexamples.businesslogic.rentals.common.Rental.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Customer.RENTALS_KEY);
    return (webobjectsexamples.businesslogic.rentals.common.Rental) eo;
  }

  public void deleteRentalsRelationship(webobjectsexamples.businesslogic.rentals.common.Rental object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Customer.RENTALS_KEY);
  }

  public void deleteAllRentalsRelationships() {
    Enumeration<webobjectsexamples.businesslogic.rentals.common.Rental> objects = rentals().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteRentalsRelationship(objects.nextElement());
    }
  }


  public static Customer createCustomer(EOEditingContext editingContext, String city
, String firstName
, String lastName
) {
    Customer eo = (Customer) EOUtilities.createAndInsertInstance(editingContext, _Customer.ENTITY_NAME);
    eo.setCity(city);
    eo.setFirstName(firstName);
    eo.setLastName(lastName);
    return eo;
  }

  public static ERXFetchSpecification<Customer> fetchSpec() {
    return new ERXFetchSpecification<Customer>(_Customer.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Customer> fetchAllCustomers(EOEditingContext editingContext) {
    return _Customer.fetchAllCustomers(editingContext, null);
  }

  public static NSArray<Customer> fetchAllCustomers(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Customer.fetchCustomers(editingContext, null, sortOrderings);
  }

  public static NSArray<Customer> fetchCustomers(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Customer> fetchSpec = new ERXFetchSpecification<Customer>(_Customer.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Customer> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Customer fetchCustomer(EOEditingContext editingContext, String keyName, Object value) {
    return _Customer.fetchCustomer(editingContext, ERXQ.equals(keyName, value));
  }

  public static Customer fetchCustomer(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Customer> eoObjects = _Customer.fetchCustomers(editingContext, qualifier, null);
    Customer eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Customer that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Customer fetchRequiredCustomer(EOEditingContext editingContext, String keyName, Object value) {
    return _Customer.fetchRequiredCustomer(editingContext, ERXQ.equals(keyName, value));
  }

  public static Customer fetchRequiredCustomer(EOEditingContext editingContext, EOQualifier qualifier) {
    Customer eoObject = _Customer.fetchCustomer(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Customer that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Customer localInstanceIn(EOEditingContext editingContext, Customer eo) {
    Customer localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}

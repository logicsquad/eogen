// DO NOT EDIT.  Make changes to Partial_AuthenticatedPerson.java instead.
package er.example.erxpartials.model;

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
public abstract class _Partial_AuthenticatedPerson extends er.extensions.partials.ERXPartial<er.example.erxpartials.model.Person> {
  public static final String ENTITY_NAME = "Person";

  // Attribute Keys
  public static final ERXKey<NSTimestamp> LAST_LOGIN_DATE = new ERXKey<NSTimestamp>("lastLoginDate", Type.Attribute);
  public static final ERXKey<String> PASSWORD = new ERXKey<String>("password", Type.Attribute);
  public static final ERXKey<String> USERNAME = new ERXKey<String>("username", Type.Attribute);

  // Relationship Keys

  // Attributes
  public static final String LAST_LOGIN_DATE_KEY = LAST_LOGIN_DATE.key();
  public static final String PASSWORD_KEY = PASSWORD.key();
  public static final String USERNAME_KEY = USERNAME.key();

  // Relationships

  private static final Logger log = LoggerFactory.getLogger(_Partial_AuthenticatedPerson.class);

  public NSTimestamp lastLoginDate() {
    return (NSTimestamp) storedValueForKey(_Partial_AuthenticatedPerson.LAST_LOGIN_DATE_KEY);
  }

  public void setLastLoginDate(NSTimestamp value) {
    log.debug( "updating lastLoginDate from {} to {}", lastLoginDate(), value);
    takeStoredValueForKey(value, _Partial_AuthenticatedPerson.LAST_LOGIN_DATE_KEY);
  }

  public String password() {
    return (String) storedValueForKey(_Partial_AuthenticatedPerson.PASSWORD_KEY);
  }

  public void setPassword(String value) {
    log.debug( "updating password from {} to {}", password(), value);
    takeStoredValueForKey(value, _Partial_AuthenticatedPerson.PASSWORD_KEY);
  }

  public String username() {
    return (String) storedValueForKey(_Partial_AuthenticatedPerson.USERNAME_KEY);
  }

  public void setUsername(String value) {
    log.debug( "updating username from {} to {}", username(), value);
    takeStoredValueForKey(value, _Partial_AuthenticatedPerson.USERNAME_KEY);
  }


  public Partial_AuthenticatedPerson initPartial_AuthenticatedPerson(EOEditingContext editingContext) {
    Partial_AuthenticatedPerson eo = (Partial_AuthenticatedPerson)this;
    return eo;
  }
}

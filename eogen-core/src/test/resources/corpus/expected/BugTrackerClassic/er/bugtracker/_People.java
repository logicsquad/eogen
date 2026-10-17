// $LastChangedRevision$ DO NOT EDIT.  Make changes to People.java instead.
package er.bugtracker;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _People extends er.extensions.eof.ERXGenericRecord {
	public static final String ENTITY_NAME = "People";

	// Attributes
	public static final String EMAIL_KEY = "email";
	public static final String IS_ACTIVE_KEY = "isActive";
	public static final String IS_ADMIN_KEY = "isAdmin";
	public static final String IS_CUSTOMER_SERVICE_KEY = "isCustomerService";
	public static final String IS_ENGINEERING_KEY = "isEngineering";
	public static final String LOGIN_KEY = "login";
	public static final String NAME_KEY = "name";
	public static final String PASSWORD_KEY = "password";

	// Relationships

  private static Logger LOG = Logger.getLogger(_People.class);

  public People localInstanceIn(EOEditingContext editingContext) {
    People localInstance = (People)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String email() {
    return (String) storedValueForKey("email");
  }

  public void setEmail(String value) {
    if (_People.LOG.isDebugEnabled()) {
    	_People.LOG.debug( "updating email from " + email() + " to " + value);
    }
    takeStoredValueForKey(value, "email");
  }

  public Boolean isActive() {
    return (Boolean) storedValueForKey("isActive");
  }

  public void setIsActive(Boolean value) {
    if (_People.LOG.isDebugEnabled()) {
    	_People.LOG.debug( "updating isActive from " + isActive() + " to " + value);
    }
    takeStoredValueForKey(value, "isActive");
  }

  public Boolean isAdmin() {
    return (Boolean) storedValueForKey("isAdmin");
  }

  public void setIsAdmin(Boolean value) {
    if (_People.LOG.isDebugEnabled()) {
    	_People.LOG.debug( "updating isAdmin from " + isAdmin() + " to " + value);
    }
    takeStoredValueForKey(value, "isAdmin");
  }

  public Boolean isCustomerService() {
    return (Boolean) storedValueForKey("isCustomerService");
  }

  public void setIsCustomerService(Boolean value) {
    if (_People.LOG.isDebugEnabled()) {
    	_People.LOG.debug( "updating isCustomerService from " + isCustomerService() + " to " + value);
    }
    takeStoredValueForKey(value, "isCustomerService");
  }

  public Boolean isEngineering() {
    return (Boolean) storedValueForKey("isEngineering");
  }

  public void setIsEngineering(Boolean value) {
    if (_People.LOG.isDebugEnabled()) {
    	_People.LOG.debug( "updating isEngineering from " + isEngineering() + " to " + value);
    }
    takeStoredValueForKey(value, "isEngineering");
  }

  public String login() {
    return (String) storedValueForKey("login");
  }

  public void setLogin(String value) {
    if (_People.LOG.isDebugEnabled()) {
    	_People.LOG.debug( "updating login from " + login() + " to " + value);
    }
    takeStoredValueForKey(value, "login");
  }

  public String name() {
    return (String) storedValueForKey("name");
  }

  public void setName(String value) {
    if (_People.LOG.isDebugEnabled()) {
    	_People.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, "name");
  }

  public String password() {
    return (String) storedValueForKey("password");
  }

  public void setPassword(String value) {
    if (_People.LOG.isDebugEnabled()) {
    	_People.LOG.debug( "updating password from " + password() + " to " + value);
    }
    takeStoredValueForKey(value, "password");
  }


  public static People createPeople(EOEditingContext editingContext, Boolean isActive
, Boolean isAdmin
, Boolean isCustomerService
, Boolean isEngineering
, String login
, String password
) {
    People eo = (People) EOUtilities.createAndInsertInstance(editingContext, _People.ENTITY_NAME);    
		eo.setIsActive(isActive);
		eo.setIsAdmin(isAdmin);
		eo.setIsCustomerService(isCustomerService);
		eo.setIsEngineering(isEngineering);
		eo.setLogin(login);
		eo.setPassword(password);
    return eo;
  }

  public static NSArray<People> fetchAllPeoples(EOEditingContext editingContext) {
    return _People.fetchAllPeoples(editingContext, null);
  }

  public static NSArray<People> fetchAllPeoples(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _People.fetchPeoples(editingContext, null, sortOrderings);
  }

  public static NSArray<People> fetchPeoples(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_People.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<People> eoObjects = (NSArray<People>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static People fetchPeople(EOEditingContext editingContext, String keyName, Object value) {
    return _People.fetchPeople(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static People fetchPeople(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<People> eoObjects = _People.fetchPeoples(editingContext, qualifier, null);
    People eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (People)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one People that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static People fetchRequiredPeople(EOEditingContext editingContext, String keyName, Object value) {
    return _People.fetchRequiredPeople(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static People fetchRequiredPeople(EOEditingContext editingContext, EOQualifier qualifier) {
    People eoObject = _People.fetchPeople(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no People that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static People localInstanceIn(EOEditingContext editingContext, People eo) {
    People localInstance = (eo == null) ? null : (People)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
  public static NSArray<er.bugtracker.People> fetchLogin(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("login", "People");
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<er.bugtracker.People>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
  public static NSArray<er.bugtracker.People> fetchLogin(EOEditingContext editingContext,
	String passwordBinding,
	String usernameBinding)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("login", "People");
    NSMutableDictionary<String, Object> bindings = new NSMutableDictionary<String, Object>();
    bindings.takeValueForKey(passwordBinding, "password");
    bindings.takeValueForKey(usernameBinding, "username");
	fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<er.bugtracker.People>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }
  
}

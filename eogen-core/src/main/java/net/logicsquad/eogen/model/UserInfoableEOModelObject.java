/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package net.logicsquad.eogen.model;

import java.util.HashMap;
import java.util.Map;

import net.logicsquad.eogen.utils.NotificationMap;

import com.uwyn.rife.tools.ObjectUtils;

public abstract class UserInfoableEOModelObject<T> extends EOModelObject<T> implements IUserInfoable, Cloneable {
	public static final String ENTITY_MODELER_KEY = "_EntityModeler";

	public static final String DOCUMENTATION_KEY = "documentation";

	public static final String USER_INFO = "userInfo";

	private PropertyChangeRepeater _userInfoRepeater;

	private NotificationMap<Object, Object> _userInfo;
	
	public UserInfoableEOModelObject() {
		_userInfoRepeater = new PropertyChangeRepeater(UserInfoableEOModelObject.USER_INFO);
		setUserInfo(new NotificationMap<Object, Object>(), false);
	}

	public NotificationMap<Object, Object> getUserInfo() {
		return _userInfo;
	}

	public void setUserInfo(Map<Object, Object> userInfo) {
		setUserInfo(userInfo, true);
	}

	public void setUserInfo(Map<Object, Object> userInfo, boolean fireEvents) {
		_userInfo = mapChanged(_userInfo, userInfo, _userInfoRepeater, fireEvents);
	}
	
	public void userInfoChanged(String path, Object oldValue, Object newValue) {
		firePropertyChange(UserInfoableEOModelObject.USER_INFO + "." + path, oldValue, newValue);
	}

	public EOModelMap getEntityModelerMap(boolean readWrite) {  
		NotificationMap<Object, Object> userInfo = getUserInfo();
		Map entityModelerMap = (Map) userInfo.get(UserInfoableEOModelObject.ENTITY_MODELER_KEY);
		if (entityModelerMap == null) {
			entityModelerMap = new HashMap<Object, Object>();
			if (readWrite) {
				userInfo._putWithoutEvents(UserInfoableEOModelObject.ENTITY_MODELER_KEY, entityModelerMap);
			}
		}
		return new EOModelMap(entityModelerMap);
	}
	
	/**
	 * Sets the documentation field on this object.  documentation writes
	 * into userInfo=>_EntityModeler=>Documentation.
	 * 
	 * @param documentation the documentation for this object
	 */
	public void setDocumentation(String documentation) {
		String oldDocumentation = getDocumentation();
		EOModelMap entityModelerMap = getEntityModelerMap(true);
		if (documentation == null) {
			entityModelerMap.remove(UserInfoableEOModelObject.DOCUMENTATION_KEY);
		} else {
			entityModelerMap.put(UserInfoableEOModelObject.DOCUMENTATION_KEY, documentation);
		}
		firePropertyChange(UserInfoableEOModelObject.DOCUMENTATION_KEY, oldDocumentation, documentation);
	}

	/**
	 * Returns the documentation for this object.
	 * 
	 * @return the documentation for this object
	 */
	public String getDocumentation() {
		EOModelMap entityModelerMap = getEntityModelerMap(false);
		String documentation = (String) entityModelerMap.get(UserInfoableEOModelObject.DOCUMENTATION_KEY);
		return documentation;
	}

	protected void writeUserInfo(EOModelMap modelMap) {
		EOModelMap entityModelerMap = getEntityModelerMap(false);
		if (entityModelerMap.isEmpty()) {
			getUserInfo()._removeWithoutEvents(UserInfoableEOModelObject.ENTITY_MODELER_KEY);
		} else {
			getUserInfo()._putWithoutEvents(UserInfoableEOModelObject.ENTITY_MODELER_KEY, entityModelerMap);
		}

		modelMap.setMap("userInfo", _userInfo, true);
		modelMap.remove("userDictionary");
	}

	protected void loadUserInfo(EOModelMap modelMap) {
		if (modelMap.containsKey("userDictionary")) {
			setUserInfo(modelMap.getMap("userDictionary", true), false);
		} else {
			setUserInfo(modelMap.getMap("userInfo", true), false);
		}
	}

	protected void _cloneUserInfoInto(UserInfoableEOModelObject<T> obj) {
		try {
			obj.setUserInfo(ObjectUtils.deepClone(_userInfo));
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Failed to clone user info: " + obj + ".", e);
		}
	}
}

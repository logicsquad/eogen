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
package net.logicsquad.eogen.utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NotificationMap<U, V> implements Map<U, V>, Cloneable {
	public static final String CONTENTS = "__contents__";

	private Map<U, V> myMap;

	private PropertyChangeSupport myPropertyChangeSupport;

	public NotificationMap() {
		this(null);
	}

	public NotificationMap(Map<U, V> _map) {
		if (_map == null) {
			myMap = new HashMap<U, V>();
		} else {
			myMap = _map;
		}
		myPropertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	@Override
	public NotificationMap<U, V> clone() throws CloneNotSupportedException {
		NotificationMap map = new NotificationMap<U, V>(new HashMap(myMap));
		return map;
	}

	public void clear() {
		myMap.clear();
		myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
	}

	public boolean containsKey(Object _key) {
		return myMap.containsKey(_key);
	}

	public boolean containsValue(Object _value) {
		return myMap.containsValue(_value);
	}

	public Set<Map.Entry<U, V>> entrySet() {
		return myMap.entrySet();
	}

	public boolean equals(Object _o) {
		return myMap.equals(_o);
	}

	public V get(Object _key) {
		return myMap.get(_key);
	}

	public int hashCode() {
		return myMap.hashCode();
	}

	public boolean isEmpty() {
		return myMap.isEmpty();
	}

	public Set<U> keySet() {
		return myMap.keySet();
	}

	public V put(U _key, V _value) {
		V oldValue = myMap.put(_key, _value);
		if (_key instanceof String) {
			myPropertyChangeSupport.firePropertyChange((String) _key, oldValue, _value);
			if (oldValue == null) {
				myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
			}
		} else {
			myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
		}
		return oldValue;
	}

	public V _putWithoutEvents(U _key, V _value) {
		V oldValue = myMap.put(_key, _value);
		return oldValue;
	}
	
	public void putAll(Map<? extends U, ? extends V> _t) {
		myMap.putAll(_t);
		myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
	}

	public V remove(Object _key) {
		V oldValue = myMap.remove(_key);
		if (_key instanceof String) {
			myPropertyChangeSupport.firePropertyChange((String) _key, oldValue, null);
			myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
		} else {
			myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
		}
		return oldValue;
	}

	public V _removeWithoutEvents(Object _key) {
		V oldValue = myMap.remove(_key);
		return oldValue;
	}
	
	public int size() {
		return myMap.size();
	}

	public Collection<V> values() {
		return myMap.values();
	}

	public void addPropertyChangeListener(PropertyChangeListener _listener) {
		myPropertyChangeSupport.addPropertyChangeListener(_listener);
	}

	public void addPropertyChangeListener(String _propertyName, PropertyChangeListener _listener) {
		myPropertyChangeSupport.addPropertyChangeListener(_propertyName, _listener);
	}

	public PropertyChangeListener[] getPropertyChangeListeners() {
		return myPropertyChangeSupport.getPropertyChangeListeners();
	}

	public PropertyChangeListener[] getPropertyChangeListeners(String _propertyName) {
		return myPropertyChangeSupport.getPropertyChangeListeners(_propertyName);
	}

	public boolean hasListeners(String _propertyName) {
		return myPropertyChangeSupport.hasListeners(_propertyName);
	}

	public void removePropertyChangeListener(PropertyChangeListener _listener) {
		myPropertyChangeSupport.removePropertyChangeListener(_listener);
	}

	public void removePropertyChangeListener(String _propertyName, PropertyChangeListener _listener) {
		myPropertyChangeSupport.removePropertyChangeListener(_propertyName, _listener);
	}

	public String toString() {
		return myMap.toString();
	}
}

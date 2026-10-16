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
package net.logicsquad.eogen.model.qualifier;

import net.logicsquad.eogen.model.AbstractEOAttributePath;
import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.ISortableEOModelObject;

/**
 * EOQualifierBindings represents the binding of a $qualifierVariable to a
 * particular keypath of an entity.
 * 
 * @author mschrag
 */
public class EOQualifierBinding implements ISortableEOModelObject {
	private EOEntity _entity;

	private String _name;

	private String _keyPath;

	public EOQualifierBinding(EOEntity entity, String name, String keyPath) {
		_entity = entity;
		_name = name;
		_keyPath = keyPath;
	}

	public EOEntity getEntity() {
		return _entity;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getKeyPath() {
		return _keyPath;
	}

	public AbstractEOAttributePath getAttributePath() {
		return _entity.resolveKeyPath(_keyPath);
	}
	
	public String getJavaClassName() {
		AbstractEOAttributePath attributePath = getAttributePath();
		if (attributePath == null) {
			throw new IllegalStateException("The binding name '" + _name + "' refers to a keypath '" + _keyPath + " that could not be resolved on the entity '" + _entity.getName() + "'.");
		}
		return attributePath.getChildClassName();
	}

	public String toString() {
		return "[EOQualifierBinding: " + _keyPath + " = $" + _name + "]";
	}
}

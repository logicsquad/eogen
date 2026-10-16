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

import java.util.HashSet;
import java.util.Set;

import net.logicsquad.eogen.Messages;

public class EOArgument extends AbstractEOArgument<EOStoredProcedure> {
	public static final String DIRECTION = "direction";

	private EOStoredProcedure myStoredProcedure;

	private EOArgumentDirection myDirection;

	public EOArgument() {
		myDirection = EOArgumentDirection.VOID;
	}

	public EOArgument(String _name) {
		super(_name);
		myDirection = EOArgumentDirection.VOID;
	}

	public EOArgument(String _name, String _definition) {
		super(_name, _definition);
		myDirection = EOArgumentDirection.VOID;
	}

//	public int hashCode() {
//		return ((myStoredProcedure == null) ? 1 : myStoredProcedure.hashCode()) * super.hashCode();
//	}
//
//	public boolean equals(Object _obj) {
//		boolean equals = false;
//		if (_obj instanceof EOArgument) {
//			EOArgument argument = (EOArgument) _obj;
//			equals = (argument == this) || (ComparisonUtils.equals(argument.myStoredProcedure, myStoredProcedure) && ComparisonUtils.equals(argument.getName(), getName()));
//		}
//		return equals;
//	}

	public EOStoredProcedure getStoredProcedure() {
		return myStoredProcedure;
	}

	public void _setStoredProcedure(EOStoredProcedure _storedProcedure) {
		myStoredProcedure = _storedProcedure;
	}

	protected AbstractEOArgument _createArgument(String _name) {
		return new EOArgument(_name);
	}

	protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
		if (myStoredProcedure != null) {
			myStoredProcedure._argumentChanged(this, _propertyName, _oldValue, _newValue);
		}
	}

	public void setName(String _name, boolean _fireEvents) throws DuplicateNameException {
		if (_name == null) {
			throw new NullPointerException(Messages.getString("EOArgument.noBlankArgumentNames"));
		}
		if (myStoredProcedure != null) {
			myStoredProcedure._checkForDuplicateArgumentName(this, _name, null);
		}
		super.setName(_name, _fireEvents);
	}

	public void setDirection(EOArgumentDirection _direction) {
		EOArgumentDirection oldDirection = myDirection;
		myDirection = _direction;
		firePropertyChange(EOArgument.DIRECTION, oldDirection, myDirection);
	}

	public EOArgumentDirection getDirection() {
		return myDirection;
	}

	public void loadFromMap(EOModelMap _argumentMap, Set<EOModelVerificationFailure> _failures) {
		super.loadFromMap(_argumentMap, _failures);
		Integer argumentDirectionNum = _argumentMap.getInteger("parameterDirection");
		if (argumentDirectionNum == null) {
			argumentDirectionNum = Integer.valueOf(0);
		}
		myDirection = EOArgumentDirection.getArgumentDirectionByID(argumentDirectionNum.intValue());
	}

	public EOModelMap toMap() {
		EOModelMap argumentMap = super.toMap();
		if (myDirection == null || myDirection == EOArgumentDirection.VOID) {
			argumentMap.remove("parameterDirection");
		} else {
			argumentMap.setInteger("parameterDirection", Integer.valueOf(myDirection.getID()));
		}
		return argumentMap;
	}
	
	public Set<EOModelReferenceFailure> getReferenceFailures() {
		return new HashSet<EOModelReferenceFailure>();
	}

  @SuppressWarnings("unused")
	public void resolve(Set<EOModelVerificationFailure> _failures) {
		// DO NOTHING
	}

	public void verify(Set<EOModelVerificationFailure> _failures) {
		String name = getName();
		if (name == null || name.trim().length() == 0) {
			_failures.add(new EOModelVerificationFailure(myStoredProcedure.getModel(), this, "The argument " + getName() + " has an empty name.", false));
		} else {
			if (name.indexOf(' ') != -1) {
				_failures.add(new EOModelVerificationFailure(myStoredProcedure.getModel(), this, "The argument " + getName() + "'s name has a space in it.", false));
			}
		}
		// if (!isFlattened()) {
		// String columnName = getColumnName();
		// if (columnName == null || columnName.trim().length() == 0) {
		// _failures.add(new
		// EOModelVerificationFailure(myStoredProcedure.getModel().getName() +
		// "/" + myStoredProcedure.getName() + "/" + name + " does not have a
		// column name set."));
		// }
		// else if (columnName.indexOf(' ') != -1) {
		// _failures.add(new
		// EOModelVerificationFailure(myStoredProcedure.getModel().getName() +
		// "/" + myStoredProcedure.getName() + "/" + name + "'s column name '" +
		// columnName + "' has a space in it."));
		// }
		// }
		if (myDirection == null) {
			_failures.add(new EOModelVerificationFailure(myStoredProcedure.getModel(), this, "The argument " + getName() + " has no direction specified.", false));
		}
	}

	public String getFullyQualifiedName() {
		return ((myStoredProcedure == null) ? "?" : myStoredProcedure.getFullyQualifiedName()) + "/arg: " + getName();
	}

	@Override
	public EOArgument _cloneModelObject() {
		EOArgument argument = (EOArgument) _cloneArgument();
		argument.myDirection = myDirection;
		return argument;
	}
	
	@Override
	public Class<EOStoredProcedure> _getModelParentType() {
		return EOStoredProcedure.class;
	}
	
	public EOStoredProcedure _getModelParent() {
		return getStoredProcedure();
	}
	
	public void _removeFromModelParent(Set<EOModelVerificationFailure> failures) {
		getStoredProcedure().removeArgument(this);
	}
	
	public void _addToModelParent(EOStoredProcedure modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) throws EOModelException {
		if (findUniqueName) {
			setName(modelParent.findUnusedArgumentName(getName()));
		}
		modelParent.addArgument(this);
	}

	public String toString() {
		return "[EOArgument: name = " + getName() + "]";
	}
}

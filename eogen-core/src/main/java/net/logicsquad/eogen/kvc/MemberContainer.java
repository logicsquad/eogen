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
package net.logicsquad.eogen.kvc;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class MemberContainer {
	public static final int GET = 1;

	public static final int SET = 2;

	private static final String[] GET_METHOD_PREFIXES = { "get", "", "_", "_get", "is", "_is" }; //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

	private static final String[] SET_METHOD_PREFIXES = { "set", "", "_", "_set" }; //$NON-NLS-4$

	private static final String[] FIELD_PREFIXES = { "", "_" };

	private int myMemberType;

	private Class myClass;

	private Map<String, IKey> myMembers;

	public MemberContainer(Class _class, int _memberType) {
		myMembers = new HashMap<String, IKey>();
		myClass = _class;
		myMemberType = _memberType;
		Class currentClass = _class;
		while (currentClass != null) {
			hashMembers(currentClass.getDeclaredFields());
			currentClass = currentClass.getSuperclass();
		}
		currentClass = _class;
		while (currentClass != null) {
			hashMembers(currentClass.getDeclaredMethods());
			currentClass = currentClass.getSuperclass();
		}
	}

	public IKey getMember(Object _instance, String _name) {
		String[] prefixes = (myMemberType == MemberContainer.GET) ? MemberContainer.GET_METHOD_PREFIXES : MemberContainer.SET_METHOD_PREFIXES;
		IKey setMember = getMemberWithPrefixes(_name, prefixes);
		if (setMember == null) {
			setMember = getMemberWithPrefixes(_name, MemberContainer.FIELD_PREFIXES);
			if (setMember == null) {
				if (_instance instanceof Map) {
					setMember = new MapKey(_name);
				} else if (myMemberType == MemberContainer.GET) {
					throw new IllegalArgumentException("There is no get-method named '" + _name + "' on the class " + myClass.getName() + ".");
				} else {
					throw new IllegalArgumentException("There is no set-method named '" + _name + "' on the class " + myClass.getName() + ".");
				}
			}
		}
		return setMember;
	}

	protected void hashMembers(Member[] _members) {
		for (int memberNum = 0; memberNum < _members.length; memberNum++) {
			Member member = _members[memberNum];
			int modifiers = member.getModifiers();
			if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
				IKey matchingMember = null;
				if (myMemberType == MemberContainer.GET) {
					if (member instanceof Field) {
						matchingMember = new FieldKey((Field) member);
					} else {
						Method method = (Method) member;
						if (method.getReturnType() != null && method.getParameterTypes().length == 0) {
							matchingMember = new MethodKey(method);
						}
					}
				} else if (myMemberType == MemberContainer.SET) {
					if (member instanceof Field) {
						matchingMember = new FieldKey((Field) member);
					} else {
						Method method = (Method) member;
						if (method.getReturnType() == void.class && method.getParameterTypes().length == 1) {
							matchingMember = new MethodKey(method);
						}
					}
				}
				if (matchingMember != null) {
					myMembers.put(member.getName(), matchingMember);
				}
			}
		}
	}

	protected IKey getMemberWithPrefixes(String _key, String[] _prefixes) {
		IKey matchingMember = null;
		for (int prefixNum = 0; matchingMember == null && prefixNum < _prefixes.length; prefixNum++) {
			String prefix = _prefixes[prefixNum];
			boolean capitalize = prefix.length() > 1; // Don't capitalize
														// blank and _ prefixes
			String keyWithPrefix = prependToKey(prefix, _key, capitalize);
			matchingMember = myMembers.get(keyWithPrefix);
		}
		return matchingMember;
	}

	protected String prependToKey(String _prepend, String _key, boolean _capitalize) {
		StringBuffer sb = new StringBuffer();
		sb.append(_prepend);
		if (_capitalize) {
			sb.append(Character.toUpperCase(_key.charAt(0)));
			sb.append(_key.substring(1));
		} else {
			sb.append(_key);
		}
		return sb.toString();
	}

}

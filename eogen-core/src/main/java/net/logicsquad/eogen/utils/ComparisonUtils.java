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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ComparisonUtils {
	public static boolean equals(Object _o1, Object _o2) {
		boolean equals;
		if (_o1 == null) {
			equals = (_o2 == null);
		} else {
			equals = (_o1 == _o2);
			if (!equals) {
				equals = _o1.equals(_o2);
			}
		}
		return equals;
	}

	public static boolean equals(String _o1, String _o2, boolean _blankIsNull) {
		boolean equals = ComparisonUtils.equals(_o1, _o2);
		if (!equals && _blankIsNull) {
			equals = ((_o1 == null || _o1.length() == 0) && (_o2 == null || _o2.length() == 0));
		}
		return equals;
	}

	public static boolean equalsIgnoreCaseIfStrings(Object o1, Object o2) {
		if (o1 instanceof String && o2 instanceof String) {
			return equalsIgnoreCase((String)o1, (String)o2);
		}
		return equals(o1, o2);
	}

	public static boolean equalsIgnoreCase(String _o1, String _o2) {
		boolean equals;
		if (_o1 == null) {
			equals = (_o2 == null);
		} else {
			equals = (_o1 == _o2);
			if (!equals) {
				equals = _o1.equalsIgnoreCase(_o2);
			}
		}
		return equals;
	}

	public static boolean equalsIgnoreCase(String _o1, String _o2, boolean _blankIsNull) {
		boolean equals = ComparisonUtils.equalsIgnoreCase(_o1, _o2);
		if (!equals && _blankIsNull) {
			equals = ((_o1 == null || _o1.length() == 0) && (_o2 == null || _o2.length() == 0));
		}
		return equals;
	}

	private static Object deepCopyVanilla(Object original) {
		if (original instanceof Map) {
			Map<Object, Object> newMap = new HashMap<Object, Object>();
			Iterator iterator = ((Map) original).entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				newMap.put(entry.getKey(), deepCopyVanilla(entry.getValue()));
			}
			return newMap;
		} else if (original instanceof Set) {
			Set<Object> newSet = new HashSet<Object>();
			Iterator iterator = ((Set) original).iterator();
			while (iterator.hasNext()) {
				newSet.add(deepCopyVanilla(iterator.next()));
			}
			return newSet;
		} else if (original instanceof Number) {
			return original.toString();
		} else {
			return original;
		}
	}

	public static boolean deepEquals(Object one, Object two) {
		if (one == null && two == null) {
			return true;
		} else if (one == null || two == null) {
			return false;
		} else {
			return deepCopyVanilla(one).equals(deepCopyVanilla(two));
		}
	}
}

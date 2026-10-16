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

import java.util.Map;

import net.logicsquad.eogen.utils.ComparisonUtils;
import net.logicsquad.eogen.utils.StringUtils;
import net.logicsquad.eogen.model.EOModelMap;
import net.logicsquad.eogen.model.PropertyListMap;

public class NamingConvention {
	public static final NamingConvention DEFAULT = new NamingConvention(NamingConvention.Case.Camel, NamingConvention.Separator.None, null, null);

	public static enum Case {
		Upper, Lower, Camel, CappedCamel;
	}

	public static enum Separator {
		None, Underscore;
	}

	private Case _case;

	private Separator _separator;

	private String _prefix;

	private String _suffix;

	public NamingConvention() {
		// DO NOTHING
	}

	public NamingConvention(Case ccase, Separator separator, String prefix, String suffix) {
		_case = ccase;
		_separator = separator;
		_prefix = prefix != null && prefix.length() == 0 ? null : prefix;
		_suffix = suffix != null && suffix.length() == 0 ? null : suffix;
	}

	@Override
	public int hashCode() {
		return _case.hashCode() + _separator.hashCode() + ((_prefix == null) ? 0 : _prefix.hashCode()) + ((_suffix == null) ? 0 : _suffix.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof NamingConvention && ComparisonUtils.equals(_case, ((NamingConvention) obj)._case) && ComparisonUtils.equals(_separator, ((NamingConvention) obj)._separator) && ComparisonUtils.equals(_prefix, ((NamingConvention) obj)._prefix) && ComparisonUtils.equals(_suffix, ((NamingConvention) obj)._suffix));
	}

	public Case getCase() {
		return _case;
	}

	public void setCase(Case ccase) {
		_case = ccase;
	}

	public Separator getSeparator() {
		return _separator;
	}

	public void setSeparator(Separator separator) {
		_separator = separator;
	}

	public String getPrefix() {
		return _prefix;
	}

	public void setPrefix(String prefix) {
		_prefix = prefix;
	}

	public String getSuffix() {
		return _suffix;
	}

	public void setSuffix(String suffix) {
		_suffix = suffix;
	}

	public String format(String oldName, String newName, String oldFormattedName) {
		String newFormattedName;
		String expectedOldFormattedName = format(oldName);
		if (oldFormattedName == null || oldFormattedName.length() == 0 || ComparisonUtils.equals(expectedOldFormattedName, oldFormattedName)) {
			newFormattedName = format(newName);
		} else {
			newFormattedName = oldFormattedName;
		}
		return newFormattedName;
	}

	public String format(String name) {
		String newName = name;
		if (name == null) {
			newName = null;
		} else {
			if (_prefix != null) {
				newName = _prefix + newName;
			}
			if (_suffix != null) {
				newName = newName + _suffix;
			}
			if (_separator == Separator.None) {
				if (_case == Case.Camel) {
					// newName = newName;
				} else if (_case == Case.CappedCamel) {
					newName = StringUtils.toUppercaseFirstLetter(newName);
				} else if (_case == Case.Lower) {
					newName = newName.toLowerCase();
				} else if (_case == Case.Upper) {
					newName = newName.toUpperCase();
				}
			} else if (_separator == Separator.Underscore) {
				if (_case == Case.Camel) {
					newName = StringUtils.camelCaseToUnderscore(newName, false);
				} else if (_case == Case.CappedCamel) {
					newName = StringUtils.toUppercaseFirstLetter(StringUtils.camelCaseToUnderscore(newName, false));
				} else if (_case == Case.Lower) {
					newName = StringUtils.camelCaseToUnderscore(newName, true);
				} else if (_case == Case.Upper) {
					newName = StringUtils.camelCaseToUnderscore(newName, false).toUpperCase();
				}
			}
		}
		return newName;
	}

	public void loadFromMap(Map<String, String> map) {
		_prefix = map.get("prefix");
		_suffix = map.get("suffix");
		_case = NamingConvention.Case.valueOf(map.get("case"));
		_separator = NamingConvention.Separator.valueOf(map.get("separator"));
	}

	public Map<String, String> toMap() {
		Map<String, String> namingConventionMap = new PropertyListMap<String, String>();
		namingConventionMap.put("prefix", getPrefix());
		namingConventionMap.put("suffix", getSuffix());
		namingConventionMap.put("case", getCase().name());
		namingConventionMap.put("separator", getSeparator().name());
		return namingConventionMap;
	}

	public static NamingConvention loadFromMap(String name, EOModelMap map) {
		NamingConvention namingConvention;
		Map<String, String> namingConventionMap = map.getMap(name);
		if (namingConventionMap != null) {
			namingConvention = new NamingConvention();
			namingConvention.loadFromMap(namingConventionMap);
		} else {
			namingConvention = NamingConvention.DEFAULT;
		}
		return namingConvention;
	}

	public static void toMap(NamingConvention namingConvention, String name, Map<String, Object> map) {
		if (namingConvention != null && !namingConvention.equals(NamingConvention.DEFAULT)) {
			map.put(name, namingConvention.toMap());
		} else {
			map.remove(name);
		}
	}

	public static String newClassName(String oldName, String newName, String oldClassName) {
		String newClassName;
		if (ComparisonUtils.equals(oldName, oldClassName, true)) {
			newClassName = newName;
		} else if (oldClassName != null && oldClassName.endsWith("." + oldName)) {
			String oldPackage = oldClassName.substring(0, oldClassName.lastIndexOf('.') + 1);
			newClassName = oldPackage + newName;
		} else {
			newClassName = oldClassName;
		}
		return newClassName;
	}
}
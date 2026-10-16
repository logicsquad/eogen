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

public class EOModelRenderContext {
	private static ThreadLocal<EOModelRenderContext> _renderContext = new ThreadLocal<EOModelRenderContext>();

	public static EOModelRenderContext getInstance() {
		EOModelRenderContext renderContext = _renderContext.get();
		if (renderContext == null) {
			renderContext = new EOModelRenderContext();
		}
		return renderContext;
	}

	public static void setRenderContext(EOModelRenderContext renderContext) {
		_renderContext.set(renderContext);
	}

	public static void clearRenderContext() {
		_renderContext.remove();
	}

	private String _prefix;

	private String _eogenericRecordClassName;

	private String _superclassPackage;
	
	private boolean _javaClient;
	
	private boolean _javaClientCommon;

	public EOModelRenderContext() {
		_prefix = "_";
		_eogenericRecordClassName = "com.webobjects.eocontrol.EOGenericRecord";
	}
	
	public void setJavaClient(boolean javaClient) {
		_javaClient = javaClient;
	}
	
	public boolean isJavaClient() {
		return _javaClient;
	}
	
	public void setJavaClientCommon(boolean javaClientCommon) {
		_javaClientCommon = javaClientCommon;
	}
	
	public boolean isJavaClientCommon() {
		return _javaClientCommon;
	}

	public void setPrefix(String prefix) {
		_prefix = prefix;
	}

	public String getPrefix() {
		return _prefix;
	}

	public void setSuperclassPackage(String superclassPackage) {
		_superclassPackage = superclassPackage;
	}

	public String getSuperclassPackage() {
		return _superclassPackage;
	}

	public void setEOGenericRecordClassName(String eogenericRecordClassName) {
		_eogenericRecordClassName = eogenericRecordClassName;
	}

	public String getEOGenericRecordClassName() {
		return _eogenericRecordClassName;
	}

	public String getClassNameForEntity(EOEntity entity) {
	  String className;
	  if (_javaClientCommon) {
		className = entity.getParentClassName();
	  }
	  else if (_javaClient) {
	    className = entity.getClientClassName();
	  }
	  else {
	    className = entity.getClassName();
	  }
	  if (className != null) {
		  className = className.replace('$', '.');
	  }
	  return className;
	}
}

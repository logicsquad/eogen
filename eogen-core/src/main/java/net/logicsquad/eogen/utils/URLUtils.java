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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class URLUtils {
	public static String getExtension(URL url) {
		return URLUtils.getExtension(url.getPath());
	}
	
	public static String getExtension(URI uri) {
		return URLUtils.getExtension(uri.getPath());
	}
	
	public static String getExtension(String path) {
		String extension = null;
		if (path != null) {
			int dotIndex = path.lastIndexOf('.');
			if (dotIndex != -1) {
				extension = path.substring(dotIndex + 1);
			}
		}
		return extension;
	}
	
	public static String getName(URL url) {
		return URLUtils.getName(url.getPath());
	}
	
	public static String getName(URI uri) {
		return URLUtils.getName(uri.getPath());
	}
	
	public static String getName(String path) {
		String name = null;
		if (path != null) {
			int slashIndex = path.lastIndexOf('/');
			if (slashIndex != -1) {
				name = path.substring(slashIndex + 1);
			}
			else {
				name = path;
			}
		}
		return name;
	}
	
	public static boolean isFolder(URL url) throws IOException {
		boolean isFolder = false;
		String protocol = url.getProtocol();
		if ("file".equals(protocol)) {
			File f = new File(url.getPath());
			isFolder = f.isDirectory();
		} else if ("jar".equals(protocol)) {
			JarURLConnection conn = (JarURLConnection) url.openConnection();
			isFolder = conn.getJarEntry().isDirectory();
		} else {
			throw new IllegalArgumentException(url + " is not a File.");
		}
		return isFolder;
	}
	
	/**
	 * @param url - the url for the file or bundle resource
	 * @return true when the protocol is either <code>bundleresource</code> or <code>file</code> and the file exists. 
	 */
	public static boolean isFileOrBundleResource(URL url) {
		String protocol = url.getProtocol();
		if ("bundleresource".equals(protocol) || "file".equals(protocol))
			return exists(url);
		return false;
	}
	
	public static boolean isJarURL(URL url) {
		return "jar".equals(url.getProtocol());
	}

	public static URL[] getChildrenFolders(URL url) throws IOException {
		URL parentUrl = url;
		URL[] children;
		String protocol = parentUrl.getProtocol();
		if ("bundleresource".equals(protocol)) {
			URLConnection conn = parentUrl.openConnection();
			parentUrl = conn.getURL();
			protocol = parentUrl.getProtocol();
		}
				
		if ("file".equals(protocol)) {
			File f = new File(parentUrl.getPath()).getAbsoluteFile();
			if (!f.exists()) {
				children = new URL[0];
			}
			else {
				File[] files = f.listFiles();
				if (files == null) {
					children = new URL[0];
				}
				else {
					List<URL> childrenList = new LinkedList<URL>();
					for (int i = 0; i < files.length; i++) {
						File child = files[i];
						if (!child.isHidden() && child.isDirectory()) {
							childrenList.add(child.toURI().toURL());
						}
					}
					children = childrenList.toArray(new URL[childrenList.size()]);
				}
			}
		} else if ("jar".equals(protocol)) {
			List<URL> childEntries = new LinkedList<URL>();
			JarURLConnection conn = (JarURLConnection) parentUrl.openConnection();
			JarFile jarFile = null;
			try {
				jarFile = conn.getJarFile();
				JarEntry folderJarEntry = conn.getJarEntry();
				String folderName = folderJarEntry.getName();
				String parentUrlString = parentUrl.toString();
				String base = parentUrlString.substring(0, parentUrlString.indexOf("!"));
				Enumeration<JarEntry> jarEntriesEnum = jarFile.entries();
				while (jarEntriesEnum.hasMoreElements()) {
					JarEntry jarEntry = jarEntriesEnum.nextElement();
					String name = jarEntry.getName();
					
					if (name.startsWith(folderName)) {
						URL childURL = new URL(base + "!/" + name);
						childEntries.add(childURL);
					}
				}
				children = childEntries.toArray(new URL[childEntries.size()]);
			} catch (Exception e) {
				children = new URL[0];
			}
		} else {
			throw new IllegalArgumentException(parentUrl + " is not a format that can have its children retrieved.");
		}
		return children;
	}

	public static File cheatAndTurnIntoFile(URI uri) {
		try {
			return cheatAndTurnIntoFile(uri.toURL());
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Unable to turn '" + uri + "' into a URL.", e);
		}
	}

	public static File cheatAndTurnIntoFile(URL url) {
		File f;
		if (url == null) {
			f = null;
		}
		else {
			String protocol = url.getProtocol();
			if ("jar".equals(protocol)) {
				String externalForm = url.toExternalForm();
				int colonIndex = externalForm.indexOf(':');
				int bangIndex = externalForm.indexOf('!');
				String jarPath;
				if (bangIndex == -1) {
					jarPath = externalForm.substring(colonIndex + 1);
				}
				else {
					jarPath = externalForm.substring(colonIndex + 1, bangIndex);
				}
				try {
					f = new File(new URI(jarPath));
				}
				catch (Exception e) {
					throw new IllegalArgumentException(url + " cannot be turned into a File.", e);
				}
			}
			else if ("file".equals(protocol)) {
				try {
					String externalForm = url.getPath();
					f = new File(externalForm);
					if (!f.exists()) {
						externalForm = URLDecoder.decode(externalForm, "UTF-8");
						f = new File(externalForm);
					}
				} catch (IOException e) {
					throw new IllegalArgumentException(url + " cannot be turned into a File.", e);
				}
			} else if ("bundleresource".equals(protocol)) {
				try {
					URLConnection conn =  url.openConnection();
					String externalForm = conn.getURL().toExternalForm();
					externalForm = URLDecoder.decode(externalForm, "UTF-8");
					externalForm = externalForm.replaceAll(" ", "%20");
					f = new File(new URI(externalForm));
				} catch (IOException e) {
					throw new IllegalArgumentException(url + " cannot be turned into a File.", e);
				} catch (URISyntaxException e) {
					throw new IllegalArgumentException(url + " cannot be turned into a File.", e);
				}
			} else {
				throw new IllegalArgumentException(url + " is not a File.");
			}
		}
		return f;
	}

	public static boolean exists(URL url) {
		boolean exists = false;
		String protocol = url.getProtocol();
		if ("jar".equals(protocol)) {
			try {
				JarURLConnection conn = (JarURLConnection) url.openConnection();
				conn.getJarEntry();
				exists = true;
			} catch (FileNotFoundException e) {
				exists = false;
			} catch (IOException e) {
				// MS: Ah yes ... I remember now. It's chatty as all hell, that's why i commented this out. It just 
				// happens constantly for no particularly good reason. Another victory for crappy core libraries in
				// java!
				//e.printStackTrace();
				//throw new IllegalArgumentException(url + " is not a File.");
			}
		} else if ("file".equals(protocol) || "bundleresource".equals(protocol)) {
			File file = cheatAndTurnIntoFile(url);
			if (file != null) {
				exists = file.exists();
			}
		} else {
			throw new IllegalArgumentException(url + " is not a File.");
		}
		return exists;
	}
}
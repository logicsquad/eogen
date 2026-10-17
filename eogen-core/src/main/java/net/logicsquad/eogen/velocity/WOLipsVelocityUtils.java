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
package net.logicsquad.eogen.velocity;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.slf4j.helpers.NOPLogger;

public class WOLipsVelocityUtils {

	/**
	 * Creates a configured VelocityEngine.
	 * 
	 * Templates are looked up in the project folder, the custom template
	 * folder, the WOLips template folders in /Library and the user's home,
	 * and last with the given resource loader. WOLips looked in ".", the
	 * working directory, first. Outside Eclipse that is wherever the build or
	 * command started, so the project folder takes its place.
	 * 
	 * The engine is Velocity 2 in the settings that make it render
	 * WOLips's Velocity 1.5 templates the same way: space gobbling "bc",
	 * no empty check in #if, hyphens allowed in identifiers, Velocity 1
	 * macro behaviour, no conversion of method arguments, and Velocity 1's
	 * lookup of properties (VelocityOneUberspector). Template files keep any
	 * byte order mark, as Velocity 1.5 kept it. Render with a
	 * VelocityOneContext, which with VelocityOneEventHandler restores
	 * $velocityCount and Velocity 1's #set of null values, and with
	 * UnresolvedReferenceHandler collects the references that render without
	 * a value.
	 * 
	 * @param templateFamilyName
	 *            the name of the template family (the common folder name that
	 *            contains the templates)
	 * @param customTemplatePath
	 *            the custom template path to load with
	 * @param projectPath
	 *            the project path to prepend if the customTemplatePath is
	 *            relative
	 * @param resourceLoaderClass
	 *            the resource loader for the bundled templates, or null
	 * @return an initialized VelocityEngine
	 * @throws Exception
	 *             if velocity engine creation fails
	 */
	public static VelocityEngine createVelocityEngine(String templateFamilyName, String customTemplatePath, Path projectPath, Class<? extends ResourceLoader> resourceLoaderClass) throws Exception {
		return createVelocityEngine(templateFamilyName, customTemplatePath, projectPath, Collections.<File>emptyList(), resourceLoaderClass);
	}

	/**
	 * eogen: creates a Velocity engine as above, which looks for shared
	 * templates, with {@link SharedTemplateResourceLoader}, after the project
	 * folder and the custom template path, and before the WOLips folders and
	 * the bundled templates.
	 * 
	 * @param templateFamilyName
	 *            the name of the template family (the common folder name that
	 *            contains the templates)
	 * @param customTemplatePath
	 *            the custom template path to load with
	 * @param projectPath
	 *            the project path to prepend if the customTemplatePath is
	 *            relative
	 * @param sharedTemplatePath
	 *            jars and folders of shared templates, which come before
	 *            those on eogen's classpath
	 * @param resourceLoaderClass
	 *            the resource loader for the bundled templates, or null
	 * @return an initialized VelocityEngine
	 * @throws Exception
	 *             if velocity engine creation fails
	 */
	public static VelocityEngine createVelocityEngine(String templateFamilyName, String customTemplatePath, Path projectPath, List<File> sharedTemplatePath, Class<? extends ResourceLoader> resourceLoaderClass) throws Exception {
		StringBuffer templatePaths = new StringBuffer();
		templatePaths.append(projectPath != null ? projectPath.toFile().getAbsolutePath() : new File(".").getAbsolutePath());
		if (customTemplatePath != null && customTemplatePath.length() > 0) {
			templatePaths.append(",");
			File templateFolder = new File(customTemplatePath);
			if (!templateFolder.isAbsolute() && projectPath != null) {
				templateFolder = new File(projectPath.toFile(), customTemplatePath);
			}
			templatePaths.append(templateFolder.getAbsolutePath());
		}

		StringBuffer folderPaths = new StringBuffer();
		folderPaths.append(new File("/Library/Application Support/WOLips/" + templateFamilyName).getAbsolutePath());
		folderPaths.append(",");
		folderPaths.append(new File(System.getProperty("user.home"), "Documents and Settings/Application Data/WOLips/" + templateFamilyName).getAbsolutePath());
		folderPaths.append(",");
		folderPaths.append(new File(System.getProperty("user.home"), "Documents and Settings/AppData/Local/WOLips/" + templateFamilyName).getAbsolutePath());
		folderPaths.append(",");
		folderPaths.append(new File(System.getProperty("user.home"), "Library/Application Support/WOLips/" + templateFamilyName).getAbsolutePath());

		StringBuffer sharedPaths = new StringBuffer();
		for (File entry : sharedTemplatePath) {
			if (sharedPaths.length() > 0) {
				sharedPaths.append(",");
			}
			sharedPaths.append(entry.getAbsolutePath());
		}

		Thread thread = Thread.currentThread();
		ClassLoader loader = thread.getContextClassLoader();
		thread.setContextClassLoader(resourceLoaderClass != null ? resourceLoaderClass.getClassLoader() : WOLipsVelocityUtils.class.getClassLoader());
		try {
			VelocityEngine velocityEngine = new VelocityEngine();
			velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_INSTANCE, NOPLogger.NOP_LOGGER);
			velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, resourceLoaderClass != null ? "file,shared,folders,wolips" : "file,shared,folders");
			velocityEngine.setProperty("resource.loader.file.class", VelocityOneFileResourceLoader.class.getName());
			velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templatePaths.toString());
			velocityEngine.setProperty("resource.loader.shared.class", SharedTemplateResourceLoader.class.getName());
			velocityEngine.setProperty("resource.loader.shared." + SharedTemplateResourceLoader.PATH, sharedPaths.toString());
			velocityEngine.setProperty("resource.loader.folders.class", VelocityOneFileResourceLoader.class.getName());
			velocityEngine.setProperty("resource.loader.folders.path", folderPaths.toString());
			if (resourceLoaderClass != null) {
				velocityEngine.setProperty("resource.loader.wolips.class", resourceLoaderClass.getName());
			}
			velocityEngine.setProperty(RuntimeConstants.SPACE_GOBBLING, "bc");
			velocityEngine.setProperty(RuntimeConstants.CHECK_EMPTY_OBJECTS, "false");
			velocityEngine.setProperty(RuntimeConstants.PARSER_HYPHEN_ALLOWED, "true");
			velocityEngine.setProperty(RuntimeConstants.VM_ENABLE_BC_MODE, "true");
			velocityEngine.setProperty(RuntimeConstants.CONVERSION_HANDLER_CLASS, "none");
			velocityEngine.setProperty(RuntimeConstants.UBERSPECT_CLASSNAME, VelocityOneUberspector.class.getName());
			velocityEngine.setProperty(RuntimeConstants.EVENTHANDLER_INVALIDREFERENCES, VelocityOneEventHandler.class.getName());
			velocityEngine.setProperty(RuntimeConstants.EVENTHANDLER_REFERENCEINSERTION, UnresolvedReferenceHandler.class.getName());
			velocityEngine.init();
			return velocityEngine;
		} finally {
			thread.setContextClassLoader(loader);
		}
	}

	public static String writeTemplateToString(VelocityEngine engine, VelocityContext context, String templateName, ByteArrayOutputStream newFileContentsStream) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		Template template;
		try {
			template = engine.getTemplate(templateName, "UTF-8");
		} catch (ResourceNotFoundException e) {
			throw new Exception("Failed to load the template '" + templateName + "'.  Check your model's eogen file to make sure that it specifies the correct template folder and template names.");
		}

		Writer newFileContentsWriter = new BufferedWriter(new OutputStreamWriter(newFileContentsStream, "UTF-8"));
		try {
			template.merge(context, newFileContentsWriter);
		} finally {
			newFileContentsWriter.close();
		}
		String newFileContentsStr = newFileContentsStream.toString("UTF-8");
		if (newFileContentsStr != null) {
			if (newFileContentsStr.contains("<%")) {
				throw new IOException("You are attempting to use an old EOGenerator template with Velocity EOGenerator.");
			} else if (newFileContentsStr.contains("<wo:")) {
				throw new IOException("You are attempting to use a JavaEOGenerator template with Velocity EOGenerator.");
			}
		}
		return newFileContentsStr;
	}

	public static String writeTemplateToString(VelocityEngine engine, VelocityContext context, String templateName) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		return writeTemplateToString(engine, context, templateName, new ByteArrayOutputStream());
	}

	public static void writeTemplate(VelocityEngine engine, VelocityContext context, String templateName, OutputStream outputStream) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		ByteArrayOutputStream newFileContentsStream = new ByteArrayOutputStream();
		String newFileContentsStr = WOLipsVelocityUtils.writeTemplateToString(engine, context, templateName, newFileContentsStream);
		BufferedWriter newFileBufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
		try {
			newFileBufferedWriter.write(newFileContentsStr);
		} finally {
			newFileBufferedWriter.close();
		}
	}
	
	public static void writeTemplate(VelocityEngine engine, VelocityContext context, String templateName, File outputFile) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		ByteArrayOutputStream newFileContentsStream = new ByteArrayOutputStream();
		String newFileContentsStr = WOLipsVelocityUtils.writeTemplateToString(engine, context, templateName, newFileContentsStream);
		writeToFile(outputFile, newFileContentsStream, newFileContentsStr);
	}
	
	public static void writeTemplateToDirectory(VelocityEngine engine, VelocityContext context, String templateName, File outputDirectory) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		ByteArrayOutputStream newFileContentsStream = new ByteArrayOutputStream();
		String newFileContentsStr = WOLipsVelocityUtils.writeTemplateToString(engine, context, templateName, newFileContentsStream);
		int beginIndex = newFileContentsStr.indexOf(" class ") + 7;
		int endIndex = newFileContentsStr.substring(beginIndex).indexOf(" ") + beginIndex;
		String filename = newFileContentsStr.substring(beginIndex, endIndex);
		File outputFile = new File(outputDirectory, filename+".java");
		writeToFile(outputFile, newFileContentsStream, newFileContentsStr);
	}

	private static void writeToFile(File outputFile, ByteArrayOutputStream newFileContentsStream, String newFileContentsStr) throws IOException, UnsupportedEncodingException, FileNotFoundException, NoSuchAlgorithmException {
		if (!outputFile.getParentFile().exists()) {
			if (!outputFile.getParentFile().mkdirs()) {
				throw new IOException("Unable to create the folder " + outputFile.getParentFile() + ".");
			}
		}
		boolean templateChanged = true;
		if (!outputFile.exists()) {
			BufferedWriter newFileBufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
			try {
				newFileBufferedWriter.write(newFileContentsStr);
			} finally {
				newFileBufferedWriter.close();
			}
		} else {
			byte[] newFileContents = newFileContentsStream.toByteArray();
			if (outputFile.exists()) {
				FileInputStream fis = new FileInputStream(outputFile);
				int bytesRemaining = (int) outputFile.length();
				if (bytesRemaining == newFileContents.length) {
					byte[] oldFileContents;
					try {
						ByteArrayOutputStream bos = new ByteArrayOutputStream((int) outputFile.length());
						byte[] buf = new byte[4096];
						while (bytesRemaining > 0) {
							int bytesRead = fis.read(buf, 0, Math.min(buf.length, bytesRemaining));
							bos.write(buf, 0, bytesRead);
							bytesRemaining -= bytesRead;
						}
						oldFileContents = bos.toByteArray();
					} finally {
						fis.close();
					}

					MessageDigest md5 = MessageDigest.getInstance("MD5");
					byte[] oldMD5 = md5.digest(oldFileContents);
					md5.reset();
					byte[] newMD5 = md5.digest(newFileContents);
					md5.reset();

					if (oldMD5.length == newMD5.length) {
						templateChanged = false;
						for (int i = 0; !templateChanged && i < oldMD5.length; i++) {
							if (oldMD5[i] != newMD5[i]) {
								templateChanged = true;
							}
						}
					}
				}
			}

			if (templateChanged) {
				FileOutputStream newFileStream = new FileOutputStream(outputFile);
				BufferedOutputStream newFileBufferedStream = new BufferedOutputStream(newFileStream);
				try {
					newFileBufferedStream.write(newFileContents);
				} finally {
					newFileBufferedStream.close();
				}
			} else {
				// System.out.println("EOGenerator.writeTemplate: skipping " +
				// outputFile);
			}
		}
	}
}

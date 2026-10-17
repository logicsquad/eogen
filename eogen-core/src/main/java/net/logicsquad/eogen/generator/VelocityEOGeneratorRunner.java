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
package net.logicsquad.eogen.generator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import net.logicsquad.eogen.velocity.ListTool;
import net.logicsquad.eogen.velocity.SetTool;
import net.logicsquad.eogen.velocity.UnresolvedReference;
import net.logicsquad.eogen.velocity.VelocityOneContext;
import net.logicsquad.eogen.generator.EOGeneratorModel.Define;
import net.logicsquad.eogen.Finding;
import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EOModelException;
import net.logicsquad.eogen.model.EOModelGroup;
import net.logicsquad.eogen.model.EOModelRenderContext;
import net.logicsquad.eogen.model.EOModelVerificationFailure;
import net.logicsquad.eogen.model.IEOModelGroupFactory;
import net.logicsquad.eogen.runtime.IProgressMonitor;
import net.logicsquad.eogen.runtime.OperationCanceledException;
import net.logicsquad.eogen.utils.BooleanUtils;
import net.logicsquad.eogen.velocity.WOLipsVelocityUtils;

public class VelocityEOGeneratorRunner {
	private boolean _useStdout = false;

	private List<IEOModelGroupFactory> _modelGroupFactories = Collections.emptyList();

	private DestinationMapper _destinationMapper = (name, destination) -> destination;

	// eogen: what the run reported as errors, as findings.
	private final List<Finding> _findings = new ArrayList<Finding>();

	// eogen: jars and folders of shared templates.
	private List<File> _templatePath = Collections.emptyList();

	public VelocityEOGeneratorRunner() {
	}

	/**
	 * Sets the factories that load the model group for -loadModelGroup, in
	 * the order to try them. WOLips found them in the Eclipse extension
	 * registry.
	 * 
	 * @param modelGroupFactories
	 *            the model group factories
	 */
	public void setModelGroupFactories(List<IEOModelGroupFactory> modelGroupFactories) {
		_modelGroupFactories = new ArrayList<IEOModelGroupFactory>(modelGroupFactories);
	}

	public List<IEOModelGroupFactory> getModelGroupFactories() {
		return _modelGroupFactories;
	}

	/**
	 * Sets the jars and folders of shared templates, which come after the
	 * project's own and before WOLips's.
	 *
	 * @param templatePath
	 *            jars and folders
	 */
	public void setTemplatePath(List<File> templatePath) {
		_templatePath = new ArrayList<File>(templatePath);
	}

	/**
	 * Returns what the last run reported as errors, as findings, with absolute
	 * file paths.
	 *
	 * @return the findings
	 */
	public List<Finding> getFindings() {
		return Collections.unmodifiableList(_findings);
	}

	/**
	 * Sets where each destination's files are written. Templates still see
	 * the EOGeneratorModel's own destinations, and a subclass is still only
	 * generated when it is missing from the subclass destination itself. The
	 * default writes to the destinations themselves.
	 * 
	 * @param destinationMapper
	 *            chooses the folder to write each destination's files to
	 */
	public void setDestinationMapper(DestinationMapper destinationMapper) {
		_destinationMapper = destinationMapper;
	}

	/**
	 * Chooses the folder that a destination's files are written to.
	 */
	public interface DestinationMapper {
		/**
		 * Returns the folder to write a destination's files to.
		 * 
		 * @param name
		 *            the destination's name: EOGeneratorModel.DESTINATION,
		 *            SUBCLASS_DESTINATION, DESTINATION2, DESTINATION3 or
		 *            DESTINATION4
		 * @param destination
		 *            the destination folder, resolved against the project
		 * @return the folder to write to
		 */
		File map(String name, File destination);
	}

	public boolean generate(EOGeneratorModel eogeneratorModel, StringBuffer results, IProgressMonitor monitor) throws Exception {
		return generate(eogeneratorModel, results, null, null, EOGeneratorResourceLoader.class, monitor);
	}

	public boolean generate(EOGeneratorModel eogeneratorModel, StringBuffer results, EOModelGroup preloadedModelGroup, IProgressMonitor monitor) throws Exception {
		return generate(eogeneratorModel, results, preloadedModelGroup, null, EOGeneratorResourceLoader.class, monitor);
	}

	public boolean generate(EOGeneratorModel eogeneratorModel, StringBuffer results, EOModelGroup preloadedModelGroup, List<String> entityList, Class<? extends ResourceLoader> resourceLoaderClass, IProgressMonitor monitor) throws Exception {
		_findings.clear();
		boolean showResults = false;

		String superclassTemplateName = eogeneratorModel.getJavaTemplate();
		String superclassTemplate2Name = eogeneratorModel.getJavaTemplate2();
		String superclassTemplate3Name = eogeneratorModel.getJavaTemplate3();
		String superclassTemplate4Name = eogeneratorModel.getJavaTemplate4();
		String subclassTemplateName = eogeneratorModel.getSubclassJavaTemplate();

		boolean eogeneratorJava14 = eogeneratorModel.isJava14();
		if (eogeneratorJava14) {
			if (superclassTemplateName == null || superclassTemplateName.length() == 0) {
				superclassTemplateName = "_Entity14.java";
			}
			if (subclassTemplateName == null || subclassTemplateName.length() == 0) {
				subclassTemplateName = "Entity14.java";
			}
		} else {
			if (superclassTemplateName == null || superclassTemplateName.length() == 0) {
				superclassTemplateName = "_Entity.java";
			}
			if (subclassTemplateName == null || subclassTemplateName.length() == 0) {
				subclassTemplateName = "Entity.java";
			}
		}

		VelocityEngine velocityEngine = WOLipsVelocityUtils.createVelocityEngine("EOGenerator", eogeneratorModel.getTemplateDir(), eogeneratorModel.getProjectPath(), _templatePath, resourceLoaderClass);

		List<EOModel> models = new LinkedList<EOModel>();
		EOModelRenderContext renderContext = new EOModelRenderContext();
		try {
			String prefix = eogeneratorModel.getPrefix();
			if (prefix != null) {
				renderContext.setPrefix(prefix);
			}
			renderContext.setSuperclassPackage(eogeneratorModel.getSuperclassPackage());
			String eogenericRecordClassName = eogeneratorModel.getDefineValueNamed("EOGenericRecord");
			if (eogenericRecordClassName != null) {
				renderContext.setEOGenericRecordClassName(eogenericRecordClassName);
			}
			renderContext.setJavaClientCommon(eogeneratorModel.isJavaClientCommon() != null && eogeneratorModel.isJavaClientCommon().booleanValue());
			renderContext.setJavaClient(eogeneratorModel.isJavaClient() != null && eogeneratorModel.isJavaClient().booleanValue());
			EOModelRenderContext.setRenderContext(renderContext);

			EOModelGroup modelGroup;
			if (preloadedModelGroup == null) {
				modelGroup = new EOModelGroup();

				Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();

				if (BooleanUtils.isTrue(eogeneratorModel.isLoadModelGroup())) {
					for (EOModelReference eomodelReference : eogeneratorModel.getModels()) {
						EOModelGroup generatingModelGroup = new EOModelGroup();
						URL modelURL = getModelURL(eogeneratorModel, eomodelReference);
						IEOModelGroupFactory.Utility.loadModelGroup(_modelGroupFactories, modelURL, generatingModelGroup, failures, true, modelURL, monitor);
						if (monitor.isCanceled()) {
							throw new OperationCanceledException("EOGenerator canceled.");
						}
						EOModel generatingModel = generatingModelGroup.getEditingModel();
						if (generatingModel == null) {
							// eogen: the errors say which model failed, and where, so the
							// URL is only for when there are none.
							Set<String> errors = new LinkedHashSet<String>();
							for (EOModelVerificationFailure failure : failures) {
								if (!failure.isWarning() && errors.add(failure.getMessage())) {
									_findings.add(Finding.of(failure).build());
								}
							}
							throw new EOModelException(errors.isEmpty() ? "Failed to load the model " + modelURL + "." : String.join(" ", errors));
						}
						models.add(generatingModel);

						for (EOModel model : generatingModelGroup.getModels()) {
							if (!modelGroup.containsModelNamed(model.getName())) {
								modelGroup.addModel(model);
							}
						}
					}
				} else {
					loadModels(eogeneratorModel, modelGroup, eogeneratorModel.getModels(), models, monitor);
					loadModels(eogeneratorModel, modelGroup, eogeneratorModel.getRefModels(), new LinkedList<EOModel>(), monitor);

					modelGroup.resolve(failures);
					modelGroup.verify(failures);
				}

				for (EOModelVerificationFailure failure : failures) {
					if (!failure.isWarning()) {
						results.append("Error: " + failure.getMessage() + "\n");
						_findings.add(Finding.of(failure).build());
						showResults = true;
					}
				}
			} else {
				modelGroup = preloadedModelGroup;
				for (EOModelReference modelRef : eogeneratorModel.getModels()) {
					String modelName = modelRef.getName();
					EOModel model = modelGroup.getModelNamed(modelName);
					if (model == null) {
						throw new RuntimeException("There was no model named '" + modelName + "' in this model group.");
					}
					models.add(model);
				}
			}

			File superclassDestination = new File(eogeneratorModel.getDestination());
			if (!superclassDestination.isAbsolute()) {
				Path projectPath = eogeneratorModel.getProjectPath();
				if (projectPath != null) {
					superclassDestination = new File(projectPath.toFile(), eogeneratorModel.getDestination());
				}
			}
			File realSuperclassDestination = superclassDestination;
			superclassDestination = _destinationMapper.map(EOGeneratorModel.DESTINATION, superclassDestination);
			if (!superclassDestination.exists()) {
				if (!superclassDestination.mkdirs()) {
					throw new IOException("Failed to create destination '" + superclassDestination + "'.");
				}
			}

			File subclassDestination = new File(eogeneratorModel.getSubclassDestination());
			if (!subclassDestination.isAbsolute()) {
				Path projectPath = eogeneratorModel.getProjectPath();
				if (projectPath != null) {
					subclassDestination = new File(projectPath.toFile(), eogeneratorModel.getSubclassDestination());
				}
			}
			File subclassOutputDestination = _destinationMapper.map(EOGeneratorModel.SUBCLASS_DESTINATION, subclassDestination);
			if (!subclassOutputDestination.exists()) {
				if (!subclassOutputDestination.mkdirs()) {
					throw new IOException("Failed to create subclass destination '" + subclassOutputDestination + "'.");
				}
			}
			
			File superclass2Destination = null;
			if (eogeneratorModel.getDestination2() != null) {
				superclass2Destination = new File(eogeneratorModel.getDestination2());
				if (!superclass2Destination.isAbsolute()) {
					Path projectPath = eogeneratorModel.getProjectPath();
					if (projectPath != null) {
						superclass2Destination = new File(projectPath.toFile(), eogeneratorModel.getDestination2());
					}
				}
				superclass2Destination = _destinationMapper.map(EOGeneratorModel.DESTINATION2, superclass2Destination);
				if (!superclass2Destination.exists()) {
					if (!superclass2Destination.mkdirs()) {
						throw new IOException("Failed to create destination2 '" + superclass2Destination + "'.");
					}
				}
			}
			
			File superclass3Destination = null;
			if (eogeneratorModel.getDestination3() != null) {
				superclass3Destination = new File(eogeneratorModel.getDestination3());
				if (!superclass3Destination.isAbsolute()) {
					Path projectPath = eogeneratorModel.getProjectPath();
					if (projectPath != null) {
						superclass3Destination = new File(projectPath.toFile(), eogeneratorModel.getDestination3());
					}
				}
				superclass3Destination = _destinationMapper.map(EOGeneratorModel.DESTINATION3, superclass3Destination);
				if (!superclass3Destination.exists()) {
					if (!superclass3Destination.mkdirs()) {
						throw new IOException("Failed to create destination3 '" + superclass3Destination + "'.");
					}
				}
			}
			
			File superclass4Destination = null;
			if (eogeneratorModel.getDestination4() != null) {
				superclass4Destination = new File(eogeneratorModel.getDestination4());
				if (!superclass4Destination.isAbsolute()) {
					Path projectPath = eogeneratorModel.getProjectPath();
					if (projectPath != null) {
						superclass4Destination = new File(projectPath.toFile(), eogeneratorModel.getDestination4());
					}
				}
				superclass4Destination = _destinationMapper.map(EOGeneratorModel.DESTINATION4, superclass4Destination);
				if (!superclass4Destination.exists()) {
					if (!superclass4Destination.mkdirs()) {
						throw new IOException("Failed to create destination4 '" + superclass4Destination + "'.");
					}
				}
			}

			// String filePathTemplate = eogeneratorModel.getFilenameTemplate();
			// if (filePathTemplate == null || filePathTemplate.trim().length()
			// == 0) {
			// }

			String extension = eogeneratorModel.getExtension();
			Set<String> entitySet = new HashSet<String>();
			if (entityList != null) {
				entitySet.addAll(entityList);
			}
			Date date = new Date();
			
			for (EOModel model : models) {
				if (monitor.isCanceled()) {
					throw new OperationCanceledException("EOGenerator canceled.");
				}
				// System.out.println("Generating " + model.getName() + " ...");
				for (EOEntity entity : model.getEntities()) {
					VelocityContext context = new VelocityOneContext();
					context.put("eogeneratorModel", eogeneratorModel);
					context.put("date", date);
					context.put("calendar", Calendar.getInstance());
					for (Define define : eogeneratorModel.getDefines()) {
						context.put(define.getName(), define.getValue());
					}
					context.put("list", new ListTool());
					context.put("set", new SetTool());
					//context.put("sorter", new SortTool());
					context.put("model", model);

					if (entitySet.size() != 0 && !entitySet.contains(entity.getName())) {
						continue;
					}
					if (monitor.isCanceled()) {
						throw new OperationCanceledException("EOGenerator canceled.");
					}
					// System.out.println("Generating " + model.getName() + "."
					// + entity.getName() + " ...");
					context.put("entity", entity);

					String classNameWithPackage = renderContext.getClassNameForEntity(entity);
					boolean eogenericRecord = classNameWithPackage == null || (classNameWithPackage.endsWith("GenericRecord") && !entity.getName().endsWith("GenericRecord"));
					if (entity.isGenerateSource() && !eogenericRecord) {
						String prefixClassNameWithPackage = entity.getPrefixClassName();
						context.put("className", classNameWithPackage);
						context.put("prefixClassName", prefixClassNameWithPackage);
						context.put("packageName", entity.getPackageName());
						context.put("classNameWithoutPackage", entity.getClassNameWithoutPackage());
						context.put("prefixClassNameWithoutPackage", entity.getPrefixClassNameWithoutPackage());

						if (_useStdout) {
							WOLipsVelocityUtils.writeTemplate(velocityEngine, context, superclassTemplateName, System.out);
							showResults |= reportUnresolvedReferences(context, superclassTemplateName, null, results);
							WOLipsVelocityUtils.writeTemplate(velocityEngine, context, subclassTemplateName, System.out);
							showResults |= reportUnresolvedReferences(context, subclassTemplateName, null, results);
							if (superclassTemplate2Name != null) {
								WOLipsVelocityUtils.writeTemplate(velocityEngine, context, superclassTemplate2Name, System.out);
								showResults |= reportUnresolvedReferences(context, superclassTemplate2Name, null, results);
							}
							if (superclassTemplate3Name != null) {
								WOLipsVelocityUtils.writeTemplate(velocityEngine, context, superclassTemplate3Name, System.out);
								showResults |= reportUnresolvedReferences(context, superclassTemplate3Name, null, results);
							}
							if (superclassTemplate4Name != null) {
								WOLipsVelocityUtils.writeTemplate(velocityEngine, context, superclassTemplate4Name, System.out);
								showResults |= reportUnresolvedReferences(context, superclassTemplate4Name, null, results);
							}
						}
						else {
							String superclassFileTemplate;
							// StringWriter superclassFilePathWriter = new
							// StringWriter();
							// velocityEngine.evaluate(context,
							// superclassFilePathWriter, "LOG",
							// superclassFileTemplate);
							if (BooleanUtils.isFalse(eogeneratorModel.isPackageDirs())) {
								superclassFileTemplate = entity.getPrefixClassNameWithoutPackage();
							} else {
								superclassFileTemplate = prefixClassNameWithPackage.toString().replace('.', '/');
							}

							String superclassFilePath = superclassFileTemplate + "." + extension;
							File superclassFile = new File(superclassDestination, superclassFilePath);
							File superclassFolder = superclassFile.getParentFile();
							if (!superclassFolder.exists()) {
								if (!superclassFolder.mkdirs()) {
									throw new IOException("Unable to make superclass folder '" + superclassFolder + "'.");
								}
							}
							
							WOLipsVelocityUtils.writeTemplate(velocityEngine, context, superclassTemplateName, superclassFile);
							showResults |= reportUnresolvedReferences(context, superclassFilePath, new File(realSuperclassDestination, superclassFilePath), results);

							String subclassFileTemplate;
							// StringWriter subclassFilePathWriter = new
							// StringWriter();
							// velocityEngine.evaluate(context,
							// subclassFilePathWriter, "LOG", subclassFileTemplate);
							if (BooleanUtils.isFalse(eogeneratorModel.isPackageDirs())) {
								subclassFileTemplate = entity.getClassNameWithoutPackage();
							} else {
								subclassFileTemplate = classNameWithPackage.toString().replace('.', '/');
							}

							String subclassFilePath = subclassFileTemplate + "." + extension;
							File subclassFile = new File(subclassDestination, subclassFilePath);
							File subclassOutputFile = new File(subclassOutputDestination, subclassFilePath);
							File subclassFolder = subclassOutputFile.getParentFile();
							if (!subclassFolder.exists()) {
								if (!subclassFolder.mkdirs()) {
									throw new IOException("Unable to make subclass folder '" + superclassFolder + "'.");
								}
							}
							if (!subclassFile.exists() && !subclassOutputFile.exists()) {
								WOLipsVelocityUtils.writeTemplate(velocityEngine, context, subclassTemplateName, subclassOutputFile);
								showResults |= reportUnresolvedReferences(context, subclassFilePath, subclassFile, results);
							}
							
							if (superclass2Destination != null) {
								File superclass2File = new File(superclass2Destination, superclassFilePath);
								File superclass2Folder = superclass2File.getParentFile();
								if (!superclass2Folder.exists()) {
									if (!superclass2Folder.mkdirs()) {
										throw new IOException("Unable to make superclass folder2 '" + superclass2Folder + "'.");
									}
								}
								
								WOLipsVelocityUtils.writeTemplateToDirectory(velocityEngine, context, superclassTemplate2Name, superclass2Folder);
								showResults |= reportUnresolvedReferences(context, "-destination2 for " + entity.getName(), null, results);
							}
							
							if (superclass3Destination != null) {
								File superclass3File = new File(superclass3Destination, superclassFilePath);
								File superclass3Folder = superclass3File.getParentFile();
								if (!superclass3Folder.exists()) {
									if (!superclass3Folder.mkdirs()) {
										throw new IOException("Unable to make superclass folder3 '" + superclass3Folder + "'.");
									}
								}
								
								WOLipsVelocityUtils.writeTemplateToDirectory(velocityEngine, context, superclassTemplate3Name, superclass3Folder);
								showResults |= reportUnresolvedReferences(context, "-destination3 for " + entity.getName(), null, results);
							}
							
							if (superclass4Destination != null) {
								File superclass4File = new File(superclass4Destination, superclassFilePath);
								File superclass4Folder = superclass4File.getParentFile();
								if (!superclass4Folder.exists()) {
									if (!superclass4Folder.mkdirs()) {
										throw new IOException("Unable to make superclass folder4 '" + superclass4Folder + "'.");
									}
								}
								
								WOLipsVelocityUtils.writeTemplateToDirectory(velocityEngine, context, superclassTemplate4Name, superclass4Folder);
								showResults |= reportUnresolvedReferences(context, "-destination4 for " + entity.getName(), null, results);
							}
						}
					}
				}
			}
		} finally {
			EOModelRenderContext.clearRenderContext();
		}
		return showResults;
	}

	/**
	 * Reports, as errors, the references that a template has just rendered
	 * without a value. Velocity writes such a reference into the output as it
	 * stands, so the generated file would not compile.
	 * 
	 * @param context
	 *            the context the template rendered with
	 * @param output
	 *            what the template rendered: a file's path relative to its
	 *            destination, or a template's name
	 * @param outputFile
	 *            the file in its destination, or null if it isn't one
	 * @param results
	 *            where to report them
	 * @return whether there were any
	 */
	private boolean reportUnresolvedReferences(VelocityContext context, String output, File outputFile, StringBuffer results) {
		boolean reported = false;
		if (context instanceof VelocityOneContext) {
			Object entity = context.get("entity");
			for (UnresolvedReference reference : ((VelocityOneContext) context).takeUnresolvedReferences()) {
				String message = output + ": " + reference.getDescription();
				results.append("Error: " + message + "\n");
				_findings.add(Finding.error(Finding.UNRESOLVED_REFERENCE, message).file(outputFile == null ? null : outputFile.getAbsolutePath())
						.entity(entity instanceof EOEntity ? ((EOEntity) entity).getName() : null).object(reference.getObject()).key(reference.getReference())
						.template(reference.getTemplate()).build());
				reported = true;
			}
		}
		return reported;
	}

	public URL getModelURL(EOGeneratorModel eogeneratorModel, EOModelReference modelRef) throws MalformedURLException {
		String modelPath = modelRef.getPath((Path) null);
		File modelFile = new File(modelPath);
		if (!modelFile.isAbsolute()) {
			Path projectPath = eogeneratorModel.getProjectPath();
			if (projectPath != null) {
				modelFile = new File(projectPath.toFile(), modelPath);
			}
		}
		return modelFile.toURL();
	}

	public void loadModels(EOGeneratorModel eogeneratorModel, EOModelGroup modelGroup, List<EOModelReference> modelReferences, List<EOModel> loadedModels, IProgressMonitor monitor) throws MalformedURLException, IOException, EOModelException {
		for (EOModelReference modelRef : modelReferences) {
			if (monitor.isCanceled()) {
				throw new OperationCanceledException("EOGenerator canceled.");
			}
			URL modelURL = getModelURL(eogeneratorModel, modelRef);
			EOModel model = modelGroup.loadModelFromURL(modelURL);
			loadedModels.add(model);
		}
	}
	
	public void setUseStdout(boolean useStdout) {
		_useStdout = useStdout;
	}
}

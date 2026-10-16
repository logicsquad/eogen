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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.logicsquad.eogen.utils.StringUtils;
import net.logicsquad.eogen.model.EOArgument;
import net.logicsquad.eogen.model.EODatabaseConfig;
import net.logicsquad.eogen.model.EOEntity;
import net.logicsquad.eogen.model.EOModel;
import net.logicsquad.eogen.model.EOModelObject;
import net.logicsquad.eogen.model.EOModelReferenceFailure;
import net.logicsquad.eogen.model.EOStoredProcedure;
import net.logicsquad.eogen.model.IEOEntityRelative;

public class EOModelUtils {
	public static String getOperationLabel(String operation, List objs) {
		StringBuffer operationLabel = new StringBuffer();
		operationLabel.append(operation);
		if (objs != null) {
			if (objs.size() == 1) {
				Object obj = objs.get(0);
				if (obj instanceof EOModelObject) {
					operationLabel.append(" ");
					operationLabel.append(((EOModelObject) obj).getName());
				}
			} else if (objs.size() > 1) {
				operationLabel.append(" ");
				operationLabel.append(objs.size());
				Class objType = null;
				for (Object obj : objs) {
					Class thisObjType = obj.getClass();
					if (objType == null) {
						objType = thisObjType;
					} else if (!objType.isAssignableFrom(thisObjType)) {
						objType = null;
						break;
					}
				}
				operationLabel.append(" ");
				if (objType == null) {
					operationLabel.append("Items");
				} else {
					operationLabel.append(StringUtils.toPlural(StringUtils.toShortPrettyClassName(objType.getName())));
				}
			}
		}
		return operationLabel.toString();
	}

	public static Set<EOModelObject> getRecommendedDeletions(Object[] selectedObjects) {
		Set<EOModelReferenceFailure> beforeReferenceFailures = EOModelUtils.getReferenceFailures(selectedObjects); 
		Set<EOModelObject> allRecommendedObjectsSet = new HashSet<EOModelObject>();
		for (Object selectedObject : selectedObjects) {
			if (selectedObject instanceof EOModelObject) {
				EOModelObject modelObject = (EOModelObject) selectedObject;
				allRecommendedObjectsSet.add(modelObject);
			}
		}
		for (EOModelReferenceFailure referenceFailure : beforeReferenceFailures) {
			Set<EOModelObject> recommendedDeletions = referenceFailure.getRecommendedDeletions();
			for (EOModelObject recommendedDelete : recommendedDeletions) {
				allRecommendedObjectsSet.add(recommendedDelete);
			}
		}
		Object[] recommendedObjects = allRecommendedObjectsSet.toArray();
		Set<EOModelReferenceFailure> afterReferenceFailures = EOModelUtils.getReferenceFailures(recommendedObjects);
		if (beforeReferenceFailures.size() != afterReferenceFailures.size()) {
			allRecommendedObjectsSet = EOModelUtils.getRecommendedDeletions(recommendedObjects);
		}
		return allRecommendedObjectsSet;
	}

	public static Set<EOModelReferenceFailure> getReferenceFailures(Object[] selectedObjects) {
		Set<EOModelObject> deletedObjects = new HashSet<EOModelObject>();
		Set<EOModelReferenceFailure> referenceFailures = new HashSet<EOModelReferenceFailure>();
		for (Object selectedObject : selectedObjects) {
			if (selectedObject instanceof EOModelObject) {
				EOModelObject modelObject = (EOModelObject) selectedObject;
				deletedObjects.add(modelObject);
				referenceFailures.addAll(modelObject.getReferenceFailures());
			}
		}
		
		for (EOModelObject deletedObject : deletedObjects) {
			for (EOModelReferenceFailure referenceFailure : new HashSet<EOModelReferenceFailure>(referenceFailures)) {
				if (referenceFailure.getReferencingObject().equals(deletedObject)) {
					referenceFailures.remove(referenceFailure);
				}
			}
		}

		return referenceFailures;
	}

	public static EOModelObject getRelated(Class<? extends EOModelObject> type, EOModelObject obj) {
		EOModelObject relatedObj;
		if (EOModel.class.isAssignableFrom(type)) {
			relatedObj = EOModelUtils.getRelatedModel(obj);
		} else if (EOEntity.class.isAssignableFrom(type)) {
			relatedObj = EOModelUtils.getRelatedEntity(obj);
		} else if (EOStoredProcedure.class.isAssignableFrom(type)) {
			if (obj instanceof EOStoredProcedure) {
				relatedObj = obj;
			} else if (obj instanceof EOArgument) {
				relatedObj = ((EOArgument) obj).getStoredProcedure();
			} else {
				relatedObj = null;
			}
		} else {
			relatedObj = null;
		}
		return relatedObj;
	}

	public static EOModel getRelatedModel(Object obj) {
		EOModel model = null;
		if (obj instanceof EOModel) {
			model = (EOModel) obj;
		} else if (obj instanceof IEOEntityRelative) {
			model = ((IEOEntityRelative) obj).getEntity().getModel();
		} else if (obj instanceof EOStoredProcedure) {
			model = ((EOStoredProcedure) obj).getModel();
		} else if (obj instanceof EOArgument) {
			model = ((EOArgument) obj).getStoredProcedure().getModel();
		} else if (obj instanceof EODatabaseConfig) {
			model = ((EODatabaseConfig) obj).getModel();
		} else if (obj instanceof Object[]) {
			Object[] objs = (Object[]) obj;
			for (Object arrayObj : objs) {
				model = EOModelUtils.getRelatedModel(arrayObj);
				if (model != null) {
					break;
				}
			}
		}
		return model;
	}

	public static EOEntity getRelatedEntity(Object obj) {
		EOEntity entity = null;
		if (obj instanceof IEOEntityRelative) {
			entity = ((IEOEntityRelative) obj).getEntity();
		}
		return entity;
	}

	public static EOStoredProcedure getRelatedStoredProcedure(Object obj) {
		EOStoredProcedure storedProcedure = null;
		if (obj instanceof EOStoredProcedure) {
			storedProcedure = (EOStoredProcedure) obj;
		} else if (obj instanceof EOArgument) {
			storedProcedure = ((EOArgument) obj).getStoredProcedure();
		}
		return storedProcedure;
	}
}

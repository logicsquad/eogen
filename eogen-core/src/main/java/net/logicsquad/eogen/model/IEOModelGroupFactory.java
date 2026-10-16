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

import java.net.URL;
import java.util.List;
import java.util.Set;

import net.logicsquad.eogen.runtime.IProgressMonitor;

/**
 * Implemented by objects that can load EOModels within an EOModelGroup.
 * Outside Eclipse there is no extension registry to find them, so callers
 * pass the factories to {@link Utility}, in the order to try them.
 * 
 * @author mschrag
 */
public interface IEOModelGroupFactory {
	/**
	 * Loads models into the specified EOModelGroup based on the
	 * modelGroupResource provided.
	 * 
	 * @param modelGroupResource
	 *            the IProject, Folder, etc to load the model group from
	 * @param modelGroup
	 *            the model group to load into
	 * @param failures
	 *            the set of failures during load
	 * @param skipOnDuplicates
	 *            whether or not to skip duplicate models
	 * @return true if this model group factory believes it has found everything
	 * @throws EOModelException
	 *             if there is a problem loading models
	 */
	public boolean loadModelGroup(Object modelGroupResource, EOModelGroup modelGroup, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws EOModelException;

	public class Utility {
		public static EOModelGroup loadModelGroup(List<IEOModelGroupFactory> modelGroupFactories, Object modelGroupResource, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws EOModelException {
			EOModelGroup modelGroup = new EOModelGroup();
			IEOModelGroupFactory.Utility.loadModelGroup(modelGroupFactories, modelGroupResource, modelGroup, failures, skipOnDuplicates, null, progressMonitor);
			return modelGroup;
		}
		
		public static EOModelGroup loadModelGroup(List<IEOModelGroupFactory> modelGroupFactories, Object modelGroupResource, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, URL editingModelURL, IProgressMonitor progressMonitor) throws EOModelException {
			EOModelGroup modelGroup = new EOModelGroup();
			IEOModelGroupFactory.Utility.loadModelGroup(modelGroupFactories, modelGroupResource, modelGroup, failures, skipOnDuplicates, editingModelURL, progressMonitor);
			return modelGroup;
		}
		
		public static void loadModelGroup(List<IEOModelGroupFactory> modelGroupFactories, Object modelGroupResource, EOModelGroup modelGroup, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, URL editingModelURL, IProgressMonitor progressMonitor) throws EOModelException {
			modelGroup.setEditingModelURL(editingModelURL);
			for (IEOModelGroupFactory modelGroupFactory : modelGroupFactories) {
				boolean modelsLoaded = modelGroupFactory.loadModelGroup(modelGroupResource, modelGroup, failures, skipOnDuplicates, progressMonitor);
				// MS: This COOOouuullldd cause a problem, but only if you're loading models in MULTIPLE ways, which seems unlikely to me ... This
				// saves a lot of time if you have IDEA and Eclipse project files. We can just load models using the first one we find.
				if (modelsLoaded) {
					break;
				}
			}
			progressMonitor.setTaskName("Resolving model dependencies ...");
			modelGroup.resolve(failures);
			progressMonitor.setTaskName("Verifying model ...");
			modelGroup.verify(failures);
		}
	}
}

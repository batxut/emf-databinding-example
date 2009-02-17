/*******************************************************************************
 * Copyright (c) 2007 Brad Reynolds and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brad Reynolds - initial API and implementation
 *     Brad Reynolds - bug 147515
 *     Tom Schindl<tom.schindl@bestsolution.at> - Port to EMF
 ******************************************************************************/
package org.eclipse.emf.databinding.edit.properties;

import org.eclipse.core.databinding.observable.IObserving;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.domain.EditingDomain;

/**
 * <p>
 * <b>PROVISIONAL This API is subject to arbitrary change, including renaming or
 * removal.</b>
 * </p>
 * Provides access to details of EMF observables.
 * <p>
 * This interface is not meant to be implemented by clients.
 * </p>
 * 
 * @since 1.1
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IEMFEditObservable extends IObserving {
	/**
	 * @return the feature observed
	 */
	public EStructuralFeature getFeature();

	/**
	 * @return the domain the changes happen in
	 */
	public EditingDomain getDomain();
}

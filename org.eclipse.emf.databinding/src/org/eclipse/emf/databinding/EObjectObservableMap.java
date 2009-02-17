/**
 * <copyright> 
 *
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 *   Tom Schindl<tom.schindl@bestsolution.at>
 * </copyright>
 *
 * $Id: EObjectObservableMap.java,v 1.4 2008/10/21 11:03:56 emerks Exp $
 */
package org.eclipse.emf.databinding;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.map.ComputedObservableMap;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapDiff;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.ExtendedMetaData;

/**
 * <p>
 * <b>PROVISIONAL This API is subject to arbitrary change, including renaming or
 * removal.</b>
 * </p>
 * 
 * @deprecated you should not use this type it will be removed. Use the generic
 *             {@link IObservableMap}
 */
public class EObjectObservableMap extends ComputedObservableMap {
	/**
	 * The structural feature observed
	 */
	protected EStructuralFeature eStructuralFeature;

	private Adapter elementListener = new AdapterImpl() {
		@Override
		public void notifyChanged(Notification notification) {
			if (eStructuralFeature == notification.getFeature()
					&& !notification.isTouch()) {
				// TODO
				// This assumes we only get a SET notification, which isn't a
				// good assumption.
				//
				final MapDiff diff = Diffs.createMapDiffSingleChange(
						notification.getNotifier(), notification.getOldValue(),
						notification.getNewValue());
				getRealm().exec(new Runnable() {
					public void run() {
						fireMapChange(diff);
					}
				});
			}
		}
	};

	/**
	 * Create a new observable for the feature in the given set of objects
	 * 
	 * @param objects
	 *            the objects whose feature is observed
	 * @param feature
	 *            the feature to observe
	 * @deprecated you should use
	 *             {@link EMFObservables#observeMap(IObservableSet, EStructuralFeature)}
	 */
	public EObjectObservableMap(IObservableSet objects,
			EStructuralFeature feature) {
		super(objects);
		this.eStructuralFeature = feature;
	}

	@Override
	protected void hookListener(Object domainElement) {
		((EObject) domainElement).eAdapters().add(elementListener);
	}

	@Override
	protected void unhookListener(Object domainElement) {
		((EObject) domainElement).eAdapters().remove(elementListener);
	}

	@Override
	protected Object doGet(Object key) {
		EObject eObject = (EObject) key;
		return ExtendedMetaData.INSTANCE.getAffiliation(eObject.eClass(),
				eStructuralFeature) == null ? null : eObject
				.eGet(eStructuralFeature);
	}

	@Override
	protected Object doPut(Object key, Object value) {
		EObject eObject = (EObject) key;
		Object result = eObject.eGet(eStructuralFeature);
		eObject.eSet(eStructuralFeature, value);
		return result;
	}
}

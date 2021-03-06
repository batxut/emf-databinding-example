/**
 * <copyright>
 * </copyright>
 *
 * $Id: ProxyValidator.java,v 1.2 2007/03/29 15:06:59 mtaal Exp $
 */
package org.eclipse.emf.teneo.hibernate.hbannotation.validation;

import org.eclipse.emf.teneo.hibernate.hbannotation.CacheConcurrencyStrategy;

/**
 * A sample validator interface for {@link org.eclipse.emf.teneo.hibernate.hbannotation.Proxy}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface ProxyValidator {
	boolean validate();

	boolean validateProxyClass(String value);

	boolean validateLazy(boolean value);

	boolean validateProxyClass(CacheConcurrencyStrategy value);
	boolean validateLazy(Boolean value);
}

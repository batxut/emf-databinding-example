/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.examples.extlibrary.validation;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.examples.extlibrary.Book;

/**
 * A sample validator interface for {@link org.eclipse.emf.examples.extlibrary.Writer}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface WriterValidator {
	boolean validate();

	boolean validateName(String value);
	boolean validateBooks(EList<Book> value);
}

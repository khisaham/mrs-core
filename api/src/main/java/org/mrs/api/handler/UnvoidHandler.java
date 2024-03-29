/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.api.handler;

import java.util.Date;

import org.mrs.User;
import org.mrs.Voidable;
import org.mrs.aop.RequiredDataAdvice;

/**
 * This is the super interface for all unvoid* actions that take place on all services. The
 * {@link RequiredDataAdvice} class uses AOP around each method in every service to check to see if
 * its a unvoid* method. If it is a unvoid* method, this class is called to handle setting the
 * {@link Voidable#getVoided()}, {@link Voidable#setVoidReason(String)},
 * {@link Voidable#setVoidedBy(User)}, and {@link Voidable#setDateVoided(Date)} all to null. <br>
 * <br>
 * Child collections on this {@link Voidable} that are themselves a {@link Voidable} are looped over
 * and also unvoided by the {@link RequiredDataAdvice} class.<br>
 * <br>
 * 
 * @see BaseUnvoidHandler
 * @see RequiredDataAdvice
 * @see VoidHandler
 * @since 1.5
 */
public interface UnvoidHandler<V extends Voidable> extends RequiredDataHandler<V> {
	
	/**
	 * Implementing classes are called around every unvoid* method to set everything to null.<br>
	 * <br>
	 * 
	 * @see org.mrs.api.handler.RequiredDataHandler#handle(org.mrs.OpenmrsObject,
	 *      org.mrs.User, java.util.Date, java.lang.String)
	 */
	@Override
	public void handle(V voidableObject, User voidingUser, Date origParentVoidedDate, String unused);
	
}

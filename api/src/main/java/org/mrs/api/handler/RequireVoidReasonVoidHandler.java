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

import org.apache.commons.lang3.StringUtils;
import org.mrs.Cohort;
import org.mrs.Encounter;
import org.mrs.Obs;
import org.mrs.Order;
import org.mrs.Patient;
import org.mrs.User;
import org.mrs.Voidable;
import org.mrs.annotation.Handler;
import org.mrs.aop.RequiredDataAdvice;

/**
 * This class ensures that the voidReason is non-null for supported object types
 * 
 * @see RequiredDataAdvice
 * @see UnvoidHandler
 * @since 1.5
 */
@Handler(supports = { Patient.class, Encounter.class, Obs.class, Cohort.class, Order.class }, order = 1 /* low order so this is run first */)
public class RequireVoidReasonVoidHandler implements VoidHandler<Voidable> {
	
	/**
	 * Validates that the voidReason is non-null and non-empty for supported objects
	 * 
	 * @should throw IllegalArgumentException if Patient voidReason is null
	 * @should throw IllegalArgumentException if Encounter voidReason is empty
	 * @should throw IllegalArgumentException if Obs voidReason is blank
	 * @should not throw Exception if voidReason is not blank
	 * @should not throw Exception if voidReason is null for unsupported types
	 */
	@Override
	public void handle(Voidable voidableObject, User voidingUser, Date voidedDate, String voidReason) {
		
		if (StringUtils.isBlank(voidReason)) {
			throw new IllegalArgumentException("The 'reason' argument is required");
		}
	}
	
}

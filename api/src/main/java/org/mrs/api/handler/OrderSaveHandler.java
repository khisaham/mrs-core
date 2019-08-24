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

import org.mrs.Order;
import org.mrs.User;
import org.mrs.annotation.Handler;
import org.mrs.aop.RequiredDataAdvice;

/**
 * This class deals with {@link Order} objects when they are saved via a save* method in an Openmrs
 * Service. This handler is automatically called by the {@link RequiredDataAdvice} AOP class. <br>
 *
 * @see RequiredDataHandler
 * @see SaveHandler
 * @see Order
 * @since 1.5
 */
@Handler(supports = Order.class)
public class OrderSaveHandler implements SaveHandler<Order> {
	
	/**
	 * @see org.mrs.api.handler.SaveHandler#handle(org.mrs.OpenmrsObject, org.mrs.User,
	 *      java.util.Date, java.lang.String)
	 */
	@Override
	public void handle(Order order, User creator, Date dateCreated, String other) {
		if (order.getPatient() == null && order.getEncounter() != null) {
			order.setPatient(order.getEncounter().getPatient());
		}
	}
}

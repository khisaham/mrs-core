/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.propertyeditor;

import org.mrs.Order;
import org.mrs.api.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderEditorTest extends BasePropertyEditorTest<Order, OrderEditor> {
	
	private static final Integer EXISTING_ID = 2;
	
	@Autowired
	private OrderService orderService;
	
	@Override
	protected OrderEditor getNewEditor() {
		return new OrderEditor();
	}
	
	@Override
	protected Order getExistingObject() {
		return orderService.getOrder(EXISTING_ID);
	}
}

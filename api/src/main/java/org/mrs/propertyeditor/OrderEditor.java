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
import org.mrs.api.context.Context;

/**
 * Allows for serializing/deserializing a Order object to a string so that Spring knows how to pass
 * a Order back and forth through an html form or other medium <br>
 * In version 1.9, added ability for this to also retrieve objects by uuid
 * 
 * @see Order
 */
public class OrderEditor extends OpenmrsPropertyEditor<Order> {
	
	@Override
	protected Order getObjectById(Integer id) {
		return Context.getOrderService().getOrder(id);
	}
	
	@Override
	protected Order getObjectByUuid(String uuid) {
		return Context.getOrderService().getOrderByUuid(uuid);
	}
}

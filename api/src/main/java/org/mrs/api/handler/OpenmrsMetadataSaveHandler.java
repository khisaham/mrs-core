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

import org.mrs.OpenmrsMetadata;
import org.mrs.User;

/**
 * Is called when any {@link OpenmrsMetadata} object is being saved. Trims out the leading and
 * trailing whitespace around the name and description
 */
public class OpenmrsMetadataSaveHandler implements SaveHandler<OpenmrsMetadata> {
	
	/**
	 * @see org.mrs.api.handler.SaveHandler#handle(org.mrs.OpenmrsObject, org.mrs.User,
	 *      java.util.Date, java.lang.String)
	 * @should trim whitespace from name
	 * @should trim whitespace from description
	 */
	@Override
	public void handle(OpenmrsMetadata object, User creator, Date dateCreated, String other) {
		if (object.getName() != null) {
			object.setName(object.getName().trim());
		}
		
		if (object.getDescription() != null) {
			object.setDescription(object.getDescription().trim());
		}
	}
	
}

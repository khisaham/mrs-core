/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.api;

/**
 * Thrown when the new edited order contains modified properties that must be the same as previous
 * order.
 * 
 * @since 2.1
 */
public class EditedOrderDoesNotMatchPreviousException extends OrderEntryException {
	
	public static final long serialVersionUID = 22121218L;
	
	public EditedOrderDoesNotMatchPreviousException(String message) {
		super(message, (Object[]) null);
	}
}

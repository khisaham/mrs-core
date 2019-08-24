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

import org.mrs.PatientIdentifier;

public class DuplicateIdentifierException extends PatientIdentifierException {
	
	private static final long serialVersionUID = 1L;
	
	public DuplicateIdentifierException() {
	}
	
	public DuplicateIdentifierException(String message) {
		super(message);
	}
	
	public DuplicateIdentifierException(String message, PatientIdentifier identifier) {
		super(message, identifier);
	}
	
	public DuplicateIdentifierException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DuplicateIdentifierException(Throwable cause) {
		super(cause);
	}
}

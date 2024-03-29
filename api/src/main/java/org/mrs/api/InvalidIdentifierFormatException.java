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

public class InvalidIdentifierFormatException extends PatientIdentifierException {
	
	private static final long serialVersionUID = 1L;
	
	private String format;
	
	public InvalidIdentifierFormatException() {
	}
	
	public InvalidIdentifierFormatException(String message) {
		super(message);
	}
	
	public InvalidIdentifierFormatException(String message, PatientIdentifier identifier) {
		super(message, identifier);
	}
	
	public InvalidIdentifierFormatException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidIdentifierFormatException(Throwable cause) {
		super(cause);
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.attribute.handler;

import java.util.Date;

import org.mrs.customdatatype.CustomDatatypeHandler;
import org.mrs.customdatatype.datatype.DateDatatype;
import org.springframework.stereotype.Component;

@Component
public class DateDatatypeHandler implements CustomDatatypeHandler<DateDatatype, Date> {
	
	@Override
	public void setHandlerConfiguration(String handlerConfig) {
		
	}
}

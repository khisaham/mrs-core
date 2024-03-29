/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.customdatatype.datatype;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.mrs.api.context.Context;
import org.mrs.customdatatype.CustomDatatype;
import org.mrs.customdatatype.InvalidCustomValueException;
import org.mrs.customdatatype.SerializingCustomDatatype;
import org.springframework.stereotype.Component;

/**
 * Datatype for a Date (without time), represented by a java.util.Date. 
 * @since 1.9
 */
@Component
public class DateDatatype extends SerializingCustomDatatype<Date> {

	static final String dateFormat = "yyyy-MM-dd";
	
	/**
	 * @see org.mrs.customdatatype.SerializingCustomDatatype#doGetTextSummary(java.lang.Object)
	 */
	@Override
	public CustomDatatype.Summary doGetTextSummary(Date typedValue) {
		return new CustomDatatype.Summary(Context.getDateFormat().format(typedValue), true);
	}
	
	/**
	 * @see org.mrs.customdatatype.SerializingCustomDatatype#deserialize(java.lang.String)
	 * @should reconstruct a date serialized by this handler
	 */
	@Override
	public Date deserialize(String serializedValue) {
		if (StringUtils.isBlank(serializedValue)) {
			return null;
		}
		try {
			return new SimpleDateFormat(dateFormat).parse(serializedValue);
		}
		catch (Exception ex) {
			throw new InvalidCustomValueException("Invalid date: " + serializedValue);
		}
	}
	
	/**
	 * @see org.mrs.customdatatype.SerializingCustomDatatype#serialize(java.lang.Object)
	 * @should convert a date into a ymd string representation
	 */
	@Override
	public String serialize(Date typedValue) {
		return new SimpleDateFormat(dateFormat).format(typedValue);
	}
	
}

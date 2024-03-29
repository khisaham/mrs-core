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

import org.junit.Assert;
import org.junit.Test;
import org.mrs.Location;
import org.mrs.OpenmrsObject;

public class BaseOpenmrsDatatypeTest {
	
	/**
	 * @see BaseOpenmrsDatatype#serialize(org.mrs.OpenmrsObject)
	 */
	@Test
	public void serialize_shouldReturnTheUuidOfTheObject() {
		OpenmrsObject location = new Location();
		String expectedUuid = "some uuid";
		location.setUuid(expectedUuid);
		BaseOpenmrsDatatype datatype = new MockLocationDatatype();
		Assert.assertEquals(expectedUuid, datatype.serialize(location));
	}
}

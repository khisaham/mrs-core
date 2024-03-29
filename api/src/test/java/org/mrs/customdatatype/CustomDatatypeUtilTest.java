/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.customdatatype;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mrs.test.BaseContextSensitiveTest;

public class CustomDatatypeUtilTest extends BaseContextSensitiveTest {
	
	/**
	 * @see CustomDatatypeUtil#deserializeSimpleConfiguration(String)
	 */
	@Test
	public void deserializeSimpleConfiguration_shouldDeserializeAConfigurationSerializedByTheCorrespondingSerializeMethod() {
		Map<String, String> config = new HashMap<>();
		config.put("one property", "one value");
		config.put("another property", "another value < with > strange&nbsp;characters");
		
		String serialized = CustomDatatypeUtil.serializeSimpleConfiguration(config);
		Map<String, String> deserialized = CustomDatatypeUtil.deserializeSimpleConfiguration(serialized);
		Assert.assertEquals(2, deserialized.size());
		Assert.assertEquals("one value", deserialized.get("one property"));
		Assert.assertEquals("another value < with > strange&nbsp;characters", deserialized.get("another property"));
	}
}

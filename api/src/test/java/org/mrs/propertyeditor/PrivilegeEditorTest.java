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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mrs.test.BaseContextSensitiveTest;

public class PrivilegeEditorTest extends BaseContextSensitiveTest {
	
	protected static final String XML_FILENAME = "org/openmrs/api/include/UserServiceTest.xml";
	
	@Before
	public void prepareData() {
		executeDataSet(XML_FILENAME);
	}
	
	/**
	 * @see PrivilegeEditor#setAsText(String)
	 */
	@Test
	public void setAsText_shouldSetUsingName() {
		PrivilegeEditor editor = new PrivilegeEditor();
		editor.setAsText("Some Privilege");
		Assert.assertNotNull(editor.getValue());
	}
	
	/**
	 * @see PrivilegeEditor#setAsText(String)
	 */
	@Test
	public void setAsText_shouldSetUsingUuid() {
		PrivilegeEditor editor = new PrivilegeEditor();
		editor.setAsText("d979d066-15e6-467c-9d4b-cb575ef97f0f");
		Assert.assertNotNull(editor.getValue());
	}
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.api.db.hibernate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mrs.Field;
import org.mrs.FormField;
import org.mrs.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class HibernateFormDAOTest extends BaseContextSensitiveTest {
	
	private static final String FORM_XML = "org/openmrs/api/db/hibernate/include/HibernateFormDAOTestDataSet.xml";
	
	@Autowired
	private HibernateFormDAO dao;
	
	@Before
	public void setUp() {
		executeDataSet(FORM_XML);
	}
		
	@Test
	public void shouldFilterAgainstFormFields() {
		List<FormField> formFields = Arrays.asList(new FormField(2), new FormField(3));
		Assert.assertEquals(1, (Object)dao.getForms(null, false, Collections.emptyList(), null, formFields, formFields, Collections.emptyList()).size());
		
		formFields = Arrays.asList(new FormField(2), new FormField(3), new FormField(5));
		Assert.assertEquals(0, (Object)dao.getForms(null, false, Collections.emptyList(), null, formFields, formFields, Arrays.asList(new Field(3))).size());
		
	}
}

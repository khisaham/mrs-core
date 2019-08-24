/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.validator;

import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mrs.CareSetting;
import org.mrs.Concept;
import org.mrs.Encounter;
import org.mrs.Patient;
import org.mrs.Provider;
import org.mrs.TestOrder;
import org.mrs.api.ConceptService;
import org.mrs.api.OrderService;
import org.mrs.api.context.Context;
import org.mrs.test.BaseContextSensitiveTest;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 *
 */
public class TestOrderValidatorTest extends BaseContextSensitiveTest {
	
	/**
	 * @see TestOrderValidator#validate(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void validate_shouldFailValidationIfTheSpecimenSourceIsInvalid() {
		ConceptService conceptService = Context.getConceptService();
		Concept specimenSource = conceptService.getConcept(3);
		OrderService orderService = Context.getOrderService();
		assertThat(specimenSource, not(isIn(orderService.getDrugRoutes())));
		TestOrder order = new TestOrder();
		Patient patient = new Patient(8);
		order.setPatient(patient);
		order.setOrderType(orderService.getOrderTypeByName("Test order"));
		order.setConcept(conceptService.getConcept(5497));
		order.setOrderer(new Provider());
		order.setCareSetting(new CareSetting());
		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		order.setEncounter(encounter);
		order.setDateActivated(new Date());
		order.setSpecimenSource(specimenSource);
		
		Errors errors = new BindException(order, "order");
		new TestOrderValidator().validate(order, errors);
		Assert.assertTrue(errors.hasFieldErrors("specimenSource"));
		Assert.assertEquals("TestOrder.error.specimenSourceNotAmongAllowedConcepts", errors.getFieldError("specimenSource")
		        .getCode());
	}
	
	/**
	 * @see TestOrderValidator#validate(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void validate_shouldPassValidationIfTheSpecimenSourceIsValid() {
		ConceptService conceptService = Context.getConceptService();
		Concept specimenSource = conceptService.getConcept(22);
		OrderService orderService = Context.getOrderService();
		assertThat(specimenSource, isIn(orderService.getDrugRoutes()));
		TestOrder order = new TestOrder();
		Patient patient = new Patient(8);
		order.setPatient(patient);
		order.setOrderType(orderService.getOrderTypeByName("Test order"));
		order.setConcept(conceptService.getConcept(5497));
		order.setOrderer(Context.getProviderService().getProvider(1));
		order.setCareSetting(new CareSetting());
		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		order.setEncounter(encounter);
		order.setDateActivated(new Date());
		order.setSpecimenSource(specimenSource);
		
		Errors errors = new BindException(order, "order");
		new TestOrderValidator().validate(order, errors);
		Assert.assertFalse(errors.hasFieldErrors());
	}
}

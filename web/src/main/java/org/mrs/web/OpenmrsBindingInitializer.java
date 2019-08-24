/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.web;

import java.text.NumberFormat;
import java.util.Date;

import org.mrs.Cohort;
import org.mrs.Concept;
import org.mrs.ConceptAnswer;
import org.mrs.ConceptAttributeType;
import org.mrs.ConceptClass;
import org.mrs.ConceptDatatype;
import org.mrs.ConceptMapType;
import org.mrs.ConceptName;
import org.mrs.ConceptNumeric;
import org.mrs.ConceptReferenceTerm;
import org.mrs.ConceptSource;
import org.mrs.Drug;
import org.mrs.Encounter;
import org.mrs.Form;
import org.mrs.Location;
import org.mrs.LocationAttributeType;
import org.mrs.LocationTag;
import org.mrs.Order;
import org.mrs.Patient;
import org.mrs.PatientIdentifierType;
import org.mrs.Person;
import org.mrs.PersonAttribute;
import org.mrs.PersonAttributeType;
import org.mrs.Privilege;
import org.mrs.Program;
import org.mrs.ProgramWorkflow;
import org.mrs.ProgramWorkflowState;
import org.mrs.Provider;
import org.mrs.Role;
import org.mrs.User;
import org.mrs.Visit;
import org.mrs.VisitType;
import org.mrs.api.context.Context;
import org.mrs.propertyeditor.CohortEditor;
import org.mrs.propertyeditor.ConceptAnswerEditor;
import org.mrs.propertyeditor.ConceptAttributeTypeEditor;
import org.mrs.propertyeditor.ConceptClassEditor;
import org.mrs.propertyeditor.ConceptDatatypeEditor;
import org.mrs.propertyeditor.ConceptEditor;
import org.mrs.propertyeditor.ConceptMapTypeEditor;
import org.mrs.propertyeditor.ConceptNameEditor;
import org.mrs.propertyeditor.ConceptNumericEditor;
import org.mrs.propertyeditor.ConceptReferenceTermEditor;
import org.mrs.propertyeditor.ConceptSourceEditor;
import org.mrs.propertyeditor.DateOrDatetimeEditor;
import org.mrs.propertyeditor.DrugEditor;
import org.mrs.propertyeditor.EncounterEditor;
import org.mrs.propertyeditor.FormEditor;
import org.mrs.propertyeditor.LocationAttributeTypeEditor;
import org.mrs.propertyeditor.LocationEditor;
import org.mrs.propertyeditor.LocationTagEditor;
import org.mrs.propertyeditor.OrderEditor;
import org.mrs.propertyeditor.PatientEditor;
import org.mrs.propertyeditor.PatientIdentifierTypeEditor;
import org.mrs.propertyeditor.PersonAttributeEditor;
import org.mrs.propertyeditor.PersonAttributeTypeEditor;
import org.mrs.propertyeditor.PersonEditor;
import org.mrs.propertyeditor.PrivilegeEditor;
import org.mrs.propertyeditor.ProgramEditor;
import org.mrs.propertyeditor.ProgramWorkflowEditor;
import org.mrs.propertyeditor.ProgramWorkflowStateEditor;
import org.mrs.propertyeditor.ProviderEditor;
import org.mrs.propertyeditor.RoleEditor;
import org.mrs.propertyeditor.UserEditor;
import org.mrs.propertyeditor.VisitEditor;
import org.mrs.propertyeditor.VisitTypeEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

/**
 * Shared WebBindingInitializer that allows all OpenMRS annotated controllers to use our custom
 * editors.
 */
public class OpenmrsBindingInitializer implements WebBindingInitializer {
	
	/**
	 * @see org.springframework.web.bind.support.WebBindingInitializer#initBinder(org.springframework.web.bind.WebDataBinder,
	 *      org.springframework.web.context.request.WebRequest)
	 */
	@Override
	public void initBinder(WebDataBinder wdb, WebRequest request) {
		wdb.registerCustomEditor(Cohort.class, new CohortEditor());
		wdb.registerCustomEditor(Concept.class, new ConceptEditor());
		wdb.registerCustomEditor(ConceptAnswer.class, new ConceptAnswerEditor());
		wdb.registerCustomEditor(ConceptClass.class, new ConceptClassEditor());
		wdb.registerCustomEditor(ConceptDatatype.class, new ConceptDatatypeEditor());
		wdb.registerCustomEditor(ConceptName.class, new ConceptNameEditor());
		wdb.registerCustomEditor(ConceptNumeric.class, new ConceptNumericEditor());
		wdb.registerCustomEditor(ConceptSource.class, new ConceptSourceEditor());
		wdb.registerCustomEditor(Drug.class, new DrugEditor());
		wdb.registerCustomEditor(Encounter.class, new EncounterEditor());
		wdb.registerCustomEditor(Form.class, new FormEditor());
		wdb.registerCustomEditor(Location.class, new LocationEditor());
		wdb.registerCustomEditor(LocationTag.class, new LocationTagEditor());
		wdb.registerCustomEditor(LocationAttributeType.class, new LocationAttributeTypeEditor());
		wdb.registerCustomEditor(Order.class, new OrderEditor());
		wdb.registerCustomEditor(Patient.class, new PatientEditor());
		wdb.registerCustomEditor(PatientIdentifierType.class, new PatientIdentifierTypeEditor());
		wdb.registerCustomEditor(PersonAttribute.class, new PersonAttributeEditor());
		wdb.registerCustomEditor(PersonAttributeType.class, new PersonAttributeTypeEditor());
		wdb.registerCustomEditor(Person.class, new PersonEditor());
		wdb.registerCustomEditor(Privilege.class, new PrivilegeEditor());
		wdb.registerCustomEditor(Program.class, new ProgramEditor());
		wdb.registerCustomEditor(ProgramWorkflow.class, new ProgramWorkflowEditor());
		wdb.registerCustomEditor(ProgramWorkflowState.class, new ProgramWorkflowStateEditor());
		wdb.registerCustomEditor(Provider.class, new ProviderEditor());
		wdb.registerCustomEditor(Role.class, new RoleEditor());
		wdb.registerCustomEditor(User.class, new UserEditor());
		wdb.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, NumberFormat
		        .getInstance(Context.getLocale()), true));
		wdb.registerCustomEditor(Date.class, new DateOrDatetimeEditor());
		wdb.registerCustomEditor(PatientIdentifierType.class, new PatientIdentifierTypeEditor());
		wdb.registerCustomEditor(ConceptMapType.class, new ConceptMapTypeEditor());
		wdb.registerCustomEditor(ConceptSource.class, new ConceptSourceEditor());
		wdb.registerCustomEditor(ConceptReferenceTerm.class, new ConceptReferenceTermEditor());
		wdb.registerCustomEditor(ConceptAttributeType.class, new ConceptAttributeTypeEditor());
		wdb.registerCustomEditor(VisitType.class, new VisitTypeEditor());
		wdb.registerCustomEditor(Visit.class, new VisitEditor());
		
	}
	
}

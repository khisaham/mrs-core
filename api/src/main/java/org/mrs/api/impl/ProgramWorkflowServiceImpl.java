/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.api.impl;

import java.util.ArrayList;
import java.util.Set;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.mrs.Cohort;
import org.mrs.Concept;
import org.mrs.ConceptAnswer;
import org.mrs.ConceptStateConversion;
import org.mrs.Patient;
import org.mrs.PatientProgram;
import org.mrs.PatientProgramAttribute;
import org.mrs.PatientState;
import org.mrs.Program;
import org.mrs.ProgramAttributeType;
import org.mrs.ProgramWorkflow;
import org.mrs.ProgramWorkflowState;
import org.mrs.api.APIException;
import org.mrs.api.ProgramNameDuplicatedException;
import org.mrs.api.ProgramWorkflowService;
import org.mrs.api.context.Context;
import org.mrs.api.db.ProgramWorkflowDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the ProgramWorkflow-related services class. This method should not be
 * invoked by itself. Spring injection is used to inject this implementation into the
 * ServiceContext. Which implementation is injected is determined by the spring application context
 * file: /metadata/api/spring/applicationContext.xml
 *
 * @see org.mrs.api.ProgramWorkflowService
 */
@Transactional
public class ProgramWorkflowServiceImpl extends BaseOpenmrsService implements ProgramWorkflowService {
	
	private static final Logger log = LoggerFactory.getLogger(ProgramWorkflowServiceImpl.class);
	
	protected ProgramWorkflowDAO dao;
        
	public ProgramWorkflowServiceImpl() {
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#setProgramWorkflowDAO(org.mrs.api.db.ProgramWorkflowDAO)
	 */
	@Override
	public void setProgramWorkflowDAO(ProgramWorkflowDAO dao) {
		this.dao = dao;
	}
	
	// **************************
	// PROGRAM
	// **************************
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#saveProgram(org.mrs.Program)
	 */
	@Override
	public Program saveProgram(Program program) throws APIException {
		// Program
		if (program.getConcept() == null) {
			throw new APIException("Program.concept.required", (Object[]) null);
		}
		
		for (ProgramWorkflow workflow : program.getAllWorkflows()) {
			if (workflow.getConcept() == null) {
				throw new APIException("ProgramWorkflow.concept.required", (Object[]) null);
			}			
			ensureProgramIsSet(workflow, program);						
			for (ProgramWorkflowState state : workflow.getStates()) {
				if (state.getConcept() == null || state.getInitial() == null || state.getTerminal() == null) {
					throw new APIException("ProgramWorkflowState.requires", (Object[]) null);
				}				

				ensureProgramWorkflowIsSet(state, workflow);
			}
		}
		return dao.saveProgram(program);
	}
	 
	private void ensureProgramIsSet(ProgramWorkflow workflow, Program program) {		
		if (workflow.getProgram() == null) {
			workflow.setProgram(program);
		} else if (!workflow.getProgram().equals(program)) {
			throw new APIException("Program.error.contains.ProgramWorkflow", new Object[] { workflow.getProgram() });
		}
	}
	
	private void ensureProgramWorkflowIsSet(ProgramWorkflowState state, ProgramWorkflow workflow) {
		if (state.getProgramWorkflow() == null) {
			state.setProgramWorkflow(workflow);
		} else if (!state.getProgramWorkflow().equals(workflow)) {
			throw new APIException("ProgramWorkflow.error.contains.state", new Object[] { workflow.getProgram() });
		}
		
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getProgram(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public Program getProgram(Integer id) {
		return dao.getProgram(id);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getProgram(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public Program getProgram(String name) {
		return Context.getProgramWorkflowService().getProgramByName(name);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getProgram(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Program getProgramByName(String name) throws APIException {
		List<Program> programs = dao.getProgramsByName(name, false);
		
		if (programs.isEmpty()) {
			programs = dao.getProgramsByName(name, true);
		}
		
		//Must be unique not retired or unique retired
		if (programs.size() > 1) {
			throw new ProgramNameDuplicatedException(name);
		}
		return programs.isEmpty() ? null : programs.get(0);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getAllPrograms()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Program> getAllPrograms() throws APIException {
		return Context.getProgramWorkflowService().getAllPrograms(true);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getAllPrograms(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Program> getAllPrograms(boolean includeRetired) throws APIException {
		return dao.getAllPrograms(includeRetired);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getPrograms(String)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Program> getPrograms(String nameFragment) throws APIException {
		return dao.findPrograms(nameFragment);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#purgeProgram(org.mrs.Program)
	 */
	@Override
	public void purgeProgram(Program program) throws APIException {
		Context.getProgramWorkflowService().purgeProgram(program, false);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#purgeProgram(org.mrs.Program, boolean)
	 */
	@Override
	public void purgeProgram(Program program, boolean cascade) throws APIException {
		if (cascade && !program.getAllWorkflows().isEmpty()) {
			throw new APIException("Program.cascade.purging.not.implemented", (Object[]) null);
		}
		for (PatientProgram patientProgram : Context.getProgramWorkflowService().getPatientPrograms(null, program, null,
		    null, null, null, true)) {
			purgePatientProgram(patientProgram);
		}
		dao.deleteProgram(program);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#retireProgram(org.mrs.Program)
	 */
	@Override
	public Program retireProgram(Program program, String reason) throws APIException {
		//program.setRetired(true); - Note the BaseRetireHandler aspect is already setting the retired flag and reason
		for (ProgramWorkflow workflow : program.getWorkflows()) {
			workflow.setRetired(true);
			for (ProgramWorkflowState state : workflow.getStates()) {
				state.setRetired(true);
			}
		}
		return saveProgram(program);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#retireProgram(org.mrs.Program)
	 */
	@Override
	public Program unretireProgram(Program program) throws APIException {
		Date lastModifiedDate = program.getDateChanged();
		program.setRetired(false);
		for (ProgramWorkflow workflow : program.getAllWorkflows()) {
			if (lastModifiedDate != null && lastModifiedDate.equals(workflow.getDateChanged())) {
				workflow.setRetired(false);
				for (ProgramWorkflowState state : workflow.getStates()) {
					if (lastModifiedDate.equals(state.getDateChanged())) {
						state.setRetired(false);
					}
				}
			}
		}
		return saveProgram(program);
	}
	
	// **************************
	// PATIENT PROGRAM 
	// **************************
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#savePatientProgram(org.mrs.PatientProgram)
	 */
	@Override
	public PatientProgram savePatientProgram(PatientProgram patientProgram) throws APIException {
		
		if (patientProgram.getPatient() == null || patientProgram.getProgram() == null) {
			throw new APIException("PatientProgram.requires", (Object[]) null);
		}
		
		// Patient State
		for (PatientState state : patientProgram.getStates()) {
			if (state.getState() == null) {
				throw new APIException("PatientState.requires", (Object[]) null);
			}
			if (state.getPatientProgram() == null) {
				state.setPatientProgram(patientProgram);
			} else if (!state.getPatientProgram().equals(patientProgram)) {
				throw new APIException("PatientProgram.already.assigned", new Object[] { state.getPatientProgram() });
			}
			if (patientProgram.getVoided() || state.getVoided()) {
				state.setVoided(true);
				if (state.getVoidReason() == null && patientProgram.getVoidReason() != null) {
					state.setVoidReason(patientProgram.getVoidReason());
				}
			}
		}
		// Makes sure that the end dates of most recent states in each workflow
		// and the program end date are consistent
		if (patientProgram.getDateCompleted() != null) {
			for (PatientState state : patientProgram.getMostRecentStateInEachWorkflow()) {
				// The EndDate of active states only should be updated
				if (state.getEndDate() == null) {
					state.setEndDate(patientProgram.getDateCompleted());
				}
			}
		}

		return dao.savePatientProgram(patientProgram);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getPatientProgram(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public PatientProgram getPatientProgram(Integer patientProgramId) {
		return dao.getPatientProgram(patientProgramId);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getPatientPrograms(Patient, Program, Date, Date,
	 *      Date, Date, boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PatientProgram> getPatientPrograms(Patient patient, Program program, Date minEnrollmentDate,
	        Date maxEnrollmentDate, Date minCompletionDate, Date maxCompletionDate, boolean includeVoided)
	                throws APIException {
		return dao.getPatientPrograms(patient, program, minEnrollmentDate, maxEnrollmentDate, minCompletionDate,
		    maxCompletionDate, includeVoided);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getPatientPrograms(Cohort, Collection)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PatientProgram> getPatientPrograms(Cohort cohort, Collection<Program> programs) {
		if (cohort.getMemberIds().isEmpty()) {
			return dao.getPatientPrograms(null, programs);
		} else {
			return dao.getPatientPrograms(cohort, programs);
		}
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#purgePatientProgram(org.mrs.PatientProgram)
	 */
	@Override
	public void purgePatientProgram(PatientProgram patientProgram) throws APIException {
		Context.getProgramWorkflowService().purgePatientProgram(patientProgram, false);
		
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#purgePatientProgram(org.mrs.PatientProgram,
	 *      boolean)
	 */
	@Override
	public void purgePatientProgram(PatientProgram patientProgram, boolean cascade) throws APIException {
		if (cascade && !patientProgram.getStates().isEmpty()) {
			throw new APIException("PatientProgram.cascade.purging.not.implemented", (Object[]) null);
		}
		dao.deletePatientProgram(patientProgram);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#voidPatientProgram(org.mrs.PatientProgram,
	 *      java.lang.String)
	 */
	@Override
	public PatientProgram voidPatientProgram(PatientProgram patientProgram, String reason) {
		patientProgram.setVoided(true);
		patientProgram.setVoidReason(reason);
		return Context.getProgramWorkflowService().savePatientProgram(patientProgram); // The savePatientProgram method handles all of the voiding defaults and cascades
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#voidPatientProgram(org.mrs.PatientProgram,
	 *      java.lang.String)
	 */
	@Override
	public PatientProgram unvoidPatientProgram(PatientProgram patientProgram) {
		Date voidDate = patientProgram.getDateVoided();
		patientProgram.setVoided(false);
		for (PatientState state : patientProgram.getStates()) {
			if (voidDate != null && voidDate.equals(state.getDateVoided())) {
				state.setVoided(false);
				state.setVoidedBy(null);
				state.setDateVoided(null);
				state.setVoidReason(null);
			}
		}
		return Context.getProgramWorkflowService().savePatientProgram(patientProgram); // The savePatientProgram method handles all of the unvoiding defaults
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getPossibleOutcomes(Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Concept> getPossibleOutcomes(Integer programId) {
		List<Concept> possibleOutcomes = new ArrayList<>();
		Program program = Context.getProgramWorkflowService().getProgram(programId);
		if (program == null) {
			return possibleOutcomes;
		}
		Concept outcomesConcept = program.getOutcomesConcept();
		if (outcomesConcept == null) {
			return possibleOutcomes;
		}
		if (!outcomesConcept.getAnswers().isEmpty()) {
			for (ConceptAnswer conceptAnswer : outcomesConcept.getAnswers()) {
				possibleOutcomes.add(conceptAnswer.getAnswerConcept());
			}
			return possibleOutcomes;
		}
		if (!outcomesConcept.getSetMembers().isEmpty()) {
			return outcomesConcept.getSetMembers();
		}
		return possibleOutcomes;
	}
	
	// **************************
	// CONCEPT STATE CONVERSION 
	// **************************
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#saveConceptStateConversion(org.mrs.ConceptStateConversion)
	 */
	@Override
	public ConceptStateConversion saveConceptStateConversion(ConceptStateConversion csc) throws APIException {
		if (csc.getConcept() == null || csc.getProgramWorkflow() == null || csc.getProgramWorkflowState() == null) {
			throw new APIException("ConceptStateConversion.requires", (Object[]) null);
		}
		return dao.saveConceptStateConversion(csc);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getConceptStateConversion(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public ConceptStateConversion getConceptStateConversion(Integer id) {
		return dao.getConceptStateConversion(id);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getAllConceptStateConversions()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ConceptStateConversion> getAllConceptStateConversions() throws APIException {
		return dao.getAllConceptStateConversions();
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#purgeConceptStateConversion(org.mrs.ConceptStateConversion)
	 */
	@Override
	public void purgeConceptStateConversion(ConceptStateConversion conceptStateConversion) throws APIException {
		Context.getProgramWorkflowService().purgeConceptStateConversion(conceptStateConversion, false);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#purgeConceptStateConversion(org.mrs.ConceptStateConversion,
	 *      boolean)
	 */
	@Override
	public void purgeConceptStateConversion(ConceptStateConversion conceptStateConversion, boolean cascade)
	        throws APIException {
		dao.deleteConceptStateConversion(conceptStateConversion);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#triggerStateConversion(org.mrs.Patient,
	 *      org.mrs.Concept, java.util.Date)
	 */
	public void triggerStateConversion(Patient patient, Concept trigger, Date dateConverted) {
		
		// Check input parameters
		if (patient == null) {
			throw new APIException("convert.state.invalid.patient", (Object[]) null);
		}
		if (trigger == null) {
			throw new APIException("convert.state.patient.without.valid.trigger", (Object[]) null);
		}
		if (dateConverted == null) {
			throw new APIException("convert.state.invalid.date", (Object[]) null);
		}
		
		for (PatientProgram patientProgram : getPatientPrograms(patient, null, null, null, null, null, false)) {
			//skip past patient programs that already completed
			if (patientProgram.getDateCompleted() == null) {
				Set<ProgramWorkflow> workflows = patientProgram.getProgram().getWorkflows();
				for (ProgramWorkflow workflow : workflows) {
					// (getWorkflows() is only returning over nonretired workflows)
					PatientState patientState = patientProgram.getCurrentState(workflow);
					
					// #1080 cannot exit patient from care  
					// Should allow a transition from a null state to a terminal state
					// Or we should require a user to ALWAYS add an initial workflow/state when a patient is added to a program
					ProgramWorkflowState currentState = (patientState != null) ? patientState.getState() : null;
					ProgramWorkflowState transitionState = workflow.getState(trigger);
					
					log.debug("Transitioning from current state [" + currentState + "]");
					log.debug("|---> Transitioning to final state [" + transitionState + "]");
					
					if (transitionState != null && workflow.isLegalTransition(currentState, transitionState)) {
						patientProgram.transitionToState(transitionState, dateConverted);
						log.debug("State Conversion Triggered: patientProgram=" + patientProgram + " transition from "
						        + currentState + " to " + transitionState + " on " + dateConverted);
					}
				}
				
				// #1068 - Exiting a patient from care causes "not-null property references
				// a null or transient value: org.mrs.PatientState.dateCreated". Explicitly
				// calling the savePatientProgram() method will populate the metadata properties.
				// 
				// #1067 - We should explicitly save the patient program rather than let 
				// Hibernate do so when it flushes the session.
				Context.getProgramWorkflowService().savePatientProgram(patientProgram);
			}
		}
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getConceptStateConversion(org.mrs.ProgramWorkflow,
	 *      org.mrs.Concept)
	 */
	@Override
	@Transactional(readOnly = true)
	public ConceptStateConversion getConceptStateConversion(ProgramWorkflow workflow, Concept trigger) {
		return dao.getConceptStateConversion(workflow, trigger);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getProgramsByConcept(org.mrs.Concept)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Program> getProgramsByConcept(Concept concept) {
		return dao.getProgramsByConcept(concept);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getProgramWorkflowsByConcept(org.mrs.Concept)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProgramWorkflow> getProgramWorkflowsByConcept(Concept concept) {
		return dao.getProgramWorkflowsByConcept(concept);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getProgramWorkflowStatesByConcept(org.mrs.Concept)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProgramWorkflowState> getProgramWorkflowStatesByConcept(Concept concept) {
		return dao.getProgramWorkflowStatesByConcept(concept);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getConceptStateConversionByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public ConceptStateConversion getConceptStateConversionByUuid(String uuid) {
		return dao.getConceptStateConversionByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getPatientProgramByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public PatientProgram getPatientProgramByUuid(String uuid) {
		return dao.getPatientProgramByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getProgramByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Program getProgramByUuid(String uuid) {
		return dao.getProgramByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getWorkflow(Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public ProgramWorkflowState getState(Integer stateId) {
		return dao.getState(stateId);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getStateByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public ProgramWorkflowState getStateByUuid(String uuid) {
		return dao.getStateByUuid(uuid);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PatientState getPatientStateByUuid(String uuid) {
		return dao.getPatientStateByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getWorkflow(Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public ProgramWorkflow getWorkflow(Integer workflowId) {
		return dao.getWorkflow(workflowId);
	}
	
	/**
	 * @see org.mrs.api.ProgramWorkflowService#getWorkflowByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public ProgramWorkflow getWorkflowByUuid(String uuid) {
		return dao.getWorkflowByUuid(uuid);
	}
        
        @Override
        public List<ProgramAttributeType> getAllProgramAttributeTypes() {
            return dao.getAllProgramAttributeTypes();
        }

        @Override
        public ProgramAttributeType getProgramAttributeType(Integer id) {
            return dao.getProgramAttributeType(id);
        }
        
        @Override
        public ProgramAttributeType getProgramAttributeTypeByUuid(String uuid) {
            return dao.getProgramAttributeTypeByUuid(uuid);
        }

        @Override
        public ProgramAttributeType saveProgramAttributeType(ProgramAttributeType type) {
            return dao.saveProgramAttributeType(type);
        }

        @Override
        public void purgeProgramAttributeType(ProgramAttributeType type) {
            dao.purgeProgramAttributeType(type);
        }

        @Override
        public PatientProgramAttribute getPatientProgramAttributeByUuid(String uuid) {
            return dao.getPatientProgramAttributeByUuid(uuid);
        }

        @Override
        public Map<Object, Object> getPatientProgramAttributeByAttributeName(List<Integer> patients, String attributeName){
            return dao.getPatientProgramAttributeByAttributeName(patients, attributeName);
        }
        @Override
        public List<PatientProgram> getPatientProgramByAttributeNameAndValue(String attributeName, String attributeValue) {
            return dao.getPatientProgramByAttributeNameAndValue(attributeName, attributeValue);
        }	
}

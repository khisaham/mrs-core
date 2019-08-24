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

import static org.mrs.util.DateUtil.truncateToSeconds;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.mrs.Cohort;
import org.mrs.CohortMembership;
import org.mrs.Patient;
import org.mrs.User;
import org.mrs.api.APIException;
import org.mrs.api.CohortService;
import org.mrs.api.context.Context;
import org.mrs.api.db.CohortDAO;
import org.mrs.util.OpenmrsUtil;
import org.mrs.util.PrivilegeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * API functions related to Cohorts
 */
@Transactional
public class CohortServiceImpl extends BaseOpenmrsService implements CohortService {
	
	private static final Logger log = LoggerFactory.getLogger(CohortServiceImpl.class);
	
	private CohortDAO dao;
	
	/**
	 * @see org.mrs.api.CohortService#setCohortDAO(org.mrs.api.db.CohortDAO)
	 */
	@Override
	public void setCohortDAO(CohortDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.mrs.api.CohortService#saveCohort(org.mrs.Cohort)
	 */
	@Override
	public Cohort saveCohort(Cohort cohort) throws APIException {
		if (cohort.getCohortId() == null) {
			Context.requirePrivilege(PrivilegeConstants.ADD_COHORTS);
		} else {
			Context.requirePrivilege(PrivilegeConstants.EDIT_COHORTS);
		}
		if (cohort.getName() == null) {
			throw new APIException("Cohort.save.nameRequired", (Object[]) null);
		}
		if (cohort.getDescription() == null) {
			throw new APIException("Cohort.save.descriptionRequired", (Object[]) null);
		}
		if (log.isInfoEnabled()) {
			log.info("Saving cohort " + cohort);
		}
		
		return dao.saveCohort(cohort);
	}
	
	/**
	 * @see org.mrs.api.CohortService#getCohort(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public Cohort getCohort(Integer id) {
		return dao.getCohort(id);
	}
	
	/**
	 * @see org.mrs.api.CohortService#voidCohort(org.mrs.Cohort, java.lang.String)
	 */
	@Override
	public Cohort voidCohort(Cohort cohort, String reason) {
		// other setters done by the save handlers
		return Context.getCohortService().saveCohort(cohort);
	}
	
	/**
	 * @see org.mrs.api.CohortService#getCohortByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Cohort getCohortByUuid(String uuid) {
		return dao.getCohortByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.CohortService#getCohortMembershipByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public CohortMembership getCohortMembershipByUuid(String uuid) {
		return dao.getCohortMembershipByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.CohortService#addPatientToCohort(org.mrs.Cohort,
	 *      org.mrs.Patient)
	 */
	@Override
	public Cohort addPatientToCohort(Cohort cohort, Patient patient) {
		if (!cohort.contains(patient.getPatientId())) {
			CohortMembership cohortMembership = new CohortMembership(patient.getPatientId());
			cohort.addMembership(cohortMembership);
			Context.getCohortService().saveCohort(cohort);
		}
		return cohort;
	}
	
	/**
	 * @see org.mrs.api.CohortService#removePatientFromCohort(org.mrs.Cohort,
	 *      org.mrs.Patient)
	 */
	@Override
	public Cohort removePatientFromCohort(Cohort cohort, Patient patient) {
		List<CohortMembership> memberships = getCohortMemberships(patient.getPatientId(), null, false);
		List<CohortMembership> toVoid = memberships.stream()
				.filter(m -> m.getCohort().equals(cohort))
				.collect(Collectors.toList());
		
		for (CohortMembership membership : toVoid) {
			Context.getCohortService().voidCohortMembership(membership, "removePatientFromCohort");
		}
		return cohort;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Cohort> getCohortsContainingPatient(Patient patient) {
		return getCohortsContainingPatientId(patient.getPatientId());
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Cohort> getCohortsContainingPatientId(Integer patientId) {
		return dao.getCohortsContainingPatientId(patientId, false, new Date());
	}
	
	/**
	 * @see org.mrs.api.CohortService#getCohorts(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Cohort> getCohorts(String nameFragment) throws APIException {
		return dao.getCohorts(nameFragment);
	}
	
	/**
	 * @see org.mrs.api.CohortService#getAllCohorts()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Cohort> getAllCohorts() throws APIException {
		return Context.getCohortService().getAllCohorts(false);
	}
	
	/**
	 * @see org.mrs.api.CohortService#getAllCohorts(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Cohort> getAllCohorts(boolean includeVoided) throws APIException {
		return dao.getAllCohorts(includeVoided);
	}
	
	/**
	 * @see org.mrs.api.CohortService#getCohortByName(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Cohort getCohortByName(String name) throws APIException {
		return dao.getCohort(name);
	}
	
	/**
	 * @see org.mrs.api.CohortService#getCohort(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Cohort getCohort(String name) throws APIException {
		return getCohortByName(name);
	}
	
	/**
	 * @see org.mrs.api.CohortService#purgeCohort(org.mrs.Cohort)
	 */
	@Override
	public Cohort purgeCohort(Cohort cohort) throws APIException {
		return dao.deleteCohort(cohort);
	}
	
	/**
	 * @see CohortService#purgeCohortMembership(CohortMembership)
	 */
	@Override
	public void purgeCohortMembership(CohortMembership cohortMembership) throws APIException {
		Cohort cohort = cohortMembership.getCohort();
		boolean removed = cohort.removeMembership(cohortMembership);
		if (removed) {
			Context.getCohortService().saveCohort(cohort);
		}
	}
	
	/**
	 * @see CohortService#voidCohortMembership(CohortMembership, String)
	 */
	@Override
	public CohortMembership voidCohortMembership(CohortMembership cohortMembership, String reason) {
		Context.getCohortService().saveCohort(cohortMembership.getCohort());
		return cohortMembership;
	}
	
	/**
	 * @see CohortService#endCohortMembership(CohortMembership, Date)
	 */
	@Override
	public CohortMembership endCohortMembership(CohortMembership cohortMembership, Date onDate) {
		cohortMembership.setEndDate(onDate == null ? new Date() : onDate);
		Context.getCohortService().saveCohort(cohortMembership.getCohort());
		return cohortMembership;
	}
	
	/**
	 * @see org.mrs.api.CohortService#notifyPatientVoided(org.mrs.Patient)
	 */
	@Override
	public void notifyPatientVoided(Patient patient) throws APIException {
		List<CohortMembership> memberships = Context.getCohortService()
				.getCohortMemberships(patient.getPatientId(), null, false);
		memberships.forEach(m -> {
			m.setVoided(patient.getVoided());
			m.setDateVoided(patient.getDateVoided());
			m.setVoidedBy(patient.getVoidedBy());
			m.setVoidReason(patient.getVoidReason());
			dao.saveCohortMembership(m);
		});
	}
	
	/**
	 * @see org.mrs.api.CohortService#notifyPatientUnvoided(Patient, User, Date)
	 */
	@Override
	public void notifyPatientUnvoided(Patient patient, User originallyVoidedBy, Date originalDateVoided) throws APIException {
		List<CohortMembership> memberships = getCohortMemberships(patient.getPatientId(), null, true);
		List<CohortMembership> toUnvoid = memberships.stream().filter(
						m -> m.getVoided()
								&& m.getVoidedBy().equals(originallyVoidedBy)
								&& OpenmrsUtil.compare(
										truncateToSeconds(m.getDateVoided()),
										truncateToSeconds(originalDateVoided)) == 0)
				.collect(Collectors.toList());
		
		for (CohortMembership member : toUnvoid) {
			member.setVoided(false);
			member.setDateVoided(null);
			member.setVoidedBy(null);
			member.setVoidReason(null);
			dao.saveCohortMembership(member);
		}
	}
	
	@Override
	public List<CohortMembership> getCohortMemberships(Integer patientId, Date activeOnDate, boolean includeVoided) {
		if (patientId == null) {
			throw new IllegalArgumentException("patientId is required");
		}
		return dao.getCohortMemberships(patientId, activeOnDate, includeVoided);
	}
}

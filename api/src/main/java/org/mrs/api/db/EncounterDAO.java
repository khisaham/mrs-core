/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.api.db;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mrs.Cohort;
import org.mrs.Encounter;
import org.mrs.EncounterRole;
import org.mrs.EncounterType;
import org.mrs.Location;
import org.mrs.Patient;
import org.mrs.Visit;
import org.mrs.api.EncounterService;
import org.mrs.parameter.EncounterSearchCriteria;

/**
 * Encounter-related database functions
 */
public interface EncounterDAO {
	
	/**
	 * Saves an encounter
	 * 
	 * @param encounter to be saved
	 * @throws DAOException
	 */
	public Encounter saveEncounter(Encounter encounter) throws DAOException;
	
	/**
	 * Purge an encounter from database.
	 * 
	 * @param encounter encounter object to be purged
	 */
	public void deleteEncounter(Encounter encounter) throws DAOException;
	
	/**
	 * Get encounter by internal identifier
	 * 
	 * @param encounterId encounter id
	 * @return encounter with given internal identifier
	 * @throws DAOException
	 */
	public Encounter getEncounter(Integer encounterId) throws DAOException;
	
	/**
	 * @param patientId
	 * @return all encounters for the given patient identifier
	 * @throws DAOException
	 */
	public List<Encounter> getEncountersByPatientId(Integer patientId) throws DAOException;
	
	/**
	 * @see org.mrs.api.EncounterService#getEncounters(org.mrs.parameter.EncounterSearchCriteria)
	 */
	public List<Encounter> getEncounters(EncounterSearchCriteria encounterSearchCriteria);
	
	/**
	 * Save an Encounter Type
	 * 
	 * @param encounterType
	 */
	public EncounterType saveEncounterType(EncounterType encounterType);
	
	/**
	 * Purge encounter type from database.
	 * 
	 * @param encounterType
	 * @throws DAOException
	 */
	public void deleteEncounterType(EncounterType encounterType) throws DAOException;
	
	/**
	 * Get encounterType by internal identifier
	 * 
	 * @param encounterTypeId Internal Integer identifier for an EncounterType
	 * @return EncounterType with given internal identifier
	 * @throws DAOException
	 */
	public EncounterType getEncounterType(Integer encounterTypeId) throws DAOException;
	
	/**
	 * Get encounterType by name
	 * 
	 * @param name String representation of an encounterType
	 * @return EncounterType
	 * @throws DAOException
	 */
	public EncounterType getEncounterType(String name) throws DAOException;
	
	/**
	 * Get all encounter types
	 * 
	 * @return encounter types list
	 * @throws DAOException
	 */
	public List<EncounterType> getAllEncounterTypes(Boolean includeVoided) throws DAOException;
	
	/**
	 * Find Encounter Types matching the given name. Search string is case insensitive, so that
	 * "NaMe".equals("name") is true.
	 * 
	 * @param name
	 * @return all EncounterTypes that match
	 * @throws DAOException
	 */
	public List<EncounterType> findEncounterTypes(String name) throws DAOException;
	
	/**
	 * Gets the value of encounterDatetime currently saved in the database for the given encounter,
	 * bypassing any caches. This is used prior to saving an encounter so that we can change the obs
	 * if need be
	 * 
	 * @param encounter the Encounter go the the encounterDatetime of
	 * @return the encounterDatetime currently in the database for this encounter
	 * @should get saved encounter datetime from database
	 */
	public Date getSavedEncounterDatetime(Encounter encounter);
	
	/**
	 * Find {@link Encounter} matching a uuid
	 * 
	 * @param uuid
	 * @return {@link Encounter}
	 */
	public Encounter getEncounterByUuid(String uuid);
	
	/**
	 * Find {@link EncounterType} matching a uuid
	 * 
	 * @param uuid
	 * @return {@link EncounterType}
	 */
	public EncounterType getEncounterTypeByUuid(String uuid);
	
	/**
	 * Get a list of {@link Encounter} by Patient name or identifier based on batch settings
	 * 
	 * @param query patient name or identifier
	 * @param patientId the patient id
	 * @param start beginning index for the batch
	 * @param length number of encounters to return in the batch
	 * @param includeVoided Specifies whether voided encounters should be included
	 * @return list of {@link Encounter} based on batch settings
	 * @see EncounterService#getEncounters(String, Integer, Integer, boolean)
	 */
	List<Encounter> getEncounters(String query, Integer patientId, Integer start, Integer length, boolean includeVoided);
	
	/**
	 * Gets the location of the encounter
	 * 
	 * @param encounter to be retrieved from the database
	 * @return {@link Location}
	 */
	public Location getSavedEncounterLocation(Encounter encounter);
	
	/**
	 * @see EncounterService#getAllEncounters(Cohort)
	 */
	public Map<Integer, List<Encounter>> getAllEncounters(Cohort patients);
	
	/**
	 * Return the number of encounters matching a patient name or patient identifier
	 * 
	 * @param query patient name or identifier
	 * @param patientId the patient id
	 * @param includeVoided Specifies whether voided encounters should be included
	 * @return the number of encounters matching the given search phrase
	 * @see EncounterService#getCountOfEncounters(String, boolean)
	 */
	public Long getCountOfEncounters(String query, Integer patientId, boolean includeVoided);
	
	/**
	 * @see EncounterService#getEncountersByVisit(Visit, boolean)
	 */
	public List<Encounter> getEncountersByVisit(Visit visit, boolean includeVoided);
	
	/**
	 * Saves an encounter role
	 * 
	 * @param encounterRole role to be saved
	 * @throws org.mrs.api.db.DAOException
	 */
	public EncounterRole saveEncounterRole(EncounterRole encounterRole) throws DAOException;
	
	/**
	 * Purge an encounter role from database.
	 * 
	 * @param encounterRole encounter role object to be purged
	 */
	public void deleteEncounterRole(EncounterRole encounterRole) throws DAOException;
	
	/**
	 * Get encounter role by internal identifier
	 * 
	 * @param encounterRoleId encounter role id
	 * @return encounter role with given internal identifier
	 * @throws org.mrs.api.db.DAOException
	 */
	public EncounterRole getEncounterRole(Integer encounterRoleId) throws DAOException;
	
	/**
	 * Find {@link org.mrs.EncounterRole} matching a uuid
	 * 
	 * @param uuid
	 * @return {@link org.mrs.EncounterRole}
	 */
	public EncounterRole getEncounterRoleByUuid(String uuid);
	
	/**
	 * Get all encounter roles and optionally specify whether to include retired encountered roles
	 * 
	 * @param includeRetired include retired
	 * @return enconter roles
	 * @throws org.mrs.api.db.DAOException
	 * @see org.mrs.api.EncounterService#getAllEncounterRoles(boolean)
	 */
	public List<EncounterRole> getAllEncounterRoles(boolean includeRetired) throws DAOException;
	
	/**
	 * @see org.mrs.api.EncounterService#getEncounterRoleByName(String name)
	 */
	public EncounterRole getEncounterRoleByName(String name) throws DAOException;
	
	/**
	 * @see EncounterService#getEncountersNotAssignedToAnyVisit(Patient)
	 */
	public List<Encounter> getEncountersNotAssignedToAnyVisit(Patient patient) throws DAOException;
	
	/**
	 * @see org.mrs.api.EncounterService#getEncountersByVisitsAndPatient(org.mrs.Patient, boolean, java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	List<Encounter> getEncountersByVisitsAndPatient(Patient patient, boolean includeVoided, String query, Integer start,
	        Integer length);
	
	/**
	 * @see EncounterService#getEncountersByVisitsAndPatientCount(Patient, boolean, String)
	 */
	Integer getEncountersByVisitsAndPatientCount(Patient patient, boolean includeVoided, String query);
	
	/**
	 * Get encounter roles by name
	 * 
	 * @param name encounter role name
	 * @return encounter roles
	 * @throws org.mrs.api.db.DAOException
	 * @see org.mrs.api.EncounterService#getEncounterRolesByName(String name)
	 */
	
	public List<EncounterRole> getEncounterRolesByName(String name) throws DAOException;
}

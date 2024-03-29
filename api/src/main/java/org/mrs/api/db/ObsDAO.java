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

import org.mrs.Concept;
import org.mrs.ConceptName;
import org.mrs.Encounter;
import org.mrs.Location;
import org.mrs.Obs;
import org.mrs.Person;
import org.mrs.api.ObsService;
import org.mrs.util.OpenmrsConstants.PERSON_TYPE;

/**
 * Observation-related database functions
 * 
 * @see org.mrs.api.ObsService
 */
public interface ObsDAO {
	
	/**
	 * @see org.mrs.api.ObsService#saveObs(org.mrs.Obs, String)
	 */
	public Obs saveObs(Obs obs) throws DAOException;
	
	/**
	 * @see org.mrs.api.ObsService#getObs(java.lang.Integer)
	 */
	public Obs getObs(Integer obsId) throws DAOException;
	
	/**
	 * @see org.mrs.api.ObsService#purgeObs(Obs)
	 */
	public void deleteObs(Obs obs) throws DAOException;
		
	/**
	 * @see org.mrs.api.ObsService#getObservations(java.util.List, java.util.List,
	 *      java.util.List, java.util.List, java.util.List, java.util.List, java.util.List,
	 *      java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, boolean,
	 *      java.lang.String)
	 */
	public List<Obs> getObservations(List<Person> whom, List<Encounter> encounters, List<Concept> questions,
	        List<Concept> answers, List<PERSON_TYPE> personTypes, List<Location> locations, List<String> sort,
	        Integer mostRecentN, Integer obsGroupId, Date fromDate, Date toDate, boolean includeVoidedObs,
	        String accessionNumber) throws DAOException;
	
	/**
	 * @see org.mrs.api.ObsService#getObservationCount(java.util.List, java.util.List,
	 *      java.util.List, java.util.List, java.util.List, java.util.List, java.lang.Integer,
	 *      java.util.Date, java.util.Date, boolean, java.lang.String)
	 * @see ObsService#getObservationCount(List, boolean)
	 */
	public Long getObservationCount(List<Person> whom, List<Encounter> encounters, List<Concept> questions,
	        List<Concept> answers, List<PERSON_TYPE> personTypes, List<Location> locations, Integer obsGroupId,
	        Date fromDate, Date toDate, List<ConceptName> valueCodedNameAnswers, boolean includeVoidedObs,
	        String accessionNumber) throws DAOException;
	
	/**
	 * @param uuid
	 * @return obs or null
	 */
	public Obs getObsByUuid(String uuid);

	/**
	 * @see org.mrs.api.ObsService#getRevisionObs(org.mrs.Obs)
	 * @param initialObs
	 * @return Obs or null
	 */
	public Obs getRevisionObs(Obs initialObs);
	
	/**
	 * Gets the value of status currently saved in the database for the given obs, bypassing any caches. This is used
	 * when the user updates an existing obs so we can determine whether to change its status or not.
	 * @param obs
	 * @return
	 * @since 2.1.0
	 */
	public Obs.Status getSavedStatus(Obs obs);
	
}

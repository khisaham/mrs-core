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
import java.util.Set;

import org.mrs.Person;
import org.mrs.PersonAddress;
import org.mrs.PersonAttribute;
import org.mrs.PersonAttributeType;
import org.mrs.PersonName;
import org.mrs.Relationship;
import org.mrs.RelationshipType;
import org.mrs.person.PersonMergeLog;
import org.mrs.util.OpenmrsConstants;

/**
 * Person-related database functions
 * <p>
 * This is used by the PersonService. This should not be used directly, but rather used through the
 * methods on the PersonService.
 * <p>
 * Use case: <code>
 *   PersonService ps = Context.getPersonService();
 *   ps....
 *   
 * </code>
 * 
 * @see org.mrs.api.PersonService
 * @see org.mrs.api.context.Context
 */
public interface PersonDAO {
	
	/**
	 * @see org.mrs.api.PersonService#getSimilarPeople(java.lang.String, java.lang.Integer,
	 *      java.lang.String)
	 */
	public Set<Person> getSimilarPeople(String name, Integer birthyear, String gender) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#getPeople(String, Boolean)
	 */
	public List<Person> getPeople(String searchPhrase, Boolean dead) throws DAOException;
	
	public List<Person> getPeople(String searchPhrase, Boolean dead, Boolean voided) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#savePersonAttributeType(org.mrs.PersonAttributeType)
	 */
	public PersonAttributeType savePersonAttributeType(PersonAttributeType type) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#purgePersonAttributeType(org.mrs.PersonAttributeType)
	 */
	public void deletePersonAttributeType(PersonAttributeType type) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#getPersonAttributeTypes(java.lang.String,
	 *      java.lang.String, java.lang.Integer, java.lang.Boolean)
	 */
	public List<PersonAttributeType> getPersonAttributeTypes(String exactName, String format, Integer foreignKey,
	        Boolean searchable) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#getAllPersonAttributeTypes()
	 * @see org.mrs.api.PersonService#getAllPersonAttributeTypes(boolean)
	 */
	public List<PersonAttributeType> getAllPersonAttributeTypes(boolean includeRetired) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#getPersonAttributeType(java.lang.Integer)
	 */
	public PersonAttributeType getPersonAttributeType(Integer typeId) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#getPersonAttribute(java.lang.Integer)
	 */
	public PersonAttribute getPersonAttribute(Integer id) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#getRelationship(java.lang.Integer)
	 */
	public Relationship getRelationship(Integer relationshipId) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#getAllRelationships(boolean)
	 */
	public List<Relationship> getAllRelationships(boolean includeVoided) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#getRelationshipType(java.lang.Integer)
	 */
	public RelationshipType getRelationshipType(Integer relationshipTypeId) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#getRelationshipTypes(java.lang.String, java.lang.Boolean)
	 */
	public List<RelationshipType> getRelationshipTypes(String relationshipTypeName, Boolean preferred) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#savePerson(org.mrs.Person)
	 */
	public Person savePerson(Person person) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#deletePerson(org.mrs.Person)
	 */
	public void deletePerson(Person person) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#getPerson(java.lang.Integer)
	 */
	public Person getPerson(Integer personId) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#saveRelationship(org.mrs.Relationship)
	 */
	public Relationship saveRelationship(Relationship relationship) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#purgeRelationship(org.mrs.Relationship)
	 */
	public void deleteRelationship(Relationship relationship) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#getRelationships(org.mrs.Person, org.mrs.Person,
	 *      org.mrs.RelationshipType)
	 */
	public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType)
	        throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#getRelationships(org.mrs.Person, org.mrs.Person,
	 *      org.mrs.RelationshipType, java.util.Date, java.util.Date)
	 */
	public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType,
	        Date startEffectiveDate, Date endEffectiveDate) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#saveRelationshipType(org.mrs.RelationshipType)
	 */
	public RelationshipType saveRelationshipType(RelationshipType relationshipType) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#deleteRelationshipType(org.mrs.RelationshipType)
	 */
	public void deleteRelationshipType(RelationshipType relationshipType) throws DAOException;
	
	/**
	 * @param uuid
	 * @return person or null
	 */
	public Person getPersonByUuid(String uuid);
	
	public PersonAddress getPersonAddressByUuid(String uuid);
	
	public PersonAttribute getPersonAttributeByUuid(String uuid);
	
	public PersonName getPersonName(Integer personNameId);
	
	public PersonName getPersonNameByUuid(String uuid);
	
	/**
	 * @param uuid
	 * @return relationship or null
	 */
	public Relationship getRelationshipByUuid(String uuid);
	
	/**
	 * @param uuid
	 * @return relationship type or null
	 */
	public RelationshipType getRelationshipTypeByUuid(String uuid);
	
	/**
	 * @param uuid
	 * @return person attribute type or null
	 */
	public PersonAttributeType getPersonAttributeTypeByUuid(String uuid);
	
	/**
	 * Gets the value of name currently saved in the database for the given personAttributeType,
	 * bypassing any caches. This is used prior to saving an personAttributeType, so that we can
	 * change the vlaue of any global property which is in
	 * {@link OpenmrsConstants#GLOBAL_PROPERTIES_OF_PERSON_ATTRIBUTES} and reference the given
	 * personAttributeType. <br>
	 * 
	 * @param personAttributeType the personAttributeType get the the name of
	 * @return the name currently in the database for this personAttributeType
	 * @should get saved personAttributeType name from database
	 */
	public String getSavedPersonAttributeTypeName(PersonAttributeType personAttributeType);
	
	/**
	 * @see org.mrs.api.PersonService#getAllRelationshipTypes(boolean)
	 */
	public List<RelationshipType> getAllRelationshipTypes(boolean includeRetired);
	
	/**
	 * Saves a <code>PersonMergeLog</code> object to the database
	 * 
	 * @param personMergeLog the <code>PersonMergeLog</code> object to save
	 * @return the persisted <code>PersonMergeLog</code> object
	 */
	public PersonMergeLog savePersonMergeLog(PersonMergeLog personMergeLog) throws DAOException;
	
	/**
	 * Gets a <code>PersonMergeLog</code> object from the model by id
	 * 
	 * @param id the id of the <code>PersonMergeLog</code> object to retrieve
	 * @return the <code>PersonMergeLog</code> object
	 * @throws DAOException
	 */
	public PersonMergeLog getPersonMergeLog(Integer id) throws DAOException;
	
	/**
	 * Gets a PersonMergeLog object from the model using UUID
	 * 
	 * @param uuid the UUID of the PersonMergeLog object to retrieve
	 * @return the PersonMergeLog object
	 * @throws DAOException
	 */
	public PersonMergeLog getPersonMergeLogByUuid(String uuid) throws DAOException;
	
	/**
	 * Gets all the PersonMergeLog objects in the model
	 * 
	 * @return list of PersonMergeLog objects
	 * @throws DAOException
	 */
	public List<PersonMergeLog> getAllPersonMergeLogs() throws DAOException;
	
	/**
	 * Gets the PersonMergeLog objects by winner
	 * 
	 * @param person the winning person
	 * @return List of <code>PersonMergeLog</code> objects
	 * @throws DAOException
	 */
	public List<PersonMergeLog> getWinningPersonMergeLogs(Person person) throws DAOException;
	
	/**
	 * Finds the PersonMergeLog by loser
	 * 
	 * @param person
	 * @return <code>PersonMergeLog</code> object
	 * @throws DAOException
	 */
	public PersonMergeLog getLosingPersonMergeLogs(Person person) throws DAOException;
	
	/**
	 * @see org.mrs.api.PersonService#savePersonName(org.mrs.PersonName)
	 */
	public PersonName savePersonName(PersonName personName);
	
	/**
	 * @see org.mrs.api.PersonService#savePersonAddress(org.mrs.PersonAddress)
	 */
	public PersonAddress savePersonAddress(PersonAddress personAddress);
	
}

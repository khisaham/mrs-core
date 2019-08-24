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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.mrs.Person;
import org.mrs.PersonAddress;
import org.mrs.PersonAttribute;
import org.mrs.PersonAttributeType;
import org.mrs.PersonName;
import org.mrs.Relationship;
import org.mrs.RelationshipType;
import org.mrs.api.context.Context;
import org.mrs.api.db.DAOException;
import org.mrs.api.db.PersonDAO;
import org.mrs.api.db.hibernate.search.LuceneQuery;
import org.mrs.collection.ListPart;
import org.mrs.person.PersonMergeLog;
import org.mrs.util.OpenmrsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate specific Person database methods. <br>
 * <br>
 * This class should not be used directly. All database calls should go through the Service layer. <br>
 * <br>
 * Proper use: <code>
 *   PersonService ps = Context.getPersonService();
 *   ps.getPeople("name", false);
 * </code>
 * 
 * @see org.mrs.api.db.PersonDAO
 * @see org.mrs.api.PersonService
 * @see org.mrs.api.context.Context
 */
public class HibernatePersonDAO implements PersonDAO {
	
	private static final Logger log = LoggerFactory.getLogger(HibernatePersonDAO.class);
	
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.mrs.api.PersonService#getSimilarPeople(java.lang.String, java.lang.Integer,
	 *      java.lang.String, java.lang.String)
	 * @see org.mrs.api.db.PersonDAO#getSimilarPeople(String name, Integer birthyear, String
	 *      gender)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Set<Person> getSimilarPeople(String name, Integer birthyear, String gender) throws DAOException {
		if (birthyear == null) {
			birthyear = 0;
		}

		name = name.replaceAll("  ", " ");
		name = name.replace(", ", " ");
		String[] names = name.split(" ");
		
		StringBuilder q = new StringBuilder(
		        "select p from Person p left join p.names as pname where p.personVoided = false and pname.voided = false and ");
		
		if (names.length == 1) {
			q.append("(").append(" soundex(pname.givenName) = soundex(:n1)").append(
			    " or soundex(pname.middleName) = soundex(:n1)").append(" or soundex(pname.familyName) = soundex(:n1) ")
			        .append(" or soundex(pname.familyName2) = soundex(:n1) ").append(")");
		} else if (names.length == 2) {
			q.append("(").append(" case").append("  when pname.givenName is null then 1").append(
			    "  when pname.givenName = '' then 1").append("  when soundex(pname.givenName) = soundex(:n1) then 4")
			        .append("  when soundex(pname.givenName) = soundex(:n2) then 3").append("  else 0 ").append(" end")
			        .append(" + ").append(" case").append("  when pname.middleName is null then 1").append(
			            "  when pname.middleName = '' then 1").append(
			            "  when soundex(pname.middleName) = soundex(:n1) then 3").append(
			            "  when soundex(pname.middleName) = soundex(:n2) then 4").append("  else 0 ").append(" end").append(
			            " + ").append(" case").append("  when pname.familyName is null then 1").append(
			            "  when pname.familyName = '' then 1").append(
			            "  when soundex(pname.familyName) = soundex(:n1) then 3").append(
			            "  when soundex(pname.familyName) = soundex(:n2) then 4").append("  else 0 ").append(" end").append(
			            " +").append(" case").append("  when pname.familyName2 is null then 1").append(
			            "  when pname.familyName2 = '' then 1").append(
			            "  when soundex(pname.familyName2) = soundex(:n1) then 3").append(
			            "  when soundex(pname.familyName2) = soundex(:n2) then 4").append("  else 0 ").append(" end")
			        .append(") > 6");
		} else if (names.length == 3) {
			q.append("(").append(" case").append("  when pname.givenName is null then 0").append(
			    "  when soundex(pname.givenName) = soundex(:n1) then 3").append(
			    "  when soundex(pname.givenName) = soundex(:n2) then 2").append(
			    "  when soundex(pname.givenName) = soundex(:n3) then 1").append("  else 0 ").append(" end").append(" + ")
			        .append(" case").append("  when pname.middleName is null then 0").append(
			            "  when soundex(pname.middleName) = soundex(:n1) then 2").append(
			            "  when soundex(pname.middleName) = soundex(:n2) then 3").append(
			            "  when soundex(pname.middleName) = soundex(:n3) then 1").append("  else 0").append(" end").append(
			            " + ").append(" case").append("  when pname.familyName is null then 0").append(
			            "  when soundex(pname.familyName) = soundex(:n1) then 1").append(
			            "  when soundex(pname.familyName) = soundex(:n2) then 2").append(
			            "  when soundex(pname.familyName) = soundex(:n3) then 3").append("  else 0").append(" end").append(
			            " +").append(" case").append("  when pname.familyName2 is null then 0").append(
			            "  when soundex(pname.familyName2) = soundex(:n1) then 1").append(
			            "  when soundex(pname.familyName2) = soundex(:n2) then 2").append(
			            "  when soundex(pname.familyName2) = soundex(:n3) then 3").append("  else 0").append(" end").append(
			            ") >= 5");
		} else {
			
			// This is simply an alternative method of name matching which scales better
			// for large names, although it is hard to imagine getting names with more than
			// six or so tokens.  This can be easily updated to attain more desirable
			// results; it is just a working alternative to throwing an exception.
			
			q.append("(").append(" case").append("  when pname.givenName is null then 0");
			for (int i = 0; i < names.length; i++) {
				q.append("  when soundex(pname.givenName) = soundex(:n").append(i + 1).append(") then 1");
			}
			q.append("  else 0").append(" end").append(")").append("+").append("(").append(" case").append(
			    "  when pname.middleName is null then 0");
			for (int i = 0; i < names.length; i++) {
				q.append("  when soundex(pname.middleName) = soundex(:n").append(i + 1).append(") then 1");
			}
			q.append("  else 0").append(" end").append(")").append("+").append("(").append(" case").append(
			    "  when pname.familyName is null then 0");
			for (int i = 0; i < names.length; i++) {
				q.append("  when soundex(pname.familyName) = soundex(:n").append(i + 1).append(") then 1");
			}
			q.append("  else 0").append(" end").append(")").append("+").append("(").append(" case").append(
			    "  when pname.familyName2 is null then 0");
			for (int i = 0; i < names.length; i++) {
				q.append("  when soundex(pname.familyName2) = soundex(:n").append(i + 1).append(") then 1");
			}
			// if most of the names have at least a hit somewhere
			q.append("  else 0").append(" end").append(") >= ").append((int) (names.length * .75)); 
		}
		
		String birthdayMatch = " (year(p.birthdate) between " + (birthyear - 1) + " and " + (birthyear + 1)
		        + " or p.birthdate is null) ";
		
		String genderMatch = " (p.gender = :gender or p.gender = '') ";
		
		if (birthyear != 0 && gender != null) {
			q.append(" and (").append(birthdayMatch).append("and ").append(genderMatch).append(") ");
		} else if (birthyear != 0) {
			q.append(" and ").append(birthdayMatch);
		} else if (gender != null) {
			q.append(" and ").append(genderMatch);
		}
		
		q.append(" order by pname.givenName asc,").append(" pname.middleName asc,").append(" pname.familyName asc,").append(
		    " pname.familyName2 asc");
		
		String qStr = q.toString();
		Query query = sessionFactory.getCurrentSession().createQuery(qStr);
		
		for (int nameIndex = 0; nameIndex < names.length; nameIndex++) {
			query.setString("n" + (nameIndex + 1), names[nameIndex]);
		}
		
		if (qStr.contains(":gender")) {
			query.setString("gender", gender);
		}

		return new LinkedHashSet<Person>(query.list());
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#getPeople(java.lang.String, java.lang.Boolean)
	 * @should get no one by null
	 * @should get every one by empty string
	 * @should get no one by non-existing attribute
	 * @should get no one by non-searchable attribute
	 * @should get no one by voided attribute
	 * @should get one person by attribute
	 * @should get one person by random case attribute
	 * @should get one person by searching for a mix of attribute and voided attribute
	 * @should get multiple people by single attribute
	 * @should get multiple people by multiple attributes
	 * @should get no one by non-existing name
	 * @should get one person by name
	 * @should get one person by random case name
	 * @should get multiple people by single name
	 * @should get multiple people by multiple names
	 * @should get no one by non-existing name and non-existing attribute
	 * @should get no one by non-existing name and non-searchable attribute
	 * @should get no one by non-existing name and voided attribute
	 * @should get one person by name and attribute
	 * @should get one person by name and voided attribute
	 * @should get multiple people by name and attribute
	 * @should get one person by given name
	 * @should get multiple people by given name
	 * @should get one person by middle name
	 * @should get multiple people by middle name
	 * @should get one person by family name
	 * @should get multiple people by family name
	 * @should get one person by family name2
	 * @should get multiple people by family name2
	 * @should get one person by multiple name parts
	 * @should get multiple people by multiple name parts
	 * @should get no one by voided name
	 * @should not get voided person
	 * @should not get dead person
	 * @should get single dead person
	 * @should get multiple dead people
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Person> getPeople(String searchString, Boolean dead, Boolean voided) {
		if (searchString == null) {
			return new ArrayList<>();
		}

		int maxResults = HibernatePersonDAO.getMaximumSearchResults();

		boolean includeVoided = (voided != null) ? voided : false;

		if (StringUtils.isBlank(searchString)) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Person.class);
			if (dead != null) {
				criteria.add(Restrictions.eq("dead", dead));
			}

			if (!includeVoided) {
				criteria.add(Restrictions.eq("personVoided", false));
			}

			criteria.setMaxResults(maxResults);
			return criteria.list();
		}

		String query = LuceneQuery.escapeQuery(searchString);

		PersonLuceneQuery personLuceneQuery = new PersonLuceneQuery(sessionFactory);

		LuceneQuery<PersonName> nameQuery = personLuceneQuery.getPersonNameQueryWithOrParser(query, includeVoided);
		if (dead != null) {
			nameQuery.include("person.dead", dead);
		}

		List<Person> people = new ArrayList<>();

		ListPart<Object[]> names = nameQuery.listPartProjection(0, maxResults, "person.personId");
		names.getList().forEach(name -> people.add(getPerson((Integer) name[0])));

		LuceneQuery<PersonAttribute> attributeQuery = personLuceneQuery.getPersonAttributeQueryWithOrParser(query, includeVoided, nameQuery);
		ListPart<Object[]> attributes = attributeQuery.listPartProjection(0, maxResults, "person.personId");
		attributes.getList().forEach(attribute -> people.add(getPerson((Integer) attribute[0])));

		return people;
	}
	
	@Override
	public List<Person> getPeople(String searchString, Boolean dead) {
		return getPeople(searchString, dead, null);
	}
	
	/**
	 * Fetch the max results value from the global properties table
	 * 
	 * @return Integer value for the person search max results global property
	 */
	public static Integer getMaximumSearchResults() {
		try {
			return Integer.valueOf(Context.getAdministrationService().getGlobalProperty(
			    OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS,
			    String.valueOf(OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS_DEFAULT_VALUE)));
		}
		catch (Exception e) {
			log.warn("Unable to convert the global property " + OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS
			        + "to a valid integer. Returning the default "
			        + OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS_DEFAULT_VALUE);
		}
		
		return OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS_DEFAULT_VALUE;
	}
	
	/**
	 * @see org.mrs.api.PersonService#getPerson(java.lang.Integer)
	 * @see org.mrs.api.db.PersonDAO#getPerson(java.lang.Integer)
	 */
	@Override
	public Person getPerson(Integer personId) {
		return (Person) sessionFactory.getCurrentSession().get(Person.class, personId);
	}
	
	/**
	 * @see org.mrs.api.PersonService#deletePersonAttributeType(org.mrs.PersonAttributeType)
	 * @see org.mrs.api.db.PersonDAO#deletePersonAttributeType(org.mrs.PersonAttributeType)
	 */
	@Override
	public void deletePersonAttributeType(PersonAttributeType type) {
		sessionFactory.getCurrentSession().delete(type);
	}
	
	/**
	 * @see org.mrs.api.PersonService#savePersonAttributeType(org.mrs.PersonAttributeType)
	 * @see org.mrs.api.db.PersonDAO#savePersonAttributeType(org.mrs.PersonAttributeType)
	 */
	@Override
	public PersonAttributeType savePersonAttributeType(PersonAttributeType type) {
		sessionFactory.getCurrentSession().saveOrUpdate(type);
		return type;
	}
	
	/**
	 * @see org.mrs.api.PersonService#getPersonAttributeType(java.lang.Integer)
	 * @see org.mrs.api.db.PersonDAO#getPersonAttributeType(java.lang.Integer)
	 */
	@Override
	public PersonAttributeType getPersonAttributeType(Integer typeId) {
		return (PersonAttributeType) sessionFactory.getCurrentSession().get(PersonAttributeType.class, typeId);
	}
	
	/**
	 * @see org.mrs.api.PersonService#getPersonAttribute(java.lang.Integer)
	 * @see org.mrs.api.db.PersonDAO#getPersonAttribute(java.lang.Integer)
	 */
	@Override
	public PersonAttribute getPersonAttribute(Integer id) {
		return (PersonAttribute) sessionFactory.getCurrentSession().get(PersonAttribute.class, id);
	}
	
	/**
	 * @see org.mrs.api.PersonService#getAllPersonAttributeTypes(boolean)
	 * @see org.mrs.api.db.PersonDAO#getAllPersonAttributeTypes(boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PersonAttributeType> getAllPersonAttributeTypes(boolean includeRetired) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonAttributeType.class, "r");
		
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		
		criteria.addOrder(Order.asc("sortWeight"));
		
		return criteria.list();
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#getPersonAttributeTypes(java.lang.String, java.lang.String,
	 *      java.lang.Integer, java.lang.Boolean)
	 */
	@Override
	// TODO - PersonServiceTest fails here
	@SuppressWarnings("unchecked")
	public List<PersonAttributeType> getPersonAttributeTypes(String exactName, String format, Integer foreignKey,
	        Boolean searchable) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonAttributeType.class, "r");
		
		if (exactName != null) {
			criteria.add(Restrictions.eq("name", exactName));
		}
		
		if (format != null) {
			criteria.add(Restrictions.eq("format", format));
		}
		
		if (foreignKey != null) {
			criteria.add(Restrictions.eq("foreignKey", foreignKey));
		}
		
		if (searchable != null) {
			criteria.add(Restrictions.eq("searchable", searchable));
		}
		
		return criteria.list();
	}
	
	/**
	 * @see org.mrs.api.PersonService#getRelationship(java.lang.Integer)
	 * @see org.mrs.api.db.PersonDAO#getRelationship(java.lang.Integer)
	 */
	@Override
	public Relationship getRelationship(Integer relationshipId) throws DAOException {

		return (Relationship) sessionFactory.getCurrentSession()
		        .get(Relationship.class, relationshipId);
	}
	
	/**
	 * @see org.mrs.api.PersonService#getAllRelationships(boolean)
	 * @see org.mrs.api.db.PersonDAO#getAllRelationships(boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Relationship> getAllRelationships(boolean includeVoided) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Relationship.class, "r");
		
		if (!includeVoided) {
			criteria.add(Restrictions.eq("voided", false));
		}
		
		return criteria.list();
	}
	
	/**
	 * @see org.mrs.api.PersonService#getRelationships(org.mrs.Person, org.mrs.Person,
	 *      org.mrs.RelationshipType)
	 * @see org.mrs.api.db.PersonDAO#getRelationships(org.mrs.Person, org.mrs.Person,
	 *      org.mrs.RelationshipType)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Relationship.class, "r");
		
		if (fromPerson != null) {
			criteria.add(Restrictions.eq("personA", fromPerson));
		}
		if (toPerson != null) {
			criteria.add(Restrictions.eq("personB", toPerson));
		}
		if (relType != null) {
			criteria.add(Restrictions.eq("relationshipType", relType));
		}
		
		criteria.add(Restrictions.eq("voided", false));
		
		return criteria.list();
	}
	
	/**
	 * @see org.mrs.api.PersonService#getRelationships(org.mrs.Person, org.mrs.Person,
	 *      org.mrs.RelationshipType, java.util.Date, java.util.Date)
	 * @see org.mrs.api.db.PersonDAO#getRelationships(org.mrs.Person, org.mrs.Person,
	 *      org.mrs.RelationshipType, java.util.Date, java.util.Date)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType,
	        Date startEffectiveDate, Date endEffectiveDate) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Relationship.class, "r");
		
		if (fromPerson != null) {
			criteria.add(Restrictions.eq("personA", fromPerson));
		}
		if (toPerson != null) {
			criteria.add(Restrictions.eq("personB", toPerson));
		}
		if (relType != null) {
			criteria.add(Restrictions.eq("relationshipType", relType));
		}
		if (startEffectiveDate != null) {
			criteria.add(Restrictions.disjunction().add(
			    Restrictions.and(Restrictions.le("startDate", startEffectiveDate), Restrictions.ge("endDate",
			        startEffectiveDate))).add(
			    Restrictions.and(Restrictions.le("startDate", startEffectiveDate), Restrictions.isNull("endDate"))).add(
			    Restrictions.and(Restrictions.isNull("startDate"), Restrictions.ge("endDate", startEffectiveDate))).add(
			    Restrictions.and(Restrictions.isNull("startDate"), Restrictions.isNull("endDate"))));
		}
		if (endEffectiveDate != null) {
			criteria.add(Restrictions.disjunction().add(
			    Restrictions.and(Restrictions.le("startDate", endEffectiveDate), Restrictions
			            .ge("endDate", endEffectiveDate))).add(
			    Restrictions.and(Restrictions.le("startDate", endEffectiveDate), Restrictions.isNull("endDate"))).add(
			    Restrictions.and(Restrictions.isNull("startDate"), Restrictions.ge("endDate", endEffectiveDate))).add(
			    Restrictions.and(Restrictions.isNull("startDate"), Restrictions.isNull("endDate"))));
		}
		criteria.add(Restrictions.eq("voided", false));
		
		return criteria.list();
	}
	
	/**
	 * @see org.mrs.api.PersonService#getRelationshipType(java.lang.Integer)
	 * @see org.mrs.api.db.PersonDAO#getRelationshipType(java.lang.Integer)
	 */
	@Override
	public RelationshipType getRelationshipType(Integer relationshipTypeId) throws DAOException {

		return (RelationshipType) sessionFactory.getCurrentSession().get(
		    RelationshipType.class, relationshipTypeId);
	}
	
	/**
	 * @see org.mrs.api.PersonService#getRelationshipTypes(java.lang.String, java.lang.Boolean)
	 * @see org.mrs.api.db.PersonDAO#getRelationshipTypes(java.lang.String, java.lang.Boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RelationshipType> getRelationshipTypes(String relationshipTypeName, Boolean preferred) throws DAOException {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RelationshipType.class);
		criteria.add(Restrictions.sqlRestriction("CONCAT(a_Is_To_B, CONCAT('/', b_Is_To_A)) like (?)", relationshipTypeName,
		    new StringType()));
		
		if (preferred != null) {
			criteria.add(Restrictions.eq("preferred", preferred));
		}
		
		return criteria.list();
	}
	
	/**
	 * @see org.mrs.api.PersonService#saveRelationshipType(org.mrs.RelationshipType)
	 * @see org.mrs.api.db.PersonDAO#saveRelationshipType(org.mrs.RelationshipType)
	 */
	@Override
	public RelationshipType saveRelationshipType(RelationshipType relationshipType) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(relationshipType);
		return relationshipType;
	}
	
	/**
	 * @see org.mrs.api.PersonService#deleteRelationshipType(org.mrs.RelationshipType)
	 * @see org.mrs.api.db.PersonDAO#deleteRelationshipType(org.mrs.RelationshipType)
	 */
	@Override
	public void deleteRelationshipType(RelationshipType relationshipType) throws DAOException {
		sessionFactory.getCurrentSession().delete(relationshipType);
	}
	
	/**
	 * @see org.mrs.api.PersonService#purgePerson(org.mrs.Person)
	 * @see org.mrs.api.db.PersonDAO#deletePerson(org.mrs.Person)
	 */
	@Override
	public void deletePerson(Person person) throws DAOException {
		HibernatePersonDAO.deletePersonAndAttributes(sessionFactory, person);
	}
	
	/**
	 * @see org.mrs.api.PersonService#savePerson(org.mrs.Person)
	 * @see org.mrs.api.db.PersonDAO#savePerson(org.mrs.Person)
	 */
	@Override
	public Person savePerson(Person person) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(person);
		return person;
	}
	
	/**
	 * @see org.mrs.api.PersonService#saveRelationship(org.mrs.Relationship)
	 * @see org.mrs.api.db.PersonDAO#saveRelationship(org.mrs.Relationship)
	 */
	@Override
	public Relationship saveRelationship(Relationship relationship) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(relationship);
		return relationship;
	}
	
	/**
	 * @see org.mrs.api.PersonService#purgeRelationship(org.mrs.Relationship)
	 * @see org.mrs.api.db.PersonDAO#deleteRelationship(org.mrs.Relationship)
	 */
	@Override
	public void deleteRelationship(Relationship relationship) throws DAOException {
		sessionFactory.getCurrentSession().delete(relationship);
	}
	
	/**
	 * Used by deletePerson, deletePatient, and deleteUser to remove all properties of a person
	 * before deleting them.
	 * 
	 * @param sessionFactory the session factory from which to pull the current session
	 * @param person the person to delete
	 */
	public static void deletePersonAndAttributes(SessionFactory sessionFactory, Person person) {
		// delete properties and fields so hibernate can't complain
		for (PersonAddress address : person.getAddresses()) {
			if (address.getDateCreated() == null) {
				sessionFactory.getCurrentSession().evict(address);
			} else {
				sessionFactory.getCurrentSession().delete(address);
			}
		}
		person.setAddresses(null);
		
		for (PersonAttribute attribute : person.getAttributes()) {
			if (attribute.getDateCreated() == null) {
				sessionFactory.getCurrentSession().evict(attribute);
			} else {
				sessionFactory.getCurrentSession().delete(attribute);
			}
		}
		person.setAttributes(null);
		
		for (PersonName name : person.getNames()) {
			if (name.getDateCreated() == null) {
				sessionFactory.getCurrentSession().evict(name);
			} else {
				sessionFactory.getCurrentSession().delete(name);
			}
		}
		person.setNames(null);
		
		// finally, just tell hibernate to delete our object
		sessionFactory.getCurrentSession().delete(person);
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#getPersonAttributeTypeByUuid(java.lang.String)
	 */
	@Override
	public PersonAttributeType getPersonAttributeTypeByUuid(String uuid) {
		return (PersonAttributeType) sessionFactory.getCurrentSession().createQuery(
		    "from PersonAttributeType pat where pat.uuid = :uuid").setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#getSavedPersonAttributeTypeName(org.mrs.PersonAttributeType)
	 */
	@Override
	public String getSavedPersonAttributeTypeName(PersonAttributeType personAttributeType) {
		SQLQuery sql = sessionFactory.getCurrentSession().createSQLQuery(
		    "select name from person_attribute_type where person_attribute_type_id = :personAttributeTypeId");
		sql.setInteger("personAttributeTypeId", personAttributeType.getId());
		return (String) sql.uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#getPersonByUuid(java.lang.String)
	 */
	@Override
	public Person getPersonByUuid(String uuid) {
		return (Person) sessionFactory.getCurrentSession().createQuery("from Person p where p.uuid = :uuid").setString(
		    "uuid", uuid).uniqueResult();
	}
	
	@Override
	public PersonAddress getPersonAddressByUuid(String uuid) {
		return (PersonAddress) sessionFactory.getCurrentSession().createQuery("from PersonAddress p where p.uuid = :uuid")
		        .setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#savePersonMergeLog(PersonMergeLog)
	 */
	@Override
	public PersonMergeLog savePersonMergeLog(PersonMergeLog personMergeLog) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(personMergeLog);
		return personMergeLog;
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#getPersonMergeLog(java.lang.Integer)
	 */
	@Override
	public PersonMergeLog getPersonMergeLog(Integer id) throws DAOException {
		return (PersonMergeLog) sessionFactory.getCurrentSession().get(PersonMergeLog.class, id);
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#getPersonMergeLogByUuid(String)
	 */
	@Override
	public PersonMergeLog getPersonMergeLogByUuid(String uuid) throws DAOException {
		return (PersonMergeLog) sessionFactory.getCurrentSession().createQuery("from PersonMergeLog p where p.uuid = :uuid")
		        .setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#getWinningPersonMergeLogs(org.mrs.Person)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PersonMergeLog> getWinningPersonMergeLogs(Person person) throws DAOException {
		return (List<PersonMergeLog>) sessionFactory.getCurrentSession().createQuery(
		    "from PersonMergeLog p where p.winner.id = :winnerId").setInteger("winnerId", person.getId()).list();
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#getLosingPersonMergeLogs(org.mrs.Person)
	 */
	@Override
	public PersonMergeLog getLosingPersonMergeLogs(Person person) throws DAOException {
		return (PersonMergeLog) sessionFactory.getCurrentSession().createQuery(
		    "from PersonMergeLog p where p.loser.id = :loserId").setInteger("loserId", person.getId()).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#getAllPersonMergeLogs()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PersonMergeLog> getAllPersonMergeLogs() throws DAOException {
		return (List<PersonMergeLog>) sessionFactory.getCurrentSession().createQuery("from PersonMergeLog p").list();
	}
	
	@Override
	public PersonAttribute getPersonAttributeByUuid(String uuid) {
		return (PersonAttribute) sessionFactory.getCurrentSession().createQuery(
		    "from PersonAttribute p where p.uuid = :uuid").setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#getPersonName(Integer)
	 */
	@Override
	public PersonName getPersonName(Integer personNameId) {
		return (PersonName) sessionFactory.getCurrentSession().get(PersonName.class, personNameId);
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#getPersonNameByUuid(String)
	 */
	@Override
	public PersonName getPersonNameByUuid(String uuid) {
		return (PersonName) sessionFactory.getCurrentSession().createQuery("from PersonName p where p.uuid = :uuid")
		        .setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#getRelationshipByUuid(java.lang.String)
	 */
	@Override
	public Relationship getRelationshipByUuid(String uuid) {
		return (Relationship) sessionFactory.getCurrentSession().createQuery("from Relationship r where r.uuid = :uuid")
		        .setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#getRelationshipTypeByUuid(java.lang.String)
	 */
	@Override
	public RelationshipType getRelationshipTypeByUuid(String uuid) {
		return (RelationshipType) sessionFactory.getCurrentSession().createQuery(
		    "from RelationshipType rt where rt.uuid = :uuid").setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.PersonDAO#getAllRelationshipTypes(boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RelationshipType> getAllRelationshipTypes(boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RelationshipType.class);
		criteria.addOrder(Order.asc("weight"));
		
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		
		return criteria.list();
	}
	
	/**
	 * @see org.mrs.api.PersonService#savePersonName(org.mrs.PersonName)
	 * @see org.mrs.api.db.PersonDAO#savePersonName(org.mrs.PersonName)
	 */
	@Override
	public PersonName savePersonName(PersonName personName) {
		sessionFactory.getCurrentSession().saveOrUpdate(personName);
		return personName;
	}
	
	/**
	 * @see org.mrs.api.PersonService#savePersonAddress(org.mrs.PersonAddress)
	 * @see org.mrs.api.db.PersonDAO#savePersonAddress(org.mrs.PersonAddress)
	 */
	@Override
	public PersonAddress savePersonAddress(PersonAddress personAddress) {
		sessionFactory.getCurrentSession().saveOrUpdate(personAddress);
		return personAddress;
	}
	
}

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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.mrs.Concept;
import org.mrs.ConceptAnswer;
import org.mrs.ConceptAttribute;
import org.mrs.ConceptAttributeType;
import org.mrs.ConceptClass;
import org.mrs.ConceptComplex;
import org.mrs.ConceptDatatype;
import org.mrs.ConceptDescription;
import org.mrs.ConceptMap;
import org.mrs.ConceptMapType;
import org.mrs.ConceptName;
import org.mrs.ConceptNameTag;
import org.mrs.ConceptNumeric;
import org.mrs.ConceptProposal;
import org.mrs.ConceptReferenceTerm;
import org.mrs.ConceptReferenceTermMap;
import org.mrs.ConceptSearchResult;
import org.mrs.ConceptSet;
import org.mrs.ConceptSource;
import org.mrs.ConceptStopWord;
import org.mrs.Drug;
import org.mrs.DrugIngredient;
import org.mrs.api.APIException;
import org.mrs.api.ConceptService;

/**
 * Concept-related database functions
 * 
 * @see ConceptService
 */
public interface ConceptDAO {
	
	/**
	 * @see org.mrs.api.ConceptService#saveConcept(org.mrs.Concept)
	 */
	public Concept saveConcept(Concept concept) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#purgeConcept(org.mrs.Concept)
	 * @should purge concept
	 */
	public void purgeConcept(Concept concept) throws DAOException;
	
	/**
	 * Get a ConceptComplex. The Concept.getDatatype() is "Complex" and the Concept.getHandler() is
	 * the class name for the ComplexObsHandler key associated with this ConceptComplex.
	 * 
	 * @param conceptId
	 * @return the ConceptComplex
	 */
	public ConceptComplex getConceptComplex(Integer conceptId);
	
	/**
	 * @see org.mrs.api.ConceptService#purgeDrug(org.mrs.Drug)
	 */
	public void purgeDrug(Drug drug) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#saveDrug(org.mrs.Drug)
	 */
	public Drug saveDrug(Drug drug) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getConcept(java.lang.Integer)
	 */
	public Concept getConcept(Integer conceptId) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getConceptName(java.lang.Integer)
	 * @param conceptNameId
	 * @return The ConceptName matching the specified conceptNameId
	 */
	public ConceptName getConceptName(Integer conceptNameId) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getAllConcepts(java.lang.String, boolean, boolean)
	 */
	public List<Concept> getAllConcepts(String sortBy, boolean asc, boolean includeRetired) throws DAOException;
	
	/**
	 * Returns a list of concepts based on the search criteria
	 * 
	 * @param name
	 * @param loc
	 * @param searchOnPhrase This puts wildcard characters around the concept name search criteria
	 * @return List&lt;Concept&gt;
	 * @throws DAOException
	 * @should not return concepts with matching names that are voided
	 */
	public List<Concept> getConcepts(String name, Locale loc, boolean searchOnPhrase, List<ConceptClass> classes,
	        List<ConceptDatatype> datatypes) throws DAOException;
	
	/**
	 * @see ConceptService#getConcepts(String, List, boolean, List, List, List, List, Concept,
	 *      Integer, Integer)
	 * @throws DAOException
	 * @should return correct results for concept with names that contains words with more weight
	 * @should return correct results if a concept name contains same word more than once
	 */
	public List<ConceptSearchResult> getConcepts(String phrase, List<Locale> locales, boolean includeRetired,
	        List<ConceptClass> requireClasses, List<ConceptClass> excludeClasses, List<ConceptDatatype> requireDatatypes,
	        List<ConceptDatatype> excludeDatatypes, Concept answersToConcept, Integer start, Integer size)
	        throws DAOException;
	
	public Integer getCountOfConcepts(String phrase, List<Locale> locales, boolean includeRetired,
	        List<ConceptClass> requireClasses, List<ConceptClass> excludeClasses, List<ConceptDatatype> requireDatatypes,
	        List<ConceptDatatype> excludeDatatypes, Concept answersToConcept) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getConceptAnswer(java.lang.Integer)
	 */
	public ConceptAnswer getConceptAnswer(Integer conceptAnswerId) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getDrug(java.lang.Integer)
	 */
	public Drug getDrug(Integer drugId) throws DAOException;
	
	/**
	 * DAO for retrieving a list of drugs based on the following criteria
	 * 
	 * @param drugName
	 * @param concept
	 * @param includeRetired
	 * @return List&lt;Drug&gt;
	 * @throws DAOException
	 */
	public List<Drug> getDrugs(String drugName, Concept concept, boolean includeRetired) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getDrugs(java.lang.String)
	 */
	public List<Drug> getDrugs(String phrase) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getConceptClass(java.lang.Integer)
	 */
	public ConceptClass getConceptClass(Integer i) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getConceptClassByName(java.lang.String)
	 */
	public List<ConceptClass> getConceptClasses(String name) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getAllConceptClasses(boolean)
	 */
	public List<ConceptClass> getAllConceptClasses(boolean includeRetired) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#saveConceptClass(org.mrs.ConceptClass)
	 */
	public ConceptClass saveConceptClass(ConceptClass cc) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#purgeConceptClass(org.mrs.ConceptClass)
	 */
	public void purgeConceptClass(ConceptClass cc) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#purgeConceptNameTag(org.mrs.ConceptNameTag)
	 */
	public void deleteConceptNameTag(ConceptNameTag cnt) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getAllConceptDatatypes(boolean)
	 */
	public List<ConceptDatatype> getAllConceptDatatypes(boolean includeRetired) throws DAOException;
	
	/**
	 * @param name
	 * @return the {@link ConceptDatatype} that matches <em>name</em> exactly or null if one does
	 *         not exist.
	 * @throws DAOException
	 */
	public ConceptDatatype getConceptDatatypeByName(String name) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getConceptDatatype(java.lang.Integer)
	 */
	public ConceptDatatype getConceptDatatype(Integer i) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#saveConceptDatatype(org.mrs.ConceptDatatype)
	 */
	public ConceptDatatype saveConceptDatatype(ConceptDatatype cd) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#purgeConceptDatatype(org.mrs.ConceptDatatype)
	 */
	public void purgeConceptDatatype(ConceptDatatype cd) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getConceptSetsByConcept(org.mrs.Concept)
	 */
	public List<ConceptSet> getConceptSetsByConcept(Concept c) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getSetsContainingConcept(org.mrs.Concept)
	 */
	public List<ConceptSet> getSetsContainingConcept(Concept concept) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getConceptNumeric(java.lang.Integer)
	 */
	public ConceptNumeric getConceptNumeric(Integer conceptId) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getConceptsByAnswer(org.mrs.Concept)
	 * @should return concepts for the given answer concept
	 */
	public List<Concept> getConceptsByAnswer(Concept concept) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getPrevConcept(org.mrs.Concept)
	 */
	public Concept getPrevConcept(Concept c) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getNextConcept(org.mrs.Concept)
	 */
	public Concept getNextConcept(Concept c) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getAllConceptProposals(boolean)
	 */
	public List<ConceptProposal> getAllConceptProposals(boolean includeComplete) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getConceptProposal(java.lang.Integer)
	 */
	public ConceptProposal getConceptProposal(Integer i) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getConceptProposals(java.lang.String)
	 */
	public List<ConceptProposal> getConceptProposals(String text) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getProposedConcepts(java.lang.String)
	 */
	public List<Concept> getProposedConcepts(String text) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#saveConceptProposal(org.mrs.ConceptProposal)
	 */
	public ConceptProposal saveConceptProposal(ConceptProposal cp) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#purgeConceptProposal(org.mrs.ConceptProposal)
	 */
	public void purgeConceptProposal(ConceptProposal cp) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getConceptsWithDrugsInFormulary()
	 */
	public List<Concept> getConceptsWithDrugsInFormulary() throws DAOException;
	
	public ConceptNameTag saveConceptNameTag(ConceptNameTag nameTag);
	
	public ConceptNameTag getConceptNameTag(Integer i);
	
	public ConceptNameTag getConceptNameTagByName(String name);
	
	/**
	 * @see org.mrs.api.ConceptService#getAllConceptNameTags()
	 */
	public List<ConceptNameTag> getAllConceptNameTags();
	
	/**
	 * @see org.mrs.api.ConceptService#getConceptSource(java.lang.Integer)
	 */
	public ConceptSource getConceptSource(Integer conceptSourceId) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getAllConceptSources(boolean)
	 */
	public List<ConceptSource> getAllConceptSources(boolean includeRetired) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#saveConceptSource(org.mrs.ConceptSource)
	 */
	public ConceptSource saveConceptSource(ConceptSource conceptSource) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#purgeConceptSource(org.mrs.ConceptSource)
	 */
	public ConceptSource deleteConceptSource(ConceptSource cs) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getLocalesOfConceptNames()
	 */
	public Set<Locale> getLocalesOfConceptNames();
	
	/**
	 * @see ConceptService#getMaxConceptId()
	 */
	public Integer getMaxConceptId();
	
	/**
	 * @see org.mrs.api.ConceptService#conceptIterator()
	 */
	public Iterator<Concept> conceptIterator();
	
	/**
	 * @see org.mrs.api.ConceptService#getConceptsByMapping(java.lang.String, java.lang.String)
	 */
	public List<Concept> getConceptsByMapping(String code, String sourceName, boolean includeRetired);
	
	/**
	 * @param uuid
	 * @return concept or null
	 */
	public Concept getConceptByUuid(String uuid);
	
	/**
	 * @param uuid
	 * @return concept class or null
	 */
	public ConceptClass getConceptClassByUuid(String uuid);
	
	public ConceptAnswer getConceptAnswerByUuid(String uuid);
	
	public ConceptName getConceptNameByUuid(String uuid);
	
	public ConceptSet getConceptSetByUuid(String uuid);
	
	public ConceptSource getConceptSourceByUuid(String uuid);
	
	/**
	 * @param uuid
	 * @return concept data type or null
	 */
	public ConceptDatatype getConceptDatatypeByUuid(String uuid);
	
	/**
	 * @param uuid
	 * @return concept numeric or null
	 */
	public ConceptNumeric getConceptNumericByUuid(String uuid);
	
	/**
	 * @param uuid
	 * @return concept proposal or null
	 */
	public ConceptProposal getConceptProposalByUuid(String uuid);
	
	/**
	 * @param uuid
	 * @return drug or null
	 */
	public Drug getDrugByUuid(String uuid);
	
	public DrugIngredient getDrugIngredientByUuid(String uuid);
	
	public Map<Integer, String> getConceptUuids();
	
	public ConceptDescription getConceptDescriptionByUuid(String uuid);
	
	public ConceptNameTag getConceptNameTagByUuid(String uuid);
	
	/**
	 * @see ConceptService#getConceptMappingsToSource(ConceptSource)
	 */
	public List<ConceptMap> getConceptMapsBySource(ConceptSource conceptSource) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getConceptSourceByName(java.lang.String)
	 */
	public ConceptSource getConceptSourceByName(String conceptSourceName) throws DAOException;

	/**
	 * @see org.mrs.api.ConceptService#getConceptSourceByUniqueId(java.lang.String)
	 */
	public ConceptSource getConceptSourceByUniqueId(String uniqueId);

	/**
	 * @see org.mrs.api.ConceptService#getConceptSourceByHL7Code(java.lang.String)
	 */
	public ConceptSource getConceptSourceByHL7Code(String hl7Code);

	/**
	 * Gets the value of conceptDatatype currently saved in the database for the given concept,
	 * bypassing any caches. This is used prior to saving an concept so that we can change the obs
	 * if need be
	 * 
	 * @param concept for which the conceptDatatype should be fetched
	 * @return the conceptDatatype currently in the database for this concept
	 * @should get saved conceptDatatype from database
	 */
	public ConceptDatatype getSavedConceptDatatype(Concept concept);
	
	/**
	 * Gets the persisted copy of the conceptName currently saved in the database for the given
	 * conceptName, bypassing any caches. This is used prior to saving an concept so that we can
	 * change the obs if need be or avoid breaking any obs referencing it.
	 * 
	 * @param conceptName ConceptName to fetch from the database
	 * @return the persisted copy of the conceptName currently saved in the database for this
	 *         conceptName
	 */
	public ConceptName getSavedConceptName(ConceptName conceptName);
	
	/**
	 * @see org.mrs.api.ConceptService#saveConceptStopWord(org.mrs.ConceptStopWord)
	 */
	public ConceptStopWord saveConceptStopWord(ConceptStopWord conceptStopWord) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#deleteConceptStopWord(Integer)
	 */
	public void deleteConceptStopWord(Integer conceptStopWordId) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getConceptStopWords(java.util.Locale)
	 */
	public List<String> getConceptStopWords(Locale locale) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getAllConceptStopWords()
	 */
	public List<ConceptStopWord> getAllConceptStopWords();
	
	/**
	 * @see ConceptService#getCountOfDrugs(String, Concept, boolean, boolean, boolean)
	 */
	public Long getCountOfDrugs(String drugName, Concept concept, boolean searchOnPhrase, boolean searchDrugConceptNames,
	        boolean includeRetired) throws DAOException;
	
	/**
	 * @see ConceptService#getDrugs(String, Concept, boolean, boolean, boolean, Integer, Integer)
	 */
	public List<Drug> getDrugs(String drugName, Concept concept, boolean searchOnPhrase, boolean searchDrugConceptNames,
	        boolean includeRetired, Integer start, Integer length) throws DAOException;
	
	/**
	 * @see ConceptService#getDrugsByIngredient(Concept)
	 */
	public List<Drug> getDrugsByIngredient(Concept ingredient);
	
	/**
	 * @see ConceptService#getConceptMapTypes(boolean, boolean)
	 */
	public List<ConceptMapType> getConceptMapTypes(boolean includeRetired, boolean includeHidden) throws DAOException;
	
	/**
	 * @see ConceptService#getConceptMapType(Integer)
	 */
	public ConceptMapType getConceptMapType(Integer conceptMapTypeId) throws DAOException;
	
	/**
	 * @see ConceptService#getConceptMapTypeByUuid(String)
	 */
	public ConceptMapType getConceptMapTypeByUuid(String uuid) throws DAOException;
	
	/**
	 * @see ConceptService#getConceptMapTypeByName(String)
	 */
	public ConceptMapType getConceptMapTypeByName(String name) throws DAOException;
	
	/**
	 * @see ConceptService#saveConceptMapType(ConceptMapType)
	 */
	public ConceptMapType saveConceptMapType(ConceptMapType conceptMapType) throws DAOException;
	
	/**
	 * @see ConceptService#purgeConceptMapType(ConceptMapType)
	 */
	public void deleteConceptMapType(ConceptMapType conceptMapType) throws DAOException;
	
	/**
	 * @see ConceptService#getConceptReferenceTerms(boolean)
	 */
	public List<ConceptReferenceTerm> getConceptReferenceTerms(boolean includeRetired) throws DAOException;
	
	/**
	 * @see ConceptService#getConceptReferenceTerm(Integer)
	 */
	public ConceptReferenceTerm getConceptReferenceTerm(Integer conceptReferenceTermId) throws DAOException;
	
	/**
	 * @see ConceptService#getConceptReferenceTermByUuid(String)
	 */
	public ConceptReferenceTerm getConceptReferenceTermByUuid(String uuid) throws DAOException;
	
	public List<ConceptReferenceTerm> getConceptReferenceTermsBySource(ConceptSource conceptSource) throws DAOException;
	
	/**
	 * @see ConceptService#getConceptReferenceTermByName(String, ConceptSource)
	 */
	public ConceptReferenceTerm getConceptReferenceTermByName(String name, ConceptSource conceptSource) throws DAOException;
	
	/**
	 * @see ConceptService#getConceptReferenceTermByCode(String, ConceptSource)
	 */
	public ConceptReferenceTerm getConceptReferenceTermByCode(String code, ConceptSource conceptSource) throws DAOException;
	
	/**
	 * @see ConceptService#saveConceptReferenceTerm(ConceptReferenceTerm)
	 */
	public ConceptReferenceTerm saveConceptReferenceTerm(ConceptReferenceTerm conceptReferenceTerm) throws DAOException;
	
	/**
	 * @see ConceptService#purgeConceptReferenceTerm(ConceptReferenceTerm)
	 */
	public void deleteConceptReferenceTerm(ConceptReferenceTerm conceptReferenceTerm) throws DAOException;
	
	/**
	 * @see ConceptService#getCountOfConceptReferenceTerms(String, ConceptSource, boolean)
	 */
	public Long getCountOfConceptReferenceTerms(String query, ConceptSource conceptSource, boolean includeRetired)
	        throws DAOException;
	
	/**
	 * @see ConceptService#getConceptReferenceTerms(String, ConceptSource, Integer, Integer,
	 *      boolean)
	 */
	public List<ConceptReferenceTerm> getConceptReferenceTerms(String query, ConceptSource conceptSource, Integer start,
	        Integer length, boolean includeRetired) throws APIException;
	
	/**
	 * @see ConceptService#getReferenceTermMappingsTo(ConceptReferenceTerm)
	 */
	public List<ConceptReferenceTermMap> getReferenceTermMappingsTo(ConceptReferenceTerm term) throws DAOException;
	
	/**
	 * Checks if there are any {@link ConceptReferenceTermMap}s or {@link ConceptMap}s using the
	 * specified term
	 * 
	 * @param term
	 * @return true if term is in use
	 * @throws DAOException
	 * @should return true if a term has a conceptMap or more using it
	 * @should return true if a term has a conceptReferenceTermMap or more using it
	 * @should return false if a term has no maps using it
	 */
	public boolean isConceptReferenceTermInUse(ConceptReferenceTerm term) throws DAOException;
	
	/**
	 * Checks if there are any {@link ConceptReferenceTermMap}s or {@link ConceptMap}s using the
	 * specified mapType
	 * 
	 * @param mapType
	 * @return true if map type is in use
	 * @throws DAOException
	 * @should return true if a mapType has a conceptMap or more using it
	 * @should return true if a mapType has a conceptReferenceTermMap or more using it
	 * @should return false if a mapType has no maps using it
	 */
	public boolean isConceptMapTypeInUse(ConceptMapType mapType) throws DAOException;
	
	/**
	 * @see ConceptService#getConceptsByName(String, Locale, Boolean)
	 */
	public List<Concept> getConceptsByName(String name, Locale locale, Boolean exactLocal);
	
	/**
	 * @see ConceptService#getConceptByName(String)
	 */
	public Concept getConceptByName(String name);
	
	/**
	 * It is in the DAO, because it must be done in the MANUAL flush mode to prevent premature
	 * flushes in {@link ConceptService#saveConcept(Concept)}. It will be removed in 1.10 when we
	 * have a better way to manage flush modes.
	 * 
	 * @see ConceptService#getDefaultConceptMapType()
	 */
	public ConceptMapType getDefaultConceptMapType() throws DAOException;
	
	/**
	 * @see ConceptService#isConceptNameDuplicate(ConceptName)
	 */
	public boolean isConceptNameDuplicate(ConceptName name);
	
	/**
	 * @see ConceptService#getDrugs(String, java.util.Locale, boolean, boolean)
	 */
	public List<Drug> getDrugs(String searchPhrase, Locale locale, boolean exactLocale, boolean includeRetired);
	
	/**
	 * @see org.mrs.api.ConceptService#getDrugsByMapping(String, ConceptSource, Collection,
	 *      boolean)
	 */
	public List<Drug> getDrugsByMapping(String code, ConceptSource conceptSource,
	        Collection<ConceptMapType> withAnyOfTheseTypes, boolean includeRetired) throws DAOException;
	
	/**
	 * @see org.mrs.api.ConceptService#getDrugByMapping(String, org.mrs.ConceptSource, java.util.Collection)
	 */
	Drug getDrugByMapping(String code, ConceptSource conceptSource,
	        Collection<ConceptMapType> withAnyOfTheseTypesOrOrderOfPreference) throws DAOException;

	/**
	 * @see ConceptService#getAllConceptAttributeTypes()
	 */

	List<ConceptAttributeType> getAllConceptAttributeTypes();

	/**
	 * @see ConceptService#saveConceptAttributeType(ConceptAttributeType)
	 */
	ConceptAttributeType saveConceptAttributeType(ConceptAttributeType conceptAttributeType);

	/**
	 * @see ConceptService#getConceptAttributeType(Integer)
	 */
	ConceptAttributeType getConceptAttributeType(Integer id);

	/**
	 * @see ConceptService#getConceptAttributeTypeByUuid(String)
	 */
	ConceptAttributeType getConceptAttributeTypeByUuid(String uuid);

	/**
	 * @see ConceptService#purgeConceptAttributeType(ConceptAttributeType)
	 */
	public void deleteConceptAttributeType(ConceptAttributeType conceptAttributeType);

	/**
	 * @see ConceptService#getConceptAttributeTypes(String)
	 */
	public List<ConceptAttributeType> getConceptAttributeTypes(String name);

	/**
	 * @see ConceptService#getConceptAttributeTypeByName(String)
	 */
	public ConceptAttributeType getConceptAttributeTypeByName(String exactName);

	/**
	 * @see ConceptService#getConceptAttributeByUuid(String)
	 */
	public ConceptAttribute getConceptAttributeByUuid(String uuid);

	/**
	 * @see ConceptService#hasAnyConceptAttribute(ConceptAttributeType)
	 */
	public long getConceptAttributeCount(ConceptAttributeType conceptAttributeType);
}

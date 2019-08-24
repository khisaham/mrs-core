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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mrs.Person;
import org.mrs.Provider;
import org.mrs.ProviderAttribute;
import org.mrs.ProviderAttributeType;
import org.mrs.api.APIException;
import org.mrs.api.ProviderService;
import org.mrs.api.context.Context;
import org.mrs.api.db.ProviderDAO;
import org.mrs.customdatatype.CustomDatatypeUtil;
import org.mrs.util.OpenmrsConstants;
import org.mrs.util.OpenmrsUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the {@link ProviderService}. This class should not be used on its own.
 * The current OpenMRS implementation should be fetched from the Context.
 * 
 * @since 1.9
 */
@Transactional
public class ProviderServiceImpl extends BaseOpenmrsService implements ProviderService {
	
	private ProviderDAO dao;
	
	/**
	 * Sets the data access object for Concepts. The dao is used for saving and getting concepts
	 * to/from the database
	 * 
	 * @param dao The data access object to use
	 */
	public void setProviderDAO(ProviderDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getAllProviders()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Provider> getAllProviders() {
		return Context.getProviderService().getAllProviders(true);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getAllProviders(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Provider> getAllProviders(boolean includeRetired) {
		return dao.getAllProviders(includeRetired);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#retireProvider(org.mrs.Provider, java.lang.String)
	 */
	@Override
	public void retireProvider(Provider provider, String reason) {
		dao.saveProvider(provider);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#unretireProvider(org.mrs.Provider)
	 */
	@Override
	public Provider unretireProvider(Provider provider) {
		return Context.getProviderService().saveProvider(provider);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#purgeProvider(org.mrs.Provider)
	 */
	@Override
	public void purgeProvider(Provider provider) {
		dao.deleteProvider(provider);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getProvider(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public Provider getProvider(Integer providerId) {
		return dao.getProvider(providerId);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#saveProvider(org.mrs.Provider)
	 */
	@Override
	public Provider saveProvider(Provider provider) {
		CustomDatatypeUtil.saveAttributesIfNecessary(provider);
		return dao.saveProvider(provider);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getProviderByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Provider getProviderByUuid(String uuid) {
		return dao.getProviderByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getProvidersByPerson(org.mrs.Person, boolean )
	 */
	@Override
	@Transactional(readOnly = true)
	public Collection<Provider> getProvidersByPerson(Person person, boolean includeRetired) {
		return dao.getProvidersByPerson(person, includeRetired);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getProvidersByPerson(org.mrs.Person)
	 */
	@Override
	@Transactional(readOnly = true)
	public Collection<Provider> getProvidersByPerson(Person person) {
		if (person == null) {
			throw new IllegalArgumentException("Person must not be null");
		}
		return Context.getProviderService().getProvidersByPerson(person, true);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getCountOfProviders(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Integer getCountOfProviders(String query) {
		return Context.getProviderService().getCountOfProviders(query, false);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getCountOfProviders(java.lang.String, boolean)
	 */
	@Override
	public Integer getCountOfProviders(String query, boolean includeRetired) {
		return OpenmrsUtil.convertToInteger(dao.getCountOfProviders(query, includeRetired));
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getProviders(String, Integer, Integer, java.util.Map,
	 *      boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Provider> getProviders(String query, Integer start, Integer length,
	        Map<ProviderAttributeType, Object> attributeValues, boolean includeRetired) {
		Map<ProviderAttributeType, String> serializedAttributeValues = CustomDatatypeUtil
		        .getValueReferences(attributeValues);
		return dao.getProviders(query, serializedAttributeValues, start, length, includeRetired);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getProviders(String, Integer, Integer, java.util.Map)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Provider> getProviders(String query, Integer start, Integer length,
	        Map<ProviderAttributeType, Object> attributeValues) {
		return Context.getProviderService().getProviders(query, start, length, attributeValues, true);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getAllProviderAttributeTypes()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProviderAttributeType> getAllProviderAttributeTypes() {
		return dao.getAllProviderAttributeTypes(true);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getAllProviderAttributeTypes(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProviderAttributeType> getAllProviderAttributeTypes(boolean includeRetired) {
		return dao.getAllProviderAttributeTypes(includeRetired);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getProviderAttributeType(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public ProviderAttributeType getProviderAttributeType(Integer providerAttributeTypeId) {
		return dao.getProviderAttributeType(providerAttributeTypeId);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getProviderAttributeTypeByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public ProviderAttributeType getProviderAttributeTypeByUuid(String uuid) {
		return dao.getProviderAttributeTypeByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getProviderAttribute(java.lang.Integer)
	 */
	
	@Override
	@Transactional(readOnly = true)
	public ProviderAttribute getProviderAttribute(Integer providerAttributeID) {
		return dao.getProviderAttribute(providerAttributeID);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getProviderAttributeByUuid(String)
	 */
	
	@Override
	@Transactional(readOnly = true)
	public ProviderAttribute getProviderAttributeByUuid(String uuid) {
		return dao.getProviderAttributeByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#saveProviderAttributeType(org.mrs.ProviderAttributeType)
	 */
	@Override
	public ProviderAttributeType saveProviderAttributeType(ProviderAttributeType providerAttributeType) {
		return dao.saveProviderAttributeType(providerAttributeType);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#retireProviderAttributeType(org.mrs.ProviderAttributeType,
	 *      java.lang.String)
	 */
	@Override
	public ProviderAttributeType retireProviderAttributeType(ProviderAttributeType providerAttributeType, String reason) {
		return Context.getProviderService().saveProviderAttributeType(providerAttributeType);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#unretireProviderAttributeType(org.mrs.ProviderAttributeType)
	 */
	@Override
	public ProviderAttributeType unretireProviderAttributeType(ProviderAttributeType providerAttributeType) {
		return Context.getProviderService().saveProviderAttributeType(providerAttributeType);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#purgeProviderAttributeType(org.mrs.ProviderAttributeType)
	 */
	@Override
	public void purgeProviderAttributeType(ProviderAttributeType providerAttributeType) {
		dao.deleteProviderAttributeType(providerAttributeType);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#isProviderIdentifierUnique(Provider)
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean isProviderIdentifierUnique(Provider provider) throws APIException {
		return dao.isProviderIdentifierUnique(provider);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getProviderByIdentifier(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Provider getProviderByIdentifier(String identifier) {
		return dao.getProviderByIdentifier(identifier);
	}
	
	/**
	 * @see org.mrs.api.ProviderService#getUnknownProvider()
	 */
	@Override
	@Transactional(readOnly = true)
	public Provider getUnknownProvider() {
		return getProviderByUuid(Context.getAdministrationService().getGlobalProperty(
		    OpenmrsConstants.GP_UNKNOWN_PROVIDER_UUID));
	}
}

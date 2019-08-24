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

import java.util.List;

import org.mrs.GlobalProperty;
import org.mrs.OpenmrsObject;
import org.mrs.api.AdministrationService;
import org.springframework.validation.Errors;

/**
 * Database methods for the AdministrationService
 * 
 * @see org.mrs.api.context.Context
 * @see org.mrs.api.AdministrationService
 */
public interface AdministrationDAO {
	
	/**
	 * @see org.mrs.api.AdministrationService#getGlobalProperty(String)
	 */
	public String getGlobalProperty(String propertyName) throws DAOException;
	
	/**
	 * @see org.mrs.api.AdministrationService#getGlobalPropertyObject(java.lang.String)
	 */
	public GlobalProperty getGlobalPropertyObject(String propertyName);
	
	/**
	 * @see org.mrs.api.AdministrationService#getAllGlobalProperties()
	 */
	public List<GlobalProperty> getAllGlobalProperties() throws DAOException;
	
	public GlobalProperty getGlobalPropertyByUuid(String uuid) throws DAOException;
	
	/**
	 * @see org.mrs.api.AdministrationService#getGlobalPropertiesByPrefix(java.lang.String)
	 */
	public List<GlobalProperty> getGlobalPropertiesByPrefix(String prefix);
	
	/**
	 * @see org.mrs.api.AdministrationService#getGlobalPropertiesBySuffix(java.lang.String)
	 */
	public List<GlobalProperty> getGlobalPropertiesBySuffix(String suffix);
	
	/**
	 * @see org.mrs.api.AdministrationService#purgeGlobalProperty(org.mrs.GlobalProperty)
	 */
	public void deleteGlobalProperty(GlobalProperty gp) throws DAOException;
	
	/**
	 * @see org.mrs.api.AdministrationService#saveGlobalProperty(org.mrs.GlobalProperty)
	 */
	public GlobalProperty saveGlobalProperty(GlobalProperty gp) throws DAOException;
	
	/**
	 * @see org.mrs.api.db.AdministrationDAO#executeSQL(java.lang.String, boolean)
	 */
	public List<List<Object>> executeSQL(String sql, boolean selectOnly) throws DAOException;
	
	/**
	 * @see org.mrs.api.AdministrationService#getMaximumPropertyLength(Class, String)
	 */
	public int getMaximumPropertyLength(Class<? extends OpenmrsObject> aClass, String fieldName);
	
	/**
	 * @see org.mrs.api.AdministrationService#validate(Object, Errors)
	 */
	public void validate(Object object, Errors errors) throws DAOException;
	
	/**
	 * @see AdministrationService#isDatabaseStringComparisonCaseSensitive()
	 */
	public boolean isDatabaseStringComparisonCaseSensitive() throws DAOException;
}

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

import org.mrs.OrderSet;
import org.mrs.OrderSetMember;

/**
 * OrderSet-related database functions
 * <p>
 * This class should never be used directly. It should only be used through the
 * {@link org.mrs.api.OrderSetService}
 *
 * @see org.mrs.api.OrderSetService
 */
public interface OrderSetDAO {
	
	/**
	 * @see org.mrs.api.OrderSetService#saveOrderSet(OrderSet)
	 */
	OrderSet save(OrderSet orderSet) throws DAOException;
	
	/**
	 * @see org.mrs.api.OrderSetService#getOrderSets(boolean)
	 */
	List<OrderSet> getOrderSets(boolean includeRetired) throws DAOException;
	
	/**
	 * @see org.mrs.api.OrderSetService#getOrderSet(Integer)
	 */
	OrderSet getOrderSetById(Integer orderSetId) throws DAOException;
	
	/**
	 * @see org.mrs.api.OrderSetService#getOrderSetByUuid(String)
	 */
	OrderSet getOrderSetByUniqueUuid(String orderSetUuid) throws DAOException;
	

	/**
	 * @see org.mrs.api.OrderSetService#getOrderSetMemberByUuid(String)
	 */
	OrderSetMember getOrderSetMemberByUuid(String uuid) throws DAOException;
}

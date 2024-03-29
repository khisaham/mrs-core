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

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mrs.OrderSet;
import org.mrs.OrderSetMember;
import org.mrs.api.APIException;
import org.mrs.api.OrderSetService;
import org.mrs.api.context.Context;
import org.mrs.api.db.OrderSetDAO;
import org.springframework.transaction.annotation.Transactional;

public class OrderSetServiceImpl extends BaseOpenmrsService implements OrderSetService {
	
	protected OrderSetDAO dao;
	
	/**
	 * @see org.mrs.api.OrderSetService#setOrderSetDAO(org.mrs.api.db.OrderSetDAO)
	 */
	@Override
	public void setOrderSetDAO(OrderSetDAO dao) {
		this.dao = dao;
	}
	
	@Override
	@Transactional(readOnly = false)
	public OrderSet saveOrderSet(OrderSet orderSet) throws APIException {
		return saveOrderSetInternal(orderSet);
	}
	
	/**
	 * @see org.mrs.api.OrderSetService#retireOrderSet(OrderSet, String)
	 */
	@Override
	@Transactional(readOnly = false)
	public OrderSet retireOrderSet(OrderSet orderSet, String retireReason) throws APIException {
		if (StringUtils.isBlank(retireReason)) {
			throw new IllegalArgumentException("retire reason cannot be empty or null");
		}
		for (OrderSetMember orderSetMember : orderSet.getOrderSetMembers()) {
			orderSet.retireOrderSetMember(orderSetMember);
		}
		return saveOrderSetInternal(orderSet);
	}
	
	/**
	 * @see org.mrs.api.OrderSetService#unretireOrderSet(OrderSet)
	 */
	@Override
	@Transactional(readOnly = false)
	public OrderSet unretireOrderSet(OrderSet orderSet) throws APIException {
		return saveOrderSetInternal(orderSet);
	}
	
	/**
	 * @see org.mrs.api.OrderSetService#saveOrderSet(OrderSet)
	 */
	private synchronized OrderSet saveOrderSetInternal(OrderSet orderSet) throws APIException {
		if (CollectionUtils.isEmpty(orderSet.getOrderSetMembers())) {
			return dao.save(orderSet);
		}
		for (OrderSetMember orderSetMember : orderSet.getOrderSetMembers()) {
			if (null == orderSetMember.getOrderSet()) {
				orderSetMember.setOrderSet(orderSet);
			}
		}
		for (OrderSetMember orderSetMember : orderSet.getOrderSetMembers()) {
			if (orderSetMember.getRetired()) {
				orderSetMember.setRetiredBy(Context.getAuthenticatedUser());
				orderSetMember.setDateRetired(new Date());
			}
		}
		
		return dao.save(orderSet);
	}
	
	/**
	 * @see org.mrs.api.OrderSetService#getOrderSets(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<OrderSet> getOrderSets(boolean includeRetired) throws APIException {
		return dao.getOrderSets(includeRetired);
	}
	
	/**
	 * @see org.mrs.api.OrderSetService#getOrderSet(Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public OrderSet getOrderSet(Integer orderSetId) throws APIException {
		return dao.getOrderSetById(orderSetId);
	}
	
	/**
	 * @see org.mrs.api.OrderSetService#getOrderSetByUuid(String)
	 */
	@Override
	@Transactional(readOnly = true)
	public OrderSet getOrderSetByUuid(String orderSetUuid) throws APIException {
		return dao.getOrderSetByUniqueUuid(orderSetUuid);
	}

	/**
	 * @see org.mrs.api.OrderSetService#getOrderSetMemberByUuid(String)
	 */
	@Override
	@Transactional(readOnly = true)
	public OrderSetMember getOrderSetMemberByUuid(String uuid) {
		return dao.getOrderSetMemberByUuid(uuid);
	}
}

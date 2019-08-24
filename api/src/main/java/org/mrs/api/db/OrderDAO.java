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
import java.util.Locale;

import org.mrs.CareSetting;
import org.mrs.Concept;
import org.mrs.ConceptClass;
import org.mrs.Encounter;
import org.mrs.Order;
import org.mrs.OrderFrequency;
import org.mrs.OrderGroup;
import org.mrs.OrderType;
import org.mrs.Patient;
import org.mrs.User;
import org.mrs.api.APIException;
import org.mrs.parameter.OrderSearchCriteria;

/**
 * Order-related database functions
 * <p>
 * This class should never be used directly. It should only be used through the
 * {@link org.mrs.api.OrderService}
 * 
 * @see org.mrs.api.OrderService
 */
public interface OrderDAO {
	
	/**
	 * @see org.mrs.api.OrderService#saveOrder(org.mrs.Order, org.mrs.api.OrderContext)
	 */
	public Order saveOrder(Order order) throws DAOException;
	
	/**
	 * @see org.mrs.api.OrderService#purgeOrder(Order)
	 */
	public void deleteOrder(Order order) throws DAOException;
	
	/**
	 * @see org.mrs.api.OrderService#getOrder(Integer)
	 */
	public Order getOrder(Integer orderId) throws DAOException;
	
	/**
	 * This searches for orders given the parameters. Most arguments are optional (nullable). If
	 * multiple arguments are given, the returned orders will match on all arguments. The orders are
	 * sorted by startDate with the latest coming first
	 * 
	 * @param orderType The type of Order to get
	 * @param patients The patients to get orders for
	 * @param concepts The concepts in order.getConcept to get orders for
	 * @param orderers The orderers to match on
	 * @param encounters The encounters that the orders are assigned to
	 * @return list of Orders matching the parameters
	 */
	public List<Order> getOrders(OrderType orderType, List<Patient> patients, List<Concept> concepts, List<User> orderers,
	        List<Encounter> encounters);
	
	/**
	 * @see org.mrs.api.OrderService#getOrders(org.mrs.Patient, org.mrs.CareSetting,
	 *      org.mrs.OrderType, boolean)
	 */
	public List<Order> getOrders(Patient patient, CareSetting careSetting, List<OrderType> orderTypes,
	        boolean includeVoided, boolean includeDiscontinuationOrders);

	/**
	 * @see org.mrs.api.OrderService#getOrders(OrderSearchCriteria)
	 */
	public List<Order> getOrders(OrderSearchCriteria orderSearchCriteria);
	
	/**
	 * @param uuid
	 * @return order or null
	 */
	public Order getOrderByUuid(String uuid);
	
	/**
	 * Delete Obs that references an order
	 */
	public void deleteObsThatReference(Order order);
	
	/**
	 * @see org.mrs.api.OrderService#getOrderByOrderNumber(java.lang.String)
	 */
	public Order getOrderByOrderNumber(String orderNumber);
	
	/**
	 * Gets the next available order number seed
	 * 
	 * @return the order number seed
	 */
	public Long getNextOrderNumberSeedSequenceValue();
	
	/**
	 * @see org.mrs.api.OrderService#getActiveOrders(org.mrs.Patient, org.mrs.OrderType,
	 *      org.mrs.CareSetting, java.util.Date)
	 */
	public List<Order> getActiveOrders(Patient patient, List<OrderType> orderTypes, CareSetting careSetting, Date asOfDate);
	
	/**
	 * Get care setting by type
	 * 
	 * @param careSettingId
	 * @return the care setting type
	 */
	public CareSetting getCareSetting(Integer careSettingId);
	
	/**
	 * @see org.mrs.api.OrderService#getCareSettingByUuid(String)
	 */
	public CareSetting getCareSettingByUuid(String uuid);
	
	/**
	 * @see org.mrs.api.OrderService#getCareSettingByName(String)
	 */
	public CareSetting getCareSettingByName(String name);
	
	/**
	 * @see org.mrs.api.OrderService#getCareSettings(boolean)
	 */
	public List<CareSetting> getCareSettings(boolean includeRetired);
	
	/**
	 * @see org.mrs.api.OrderService#getOrderTypeByName(String)
	 */
	public OrderType getOrderTypeByName(String orderTypeName);
	
	/**
	 * @see org.mrs.api.OrderService#getOrderFrequency
	 */
	public OrderFrequency getOrderFrequency(Integer orderFrequencyId);
	
	/**
	 * @see org.mrs.api.OrderService#getOrderFrequencyByUuid
	 */
	public OrderFrequency getOrderFrequencyByUuid(String uuid);
	
	/**
	 * @see org.mrs.api.OrderService#getOrderFrequencies(boolean)
	 */
	List<OrderFrequency> getOrderFrequencies(boolean includeRetired);
	
	/**
	 * @see org.mrs.api.OrderService#getOrderFrequencies(String, java.util.Locale, boolean, boolean)
	 */
	public List<OrderFrequency> getOrderFrequencies(String searchPhrase, Locale locale, boolean exactLocale,
	        boolean includeRetired);
	
	/**
	 * @see org.mrs.api.OrderService#saveOrderFrequency(org.mrs.OrderFrequency)
	 */
	public OrderFrequency saveOrderFrequency(OrderFrequency orderFrequency);
	
	/**
	 * @see org.mrs.api.OrderService#purgeOrderFrequency(org.mrs.OrderFrequency)
	 */
	public void purgeOrderFrequency(OrderFrequency orderFrequency);
	
	/**
	 * Checks if an order frequency is being referenced by any order
	 * 
	 * @param orderFrequency the order frequency
	 * @return true if in use, else false
	 */
	public boolean isOrderFrequencyInUse(OrderFrequency orderFrequency);
	
	/**
	 * @see org.mrs.api.OrderService#getOrderFrequencyByConcept
	 */
	public OrderFrequency getOrderFrequencyByConcept(Concept concept);
	
	/**
	 * @see org.mrs.api.OrderService#getOrderType
	 */
	public OrderType getOrderType(Integer orderTypeId);
	
	/**
	 * @see org.mrs.api.OrderService#getOrderTypeByUuid
	 */
	public OrderType getOrderTypeByUuid(String uuid);
	
	/**
	 * @see org.mrs.api.OrderService#getOrderTypes
	 */
	public List<OrderType> getOrderTypes(boolean includeRetired);
	
	/**
	 * @see org.mrs.api.OrderService#getOrderTypeByConceptClass(org.mrs.ConceptClass)
	 */
	public OrderType getOrderTypeByConceptClass(ConceptClass conceptClass);
	
	/**
	 * @see org.mrs.api.OrderService#saveOrderType(org.mrs.OrderType)
	 */
	public OrderType saveOrderType(OrderType orderType);
	
	/**
	 * @see org.mrs.api.OrderService#purgeOrderType(org.mrs.OrderType)
	 */
	public void purgeOrderType(OrderType orderType);
	
	/**
	 * @see org.mrs.api.OrderService#getSubtypes(org.mrs.OrderType, boolean)
	 */
	public List<OrderType> getOrderSubtypes(OrderType orderType, boolean includeRetired);
	
	/**
	 * Check whether give order type is used by any order
	 * 
	 * @param orderType the order type to check the usage
	 * @return true if used else false
	 */
	public boolean isOrderTypeInUse(OrderType orderType);
	
	/**
	 * @see org.mrs.api.OrderService#getDiscontinuationOrder(Order)
	 */
	public Order getDiscontinuationOrder(Order order);
	
	/**
	 * @see org.mrs.api.OrderService#getRevisionOrder(org.mrs.Order)
	 */
	public Order getRevisionOrder(Order order) throws APIException;
	
	/**
	 * Get the fresh order from the database
	 *
	 * @param order the order to get from the database
	 * @param isOrderADrugOrder is the order a previous order
	 * @return a list of orders from the database
	 */
	public List<Object[]> getOrderFromDatabase(Order order, boolean isOrderADrugOrder) throws APIException;

	/**
	 * Saves an orderGroup to the database
	 *
	 * @param orderGroup
	 * @return an orderGroup
	 * @throws DAOException
	 */
	public OrderGroup saveOrderGroup(OrderGroup orderGroup) throws DAOException;
	
	/**
	 * @see org.mrs.api.OrderService#getOrderGroupByUuid(String)
	 */
	public OrderGroup getOrderGroupByUuid(String uuid) throws DAOException;
	
	/**
	 * @see org.mrs.api.OrderService#getOrderGroup(Integer)
	 */
	public OrderGroup getOrderGroupById(Integer orderGroupId) throws DAOException;
}

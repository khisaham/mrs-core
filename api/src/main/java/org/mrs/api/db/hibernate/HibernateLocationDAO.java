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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.mrs.Location;
import org.mrs.LocationAttribute;
import org.mrs.LocationAttributeType;
import org.mrs.LocationTag;
import org.mrs.api.db.DAOException;
import org.mrs.api.db.LocationDAO;

/**
 * Hibernate location-related database functions
 */
public class HibernateLocationDAO implements LocationDAO {
	
	private SessionFactory sessionFactory;
	
	/**
	 * @see org.mrs.api.db.LocationDAO#setSessionFactory(org.hibernate.SessionFactory)
	 */
	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#saveLocation(org.mrs.Location)
	 */
	@Override
	public Location saveLocation(Location location) {
		if (location.getChildLocations() != null && location.getLocationId() != null) {
			// hibernate has a problem updating child collections
			// if the parent object was already saved so we do it
			// explicitly here
			for (Location child : location.getChildLocations()) {
				if (child.getLocationId() == null) {
					saveLocation(child);
				}
			}
		}
		
		sessionFactory.getCurrentSession().saveOrUpdate(location);
		return location;
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getLocation(java.lang.Integer)
	 */
	@Override
	public Location getLocation(Integer locationId) {
		return (Location) sessionFactory.getCurrentSession().get(Location.class, locationId);
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getLocation(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Location getLocation(String name) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class).add(
		    Restrictions.eq("name", name));
		
		List<Location> locations = criteria.list();
		if (null == locations || locations.isEmpty()) {
			return null;
		}
		return locations.get(0);
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getAllLocations(boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Location> getAllLocations(boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		} else {
			//push retired locations to the end of the returned list
			criteria.addOrder(Order.asc("retired"));
		}
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#deleteLocation(org.mrs.Location)
	 */
	@Override
	public void deleteLocation(Location location) {
		sessionFactory.getCurrentSession().delete(location);
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#saveLocation(org.mrs.Location)
	 */
	@Override
	public LocationTag saveLocationTag(LocationTag tag) {
		sessionFactory.getCurrentSession().saveOrUpdate(tag);
		return tag;
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getLocationTag(java.lang.Integer)
	 */
	@Override
	public LocationTag getLocationTag(Integer locationTagId) {
		return (LocationTag) sessionFactory.getCurrentSession().get(LocationTag.class, locationTagId);
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getLocationTagByName(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public LocationTag getLocationTagByName(String tag) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LocationTag.class).add(
		    Restrictions.eq("name", tag));
		
		List<LocationTag> tags = criteria.list();
		if (null == tags || tags.isEmpty()) {
			return null;
		}
		return tags.get(0);
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getAllLocationTags(boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<LocationTag> getAllLocationTags(boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LocationTag.class);
		if (!includeRetired) {
			criteria.add(Restrictions.like("retired", false));
		}
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getLocationTags(String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<LocationTag> getLocationTags(String search) {
		return sessionFactory.getCurrentSession().createCriteria(LocationTag.class)
		// 'ilike' case insensitive search
		        .add(Restrictions.ilike("name", search, MatchMode.START)).addOrder(Order.asc("name")).list();
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#deleteLocationTag(org.mrs.LocationTag)
	 */
	@Override
	public void deleteLocationTag(LocationTag tag) {
		sessionFactory.getCurrentSession().delete(tag);
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getLocationByUuid(java.lang.String)
	 */
	@Override
	public Location getLocationByUuid(String uuid) {
		return (Location) sessionFactory.getCurrentSession().createQuery("from Location l where l.uuid = :uuid").setString(
		    "uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getLocationTagByUuid(java.lang.String)
	 */
	@Override
	public LocationTag getLocationTagByUuid(String uuid) {
		return (LocationTag) sessionFactory.getCurrentSession().createQuery("from LocationTag where uuid = :uuid")
		        .setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getCountOfLocations(String, Boolean)
	 */
	@Override
	public Long getCountOfLocations(String nameFragment, Boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		
		if (StringUtils.isNotBlank(nameFragment)) {
			criteria.add(Restrictions.ilike("name", nameFragment, MatchMode.START));
		}
		
		criteria.setProjection(Projections.rowCount());
		
		return (Long) criteria.uniqueResult();
	}
	
	/**
	 * @see LocationDAO#getLocations(String, org.mrs.Location, java.util.Map, boolean, Integer, Integer)
	 */
	@Override
	public List<Location> getLocations(String nameFragment, Location parent,
	        Map<LocationAttributeType, String> serializedAttributeValues, boolean includeRetired, Integer start,
	        Integer length) {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class);
		
		if (StringUtils.isNotBlank(nameFragment)) {
			criteria.add(Restrictions.ilike("name", nameFragment, MatchMode.START));
		}
		
		if (parent != null) {
			criteria.add(Restrictions.eq("parentLocation", parent));
		}
		
		if (serializedAttributeValues != null) {
			HibernateUtil.addAttributeCriteria(criteria, serializedAttributeValues);
		}
		
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		
		criteria.addOrder(Order.asc("name"));
		if (start != null) {
			criteria.setFirstResult(start);
		}
		if (length != null && length > 0) {
			criteria.setMaxResults(length);
		}
		
		return criteria.list();
	}
	
	/**
	 * @see LocationDAO#getRootLocations(boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Location> getRootLocations(boolean includeRetired) throws DAOException {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		
		criteria.add(Restrictions.isNull("parentLocation"));
		
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getAllLocationAttributeTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LocationAttributeType> getAllLocationAttributeTypes() {
		return sessionFactory.getCurrentSession().createCriteria(LocationAttributeType.class).list();
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getLocationAttributeType(java.lang.Integer)
	 */
	@Override
	public LocationAttributeType getLocationAttributeType(Integer id) {
		return (LocationAttributeType) sessionFactory.getCurrentSession().get(LocationAttributeType.class, id);
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getLocationAttributeTypeByUuid(java.lang.String)
	 */
	@Override
	public LocationAttributeType getLocationAttributeTypeByUuid(String uuid) {
		return (LocationAttributeType) sessionFactory.getCurrentSession().createCriteria(LocationAttributeType.class).add(
		    Restrictions.eq("uuid", uuid)).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#saveLocationAttributeType(org.mrs.LocationAttributeType)
	 */
	@Override
	public LocationAttributeType saveLocationAttributeType(LocationAttributeType locationAttributeType) {
		sessionFactory.getCurrentSession().saveOrUpdate(locationAttributeType);
		return locationAttributeType;
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#deleteLocationAttributeType(org.mrs.LocationAttributeType)
	 */
	@Override
	public void deleteLocationAttributeType(LocationAttributeType locationAttributeType) {
		sessionFactory.getCurrentSession().delete(locationAttributeType);
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getLocationAttributeByUuid(java.lang.String)
	 */
	@Override
	public LocationAttribute getLocationAttributeByUuid(String uuid) {
		return (LocationAttribute) sessionFactory.getCurrentSession().createCriteria(LocationAttribute.class).add(
		    Restrictions.eq("uuid", uuid)).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getLocationAttributeTypeByName(java.lang.String)
	 */
	@Override
	public LocationAttributeType getLocationAttributeTypeByName(String name) {
		return (LocationAttributeType) sessionFactory.getCurrentSession().createCriteria(LocationAttributeType.class).add(
		    Restrictions.eq("name", name)).uniqueResult();
	}
	
	/**
	 * @see org.mrs.api.db.LocationDAO#getLocationsHavingAllTags(java.util.List)
	 */
	@Override
	public List<Location> getLocationsHavingAllTags(List<LocationTag> tags) {
		tags.removeAll(Collections.singleton(null));
		
		DetachedCriteria numberOfMatchingTags = DetachedCriteria.forClass(Location.class, "alias").createAlias("alias.tags",
		    "locationTag").add(Restrictions.in("locationTag.locationTagId", getLocationTagIds(tags))).setProjection(
		    Projections.rowCount()).add(Restrictions.eqProperty("alias.locationId", "outer.locationId"));
		
		return sessionFactory.getCurrentSession().createCriteria(Location.class, "outer").add(
		    Restrictions.eq("retired", false)).add(Subqueries.eq(Long.valueOf(tags.size()), numberOfMatchingTags)).list();
	}
	
	/**
	 * Extract locationTagIds from the list of LocationTag objects provided.
	 *
	 * @param tags
	 * @return
	 */
	private List<Integer> getLocationTagIds(List<LocationTag> tags) {
		List<Integer> locationTagIds = new ArrayList<>();
		for (LocationTag tag : tags) {
			locationTagIds.add(tag.getLocationTagId());
		}
		return locationTagIds;
	}
}

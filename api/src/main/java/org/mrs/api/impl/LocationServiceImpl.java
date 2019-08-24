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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.mrs.Address;
import org.mrs.Location;
import org.mrs.LocationAttribute;
import org.mrs.LocationAttributeType;
import org.mrs.LocationTag;
import org.mrs.api.APIException;
import org.mrs.api.LocationService;
import org.mrs.api.context.Context;
import org.mrs.api.db.LocationDAO;
import org.mrs.customdatatype.CustomDatatypeUtil;
import org.mrs.util.OpenmrsConstants;
import org.mrs.util.OpenmrsUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Default implementation of the {@link LocationService}
 * <p>
 * This class should not be instantiated alone, get a service class from the Context:
 * Context.getLocationService();
 *
 * @see org.mrs.api.context.Context
 * @see org.mrs.api.LocationService
 * @see org.mrs.Location
 */
@Transactional
public class LocationServiceImpl extends BaseOpenmrsService implements LocationService {
	
	private LocationDAO dao;
	
	/**
	 * @see org.mrs.api.LocationService#setLocationDAO(org.mrs.api.db.LocationDAO)
	 */
	@Override
	public void setLocationDAO(LocationDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.mrs.api.LocationService#saveLocation(org.mrs.Location)
	 */
	@Override
	public Location saveLocation(Location location) throws APIException {
		if (location.getName() == null) {
			throw new APIException("Location.name.required", (Object[]) null);
		}
		
		// Check for transient tags. If found, try to match by name and overwrite, otherwise throw exception.
		if (location.getTags() != null) {
			for (LocationTag tag : location.getTags()) {
				
				// only check transient (aka non-precreated) location tags
				if (tag.getLocationTagId() == null) {
					if (!StringUtils.hasLength(tag.getName())) {
						throw new APIException("Location.tag.name.required", (Object[]) null);
					}
					
					LocationTag existing = Context.getLocationService().getLocationTagByName(tag.getName());
					if (existing != null) {
						location.removeTag(tag);
						location.addTag(existing);
					} else {
						throw new APIException("Location.cannot.add.transient.tags", (Object[]) null);
					}
				}
			}
		}
		
		CustomDatatypeUtil.saveAttributesIfNecessary(location);
		
		return dao.saveLocation(location);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getLocation(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public Location getLocation(Integer locationId) throws APIException {
		return dao.getLocation(locationId);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getLocation(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Location getLocation(String name) throws APIException {
		return dao.getLocation(name);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getDefaultLocation()
	 */
	@Override
	@Transactional(readOnly = true)
	public Location getDefaultLocation() throws APIException {
		Location location = null;
		String locationGP = Context.getAdministrationService().getGlobalProperty(
		    OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCATION_NAME);
		
		if (StringUtils.hasText(locationGP)) {
			location = Context.getLocationService().getLocation(locationGP);
		}

		if (location == null) {
			location = getDefaultLocation(location, locationGP);
		}
		
		// If neither exist, get the first available location
		if (location == null) {
			location = Context.getLocationService().getLocation(1);
		}
		
		return location;
	}

	private Location getDefaultLocation(Location location, String locationGP) {
		//Try to look up 'Unknown Location' in case the global property is something else
		Location result = getDefaultLocationFromSting(location, locationGP, "Unknown Location");

		// If Unknown Location does not exist, try Unknown if the global property was different
		if (result == null) {
			result = getDefaultLocationFromSting(location, locationGP, "Unknown");
		}

		return result;
	}

	private Location getDefaultLocationFromSting(Location location, String locationGP, String defaultLocation) {
		Location result = null;
		if (location == null && (!StringUtils.hasText(locationGP) || !defaultLocation.equalsIgnoreCase(locationGP))) {
			result = Context.getLocationService().getLocation(defaultLocation);
		}

		return result;
	}
	
	/**
	 * @see org.mrs.api.LocationService#getLocationByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Location getLocationByUuid(String uuid) throws APIException {
		return dao.getLocationByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getLocationTagByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public LocationTag getLocationTagByUuid(String uuid) throws APIException {
		return dao.getLocationTagByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getAllLocations()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Location> getAllLocations() throws APIException {
		return dao.getAllLocations(true);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getAllLocations(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Location> getAllLocations(boolean includeRetired) throws APIException {
		return dao.getAllLocations(includeRetired);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getLocations(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Location> getLocations(String nameFragment) throws APIException {
		return Context.getLocationService().getLocations(nameFragment, null, null, false, null, null);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getLocationsByTag(LocationTag)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Location> getLocationsByTag(LocationTag tag) throws APIException {
		List<Location> locations = new ArrayList<>();
		
		for (Location l : dao.getAllLocations(false)) {
			if (l.getTags().contains(tag)) {
				locations.add(l);
			}
		}
		
		return locations;
	}
	
	/**
	 * @see org.mrs.api.LocationService#getLocationsHavingAllTags(List)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Location> getLocationsHavingAllTags(List<LocationTag> tags) throws APIException {
		return CollectionUtils.isEmpty(tags) ? getAllLocations(false) : dao.getLocationsHavingAllTags(tags);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getLocationsHavingAnyTag(List)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Location> getLocationsHavingAnyTag(List<LocationTag> tags) throws APIException {
		List<Location> locations = new ArrayList<>();
		
		for (Location loc : dao.getAllLocations(false)) {
			for (LocationTag t : tags) {
				if (loc.getTags().contains(t) && !locations.contains(loc)) {
					locations.add(loc);
				}
			}
		}
		
		return locations;
	}
	
	/**
	 * @see org.mrs.api.LocationService#retireLocation(Location, String)
	 */
	@Override
	public Location retireLocation(Location location, String reason) throws APIException {
		location.setRetired(true);
		location.setRetireReason(reason);
		return Context.getLocationService().saveLocation(location);
	}
	
	/**
	 * @see org.mrs.api.LocationService#unretireLocation(org.mrs.Location)
	 */
	@Override
	public Location unretireLocation(Location location) throws APIException {
		location.setRetired(false);
		return Context.getLocationService().saveLocation(location);
	}
	
	/**
	 * @see org.mrs.api.LocationService#purgeLocation(org.mrs.Location)
	 */
	@Override
	public void purgeLocation(Location location) throws APIException {
		dao.deleteLocation(location);
	}
	
	/**
	 * @see org.mrs.api.LocationService#saveLocationTag(org.mrs.LocationTag)
	 */
	@Override
	public LocationTag saveLocationTag(LocationTag tag) throws APIException {
		return dao.saveLocationTag(tag);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getLocationTag(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public LocationTag getLocationTag(Integer locationTagId) throws APIException {
		return dao.getLocationTag(locationTagId);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getLocationTagByName(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public LocationTag getLocationTagByName(String tag) throws APIException {
		return dao.getLocationTagByName(tag);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getAllLocationTags()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<LocationTag> getAllLocationTags() throws APIException {
		return dao.getAllLocationTags(true);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getAllLocationTags(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<LocationTag> getAllLocationTags(boolean includeRetired) throws APIException {
		return dao.getAllLocationTags(includeRetired);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getLocationTags(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<LocationTag> getLocationTags(String search) throws APIException {
		if (StringUtils.isEmpty(search)) {
			return Context.getLocationService().getAllLocationTags(true);
		}
		
		return dao.getLocationTags(search);
	}
	
	/**
	 * @see org.mrs.api.LocationService#retireLocationTag(LocationTag, String)
	 */
	@Override
	public LocationTag retireLocationTag(LocationTag tag, String reason) throws APIException {
		if (tag.getRetired()) {
			return tag;
		} else {
			if (reason == null) {
				throw new APIException("Location.retired.reason.required", (Object[]) null);
			}
			tag.setRetired(true);
			tag.setRetireReason(reason);
			tag.setRetiredBy(Context.getAuthenticatedUser());
			tag.setDateRetired(new Date());
			return Context.getLocationService().saveLocationTag(tag);
		}
	}
	
	/**
	 * @see org.mrs.api.LocationService#unretireLocationTag(org.mrs.LocationTag)
	 */
	@Override
	public LocationTag unretireLocationTag(LocationTag tag) throws APIException {
		tag.setRetired(false);
		tag.setRetireReason(null);
		tag.setRetiredBy(null);
		tag.setDateRetired(null);
		return Context.getLocationService().saveLocationTag(tag);
	}
	
	/**
	 * @see org.mrs.api.LocationService#purgeLocationTag(org.mrs.LocationTag)
	 */
	@Override
	public void purgeLocationTag(LocationTag tag) throws APIException {
		dao.deleteLocationTag(tag);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getCountOfLocations(String, Boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public Integer getCountOfLocations(String nameFragment, Boolean includeRetired) {
		return OpenmrsUtil.convertToInteger(dao.getCountOfLocations(nameFragment, includeRetired));
	}
	
	/**
	 * @see LocationService#getLocations(String, org.mrs.Location, java.util.Map, boolean,
	 *      Integer, Integer)
	 */
	@Override
	public List<Location> getLocations(String nameFragment, Location parent,
	        Map<LocationAttributeType, Object> attributeValues, boolean includeRetired, Integer start, Integer length) {
		
		Map<LocationAttributeType, String> serializedAttributeValues = CustomDatatypeUtil
		        .getValueReferences(attributeValues);
		
		return dao.getLocations(nameFragment, parent, serializedAttributeValues, includeRetired, start, length);
	}
	
	/**
	 * @see LocationService#getRootLocations(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Location> getRootLocations(boolean includeRetired) throws APIException {
		return dao.getRootLocations(includeRetired);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getPossibleAddressValues(Address, String)
	 */
	@Override
	public List<String> getPossibleAddressValues(Address incomplete, String fieldName) throws APIException {
		// not implemented by default
		return null;
	}
	
	/**
	 * @see org.mrs.api.LocationService#getAllLocationAttributeTypes()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<LocationAttributeType> getAllLocationAttributeTypes() {
		return dao.getAllLocationAttributeTypes();
	}
	
	/**
	 * @see org.mrs.api.LocationService#getLocationAttributeType(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public LocationAttributeType getLocationAttributeType(Integer id) {
		return dao.getLocationAttributeType(id);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getLocationAttributeTypeByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public LocationAttributeType getLocationAttributeTypeByUuid(String uuid) {
		return dao.getLocationAttributeTypeByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.LocationService#saveLocationAttributeType(org.mrs.LocationAttributeType)
	 */
	@Override
	public LocationAttributeType saveLocationAttributeType(LocationAttributeType locationAttributeType) {
		return dao.saveLocationAttributeType(locationAttributeType);
	}
	
	/**
	 * @see org.mrs.api.LocationService#retireLocationAttributeType(org.mrs.LocationAttributeType,
	 *      java.lang.String)
	 */
	@Override
	public LocationAttributeType retireLocationAttributeType(LocationAttributeType locationAttributeType, String reason) {
		return dao.saveLocationAttributeType(locationAttributeType);
	}
	
	/**
	 * @see org.mrs.api.LocationService#unretireLocationAttributeType(org.mrs.LocationAttributeType)
	 */
	@Override
	public LocationAttributeType unretireLocationAttributeType(LocationAttributeType locationAttributeType) {
		return Context.getLocationService().saveLocationAttributeType(locationAttributeType);
	}
	
	/**
	 * @see org.mrs.api.LocationService#purgeLocationAttributeType(org.mrs.LocationAttributeType)
	 */
	@Override
	public void purgeLocationAttributeType(LocationAttributeType locationAttributeType) {
		dao.deleteLocationAttributeType(locationAttributeType);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getLocationAttributeByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public LocationAttribute getLocationAttributeByUuid(String uuid) {
		return dao.getLocationAttributeByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.LocationService#getAddressTemplate()
	 */
	@Override
	@Transactional(readOnly = true)
	public String getAddressTemplate() throws APIException {
		String addressTemplate = Context.getAdministrationService().getGlobalProperty(
		    OpenmrsConstants.GLOBAL_PROPERTY_ADDRESS_TEMPLATE);
		if (!StringUtils.hasLength(addressTemplate)) {
			addressTemplate = OpenmrsConstants.DEFAULT_ADDRESS_TEMPLATE;
		}
		
		return addressTemplate;
	}
	
	/**
	 * @see org.mrs.api.LocationService#saveAddressTemplate(String)
	 */
	@Override
	public void saveAddressTemplate(String xml) throws APIException {
		Context.getAdministrationService().setGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_ADDRESS_TEMPLATE, xml);
		
	}
	
	/**
	 * @see org.mrs.api.LocationService#getLocationAttributeTypeByName(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public LocationAttributeType getLocationAttributeTypeByName(String name) {
		return dao.getLocationAttributeTypeByName(name);
	}
}

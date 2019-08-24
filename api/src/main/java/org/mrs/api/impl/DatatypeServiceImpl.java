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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mrs.api.DatatypeService;
import org.mrs.api.context.Context;
import org.mrs.api.db.ClobDatatypeStorage;
import org.mrs.api.db.DatatypeDAO;
import org.mrs.customdatatype.CustomDatatype;
import org.mrs.customdatatype.CustomDatatypeException;
import org.mrs.customdatatype.CustomDatatypeHandler;
import org.springframework.transaction.annotation.Transactional;

/**
 * Standard implementation of {@link DatatypeService}
 * @since 1.9
 */
@Transactional
public class DatatypeServiceImpl extends BaseOpenmrsService implements DatatypeService {
	
	private List<Class<? extends CustomDatatype>> datatypeClasses;
	
	private List<Class<? extends CustomDatatypeHandler>> handlerClasses;
	
	private transient Map<Class<? extends CustomDatatype>, Class<? extends CustomDatatypeHandler>> prioritizedHandlerClasses;
	
	private DatatypeDAO dao;
	
	/**
	 * Sets the dao
	 *
	 * @param dao the dao to set
	 */
	public void setDao(DatatypeDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.mrs.api.DatatypeService#getAllDatatypeClasses()
	 */
	@Override
	@Transactional(readOnly = true)
	public synchronized Set<Class<? extends CustomDatatype<?>>> getAllDatatypeClasses() {
		if (datatypeClasses == null) {
			populateBeanListsFromContext();
		}
		return new LinkedHashSet(datatypeClasses);
	}
	
	/**
	 * @see org.mrs.api.DatatypeService#getAllHandlerClasses()
	 */
	@Override
	@Transactional(readOnly = true)
	public synchronized Set<Class<? extends CustomDatatypeHandler<?, ?>>> getAllHandlerClasses() {
		if (handlerClasses == null) {
			populateBeanListsFromContext();
		}
		return new LinkedHashSet(handlerClasses);
	}
	
	private synchronized void populateBeanListsFromContext() {
		if (datatypeClasses == null) {
			List<CustomDatatype> datatypeBeans = Context.getRegisteredComponents(CustomDatatype.class);
			datatypeClasses = new ArrayList<>();
			for (CustomDatatype<?> dt : datatypeBeans) {
				datatypeClasses.add(dt.getClass());
			}
			
		}
		if (handlerClasses == null) {
			List<CustomDatatypeHandler> handlerBeans = Context.getRegisteredComponents(CustomDatatypeHandler.class);
			handlerClasses = new ArrayList<>();
			for (CustomDatatypeHandler<?, ?> h : handlerBeans) {
				handlerClasses.add(h.getClass());
			}
		}
	}
	
	/**
	 * @see org.mrs.api.DatatypeService#getDatatype(java.lang.Class, java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public <T extends CustomDatatype<?>> T getDatatype(Class<T> clazz, String config) {
		try {
			T dt = clazz.newInstance();
			dt.setConfiguration(config);
			return dt;
		}
		catch (Exception ex) {
			throw new CustomDatatypeException("Failed to instantiate " + clazz + " with config " + config, ex);
		}
	}
	
	/**
	 * @see org.mrs.api.DatatypeService#getHandlerClasses(Class)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Class<? extends CustomDatatypeHandler>> getHandlerClasses(Class<? extends CustomDatatype<?>> datatype) {
		List<Class<? extends CustomDatatypeHandler>> ret = new ArrayList<>();
		for (Class<? extends CustomDatatypeHandler<?, ?>> candidate : getAllHandlerClasses()) {
			if (datatypeClassHandled(candidate).equals(datatype)) {
				ret.add(candidate);
			}
		}
		return ret;
	}
	
	/**
	 * @param t
	 * @return the generic type of t or an interface it implements that is a CustomDatatype
	 */
	private Class datatypeClassHandled(Type t) {
		if (t instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) t;
			Type first = pt.getActualTypeArguments()[0];
			if (first instanceof Class && CustomDatatype.class.isAssignableFrom((Class) first)) {
				return (Class) first;
			} else {
				return datatypeClassHandled(pt.getRawType());
			}
			
		} else if (t instanceof Class) {
			Type genericSuperclass = ((Class) t).getGenericSuperclass();
			if (genericSuperclass != null) {
				Class ret = datatypeClassHandled(genericSuperclass);
				if (ret != null) {
					return ret;
				}
			}
			for (Type candidate : ((Class) t).getGenericInterfaces()) {
				Class ret = datatypeClassHandled(candidate);
				if (ret != null) {
					return ret;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * @see org.mrs.api.DatatypeService#getHandler(org.mrs.customdatatype.CustomDatatype, java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public synchronized CustomDatatypeHandler<?, ?> getHandler(CustomDatatype<?> datatype, String handlerConfig) {
		if (prioritizedHandlerClasses == null) {
			prioritizeHandlers();
		}
		Class<? extends CustomDatatypeHandler> clazz = prioritizedHandlerClasses.get(datatype.getClass());
		if (clazz == null) {
			return null;
		}
		try {
			CustomDatatypeHandler<?, ?> ret = clazz.newInstance();
			ret.setHandlerConfiguration(handlerConfig);
			return ret;
		}
		catch (Exception ex) {
			throw new CustomDatatypeException("Failed to instantiate handler for " + datatype + " with config "
			        + handlerConfig, ex);
		}
	}
	
	/**
	 * private method that prioritizes all registered handlers so we can quickly determine which to use for
	 * each datatype
	 */
	private synchronized void prioritizeHandlers() {
		if (prioritizedHandlerClasses == null) {
			prioritizedHandlerClasses = new LinkedHashMap<>();
			for (Class dt : getAllDatatypeClasses()) {
				List<Class<? extends CustomDatatypeHandler>> handlerClasses = getHandlerClasses(dt);
				if (handlerClasses == null || handlerClasses.isEmpty()) {
					prioritizedHandlerClasses.put(dt, null);
				} else {
					prioritizedHandlerClasses.put(dt, handlerClasses.get(0));
				}
			}
		}
	}
	
	/**
	 * @see org.mrs.api.DatatypeService#getClobDatatypeStorage(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public ClobDatatypeStorage getClobDatatypeStorage(Integer id) {
		return dao.getClobDatatypeStorage(id);
	}
	
	/**
	 * @see org.mrs.api.DatatypeService#getClobDatatypeStorageByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public ClobDatatypeStorage getClobDatatypeStorageByUuid(String uuid) {
		return dao.getClobDatatypeStorageByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.DatatypeService#saveClobDatatypeStorage(org.mrs.api.db.ClobDatatypeStorage)
	 */
	@Override
	public ClobDatatypeStorage saveClobDatatypeStorage(ClobDatatypeStorage storage) {
		return dao.saveClobDatatypeStorage(storage);
	}
	
	/**
	 * @see org.mrs.api.DatatypeService#deleteClobDatatypeStorage(org.mrs.api.db.ClobDatatypeStorage)
	 */
	@Override
	public void deleteClobDatatypeStorage(ClobDatatypeStorage storage) {
		dao.deleteClobDatatypeStorage(storage);
	}
	
}

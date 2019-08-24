/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.notification.db;

import java.util.List;

import org.mrs.User;
import org.mrs.api.db.DAOException;
import org.mrs.notification.Alert;

/**
 * Database methods for the AlertService
 * 
 * @see org.mrs.api.context.Context
 * @see org.mrs.notification.AlertService
 */
public interface AlertDAO {
	
	/**
	 * @see org.mrs.notification.AlertService#saveAlert(org.mrs.notification.Alert)
	 */
	public Alert saveAlert(Alert alert) throws DAOException;
	
	/**
	 * @see org.mrs.notification.AlertService#getAlert(Integer)
	 */
	public Alert getAlert(Integer alertId) throws DAOException;
	
	/**
	 * @see org.mrs.notification.AlertService#getAlerts(org.mrs.User, boolean, boolean)
	 */
	public List<Alert> getAlerts(User user, boolean includeRead, boolean includeVoided) throws DAOException;
	
	/**
	 * @see org.mrs.notification.AlertService#purgeAlert(org.mrs.notification.Alert)
	 */
	public void deleteAlert(Alert alert) throws DAOException;
	
	/**
	 * @see org.mrs.notification.AlertService#getAllAlerts(boolean)
	 */
	public List<Alert> getAllAlerts(boolean includeExpired);
	
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.propertyeditor;

import org.mrs.Location;
import org.mrs.api.LocationService;
import org.springframework.beans.factory.annotation.Autowired;

public class LocationEditorTest extends BasePropertyEditorTest<Location, LocationEditor> {

	private static final Integer EXISTING_ID = 1;

	@Autowired
	private LocationService locationService;
	
	@Override
	protected LocationEditor getNewEditor() {
		return new LocationEditor();
	}
	
	@Override
	protected Location getExistingObject() {
		return locationService.getLocation(EXISTING_ID);
	}
}

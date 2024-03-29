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

import java.beans.PropertyEditor;

import org.mrs.Visit;
import org.mrs.api.context.Context;

/**
 * {@link PropertyEditor} for {@link Visit}
 *
 * @since 1.9
 */
public class VisitEditor extends OpenmrsPropertyEditor<Visit> {
	
	public VisitEditor() {
	}
	
	@Override
	protected Visit getObjectById(Integer id) {
		return Context.getVisitService().getVisit(id);
	}
	
	@Override
	protected Visit getObjectByUuid(String uuid) {
		return Context.getVisitService().getVisitByUuid(uuid);
	}
}

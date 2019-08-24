/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.api.handler;

import java.util.Date;

import org.mrs.ConceptReferenceTerm;
import org.mrs.ConceptReferenceTermMap;
import org.mrs.User;
import org.mrs.annotation.Handler;
import org.mrs.aop.RequiredDataAdvice;

/**
 * This class deals with {@link ConceptReferenceTerm} objects when they are saved via a save* method
 * in an Openmrs Service. This handler is automatically called by the {@link RequiredDataAdvice} AOP
 * class. <br>
 * It sets the termA field for all {@link ConceptReferenceTermMap}s
 *
 * @see RequiredDataHandler
 * @see SaveHandler
 * @see ConceptReferenceTerm
 * @since 1.9
 */
@Handler(supports = ConceptReferenceTerm.class)
public class ConceptReferenceTermSaveHandler implements SaveHandler<ConceptReferenceTerm> {
	
	/**
	 * Sets the concept reference term as the term A for all the {@link ConceptReferenceTermMap}s
	 * added to it.
	 *
	 * @see org.mrs.api.handler.RequiredDataHandler#handle(org.mrs.OpenmrsObject,
	 *      org.mrs.User, java.util.Date, java.lang.String)
	 */
	@Override
	public void handle(ConceptReferenceTerm conceptReferenceTerm, User currentUser, Date currentDate, String other) {
		if (conceptReferenceTerm.getConceptReferenceTermMaps() != null) {
			for (ConceptReferenceTermMap map : conceptReferenceTerm.getConceptReferenceTermMaps()) {
				map.setTermA(conceptReferenceTerm);
			}
		}
	}
}

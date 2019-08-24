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

import org.apache.commons.lang3.StringUtils;
import org.mrs.Concept;
import org.mrs.ConceptAnswer;
import org.mrs.ConceptDescription;
import org.mrs.ConceptMap;
import org.mrs.ConceptName;
import org.mrs.ConceptSet;
import org.mrs.User;
import org.mrs.annotation.Handler;
import org.mrs.aop.RequiredDataAdvice;
import org.mrs.api.context.Context;

/**
 * This class deals with {@link Concept} objects when they are saved via a save* method in an
 * Openmrs Service. This handler is automatically called by the {@link RequiredDataAdvice} AOP
 * class. <br>
 * 
 * @see RequiredDataHandler
 * @see SaveHandler
 * @see Concept
 * @since 1.5
 */
@Handler(supports = Concept.class)
public class ConceptSaveHandler implements SaveHandler<Concept> {
	
	/**
	 * @see org.mrs.api.handler.SaveHandler#handle(org.mrs.OpenmrsObject, org.mrs.User,
	 *      java.util.Date, java.lang.String)
	 */
	@Override
	public void handle(Concept concept, User creator, Date dateCreated, String other) {
		if (concept.getNames() != null) {
			for (ConceptName cn : concept.getNames()) {
				cn.setConcept(concept);
			}
		}
		if (concept.getConceptSets() != null) {
			for (ConceptSet set : concept.getConceptSets()) {
				set.setConceptSet(concept);
			}
		}
		if (concept.getAnswers(true) != null) {
			for (ConceptAnswer ca : concept.getAnswers(true)) {
				ca.setConcept(concept);
			}
		}
		if (concept.getDescriptions() != null) {
			for (ConceptDescription cd : concept.getDescriptions()) {
				if (StringUtils.isBlank(cd.getDescription())) {
					concept.removeDescription(cd);
					continue;
				}
				if (cd.getLocale() == null) {
					cd.setLocale(Context.getLocale());
				}
				cd.setConcept(concept);
			}
		}
		if (concept.getConceptMappings() != null) {
			for (ConceptMap map : concept.getConceptMappings()) {
				map.setConcept(concept);
			}
		}
	}
}

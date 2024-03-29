/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This Annotation class is used to mark test cases generated by the eclipse "behaviortestgenerator"
 * plugin. See http://openmrs.org/wiki/Unit_Testing_with_%40should <br>
 * <br>
 * This allows a unit test to be linked back to a specific behavior being tested on a specific
 * method. The class being tested is implied from the name of the parent test class.
 * 
 * @deprecated as of release 2.2.0 in favor of standard testing best practices, see
 *             https://issues.openmrs.org/browse/TRUNK-5122
 */
@Deprecated
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Verifies {
	
	/**
	 * The text description of what this test will be doing. This is how unit tests are linked back
	 * to a specific "@should" on the (hopefully) many "@shoulds" on a method.
	 * 
	 * @return the text after the "@should" on the method this unit test is testing
	 */
	public String value();
	
	/**
	 * The method name within the class that this unit test is testing.
	 * 
	 * @return the name of the method being tested
	 */
	public String method();
}

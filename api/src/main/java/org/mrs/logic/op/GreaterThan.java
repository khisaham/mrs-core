/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.logic.op;

/**
 * The GreaterThan operator will return result that have a greater value than the operand.<br>
 * <br>
 * Example: <br>
 * - <code>logicService.parse("'CD4 COUNT'").gt(200);</code><br>
 * The above will give us a criteria to get the "CD4 COUNT" observations that has the value numeric
 * more than 200
 * 
 * @see GreaterThanEquals
 * @see LessThan
 * @see LessThanEquals
 */
public class GreaterThan implements ComparisonOperator {
	
	@Override
	public String toString() {
		return "GREATER THAN";
	}
	
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.scheduler.tasks;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a task that writes "Hello World" to a log file.
 * 
 */
public class HelloWorldTask extends AbstractTask {
	
	private static final Logger log = LoggerFactory.getLogger(HelloWorldTask.class);
	
	/**
	 * Public constructor.
	 */
	public HelloWorldTask() {
		log.debug("hello world task created at " + new Date());
	}
	
	@Override
	public void execute() {
		log.debug("executing hello world task");
		super.startExecuting();
	}
	
	@Override
	public void shutdown() {
		log.debug("shutting down hello world task");
		this.stopExecuting();
	}
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.util;

import org.junit.Assert;
import org.junit.Test;
import org.mrs.Person;
import org.mrs.PersonName;
import org.mrs.Provider;

public class ProviderByPersonNameComparatorTest {
	
	/**
	 * @see PersonByNameComparator#comparePersonsByName(org.mrs.Person, org.mrs.Person)
	 */
	@Test
	public void compareProvidersByPersonsName_shouldReturnNegativeIfPersonNameForProvider1ComesBeforeThatOfProvider2()
	        {
		Person person1 = new Person();
		person1.addName(new PersonName("givenName", "middleName", "familyName"));
		Provider provider1 = new Provider();
		provider1.setPerson(person1);
		
		Person person2 = new Person();
		person2.addName(new PersonName("givenName", "middleNamf", "familyName"));
		Provider provider2 = new Provider();
		provider2.setPerson(person2);
		
		int actualValue = new ProviderByPersonNameComparator().compare(provider1, provider2);
		Assert.assertTrue("Expected a negative value but it was: " + actualValue, actualValue < 0);
	}
	
	/**
	 * @see PersonByNameComparator#comparePersonsByName(Person,Person)
	 */
	@Test
	public void compareProvidersByPersonName_shouldReturnPositiveIfPersonNameForProvider1ComesAfterThatOfProvider2()
	        {
		Person person1 = new Person();
		person1.addName(new PersonName("givenNamf", "middleName", "familyName"));
		Provider provider1 = new Provider();
		provider1.setPerson(person1);
		
		Person person2 = new Person();
		person2.addName(new PersonName("givenName", "middleName", "familyName"));
		Provider provider2 = new Provider();
		provider2.setPerson(person2);
		
		int actualValue = new ProviderByPersonNameComparator().compare(provider1, provider2);
		Assert.assertTrue("Expected a positive value but it was: " + actualValue, actualValue > 0);
	}
	
	/**
	 * @see PersonByNameComparator#comparePersonsByName(Person,Person)
	 */
	@Test
	public void compareProvidersByPersonName_shouldReturnZeroIfTheGivenNameMiddleNameAndFamilyNameMatch() {
		Person person1 = new Person();
		person1.addName(new PersonName("givenName", "middleName", "familyName"));
		Provider provider1 = new Provider();
		provider1.setPerson(person1);
		
		Person person2 = new Person();
		person2.addName(new PersonName("givenName", "middleName", "familyName"));
		Provider provider2 = new Provider();
		provider2.setPerson(person2);
		
		int actualValue = new ProviderByPersonNameComparator().compare(provider1, provider2);
		Assert.assertTrue("Expected zero but it was: " + actualValue, actualValue == 0);
	}
	
	@Test
	public void compareProvidersByPersonName_shouldNotFailIfProvider1HasNoAssociatedPerson() {
		Provider provider1 = new Provider();
		
		Person person2 = new Person();
		person2.addName(new PersonName("givenName", "middleName", "familyName"));
		Provider provider2 = new Provider();
		provider2.setPerson(person2);
		
		int actualValue = new ProviderByPersonNameComparator().compare(provider1, provider2);
		Assert.assertTrue("Expected a positive value but it was: " + actualValue, actualValue > 0);
	}
	
	@Test
	public void compareProvidersByPersonName_shouldNotFailIfProvider2HasNoAssociatedPerson() {
		Person person1 = new Person();
		person1.addName(new PersonName("givenName", "middleName", "familyName"));
		Provider provider1 = new Provider();
		provider1.setPerson(person1);
		
		Provider provider2 = new Provider();
		
		int actualValue = new ProviderByPersonNameComparator().compare(provider1, provider2);
		Assert.assertTrue("Expected a negative value but it was: " + actualValue, actualValue < 0);
	}
	
	@Test
	public void compareProvidersByPersonName_shouldNotFailIfNeitherProviderHasAnAssociatedPerson() {
		
		Provider provider1 = new Provider();
		Provider provider2 = new Provider();
		
		int actualValue = new ProviderByPersonNameComparator().compare(provider1, provider2);
		Assert.assertTrue("Expected zero but it was: " + actualValue, actualValue == 0);
	}
}

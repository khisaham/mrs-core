/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.obs;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mrs.Obs;
import org.mrs.api.AdministrationService;
import org.mrs.api.context.Context;
import org.mrs.obs.handler.AbstractHandler;
import org.mrs.obs.handler.TextHandler;
import org.mrs.util.OpenmrsUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractHandler.class, OpenmrsUtil.class, Context.class })

public class TextHandlerTest {
	
	@Mock
	private AdministrationService administrationService;
	
    @Rule
    public TemporaryFolder complexObsTestFolder = new TemporaryFolder();

    @Test
    public void shouldReturnSupportedViews() {
        TextHandler handler = new TextHandler();
        String[] actualViews = handler.getSupportedViews();
        String[] expectedViews = { ComplexObsHandler.TEXT_VIEW, ComplexObsHandler.RAW_VIEW, ComplexObsHandler.URI_VIEW };

        assertArrayEquals(actualViews, expectedViews);
    }

    @Test
    public void shouldSupportRawView() {
        TextHandler handler = new TextHandler();

        assertTrue(handler.supportsView(ComplexObsHandler.RAW_VIEW));
        assertTrue(handler.supportsView(ComplexObsHandler.TEXT_VIEW));
        assertTrue(handler.supportsView(ComplexObsHandler.URI_VIEW));
    }

    @Test
    public void shouldNotSupportOtherViews() {
        TextHandler handler = new TextHandler();

        assertFalse(handler.supportsView(ComplexObsHandler.HTML_VIEW));
        assertFalse(handler.supportsView(ComplexObsHandler.PREVIEW_VIEW));
        assertFalse(handler.supportsView(ComplexObsHandler.TITLE_VIEW));
        assertFalse(handler.supportsView(""));
        assertFalse(handler.supportsView(null));
    }
    
	@Test
	public void saveObs_shouldRetrieveCorrectMimetype() throws IOException {
		final String mimetype = "text/plain";
		String filename = "TestingComplexObsSaving.txt";
		String content = "Teststring";
		
		ComplexData complexData = new ComplexData(filename, content);
		
		// Construct 2 Obs to also cover the case where the filename exists already
		Obs obs1 = new Obs();
		obs1.setComplexData(complexData);
		
		Obs obs2 = new Obs();
		obs2.setComplexData(complexData);
		
		// Mocked methods
		mockStatic(Context.class);
		when(Context.getAdministrationService()).thenReturn(administrationService);
		when(administrationService.getGlobalProperty(any())).thenReturn(complexObsTestFolder.newFolder().getAbsolutePath());
		
		TextHandler handler = new TextHandler();
		
		// Execute save
		handler.saveObs(obs1);
		handler.saveObs(obs2);
		
		// Get observation
		Obs complexObs1 = handler.getObs(obs1, "RAW_VIEW");
		Obs complexObs2 = handler.getObs(obs2, "RAW_VIEW");

		assertEquals(complexObs1.getComplexData().getMimeType(), mimetype);
		assertEquals(complexObs2.getComplexData().getMimeType(), mimetype);
	}

}

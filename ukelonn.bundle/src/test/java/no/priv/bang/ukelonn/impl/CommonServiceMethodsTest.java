/*
 * Copyright 2016-2018 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package no.priv.bang.ukelonn.impl;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.mocks.MockLogService;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;

public class CommonServiceMethodsTest {

    @Before
    public void setup() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        releaseFakeOsgiServices();
    }

    @AfterClass
    static public void completeCleanup() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        releaseFakeOsgiServices();
    }

    @Test(expected=RuntimeException.class)
    public void testConnectionCheckNoConnection() {
        UkelonnService service = CommonServiceMethods.connectionCheck(null, getClass());
        assertNull(service);
    }

    @Test
    public void testConnectionCheck() {
        setupFakeOsgiServices();
        UkelonnService service = CommonServiceMethods.connectionCheck(getUkelonnServlet().getUkelonnUIProvider(), getClass());
        assertNotNull(service);
    }

    @Test
    public void testLogError() {
        // First log when there are no services available
        MockLogService logservice = new MockLogService();
        CommonServiceMethods.logError(new UkelonnUIProvider(), getClass(), "This is an error");
        assertEquals("Expect nothing to be logged", 0, logservice.getLogmessages().size());

        // Test the case where there is an UkelonnService but is no logservice
        setupFakeOsgiServices();
        CommonServiceMethods.logError(getUkelonnServlet().getUkelonnUIProvider(), getClass(), "This is another error");
        assertEquals("Still expected nothing logged", 0, logservice.getLogmessages().size());

        // Test the case where there is an UkelonnService with an injected logservice
        UkelonnUIProvider ukelonnService = getUkelonnServlet().getUkelonnUIProvider();
        ukelonnService.setLogservice(logservice);
        CommonServiceMethods.logError(getUkelonnServlet().getUkelonnUIProvider(), getClass(), "This is yet another error");
        assertEquals("Expected a single message to have been logged", 1, logservice.getLogmessages().size());
    }

    @Test
    public void testLogErrorWithException() {
        // First log when there are no services available
        Exception exception = new Exception("This is a fake exception");
        MockLogService logservice = new MockLogService();
        CommonServiceMethods.logError(new UkelonnUIProvider(), getClass(), "This is an error", exception);
        assertEquals("Expect nothing to be logged", 0, logservice.getLogmessages().size());

        // Test the case where there is an UkelonnService but is no logservice
        setupFakeOsgiServices();
        CommonServiceMethods.logError(getUkelonnServlet().getUkelonnUIProvider(), getClass(), "This is another error", exception);
        assertEquals("Still expected nothing logged", 0, logservice.getLogmessages().size());

        // Test the case where there is an UkelonnService with an injected logservice
        UkelonnUIProvider ukelonnService = getUkelonnServlet().getUkelonnUIProvider();
        ukelonnService.setLogservice(logservice);
        CommonServiceMethods.logError(getUkelonnServlet().getUkelonnUIProvider(), getClass(), "This is yet another error", exception);
        assertEquals("Expected a single message to have been logged", 1, logservice.getLogmessages().size());
    }

}

/*
 * Copyright 2018 Steinar Bang
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
package no.priv.bang.ukelonn.api;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import no.priv.bang.ukelonn.api.JobtypeListServlet;
import no.priv.bang.ukelonn.beans.TransactionType;
import no.priv.bang.ukelonn.mocks.MockHttpServletResponse;
import no.priv.bang.ukelonn.mocks.MockLogService;

public class JobtypeListServletTest extends ServletTestBase {

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    @Test
    public void testGetJobtypes() throws Exception {
        // Set up the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/api/jobtypes");
        when(request.getContextPath()).thenReturn("/api/jobtypes");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create the servlet that is to be tested
        JobtypeListServlet servlet = new JobtypeListServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);

        // Inject fake OSGi service UkelonnService
        servlet.setUkelonnService(getUkelonnServiceSingleton());

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        List<TransactionType> jobtypes = JobtypeListServlet.mapper.readValue(response.getOutput().toByteArray(), new TypeReference<List<TransactionType>>() {});
        assertEquals(4, jobtypes.size());
    }

    /**
     * Test the behaviour for internal server error.
     *
     * To provoke the error, the UkelonnService reference was
     * kept to null (ie. no simulated UkelonnService injection).
     *
     * Note: since the JobtypesListServlet component won't be
     * active until it receives an UkelonnService injection,
     * this can't happen in a production setting (if the servlet
     * isn't active it won't be plugged into the whiteboard and
     * receive web requests).
     *
     * @throws Exception
     */
    @Test
    public void testGetJobtypesInternalServerError() throws Exception {
        // Set up the request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/api/jobtypes");
        when(request.getContextPath()).thenReturn("/api/jobtypes");

        // Create a response object that will receive and hold the servlet output
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        // Create the servlet that is to be tested
        JobtypeListServlet servlet = new JobtypeListServlet();

        // Create mock OSGi services to inject and inject it
        MockLogService logservice = new MockLogService();
        servlet.setLogservice(logservice);

        // Call the method under test
        servlet.service(request, response);

        // Check the output
        assertEquals(500, response.getStatus());
    }

}

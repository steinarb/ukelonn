/*
 * Copyright 2016-2017 Steinar Bang
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
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

import no.priv.bang.ukelonn.mocks.MockHttpServletResponse;
import no.priv.bang.ukelonn.mocks.MockLogService;

import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

public class UkelonnServletTest {

    @Test
    public void testGet() throws Exception {
        MockLogService logservice = new MockLogService();
        UkelonnServlet servlet = new UkelonnServlet();
        ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getInitParameter("from")).thenReturn("to");
        servlet.init(servletConfig);
        servlet.setLogService(logservice);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/");
        when(request.getPathInfo()).thenReturn("/");
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        servlet.doGet(request, response);

        assertEquals("text/html", response.getContentType());
        assertEquals(200, response.getStatus());
        assertThat(response.getOutput().size()).isGreaterThan(0);
    }


    @Test
    public void testDoGetAddTrailingSlash() throws Exception {
        MockLogService logservice = new MockLogService();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn");
        when(request.getServletPath()).thenReturn("/frontend-karaf-demo");
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        UkelonnServlet servlet = new UkelonnServlet();
        servlet.setLogService(logservice);

        servlet.doGet(request, response);

        assertEquals(301, response.getStatus());
    }

    @Test
    public void testDoGetResponseThrowsIOException() throws Exception {
        MockLogService logservice = new MockLogService();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/");
        when(request.getPathInfo()).thenReturn("/");
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);
        ServletOutputStream streamThrowingIOException = mock(ServletOutputStream.class);
        doThrow(IOException.class).when(streamThrowingIOException).write(anyInt());
        when(response.getOutputStream()).thenReturn(streamThrowingIOException);

        UkelonnServlet servlet = new UkelonnServlet();
        servlet.setLogService(logservice);

        servlet.doGet(request, response);

        assertEquals(500, response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDoGetResponseStreamMethodThrowsIOException() throws Exception {
        MockLogService logservice = new MockLogService();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/");
        when(request.getPathInfo()).thenReturn("/");
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);
        when(response.getOutputStream()).thenThrow(IOException.class);

        UkelonnServlet servlet = new UkelonnServlet();
        servlet.setLogService(logservice);

        servlet.doGet(request, response);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testDoGetResourceNotFound() throws Exception {
        MockLogService logservice = new MockLogService();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/static/nosuchname.png");
        when(request.getPathInfo()).thenReturn("/static/nosuchname.png");
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);

        UkelonnServlet servlet = new UkelonnServlet();
        servlet.setLogService(logservice);

        servlet.doGet(request, response);

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testGuessContentTypeFromResourceName() {
        UkelonnServlet servlet = new UkelonnServlet();
        assertEquals("text/html", servlet.guessContentTypeFromResourceName("/index.html"));
        assertEquals("application/javascript", servlet.guessContentTypeFromResourceName("/bundle.js"));
        assertEquals("text/css", servlet.guessContentTypeFromResourceName("/bundle.css"));
        assertNull(servlet.guessContentTypeFromResourceName("/bundle.nomatch"));
    }

}

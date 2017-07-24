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
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UkelonnServletTest {

    @Test
    public void testRender() throws IOException, ServletException {
        UkelonnServlet servlet = new UkelonnServlet("/ukelonn");
        ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getInitParameter("from")).thenReturn("to");
        servlet.init(servletConfig);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletPath()).thenReturn("/ukelonn");
        StringWriter responseContent = new StringWriter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(responseContent));

        servlet.doGet(request, response);

        System.out.println("html: " + responseContent.toString());
        assertNotEquals(0, responseContent.toString().length());
    }

}

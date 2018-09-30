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
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.subject.WebSubject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.priv.bang.ukelonn.api.beans.LoginCredentials;

public class ServletTestBase {
    public static final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public ServletTestBase() {
        super();
    }

    protected WebSubject createSubjectAndBindItToThread(HttpServletRequest request, HttpServletResponse response) {
        WebSubject subject = new WebSubject.Builder(getShirofilter().getSecurityManager(), request, response).buildWebSubject();
        ThreadContext.bind(subject);
        return subject;
    }

    protected void loginUser(HttpServletRequest request, HttpServletResponse response, String username, String password) {
        WebSubject subject = createSubjectAndBindItToThread(request, response);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray(), true);
        subject.login(token);
    }

    protected HttpServletRequest buildLoginRequest(LoginCredentials credentials) throws JsonProcessingException, IOException {
        String credentialsAsJson = ServletTestBase.mapper.writeValueAsString(credentials);
        return buildRequestFromStringBody(credentialsAsJson);
    }

    protected HttpServletRequest buildGetRequest() throws IOException {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        when(request.getSession()).thenReturn(session);
        return request;
    }

    protected HttpServletRequest buildRequestFromStringBody(String textToSendAsBody) throws IOException {
        ServletInputStream postBody = wrap(new ByteArrayInputStream(textToSendAsBody.getBytes(StandardCharsets.UTF_8)));
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/ukelonn/api/login"));
        when(request.getRequestURI()).thenReturn("/ukelonn/api/login");
        when(request.getContextPath()).thenReturn("/ukelonn");
        when(request.getServletPath()).thenReturn("/api");
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Arrays.asList("Content-Type")));
        when(request.getHeaders(eq("Content-Type"))).thenReturn(Collections.enumeration(Arrays.asList("application/json")));
        when(request.getInputStream()).thenReturn(postBody);
        when(request.getSession()).thenReturn(session);
        return request;
    }

    private ServletInputStream wrap(InputStream inputStream) {
        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean isReady() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isFinished() {
                // TODO Auto-generated method stub
                return false;
            }
        };
    }

}

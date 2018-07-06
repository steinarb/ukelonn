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

import static no.priv.bang.ukelonn.testutils.TestUtils.getShirofilter;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.subject.WebSubject;

import com.fasterxml.jackson.core.JsonProcessingException;

import no.priv.bang.ukelonn.api.beans.LoginCredentials;
import no.priv.bang.ukelonn.mocks.MockHttpServletResponse;

public class ServletTestBase {

    public ServletTestBase() {
        super();
    }

    protected WebSubject createSubjectAndBindItToThread(HttpServletRequest request, MockHttpServletResponse response) {
        WebSubject subject = new WebSubject.Builder(getShirofilter().getSecurityManager(), request, response).buildWebSubject();
        ThreadContext.bind(subject);
        return subject;
    }

    protected void loginUser(HttpServletRequest request, MockHttpServletResponse response, String username, String password) {
        WebSubject subject = createSubjectAndBindItToThread(request, response);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray(), true);
        subject.login(token);
    }

    protected HttpServletRequest buildLoginRequest(LoginCredentials credentials) throws JsonProcessingException, IOException {
        String credentialsAsJson = LoginServlet.mapper.writeValueAsString(credentials);
        return buildRequestFromStringBody(credentialsAsJson);
    }

    protected HttpServletRequest buildRequestFromStringBody(String textToSendAsBody) throws IOException {
        ServletInputStream postBody = wrap(new ByteArrayInputStream(textToSendAsBody.getBytes()));
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/ukelonn/api/login");
        when(request.getPathInfo()).thenReturn("/api/login");
        when(request.getAttribute(anyString())).thenReturn("");
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

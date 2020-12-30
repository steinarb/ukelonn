/*
 * Copyright 2018-2020 Steinar Bang
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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.subject.WebSubject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletOutputStream;

public class ServletTestBase {
    private String baseURL = "http://localhost:8181";
    private String contextPath = "/ukelonn";
    private String servletPath = "/api";

    public ServletTestBase(String baseURL, String contextPath, String servletPath) {
        this.baseURL = baseURL;
        this.contextPath = contextPath;
        this.servletPath = servletPath;
    }

    public ServletTestBase(String contextPath, String servletPath) {
        this("http://localhost:8181", contextPath, servletPath);
    }

    public static final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    protected byte[] getBinaryContent(MockHttpServletResponse response) throws IOException {
        MockServletOutputStream outputstream = (MockServletOutputStream) response.getOutputStream();
        return outputstream.getBinaryContent();
    }

    protected MockHttpServletRequest buildGetUrl(String resource) {
        MockHttpServletRequest request = buildRequest(resource);
        request.setMethod("GET");
        return request;
    }

    protected MockHttpServletRequest buildPostUrl(String resource) throws Exception {
        String contenttype = MediaType.APPLICATION_JSON;
        MockHttpServletRequest request = buildRequest(resource);
        request.setMethod("POST");
        request.setContentType(contenttype);
        request.addHeader("Content-Type", contenttype);
        request.setCharacterEncoding("UTF-8");
        return request;
    }

    protected void loginUser(HttpServletRequest request, HttpServletResponse response, String username, String password) {
        WebSubject subject = createSubjectAndBindItToThread(request, response);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray(), true);
        subject.login(token);
    }

    protected WebSubject createSubjectAndBindItToThread(HttpServletRequest request, HttpServletResponse response) {
        WebSubject subject = new WebSubject.Builder(getSecurityManager(), request, response).buildWebSubject();
        ThreadContext.bind(subject);
        return subject;
    }

    protected WebSubject createSubjectWithNullPrincipalAndBindItToThread() {
        WebSubject subject = mock(WebSubject.class);
        ThreadContext.bind(subject);
        return subject;
    }

    private MockHttpServletRequest buildRequest(String resource) {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setProtocol("HTTP/1.1");
        request.setRequestURL(buildFullURL(resource));
        request.setRequestURI(buildURI(resource));
        request.setContextPath(contextPath);
        request.setServletPath(servletPath);
        request.setSession(session);
        return request;
    }

    String buildURI(String resource) {
        return removeDriveLetter(Paths.get(contextPath, servletPath, resource).toUri().getPath());
    }

    String removeDriveLetter(String path) {
        if (path.matches("^/[A-Za-z]:.*")) {
            return path.replaceAll("^/[A-Za-z]:", "");
        }

        return path;
    }

    String buildFullURL(String resource) {
        return baseURL + buildURI(resource);
    }

}

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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.log.LogService;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.priv.bang.ukelonn.api.beans.LoginCredentials;
import no.priv.bang.ukelonn.api.beans.LoginResult;

@Component(
    property= {
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN+"=/api/login",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +"=ukelonn)",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME+"=login"},
    service=Servlet.class,
    immediate=true
)
public class LoginServlet extends HttpServlet {
    public static final ObjectMapper mapper = new ObjectMapper();
    private static final long serialVersionUID = 5271925508152009445L;
    LogService logservice = null; // NOSONAR Not touched after activate, in practice a constant

    @Reference
    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Subject subject = SecurityUtils.getSubject();
            Object result = createLoginResultFromSubject(subject);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            try(PrintWriter responseBody = response.getWriter()) { // NOSONAR IOException caught by enclosing try/catch
                mapper.writeValue(responseBody, result); // NOSONAR IOException caught by enclosing try/catch
            }
        } catch (Exception e) {
            // Never throw exception, log underlying error and return error code
            logservice.log(LogService.LOG_ERROR, "Login REST API call failed", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LoginCredentials credentials = null;
        try {
            try(ServletInputStream postBody = request.getInputStream()) { // NOSONAR Can't put this in a nested method because there will be no way to return a 400 response
                credentials = mapper.readValue(postBody, LoginCredentials.class);
            } catch (Exception e) {
                // Log parse error and return a 400 response
                logservice.log(LogService.LOG_WARNING, "Login REST API: Unable to parse the POSTed credentials", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unable to parse the POSTed credentials");
                return;
            }

            LoginResult result = doLogin(credentials);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            try(PrintWriter responseBody = response.getWriter()) { // NOSONAR IOException caught by enclosing try/catch
                mapper.writeValue(responseBody, result); // NOSONAR IOException caught by enclosing try/catch
            }
        } catch (Exception e) {
            // Never throw exception, log underlying error and return error code
            logservice.log(LogService.LOG_ERROR, "Login REST API call failed", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    LoginResult doLogin(LoginCredentials credentials) {
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(credentials.getUsername(), credentials.getPassword().toCharArray(), true);
        try {
            subject.login(token);

            return createLoginResultFromSubject(subject);
        } catch(UnknownAccountException e) {
            logservice.log(LogService.LOG_WARNING, "Login error: unknown account", e);
            return new LoginResult("Unknown account");
        } catch (IncorrectCredentialsException  e) {
            logservice.log(LogService.LOG_WARNING, "Login error: wrong password", e);
            return new LoginResult("Wrong password");
        } catch (LockedAccountException  e) {
            logservice.log(LogService.LOG_WARNING, "Login error: locked account", e);
            return new LoginResult("Locked account");
        } catch (AuthenticationException e) {
            logservice.log(LogService.LOG_WARNING, "Login error: unknown error", e);
            return new LoginResult("Unknown error");
        } finally {
            token.clear();
        }
    }

    private LoginResult createLoginResultFromSubject(Subject subject) {
        if (subject.isAuthenticated()) {
            String[] roles = { "user" };
            if (subject.hasRole("administrator")) {
                roles =  new String[]{ "administrator" };
            }

            return new LoginResult(roles);
        }

        return new LoginResult();
    }

}

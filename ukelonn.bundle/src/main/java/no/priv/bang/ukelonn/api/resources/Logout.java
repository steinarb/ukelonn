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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.log.LogService;

@Component(
    property= {
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN+"=/api/logout",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +"=ukelonn)",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME+"=logout"},
    service=Servlet.class,
    immediate=true
)
public class LogoutServlet extends HttpServlet {
    private static final String LOGIN_RESULT_NO_ROLES_NO_ERROR = "{ \"roles\": [], \"errorMessage\": \"\"}";
    private static final long serialVersionUID = 7499435937342301725L;
    LogService logservice = null; // NOSONAR Not touched after activate, in practice a constant

    @Reference
    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            doLogout();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            try(PrintWriter responseBody = response.getWriter()) { // NOSONAR IOException caught by enclosing try/catch
                responseBody.print(LOGIN_RESULT_NO_ROLES_NO_ERROR);
            }
        } catch (Exception e) {
            // Never throw exception, log underlying error and return error code
            logservice.log(LogService.LOG_ERROR, "Logout REST API call failed", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    void doLogout() {
        Subject subject = SecurityUtils.getSubject();

        subject.logout();
    }

}

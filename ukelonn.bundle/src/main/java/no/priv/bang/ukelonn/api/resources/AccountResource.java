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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.beans.Account;

@Component(
    property= {
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN+"=/api/account/*",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +"=ukelonn)",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME+"=account"},
    service=Servlet.class,
    immediate=true
)
public class AccountServlet extends ApiServletBase {
    private static final long serialVersionUID = 8695977326010320196L;

    @Activate
    public void activate() {
        // Nothing to do here yet
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            String username = request.getPathInfo().startsWith("/") ? request.getPathInfo().substring(1) : request.getPathInfo();
            if (!isCurrentUserOrAdmin(username)) {
                logservice.log(LogService.LOG_WARNING, String.format("Servlet /ukelonn/api/account logged in user not allowed to fetch account for username %s", username));
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            Account account = ukelonn.getAccount(username);
            try(PrintWriter responseBody = response.getWriter()) {
                mapper.writeValue(responseBody, account);
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // NOSONAR Handling if the framework fails when sending an error seems out of scope
            logservice.log(LogService.LOG_ERROR, "Servlet /ukelonn/api/account failed on GET with exception", e);
        }
    }

}

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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.PerformedJob;

@Component(
    property= {
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN+"=/api/registerjob",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +"=ukelonn)",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME+"=registerjob"},
    service=Servlet.class,
    immediate=true
)
public class RegisterJobServlet extends ApiServletBase {
    private static final long serialVersionUID = 8179787419173107177L;

    @Activate
    public void activate() {
        // Nothing to do here yet
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            PerformedJob performedJob;
            try(ServletInputStream postBody = request.getInputStream()) { // NOSONAR Can't put this in a nested method because there will be no way to return a 400 response
                performedJob = mapper.readValue(postBody, PerformedJob.class);
            } catch (Exception e) {
                // Log parse error and return a 400 response
                logservice.log(LogService.LOG_WARNING, "Login REST API: Unable to parse the POSTed credentials", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unable to parse the POSTed credentials");
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            String username = performedJob.getAccount().getUsername();
            if (!isCurrentUserOrAdmin(username)) {
                logservice.log(LogService.LOG_WARNING, String.format("Servlet /ukelonn/api/account logged in user not allowed to fetch account for username %s", username));
                response.sendError(HttpServletResponse.SC_FORBIDDEN); // NOSONAR Handled by outer try/catch
                return;
            }

            Account account = ukelonn.registerPerformedJob(performedJob);
            try(PrintWriter responseBody = response.getWriter()) { // NOSONAR Handled by outer try/catch
                mapper.writeValue(responseBody, account); // NOSONAR Handled by outer try/catch
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // NOSONAR Handling if the framework fails when sending an error seems out of scope
            logservice.log(LogService.LOG_ERROR, "Servlet /ukelonn/api/account failed on GET with exception", e);
        }
    }

}

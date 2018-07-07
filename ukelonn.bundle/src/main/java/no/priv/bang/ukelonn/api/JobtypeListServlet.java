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
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.beans.TransactionType;

@Component(
    property= {
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN+"=/api/jobtypes",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +"=ukelonn)",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME+"=jobtypes"},
    service=Servlet.class,
    immediate=true
)
public class JobtypeListServlet extends ApiServletBase {
    private static final long serialVersionUID = 3986120583135460006L;

    @Activate
    public void activate() {
        // Nothing to do here yet
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            List<TransactionType> jobtypes = ukelonn.getJobTypes();
            try(PrintWriter responseBody = response.getWriter()) {
                mapper.writeValue(responseBody, jobtypes);
            }
        } catch (Exception e) {
            logservice.log(LogService.LOG_ERROR, "Servlet /ukelonn/api/joblist failed on GET with exception", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // NOSONAR Handling if the framework fails when sending an error seems out of scope
        }
    }

}

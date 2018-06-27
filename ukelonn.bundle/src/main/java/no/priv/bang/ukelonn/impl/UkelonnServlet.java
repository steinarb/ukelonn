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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.rendersnake.HtmlAttributesFactory.*;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;
import org.rendersnake.ext.servlet.HtmlServletCanvas;

@Component(service=Servlet.class, property={"alias=/ukelonn"}, immediate=true)
public class UkelonnServlet extends HttpServlet {
    private static final long serialVersionUID = -3496606785818930881L;
    private LogService logservice; // NOSONAR This is not touched after DS component activate so effectively a constant

    @Reference
    public void setLogService(LogService logservice) {
        this.logservice = logservice;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        try (PrintWriter responseWriter = response.getWriter()) {
            HtmlServletCanvas html = new HtmlServletCanvas(request, response, responseWriter);
            html
                .html()
                .head().title().content("Ukelønn")._head()
                .body(align("center"))
                .h1().content("Ukelønn")
                .img(src("/images/logo.png").border("0"))
                .h1().content(getServletConfig().getInitParameter("from"))
                .p()
                .write("Served by servlet registered at: /ukelonn").br()
                .write("Servlet Path: " + request.getServletPath()).br()
                .write("Path Info: " + request.getPathInfo())
                ._p()
                ._body()
                ._html();
        } catch(Exception e) {
            logservice.log(LogService.LOG_ERROR, "Failed to write HTML to the response", e);
            response.setStatus(500);
        }
    }

}

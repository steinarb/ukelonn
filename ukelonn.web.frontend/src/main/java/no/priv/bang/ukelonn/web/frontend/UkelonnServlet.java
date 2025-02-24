/*
 * Copyright 2016-2025 Steinar Bang
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
package no.priv.bang.ukelonn.web.frontend;

import javax.servlet.Servlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletName;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;

import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.osgi.service.log.LogService;

import no.priv.bang.servlet.frontend.FrontendServlet;
import no.priv.bang.ukelonn.UkelonnException;

@Component(service=Servlet.class, immediate=true)
@HttpWhiteboardContextSelect("(" + HTTP_WHITEBOARD_CONTEXT_NAME + "=ukelonn)")
@HttpWhiteboardServletName("ukelonn")
@HttpWhiteboardServletPattern("/*")
public class UkelonnServlet extends FrontendServlet {
    private static final long serialVersionUID = -3496606785818930881L;

    public UkelonnServlet() {
        super();
        // The paths used by the react router
        setRoutes(readLinesFromClasspath("assets/routes.txt"));
    }

    @Override
    @Reference
    public void setLogService(LogService logservice) {
        super.setLogService(logservice);
    }

    String[] readLinesFromClasspath(String fileName) {
        try (var reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(fileName)))) {
            var lines = reader.lines().toList();
            return lines.toArray(new String[0]);
        } catch (Exception e) {
            throw new UkelonnException("Failed to read routes list from classpath resource", e);
        }
    }
}

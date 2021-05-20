/*
 * Copyright 2018-2021 Steinar Bang
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

import javax.servlet.Servlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletName;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;

import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.*;
import org.osgi.service.log.LogService;

import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.servlet.jersey.JerseyServlet;
import no.priv.bang.ukelonn.UkelonnService;

@Component(service=Servlet.class, immediate=true)
@HttpWhiteboardContextSelect("(" + HTTP_WHITEBOARD_CONTEXT_NAME + "=ukelonn)")
@HttpWhiteboardServletName("ukelonnapi")
@HttpWhiteboardServletPattern("/api/*")
@SuppressWarnings("serial")
public class UkelonnRestApiServlet extends JerseyServlet {

    @Reference
    public void setUkelonnService(UkelonnService ukelonnService) {
        addInjectedOsgiService(UkelonnService.class, ukelonnService);
    }

    @Reference
    public void setUserManagement(UserManagementService useradmin) {
        addInjectedOsgiService(UserManagementService.class, useradmin);
    }

    @Reference
    @Override
    public void setLogService(LogService logservice) {
        super.setLogService(logservice);
    }

}

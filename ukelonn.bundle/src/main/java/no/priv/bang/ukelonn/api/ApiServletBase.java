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

import javax.servlet.http.HttpServlet;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.priv.bang.ukelonn.UkelonnService;

@SuppressWarnings("serial")
public abstract class ApiServletBase extends HttpServlet {

    public static final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    protected LogService logservice; // NOSONAR Injected OSGi services are in practice constants even if they can't be defined like that
    protected UkelonnService ukelonn; // NOSONAR Injected OSGi services are in practice constants even if they can't be defined like that

    @Reference
    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
    }

    @Reference
    public void setUkelonnService(UkelonnService ukelonn) {
        this.ukelonn = ukelonn;
    }

    protected boolean isCurrentUserOrAdmin(String username) {
        Subject subject = SecurityUtils.getSubject();
        return
            subject.getPrincipal().equals(username) ||
            subject.hasRole("administrator");
    }

}

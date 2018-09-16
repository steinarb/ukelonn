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

import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.WebConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.UkelonnService;

@Component(
        property= {
            HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN+"=/api/*",
            HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +"=ukelonn)",
            HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME+"=ukelonnapi",
            HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX+ServerProperties.PROVIDER_PACKAGES+"=no.priv.bang.ukelonn.api.resources"},
        service=Servlet.class,
        immediate=true
    )
@SuppressWarnings("serial")
public class UkelonnRestApiServlet extends ServletContainer {

    private UkelonnService ukelonnService; // NOSONAR In an OSGi DS component injected dependencies are set before activation and is effectively a constant
    private LogService logservice; // NOSONAR In an OSGi DS component injected dependencies are set before activation and is effectively a constant

    @Activate
    public void activate() {
        logservice.log(LogService.LOG_INFO, String.format("Ukelonn Jersey servlet activated with UkelonnService %s", ukelonnService.toString()));
    }

    @Override
    protected void init(WebConfig webConfig) throws ServletException {
        super.init(webConfig);
        ResourceConfig copyOfExistingConfig = new ResourceConfig(getConfiguration());
        copyOfExistingConfig.register(new AbstractBinder() {
                @Override
                protected void configure() {
                    bind(logservice).to(LogService.class);
                    bind(ukelonnService).to(UkelonnService.class);
                }
            });
        reload(copyOfExistingConfig);
        Map<String, Object> configProperties = getConfiguration().getProperties();
        Set<Class<?>> classes = getConfiguration().getClasses();
        logservice.log(LogService.LOG_INFO, String.format("Ukelonn Jersey servlet initialized with WebConfig, with resources: %s  and config params: %s", classes.toString(), configProperties.toString()));
    }

    @Reference
    public void setUkelonnService(UkelonnService ukelonnService) {
        this.ukelonnService = ukelonnService;

    }

    @Reference
    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
    }

}

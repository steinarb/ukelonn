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

import java.util.Dictionary;
import java.util.Hashtable;

import org.ops4j.pax.web.service.WebContainer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnService;

/**
 * The OSGi component that listens for a {@link WebContainer} service
 * and registers a servlet with the web container.
 *
 * @author Steinar Bang
 *
 */
@Component(service=UkelonnService.class, immediate=true)
public class UkelonnServiceProvider extends UkelonnServiceBase {
    private WebContainer webContainer;
    private UkelonnDatabase database;
    private LogService logservice;

    @Activate
    public void activate() {
        if (webContainer != null ) {
            final HttpContext httpContext = webContainer.createDefaultHttpContext();
            if (httpContext != null) {
                final Dictionary<String, Object> initParams = new Hashtable<>(); // NOSONAR Can't switch to HashMap because the API wants Dictionary
                initParams.put("from", "HttpService");
                final String registrationPath = "/ukelonn"; // NOSONAR I don't want to customize this URL
                try {
                    UkelonnServlet servlet = new UkelonnServlet(registrationPath);
                    servlet.setLogService(logservice);
                    webContainer.registerServlet(registrationPath, servlet, initParams, httpContext);
                    // register images as resources
                    webContainer.registerResources("/images", "/images", httpContext);
                } catch (Exception e) {
                    logservice.log(LogService.LOG_ERROR, "Failed to registee the Ukelonn servlet", e);
                }
            }
        }
    }

    @Reference
    public void setUkelonnDatabase(UkelonnDatabase database) {
        this.database = database;
    }

    @Override
    public UkelonnDatabase getDatabase() {
        return database;
    }

    @Reference
    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
    }

    @Override
    public LogService getLogservice() {
        return logservice;
    }

    @Reference
    public void setWebContainer(WebContainer webcontainer) {
        webContainer = webcontainer;
    }

    public UkelonnService get() {
        return this;
    }

}

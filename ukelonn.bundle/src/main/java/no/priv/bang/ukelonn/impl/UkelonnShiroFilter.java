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

import javax.servlet.Filter;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.ops4j.pax.web.service.WebContainer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

import no.priv.bang.ukelonn.UkelonnDatabase;

/**
 * This is an OSGi DS component that provides a {@link Filter} service.  This filter service will
 * be put in front of the servlet provided by the {@link UkelonnServletProvider}, and
 * will handle authentication and authorization from the servlet.
 *
 * @author Steinar Bang
 *
 */
@Component(
    property= {
        HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN+"=/ukelonn/*",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET+"=ukelonn"},
    service=Filter.class,
    immediate=true
)
public class UkelonnShiroFilter extends ShiroFilter {

    private static UkelonnShiroFilter instance;
    private UkelonnDatabase database;
    private WebContainer webContainer;
    private HttpContext httpcontext;
    private EnvironmentLoaderListener listener;

    public UkelonnShiroFilter() {
        instance = this;
    }

    @Reference
    public void setUkelonnDatabase(UkelonnDatabase database) {
        this.database = database;
    }

    public UkelonnDatabase getDatabase() {
        return database;
    }

    @Reference
    public void setWebContainer(WebContainer webContainer) {
        createEnvironmentLoaderListenerAndDefaultContext(webContainer);
    }

    private void createEnvironmentLoaderListenerAndDefaultContext(WebContainer webContainer) {
        if (this.webContainer == webContainer) {
            return; // already registered, nothing to do
        }

        unregisterExistingEnvironmentLoaderListener();

        this.webContainer = webContainer;

        if (webContainer != null) {
            httpcontext = webContainer.createDefaultHttpContext();
            listener = new EnvironmentLoaderListener();
            webContainer.registerEventListener(listener, httpcontext);
        }
    }

    private void unregisterExistingEnvironmentLoaderListener() {
        if (webContainer != null) {
            webContainer.unregisterEventListener(listener);
            listener = null;
        }
    }

    public static UkelonnShiroFilter getInstance() {
        return instance;
    }

}

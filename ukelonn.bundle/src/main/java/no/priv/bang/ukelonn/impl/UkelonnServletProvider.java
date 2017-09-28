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

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.Servlet;

import org.ops4j.pax.web.extender.whiteboard.ExtenderConstants;
import org.osgi.service.log.LogService;

import com.vaadin.addon.touchkit.server.TouchKitServlet;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnService;
import no.steria.osgi.jsr330activator.Jsr330Activator;
import no.steria.osgi.jsr330activator.ServiceProperties;
import no.steria.osgi.jsr330activator.ServiceProperty;

/**
 * This class will be be picked and instantiated up by the {@link Jsr330Activator} and be presented
 * in OSGi as a {@link Servlet} service.
 *
 * The way it works, is:
 *  1. The Jsr330Activator will start by instantiating this class
 *  2. The Jsr330Activator will then register listeners for the two dependent services (log and database services)
 *  3. When the dependent services become available the Jsr330Activator will call the get() method of this class
 *     to get the servlet instance, which is then registered as an OSGi service, which is picked up by the
 *     pax web whiteboard extender
 *  4. If one or both of the dependent services go away, the servlet instance will be registered as going away
 *     (and will hopefully be removed in the pax web whiteboard extender)
 *  5. If the JsrActivator is stopped (e.g. when unloading the bundle), the Jsr330Activator will retract the
 *     servlet OSGi service, and release its hold on the two injected services
 *
 *  See also: {@link ShiroFilterProvider}
 *
 * @author Steinar Bang
 *
 */
@ServiceProperties({
	@ServiceProperty( name = ExtenderConstants.PROPERTY_URL_PATTERNS, values = {"/ukelonn/*", "/VAADIN/*"}),
	@ServiceProperty( name = ExtenderConstants.PROPERTY_SERVLET_NAMES, value = "ukelonn")})
public class UkelonnServletProvider extends UIProvider implements Provider<Servlet>, UkelonnService {
    private static final long serialVersionUID = -275959896126008712L;
    private static UkelonnServletProvider instance;
    private UkelonnDatabase database;
    private LogService logservice;
    private TouchKitServlet servlet;

    public UkelonnServletProvider() {
        super();
        instance = this;
    }

    @Inject
    public void setUkelonnDatabase(UkelonnDatabase database) {
        this.database = database;
    }

    public UkelonnDatabase getDatabase() {
        return database;
    }

    @Inject
    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
    }

    public LogService getLogservice() {
        return logservice;
    }

    public Servlet get() {
        if (servlet == null) {
            servlet = new UkelonnServlet(this);
        }

        return servlet;
    }

    @Override
    public UI createInstance(UICreateEvent event) {
        return new UkelonnUI(this);
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        return UkelonnUI.class;
    }

    public static UkelonnServletProvider getInstance() {
        return instance;
    }

}

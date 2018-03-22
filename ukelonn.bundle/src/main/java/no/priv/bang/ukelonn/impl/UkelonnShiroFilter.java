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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.ops4j.pax.web.service.WebContainer;
import org.osgi.service.component.annotations.Activate;
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
        "servletNames=ukelonn"},
    service=Filter.class,
    immediate=true
)
public class UkelonnShiroFilter implements Filter {

    private ShiroFilter wrappedShiroFilter;
    private static UkelonnShiroFilter instance;
    private UkelonnDatabase database;
    private WebContainer webContainer;
    private FilterConfig filterConfig;

    public UkelonnShiroFilter() {
        wrappedShiroFilter = new ShiroFilter();
        instance = this;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        if (bothInitAndActivateHasBeenCalled()) {
            runAfterBothInitAndActivate();
        }
    }

    @Activate
    public void activate() throws ServletException {
        if (bothInitAndActivateHasBeenCalled()) {
            runAfterBothInitAndActivate();
        }
    }

    private boolean bothInitAndActivateHasBeenCalled() {
        return (filterConfig != null && webContainer != null);
    }

    private void runAfterBothInitAndActivate() throws ServletException {
        ensureEnvironmentLoaderIsPresentBeforeShiroFilterInit();
        wrappedShiroFilter.init(filterConfig);
    }

    private void ensureEnvironmentLoaderIsPresentBeforeShiroFilterInit() {
    	String httpContextPath = filterConfig.getServletContext().getContextPath();
        HttpContext httpcontext = webContainer.createDefaultHttpContext(httpContextPath);
        EnvironmentLoaderListener listener = new EnvironmentLoaderListener();
        webContainer.registerEventListener(listener, httpcontext);
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
        this.webContainer = webContainer;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        wrappedShiroFilter.doFilter(request, response, chain);
    }

    @Override
    public void destroy() {
        wrappedShiroFilter.destroy();
    }

    public static UkelonnShiroFilter getInstance() {
        return instance;
    }

}

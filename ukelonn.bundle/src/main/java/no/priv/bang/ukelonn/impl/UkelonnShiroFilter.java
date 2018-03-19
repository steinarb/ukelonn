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
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
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
public class UkelonnShiroFilter extends AbstractShiroFilter {

    private static UkelonnShiroFilter instance;
    private UkelonnDatabase database;
    WebSecurityManager securitymanager;
    FilterChainResolver resolver;

    public UkelonnShiroFilter() {
        instance = this;
    }

    @Activate
    public void activate() {
        setSecurityManager(securitymanager);

        if (resolver != null) {
            setFilterChainResolver(resolver);
        }
    }

    @Reference
    public void setUkelonnDatabase(UkelonnDatabase database) {
        this.database = database;
    }

    public UkelonnDatabase getDatabase() {
        return database;
    }

    @Reference
    public void setSecuritymanager(WebSecurityManager securitymanager) {
        this.securitymanager = securitymanager;
    }

    @Reference
    public void setResolver(FilterChainResolver resolver) {
        this.resolver = resolver;
    }

    public static UkelonnShiroFilter getInstance() {
        return instance;
    }

}

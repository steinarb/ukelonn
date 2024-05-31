/*
 * Copyright 2016-2024 Steinar Bang
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
package no.priv.bang.ukelonn.web.security;

import javax.servlet.Filter;

import org.apache.shiro.config.Ini;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.filter.authc.PassThruAuthenticationFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;

import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.*;

/**
 * This is an OSGi DS component that provides a {@link Filter} service.  This filter service will
 * be put in front of the servlets in the "/ukelonn" webcontext, and
 * will handle authentication and verify authorization to the servlet paths.
 *
 * @author Steinar Bang
 *
 */
@Component(service=Filter.class, immediate=true)
@HttpWhiteboardContextSelect("(" + HTTP_WHITEBOARD_CONTEXT_NAME + "=ukelonn)")
@HttpWhiteboardFilterPattern("/*")
public class UkelonnShiroFilter extends AbstractShiroFilter { // NOSONAR

    private Realm realm;
    private SessionDAO session;
    private static final Ini INI_FILE = new Ini();
    static {
        // Can't use the Ini.fromResourcePath(String) method because it can't find "shiro.ini" on the classpath in an OSGi context
        INI_FILE.load(UkelonnShiroFilter.class.getClassLoader().getResourceAsStream("shiro.ini"));
    }

    @Reference
    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    @Reference
    public void setSession(SessionDAO session) {
        this.session = session;
    }

    @Activate
    public void activate() {
        var environment = new IniWebEnvironment();
        environment.getObjects().put("authc", new PassThruAuthenticationFilter());
        environment.setIni(INI_FILE);
        environment.setServletContext(getServletContext());
        environment.init();
        var sessionmanager = new DefaultWebSessionManager();
        sessionmanager.setSessionDAO(session);
        sessionmanager.setSessionIdUrlRewritingEnabled(false);
        var securityManager = DefaultWebSecurityManager.class.cast(environment.getWebSecurityManager());
        securityManager.setSessionManager(sessionmanager);
        securityManager.setRealm(realm);
        setSecurityManager(securityManager);
        setFilterChainResolver(environment.getFilterChainResolver());
    }
}

/*
 * Copyright 2016-2018 Steinar Bang
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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.log.LogService;

import com.vaadin.addon.touchkit.server.TouchKitServlet;
import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

import no.priv.bang.ukelonn.UkelonnDatabase;

@Component(
    property= {
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN+"=/*",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +"=ukelonn)",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME+"=ukelonn"},
    service=Servlet.class,
    immediate=true
)
public class UkelonnServlet extends TouchKitServlet {
    private static final long serialVersionUID = 2305317590355701822L;
    private final UkelonnUIProvider ukelonnUIProvider = new UkelonnUIProvider();

    public UkelonnUIProvider getUkelonnUIProvider() {
        return ukelonnUIProvider;
    }

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        addSessionInitListenerThatWillSetUIProviderOnSession();
    }

    @Reference
    public void setUkelonnDatabase(UkelonnDatabase database) {
        ukelonnUIProvider.setUkelonnDatabase(database);
    }

    @Reference
    public void setLogservice(LogService logservice) {
        ukelonnUIProvider.setLogservice(logservice);
    }

    private void addSessionInitListenerThatWillSetUIProviderOnSession() {
        VaadinServletService service = getService();
        service.addSessionInitListener(new SessionInitListener() {
                private static final long serialVersionUID = -5085594781477821868L;

                @Override
                public void sessionInit(SessionInitEvent sessionInitEvent) throws ServiceException {
                    VaadinSession session = sessionInitEvent.getSession();
                    removeDefaultUIProvider(session);
                    session.addUIProvider(ukelonnUIProvider);
                }

                private void removeDefaultUIProvider(VaadinSession session) {
                    List<UIProvider> uiProviders = new ArrayList<>(session.getUIProviders());
                    for (UIProvider uiProvider : uiProviders) {
                        if (DefaultUIProvider.class.getCanonicalName().equals(uiProvider.getClass().getCanonicalName())) {
                            session.removeUIProvider(uiProvider);
                        }
                    }
                }
            });
    }

}

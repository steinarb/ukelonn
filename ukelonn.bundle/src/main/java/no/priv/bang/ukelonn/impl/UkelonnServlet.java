package no.priv.bang.ukelonn.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import com.vaadin.addon.touchkit.server.TouchKitServlet;
import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

public class UkelonnServlet extends TouchKitServlet {
    private static final long serialVersionUID = 2305317590355701822L;
    private UkelonnServletProvider ukelonnServletProvider;

    public UkelonnServlet(UkelonnServletProvider ukelonnServletProvider) {
        this.ukelonnServletProvider = ukelonnServletProvider;
    }

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        addSessionInitListenerThatWillSetUIProviderOnSession();
    }

    private void addSessionInitListenerThatWillSetUIProviderOnSession() {
        VaadinServletService service = getService();
        service.addSessionInitListener(new SessionInitListener() {
                private static final long serialVersionUID = -5085594781477821868L;

                @Override
                public void sessionInit(SessionInitEvent sessionInitEvent) throws ServiceException {
                    VaadinSession session = sessionInitEvent.getSession();
                    removeDefaultUIProvider(session);
                    session.addUIProvider(ukelonnServletProvider);
                }

                private void removeDefaultUIProvider(VaadinSession session) {
                    List<UIProvider> uiProviders = new ArrayList<UIProvider>(session.getUIProviders());
                    for (UIProvider uiProvider : uiProviders) {
                        if (DefaultUIProvider.class.getCanonicalName().equals(uiProvider.getClass().getCanonicalName())) {
                            session.removeUIProvider(uiProvider);
                        }
                    }
                }
            });
    }

}

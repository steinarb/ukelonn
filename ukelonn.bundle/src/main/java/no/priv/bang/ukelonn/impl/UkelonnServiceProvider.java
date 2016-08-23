package no.priv.bang.ukelonn.impl;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletException;

import org.ops4j.pax.web.service.WebContainer;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.NamespaceException;

import no.priv.bang.ukelonn.UkelonnService;
import no.steria.osgi.jsr330activator.Jsr330Activator;

/**
 * A thin wrapper around {@link UkelonnServiceBase} that will
 * be picked up by the {@link Jsr330Activator} and be presented
 * in OSGi as a {@link UkelonnService} service.
 *
 * @author Steinar Bang
 *
 */
public class UkelonnServiceProvider extends UkelonnServiceBase implements Provider<UkelonnService> {

    private WebContainer webContainer;

    @Inject
    public void setWebContainer(WebContainer webcontainer) {
        webContainer = webcontainer;
        if (webcontainer != null ) {
            final HttpContext httpContext = webContainer.createDefaultHttpContext();
            if (httpContext != null) {
                final Dictionary<String, Object> initParams = new Hashtable<String, Object>();
                initParams.put("from", "HttpService");
                final String registrationPath = "/ukelonn";
                try {
                    webcontainer.registerServlet(registrationPath, new UkelonnServlet(registrationPath), initParams, httpContext);
                    // register images as resources
                    webcontainer.registerResources("/images", "/images", httpContext);
                } catch (ServletException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NamespaceException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public UkelonnService get() {
        return this;
    }

}

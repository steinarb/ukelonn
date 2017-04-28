package no.priv.bang.ukelonn.impl;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletException;

import org.ops4j.pax.web.service.WebContainer;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnService;
import no.steria.osgi.jsr330activator.ActivatorShutdown;
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
    private static UkelonnServiceProvider instance;
    private WebContainer webContainer;
    private HttpContext httpContext;
    private UkelonnDatabase database;
    private LogService logservice;

    public UkelonnServiceProvider() {
        super();
        instance = this;
    }

    @ActivatorShutdown
    public void stop(BundleContext context) {
    	unregisterWebappWithWebContainer();
    }

    @Inject
    public void setUkelonnDatabase(UkelonnDatabase database) {
    	this.database = database;
    }

    @Override
    public UkelonnDatabase getDatabase() {
        return database;
    }

    @Inject
    public void setLogservice(LogService logservice) {
    	this.logservice = logservice;
    }

    @Override
    public LogService getLogservice() {
        return logservice;
    }

    @Inject
    public void setWebContainer(WebContainer webcontainer) throws ClassNotFoundException, ServletException, NamespaceException {
        registerWebappWithWebContainer(webcontainer);
    }

    public WebContainer getWebContainer() {
        return webContainer;
    }

    private void registerWebappWithWebContainer(WebContainer webcontainer) throws NamespaceException {
    	if (webcontainer == webContainer) {
            return; // Nothing to do, already registered
    	}

        // Disconnect the existing container before setting a new
        unregisterWebappWithWebContainer();

    	webContainer = webcontainer;

    	if (webcontainer != null) {
            httpContext = webContainer.createDefaultHttpContext();

            // register images as resources
            webContainer.registerResources("/images", "/images", httpContext);
    	}
    }

    private void unregisterWebappWithWebContainer() {
    	if (webContainer != null) {
            webContainer.unregister("/images");
            webContainer = null;
    	}
    }

    public UkelonnService get() {
        return this;
    }

    public static UkelonnService getInstance() {
        return instance;
    }

}

package no.priv.bang.ukelonn.impl;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletException;

import org.ops4j.pax.web.service.WebContainer;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.NamespaceException;

import no.priv.bang.ukelonn.UkelonnDatabase;
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
    private static UkelonnServiceProvider instance;
    private WebContainer webContainer;
    private HttpContext httpContext;
    private UkelonnDatabase database;

    public UkelonnServiceProvider() {
        super();
        instance = this;
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
    public void setWebContainer(WebContainer webcontainer) throws ClassNotFoundException, ServletException, NamespaceException {
        webContainer = webcontainer;
        httpContext = webContainer.createDefaultHttpContext();

        // register images as resources
        webContainer.registerResources("/images", "/images", httpContext);
    }

    public UkelonnService get() {
        return this;
    }

    public static UkelonnService getInstance() {
        return instance;
    }

}

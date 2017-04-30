package no.priv.bang.ukelonn.impl;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.ops4j.pax.web.service.WebContainer;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.log.LogService;

import com.vaadin.addon.touchkit.server.TouchKitServlet;

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
    private EnvironmentLoaderListener listener;
    private ShiroFilter shirofilter;
    private TouchKitServlet servlet;

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
    public void setWebContainer(WebContainer webcontainer) {
        registerWebappWithWebContainer(webcontainer);
    }

    public WebContainer getWebContainer() {
        return webContainer;
    }

    private void registerWebappWithWebContainer(WebContainer webcontainer) {
    	if (webcontainer == webContainer) {
            return; // Nothing to do, already registered
    	}

        // Disconnect the existing container before setting a new
        unregisterWebappWithWebContainer();

    	webContainer = webcontainer;

    	if (webcontainer != null) {
            httpContext = webContainer.createDefaultHttpContext();

            // Shiro filter config values
            final Dictionary<String, Object> initParamsShiroFilter = new Hashtable<>();
            final String[] urlPatternsShiroFilter = { "/ukelonn/*" };

            // servlet config values
            final Dictionary<String, Object> initParams = new Hashtable<String, Object>();
            initParams.put("UI", "no.priv.bang.ukelonn.impl.UkelonnUI");
            final String registrationPath = "/ukelonn/*";
            final String[] urlPatterns = { registrationPath, "/VAADIN/*" };

            try {
            	listener = new EnvironmentLoaderListener();
            	webContainer.registerEventListener(listener, httpContext);

                shirofilter = new ShiroFilter();
            	webcontainer.registerFilter(shirofilter, urlPatternsShiroFilter, null, initParamsShiroFilter, httpContext);

                servlet = new TouchKitServlet();
                webContainer.registerServlet(servlet, urlPatterns, initParams, httpContext);

            } catch (Exception e) {
                safeLogError("Failed to configure ukelonn webapp", e);
            }
    	}
    }

    private void unregisterWebappWithWebContainer() {
    	if (webContainer != null) {
            webContainer.unregisterServlet(servlet);
            webContainer.unregisterFilter(shirofilter);
            webContainer.unregisterEventListener(listener);
            webContainer = null;
    	}
    }

    public UkelonnService get() {
        return this;
    }

    public static UkelonnService getInstance() {
        return instance;
    }

    /***
     * Log an error level message to the OSGi log service if available,
     * if the OSGi log service isn't available, just eat the log message quietly.
     *
     * @param message the message to log
     * @param e the exception
     */
    private void safeLogError(String message, Throwable e) {
        if (logservice != null) {
            logservice.log(LogService.LOG_ERROR, message, e);
        }
    }

}

package no.priv.bang.ukelonn.impl;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.Filter;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.ops4j.pax.web.extender.whiteboard.ExtenderConstants;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.steria.osgi.jsr330activator.Jsr330Activator;
import no.steria.osgi.jsr330activator.ServiceProperties;
import no.steria.osgi.jsr330activator.ServiceProperty;

/**
 * This class will be be picked and instantiated up by the {@link Jsr330Activator} and be presented
 * in OSGi as a {@link Filter} service.  This filter service will be but in front of the servlet
 * exposed by the {@link UkelonnServletProvider}, and will handle authentication and authorizatio
 * from the servlet.
 *
 * The way it works, is:
 *  1. The Jsr330Activator will start by instantiating this class
 *  2. The Jsr330Activator will then register listeners for the two dependent services (log and database services)
 *  3. When the dependent services become available the Jsr330Activator will call the get() method of this class
 *     to get the filter instance, which is then registered as an OSGi service, which is picked up by the
 *     pax web whiteboard extender
 *  4. When the filter is starting, it will look for a file called shiro.ini at the root of the classpath,
 *     in the shiro.ini file the filter will find that it should use the {@link PasswordCompareValidator}
 *     for authentication.  This class is used instead of the built-in JdbcCompareValidator, because
 *     there is an extra database OSGi service layer in this application, that handles:
 *      1. Switching between the Derby test database and PostgreSQL
 *      2. Abstracts database authentication for the PostgreSQL base, and handles JDBC pooling
 *  5. If one or both of the dependent services go away, the servlet instance will be registered as going away
 *     (and will hopefully be removed in the pax web whiteboard extender)
 *  6. If the JsrActivator is stopped (e.g. when unloading the bundle), the Jsr330Activator will retract the
 *     servlet OSGi service, and release its hold on the two injected services
 *
 *  See also: {@link UkelonnServletProvider}, {@link ShiroEnvironmentLoaderListenerProvider}
 *
 * @author Steinar Bang
 *
 */
@ServiceProperties({ @ServiceProperty( name = ExtenderConstants.PROPERTY_URL_PATTERNS, values = {"/*"}),
                     @ServiceProperty( name = ExtenderConstants.PROPERTY_SERVLET_NAMES, value = "ukelonn"),
                     @ServiceProperty( name = ExtenderConstants.PROPERTY_HTTP_CONTEXT_PATH, value = "/ukelonn")})
public class ShiroFilterProvider implements Provider<Filter> {

    private static ShiroFilterProvider instance;
    private UkelonnDatabase database;
    private ShiroFilter filter;

    public ShiroFilterProvider() {
        instance = this;
    }

    @Inject
    public void setUkelonnDatabase(UkelonnDatabase database) {
        this.database = database;
    }

    public UkelonnDatabase getDatabase() {
        return database;
    }

    @Override
    public Filter get() {
        if (filter == null) {
            filter = new ShiroFilter();
        }

        return filter;
    }

    public static ShiroFilterProvider getInstance() {
        return instance;
    }

}

package no.priv.bang.ukelonn.impl;

import java.util.EventListener;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.Servlet;

import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.ops4j.pax.web.extender.whiteboard.ExtenderConstants;

import no.steria.osgi.jsr330activator.Jsr330Activator;
import no.steria.osgi.jsr330activator.ServiceProperties;
import no.steria.osgi.jsr330activator.ServiceProperty;

/**
 * This class will be be picked and instantiated up by the {@link Jsr330Activator} and be presented
 * in OSGi as an {@link EnvironmentLoaderListener} service.  This listener service works together
 * with the filter exposed by {@link ShiroFilterProvider} to provide authentication and authorization
 * for the servlet exposed by the {@link UkelonnServletProvider}.
 *
 * The way it works, is:
 *  1. The Jsr330Activator will start by instantiating this class
 *  2. The Jsr330Activator will call the get() method of this class to get the listener instance,
 *     which is then registered as an OSGi service, which is picked up by the pax web whiteboard extender
 *  3. If the JsrActivator is stopped (e.g. when unloading the bundle), the Jsr330Activator will retract the
 *     servlet OSGi service, and release its hold on the two injected services
 *
 *  See also: {@link UkelonnServletProvider}, {@link ShiroFilterProvider}
 *
 * @author Steinar Bang
 *
 */
@ServiceProperties({
	@ServiceProperty( name = ExtenderConstants.PROPERTY_HTTP_CONTEXT_ID, value = "default")})
public class ShiroEnvironmentLoaderListenerProvider implements Provider<EventListener> {

    private Servlet dependencyServletService;
    private EnvironmentLoaderListener listener;

    @Inject
    public void setDependencyServletService(Servlet dependencyServletService) {
        this.dependencyServletService = dependencyServletService;
    }

    public Servlet getDependencyServletService() {
        return dependencyServletService;
    }

    @Override
    public EventListener get() {
        if (listener == null) {
            listener = new EnvironmentLoaderListener();
        }

        return listener;
    }

}

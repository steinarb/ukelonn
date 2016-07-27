package no.priv.bang.ukelonn.impl;

import javax.inject.Provider;

import no.priv.bang.ukelonn.Webapp;
import no.steria.osgi.jsr330activator.Jsr330Activator;

/**
 * A thin wrapper around {@link WebappBase} that will
 * be picked up by the {@link Jsr330Activator} and be presented
 * in OSGi as a {@link Webapp} service.
 *
 * @author Steinar Bang
 *
 */
public class WebappProvider extends WebappBase implements Provider<Webapp> {

    public Webapp get() {
        return this;
    }

}

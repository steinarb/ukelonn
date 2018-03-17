package no.priv.bang.ukelonn.impl;

import java.util.EventListener;

import javax.servlet.Servlet;

import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * This is a DS component that provides an {@link EnvironmentLoaderListener} service.
 * This listener service works together with the filter exposed by
 * {@link ShiroFilterProvider} to provide authentication and authorization
 * for the servlet exposed by the {@link UkelonnServletProvider}.
 *
 * The purpose of this component is to make sure an {@link EnvironmentLoaderListener}
 * is created.
 *
 *  See also: {@link UkelonnServletProvider}, {@link ShiroFilterProvider}
 *
 * @author Steinar Bang
 *
 */
@Component(service=EventListener.class, immediate=true)
public class ShiroEnvironmentLoaderListenerProvider extends EnvironmentLoaderListener {

    private Servlet dependencyServletService;

    @Reference
    public void setDependencyServletService(Servlet dependencyServletService) {
        this.dependencyServletService = dependencyServletService;
    }

    public Servlet getDependencyServletService() {
        return dependencyServletService;
    }

}

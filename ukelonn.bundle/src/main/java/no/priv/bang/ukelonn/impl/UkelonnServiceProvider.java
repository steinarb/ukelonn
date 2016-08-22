package no.priv.bang.ukelonn.impl;

import java.net.URL;
import java.util.Enumeration;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.jasper.compiler.JspUtil;
import org.ops4j.pax.web.service.WebContainer;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
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
    private static final String JSP = "/ukelonn/jsp";
    private static final String JSPC = JSP + 'c';
    private static final String DEFAULT_PACKAGE = "org.apache.jsp.";

    private WebContainer webContainer;

    @Inject
    public void setWebContainer(WebContainer webcontainer) throws ClassNotFoundException, ServletException, NamespaceException {
        webContainer = webcontainer;
        final HttpContext httpContext = webContainer.createDefaultHttpContext();
        Bundle bundle = FrameworkUtil.getBundle(getClass());
        Enumeration<?> entries = bundle.findEntries(JSP, "*", true);
        if (entries != null) {
            while (entries.hasMoreElements()) {
                URL entry = (URL) entries.nextElement();
                String jspFile = entry.toExternalForm().substring(entry.toExternalForm().lastIndexOf('/') + 1);
                String urlPattern = JSPC + "/" + jspFile;
                String jspcClassName = DEFAULT_PACKAGE
                    + convertPath(entry.toExternalForm()) + "."
                    + JspUtil.makeJavaIdentifier(jspFile);
                @SuppressWarnings("unchecked")
                    Class<Servlet> precompiledClass = (Class<Servlet>) getClass().getClassLoader().loadClass(jspcClassName);
                webContainer.registerServlet(precompiledClass, new String[] { urlPattern }, null, httpContext);
            }
        }

        webContainer.registerJsps(new String[] { JSP + "/*" }, httpContext);

        // register images as resources
        webContainer.registerResources("/images", "/images", httpContext);
        // webContainer.end(httpContext);
    }

    private String convertPath(String jspPath) {
        // String path = jspPath.replaceFirst("bundle\\:\\/\\/\\d*\\.\\d*.\\d\\/", "");
        String path = jspPath.replaceFirst("(bundle.*://\\d*\\.)((\\w*)|(\\d*\\:\\d*))/", "");
        path = path.substring(0, path.lastIndexOf('/'));
        path = path.replace("/", ".");
        return path;
    }

    public UkelonnService get() {
        return this;
    }

}

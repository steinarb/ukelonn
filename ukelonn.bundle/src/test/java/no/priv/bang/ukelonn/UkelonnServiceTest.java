package no.priv.bang.ukelonn;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Dictionary;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Matchers;
import org.ops4j.pax.web.service.WebContainer;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.impl.UkelonnServiceProvider;

/**
 * Unit test for the {@link UkelonnService} interface and its
 * implementations.
 *
 * @author Steinar Bang
 *
 */
public class UkelonnServiceTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Test fetching a {@link UkelonnService}.
     */
    @Test
    public void testGetUkelonnServices() {
        UkelonnService ukelonnService = new UkelonnServiceProvider().get();
        assertNotNull(ukelonnService);
        assertEquals("Hello world!", ukelonnService.getMessage());
    }

    @Test
    public void testSetWebContainer() throws ClassNotFoundException, ServletException, NamespaceException {
        UkelonnServiceProvider ukelonnServiceProvider = new UkelonnServiceProvider();

        // Verify that setting null WebContainer on a service provider is safe
        ukelonnServiceProvider.setWebContainer(null);
        assertNull(ukelonnServiceProvider.getWebContainer());

        // Verify that setting a webcontainer is persisted
        WebContainer webcontainer = mock(WebContainer.class);
        ukelonnServiceProvider.setWebContainer(webcontainer);
        assertEquals(webcontainer, ukelonnServiceProvider.getWebContainer());

        // Verifying that trying to set the same webcontainer twice, does no harm
        ukelonnServiceProvider.setWebContainer(webcontainer);
        assertEquals(webcontainer, ukelonnServiceProvider.getWebContainer());

        // Verifying that setting a different webcontainer will replace the container
        WebContainer aDifferentWebcontainer = mock(WebContainer.class);
        ukelonnServiceProvider.setWebContainer(aDifferentWebcontainer);
        assertEquals(aDifferentWebcontainer, ukelonnServiceProvider.getWebContainer());

        // Set the webcontainer to null twice to verify that this is safe
        ukelonnServiceProvider.setWebContainer(null);
        assertNull(ukelonnServiceProvider.getWebContainer());
        ukelonnServiceProvider.setWebContainer(null);
        assertNull(ukelonnServiceProvider.getWebContainer());
    }

    @Test
    public void testStopProvider() {
        UkelonnServiceProvider ukelonnServiceProvider = new UkelonnServiceProvider();

        // Test that stopping a provider that hasn't been started, with a null bundlecontext, doesn't crash anything
        ukelonnServiceProvider.stop(null);

        WebContainer webcontainer = mock(WebContainer.class);
        ukelonnServiceProvider.setWebContainer(webcontainer);
        verify(webcontainer, times(1)).createDefaultHttpContext();

        // Stop the provider, verifying that unregistration will take place
        ukelonnServiceProvider.stop(mock(BundleContext.class));
        verify(webcontainer, times(1)).unregisterServlet(any(Servlet.class));

        // Stop the provider again, verifying that no crash will happen
        ukelonnServiceProvider.stop(mock(BundleContext.class));
    }

    @Test
    public void testExceptionRegisteringServlet() throws ServletException {
        UkelonnServiceProvider ukelonnServiceProvider = new UkelonnServiceProvider();

        // Create a webcontainer that will throw exception on servlet registration
        WebContainer webcontainer = mock(WebContainer.class);
        doThrow(new ServletException())
            .when(webcontainer).registerServlet(
                any(Servlet.class),
                any(String[].class),
                Matchers.<Dictionary<String, Object>>any(),
                any(HttpContext.class));

        // First test, no log service present, the test is just that nothing fails
        ukelonnServiceProvider.setWebContainer(webcontainer);

        // disconnect the webcontainer
        ukelonnServiceProvider.setWebContainer(null);

        // Mock a logservice
        LogService logservice = mock(LogService.class);
        ukelonnServiceProvider.setLogservice(logservice);

        // Try registering a servlet and verify that the log service has been called
        ukelonnServiceProvider.setWebContainer(webcontainer);
        verify(logservice, times(1)).log(eq(LogService.LOG_ERROR), anyString(), any(Exception.class));
    }

}

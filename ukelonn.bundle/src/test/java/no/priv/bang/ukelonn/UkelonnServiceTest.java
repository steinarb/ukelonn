package no.priv.bang.ukelonn;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.servlet.ServletException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.ops4j.pax.web.service.WebContainer;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.NamespaceException;

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
    public void testStopProvider() throws ClassNotFoundException, ServletException, NamespaceException {
        UkelonnServiceProvider ukelonnServiceProvider = new UkelonnServiceProvider();

        // Test that stopping a provider that hasn't been started, with a null bundlecontext, doesn't crash anything
        ukelonnServiceProvider.stop(null);

        WebContainer webcontainer = mock(WebContainer.class);
        ukelonnServiceProvider.setWebContainer(webcontainer);
        verify(webcontainer, times(1)).createDefaultHttpContext();

        // Stop the activator, verifying that unregistration will take place
        ukelonnServiceProvider.stop(mock(BundleContext.class));
        verify(webcontainer, times(1)).unregister("/images");
    }

}

package no.priv.bang.ukelonn;


import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import no.priv.bang.ukelonn.Webapp;
import no.priv.bang.ukelonn.impl.WebappProvider;

/**
 * Unit test for the {@link Webapp} interface and its
 * implementations.
 *
 * @author Steinar Bang
 *
 */
public class WebappTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Test fetching a {@link Webapp}.
     */
    @Test
    public void testGetWebapp() {
        Webapp webapp = new WebappProvider().get();
        assertNotNull(webapp);
        assertEquals("Hello world!", webapp.getMessage());
    }

}

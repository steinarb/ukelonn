package no.priv.bang.ukelonn;


import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
    public void testGetWebapp() {
        UkelonnService webapp = new UkelonnServiceProvider().get();
        assertNotNull(webapp);
        assertEquals("Hello world!", webapp.getMessage());
    }

}

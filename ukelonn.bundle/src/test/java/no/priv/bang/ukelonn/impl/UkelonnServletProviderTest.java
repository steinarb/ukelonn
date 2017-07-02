package no.priv.bang.ukelonn.impl;


import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import no.priv.bang.ukelonn.UkelonnService;

/**
 * Unit test for the {@link UkelonnServletProvider} class.
 *
 * @author Steinar Bang
 *
 */
public class UkelonnServletProviderTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Test fetching a {@link UkelonnService}.
     */
    @Test
    public void testCreateAndAssignToInterface() {
        UkelonnService ukelonnService = new UkelonnServletProvider();
        assertNull(ukelonnService.getDatabase());
        assertNull(ukelonnService.getLogservice());
    }

}

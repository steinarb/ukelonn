package no.priv.bang.ukelonn.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import org.junit.Test;

import no.priv.bang.ukelonn.mocks.UkelonnDatabaseRecordingUnlockCall;

/*
 * Unit tests for {@link KarafReleaseLiquibaseLockCommand}.
 */
public class KarafReleaseLiquibaseLockCommandTest {

    /*
     * If no {@link UkelonnDatabase} OSGi service is present
     * a NullPointerException will abort the command.
     */
    @Test
    public void testExecuteNullDatabaseOsgiService() throws Exception {
        try {
            KarafReleaseLiquibaseLockCommand action = new KarafReleaseLiquibaseLockCommand();
            action.execute();
            fail("Expected a NullPointerExceptio to be thrown");
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void testExecute() throws Exception {
        UkelonnDatabaseRecordingUnlockCall database = mock(UkelonnDatabaseRecordingUnlockCall.class, CALLS_REAL_METHODS);
        KarafReleaseLiquibaseLockCommand action = new KarafReleaseLiquibaseLockCommand();

        // Fake OSGi service injection
        setInternalState(action, "database", database);

        // Verify precondition
        assertFalse(database.isForceReleaseLocksCalled());

        // Run the code under test
        action.execute();

        // Verify expected results
        assertTrue(database.isForceReleaseLocksCalled());
    }

}

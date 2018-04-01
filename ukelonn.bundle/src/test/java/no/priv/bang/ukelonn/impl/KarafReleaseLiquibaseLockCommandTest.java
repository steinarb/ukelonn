/*
 * Copyright 2016-2018 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
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

/*
 * Copyright 2016-2020 Steinar Bang
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
package no.priv.bang.ukelonn.backend;

import static org.junit.Assert.*;
import static org.mockito.internal.util.reflection.Whitebox.*;
import static no.priv.bang.ukelonn.testutils.TestUtils.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import no.priv.bang.ukelonn.UkelonnService;

/*
 * Unit tests for {@link KarafReleaseLiquibaseLockCommand}.
 */
public class KarafReleaseLiquibaseLockCommandTest {

    @BeforeClass
    public static void setupForAllTests() throws Exception {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    /*
     * If no {@link UkelonnDatabase} OSGi service is present
     * a NullPointerException will abort the command.
     */
    @Test
    public void testExecuteNullDatabaseOsgiService() throws Exception {
        KarafReleaseLiquibaseLockCommand action = new KarafReleaseLiquibaseLockCommand();
        try {
            action.execute();
            fail("Expected a NullPointerExceptio to be thrown");
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void testExecute() throws Exception {
        KarafReleaseLiquibaseLockCommand action = new KarafReleaseLiquibaseLockCommand();
        UkelonnService ukelonn = getUkelonnServiceSingleton();

        // Fake OSGi service injection
        setInternalState(action, "ukelonn", ukelonn);

        // Run the code under test
        Object result = action.execute();

        // Verify expected results
        assertNull(result);
    }

}

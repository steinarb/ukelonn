/*
 * Copyright 2018-2024 Steinar Bang
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
package no.priv.bang.ukelonn.api.resources;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;

import no.priv.bang.ukelonn.UkelonnService;

class JobtypeListTest {

    @Test
    void testGetJobtypes() throws Exception {
        // Create the resource that is to be tested
        var resource = new JobtypeList();

        // Inject fake OSGi service UkelonnService
        var ukelonn = mock(UkelonnService.class);
        when(ukelonn.getJobTypes()).thenReturn(getJobtypes());
        resource.ukelonn = ukelonn;

        // Run the method that is to be tested
        var jobtypes = resource.getJobtypes();

        // Check the output
        assertEquals(4, jobtypes.size());
    }

}

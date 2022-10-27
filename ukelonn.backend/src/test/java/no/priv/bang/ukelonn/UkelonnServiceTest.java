/*
 * Copyright 2016-2021 Steinar Bang
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
package no.priv.bang.ukelonn;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;

/**
 * Unit test for the {@link UkelonnService} interface and its
 * implementations.
 *
 * @author Steinar Bang
 *
 */
class UkelonnServiceTest {
    /**
     * Test fetching a {@link UkelonnService}.
     */
    @Test
    void testGetUkelonnServices() {
        UkelonnService ukelonnService = new UkelonnServiceProvider();
        assertNotNull(ukelonnService);
        assertEquals("Hello world!", ukelonnService.getMessage());
    }

}

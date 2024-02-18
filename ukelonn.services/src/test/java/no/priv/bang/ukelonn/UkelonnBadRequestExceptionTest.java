/*
 * Copyright 2016-2024 Steinar Bang
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

class UkelonnBadRequestExceptionTest {

    @Test
    void testMessage() {
        var exception = new UkelonnBadRequestException("Test");
        assertEquals("Test", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageAndCause() {
        var cause = new NullPointerException();
        var exception = new UkelonnBadRequestException("Test", cause);
        assertEquals("Test", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testCause() {
        var cause = new NullPointerException();
        var exception = new UkelonnBadRequestException(cause);
        assertEquals(cause.getClass().getName(), exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

}

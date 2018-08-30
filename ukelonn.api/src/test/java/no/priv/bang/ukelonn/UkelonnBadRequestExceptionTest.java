/*
 * Copyright 2016-2017 Steinar Bang
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

import static org.junit.Assert.*;

import org.junit.Test;

public class UkelonnBadRequestExceptionTest {

    @Test
    public void testMessage() {
        UkelonnBadRequestException exception = new UkelonnBadRequestException("Test");
        assertEquals("Test", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testMessageAndCause() {
        NullPointerException cause = new NullPointerException();
        UkelonnBadRequestException exception = new UkelonnBadRequestException("Test", cause);
        assertEquals("Test", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testCause() {
        NullPointerException cause = new NullPointerException();
        UkelonnBadRequestException exception = new UkelonnBadRequestException(cause);
        assertEquals(cause.getClass().getName(), exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

}

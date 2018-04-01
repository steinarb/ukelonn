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

import org.junit.Test;

public class CommonStringMethodsTest {

    @Test
    public void testStringIsNullEmptyOrBlank() {
        assertTrue(CommonStringMethods.isNullEmptyOrBlank(null));
        assertTrue(CommonStringMethods.isNullEmptyOrBlank(""));
        assertTrue(CommonStringMethods.isNullEmptyOrBlank(" \t\r\n"));
        assertFalse(CommonStringMethods.isNullEmptyOrBlank("ab"));
        assertFalse(CommonStringMethods.isNullEmptyOrBlank("  ab  "));
    }

    @Test
    public void testStringSafeTrim() {
        assertNull(CommonStringMethods.safeTrim(null));
        assertEquals("", CommonStringMethods.safeTrim(""));
        assertEquals("abc", CommonStringMethods.safeTrim("abc"));
        assertEquals("abc", CommonStringMethods.safeTrim("  abc  \r\t\n "));
    }

    @Test
    public void testNullSafeEquals() {
        assertTrue(CommonStringMethods.nullSafeEquals(null, null));
        assertFalse(CommonStringMethods.nullSafeEquals(null, ""));
        assertFalse(CommonStringMethods.nullSafeEquals("", null));
        String sameStringObject = "This is the same string object";
        assertTrue(CommonStringMethods.nullSafeEquals(sameStringObject, sameStringObject));
        assertTrue(CommonStringMethods.nullSafeEquals("identical string", "identical string"));
        assertTrue(CommonStringMethods.nullSafeEquals("", ""));
        assertFalse(CommonStringMethods.nullSafeEquals("not identical", "to this"));
    }

}

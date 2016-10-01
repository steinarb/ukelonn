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

}

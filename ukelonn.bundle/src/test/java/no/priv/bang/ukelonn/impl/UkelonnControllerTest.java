package no.priv.bang.ukelonn.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class UkelonnControllerTest {

    @Test
    public void testFornavn() {
        UkelonnController ukelonn = new UkelonnController();
        assertEquals("Ola", ukelonn.getFornavn());
        ukelonn.setFornavn("Kari");
        assertEquals("Kari", ukelonn.getFornavn());
    }

    @Test
    public void testBalanse() {
        UkelonnController ukelonn = new UkelonnController();
        assertEquals(Double.valueOf(120), ukelonn.getBalanse());
        ukelonn.setBalanse(Double.valueOf(-1));
        assertEquals(Double.valueOf(-1), ukelonn.getBalanse());
    }


    @Test
    public void testJobs() {
        UkelonnController ukelonn = new UkelonnController();
        assertEquals(3, ukelonn.getJobs().size());
    }
}

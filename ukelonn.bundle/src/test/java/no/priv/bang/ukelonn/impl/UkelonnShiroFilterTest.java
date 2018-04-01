package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.Assert.*;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class UkelonnShiroFilterTest {

    @BeforeClass
    public static void setupClass() {
        setupFakeOsgiServices();
    }

    @AfterClass
    public static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    @Test
    public void testAuthenticate() {
        UkelonnShiroFilter shirofilter = new UkelonnShiroFilter();
        shirofilter.setUkelonnDatabase(getUkelonnServlet().getUkelonnUIProvider().getDatabase());
        shirofilter.activate();
        WebSecurityManager securitymanager = shirofilter.getSecurityManager();
        AuthenticationToken token = new UsernamePasswordToken("jad", "1ad".toCharArray());
        AuthenticationInfo info = securitymanager.authenticate(token);
        assertEquals(1, info.getPrincipals().asList().size());
    }

}

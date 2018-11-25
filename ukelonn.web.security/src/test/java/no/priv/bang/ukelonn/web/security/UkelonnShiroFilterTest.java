package no.priv.bang.ukelonn.web.security;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.Assert.*;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import no.priv.bang.ukelonn.web.security.dbrealm.UkelonnRealm;

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
        UkelonnRealm realm = new UkelonnRealm();
        realm.setDatabase(getUkelonnServiceSingleton().getDatabase());
        realm.activate();
        shirofilter.setRealm(realm);
        shirofilter.activate();
        WebSecurityManager securitymanager = shirofilter.getSecurityManager();
        AuthenticationToken token = new UsernamePasswordToken("jad", "1ad".toCharArray());
        AuthenticationInfo info = securitymanager.authenticate(token);
        assertEquals(1, info.getPrincipals().asList().size());
    }

}

package no.priv.bang.ukelonn.web.security;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.priv.bang.authservice.web.security.dbrealm.AuthserviceDbRealm;
import no.priv.bang.authservice.web.security.memorysession.MemorySession;

class UkelonnShiroFilterTest {

    @BeforeAll
    static void setupClass() throws Exception {
        setupFakeOsgiServices();
    }

    @AfterAll
    static void teardownForAllTests() throws Exception {
        releaseFakeOsgiServices();
    }

    @Test
    void testAuthenticate() {
        var shirofilter = new UkelonnShiroFilter();
        var realm = new AuthserviceDbRealm();
        var session = new MemorySession();
        session.activate();
        realm.setDataSource(getUkelonnServiceSingleton().getDataSource());
        realm.activate();
        shirofilter.setSession(session);
        shirofilter.setRealm(realm);
        shirofilter.activate();
        var securitymanager = shirofilter.getSecurityManager();
        var token = new UsernamePasswordToken("jad", "1ad".toCharArray());
        var info = securitymanager.authenticate(token);
        assertEquals(1, info.getPrincipals().asList().size());
    }

}

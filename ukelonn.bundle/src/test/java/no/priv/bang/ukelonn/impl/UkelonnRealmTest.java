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

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/***
 * Tests for class {@link UkelonnRealm}.
 * This test sets up a mock database and OSGi services to check password from.
 *
 * The test class {@link UkelonnRealmTestMissingServices} tests the corner case
 * where one or both OSGi services are missing.
 *
 * @author Steinar Bang
 *
 */
public class UkelonnRealmTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setupForAllTests() {
        setupFakeOsgiServices();
    }

    /***
     * Test a successful authentication.
     */
    @Test
    public void testGetAuthenticationInfo() {
        UkelonnShiroFilter shiroFilter = new UkelonnShiroFilter();
        shiroFilter.setUkelonnDatabase(getUkelonnServlet().getUkelonnUIProvider().getDatabase());
        UkelonnRealm realm = new UkelonnRealm(shiroFilter);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        AuthenticationToken token = new UsernamePasswordToken("jad", "1ad".toCharArray());
        AuthenticationInfo authInfo = realm.getAuthenticationInfo(token);
        assertEquals(1, authInfo.getPrincipals().asList().size());
    }

    /***
     * Test authentication failing because of a wrong password.
     */
    @Test
    public void testGetAuthenticationInfoWrongPassword() {
        UkelonnShiroFilter shiroFilter = new UkelonnShiroFilter();
        shiroFilter.setUkelonnDatabase(getUkelonnServlet().getUkelonnUIProvider().getDatabase());
        UkelonnRealm realm = new UkelonnRealm(shiroFilter);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        AuthenticationToken token = new UsernamePasswordToken("jad", "1add".toCharArray());

        exception.expect(IncorrectCredentialsException.class);
        AuthenticationInfo authInfo = realm.getAuthenticationInfo(token);
        assertEquals(1, authInfo.getPrincipals().asList().size());
    }

    /***
     * Test authentication failing because of a wrong username, i.e. user
     * not found.
     */
    @Test
    public void testGetAuthenticationInfoWrongUsername() {
        UkelonnShiroFilter shiroFilter = new UkelonnShiroFilter();
        shiroFilter.setUkelonnDatabase(getUkelonnServlet().getUkelonnUIProvider().getDatabase());
        UkelonnRealm realm = new UkelonnRealm(shiroFilter);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        AuthenticationToken token = new UsernamePasswordToken("jadd", "1ad".toCharArray());

        exception.expect(IncorrectCredentialsException.class);
        AuthenticationInfo authInfo = realm.getAuthenticationInfo(token);
        assertEquals(1, authInfo.getPrincipals().asList().size());
    }

    /***
     * Test authentication failing because the token is not a {@link UsernamePasswordToken}.
     */
    @Test
    public void testGetAuthenticationInfoWrongTokenType() {
        UkelonnShiroFilter shiroFilter = new UkelonnShiroFilter();
        shiroFilter.setUkelonnDatabase(getUkelonnServlet().getUkelonnUIProvider().getDatabase());
        UkelonnRealm realm = new UkelonnRealm(shiroFilter);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        AuthenticationToken token = mock(AuthenticationToken.class);
        String username = "jad";
        String password = "1ad";
        when(token.getPrincipal()).thenReturn(username);
        when(token.getCredentials()).thenReturn(password);

        exception.expect(AuthenticationException.class);
        AuthenticationInfo authInfo = realm.getAuthenticationInfo(token);
        assertEquals(1, authInfo.getPrincipals().asList().size());
    }

    /***
     * Test that a user gets the correct roles.
     */
    @Test
    public void testGetRolesForUsers() {
        UkelonnShiroFilter shiroFilter = new UkelonnShiroFilter();
        shiroFilter.setUkelonnDatabase(getUkelonnServlet().getUkelonnUIProvider().getDatabase());
        UkelonnRealm realm = new UkelonnRealm(shiroFilter);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        AuthenticationToken token = new UsernamePasswordToken("jad", "1ad".toCharArray());
        AuthenticationInfo authenticationInfoForUser = realm.getAuthenticationInfo(token);

        boolean jadHasRoleUser = realm.hasRole(authenticationInfoForUser.getPrincipals(), "user");
        assertTrue(jadHasRoleUser);

        boolean jadHasRoleAdministrator = realm.hasRole(authenticationInfoForUser.getPrincipals(), "administrator");
        assertFalse(jadHasRoleAdministrator);
    }

    /***
     * Test that an administrator gets the correct roles.
     */
    @Test
    public void testGetRolesForAdministrators() {
        UkelonnShiroFilter shiroFilter = new UkelonnShiroFilter();
        shiroFilter.setUkelonnDatabase(getUkelonnServlet().getUkelonnUIProvider().getDatabase());
        UkelonnRealm realm = new UkelonnRealm(shiroFilter);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        AuthenticationToken token = new UsernamePasswordToken("on", "ola12".toCharArray());
        AuthenticationInfo authenticationInfoForUser = realm.getAuthenticationInfo(token);

        boolean onHasRoleUser = realm.hasRole(authenticationInfoForUser.getPrincipals(), "user");
        assertTrue(onHasRoleUser);

        boolean onHasRoleAdministrator = realm.hasRole(authenticationInfoForUser.getPrincipals(), "administrator");
        assertTrue(onHasRoleAdministrator);
    }

    private CredentialsMatcher createSha256HashMatcher(int iterations) {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
        credentialsMatcher.setStoredCredentialsHexEncoded(false);
        credentialsMatcher.setHashIterations(iterations);
        return credentialsMatcher;
    }

}

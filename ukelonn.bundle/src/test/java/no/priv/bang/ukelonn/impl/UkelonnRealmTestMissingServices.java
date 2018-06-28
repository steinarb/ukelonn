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

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnService;

/***
 * Corner case tests for {@link UkelonnRealm}.  This test class tests
 * the cases of missing OSGi services.
 *
 * For tests for the functionality of {@link UkelonnRealm}, see {@link UkelonnRealmTest}.
 *
 * @author Steinar Bang
 *
 */
public class UkelonnRealmTestMissingServices {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        releaseFakeOsgiServices();
    }

    /***
     * Test the case where there is an {@link UkelonnService} OSGi service present,
     * but no {@link UkelonnDatabase} OSGi service.
     */
    @Test
    public void testNoUkelonnDatabaseService() {
        UkelonnShiroFilter shiroFilter = new UkelonnShiroFilter();
        shiroFilter.setUkelonnDatabase(null);
        UkelonnRealm realm = new UkelonnRealm(shiroFilter);
        AuthenticationToken token = new UsernamePasswordToken("jad", "1ad".toCharArray());

        exception.expect(AuthenticationException.class);
        AuthenticationInfo authInfo = realm.getAuthenticationInfo(token);
        assertEquals(1, authInfo.getPrincipals().asList().size());
    }

}

/*
 * Copyright 2025 Steinar Bang
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
package no.priv.bang.ukelonn.backend.testdata;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.osgiservice.users.UserRoles;
import no.priv.bang.ukelonn.UkelonnService;

import static no.priv.bang.ukelonn.UkelonnConstants.*;


class UkelonnTestdataTest {

    @Test
    void testActivate() {
        var testdata = new UkelonnTestdata();

        var ukelonn = mock(UkelonnService.class);
        testdata.setUkelonnService(ukelonn);
        var useradmin = mock(MockUserManagementService.class, CALLS_REAL_METHODS);
        when(useradmin.getUser("jod")).thenReturn(User.with().userid(1).username("jod").email("jd@gmail.com").firstname("John").lastname("Doe").build());
        when(useradmin.getUser("jad")).thenReturn(User.with().userid(1).username("jad").email("jad@gmail.com").firstname("Jane").lastname("Doe").build());
        when(useradmin.getRoles()).thenReturn(Arrays.asList(Role.with().build(), Role.with().build(), Role.with().id(2).rolename(UKELONNUSER_ROLE).build()));
        testdata.setUseradmin(useradmin);

        assertEquals(0, useradmin.getUserRoles().size()); // Verify preconditions
        testdata.activate();
        assertThat(useradmin.getUserRoles()).isNotEmpty();
    }

    abstract class MockUserManagementService implements UserManagementService {
        Map<String, List<Role>> userroles;

        @Override
        public Map<String, List<Role>> getUserRoles() {
            if (userroles == null) {
                userroles = new HashMap<>();
            }

            return userroles;
        }

        @Override
        public Map<String, List<Role>> addUserRoles(UserRoles nyrolle) {
            userroles.put(nyrolle.user().username(), nyrolle.roles());
            return userroles;
        }
    }

}

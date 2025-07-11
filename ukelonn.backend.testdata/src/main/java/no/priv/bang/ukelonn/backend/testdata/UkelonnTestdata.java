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

import static no.priv.bang.ukelonn.UkelonnConstants.*;

import java.util.Arrays;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.osgiservice.users.UserRoles;
import no.priv.bang.ukelonn.UkelonnService;

@Component(immediate=true)
public class UkelonnTestdata {

    private UserManagementService useradmin;

    @Reference
    public void setUkelonnService(UkelonnService ukelonn) {
        // Only used to determine the order of execution
        // When this is used we know that UkelonnServiceProvider
        // has added role ukelonnruser
    }

    @Reference
    public void setUseradmin(UserManagementService useradmin) {
        this.useradmin = useradmin;
    }

    @Activate
    public void activate() {
        addRolesForTestusers();
    }

    void addRolesForTestusers() {
        var ratatoskruser = useradmin.getRoles().stream().filter(r -> UKELONNUSER_ROLE.equals(r.rolename())).findFirst().get(); // NOSONAR testkode
        var jad = useradmin.getUser("jad");
        useradmin.addUserRoles(UserRoles.with().user(jad).roles(Arrays.asList(ratatoskruser)).build());
        var jod = useradmin.getUser("jod");
        useradmin.addUserRoles(UserRoles.with().user(jod).roles(Arrays.asList(ratatoskruser)).build());
    }

}

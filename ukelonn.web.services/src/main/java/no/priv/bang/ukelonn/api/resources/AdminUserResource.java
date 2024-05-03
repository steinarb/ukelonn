/*
 * Copyright 2018-2024 Steinar Bang
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
package no.priv.bang.ukelonn.api.resources;

import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.authservice.definitions.AuthservicePasswordEmptyException;
import no.priv.bang.authservice.definitions.AuthservicePasswordsNotIdenticalException;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.osgiservice.users.UserRoles;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.api.beans.AdminStatus;
import static no.priv.bang.ukelonn.UkelonnConstants.*;

@Path("/admin/user")
@Produces(MediaType.APPLICATION_JSON)
@RequiresUser
@RequiresRoles("ukelonnadmin")
public class AdminUserResource {

    @Inject
    UserManagementService useradmin;

    @Inject
    UkelonnService ukelonn;

    Logger logger;

    @Inject
    void setLogservice(LogService logservice) {
        this.logger = logservice.getLogger(getClass());
    }

    @Path("modify")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public List<User> modify(User user) {
        try {
            return useradmin.modifyUser(user);
        } catch (AuthserviceException e) {
            logger.error(String.format("REST endpoint /ukelonn/api/admin/user/modify failed to modify user %d", user.getUserid()));
            throw new InternalServerErrorException("See log for details");
        }
    }

    @Path("create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public List<User> create(UserAndPasswords passwords) {
        try {
            var users = useradmin.addUser(passwords);

            // Create an account with a balance for the new user
            var username = passwords.getUser().getUsername();
            var createdUser = users.stream().filter(u -> username.equals(u.getUsername())).findFirst();
            if (!createdUser.isPresent()) {
                throw new UkelonnException(String.format("Found no user matching %s in the users table", username));
            }

            var user = no.priv.bang.ukelonn.beans.User.with()
                .userId(createdUser.get().getUserid())
                .username(username)
                .email(createdUser.get().getEmail())
                .firstname(createdUser.get().getFirstname())
                .lastname(createdUser.get().getLastname())
                .build();
            ukelonn.addAccount(user);

            return users;
        } catch (AuthserviceException e) {
            logger.error("REST endpoint /ukelonn/api/admin/user/create got error from the database", e);
            throw new InternalServerErrorException("See log for error cause");
        }
    }

    @Path("password")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public List<User> password(UserAndPasswords passwords) {
        try {
            return useradmin.updatePassword(passwords);
        } catch (AuthservicePasswordEmptyException e) {
            logger.warn("REST endpoint /ukelonn/api/admin/user/password received empty password");
            throw new BadRequestException(e.getMessage());
        } catch (AuthservicePasswordsNotIdenticalException e) {
            logger.warn("REST endpoint /ukelonn/api/admin/user/password received passwords that weren't identical");
            throw new BadRequestException(e.getMessage());
        } catch (AuthserviceException e) {
            logger.error(String.format("REST endpoint /ukelonn/api/admin/user/password got bad request: %s", e.getMessage()));
            throw new InternalServerErrorException("See log for error details");
        }
    }

    @Path("adminstatus")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public AdminStatus adminStatus(User user) {
        var administrator = userIsAdministrator(user);
        return AdminStatus.with()
            .user(user)
            .administrator(administrator)
            .build();
    }

    @Path("changeadminstatus")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public AdminStatus changeAdminStatus(AdminStatus status) {
        if (status.administrator() != userIsAdministrator(status.user())) {
            var ukelonnadmin = useradmin.getRoles().stream().filter(r -> UKELONNADMIN_ROLE.equals(r.getRolename())).findFirst();
            if (!ukelonnadmin.isPresent()) {
                // If no ukelonn admin role is present in the auth service
                // administrator will always be false
                return AdminStatus.with()
                    .user(status.user())
                    .administrator(false)
                    .build();
            }

            if (status.administrator()) {
                // admin role is missing, add the role
                useradmin.addUserRoles(UserRoles.with().user(status.user()).roles(Arrays.asList(ukelonnadmin.get())).build());
            } else {
                // admin role is present, remove the role
                useradmin.removeUserRoles(UserRoles.with().user(status.user()).roles(Arrays.asList(ukelonnadmin.get())).build());
            }
        }

        return AdminStatus.with()
            .user(status.user())
            .administrator(userIsAdministrator(status.user()))
            .build();
    }

    boolean userIsAdministrator(User user) {
        return useradmin.getRolesForUser(user.getUsername()).stream().anyMatch(r -> UKELONNADMIN_ROLE.equals(r.getRolename()));
    }

}

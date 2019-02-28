/*
 * Copyright 2018 Steinar Bang
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

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.log.LogService;

import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.ukelonn.UkelonnBadRequestException;
import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;

@Path("/admin/user")
@Produces(MediaType.APPLICATION_JSON)
public class AdminUserResource {

    @Inject
    UserManagementService useradmin;

    @Inject
    UkelonnService ukelonn;

    @Inject
    LogService logservice;

    @Path("modify")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public List<User> modify(User user) {
        try {
            return useradmin.modifyUser(user);
        } catch (UkelonnException e) {
            logservice.log(LogService.LOG_ERROR, String.format("REST endpoint /ukelonn/api/admin/user/modify failed to modify user %d", user.getUserid()));
            throw new InternalServerErrorException("See log for details");
        }
    }

    @Path("create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public List<User> create(UserAndPasswords passwords) {
        try {
            List<User> users = useradmin.addUser(passwords);

            // Create an account with a balance for the new user
            String username = passwords.getUser().getUsername();
            Optional<User> createdUser = users.stream().filter(u -> username.equals(u.getUsername())).findFirst();
            if (!createdUser.isPresent()) {
                throw new UkelonnException(String.format("Found no user matching %s in the users table", username));
            }

            no.priv.bang.ukelonn.beans.User user = new no.priv.bang.ukelonn.beans.User(createdUser.get().getUserid(), username, createdUser.get().getEmail(), createdUser.get().getFirstname(), createdUser.get().getLastname());
            ukelonn.addAccount(user);

            return users;
        } catch (UkelonnBadRequestException e) {
            logservice.log(LogService.LOG_WARNING, String.format("REST endpoint /ukelonn/api/admin/user/create got bad request: %s", e.getMessage()));
            throw new BadRequestException(e.getMessage());
        } catch (UkelonnException e) {
            logservice.log(LogService.LOG_ERROR, "REST endpoint /ukelonn/api/admin/user/create got error from the database", e);
            throw new InternalServerErrorException("See log for error cause");
        }
    }

    @Path("password")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public List<User> password(UserAndPasswords passwords) {
        try {
            return useradmin.updatePassword(passwords);
        } catch (UkelonnBadRequestException e) {
            logservice.log(LogService.LOG_WARNING, String.format("REST endpoint /ukelonn/api/admin/user/password got bad request: %s", e.getMessage()));
            throw new BadRequestException(e.getMessage());
        } catch (UkelonnException e) {
            logservice.log(LogService.LOG_ERROR, String.format("REST endpoint /ukelonn/api/admin/user/password got bad request: %s", e.getMessage()));
            throw new InternalServerErrorException("See log for error details");
        }
    }

}

/*
 * Copyright 2018-2021 Steinar Bang
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

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource extends ResourceBase {
    static final String USERNAME_MISSING_ERROR = "REST endpoint /ukelonn/api/account requires a username argument, and the request was missing a username argument";

    private LogService logservice;

    Logger logger;

    @Inject
    UkelonnService ukelonn;

    @Inject
    void setLogservice(LogService logservice) {
        this.logservice = logservice;
        this.logger = logservice.getLogger(getClass());
    }

    @GET
    @Path("{username}")
    public Account getAccount(@PathParam("username") String username) {
        if (username == null) {
            logger.warn(USERNAME_MISSING_ERROR);
            throw new BadRequestException(USERNAME_MISSING_ERROR);
        }

        if (!isCurrentUserOrAdmin(username, logservice)) {
            logger.warn(String.format("REST endpoint /ukelonn/api/account logged in user not allowed to fetch account for username %s", username));
            throw new ForbiddenException();
        }

        try {
            return ukelonn.getAccount(username);
        } catch (UkelonnException e) {
            logger.error(String.format("Internal Server Error in REST endpoint /ukelonn/api/account: %s", e.getMessage()), e);
            throw new InternalServerErrorException("Failed to get account from database. See log for details.");
        }
    }

}

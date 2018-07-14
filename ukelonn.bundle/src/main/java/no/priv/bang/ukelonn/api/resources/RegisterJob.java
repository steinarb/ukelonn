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

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.PerformedJob;

@Path("/registerjob")
@Produces(MediaType.APPLICATION_JSON)
public class RegisterJob extends ResourceBase {

    @Inject
    LogService logservice;

    @Inject
    UkelonnService ukelonn;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Account doRegisterJob(PerformedJob performedJob) {
        String username = performedJob.getAccount().getUsername();
        if (!isCurrentUserOrAdmin(username, logservice)) {
            logservice.log(LogService.LOG_WARNING, String.format("REST Endpoint /ukelonn/api/account logged in user not allowed to fetch account for username %s", username));
            throw new ForbiddenException();
        }

        return ukelonn.registerPerformedJob(performedJob);
    }

}

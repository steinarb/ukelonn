package no.priv.bang.ukelonn.api.resources;
/*
 * Copyright 2018-2026 Steinar Bang
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

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.Transaction;
import no.priv.bang.ukelonn.beans.UpdatedTransaction;

@Path("/job")
@Produces(MediaType.APPLICATION_JSON)
@RequiresUser
public class JobResource extends ResourceBase {
    private Logger logger = LoggerFactory.getLogger(JobResource.class);

    @Inject
    UkelonnService ukelonn;

    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Account doRegisterJob(PerformedTransaction performedJob) {
        var username = performedJob.account().username();
        if (!isCurrentUserOrAdmin(username)) {
            logger.warn(String.format("REST Endpoint /ukelonn/api/account logged in user not allowed to fetch account for username %s", username));
            throw new ForbiddenException();
        }

        return ukelonn.registerPerformedJob(performedJob);
    }

    @Path("/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Transaction> doUpdateJob(UpdatedTransaction editedJob) {
        try {
            return ukelonn.updateJob(editedJob);
        } catch (UkelonnException e) {
            logger.error("REST endpoint /api/job/update failed", e);
            throw new InternalServerErrorException("See log for details");
        }
    }
}

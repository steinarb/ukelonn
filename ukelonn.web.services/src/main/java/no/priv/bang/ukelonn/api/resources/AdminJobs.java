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
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.AccountWithJobIds;
import no.priv.bang.ukelonn.beans.Transaction;

@Path("/admin/jobs")
@Produces(MediaType.APPLICATION_JSON)
@RequiresUser
@RequiresRoles("ukelonnadmin")
public class AdminJobs {
    private Logger logger = LoggerFactory.getLogger(AdminJobs.class);

    @Inject
    UkelonnService ukelonn;

    @Path("delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Transaction> delete(AccountWithJobIds accountWithJobIds) {
        try {
            return ukelonn.deleteJobsFromAccount(accountWithJobIds.account().accountId(), accountWithJobIds.jobIds());
        } catch (Exception e) {
            String message = "REST endpoint /ukelonn/admin/jobs/delete failed with exception";
            logger.error(message, e);
            throw new InternalServerErrorException(String.format("%s, see log for details", message));
        }
    }

}

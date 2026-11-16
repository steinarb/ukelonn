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

import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.TransactionType;

@Path("/admin/jobtype")
@Produces(MediaType.APPLICATION_JSON)
@RequiresUser
@RequiresRoles("ukelonnadmin")
public class AdminJobtype {
    private Logger logger = LoggerFactory.getLogger(AdminJobtype.class);

    @Inject
    UkelonnService ukelonn;

    @Path("modify")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public List<TransactionType> modify(TransactionType jobtype) {
        try {
            return ukelonn.modifyJobtype(jobtype);
        } catch (UkelonnException e) {
            logger.error(String.format("REST endpoint /api/jobtype/modify failed to modify jobtype %d in the database", jobtype.id()), e);
            throw new InternalServerErrorException("See log for the cause of the problem");
        }
    }

    @Path("create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public List<TransactionType> create(TransactionType jobtype) {
        try {
            return ukelonn.createJobtype(jobtype);
        } catch (UkelonnException e) {
            logger.error(String.format("REST endpoint /api/jobtype/modify failed to create jobtype \"%s\" in the database", jobtype.transactionTypeName()), e);
            throw new InternalServerErrorException("See log for the cause of the problem");
        }
    }

}

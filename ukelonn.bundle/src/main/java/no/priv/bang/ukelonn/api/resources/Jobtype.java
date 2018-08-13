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

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.TransactionType;

@Path("/admin/jobtype")
@Produces("application/json")
public class Jobtype {

    @Inject
    UkelonnService ukelonn;

    @Inject
    LogService logservice;

    @Path("modify")
    @POST
    @Consumes("application/json")
    public List<TransactionType> modify(TransactionType jobtype) {
        try {
            return ukelonn.modifyJobtype(jobtype);
        } catch (UkelonnException e) {
            logservice.log(LogService.LOG_ERROR, String.format("REST endpoint /api/jobtype/modify failed to modify jobtype %d in the database", jobtype.getId()), e);
            throw new InternalServerErrorException("See log for the cause of the problem");
        }
    }

}

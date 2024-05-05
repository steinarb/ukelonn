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
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import no.priv.bang.ukelonn.UkelonnException;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.TransactionType;

@Path("/admin/paymenttype")
@Produces(MediaType.APPLICATION_JSON)
@RequiresUser
@RequiresRoles("ukelonnadmin")
public class AdminPaymenttype {

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
    public List<TransactionType> modify(TransactionType paymenttype) {
        try {
            return ukelonn.modifyPaymenttype(paymenttype);
        } catch (UkelonnException e) {
            logger.error(String.format("REST endpoint /api/paymenttype/modify failed to modify payment type %d in the database", paymenttype.id()), e);
            throw new InternalServerErrorException("See log for the cause of the problem");
        }
    }

    @Path("create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public List<TransactionType> create(TransactionType paymenttype) {
        try {
            return ukelonn.createPaymenttype(paymenttype);
        } catch (UkelonnException e) {
            logger.error(String.format("REST endpoint /api/jobtype/modify failed to create payment type \"%s\" in the database", paymenttype.transactionTypeName()), e);
            throw new InternalServerErrorException("See log for the cause of the problem");
        }
    }

}

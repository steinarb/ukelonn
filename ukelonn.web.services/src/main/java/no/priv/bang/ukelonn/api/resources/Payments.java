/*
 * Copyright 2018-2025 Steinar Bang
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

import static no.priv.bang.ukelonn.UkelonnConstants.INFINITE_SCROLL_PAGE_SIZE;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.authz.annotation.RequiresUser;

import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Transaction;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@RequiresUser
public class Payments extends ResourceBase {

    @Inject
    public UkelonnService ukelonn;

    @GET
    @Path("{accountId}")
    public List<Transaction> payments(
            @PathParam("accountId") int accountId, 
            @DefaultValue("0") @QueryParam("pagenumber") int pageNumber, 
            @DefaultValue(INFINITE_SCROLL_PAGE_SIZE) @QueryParam("pagesize") int pageSize) 
    {
        return ukelonn.getPayments(accountId, pageNumber, pageSize);
    }

}

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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.authz.annotation.RequiresUser;

import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Notification;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@RequiresUser
public class NotificationResource {

    @Inject
    UkelonnService ukelonn;

    @GET
    @Path("/notificationsto/{username}")
    public List<Notification> notificationsTo(@PathParam("username") String username) {
        return ukelonn.notificationsTo(username);
    }

    @POST
    @Path("/notificationto/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void notificationTo(@PathParam("username") String username, Notification notification) {
        ukelonn.notificationTo(username, notification);
    }

}

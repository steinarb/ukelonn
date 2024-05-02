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

import java.util.Optional;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.osgi.service.log.LogService;

public class ResourceBase {

    public ResourceBase() {
        super();
    }

    protected boolean isCurrentUserOrAdmin(String username, LogService logservice) {
        var logger = logservice.getLogger(getClass());
        try {
            var subject = Optional.ofNullable(SecurityUtils.getSubject());
            var isCurrentUser = subject
                .map(s -> (String)s.getPrincipal())
                .map(principal -> principal.equals(username))
                .orElse(false);
            return subject.map(s -> s.hasRole("ukelonnadmin") || isCurrentUser).orElse(false);
        } catch (Exception e) {
            var message = "Failure retrieving Shiro subject";
            logger.error(message, e);
            throw new InternalServerErrorException(message);
        }
    }

    protected Response response(int status, String message) {
        return Response
            .status(status)
            .entity(new ErrorMessage(status, message))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

}

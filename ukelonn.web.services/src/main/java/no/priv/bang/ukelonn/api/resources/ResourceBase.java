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

import javax.ws.rs.InternalServerErrorException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.osgi.service.log.LogService;

public class ResourceBase {

    public ResourceBase() {
        super();
    }

    protected boolean isCurrentUserOrAdmin(String username, LogService logservice) {
        try {
            Subject subject = SecurityUtils.getSubject();
            if (subject.getPrincipal() == null) {
                String message = "No user available from Shiro";
                logservice.log(LogService.LOG_ERROR, message);
                throw new InternalServerErrorException(message);
            }

            return
                subject.getPrincipal().equals(username) ||
                subject.hasRole("ukelonnadmin");
        } catch (Exception e) {
            String message = "Failure retrieving Shiro subject";
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new InternalServerErrorException(message);
        }
    }

}

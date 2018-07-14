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
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.api.beans.LoginCredentials;
import no.priv.bang.ukelonn.api.beans.LoginResult;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
public class Login {

    @Inject
    LogService logservice;

    @GET
    public LoginResult loginStatus() {
        Subject subject = SecurityUtils.getSubject();
        return createLoginResultFromSubject(subject);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public LoginResult doLogin(LoginCredentials credentials) {
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(credentials.getUsername(), credentials.getPassword().toCharArray(), true);
        try {
            subject.login(token);

            return createLoginResultFromSubject(subject);
        } catch(UnknownAccountException e) {
            logservice.log(LogService.LOG_WARNING, "Login error: unknown account", e);
            return new LoginResult("Unknown account");
        } catch (IncorrectCredentialsException  e) {
            logservice.log(LogService.LOG_WARNING, "Login error: wrong password", e);
            return new LoginResult("Wrong password");
        } catch (LockedAccountException  e) {
            logservice.log(LogService.LOG_WARNING, "Login error: locked account", e);
            return new LoginResult("Locked account");
        } catch (AuthenticationException e) {
            logservice.log(LogService.LOG_WARNING, "Login error: general authentication error", e);
            return new LoginResult("Unknown error");
        } catch (Exception e) {
            logservice.log(LogService.LOG_ERROR, "Login error: internal server error", e);
            throw new InternalServerErrorException();
        } finally {
            token.clear();
        }
    }

    private LoginResult createLoginResultFromSubject(Subject subject) {
        if (subject.isAuthenticated()) {
            String[] roles = { "user" };
            if (subject.hasRole("administrator")) {
                roles =  new String[]{ "administrator" };
            }

            String username = (String) subject.getPrincipal();
            return new LoginResult(username, roles);
        }

        return new LoginResult();
    }

}

/*
 * Copyright 2018-2021 Steinar Bang
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

import java.util.Base64;

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
import org.osgi.service.log.Logger;

import no.priv.bang.ukelonn.api.beans.LoginCredentials;
import no.priv.bang.ukelonn.api.beans.LoginResult;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
public class Login {

    Logger logger;

    @Inject
    void setLogservice(LogService logservice) {
        this.logger = logservice.getLogger(getClass());
    }

    @GET
    public LoginResult loginStatus() {
        Subject subject = SecurityUtils.getSubject();
        return createLoginResultFromSubject(subject);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public LoginResult doLogin(LoginCredentials credentials) {
        Subject subject = SecurityUtils.getSubject();
        var decodedPassword = new String(Base64.getDecoder().decode(credentials.getPassword()));

        UsernamePasswordToken token = new UsernamePasswordToken(credentials.getUsername(), decodedPassword, true);
        try {
            subject.login(token);

            return createLoginResultFromSubject(subject);
        } catch(UnknownAccountException e) {
            logger.warn("Login error: unknown account", e);
            return LoginResult.with()
                .errorMessage("Unknown account")
                .build();
        } catch (IncorrectCredentialsException  e) {
            logger.warn("Login error: wrong password", e);
            return LoginResult.with()
                .errorMessage("Wrong password")
                .build();
        } catch (LockedAccountException  e) {
            logger.warn("Login error: locked account", e);
            return LoginResult.with()
                .errorMessage("Locked account")
                .build();
        } catch (AuthenticationException e) {
            logger.warn("Login error: general authentication error", e);
            return LoginResult.with()
                .errorMessage("Unknown error")
                .build();
        } catch (Exception e) {
            logger.error("Login error: internal server error", e);
            throw new InternalServerErrorException();
        } finally {
            token.clear();
        }
    }

    private LoginResult createLoginResultFromSubject(Subject subject) {
        if (subject.isAuthenticated()) {
            String[] roles = { "user" };
            if (subject.hasRole("ukelonnadmin")) {
                roles =  new String[]{ "ukelonnadmin" };
            }

            String username = (String) subject.getPrincipal();
            return LoginResult.with()
                .username(username)
                .roles(roles)
                .build();
        }

        return LoginResult.with().build();
    }

}

/*
 * Copyright 2016-2018 Steinar Bang
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
package no.priv.bang.ukelonn.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.ByteSource.Util;

import no.priv.bang.ukelonn.UkelonnDatabase;

public class UkelonnRealm extends AuthorizingRealm {

    private UkelonnShiroFilter shiroFilter;

    public UkelonnRealm(UkelonnShiroFilter shiroFilter) {
        super();
        this.shiroFilter = shiroFilter;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Set<String> roles = new HashSet<>();
        roles.add("user");
        Set<String> administrators = new HashSet<>();
        try {
            UkelonnDatabase ukelonnDatabase = connectionCheck();
            PreparedStatement statement = ukelonnDatabase.prepareStatement("select * from administrators_view");
            try(ResultSet administratorsResults = ukelonnDatabase.query(statement)) {
                while (administratorsResults.next()) {
                    administrators.add(administratorsResults.getString("username"));
                }
            }
        } catch (Exception e) {
            throw new AuthorizationException(e);
        }

        Collection<String> usernames = principals.byType(String.class);
        boolean allPrincipalsAreAdministrators = true;
        for (String username : usernames) {
            allPrincipalsAreAdministrators &= administrators.contains(username);
        }

        if (allPrincipalsAreAdministrators) {
            roles.add("administrator");
        }

        return new SimpleAuthorizationInfo(roles);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        if (!(token instanceof UsernamePasswordToken)) {
            throw new AuthenticationException("UkelonnRealm shiro realm only accepts UsernamePasswordToken");
        }

        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        Object principal = usernamePasswordToken.getPrincipal();
        String username = usernamePasswordToken.getUsername();
        try {
            UkelonnDatabase ukelonnDatabase = connectionCheck();
            PreparedStatement statement = ukelonnDatabase.prepareStatement("select * from users where username=?");
            statement.setString(1, username);
            try(ResultSet passwordResultSet = ukelonnDatabase.query(statement)) {
                if (passwordResultSet == null) {
                    throw new AuthenticationException("UkelonnRealm shiro realm failed to get passwords from the database");
                }

                if (passwordResultSet.next()) {
                    String password = passwordResultSet.getString("password");
                    String salt = passwordResultSet.getString("salt");
                    ByteSource decodedSalt = Util.bytes(Base64.getDecoder().decode(salt));
                    return new SimpleAuthenticationInfo(principal, password, decodedSalt, getName());
                } else {
                    throw new IncorrectCredentialsException("Username \"" + username + "\" not found");
                }
            }
        } catch (SQLException e) {
            throw new AuthenticationException("UkelonnRealm shiro realm got SQL error exploring the password results", e);
        }
    }

    private UkelonnDatabase connectionCheck() {
        if (shiroFilter == null) {
            throw new AuthenticationException("UkelonnRealm shiro realm unable to find the ShiroFilterProvider, giving up");
        }

        UkelonnDatabase database = shiroFilter.getDatabase();
        if (database == null) {
            throw new AuthenticationException("UkelonnRealm shiro realm unable to find OSGi service UkelonnDatabase, giving up");
        }

        return database;
    }

}

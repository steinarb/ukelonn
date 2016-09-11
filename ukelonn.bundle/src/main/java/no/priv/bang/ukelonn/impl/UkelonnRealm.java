package no.priv.bang.ukelonn.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnService;

public class UkelonnRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (!(token instanceof UsernamePasswordToken)) {
            throw new AuthenticationException("UkelonnRealm shiro realm only accepts UsernamePasswordToken");
        }

        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        Object principal = usernamePasswordToken.getPrincipal();
        String username = usernamePasswordToken.getUsername();
        StringBuffer passwordQuery = new StringBuffer("select * from users where username='");
        passwordQuery.append(username);
        passwordQuery.append("'");
        UkelonnDatabase ukelonnDatabase = connectionCheck();
        ResultSet passwordResultSet = ukelonnDatabase.query(passwordQuery.toString());
        if (passwordResultSet == null) {
            throw new AuthenticationException("UkelonnRealm shiro realm failed to get passwords from the database");
        }

        try {
            if (passwordResultSet.next()) {
                String password = passwordResultSet.getString("password");
                return new SimpleAuthenticationInfo(principal, password, getName());
            } else {
                throw new IncorrectCredentialsException("Username \"" + username + "\" not found");
            }
        } catch (SQLException e) {
            throw new AuthenticationException("UkelonnRealm shiro realm got SQL error exploring the password results", e);
        }
    }

    private UkelonnDatabase connectionCheck() {
        UkelonnService ukelonnService = UkelonnServiceProvider.getInstance();
        if (ukelonnService == null) {
            throw new AuthenticationException("UkelonnRealm shiro realm unable to find OSGi service Ukelonnservice, giving up");
        }

        UkelonnDatabase database = ukelonnService.getDatabase();
        if (database == null) {
            throw new AuthenticationException("UkelonnRealm shiro realm unable to find OSGi service UkelonnDatabase, giving up");
        }

        return database;
    }

}

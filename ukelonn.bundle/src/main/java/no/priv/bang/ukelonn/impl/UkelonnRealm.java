package no.priv.bang.ukelonn.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
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

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnService;

public class UkelonnRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    	Set<String> roles = new HashSet<String>();
    	roles.add("user");
    	Set<String> administrators = new HashSet<String>();
    	try {
            UkelonnDatabase ukelonnDatabase = connectionCheck();
            ResultSet administratorsResults = ukelonnDatabase.query("select * from administrators_view");
            while (administratorsResults.next()) {
                administrators.add(administratorsResults.getString("username"));
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

    	SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo(roles);
        return authorizationInfo;
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

package no.priv.bang.ukelonn.backend.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource.Util;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import no.priv.bang.osgiservice.users.Permission;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.RolePermissions;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.osgiservice.users.UserRoles;
import no.priv.bang.ukelonn.UkelonnBadRequestException;
import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnException;

@Component(service=UserManagementService.class, immediate=true)
public class UserManagementServiceProvider implements UserManagementService {

    private static final String METHOD_NOT_IMPLMENENTED_MESSAGE = "Method not implmenented";
    private LogService logservice;
    private UkelonnDatabase database;

    @Reference
    public void setLogService(LogService logservice) {
        this.logservice = logservice;
    }

    @Reference
    public void setDatabase(UkelonnDatabase database) {
        this.database = database;
    }

    @Activate
    public void activate() {
        // Nothing to do here yet
    }

    @Override
    public User getUser(String username) {
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from users where username=?")) {
                statement.setString(1, username);
                try(ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        return getUserFromResultset(resultSet);
                    }

                    String message = String.format("User \"%s\" not found", username);
                    throw new UkelonnException(message);
                }
            }
        } catch (SQLException e) {
            String message = String.format("Failed to get user \"%s\" from the database", username);
            throw new UkelonnException(message, e);
        }
    }

    @Override
    public List<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from users order by user_id")) {
                try(ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        User user = getUserFromResultset(resultSet);
                        users.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new UkelonnException("Failed to get the list of users", e);
        }

        return users;
    }

    private User getUserFromResultset(ResultSet results) throws SQLException {
        int id = results.getInt(1);
        String username = results.getString(2);
        String email = results.getString(5);
        String firstname = results.getString(6);
        String lastname = results.getString(7);
        return new User(id, username, email, firstname, lastname);
    }

    @Override
    public List<User> modifyUser(User user) {
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement updateUserSql = connection.prepareStatement("update users set username=?, email=?, first_name=?, last_name=? where user_id=?")) {
                updateUserSql.setString(1, user.getUsername());
                updateUserSql.setString(2, user.getEmail());
                updateUserSql.setString(3, user.getFirstname());
                updateUserSql.setString(4, user.getLastname());
                updateUserSql.setInt(5, user.getUserid());
                updateUserSql.executeUpdate();
            }
        } catch (SQLException e) {
            throw new UkelonnException(String.format("Failed to update user %d in the database", user.getUserid()), e);
        }

        return getUsers();
    }

    @Override
    public List<User> updatePassword(UserAndPasswords userAndPasswords) {
        if (!hasUserWithNonEmptyUsername(userAndPasswords)) {
            String message = "Empty username when changing password";
            logservice.log(LogService.LOG_WARNING, String.format("Bad request: %s", message));
            throw new UkelonnBadRequestException(message);
        }

        if (!passwordsEqualsAndNotEmpty(userAndPasswords)) {
            String message = String.format("Passwords don't match and/or are empty when changing passwords for user \"%s\"", userAndPasswords.getUser().getUsername());
            logservice.log(LogService.LOG_WARNING, String.format("Bad request: %s", message));
            throw new UkelonnBadRequestException(message);
        }

        String username = userAndPasswords.getUser().getUsername();
        String password = userAndPasswords.getPassword1();
        String salt = getNewSalt();
        String hashedPassword = hashPassword(password, salt);
        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("update users set password=?, salt=? where username=?")) { // NOSONAR It's hard to handle passwords without using the text password
                statement.setString(1, hashedPassword);
                statement.setString(2, salt);
                statement.setString(3, username);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            String message = String.format("Database failure when changing password for user \"%s\"", userAndPasswords.getUser().getUsername());
            logservice.log(LogService.LOG_ERROR, message);
            throw new UkelonnException(message);
        }

        return getUsers();
    }

    @Override
    public List<User> addUser(UserAndPasswords passwords) {
        if (!passwordsEqualsAndNotEmpty(passwords)) {
            throw new UkelonnBadRequestException("Passwords are not identical and/or empty");
        }

        String newUserUsername = passwords.getUser().getUsername();
        String newUserPassword = passwords.getPassword1();
        String newUserEmail = passwords.getUser().getEmail();
        String newUserFirstname = passwords.getUser().getFirstname();
        String newUserLastname = passwords.getUser().getLastname();
        String salt = getNewSalt();
        String hashedPassword = hashPassword(newUserPassword, salt);

        try(Connection connection = database.getConnection()) {
            try(PreparedStatement insertUserSql = connection.prepareStatement("insert into users (username, password, salt, email, first_name, last_name) values (?, ?, ?, ?, ?, ?)")) {
                insertUserSql.setString(1, newUserUsername);
                insertUserSql.setString(2, hashedPassword);
                insertUserSql.setString(3, salt);
                insertUserSql.setString(4, newUserEmail);
                insertUserSql.setString(5, newUserFirstname);
                insertUserSql.setString(6, newUserLastname);
                insertUserSql.executeUpdate();
            }

            return getUsers();
        } catch (SQLException e) {
            String message = "Database exception when creating user";
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new UkelonnException(message, e);
        }
    }

    @Override
    public List<Role> getRoles() {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLMENENTED_MESSAGE);
    }

    @Override
    public List<Role> modifyRole(Role role) {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLMENENTED_MESSAGE);
    }

    @Override
    public List<Role> addRole(Role newRole) {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLMENENTED_MESSAGE);
    }

    @Override
    public List<Permission> getPermissions() {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLMENENTED_MESSAGE);
    }

    @Override
    public List<Permission> modifyPermission(Permission permission) {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLMENENTED_MESSAGE);
    }

    @Override
    public List<Permission> addPermission(Permission newPermission) {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLMENENTED_MESSAGE);
    }

    @Override
    public Map<String, List<Role>> getUserRoles() {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLMENENTED_MESSAGE);
    }

    @Override
    public Map<String, List<Role>> addUserRoles(UserRoles userroles) {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLMENENTED_MESSAGE);
    }

    @Override
    public Map<String, List<Role>> removeUserRoles(UserRoles userroles) {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLMENENTED_MESSAGE);
    }

    @Override
    public Map<String, List<Permission>> getRolesPermissions() {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLMENENTED_MESSAGE);
    }

    @Override
    public Map<String, List<Permission>> addRolePermissions(RolePermissions rolepermissions) {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLMENENTED_MESSAGE);
    }

    @Override
    public Map<String, List<Permission>> removeRolePermissions(RolePermissions rolepermissions) {
        throw new UnsupportedOperationException(METHOD_NOT_IMPLMENENTED_MESSAGE);
    }

    static String getNewSalt() {
        RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
        return randomNumberGenerator.nextBytes().toBase64();
    }

    static String hashPassword(String newUserPassword, String salt) {
        Object decodedSaltUsedWhenHashing = Util.bytes(Base64.getDecoder().decode(salt));
        return new Sha256Hash(newUserPassword, decodedSaltUsedWhenHashing, 1024).toBase64();
    }

    static boolean passwordsEqualsAndNotEmpty(UserAndPasswords passwords) {
        if (passwords.getPassword1() == null || passwords.getPassword1().isEmpty()) {
            return false;
        }

        return passwords.getPassword1().equals(passwords.getPassword2());
    }

    static boolean hasUserWithNonEmptyUsername(UserAndPasswords passwords) {
        User user = passwords.getUser();
        if (user == null) {
            return false;
        }

        String username = user.getUsername();
        if (username == null) {
            return false;
        }

        return !username.isEmpty();
    }

}

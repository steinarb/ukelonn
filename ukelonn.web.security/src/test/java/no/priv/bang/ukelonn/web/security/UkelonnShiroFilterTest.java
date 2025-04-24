package no.priv.bang.ukelonn.web.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.authservice.db.liquibase.test.TestLiquibaseRunner;
import no.priv.bang.authservice.definitions.CipherKeyService;
import no.priv.bang.authservice.web.security.dbrealm.AuthserviceDbRealm;
import no.priv.bang.authservice.web.security.memorysession.MemorySession;

class UkelonnShiroFilterTest {

    @Test
    void testAuthenticate() throws Exception {
        var shirofilter = new UkelonnShiroFilter();
        var realm = new AuthserviceDbRealm();
        var session = new MemorySession();
        session.activate();
        var cipherKeyService = mock(CipherKeyService.class);
        var datasource = createDataSource("authservice1");
        addUserDatabaseSchemaAndPopulateWithTestUsersAndGroups(datasource);
        realm.setDataSource(datasource);
        realm.activate();
        shirofilter.setSession(session);
        shirofilter.setRealm(realm);
        shirofilter.setCipherKeyService(cipherKeyService);
        shirofilter.activate();
        var securitymanager = shirofilter.getSecurityManager();
        var token = new UsernamePasswordToken("jad", "1ad".toCharArray());
        var info = securitymanager.authenticate(token);
        assertEquals(1, info.getPrincipals().asList().size());
    }

    private void addUserDatabaseSchemaAndPopulateWithTestUsersAndGroups(DataSource datasource) throws SQLException {
        var authserviceDbContent = new TestLiquibaseRunner();
        authserviceDbContent.activate();
        authserviceDbContent.prepare(datasource);
    }

    private DataSource createDataSource(String dbname) throws Exception {
        var derbyDataSourceFactory = new DerbyDataSourceFactory();
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        return derbyDataSourceFactory.createDataSource(properties);
    }

}

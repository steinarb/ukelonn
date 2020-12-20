/*
 * Copyright 2016-2019 Steinar Bang
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
package no.priv.bang.ukelonn.testutils;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

import javax.sql.DataSource;

import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.db.liquibase.test.TestLiquibaseRunner;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;

/**
 * Contains static methods used in more than one unit test.
 *
 * @author Steinar Bang
 *
 */
public class TestUtils {

    private static UkelonnServiceProvider ukelonnServiceSingleton;

    public static UkelonnServiceProvider getUkelonnServiceSingleton() {
        ukelonnServiceSingleton.setUserAdmin(null); // Set to null so that usage will fail with NPE until replaced by mock
        return ukelonnServiceSingleton;
    }

    /**
     * Get a {@link File} referencing a resource.
     *
     * @param resource the name of the resource to get a File for
     * @return a {@link File} object referencing the resource
     * @throws URISyntaxException
     */
    public static File getResourceAsFile(String resource) throws URISyntaxException {
        return Paths.get(TestUtils.class.getResource(resource).toURI()).toFile();
    }

    /***
     * Fake injected OSGi services.
     * @return the serviceprovider implmenting the UkelonnService
     * @throws Exception
     */
    public static UkelonnServiceProvider setupFakeOsgiServices() throws Exception {
        ukelonnServiceSingleton = new UkelonnServiceProvider();
        LogService logservice = new MockLogService();
        DataSource ukelonnDatasource = createUkelonnDatasource(logservice);
        UserManagementService useradmin = mock(UserManagementService.class);

        ukelonnServiceSingleton.setDataSource(ukelonnDatasource);
        ukelonnServiceSingleton.setLogservice(logservice);
        ukelonnServiceSingleton.setUserAdmin(useradmin);
        ukelonnServiceSingleton.activate(Collections.singletonMap("defaultlocale", "nb_NO"));
        return ukelonnServiceSingleton;
    }

    /***
     * Clear any (fake or non-fake) injected OSGi services.
     * @throws Exception
     */
    public static void releaseFakeOsgiServices() throws Exception {
        rollbackMockDataInTestDatabase();
        UkelonnServiceProvider ukelonnService = new UkelonnServiceProvider();
        if (ukelonnService != null) {
            ukelonnService.setDataSource(null); // Release the database
        }
    }

    public static void restoreTestDatabase() throws Exception {
        rollbackMockDataInTestDatabase();
        DataSource ukelonnDatasource = createUkelonnDatasource(new MockLogService());
        getUkelonnServiceSingleton().setDataSource(ukelonnDatasource);
    }

    public static void rollbackMockDataInTestDatabase() throws Exception {
        DataSource ukelonnDatasource = null;
        try {
            ukelonnDatasource = ukelonnServiceSingleton.getDataSource();
        } catch (Exception e) {
            // Swallow exception and continue
        }

        if (ukelonnDatasource == null) {
            ukelonnDatasource = createUkelonnDatasource(new MockLogService());
        }

        TestLiquibaseRunner runner = createLiquibaseRunner(new MockLogService());
        runner.rollbackMockData(ukelonnDatasource);
    }

    static DataSource createUkelonnDatasource(LogService logservice) throws SQLException {
        DerbyDataSourceFactory datasourceFactory = new DerbyDataSourceFactory();
        Properties derbyDbCredentials = createDerbyMemoryDbCredentials();
        DataSource ukelonnDatasource = datasourceFactory.createDataSource(derbyDbCredentials);
        TestLiquibaseRunner runner = createLiquibaseRunner(logservice);
        runner.prepare(ukelonnDatasource);
        return ukelonnDatasource;
    }

    static TestLiquibaseRunner createLiquibaseRunner(LogService logservice) {
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        runner.setLogService(logservice);
        runner.activate();
        return runner;
    }

    private static Properties createDerbyMemoryDbCredentials() {
        Properties properties = new Properties();
        properties.put(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:ukelonn;create=true");
        return properties;
    }

}

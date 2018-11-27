/*
 * Copyright 2016-2017 Steinar Bang
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
package no.priv.bang.ukelonn.tests;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.web.itest.base.client.HttpTestClient;
import org.ops4j.pax.web.itest.base.client.HttpTestClientFactory;

import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.UkelonnDatabase;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class UkelonnServiceIntegrationTest extends UkelonnServiceIntegrationTestBase {
    public static final String RMI_SERVER_PORT = "44445";
    public static final String RMI_REG_PORT = "1100";

    @Inject
    private UkelonnService ukelonnService;

    @Inject
    private UkelonnDatabase database;

    @Configuration
    public Option[] config() {
        final String jmxPort = freePortAsString();
        final String httpPort = freePortAsString();
        final String httpsPort = freePortAsString();
        final MavenArtifactUrlReference karafUrl = maven().groupId("org.apache.karaf").artifactId("apache-karaf-minimal").type("zip").versionAsInProject();
        final MavenArtifactUrlReference paxJdbcRepo = maven().groupId("org.ops4j.pax.jdbc").artifactId("pax-jdbc-features").versionAsInProject().type("xml").classifier("features");
        final MavenArtifactUrlReference ukelonnFeatureRepo = maven().groupId("no.priv.bang.ukelonn").artifactId("karaf").versionAsInProject().type("xml").classifier("features");
        return options(
            karafDistributionConfiguration().frameworkUrl(karafUrl).unpackDirectory(new File("target/exam")).useDeployFolder(false).runEmbedded(true),
            configureConsole().ignoreLocalConsole().ignoreRemoteShell(),
            systemTimeout(720000),
            keepRuntimeFolder(),
            logLevel(LogLevel.DEBUG),
            editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", RMI_REG_PORT),
            editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", RMI_SERVER_PORT),
            editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", httpPort),
            editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port.secure", httpsPort),
            replaceConfigurationFile("etc/org.ops4j.pax.logging.cfg", getConfigFile("/etc/org.ops4j.pax.logging.cfg")),
            systemProperty("org.ops4j.pax.logging.DefaultSer‌​viceLog.level").value("DEBUG"),
            vmOptions("-Dtest-jmx-port=" + jmxPort),
            junitBundles(),
            mavenBundle("org.apache.shiro", "shiro-core").versionAsInProject(),
            features(paxJdbcRepo),
            features(ukelonnFeatureRepo, "ukelonn-with-derby"));
    }

    @Test
    public void ukelonnServiceIntegrationTest() {
        // Verify that the service could be injected
        assertNotNull(ukelonnService);
        assertEquals("Hello world!", ukelonnService.getMessage());
    }

    @Test
    public void testDerbyTestDatabase() throws SQLException {
        PreparedStatement statement = database.prepareStatement("select * from accounts_view where username=?");
        statement.setString(1, "jad");
        ResultSet onAccount = database.query(statement);
        assertNotNull(onAccount);
        assertTrue(onAccount.next()); // Verify that there is at least one result
        int account_id = onAccount.getInt("account_id");
        int user_id = onAccount.getInt("user_id");
        String username = onAccount.getString("username");
        String first_name = onAccount.getString("first_name");
        String last_name = onAccount.getString("last_name");
        assertEquals(4, account_id);
        assertEquals(4, user_id);
        assertEquals("jad", username);
        assertEquals("Jane", first_name);
        assertEquals("Doe", last_name);
    }

    @Ignore("I haven't been able to make this one work yet but hope I will.")
    @Test
    public void webappAccessTest() throws Exception {
        HttpTestClient testclient = HttpTestClientFactory.createDefaultTestClient();
        try {
            testclient.doGET("http://localhost:8081/ukelonn/").withReturnCode(404);
            String response = testclient.executeTest();
            assertEquals("", response);
        } finally {
            testclient = null;
        }
    }
}

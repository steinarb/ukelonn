package no.priv.bang.ukelonn.tests;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.web.itest.base.HttpTestClient;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.UkelonnDatabase;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class UkelonnServiceIntegrationTest extends UkelonnServiceIntegrationTestBase {
    Bundle installWarBundle;

    @Inject
    private UkelonnDatabase database;

    @Inject
    BundleContext bundleContext;

    UkelonnService ukelonnService;
    ServiceListener ukelonnServiceListener;

    @Configuration
    public Option[] config() {
        final MavenArtifactUrlReference karafUrl = maven().groupId("org.apache.karaf").artifactId("apache-karaf-minimal").type("zip").versionAsInProject();
        final MavenArtifactUrlReference paxJdbcRepo = maven().groupId("org.ops4j.pax.jdbc").artifactId("pax-jdbc-features").versionAsInProject().type("xml").classifier("features");
        final MavenArtifactUrlReference ukelonnFeatureRepo = maven().groupId("no.priv.bang.ukelonn").artifactId("ukelonn.karaf").versionAsInProject().type("xml").classifier("features");
        return options(
            karafDistributionConfiguration().frameworkUrl(karafUrl).unpackDirectory(new File("target/exam")).useDeployFolder(false),
            configureConsole().ignoreLocalConsole().ignoreRemoteShell(),
            junitBundles(),
            features(paxJdbcRepo),
            features(ukelonnFeatureRepo, "ukelonn-db-derby-test", "ukelonn"));
    }

    @Before
    public void setup() throws BundleException {
        // Can't use injection for the Webapp service since the bundle isn't
        // started until setUp and all injections are resolved
        // before the tests start.
        //
        // Creating a service listener instead.
        ukelonnServiceListener = new ServiceListener() {

	    	@Override
                public void serviceChanged(ServiceEvent event) {
                    @SuppressWarnings("rawtypes")
                        ServiceReference sr = event.getServiceReference();
                    @SuppressWarnings("unchecked")
                        Object rawService = bundleContext.getService(sr);
                    if (rawService instanceof UkelonnService) {
                        UkelonnService service = (UkelonnService) rawService;
                        switch(event.getType()) {
                          case ServiceEvent.REGISTERED:
                            ukelonnService = service;
                            break;
                          default:
                            break;
                        }
                    }
                }
            };
        bundleContext.addServiceListener(ukelonnServiceListener);

        // The war bundle has to be manually started or it won't work
        final String bundlePath = "mvn:no.priv.bang.ukelonn/ukelonn.bundle/" + getMavenProjectVersion() + "/war";
        installWarBundle = bundleContext.installBundle(bundlePath);
        installWarBundle.start();
    }

    @After
    public void tearDown() throws BundleException {
    	installWarBundle.stop();
    }

    @Test
    public void ukelonnServiceIntegrationTest() {
    	// Verify that the service could be injected
    	assertNotNull(ukelonnService);
    	assertEquals("Hello world!", ukelonnService.getMessage());
    }

    @Test
    public void testDerbyTestDatabase() throws SQLException {
        ResultSet onAccount = database.query("select * from accounts_view where username='jad'");
        assertNotNull(onAccount);
        while (onAccount.next()) {
            int account_id = onAccount.getInt("account_id");
            int user_id = onAccount.getInt("user_id");
            String username = onAccount.getString("username");
            String first_name = onAccount.getString("first_name");
            String last_name = onAccount.getString("last_name");
            assertEquals(3, account_id);
            assertEquals(3, user_id);
            assertEquals("jad", username);
            assertEquals("Jane", first_name);
            assertEquals("Doe", last_name);
        }
    }

    @Ignore
    @Test
    public void webappAccessTest() throws Exception {
    	Thread.sleep(20*1000);
        HttpTestClient testclient = new HttpTestClient();
    	try {
            String response = testclient.testWebPath("http://localhost:8081/ukelonn/", 404);
            assertEquals("", response);
    	} finally {
            testclient.close();
            testclient = null;
    	}
    }

}

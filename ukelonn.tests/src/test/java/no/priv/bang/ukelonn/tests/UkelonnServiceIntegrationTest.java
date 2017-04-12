package no.priv.bang.ukelonn.tests;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
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
        return options(
            junitBundles(),
            systemProperty("logback.configurationFile").value("file:src/test/resources/logback.xml"),
            systemProperty("org.osgi.service.http.port").value("8081"),
            systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
            systemProperty("org.osgi.service.http.hostname").value("127.0.0.1"),
            systemProperty("org.osgi.service.http.port").value("8181"),
            systemProperty("java.protocol.handler.pkgs").value("org.ops4j.pax.url"),
            systemProperty("org.ops4j.pax.url.war.importPaxLoggingPackages").value("true"),
            systemProperty("org.ops4j.pax.web.log.ncsa.enabled").value("true"),
            systemProperty("org.ops4j.pax.web.log.ncsa.directory").value("target/logs"),
            systemProperty("org.ops4j.pax.web.jsp.scratch.dir").value("target/paxexam/scratch-dir"),
            systemProperty("org.ops4j.pax.url.mvn.certificateCheck").value("false"),
            mavenBundle("org.ops4j.pax.logging", "pax-logging-api").versionAsInProject(),
            mavenBundle("org.ops4j.pax.logging", "pax-logging-service").versionAsInProject(),
            mavenBundle("org.ops4j.pax.url", "pax-url-war").versionAsInProject().type("jar").classifier("uber"),
            mavenBundle("org.ops4j.pax.web", "pax-web-spi").versionAsInProject(),
            mavenBundle("org.ops4j.pax.web", "pax-web-api").versionAsInProject(),
            mavenBundle("org.ops4j.pax.web", "pax-web-extender-war").versionAsInProject(),
            mavenBundle("org.ops4j.pax.web", "pax-web-extender-whiteboard").versionAsInProject(),
            mavenBundle("org.ops4j.pax.web", "pax-web-runtime").versionAsInProject(),
            mavenBundle("org.ops4j.pax.web", "pax-web-jsp").versionAsInProject(),
            mavenBundle("org.ops4j.pax.web.itest", "pax-web-itest-base").versionAsInProject(),
            mavenBundle("org.eclipse.jdt.core.compiler", "ecj"),
            mavenBundle("org.apache.xbean", "xbean-reflect"),
            mavenBundle("org.apache.xbean", "xbean-finder"),
            mavenBundle("org.apache.xbean", "xbean-bundleutils"),
            mavenBundle("org.ow2.asm", "asm-all").versionAsInProject(),
            mavenBundle("commons-codec", "commons-codec"),
            mavenBundle("org.apache.felix", "org.apache.felix.eventadmin"),
            mavenBundle("org.apache.httpcomponents", "httpcore-osgi"),
            mavenBundle("org.apache.httpcomponents", "httpmime"),
            mavenBundle("org.apache.httpcomponents", "httpclient-osgi"),
            mavenBundle("javax.servlet", "javax.servlet-api").versionAsInProject(),
            mavenBundle("org.ops4j.pax.web", "pax-web-jetty").versionAsInProject(),
            mavenBundle("org.eclipse.jetty", "jetty-util").versionAsInProject(),
            mavenBundle("org.eclipse.jetty", "jetty-io").versionAsInProject(),
            mavenBundle("org.eclipse.jetty", "jetty-http").versionAsInProject(),
            mavenBundle("org.eclipse.jetty", "jetty-continuation").versionAsInProject(),
            mavenBundle("org.eclipse.jetty", "jetty-server").versionAsInProject(),
            mavenBundle("org.eclipse.jetty", "jetty-client").versionAsInProject(),
            mavenBundle("org.eclipse.jetty", "jetty-security").versionAsInProject(),
            mavenBundle("org.eclipse.jetty", "jetty-xml").versionAsInProject(),
            mavenBundle("org.eclipse.jetty", "jetty-servlet").versionAsInProject(),
            mavenBundle("commons-beanutils", "commons-beanutils").versionAsInProject(),
            mavenBundle("commons-collections", "commons-collections").versionAsInProject(),
            mavenBundle("org.apache.derby", "derby"),
            mavenBundle("org.ops4j.pax.jdbc", "pax-jdbc-derby"),
            mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.commons-digester"),
            mavenBundle("org.apache.servicemix.specs", "org.apache.servicemix.specs.jsr303-api-1.0.0"),
            mavenBundle("org.apache.servicemix.specs", "org.apache.servicemix.specs.jsr250-1.0"),
            mavenBundle("org.apache.geronimo.bundles", "commons-discovery"),
            mavenBundle("javax.enterprise", "cdi-api"),
            mavenBundle("javax.interceptor", "javax.interceptor-api"),
            mavenBundle("javax.validation", "validation-api"),
            mavenBundle("org.apache.myfaces.core", "myfaces-api"),
            mavenBundle("org.apache.myfaces.core", "myfaces-impl"),
            mavenBundle("no.priv.bang.ukelonn.rebundled", "ukelonn.rebundled.com.vaadin.external.gwt.gwt-user").versionAsInProject(),
            mavenBundle("com.vaadin", "vaadin-shared").versionAsInProject(),
            mavenBundle("com.vaadin", "vaadin-server").versionAsInProject(),
            mavenBundle("com.vaadin", "vaadin-themes").versionAsInProject(),
            mavenBundle("com.vaadin", "vaadin-client-compiled").versionAsInProject(),
            mavenBundle("com.vaadin.external.google", "guava").versionAsInProject(),
            mavenBundle("com.vaadin.external.flute", "flute").versionAsInProject(),
            mavenBundle("com.vaadin.external.streamhtmlparser", "streamhtmlparser-jsilver").versionAsInProject(),
            mavenBundle("no.priv.bang.ukelonn.rebundled", "ukelonn.rebundled.com.vaadin.addon.vaadin-touchkit-agpl").versionAsInProject(),
            mavenBundle("org.jsoup", "jsoup").versionAsInProject(),
            mavenBundle("org.ops4j.pax.shiro", "pax-shiro-faces"),
            mavenBundle("org.apache.shiro", "shiro-core").versionAsInProject(),
            mavenBundle("org.apache.shiro", "shiro-web").versionAsInProject(),
            mavenBundle("no.priv.bang.ukelonn", "ukelonn.api"),
            mavenBundle("no.priv.bang.ukelonn", "ukelonn.bundle.test.db"));
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

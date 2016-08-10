package no.priv.bang.ukelonn.tests;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import no.priv.bang.ukelonn.Webapp;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class WebappIntegrationTest extends WebappIntegrationtestBase {
    String paxSwissboxVersion = "1.8.2";
    String paxWebVersion = "4.2.7";
    String xbeanVersion = "4.1";
    String httpcomponentsVersion = "4.3.3";
    String jettyVersion = "9.2.17.v20160517";

    @Inject
    private Webapp webappService;

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
                       mavenBundle("org.ops4j.pax.logging", "pax-logging-api", paxSwissboxVersion),
                       mavenBundle("org.ops4j.pax.logging", "pax-logging-service", paxSwissboxVersion),
                       mavenBundle("org.ops4j.pax.url", "pax-url-war", "2.4.7"),
                       mavenBundle("org.ops4j.pax.web", "pax-web-spi", paxWebVersion),
                       mavenBundle("org.ops4j.pax.web", "pax-web-api", paxWebVersion),
                       mavenBundle("org.ops4j.pax.web", "pax-web-extender-war", paxWebVersion),
                       mavenBundle("org.ops4j.pax.web", "pax-web-extender-whiteboard", paxWebVersion),
                       mavenBundle("org.ops4j.pax.web", "pax-web-runtime", paxWebVersion),
                       mavenBundle("org.ops4j.pax.web", "pax-web-jsp", paxWebVersion),
                       mavenBundle("org.eclipse.jdt.core.compiler", "ecj", "4.4"),
                       mavenBundle("org.apache.xbean", "xbean-reflect", xbeanVersion),
                       mavenBundle("org.apache.xbean", "xbean-finder", xbeanVersion),
                       mavenBundle("org.apache.xbean", "xbean-bundleutils", xbeanVersion),
                       mavenBundle("org.ow2.asm", "asm-all", "5.0.2"),
                       mavenBundle("commons-codec", "commons-codec", "1.10"),
                       mavenBundle("org.apache.felix", "org.apache.felix.eventadmin", "1.3.2"),
                       mavenBundle("org.apache.httpcomponents", "httpcore", httpcomponentsVersion),
                       mavenBundle("org.apache.httpcomponents", "httpmime", httpcomponentsVersion),
                       mavenBundle("org.apache.httpcomponents", "httpclient", httpcomponentsVersion),
                       mavenBundle("javax.servlet", "javax.servlet-api", "3.1.0"),
                       mavenBundle("org.ops4j.pax.web", "pax-web-jetty", paxWebVersion),
                       mavenBundle("org.eclipse.jetty", "jetty-util", jettyVersion),
                       mavenBundle("org.eclipse.jetty", "jetty-io", jettyVersion),
                       mavenBundle("org.eclipse.jetty", "jetty-http", jettyVersion),
                       mavenBundle("org.eclipse.jetty", "jetty-continuation", jettyVersion),
                       mavenBundle("org.eclipse.jetty", "jetty-server", jettyVersion),
                       mavenBundle("org.eclipse.jetty", "jetty-client", jettyVersion),
                       mavenBundle("org.eclipse.jetty", "jetty-security", jettyVersion),
                       mavenBundle("org.eclipse.jetty", "jetty-xml", jettyVersion),
                       mavenBundle("org.eclipse.jetty", "jetty-servlet", jettyVersion),
                       mavenBundle("no.priv.bang.ukelonn", "ukelonn.bundle", getMavenProjectVersion()));
    }

    @Test
    public void modelstoreIntegrationTest() {
    	// Verify that the service could be injected
    	assertNotNull(webappService);
    	assertEquals("Hello world!", webappService.getMessage());
    }

}

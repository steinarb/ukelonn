package no.priv.bang.ukelonn.tests;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.MavenUtils.*;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import no.priv.bang.ukelonn.UkelonnService;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class UkelonnServiceIntegrationTest extends UkelonnServiceIntegrationTestBase {
    @Inject
    private BundleContext bundleContext;

    @Inject
    private UkelonnService ukelonnService;

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
                       mavenBundle("org.ops4j.pax.logging", "pax-logging-api").version(asInProject()),
                       mavenBundle("org.ops4j.pax.logging", "pax-logging-service").version(asInProject()),
                       mavenBundle("org.ops4j.pax.url", "pax-url-war").version(asInProject()).type("jar").classifier("uber"),
                       mavenBundle("org.ops4j.pax.web", "pax-web-spi").version(asInProject()),
                       mavenBundle("org.ops4j.pax.web", "pax-web-api").version(asInProject()),
                       mavenBundle("org.ops4j.pax.web", "pax-web-extender-war").version(asInProject()),
                       mavenBundle("org.ops4j.pax.web", "pax-web-extender-whiteboard").version(asInProject()),
                       mavenBundle("org.ops4j.pax.web", "pax-web-runtime").version(asInProject()),
                       mavenBundle("org.ops4j.pax.web", "pax-web-jsp").version(asInProject()),
                       mavenBundle("org.eclipse.jdt.core.compiler", "ecj").version(asInProject()),
                       mavenBundle("org.apache.xbean", "xbean-reflect").version(asInProject()),
                       mavenBundle("org.apache.xbean", "xbean-finder").version(asInProject()),
                       mavenBundle("org.apache.xbean", "xbean-bundleutils").version(asInProject()),
                       mavenBundle("org.ow2.asm", "asm-all").version(asInProject()),
                       mavenBundle("commons-codec", "commons-codec").version(asInProject()),
                       mavenBundle("org.apache.felix", "org.apache.felix.eventadmin").version(asInProject()),
                       mavenBundle("org.apache.httpcomponents", "httpcore").version(asInProject()),
                       mavenBundle("org.apache.httpcomponents", "httpmime").version(asInProject()),
                       mavenBundle("javax.servlet", "javax.servlet-api").version(asInProject()),
                       mavenBundle("org.ops4j.pax.web", "pax-web-jetty").version(asInProject()),
                       mavenBundle("org.eclipse.jetty", "jetty-util").version(asInProject()),
                       mavenBundle("org.eclipse.jetty", "jetty-io").version(asInProject()),
                       mavenBundle("org.eclipse.jetty", "jetty-http").version(asInProject()),
                       mavenBundle("org.eclipse.jetty", "jetty-continuation").version(asInProject()),
                       mavenBundle("org.eclipse.jetty", "jetty-server").version(asInProject()),
                       mavenBundle("org.eclipse.jetty", "jetty-client").version(asInProject()),
                       mavenBundle("org.eclipse.jetty", "jetty-security").version(asInProject()),
                       mavenBundle("org.eclipse.jetty", "jetty-xml").version(asInProject()),
                       mavenBundle("org.eclipse.jetty", "jetty-servlet").version(asInProject()),
                       mavenBundle("org.apache.commons", "commons-lang3", "3.3.2"),
                       mavenBundle("org.rendersnake", "rendersnake").version(asInProject()),
                       mavenBundle("no.priv.bang.ukelonn", "ukelonn.api", getMavenProjectVersion()),
                       mavenBundle("no.priv.bang.ukelonn", "ukelonn.bundle", getMavenProjectVersion()));
    }

    @Before
    public void start() throws BundleException {
    	for (Bundle bundle : bundleContext.getBundles()) {
            System.out.println("location: " + bundle.getLocation());
        }
    	final String rendersnakeBundlePath = "mvn:org.rendersnake/rendersnake/1.8";
        Bundle rendersnakeBundle = bundleContext.getBundle(rendersnakeBundlePath);
        try {
            rendersnakeBundle.start();
        } catch(Exception e) {
            System.out.println(e);
        }
    	final String paxurlwarBundlePath = "mvn:org.ops4j.pax.url/pax-url-war/2.4.7/jar/uber";
        Bundle paxwarBundle = bundleContext.getBundle(paxurlwarBundlePath);
        try {
            paxwarBundle.start();
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    @Test
    public void ukelonnServiceIntegrationTest() {
    	// Verify that the service could be injected
    	assertNotNull(ukelonnService);
    	assertEquals("Hello world!", ukelonnService.getMessage());
    }

}

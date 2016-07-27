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

    @Inject
    private Webapp webappService;

    @Configuration
    public Option[] config() {
        return options(
                       systemProperty("logback.configurationFile").value("file:src/test/resources/logback.xml"),
                       mavenBundle("org.slf4j", "slf4j-api", "1.7.2"),
                       mavenBundle("ch.qos.logback", "logback-core", "1.0.4"),
                       mavenBundle("ch.qos.logback", "logback-classic", "1.0.4"),
                       mavenBundle("com.fasterxml.jackson.core", "jackson-core", "2.5.3"),
                       mavenBundle("no.priv.bang.ukelonn", "ukelonn.bundle", getMavenProjectVersion()),
                       junitBundles());
    }

    @Test
    public void modelstoreIntegrationTest() {
    	// Verify that the service could be injected
    	assertNotNull(webappService);
    	assertEquals("Hello world!", webappService.getMessage());
    }

}

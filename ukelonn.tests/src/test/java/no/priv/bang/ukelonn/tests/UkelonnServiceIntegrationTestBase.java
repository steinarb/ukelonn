package no.priv.bang.ukelonn.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;


public class UkelonnServiceIntegrationTestBase {

    private String mavenProjectVersion;

    public UkelonnServiceIntegrationTestBase() {
    	try {
            Properties examProperties = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("exam.properties");
            examProperties.load(inputStream);
            mavenProjectVersion = examProperties.getProperty("maven.project.version");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMavenProjectVersion() {
        return mavenProjectVersion;
    }

    /***
     * Debug method that will print the location strings of a all bundles
     * in the bundle context given as an argument.
     *
     * @param bundleContext the BundleContext containing all bundles
     * @param printExportedPackages if true, also print the exported packages of each bundle
     */
    public void printBundleLocations(BundleContext bundleContext, boolean printExportedPackages) {
        for (Bundle bundle : bundleContext.getBundles()) {
            System.out.println("location: " + bundle.getLocation());
            if (printExportedPackages) {
                BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
                List<BundleCapability> capabilities = bundleWiring.getCapabilities("osgi.wiring.package");
                for (BundleCapability capability : capabilities) {
                    System.out.println("   " + capability.getAttributes().get("osgi.wiring.package") + ";" + capability.getAttributes().get("version"));
                }
            }
        }
    }

    /***
     * Debug method: Find a bundle using its location and then try to start the bundle
     * and print the error message from the start operation to the standard out.
     *
     * @param bundleContext where the bundle is located
     * @param location the location of the bundle in the bundleContext
     */
    public void tryStartingBundleAndPrintErrorTrace(BundleContext bundleContext, final String location) {
        Bundle testDbBundle = bundleContext.getBundle(location);
        try {
            testDbBundle.start();
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    /***
     * Debug method: Find a bundle using its location and then try to start the bundle
     * and print the error message from the start operation to the standard out.
     *
     * @param bundleContext where the bundle is located
     * @param location the location of the bundle in the bundleContext
     */
    public void printBundleExportedPackages(BundleContext bundleContext, final String location) {
        Bundle bundle = bundleContext.getBundle(location);
        System.out.println("Printing exported packages of: " + bundle.getLocation());
        BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        List<BundleCapability> capabilities = bundleWiring.getCapabilities("osgi.wiring.package");
        for (BundleCapability capability : capabilities) {
            System.out.println("   " + capability.getAttributes().get("osgi.wiring.package") + ";" + capability.getAttributes().get("version"));
        }
    }

}

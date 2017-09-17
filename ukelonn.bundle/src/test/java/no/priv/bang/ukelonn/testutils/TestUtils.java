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
package no.priv.bang.ukelonn.testutils;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;

import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;

import no.priv.bang.ukelonn.bundle.db.test.UkelonnDatabaseProvider;
import no.priv.bang.ukelonn.impl.ShiroFilterProvider;
import no.priv.bang.ukelonn.impl.UkelonnServletProvider;
import no.priv.bang.ukelonn.impl.UkelonnUI;
import no.priv.bang.ukelonn.mocks.MockLogService;

/**
 * Contains static methods used in more than one unit test.
 *
 * @author Steinar Bang
 *
 */
public class TestUtils {

    private static UkelonnServletProvider ukelonnServletProvider;

    public static UkelonnServletProvider getUkelonnServletProvider() {
        return ukelonnServletProvider;
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
     */
    public static void setupFakeOsgiServices() {
        ukelonnServletProvider = new UkelonnServletProvider();
        UkelonnDatabaseProvider ukelonnDatabaseProvider = new UkelonnDatabaseProvider();
        DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();
        ukelonnDatabaseProvider.setDataSourceFactory(derbyDataSourceFactory);
        LogService logservice = new MockLogService();
        ukelonnDatabaseProvider.setLogService(logservice);

        ukelonnServletProvider.setUkelonnDatabase(ukelonnDatabaseProvider.get());
        ukelonnServletProvider.setLogservice(logservice);

        ShiroFilterProvider shiroFilterProvider = new ShiroFilterProvider();
        shiroFilterProvider.setUkelonnDatabase(ukelonnDatabaseProvider.get());
    }

    /***
     * Clear any (fake or non-fake) injected OSGi services.
     *
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void releaseFakeOsgiServices() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        UkelonnServletProvider ukelonnService = (UkelonnServletProvider) UkelonnServletProvider.getInstance();
        if (ukelonnService != null) {
            ukelonnService.setUkelonnDatabase(null); // Release the database

            // Release the UkelonnService
            Field ukelonnServiceInstanceField = UkelonnServletProvider.class.getDeclaredField("instance");
            ukelonnServiceInstanceField.setAccessible(true);
            ukelonnServiceInstanceField.set(null, null);
        }

        dropTestDatabase();
    }

    public static void dropTestDatabase() {
        try {
            DriverManager.getConnection("jdbc:derby:memory:ukelonn;drop=true");
        } catch (SQLException e) {
            // Just eat any exceptions quietly. The database will be cleaned up
        }
    }

    public static void restoreTestDatabase() {
        dropTestDatabase();
        UkelonnDatabaseProvider ukelonnDatabaseProvider = (UkelonnDatabaseProvider) UkelonnServletProvider.getInstance().getDatabase();
        DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();
        ukelonnDatabaseProvider.setDataSourceFactory(derbyDataSourceFactory);
    }

    public static VaadinSession createSession() throws ServletException, ServiceException {
        VaadinServlet servlet = new VaadinServlet();
        ServletConfig config = mock(ServletConfig.class);
        ServletContext context = mock(ServletContext.class);
        when(context.getInitParameterNames()).thenReturn(Collections.emptyEnumeration());
        when(config.getServletContext()).thenReturn(context);
        when(config.getInitParameterNames()).thenReturn(Collections.emptyEnumeration());
        servlet.init(config);
        VaadinServletService service = new VaadinServletService(servlet,
                                                                new DefaultDeploymentConfiguration(UkelonnUI.class,
                                                                                                   new Properties()));
        VaadinSession session = new VaadinSession(service);
        WrappedSession wrappedsession = mock(WrappedSession.class);
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        when(wrappedsession.getAttribute(anyString())).thenReturn(lock);
        session.refreshTransients(wrappedsession, service);

        return session;
    }


    public static VaadinRequest createMockVaadinRequest(String location) {
        VaadinRequest request = mock(VaadinRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[0]);
        when(request.getParameter(eq("v-loc"))).thenReturn(location);
        return request;
    }


}

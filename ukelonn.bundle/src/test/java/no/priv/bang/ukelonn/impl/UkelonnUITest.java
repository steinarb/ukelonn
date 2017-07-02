package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

public class UkelonnUITest {

    private static VaadinSession session;
    private static Subject user;

    @BeforeClass
    public static void setupClass() throws ServiceException, ServletException {
        setupFakeOsgiServices();
    	UkelonnServlet servlet = new UkelonnServlet(getUkelonnServletProvider());
    	ServletConfig config = mock(ServletConfig.class);
    	ServletContext context = mock(ServletContext.class);
    	when(context.getInitParameterNames()).thenReturn(Collections.emptyEnumeration());
    	when(config.getServletContext()).thenReturn(context);
    	when(config.getInitParameterNames()).thenReturn(Collections.emptyEnumeration());
    	servlet.init(config);
    	VaadinServletService service = new VaadinServletService(servlet,
                                                                new DefaultDeploymentConfiguration(UkelonnUI.class,
                                                                                                   new Properties()));
    	session = new VaadinSession(service);
    	WrappedSession wrappedsession = mock(WrappedSession.class);
    	ReentrantLock lock = new ReentrantLock();
    	lock.lock();
    	when(wrappedsession.getAttribute(anyString())).thenReturn(lock);
    	session.refreshTransients(wrappedsession, service);

    	SecurityManager securitymanager = mock(SecurityManager.class);
    	user = mock(Subject.class);
    	when(securitymanager.createSubject(any(SubjectContext.class))).thenReturn(user);
    	SecurityUtils.setSecurityManager(securitymanager);
    }

    @AfterClass
    public static void teardownForAllTests() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    	releaseFakeOsgiServices();
    }

    @Test
    public void testInitWhenNotLoggedIn() {
        VaadinSession.setCurrent(session);
    	when(user.isRemembered()).thenReturn(false);
    	when(user.isAuthenticated()).thenReturn(false);
    	when(user.hasRole(eq("administrator"))).thenReturn(false);
        UkelonnUI ui = new UkelonnUI(getUkelonnServletProvider());
        String location = "http://localhost:8181/ukelonn/";
        VaadinRequest request = createMockVaadinRequest(location);
        ui.doInit(request, -1, location);

        assertEquals(LoginView.class, ui.getNavigator().getCurrentView().getClass());
    }

    @Test
    public void testInitWhenRegularUser() {
        VaadinSession.setCurrent(session);
        when(user.isRemembered()).thenReturn(false);
        when(user.isAuthenticated()).thenReturn(true);
        when(user.hasRole(eq("administrator"))).thenReturn(false);
        UkelonnUI ui = new UkelonnUI(getUkelonnServletProvider());
        String location = "http://localhost:8181/ukelonn/";
        VaadinRequest request = createMockVaadinRequest(location);
        ui.doInit(request, -1, location);

        assertEquals(UserView.class, ui.getNavigator().getCurrentView().getClass());
    }

    @Test
    public void testFallbackUserViewInitWhenRegularUser() {
        VaadinSession.setCurrent(session);
    	when(user.isRemembered()).thenReturn(false);
    	when(user.isAuthenticated()).thenReturn(true);
    	when(user.hasRole(eq("administrator"))).thenReturn(false);
        UI ui = getUkelonnServletProvider().createInstance(null);
        String location = "http://localhost:8181/ukelonn/";
        VaadinRequest request = createMockVaadinRequest(location);
        Cookie[] cookies = { new Cookie("cookie", "crumb"), new Cookie("ui-style", "browser")};
        when(request.getCookies()).thenReturn(cookies);
        ui.doInit(request, -1, location);

        assertEquals(UserFallbackView.class, ui.getNavigator().getCurrentView().getClass());
    }

    @Test
    public void testSwitchToBrowserFriendlyView() {
        VaadinSession.setCurrent(session);
    	when(user.isRemembered()).thenReturn(false);
    	when(user.isAuthenticated()).thenReturn(true);
    	when(user.hasRole(eq("administrator"))).thenReturn(false);
        UkelonnUI ui = new UkelonnUI(getUkelonnServletProvider());
        String location = "http://localhost:8181/ukelonn/";
        VaadinRequest request = createMockVaadinRequest(location);
        when(request.getParameter(eq("ui-style"))).thenReturn("browser");
    	CurrentInstance.set(VaadinResponse.class, mock(VaadinResponse.class));
        ui.doInit(request, -1, location);

        assertEquals(UserFallbackView.class, ui.getNavigator().getCurrentView().getClass());
    }

    @Test
    public void testSwitchToMobileFriendlyView() {
        VaadinSession.setCurrent(session);
    	when(user.isRemembered()).thenReturn(false);
    	when(user.isAuthenticated()).thenReturn(true);
    	when(user.hasRole(eq("administrator"))).thenReturn(false);
        UkelonnUI ui = new UkelonnUI(getUkelonnServletProvider());
        String location = "http://localhost:8181/ukelonn/";
        VaadinRequest request = createMockVaadinRequest(location);
        when(request.getParameter(eq("ui-style"))).thenReturn("mobile");
    	CurrentInstance.set(VaadinResponse.class, mock(VaadinResponse.class));
        ui.doInit(request, -1, location);

        assertEquals(UserView.class, ui.getNavigator().getCurrentView().getClass());
    }

    @Test
    public void testInitWhenRememberedUser() {
        VaadinSession.setCurrent(session);
    	when(user.isRemembered()).thenReturn(true);
    	when(user.isAuthenticated()).thenReturn(false);
    	when(user.hasRole(eq("administrator"))).thenReturn(false);
        UkelonnUI ui = new UkelonnUI(getUkelonnServletProvider());
        String location = "http://localhost:8181/ukelonn/";
        VaadinRequest request = createMockVaadinRequest(location);
        ui.doInit(request, -1, location);

        assertEquals(UserView.class, ui.getNavigator().getCurrentView().getClass());
    }

    @Test
    public void testInitWhenLoggedInAsAdministrator() {
        VaadinSession.setCurrent(session);
    	when(user.isRemembered()).thenReturn(true);
    	when(user.isAuthenticated()).thenReturn(false);
    	when(user.hasRole(eq("administrator"))).thenReturn(true);
        UkelonnUI ui = new UkelonnUI(getUkelonnServletProvider());
        String location = "http://localhost:8181/ukelonn/";
        VaadinRequest request = createMockVaadinRequest(location);
        ui.doInit(request, -1, location);

        assertEquals(AdminView.class, ui.getNavigator().getCurrentView().getClass());
    }

    @Test
    public void testAdminFallbackViewInitWhenLoggedInAsAdministrator() {
        VaadinSession.setCurrent(session);
    	when(user.isRemembered()).thenReturn(true);
    	when(user.isAuthenticated()).thenReturn(false);
    	when(user.hasRole(eq("administrator"))).thenReturn(true);
        UkelonnUI ui = new UkelonnUI(getUkelonnServletProvider());
        String location = "http://localhost:8181/ukelonn/";
        VaadinRequest request = createMockVaadinRequest(location);
        Cookie[] cookies = { new Cookie("ui-style", "browser")};
        when(request.getCookies()).thenReturn(cookies);
        ui.doInit(request, -1, location);

        assertEquals(AdminFallbackView.class, ui.getNavigator().getCurrentView().getClass());
    }

    private VaadinRequest createMockVaadinRequest(String location) {
        VaadinRequest request = mock(VaadinRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[0]);
        when(request.getParameter(eq("v-loc"))).thenReturn(location);
        return request;
    }

}

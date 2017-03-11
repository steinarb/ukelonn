package no.priv.bang.ukelonn.impl;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

@Theme("touchkit")
@Widgetset("com.vaadin.addon.touchkit.gwt.TouchKitWidgetSet")
public class UkelonnUI extends UI {
    private static final long serialVersionUID = 1388525490129647161L;
    private Navigator navigator;

    @Override
    protected void init(VaadinRequest request) {
    	getPage().setTitle("Ukelønn");
    	navigator = new Navigator(this, this);

    	// Add all of the different views
    	navigator.addView("", new UserView(request));
    	navigator.addView("admin", new AdminView(request));
    	navigator.addView("login", new LoginView(request, navigator));
    	if (!isLoggedIn()) {
            navigator.navigateTo("login");
    	} else if (isAdministrator()) {
            navigator.navigateTo("admin");
    	} else {
            navigator.navigateTo("");
    	}
    }

    protected boolean isAdministrator() {
        Subject currentUser = SecurityUtils.getSubject();
        return currentUser.hasRole("administrator");
    }

    protected boolean isLoggedIn() {
        Subject currentUser = SecurityUtils.getSubject();
        boolean isRemembered = currentUser.isRemembered();
        boolean isAuthenticated = currentUser.isAuthenticated();
        return isRemembered || isAuthenticated;
    }
}

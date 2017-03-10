package no.priv.bang.ukelonn.impl;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;

@Theme("touchkit")
@Widgetset("com.vaadin.addon.touchkit.gwt.TouchKitWidgetSet")
public class UkelonnUI extends AbstractUI {
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
}

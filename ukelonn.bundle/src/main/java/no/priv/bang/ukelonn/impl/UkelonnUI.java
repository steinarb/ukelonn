package no.priv.bang.ukelonn.impl;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class UkelonnUI extends UI {
    private static final long serialVersionUID = 1388525490129647161L;
    private Navigator navigator;

    @Override
    protected void init(VaadinRequest request) {
        navigator = new Navigator(this, this);
        navigator.addView("admin", new AdminView(request));
        navigator.addView("", new UserView(request, navigator));
    }
}

package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;

import java.security.Principal;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class UkelonnUI extends UI {
    private static final long serialVersionUID = 1388525490129647161L;
    private Navigator navigator;

    @Override
    protected void init(VaadinRequest request) {
    	Principal currentUser = request.getUserPrincipal();
    	Account account = getAccountInfoFromDatabase(getClass(), (String) currentUser.getName());

        navigator = new Navigator(this, this);
        navigator.addView("", new UserView(account));
    }
}

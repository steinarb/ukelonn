package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;

import java.security.Principal;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class AdminView extends VerticalLayout implements View {
    private static final long serialVersionUID = -1581589472749242129L;

    public AdminView(VaadinRequest request) {
    	Principal currentUser = request.getUserPrincipal();
    	AdminUser admin = getAdminUserFromDatabase(getClass(), (String) currentUser.getName());
        // Display the greeting
        Component greeting = new Label("Hei " + admin.getFirstname());
        greeting.setStyleName("h1");
        addComponent(greeting);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

}

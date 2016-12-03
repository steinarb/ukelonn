package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;

import java.security.Principal;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class UkelonnUI extends UI {
    private static final long serialVersionUID = 1388525490129647161L;

    @Override
    protected void init(VaadinRequest request) {
    	Principal currentUser = request.getUserPrincipal();
    	Account account = getAccountInfoFromDatabase(getClass(), (String) currentUser.getName());
        // Create the content root layout for the UI
        VerticalLayout content = new VerticalLayout();
        setContent(content);

        // Display the greeting
        Component greeting = new Label("Hello " + account.getFirstName());
        greeting.setStyleName("h1");
        content.addComponent(greeting);

        // Have a clickable button
        content.addComponent(new Button("Registrer jobb",
                                        new Button.ClickListener() {
                                            private static final long serialVersionUID = 2723190031041985566L;

                                            @Override
                                            public void buttonClick(ClickEvent e) {
                                                Notification.show("Pushed!");
                                            }
                                        }));
    }

}

package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class UkelonnUI extends UI {
    private static final long serialVersionUID = 1388525490129647161L;

    @Override
    protected void init(VaadinRequest request) {
    	Subject currentUser = SecurityUtils.getSubject();
    	Account account = getAccountInfoFromDatabase(getClass(), (String) currentUser.getPrincipal());
        // Create the content root layout for the UI
        VerticalLayout content = new VerticalLayout();
        setContent(content);

        // Display the greeting
        content.addComponent(new Label("Hello " + account.getFirstName()));

        // Have a clickable button
        content.addComponent(new Button("Push Me!",
                                        new Button.ClickListener() {
                                            private static final long serialVersionUID = 2723190031041985566L;

                                            @Override
                                            public void buttonClick(ClickEvent e) {
                                                Notification.show("Pushed!");
                                            }
                                        }));
    }

}

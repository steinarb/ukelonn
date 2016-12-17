package no.priv.bang.ukelonn.impl;

import static no.priv.bang.ukelonn.impl.CommonDatabaseMethods.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;

public class UkelonnAdminUI extends AbstractUI {
    private static final long serialVersionUID = -1581589472749242129L;

    @Override
    protected void init(VaadinRequest request) {
    	if (!isAdministrator()) {
            URI adminPage = addPathToURI(getPage().getLocation(), "../user/");
            getPage().setLocation(adminPage);
    	}

    	VerticalLayout content = new VerticalLayout();
    	Principal currentUser = request.getUserPrincipal();
    	AdminUser admin = getAdminUserFromDatabase(getClass(), (String) currentUser.getName());
        // Display the greeting
        Component greeting = new Label("Hei " + admin.getFirstname());
        greeting.setStyleName("h1");
        content.addComponent(greeting);

        Accordion accordion = new Accordion();

        VerticalLayout registerPaymentTab = new VerticalLayout();
        List<Account> accounts = getAccounts(getClass());
        BeanItemContainer<Account> accountsContainer = new BeanItemContainer<Account>(Account.class, accounts);
        ComboBox accountSelector = new ComboBox("Velg hvem det skal betales til", accountsContainer);
        accountSelector.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        accountSelector.setItemCaptionPropertyId("fullName");
        registerPaymentTab.addComponent(accountSelector);
        Accordion userinfo = new Accordion();
        VerticalLayout jobsTab = new VerticalLayout();
        userinfo.addTab(jobsTab, "Siste jobber");
        VerticalLayout paymentsTab = new VerticalLayout();
        userinfo.addTab(paymentsTab, "Siste utbetalinger");
        registerPaymentTab.addComponent(userinfo);
        accordion.addTab(registerPaymentTab, "Registrere utbetaling");

        VerticalLayout jobtypeAdminTab = new VerticalLayout();
        Accordion jobtypes = new Accordion();
        VerticalLayout newJobTypeTab = new VerticalLayout();
        jobtypes.addTab(newJobTypeTab, "Lag ny jobbtype");
        VerticalLayout jobtypesform = new VerticalLayout();
        jobtypes.addTab(jobtypesform, "Endre jobbtyper");
        jobtypeAdminTab.addComponent(jobtypes);
        accordion.addTab(jobtypeAdminTab, "Administrere jobbtyper");


        VerticalLayout paymentstypeadminTab = new VerticalLayout();
        Accordion paymentstypeadmin = new Accordion();
        VerticalLayout newpaymenttypeTab = new VerticalLayout();
        paymentstypeadmin.addTab(newpaymenttypeTab, "Lag ny utbetalingstype");
        VerticalLayout paymenttypesform = new VerticalLayout();
        paymentstypeadmin.addTab(paymenttypesform, "Endre utbetalingstyper");
        paymentstypeadminTab.addComponent(paymentstypeadmin);
        accordion.addTab(paymentstypeadminTab, "Endre utbetalingstyper");

        VerticalLayout useradminTab = new VerticalLayout();
        Accordion useradmin = new Accordion();
        VerticalLayout newuserTab = new VerticalLayout();
        useradmin.addTab(newuserTab, "Legg til ny bruker");
        VerticalLayout changeuserpasswordTab = new VerticalLayout();
        useradmin.addTab(changeuserpasswordTab, "Bytt passord på bruker");
        VerticalLayout usersTab = new VerticalLayout();
        useradmin.addTab(usersTab, "Endre brukere");
        useradminTab.addComponent(useradmin);
        accordion.addTab(useradminTab, "Administrere brukere");

        content.addComponent(accordion);

        setContent(content);
    }

}

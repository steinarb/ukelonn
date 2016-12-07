package no.priv.bang.ukelonn.impl;

import java.net.URI;

import com.vaadin.server.VaadinRequest;

public class TopUI extends AbstractUI {
    private static final long serialVersionUID = -9085285758065641021L;

    @Override
    protected void init(VaadinRequest request) {
    	if (isAdministrator()) {
            URI adminPage = addPathToURI(getPage().getLocation(), "admin/");
            getPage().setLocation(adminPage);
    	}

    	URI userPage = addPathToURI(getPage().getLocation(), "user/");
    	getPage().setLocation(userPage);
    }

}

package no.priv.bang.ukelonn.impl;

import java.net.URI;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.vaadin.ui.UI;

public abstract class AbstractUI extends UI {
    private static final long serialVersionUID = 267153275586375959L;

    public static URI addPathToURI(URI location, String path) {
        String combinedPath = location.getPath() + path;
        URI newURI = location.resolve(combinedPath);
        return newURI;
    }

    protected boolean isAdministrator() {
    	Subject currentUser = SecurityUtils.getSubject();
        return currentUser.hasRole("administrator");
    }

}

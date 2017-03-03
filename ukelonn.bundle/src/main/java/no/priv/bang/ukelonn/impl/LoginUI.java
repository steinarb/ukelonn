package no.priv.bang.ukelonn.impl;

import java.net.URI;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import static no.priv.bang.ukelonn.impl.CommonServiceMethods.*;


public class LoginUI extends AbstractUI {
    private static final long serialVersionUID = 4812377913694429252L;

    @Override
    protected void init(VaadinRequest request) {

        ObjectProperty<String> username = new ObjectProperty<String>("");
        ObjectProperty<String> password = new ObjectProperty<String>("");

        FormLayout content = new FormLayout();
        TextField usernameField = new TextField("Username", username);
        content.addComponent(usernameField);
        PasswordField passwordfield = new PasswordField("Password", password);
        content.addComponent(passwordfield);
        VerticalLayout notificationArea = new VerticalLayout();
        content.addComponent(notificationArea);
        Class<? extends LoginUI> classForLogging = getClass();
        Button loginButton = new Button("Login", new Button.ClickListener() {
                private static final long serialVersionUID = -683422815692655520L;

                @Override
                public void buttonClick(ClickEvent event) {
                    Subject subject = SecurityUtils.getSubject();

                    UsernamePasswordToken token = new UsernamePasswordToken(username.getValue(), password.getValue().toCharArray(), true);

                    try {
                        subject.login(token);

                        if (subject.hasRole("administrator")) {
                            URI adminPage = addPathToURI(getPage().getLocation(), "../admin/");
                            getPage().setLocation(adminPage);
                        } else {
                            URI userPage = addPathToURI(getPage().getLocation(), "../user/");
                            getPage().setLocation(userPage);
                        }
                    } catch(UnknownAccountException e) {
                        notification("Unknown account");
                        logError(classForLogging, "Login error: unknown account", e);
                    } catch (IncorrectCredentialsException  e) {
                        notification("Wrong password");
                        logError(classForLogging, "Login error: wrong password", e);
                    } catch (LockedAccountException  e) {
                        notification("Locked account");
                        logError(classForLogging, "Login error: locked account", e);
                    } catch (AuthenticationException e) {
                        notification("Unknown error");
                        logError(classForLogging, "Login error: unknown error", e);
                    } finally {
                        token.clear();
                    }
                }
            });
        loginButton.setClickShortcut(KeyCode.ENTER);
        content.addComponent(loginButton);
        setContent(content);
    }

    public void notification(String message) {
        Notification.show(message, "", Notification.Type.WARNING_MESSAGE);
    }

}

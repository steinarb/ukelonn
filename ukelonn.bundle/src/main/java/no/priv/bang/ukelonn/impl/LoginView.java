/*
 * Copyright 2016-2018 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package no.priv.bang.ukelonn.impl;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import static no.priv.bang.ukelonn.impl.CommonServiceMethods.*;


public class LoginView extends AbstractView { // NOSONAR
    private static final long serialVersionUID = 4812377913694429252L;
    ObjectProperty<String> username = new ObjectProperty<>("");
    ObjectProperty<String> password = new ObjectProperty<>("");

    public LoginView(UkelonnUIProvider provider, Navigator navigator) {
        FormLayout content = new FormLayout();
        TextField usernameField = new TextField("Username", username);
        content.addComponent(usernameField);
        PasswordField passwordfield = new PasswordField("Password", password);
        content.addComponent(passwordfield);
        VerticalLayout notificationArea = new VerticalLayout();
        content.addComponent(notificationArea);
        Class<? extends LoginView> classForLogging = getClass();
        Button loginButton = new Button("Login", new Button.ClickListener() {
                private static final long serialVersionUID = -683422815692655520L;

                @Override
                public void buttonClick(ClickEvent event) {
                    passwordfield.commit();
                    Subject subject = SecurityUtils.getSubject();

                    UsernamePasswordToken token = new UsernamePasswordToken(username.getValue(), password.getValue().toCharArray(), true);

                    try {
                        subject.login(token);

                        if (subject.hasRole("administrator")) {
                            navigator.navigateTo("admin");
                        } else {
                            navigator.navigateTo("");
                        }
                    } catch(UnknownAccountException e) {
                        notification("Unknown account");
                        logError(provider, classForLogging, "Login error: unknown account", e);
                    } catch (IncorrectCredentialsException  e) {
                        notification("Wrong password");
                        logError(provider, classForLogging, "Login error: wrong password", e);
                    } catch (LockedAccountException  e) {
                        notification("Locked account");
                        logError(provider, classForLogging, "Login error: locked account", e);
                    } catch (AuthenticationException e) {
                        notification("Unknown error");
                        logError(provider, classForLogging, "Login error: unknown error", e);
                    } finally {
                        token.clear();
                    }
                }
            });
        loginButton.setClickShortcut(KeyCode.ENTER);
        content.addComponent(loginButton);
        addComponent(content);
    }

    public void notification(String message) {
        Notification.show(message, "", Notification.Type.WARNING_MESSAGE);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // Method intentionally left open
    }

}

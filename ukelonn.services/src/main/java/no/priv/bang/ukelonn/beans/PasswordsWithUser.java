/*
 * Copyright 2016-2017 Steinar Bang
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
package no.priv.bang.ukelonn.beans;

public class PasswordsWithUser {

    private User user;
    private String password;
    private String password2;

    public PasswordsWithUser(User user, String password, String password2) {
        this.user = user;
        this.password = password;
        this.password2 = password2;
    }

    // Argument-less constructor required by jackson
    public PasswordsWithUser() {
        this(null, "", "");
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public String getPassword2() {
        return password2;
    }

}

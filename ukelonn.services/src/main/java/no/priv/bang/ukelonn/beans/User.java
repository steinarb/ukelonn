/*
 * Copyright 2016-2019 Steinar Bang
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.priv.bang.beans.immutable.Immutable;

@JsonIgnoreProperties(ignoreUnknown=true)
public class User extends Immutable {
    private int userId;
    private String username;
    private String email;
    private String firstname;
    private String lastname;

    public User(int userId, String username, String email, String firstname, String lastname) {
        super();
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    // No-arg consructor required by jackson
    public User() {
        super();
        this.userId = -1;
        this.username = "";
        this.email = "";
        this.firstname = "";
        this.lastname = "";
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFullname() {
        return new StringBuilder(getFirstname()).append(" ").append(getLastname()).toString();
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", username=" + username + ", email=" + email + ", firstname=" + firstname + ", lastname=" + lastname + "]";
    }
}

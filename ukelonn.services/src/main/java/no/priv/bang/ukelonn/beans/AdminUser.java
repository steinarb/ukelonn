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

import no.priv.bang.beans.immutable.Immutable;

public class AdminUser extends Immutable {
    private String userName;
    private int userId = 0;
    private int administratorId = 0;
    private String firstname = "Ikke innlogget";
    private String surname = "";

    public AdminUser(String userName, int userId, int administratorId, String firstname, String surname) {
        super();
        this.userName = userName;
        this.userId = userId;
        this.administratorId = administratorId;
        this.firstname = firstname;
        this.surname = surname;
    }

    public String getUserName() {
        return userName;
    }

    public int getUserId() {
        return userId;
    }

    public int getAdministratorId() {
        return administratorId;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    @Override
    public String toString() {
        return "AdminUser [userName=" + userName + ", userId=" + userId + ", administratorId=" + administratorId + ", firstname=" + firstname + ", surname=" + surname + "]";
    }

}

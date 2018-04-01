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

import static no.priv.bang.ukelonn.impl.CommonStringMethods.*;

public class AdminUser {
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + administratorId;
        result = prime * result + ((firstname == null) ? 0 : firstname.hashCode());
        result = prime * result + ((surname == null) ? 0 : surname.hashCode());
        result = prime * result + userId;
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null ||
            getClass() != obj.getClass())
        {
            return false;
        }

        AdminUser other = (AdminUser) obj;
        return
            administratorId == other.administratorId &&
            userId == other.userId &&
            nullSafeEquals(userName, other.userName) &&
            nullSafeEquals(firstname, other.firstname) &&
            nullSafeEquals(surname, other.surname);
    }

    @Override
    public String toString() {
        return "AdminUser [userName=" + userName + ", userId=" + userId + ", administratorId=" + administratorId + ", firstname=" + firstname + ", surname=" + surname + "]";
    }

}

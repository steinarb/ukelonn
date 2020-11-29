/*
 * Copyright 2020 Steinar Bang
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
package no.priv.bang.ukelonn.api.beans;

import no.priv.bang.osgiservice.users.User;

public class AdminStatus {

    private User user;
    private boolean administrator;

    public AdminStatus(User user, boolean administrator) {
        this.user = user;
        this.administrator = administrator;
    }

    public AdminStatus() {
        // jackson and jersey require noargs constructors
    }

    public User getUser() {
        return user;
    }

    public boolean isAdministrator() {
        return administrator;
    }

}

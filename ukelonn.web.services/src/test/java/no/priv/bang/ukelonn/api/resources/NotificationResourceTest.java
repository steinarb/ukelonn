/*
 * Copyright 2018-2024 Steinar Bang
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
package no.priv.bang.ukelonn.api.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import no.priv.bang.ukelonn.backend.UkelonnServiceProvider;
import no.priv.bang.ukelonn.beans.Notification;

class NotificationResourceTest {

    @Test
    void testNotification() {
        var ukelonn = new UkelonnServiceProvider();
        var resource = new NotificationResource();
        resource.ukelonn = ukelonn;
        var notificationsToJad = resource.notificationsTo("jad");
        assertThat(notificationsToJad).isEmpty();

        // Send notification to "jad"
        var utbetalt = Notification.with().title("Ukelønn").message("150 kroner betalt til konto").build();
        resource.notificationTo("jad", utbetalt);

        // Verify that notifcations to a different user is empty
        assertThat(resource.notificationsTo("jod")).isEmpty();

        // Verify that notifications to "jad" contains the sent notification
        assertEquals(utbetalt, resource.notificationsTo("jad").get(0));
    }

}

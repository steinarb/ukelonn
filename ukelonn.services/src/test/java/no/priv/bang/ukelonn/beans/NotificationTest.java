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

import static org.junit.Assert.*;

import org.junit.Test;

public class NotificationTest {

    @Test
    public void testGetters() {
        Notification bean = new Notification("Ukelønn", "150 kroner utbetalt til konto");
        assertEquals("Ukelønn", bean.getTitle());
        assertEquals("150 kroner utbetalt til konto", bean.getMessage());
    }

    @Test
    public void testNoArgsConstructor() {
        Notification bean = new Notification();
        assertEquals("", bean.getTitle());
        assertEquals("", bean.getMessage());
    }

}

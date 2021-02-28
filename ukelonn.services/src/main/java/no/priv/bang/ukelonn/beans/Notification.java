/*
 * Copyright 2016-2021 Steinar Bang
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

public class Notification {

    private String title;
    private String message;

    private Notification() {}

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public static NotificationBuilder with() {
        return new NotificationBuilder();
    }

    public static class NotificationBuilder {
        private String title = "";
        private String message = "";

        private NotificationBuilder() {}

        public Notification build() {
            Notification notification = new Notification();
            notification.title = this.title;
            notification.message = this.message;
            return notification;
        }

        public NotificationBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NotificationBuilder message(String message) {
            this.message = message;
            return this;
        }
    }

}

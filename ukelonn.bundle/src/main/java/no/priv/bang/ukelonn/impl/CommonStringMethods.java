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

public class CommonStringMethods {

    private CommonStringMethods() {}

    public static boolean isNullEmptyOrBlank(String string) {
        if (string == null) {
            return true;
        }

        return (string.trim().isEmpty());
    }

    public static String safeTrim(String string) {
        if (string == null) {
            return null;
        }

        return string.trim();
    }

    public static boolean nullSafeEquals(String a, String b) {
        if (a == b) {
            return true;
        }

        if (a == null) {
            return false; // Can't both be null
        }

        return a.equals(b);
    }

}

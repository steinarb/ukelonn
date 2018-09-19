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
package no.priv.bang.ukelonn.db.derbytest.mocks;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class MockLogService implements LogService {
    final String[] errorLevel = {"", "[ERROR] ", "[WARNING] ", "[INFO] ", "[DEBUG] "};

    public void log(int level, String message) {
        System.err.println(errorLevel[level] + message);
    }

    public void log(int level, String message, Throwable exception) {
        System.err.println(errorLevel[level] + message + "  Exception:");
        exception.printStackTrace();
    }

    @SuppressWarnings("rawtypes")
    public void log(ServiceReference sr, int level, String message) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("rawtypes")
    public void log(ServiceReference sr, int level, String message, Throwable exception) {
        // TODO Auto-generated method stub

    }

}

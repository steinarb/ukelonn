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

import org.osgi.service.log.LogService;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

import no.priv.bang.ukelonn.UkelonnDatabase;
import no.priv.bang.ukelonn.UkelonnService;

public class UkelonnUIProvider extends UIProvider implements UkelonnService {
    private static final long serialVersionUID = -275959896126008712L;
    private UkelonnDatabase database; // NOSONAR
    private LogService logservice; // NOSONAR

    public void setUkelonnDatabase(UkelonnDatabase database) {
        this.database = database;
    }

    public UkelonnDatabase getDatabase() {
        return database;
    }

    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
    }

    public LogService getLogservice() {
        return logservice;
    }

    @Override
    public UI createInstance(UICreateEvent event) {
        return new UkelonnUI(this);
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        return UkelonnUI.class;
    }

}

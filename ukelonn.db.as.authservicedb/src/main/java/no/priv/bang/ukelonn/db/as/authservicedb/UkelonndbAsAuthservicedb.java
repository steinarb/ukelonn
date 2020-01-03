/*
 * Copyright 2019 Steinar Bang
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
package no.priv.bang.ukelonn.db.as.authservicedb;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import no.priv.bang.jdbc.datasourceproxy.DataSourceProxy;

@Component(service=DataSource.class, immediate=true, property = "osgi.jndi.service.name=jdbc/authservice")
public class UkelonndbAsAuthservicedb extends DataSourceProxy {

    @Override
    @Reference(target = "(osgi.jndi.service.name=jdbc/ukelonn)")
    public void setWrappedDataSource(DataSource wrappedDataSource) {
        super.setWrappedDataSource(wrappedDataSource);
    }

    @Activate
    void activate() {
        // Called when the component is activated
    }

}

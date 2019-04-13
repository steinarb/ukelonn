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
package no.priv.bang.ukelonn.db.authservicedbadapter;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import no.priv.bang.authservice.definitions.AuthserviceDatabaseService;
import no.priv.bang.ukelonn.UkelonnDatabase;

@Component(immediate=true)
public class UkelonnDatabaseToAuthserviceDatabaseAdapter implements AuthserviceDatabaseService {
    private UkelonnDatabase ukelonnDatabase;

    @Reference
    public void setUkelonnDatabase(UkelonnDatabase ukelonnDatabase) {
        this.ukelonnDatabase = ukelonnDatabase;
    }

    @Activate
    public void activate() {
        // Called when all services are injected
    }

    @Override
    public DataSource getDatasource() {
        return ukelonnDatabase.getDatasource();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return ukelonnDatabase.getConnection();
    }
}

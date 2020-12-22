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
package no.priv.bang.ukelonn.api.resources;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.osgi.service.log.LogService;

import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.LocaleBean;

@Path("")
public class LocalizationResource extends ResourceBase {

    @Inject
    UkelonnService ukelonn;

    @Inject
    LogService logservice;

    @GET
    @Path("defaultlocale")
    @Produces(MediaType.APPLICATION_JSON)
    public Locale defaultLocale() {
        return ukelonn.defaultLocale();
    }

    @GET
    @Path("availablelocales")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LocaleBean> availableLocales() {
        return ukelonn.availableLocales();
    }

    @GET
    @Path("displaytexts")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> displayTexts(@QueryParam("locale")String locale) {
        try {
            return ukelonn.displayTexts(Locale.forLanguageTag(locale.replace('_', '-')));
        } catch (MissingResourceException e) {
            String message = String.format("Unknown locale '%s' used when fetching GUI texts", locale);
            logservice.log(LogService.LOG_ERROR, message);
            throw new WebApplicationException(response(500, message));
        }
    }

}

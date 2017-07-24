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
package no.priv.bang.ukelonn.impl;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("userConverter")
public class UserConverter implements Converter {
    private static Map<String, User> registry = new HashMap<String, User>();

    public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
        return registry.get(value);
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
        if (value == null || "".equals(value)) {
            return null;
        }

        if (value.getClass() != User.class) {
            throw new ConverterException("Object value was not a User, but an instance of " + value.getClass().getSimpleName() + "  \"" + value.toString() + "\"");
        }

        return value.toString();
    }

    public static void registerUser(User user) {
        registry.put(user.toString(), user);
    }

}

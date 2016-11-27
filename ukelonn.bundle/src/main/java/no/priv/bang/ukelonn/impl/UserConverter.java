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

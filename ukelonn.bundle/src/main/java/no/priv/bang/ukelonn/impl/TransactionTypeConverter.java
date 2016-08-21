package no.priv.bang.ukelonn.impl;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("transactionTypeConverter")
public class TransactionTypeConverter implements Converter {
    private static Map<String, TransactionType> registry = new HashMap<String, TransactionType>();

    public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
        return registry.get(value);
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
        if (value == null || "".equals(value)) {
            return null;
        }

        if (value.getClass() != TransactionType.class) {
            throw new ConverterException("Object value was not a TransactionType, but a " + value.getClass().getSimpleName() + "  \"" + value.toString() + "\"");
        }

        return value.toString();
    }

    public static void registerTransactionType(TransactionType transactionType) {
        registry.put(transactionType.toString(), transactionType);
    }

}

package no.priv.bang.ukelonn.impl;

import com.vaadin.data.Validator;
import com.vaadin.ui.PasswordField;

public class PasswordCompareValidator implements Validator {
    private static final long serialVersionUID = 2610490969282733208L;
    PasswordField otherPassword;

    public PasswordCompareValidator(PasswordField otherPassword) {
        super();
        this.otherPassword = otherPassword;
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        if (!otherPassword.getValue().equals(value)){
            throw new InvalidValueException("Passwords aren't identical");
        }
    }

}

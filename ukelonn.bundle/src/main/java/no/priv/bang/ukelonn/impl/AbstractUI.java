package no.priv.bang.ukelonn.impl;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.StringToDateConverter;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

public abstract class AbstractUI extends UI {
    private static final long serialVersionUID = 267153275586375959L;
    public static URI addPathToURI(URI location, String path) {
        String combinedPath = location.getPath() + path;
        URI newURI = location.resolve(combinedPath);
        return newURI;
    }

    protected boolean isAdministrator() {
    	Subject currentUser = SecurityUtils.getSubject();
        return currentUser.hasRole("administrator");
    }

    protected Table createTransactionTable(String transactionTypeName, BeanItemContainer<Transaction> transactions) {
        Table lastJobsTable = new Table();
        lastJobsTable.addContainerProperty("transactionTime", Date.class, null, "Dato", null, null);
        lastJobsTable.addContainerProperty("name", String.class, null, transactionTypeName, null, null);
        lastJobsTable.addContainerProperty("transactionAmount", Double.class, null, "Beløp", null, null);
        lastJobsTable.setConverter("transactionTime", dateFormatter);
        lastJobsTable.setContainerDataSource(transactions);
        lastJobsTable.setVisibleColumns("transactionTime", "name", "transactionAmount");
        return lastJobsTable;
    }

    static final StringToDateConverter dateFormatter = new StringToDateConverter() {
	    private static final long serialVersionUID = -1728291825811483452L;

	    @Override
	    public DateFormat getFormat(Locale locale) {
	        return new SimpleDateFormat("yyyy-MM-dd");
	    }
	};

}

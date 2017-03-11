package no.priv.bang.ukelonn.impl;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.StringToDateConverter;
import com.vaadin.navigator.View;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractView extends VerticalLayout implements View {
    private static final long serialVersionUID = 267153275586375959L;
    public static URI addPathToURI(URI location, String path) {
        String combinedPath = location.getPath() + path;
        URI newURI = location.resolve(combinedPath);
        return newURI;
    }

    protected Table createTransactionTable(String transactionTypeName, BeanItemContainer<Transaction> transactions) {
        Table transactionsTable = new Table();
        transactionsTable.addContainerProperty("transactionTime", Date.class, null, "Dato", null, null);
        transactionsTable.addContainerProperty("name", String.class, null, transactionTypeName, null, null);
        transactionsTable.addContainerProperty("transactionAmount", Double.class, null, "Beløp", null, null);
        transactionsTable.setConverter("transactionTime", dateFormatter);
        transactionsTable.setContainerDataSource(transactions);
        transactionsTable.setVisibleColumns("transactionTime", "name", "transactionAmount");
        transactionsTable.setPageLength(CommonDatabaseMethods.NUMBER_OF_TRANSACTIONS_TO_DISPLAY);
        return transactionsTable;
    }

    static final StringToDateConverter dateFormatter = new StringToDateConverter() {
	    private static final long serialVersionUID = -1728291825811483452L;

	    @Override
	    public DateFormat getFormat(Locale locale) {
	        return new SimpleDateFormat("yyyy-MM-dd");
	    }
	};

}

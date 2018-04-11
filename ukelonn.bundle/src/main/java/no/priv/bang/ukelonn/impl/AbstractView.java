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

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.vaadin.touchkit.ui.NavigationButton;
import org.vaadin.touchkit.ui.NavigationManager;
import org.vaadin.touchkit.ui.NavigationView;
import org.vaadin.touchkit.ui.VerticalComponentGroup;

import com.vaadin.data.converter.StringToDateConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractView extends VerticalLayout implements View { // NOSONAR
    protected static final String TRANSACTION_TYPE_NAME_PROPERTY = "transactionTypeName";
    protected static final String TRANSACTION_AMOUNT_PROPERTY = "transactionAmount";
    protected static final String IKKE_ET_TALL = "Ikke et tall";
    private static final long serialVersionUID = 267153275586375959L;
    public static URI addPathToURI(URI location, String path) {
        String combinedPath = location.getPath() + path;
        return location.resolve(combinedPath);
    }

    @SuppressWarnings("unchecked")
    protected Grid<Transaction> createTransactionTable(String transactionTypeName, ListDataProvider<Transaction> transactions, boolean addPaidOutColumn) {
        Grid<Transaction> transactionsTable = new Grid<>(Transaction.class);
        transactionsTable.setDataProvider(transactions);
        Column<Transaction, ?> transactionTime = transactionsTable.getColumn("transactionTime").setCaption("Dato");
        Column<Transaction, ?> name = transactionsTable.getColumn("name").setCaption(transactionTypeName);
        Column<Transaction, ?> transactionAmount = transactionsTable.getColumn(TRANSACTION_AMOUNT_PROPERTY).setCaption("Beløp");
        transactionsTable.removeColumn("transactionType");
        transactionsTable.removeColumn("id");
        if (!addPaidOutColumn) {
            transactionsTable.removeColumn("paidOut");
            transactionsTable.setColumnOrder(transactionTime, name, transactionAmount);
        } else {
            Column<Transaction, ?> paidOut = transactionsTable.getColumn("paidOut").setCaption("Utbetalt");
            transactionsTable.setColumnOrder(transactionTime, name, transactionAmount, paidOut);
        }

        return transactionsTable;
    }

    protected NavigationView createNavigationViewWithTable(NavigationManager navigationManager, String tableTitle, ListDataProvider<Transaction> transactions, String navigationViewCaption, boolean addPaidOutColumn) {
        VerticalComponentGroup transactionTableGroup = new VerticalComponentGroup();
        Grid<Transaction> transactionTable = createTransactionTable(tableTitle, transactions, addPaidOutColumn);
        transactionTableGroup.addComponent(transactionTable);
        NavigationView transactionTableView = new NavigationView(navigationViewCaption, transactionTableGroup);
        navigationManager.addComponent(transactionTableView);
        navigationManager.navigateTo(transactionTableView);
        navigationManager.navigateBack();
        return transactionTableView;
    }

    protected NavigationButton createNavigationButton(String caption, NavigationView targetView) {
        return new NavigationButton(caption, targetView);
    }

    protected HorizontalLayout createLinksToBrowserVersionAndLogout(VaadinRequest request, String uiStyle, String uiStyleName) {
        HorizontalLayout links = new HorizontalLayout();
        links.setSpacing(true);
        links.setWidth("100%");
        Link linkToTop = new Link("Tilbake til topp", new ExternalResource(request.getContextPath() + "/.."));
        links.addComponent(linkToTop);
        Link linkToBrowserFriendlyUI = new Link(uiStyleName, new ExternalResource(request.getContextPath() + "?ui-style=" + uiStyle));
        links.addComponent(linkToBrowserFriendlyUI);
        Link linkToLogout = new Link("Logg ut", new ExternalResource(request.getContextPath() + "?logout=yes"));
        links.addComponent(linkToLogout);
        links.setComponentAlignment(linkToLogout, Alignment.MIDDLE_RIGHT);
        return links;
    }

    protected Component wrapInPanel(Component wrappedComponent) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        Label indent = new Label(" ");
        indent.setWidth("1mm");
        horizontalLayout.addComponent(indent);
        Panel panel = new Panel(wrappedComponent);
        panel.setWidth("100%");
        horizontalLayout.addComponent(panel);
        horizontalLayout.setExpandRatio(panel, 1);
        return horizontalLayout;
    }

    static final StringToDateConverter dateFormatter =
        new StringToDateConverter() {
            private static final long serialVersionUID = -1728291825811483452L;

            @Override
            public DateFormat getFormat(Locale locale) {
                return new SimpleDateFormat("yyyy-MM-dd");
            }
        };

}

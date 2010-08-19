/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy.web;

import java.text.SimpleDateFormat;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.complitex.dictionaryfw.dao.StringCultureBean;
import org.complitex.dictionaryfw.entity.History;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.strategy.StrategyFactory;
import org.complitex.dictionaryfw.web.component.DomainObjectInputPanel;

/**
 *
 * @author Artem
 */
public class HistoryPanel extends Panel {

    private static final String DATE_FORMAT = "HH:mm:ss dd.MM.yyyy";

    @EJB(name = "StrategyFactory")
    private StrategyFactory strategyFactory;

    @EJB(name = "StringCultureBean")
    private StringCultureBean stringBean;

    private String entity;

    private long objectId;

    public HistoryPanel(String id, String entity, long objectId) {
        super(id);
        this.entity = entity;
        this.objectId = objectId;
        init();
    }

    private Strategy getStrategy() {
        return strategyFactory.getStrategy(entity);
    }

    private void init() {
        IModel<String> labelModel = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return stringBean.displayValue(getStrategy().getEntity().getEntityNames(), getLocale());
            }
        };
        Label title = new Label("title", labelModel);
        add(title);
        Label label = new Label("label", labelModel);
        add(label);

        final List<History> historyList = getStrategy().getHistory(objectId);

        ListView<History> history = new ListView<History>("history", historyList) {

            @Override
            protected void populateItem(final ListItem<History> item) {
                final History currentHistory = item.getModelObject();

                IModel<String> dateModel = new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        String dateAsString = new SimpleDateFormat(DATE_FORMAT, getLocale()).format(currentHistory.getDate());
                        if (item.getIndex() < historyList.size() - 1) {
                            return dateAsString;
                        } else {
                            return dateAsString + ". " + getString("current_item");
                        }
                    }
                };
                item.add(new Label("date", dateModel));
                item.add(new DomainObjectInputPanel("domainObjectInputPanel", currentHistory.getObject(), entity, null, null, currentHistory.getDate()));
            }
        };
        add(history);
    }
}

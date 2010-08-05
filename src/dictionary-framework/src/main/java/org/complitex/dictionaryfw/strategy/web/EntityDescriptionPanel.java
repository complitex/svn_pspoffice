/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy.web;

import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.dictionaryfw.entity.description.Entity;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.strategy.StrategyFactory;
import org.complitex.dictionaryfw.util.DisplayLocalizedValueUtil;

/**
 *
 * @author Artem
 */
public final class EntityDescriptionPanel extends Panel {

    @EJB(name = "StrategyFactory")
    private StrategyFactory strategyFactory;

    @EJB(name = "DisplayLocalizedValueUtil")
    private DisplayLocalizedValueUtil displayLocalizedValueUtil;

    public EntityDescriptionPanel(String id, String entity) {
        super(id);
        init(entity);
    }

    private Strategy getStrategy(String entity) {
        return strategyFactory.getStrategy(entity);
    }

    private void init(final String entity) {
        final Entity description = getStrategy(entity).getEntity();

        String entityLabel = displayLocalizedValueUtil.displayValue(description.getEntityNames(), getLocale());
        add(new Label("title", new StringResourceModel("label", null, new Object[]{entityLabel})));
        add(new Label("label", new StringResourceModel("label", null, new Object[]{entityLabel})));


    }
}

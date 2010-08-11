/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.web.component;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.strategy.StrategyFactory;

/**
 *
 * @author Artem
 */
public final class ChildrenContainer extends Panel {

    @EJB(name = "StrategyFactory")
    private StrategyFactory strategyFactory;

    private String entity;

    private DomainObject object;

    public ChildrenContainer(String id, String entity, DomainObject object) {
        super(id);
        this.entity = entity;
        this.object = object;
        init();
    }

    private Strategy getStrategy() {
        return strategyFactory.getStrategy(entity);
    }

    private void init() {
        Map<String, String> childrenInfo = getStrategy().getChildrenInfo(getLocale());
        if (childrenInfo == null) {
            childrenInfo = Collections.emptyMap();
        }
        ListView<String> childrenContainers = new ListView<String>("childrenContainers", Lists.newArrayList(childrenInfo.keySet())) {

            @Override
            protected void populateItem(ListItem<String> item) {
                String childEntity = item.getModelObject();
                item.add(new Children("children", entity, object, childEntity));
            }
        };
        add(childrenContainers);
    }
}

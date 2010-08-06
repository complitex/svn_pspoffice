/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy.web;

import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.dictionaryfw.entity.SimpleTypes;
import org.complitex.dictionaryfw.entity.description.Entity;
import org.complitex.dictionaryfw.entity.description.EntityAttributeType;
import org.complitex.dictionaryfw.entity.description.EntityAttributeValueType;
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

    public EntityDescriptionPanel(String id, String entity, Class<? extends WebPage> attributeEditPage, PageParameters attributeEditPageParams) {
        super(id);
        init(entity, attributeEditPage, attributeEditPageParams);
    }

    private Strategy getStrategy(String entity) {
        return strategyFactory.getStrategy(entity);
    }

    private void init(final String entity, Class<? extends WebPage> attributeEditPage, PageParameters attributeEditPageParams) {
        Entity description = getStrategy(entity).getEntity();

        String entityLabel = displayLocalizedValueUtil.displayValue(description.getEntityNames(), getLocale());
        add(new Label("title", new StringResourceModel("label", null, new Object[]{entityLabel})));
        add(new Label("label", new StringResourceModel("label", null, new Object[]{entityLabel})));

        ListView<EntityAttributeType> attributes = new ListView<EntityAttributeType>("attributes", description.getEntityAttributeTypes()) {

            @Override
            protected void populateItem(ListItem<EntityAttributeType> item) {
                EntityAttributeType attributeType = item.getModelObject();
                item.add(new Label("name", displayLocalizedValueUtil.displayValue(attributeType.getAttributeNames(), getLocale())));
                item.add(new Label("mandatory", attributeType.isMandatory() ? getString("yes") : getString("no")));

                List<EntityAttributeValueType> valueTypes = attributeType.getEntityAttributeValueTypes();
                Label valueType = new Label("valueType", new Model<String>());
                item.add(valueType);
                WebMarkupContainer valueTypesContainer = new WebMarkupContainer("valueTypesContainer");
                RepeatingView valueTypeItem = new RepeatingView("valueTypeItem");
                valueTypesContainer.add(valueTypeItem);
                item.add(valueTypesContainer);
                if (valueTypes.size() == 1) {
                    valueType.setDefaultModelObject(displayValueType(valueTypes.get(0).getValueType()));
                    valueTypesContainer.setVisible(false);
                } else {
                    for (EntityAttributeValueType currentValueType : valueTypes) {
                        valueTypeItem.add(new Label(String.valueOf(currentValueType.getId()), displayValueType(currentValueType.getValueType())));
                    }
                }
            }
        };
        add(attributes);

        BookmarkablePageLink addAttribute = new BookmarkablePageLink("addAttribute", attributeEditPage, attributeEditPageParams);
        add(addAttribute);

        String[] parents = getStrategy(entity).getParents();
        WebMarkupContainer parentsContainer = new WebMarkupContainer("parentsContainer");
        Label parentsInfo = new Label("parentsInfo", new Model<String>());
        parentsContainer.add(parentsInfo);
        add(parentsContainer);
        if ((parents != null) && (parents.length > 0)) {
            parentsInfo.setDefaultModelObject(displayParents(parents));
        } else {
            parentsContainer.setVisible(false);
        }
    }

    private String displayParents(String[] parents) {
        StringBuilder parentsLabel = new StringBuilder();
        for (int i = 0; i < parents.length; i++) {
            parentsLabel.append("'").
                    append(displayLocalizedValueUtil.displayValue(getStrategy(parents[i]).getEntity().getEntityNames(), getLocale())).
                    append("'");
            if (i < parents.length - 1) {
                parentsLabel.append(", ");
            }
        }
        return parentsLabel.toString();
    }

    private String displayValueType(String valueType) {
        if (SimpleTypes.isSimpleType(valueType)) {
            return valueType;
        } else {
            return new StringResourceModel("reference", this, null, new Object[]{
                        displayLocalizedValueUtil.displayValue(getStrategy(valueType).getEntity().getEntityNames(), getLocale())
                    }).getObject();
        }
    }
}

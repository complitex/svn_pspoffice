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
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.dictionaryfw.dao.StringCultureBean;
import org.complitex.dictionaryfw.entity.SimpleTypes;
import org.complitex.dictionaryfw.entity.description.Entity;
import org.complitex.dictionaryfw.entity.description.EntityAttributeType;
import org.complitex.dictionaryfw.entity.description.EntityAttributeValueType;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.strategy.StrategyFactory;

/**
 *
 * @author Artem
 */
public final class EntityDescriptionPanel extends Panel {

    @EJB(name = "StrategyFactory")
    private StrategyFactory strategyFactory;

    @EJB(name = "StringCultureBean")
    private StringCultureBean stringBean;

    public EntityDescriptionPanel(String id, String entity, Class<? extends WebPage> attributeEditPage, PageParameters attributeEditPageParams) {
        super(id);
        init(entity, attributeEditPage, attributeEditPageParams);
    }

    private Strategy getStrategy(String entity) {
        return strategyFactory.getStrategy(entity);
    }

    private void init(final String entity, Class<? extends WebPage> attributeEditPage, PageParameters attributeEditPageParams) {
        final Entity description = getStrategy(entity).getEntity();

        IModel<String> entityLabelModel = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return stringBean.displayValue(description.getEntityNames(), getLocale());
            }
        };
        IModel<String> labelModel = new StringResourceModel("label", null, new Object[]{entityLabelModel});
        add(new Label("title", labelModel));
        add(new Label("label", labelModel));

        ListView<EntityAttributeType> attributes = new ListView<EntityAttributeType>("attributes", description.getEntityAttributeTypes()) {

            @Override
            protected void populateItem(ListItem<EntityAttributeType> item) {
                final EntityAttributeType attributeType = item.getModelObject();
                item.add(new Label("name", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return stringBean.displayValue(attributeType.getAttributeNames(), getLocale());
                    }
                }));
                item.add(new Label("mandatory", new ResourceModel(attributeType.isMandatory() ? "yes" : "no")));

                final List<EntityAttributeValueType> valueTypes = attributeType.getEntityAttributeValueTypes();
                Label valueType = new Label("valueType", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return displayValueType(valueTypes.get(0).getValueType());
                    }
                });
                item.add(valueType);
                WebMarkupContainer valueTypesContainer = new WebMarkupContainer("valueTypesContainer");
                RepeatingView valueTypeItem = new RepeatingView("valueTypeItem");
                valueTypesContainer.add(valueTypeItem);
                item.add(valueTypesContainer);
                if (valueTypes.size() == 1) {
                    valueTypesContainer.setVisible(false);
                } else {
                    valueType.setVisible(false);
                    for (final EntityAttributeValueType currentValueType : valueTypes) {
                        valueTypeItem.add(new Label(String.valueOf(currentValueType.getId()), new AbstractReadOnlyModel<String>() {

                            @Override
                            public String getObject() {
                                return displayValueType(currentValueType.getValueType());
                            }
                        }));
                    }
                }
            }
        };
        attributes.setReuseItems(true);
        add(attributes);

        BookmarkablePageLink addAttribute = new BookmarkablePageLink("addAttribute", attributeEditPage, attributeEditPageParams);
        add(addAttribute);

        final String[] parents = getStrategy(entity).getParents();
        WebMarkupContainer parentsContainer = new WebMarkupContainer("parentsContainer");
        Label parentsInfo = new Label("parentsInfo", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if ((parents != null) && (parents.length > 0)) {
                    return displayParents(parents);
                }
                return null;
            }
        });
        parentsContainer.add(parentsInfo);
        add(parentsContainer);
        if (parents == null || parents.length == 0) {
            parentsContainer.setVisible(false);
        }
    }

    private String displayParents(String[] parents) {
        StringBuilder parentsLabel = new StringBuilder();
        for (int i = 0; i < parents.length; i++) {
            parentsLabel.append("'").
                    append(stringBean.displayValue(getStrategy(parents[i]).getEntity().getEntityNames(), getLocale())).
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
                        stringBean.displayValue(getStrategy(valueType).getEntity().getEntityNames(), getLocale())
                    }).getObject();
        }
    }
}

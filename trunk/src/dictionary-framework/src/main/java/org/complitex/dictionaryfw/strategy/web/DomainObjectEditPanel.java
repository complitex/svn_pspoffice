/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy.web;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionaryfw.converter.BooleanConverter;
import org.complitex.dictionaryfw.converter.DateConverter;
import org.complitex.dictionaryfw.converter.DoubleConverter;
import org.complitex.dictionaryfw.converter.IConverter;
import org.complitex.dictionaryfw.converter.IntegerConverter;
import org.complitex.dictionaryfw.dao.StringCultureBean;
import org.complitex.dictionaryfw.entity.Attribute;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.SimpleTypes;
import org.complitex.dictionaryfw.entity.StringCulture;
import org.complitex.dictionaryfw.entity.description.EntityAttributeType;
import org.complitex.dictionaryfw.entity.description.Entity;
import org.complitex.dictionaryfw.entity.description.EntityType;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.strategy.StrategyFactory;
import org.complitex.dictionaryfw.util.CloneUtil;
import org.complitex.dictionaryfw.web.component.BooleanPanel;
import org.complitex.dictionaryfw.web.component.ChildrenContainer;
import org.complitex.dictionaryfw.web.component.DatePanel;
import org.complitex.dictionaryfw.web.component.DoublePanel;
import org.complitex.dictionaryfw.web.component.IntegerPanel;
import org.complitex.dictionaryfw.web.component.SaveCancelPanel;
import org.complitex.dictionaryfw.web.component.StringCulturePanel;
import org.complitex.dictionaryfw.web.component.StringPanel;
import org.complitex.dictionaryfw.web.component.search.ISearchCallback;
import org.complitex.dictionaryfw.web.component.search.SearchComponent;
import org.complitex.dictionaryfw.web.component.search.SearchComponentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public final class DomainObjectEditPanel extends Panel {

    private static final Logger log = LoggerFactory.getLogger(DomainObjectEditPanel.class);

    @EJB(name = "StrategyFactory")
    private StrategyFactory strategyFactory;

    @EJB(name = "StringCultureBean")
    private StringCultureBean stringBean;

    private SearchComponentState searchComponentState;

    private boolean isNew;

    private boolean fromParent;

    private String entity;

    private DomainObject oldObject;

    private DomainObject newObject;

    private Long parentId;

    private String parentEntity;

    private static class SimpleTypeModel<T extends Serializable> extends Model<T> {

        private StringCulture systemLocaleStringCulture;

        private IConverter<T> converter;

        public SimpleTypeModel(StringCulture systemLocaleStringCulture, IConverter<T> converter) {
            this.systemLocaleStringCulture = systemLocaleStringCulture;
            this.converter = converter;
        }

        @Override
        public T getObject() {
            if (!Strings.isEmpty(systemLocaleStringCulture.getValue())) {
                return converter.toObject(systemLocaleStringCulture.getValue());
            }
            return null;
        }

        @Override
        public void setObject(T object) {
            if (object != null) {
                systemLocaleStringCulture.setValue(converter.toString(object));
            }
        }
    }

    public DomainObjectEditPanel(String id, String entity, Long objectId, Long parentId, String parentEntity) {
        super(id);
        this.entity = entity;
        this.parentId = parentId;
        this.parentEntity = parentEntity;

        fromParent = (parentId != null) && !Strings.isEmpty(parentEntity);

        if (objectId == null) {
            //create new entity
            isNew = true;
            oldObject = null;
            newObject = getStrategy().newInstance();

        } else {
            //edit existing entity
            newObject = getStrategy().findById(objectId);
            oldObject = CloneUtil.cloneObject(newObject);
        }
        init();
    }

    private Strategy getStrategy() {
        return strategyFactory.getStrategy(entity);
    }

    public DomainObject getObject() {
        return newObject;
    }

    private void init() {
        final Entity description = getStrategy().getEntity();

        Label title = new Label("title", stringBean.displayValue(description.getEntityNames(), getLocale()));
        add(title);

        Label label = new Label("label", stringBean.displayValue(description.getEntityNames(), getLocale()));
        add(label);

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        Form form = new Form("form");

        //entity type
        WebMarkupContainer typeContainer = new WebMarkupContainer("typeContainer");
        form.add(typeContainer);
        final List<EntityType> entityTypes = description.getEntityTypes() != null ? description.getEntityTypes() : new ArrayList<EntityType>();
        if (entityTypes.isEmpty()) {
            typeContainer.setVisible(false);
        }
        IModel<EntityType> typeModel = new Model<EntityType>() {

            @Override
            public void setObject(EntityType object) {
                newObject.setEntityTypeId(object.getId());
            }

            @Override
            public EntityType getObject() {
                if (newObject.getEntityTypeId() != null) {
                    return Iterables.find(entityTypes, new Predicate<EntityType>() {

                        @Override
                        public boolean apply(EntityType type) {
                            return type.getId().equals(newObject.getEntityTypeId());
                        }
                    });
                } else {
                    return null;
                }
            }
        };
        DropDownChoice<EntityType> types = new DropDownChoice<EntityType>("types", typeModel, entityTypes, new IChoiceRenderer<EntityType>() {

            @Override
            public Object getDisplayValue(EntityType object) {
                return stringBean.displayValue(object.getEntityTypeNames(), getLocale());
            }

            @Override
            public String getIdValue(EntityType object, int index) {
                return String.valueOf(object.getId());
            }
        });
        types.setLabel(new ResourceModel("entity_type"));
        types.setRequired(true);
        typeContainer.add(types);


        //simple attributes
        final Map<Attribute, EntityAttributeType> attrAndDesc = Maps.newHashMap();

        for (final Attribute attr : newObject.getAttributes()) {
            try {
                EntityAttributeType attrDesc = Iterables.find(description.getEntityAttributeTypes(), new Predicate<EntityAttributeType>() {

                    @Override
                    public boolean apply(EntityAttributeType attrDesc) {
                        return attrDesc.getId().equals(attr.getAttributeTypeId()) && getStrategy().isSimpleAttributeType(attrDesc);
                    }
                });
                attrAndDesc.put(attr, attrDesc);
            } catch (NoSuchElementException e) {
            }
        }

        ListView<Attribute> simpleAttributes = new ListView<Attribute>("simpleAttributes", Lists.newArrayList(attrAndDesc.keySet())) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                Attribute attr = item.getModelObject();
                final EntityAttributeType desc = attrAndDesc.get(attr);

                IModel<String> labelModel = new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return stringBean.displayValue(desc.getAttributeNames(), getLocale());
                    }
                };
                item.add(new Label("label", labelModel));

                WebMarkupContainer required = new WebMarkupContainer("required");
                item.add(required);
                required.setVisible(desc.isMandatory());


                String valueType = desc.getEntityAttributeValueTypes().get(0).getValueType();
                SimpleTypes type = SimpleTypes.valueOf(valueType.toUpperCase());

                Component input = null;
                final StringCulture systemLocaleStringCulture = stringBean.getSystemStringCulture(attr.getLocalizedValues());
                switch (type) {
                    case STRING: {
                        IConverter<String> stringConverter = new IConverter<String>() {

                            @Override
                            public String toString(String object) {
                                return object;
                            }

                            @Override
                            public String toObject(String value) {
                                return value;
                            }
                        };
                        IModel<String> model = new SimpleTypeModel<String>(systemLocaleStringCulture, stringConverter);
                        input = new StringPanel("input", model, desc.isMandatory(), labelModel, true);
                    }
                    break;
                    case STRING_CULTURE: {
                        IModel<List<StringCulture>> model = new PropertyModel<List<StringCulture>>(attr, "localizedValues");
                        input = new StringCulturePanel("input", model, desc.isMandatory(), labelModel, true);
                    }
                    break;
                    case INTEGER: {
                        IModel<Integer> model = new SimpleTypeModel<Integer>(systemLocaleStringCulture, new IntegerConverter());
                        input = new IntegerPanel("input", model, desc.isMandatory(), labelModel, true);
                    }
                    break;
                    case DATE: {
                        IModel<Date> model = new SimpleTypeModel<Date>(systemLocaleStringCulture, new DateConverter());
                        input = new DatePanel("input", model, desc.isMandatory(), labelModel, true);
                    }
                    break;
                    case BOOLEAN: {
                        IModel<Boolean> model = new SimpleTypeModel<Boolean>(systemLocaleStringCulture, new BooleanConverter());
                        input = new BooleanPanel("input", model, labelModel, true);
                    }
                    break;
                    case DOUBLE: {
                        IModel<Double> model = new SimpleTypeModel<Double>(systemLocaleStringCulture, new DoubleConverter());
                        input = new DoublePanel("input", model, desc.isMandatory(), labelModel, true);
                    }
                    break;
                }

                item.add(input);
            }
        };
        simpleAttributes.setReuseItems(true);
        form.add(simpleAttributes);

        //complex attributes
        AbstractComplexAttributesPanel complexAttributes = null;
        Class<? extends AbstractComplexAttributesPanel> clazz = getStrategy().getComplexAttributesPanelClass();
        if (clazz != null) {
            try {
                complexAttributes = clazz.getConstructor(String.class).newInstance("complexAttributes");

            } catch (Exception e) {
                log.warn("Couldn't instantiate complex attributes panel object.", e);
            }
        }
        if (complexAttributes == null) {
            form.add(new EmptyPanel("complexAttributes"));
        } else {
            form.add(complexAttributes);
        }

        //parent search
        if (isNew) {
            if (!fromParent) {
                searchComponentState = new SearchComponentState();
            } else {
                searchComponentState = getStrategy().getSearchComponentStateForParent(parentId, parentEntity);
            }
        } else {
            Strategy.RestrictedObjectInfo info = getStrategy().findParentInSearchComponent(newObject.getId());
            if (info != null) {
                searchComponentState = getStrategy().getSearchComponentStateForParent(info.getId(), info.getEntityTable());
            }
        }

        WebMarkupContainer parentContainer = new WebMarkupContainer("parentContainer");
        form.add(parentContainer);
        List<String> parentFilters = getStrategy().getParentSearchFilters();
        ISearchCallback parentSearchCallback = getStrategy().getParentSearchCallback();
        Component parentSearch = null;
        if (parentFilters == null || parentFilters.isEmpty() || parentSearchCallback == null) {
            parentContainer.setVisible(false);
            parentSearch = new EmptyPanel("parentSearch");
        } else {
            parentSearch = new SearchComponent("parentSearch", searchComponentState, parentFilters, parentSearchCallback);
        }
        parentContainer.add(parentSearch);

        //children
        Component childrenContainer = new EmptyPanel("childrenContainer");
        if (!isNew) {
            childrenContainer = new ChildrenContainer("childrenContainer", entity, newObject.getId());
        }
        form.add(childrenContainer);

        //save-cancel panel
        form.add(new SaveCancelPanel("saveCancelPanel", entity, oldObject, newObject, parentId, parentEntity));
        add(form);
    }

//    private void back() {
//        if (!fromParent) {
//            //return to list page for current entity.
//            setResponsePage(getStrategy().getListPage(), getStrategy().getListPageParams());
//        } else {
//            //return to edit page for parent entity.
//            Strategy parentStrategy = strategyFactory.getStrategy(parentEntity);
//            setResponsePage(parentStrategy.getEditPage(), parentStrategy.getEditPageParams(parentId, null, null));
//        }
//    }
    public SearchComponentState getParentSearchComponentState() {
        return searchComponentState;
    }
//    private boolean validateParent() {
//        if (!(getStrategy().getParentSearchFilters() == null || getStrategy().getParentSearchFilters().isEmpty()
//                || getStrategy().getParentSearchCallback() == null)) {
//            if ((newObject.getParentId() == null) || (newObject.getParentEntityId() == null)) {
//                error(getString("parent_required"));
//                return false;
//            }
//        }
//        return true;
//    }
}

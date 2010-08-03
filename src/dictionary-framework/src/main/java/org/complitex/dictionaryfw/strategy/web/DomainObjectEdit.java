/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy.web;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.description.AttributeDescription;
import org.complitex.dictionaryfw.entity.EntityAttribute;
import org.complitex.dictionaryfw.entity.SimpleTypes;
import org.complitex.dictionaryfw.entity.StringCulture;
import org.complitex.dictionaryfw.entity.description.DomainObjectDescription;
import org.complitex.dictionaryfw.entity.description.EntityType;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.strategy.StrategyFactory;
import org.complitex.dictionaryfw.util.CloneUtil;
import org.complitex.dictionaryfw.util.DisplayLocalizedValueUtil;
import org.complitex.dictionaryfw.web.component.ChildrenContainer;
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
public final class DomainObjectEdit extends WebPage {

    private static final Logger log = LoggerFactory.getLogger(DomainObjectEdit.class);

    public static final String ENTITY = "entity";

    public static final String OBJECT_ID = "object_id";

    public static final String PARENT_ID = "parent_id";

    public static final String PARENT_ENTITY = "parent_entity";

    @EJB(name = "StrategyFactory")
    private StrategyFactory strategyFactory;

    @EJB(name = "DisplayLocalizedValueUtil")
    private DisplayLocalizedValueUtil displayLocalizedValueUtil;

    private SearchComponentState searchComponentState;

    private boolean isNew;

    private boolean fromParent;

    private String entityTable;

    private DomainObject oldObject;

    private DomainObject newObject;

    private Long parentId;

    private String parentEntity;

    private Strategy getStrategy() {
        return strategyFactory.getStrategy(entityTable);
    }

    public DomainObject getObject() {
        return newObject;
    }

    public DomainObjectEdit(PageParameters parameters) {
        entityTable = parameters.getString(ENTITY);
        parentId = parameters.getAsLong(PARENT_ID);
        parentEntity = parameters.getString(PARENT_ENTITY);

        fromParent = (parentId != null) && !Strings.isEmpty(parentEntity);

        Long id = parameters.getAsLong(OBJECT_ID);
        if (id == null) {
            //create new entity
            isNew = true;
            oldObject = null;
            newObject = getStrategy().newInstance();

        } else {
            //edit existing entity
            newObject = getStrategy().findById(id);
            oldObject = CloneUtil.cloneObject(newObject);
        }
        init();
    }

    private void init() {
        final DomainObjectDescription description = getStrategy().getDescription();

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
                return displayLocalizedValueUtil.displayValue(object.getEntityTypeNames(), getLocale());
            }

            @Override
            public String getIdValue(EntityType object, int index) {
                return String.valueOf(object.getId());
            }
        });
        types.setRequired(true);
        typeContainer.add(types);


        //simple attributes
        final Map<EntityAttribute, AttributeDescription> attrAndDesc = Maps.newHashMap();

        for (final EntityAttribute attr : newObject.getAttributes()) {
            try {
                AttributeDescription attrDesc = Iterables.find(description.getAttributeDescriptions(), new Predicate<AttributeDescription>() {

                    @Override
                    public boolean apply(AttributeDescription attrDesc) {
                        return attrDesc.getId().equals(attr.getAttributeTypeId()) && getStrategy().isSimpleAttributeDesc(attrDesc);
                    }
                });
                attrAndDesc.put(attr, attrDesc);
            } catch (NoSuchElementException e) {
            }
        }

        ListView<EntityAttribute> simpleAttributes = new ListView<EntityAttribute>("simpleAttributes", Lists.newArrayList(attrAndDesc.keySet())) {

            @Override
            protected void populateItem(ListItem<EntityAttribute> item) {
                EntityAttribute attr = item.getModelObject();
                AttributeDescription desc = attrAndDesc.get(attr);

                boolean isSimpleInput = false;
                boolean isDate = false;
                boolean isString = false;
                boolean isSelectable = false;
                boolean isAutoComplete = false;
                boolean isBoolean = false;

                String valueType = desc.getAttributeValueDescriptions().get(0).getValueType();

                if (valueType.equalsIgnoreCase(SimpleTypes.STRING.name())) {
                    isString = true;
                }

                String label = displayLocalizedValueUtil.displayValue(desc.getAttributeNames(), getLocale());
                item.add(new Label("label", label));

                WebMarkupContainer required = new WebMarkupContainer("required");
                item.add(required);

                required.setVisible(isString ? false : desc.isMandatory());

                Panel stringPanel = new EmptyPanel("stringPanel");

                if (isString) {
                    IModel<List<StringCulture>> model = new PropertyModel<List<StringCulture>>(attr, "localizedValues");
                    stringPanel = new StringPanel("stringPanel", model, label, true, desc.isMandatory());
                }

                item.add(stringPanel);
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

        List<String> parentFilters = getStrategy().getParentSearchFilters();
        ISearchCallback parentSearchCallback = getStrategy().getParentSearchCallback();
        Component parentSearch = null;
        if (parentFilters == null || parentFilters.isEmpty() || parentSearchCallback == null) {
            parentSearch = new EmptyPanel("parentSearch");
        } else {
            parentSearch = new SearchComponent("parentSearch", searchComponentState, parentFilters, parentSearchCallback);
        }
        form.add(parentSearch);

        //children
        Component childrenContainer = new EmptyPanel("childrenContainer");
        if (!isNew) {
            childrenContainer = new ChildrenContainer("childrenContainer", entityTable, newObject.getId());
        }
        form.add(childrenContainer);


        Button submit = new Button("submit") {

            @Override
            public void onSubmit() {
                boolean valid = validateParent();
                IValidator validator = getStrategy().getValidator();
                if (validator != null) {
                    valid = validator.validate(newObject, DomainObjectEdit.this);
                }

                if (valid) {
                    if (isNew) {
                        getStrategy().insert(newObject);
                    } else {
                        getStrategy().update(oldObject, newObject);
                    }
                    back();
                }

            }
        };
        form.add(submit);
        Link cancel = new Link("cancel") {

            @Override
            public void onClick() {
                back();
            }
        };
        form.add(cancel);
        add(form);
    }

    private void back() {
        if (!fromParent) {
            //return to list page for current entity.
            setResponsePage(getStrategy().getListPage(), getStrategy().getListPageParams());
        } else {
            //return to edit page for parent entity.
            Strategy parentStrategy = strategyFactory.getStrategy(parentEntity);
            setResponsePage(parentStrategy.getEditPage(), parentStrategy.getEditPageParams(parentId, null, null));
        }
    }

    public SearchComponentState getParentSearchComponentState() {
        return searchComponentState;
    }

    private boolean validateParent() {
        if (!(getStrategy().getParentSearchFilters() == null || getStrategy().getParentSearchFilters().isEmpty()
                || getStrategy().getParentSearchCallback() == null)) {
            if ((newObject.getParentId() == null) || (newObject.getParentEntityId() == null)) {
                error("Parent must be specified.");
                return false;
            }
        }
        return true;
    }
}


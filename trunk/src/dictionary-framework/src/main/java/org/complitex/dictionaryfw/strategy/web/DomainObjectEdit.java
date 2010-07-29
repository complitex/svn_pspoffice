/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy.web;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.description.AttributeDescription;
import org.complitex.dictionaryfw.entity.EntityAttribute;
import org.complitex.dictionaryfw.entity.SimpleTypes;
import org.complitex.dictionaryfw.entity.StringCulture;
import org.complitex.dictionaryfw.entity.description.DomainObjectDescription;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.strategy.StrategyFactory;
import org.complitex.dictionaryfw.util.CloneUtil;
import org.complitex.dictionaryfw.util.DisplayLocalizedValueUtil;
import org.complitex.dictionaryfw.web.DictionaryFwSession;
import org.complitex.dictionaryfw.web.component.StringPanel;
import org.complitex.dictionaryfw.web.component.search.ISearchBehaviour;
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
                    stringPanel = new StringPanel("stringPanel", model, label, true);
                }

                item.add(stringPanel);
            }
        };
        simpleAttributes.setReuseItems(true);
        form.add(simpleAttributes);

        //parent search
        SearchComponentState componentState = null;
        if (isNew) {
            if (!fromParent) {
                componentState = new SearchComponentState();
            } else {
//                DomainObjectExample example = new DomainObjectExample();
//                DomainObject parent = strategyFactory.getStrategy(parentEntity).fi
                componentState = new SearchComponentState();
            }
        } else {
            componentState = CloneUtil.cloneObject(getSearchComponentState());
        }

        List<String> parentBehaviours = getStrategy().getParentSearchFilters();
        ISearchCallback parentSearchCallback = getStrategy().getParentSearchCallback();
        Component parentSearch = null;
        if (parentBehaviours == null || parentBehaviours.isEmpty() || parentSearchCallback == null) {
            parentSearch = new EmptyPanel("parentSearch");
        } else {
            parentSearch = new SearchComponent("parentSearch", componentState, parentBehaviours, parentSearchCallback);
        }
        form.add(parentSearch);


        Button submit = new Button("submit") {

            @Override
            public void onSubmit() {
                if (isNew) {
                    getStrategy().insert(newObject);
                } else {
                    getStrategy().update(oldObject, newObject);
                }
                back();
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
        }
    }

    protected DictionaryFwSession getDictionaryFwSession() {
        return (DictionaryFwSession) getSession();
    }

    protected SearchComponentState getSearchComponentState() {
        return getDictionaryFwSession().getSearchComponentSessionState().get(entityTable);
    }
}


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy.web;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionaryfw.converter.BooleanConverter;
import org.complitex.dictionaryfw.converter.DateConverter;
import org.complitex.dictionaryfw.converter.DoubleConverter;
import org.complitex.dictionaryfw.converter.IntegerConverter;
import org.complitex.dictionaryfw.dao.StringCultureBean;
import org.complitex.dictionaryfw.entity.Attribute;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.SimpleTypes;
import org.complitex.dictionaryfw.entity.description.EntityAttributeType;
import org.complitex.dictionaryfw.entity.description.Entity;
import org.complitex.dictionaryfw.entity.example.DomainObjectAttributeExample;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.strategy.StrategyFactory;
import org.complitex.dictionaryfw.util.DisplayLocalizedValueUtil;
import org.complitex.dictionaryfw.web.DictionaryFwSession;
import org.complitex.dictionaryfw.web.component.datatable.ArrowOrderByBorder;
import org.complitex.dictionaryfw.web.component.search.SearchComponent;
import org.complitex.dictionaryfw.web.component.search.SearchComponentSessionState;
import org.complitex.dictionaryfw.web.component.search.SearchComponentState;

/**
 *
 * @author Artem
 */
public final class DomainObjectListPanel extends Panel {

    @EJB(name = "StrategyFactory")
    private StrategyFactory strategyFactory;

    @EJB(name = "DisplayLocalizedValueUtil")
    private DisplayLocalizedValueUtil displayLocalizedValueUtil;

    @EJB(name = "StringCultureBean")
    private StringCultureBean stringBean;

    private String entity;

    private final DomainObjectExample example = new DomainObjectExample();

    private WebMarkupContainer content;

    private DataView<DomainObject> data;

    public DomainObjectListPanel(String id, String entity) {
        super(id);
        this.entity = entity;
        example.setTable(entity);
        init();
    }

    private Strategy getStrategy() {
        return strategyFactory.getStrategy(entity);
    }

    public DomainObjectExample getExample() {
        return example;
    }

    public void refreshContent(AjaxRequestTarget target) {
        content.setVisible(true);
        data.setCurrentPage(0);
        if (target != null) {
            target.addComponent(content);
        }
    }

    private void init() {
        List<String> searchFilters = getStrategy().getSearchFilters();

        content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        Component searchComponent = null;
        if (searchFilters == null || searchFilters.isEmpty()) {
            searchComponent = new EmptyPanel("searchComponent");
            content.setVisible(true);
        } else {
            SearchComponentState componentState = getSearchComponentState();
            searchComponent = new SearchComponent("searchComponent", componentState, searchFilters, getStrategy().getSearchCallback());
            content.setVisible(false);
        }
        add(searchComponent);
        add(content);

        final Entity description = getStrategy().getEntity();
        final List<EntityAttributeType> filterAttrDescs = getStrategy().getListColumns();

        for (EntityAttributeType filterAttrDesc : filterAttrDescs) {
            example.addAttributeExample(new DomainObjectAttributeExample(filterAttrDesc.getId()));
        }

        final SortableDataProvider<DomainObject> dataProvider = new SortableDataProvider<DomainObject>() {

            @Override
            public Iterator<? extends DomainObject> iterator(int first, int count) {
                boolean asc = getSort().isAscending();

                if (!Strings.isEmpty(getSort().getProperty())) {
                    Long sortProperty = Long.valueOf(getSort().getProperty());
                    example.setOrderByAttribureTypeId(sortProperty);
                }

                example.setLocale(getLocale().getLanguage());
                example.setAsc(asc);
                example.setStart(first);
                example.setSize(count);
                return getStrategy().find(example).iterator();
            }

            @Override
            public int size() {
                if (!Strings.isEmpty(getSort().getProperty())) {
                    Long sortProperty = Long.valueOf(getSort().getProperty());
                    example.setOrderByAttribureTypeId(sortProperty);
                }
                example.setLocale(getLocale().getLanguage());
                return getStrategy().count(example);
            }

            @Override
            public IModel<DomainObject> model(DomainObject object) {
                return new Model<DomainObject>(object);
            }
        };
        dataProvider.setSort("", true);

        Label title = new Label("title", displayLocalizedValueUtil.displayValue(description.getEntityNames(), getLocale()));
        add(title);

        Label label = new Label("label", displayLocalizedValueUtil.displayValue(description.getEntityNames(), getLocale()));
        add(label);

        final Form filterForm = new Form("filterForm");
        content.add(filterForm);

        ListView<EntityAttributeType> columns = new ListView<EntityAttributeType>("columns", filterAttrDescs) {

            @Override
            protected void populateItem(ListItem<EntityAttributeType> item) {
                EntityAttributeType attrDesc = item.getModelObject();
                ArrowOrderByBorder column = new ArrowOrderByBorder("column", String.valueOf(attrDesc.getId()), dataProvider, data, content);
                column.add(new Label("columnName", displayLocalizedValueUtil.displayValue(attrDesc.getAttributeNames(), getLocale())));
                item.add(column);
            }
        };
        columns.setReuseItems(true);
        filterForm.add(columns);

        AjaxLink reset = new AjaxLink("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();

                for (final EntityAttributeType attrDesc : filterAttrDescs) {
                    DomainObjectAttributeExample attrExample = Iterables.find(example.getAttributeExamples(),
                            new Predicate<DomainObjectAttributeExample>() {

                                @Override
                                public boolean apply(DomainObjectAttributeExample attrExample) {
                                    return attrExample.getAttributeTypeId().equals(attrDesc.getId());
                                }
                            });
                    attrExample.setValue(null);
                }
                target.addComponent(content);
            }
        };
        filterForm.add(reset);

        ListView<EntityAttributeType> filters = new ListView<EntityAttributeType>("filters", filterAttrDescs) {

            @Override
            protected void populateItem(ListItem<EntityAttributeType> item) {
                final EntityAttributeType attrDesc = item.getModelObject();

                IModel<String> filterModel = new Model<String>() {

                    @Override
                    public String getObject() {
                        return Iterables.find(example.getAttributeExamples(), new Predicate<DomainObjectAttributeExample>() {

                            @Override
                            public boolean apply(DomainObjectAttributeExample attrExample) {
                                return attrExample.getAttributeTypeId().equals(attrDesc.getId());
                            }
                        }).getValue();
                    }

                    @Override
                    public void setObject(String object) {
                        Iterables.find(example.getAttributeExamples(), new Predicate<DomainObjectAttributeExample>() {

                            @Override
                            public boolean apply(DomainObjectAttributeExample attrExample) {
                                return attrExample.getAttributeTypeId().equals(attrDesc.getId());
                            }
                        }).setValue(object);
                    }
                };
                TextField<String> filter = new TextField<String>("filter", filterModel);
                item.add(filter);
            }
        };
        filters.setReuseItems(true);
        filterForm.add(filters);

        AjaxButton submit = new AjaxButton("submit", filterForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(content);
            }
        };
        filterForm.add(submit);

        data = new DataView<DomainObject>("data", dataProvider, 5) {

            @Override
            protected void populateItem(Item<DomainObject> item) {
                DomainObject object = item.getModelObject();

                List<Attribute> attrs = Lists.newArrayList();
                for (final EntityAttributeType attrDesc : filterAttrDescs) {
                    Attribute attr = null;
                    try {
                        attr = Iterables.find(object.getAttributes(), new Predicate<Attribute>() {

                            @Override
                            public boolean apply(Attribute attr) {
                                return attr.getAttributeTypeId().equals(attrDesc.getId());
                            }
                        });
                    } catch (NoSuchElementException e) {
                        attr = new Attribute();
                        attr.setAttributeTypeId(-1L);
                    }
                    attrs.add(attr);
                }

                ListView<Attribute> dataColumns = new ListView<Attribute>("dataColumns", attrs) {

                    @Override
                    protected void populateItem(ListItem<Attribute> item) {
                        final Attribute attr = item.getModelObject();
                        String attributeValue = "";
                        if (!attr.getAttributeTypeId().equals(-1L)) {
                            EntityAttributeType desc = Iterables.find(filterAttrDescs, new Predicate<EntityAttributeType>() {

                                @Override
                                public boolean apply(EntityAttributeType attrDesc) {
                                    return attrDesc.getId().equals(attr.getAttributeTypeId());
                                }
                            });
                            String valueType = desc.getEntityAttributeValueTypes().get(0).getValueType();
                            SimpleTypes type = SimpleTypes.valueOf(valueType.toUpperCase());
                            String systemLocaleValue = stringBean.getSystemStringCulture(attr.getLocalizedValues()).getValue();
                            switch (type) {
                                case STRING_CULTURE:
                                    attributeValue = displayLocalizedValueUtil.displayValue(attr.getLocalizedValues(), getLocale());
                                    break;
                                case STRING:
                                    attributeValue = systemLocaleValue;
                                    break;
                                case DOUBLE:
                                    attributeValue = new DoubleConverter().toObject(systemLocaleValue).toString();
                                    break;
                                case INTEGER:
                                    attributeValue = new IntegerConverter().toObject(systemLocaleValue).toString();
                                    break;
                                case BOOLEAN:
                                    attributeValue = getString(new BooleanConverter().toObject(systemLocaleValue).toString());
                                    break;
                                case DATE:
                                    DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", getLocale());
                                    attributeValue = dateFormatter.format(new DateConverter().toObject(systemLocaleValue));
                                    break;
                            }
                        }
                        item.add(new Label("dataColumn", attributeValue));
                    }
                };
                item.add(dataColumns);
                item.add(new BookmarkablePageLink("detailsLink", getStrategy().getEditPage(),
                        getStrategy().getEditPageParams(object.getId(), null, null)));
            }
        };
        filterForm.add(data);

        add(new BookmarkablePageLink("add", getStrategy().getEditPage(), getStrategy().getEditPageParams(null, null, null)));
    }

    protected DictionaryFwSession getDictionaryFwSession() {
        return (DictionaryFwSession) getSession();
    }

    protected SearchComponentState getSearchComponentState() {
        SearchComponentSessionState searchComponentSessionState = getDictionaryFwSession().getSearchComponentSessionState();
        SearchComponentState componentState = searchComponentSessionState.get(entity);
        if (componentState == null) {
            componentState = new SearchComponentState();
            searchComponentSessionState.put(entity, componentState);
        }
        return componentState;
    }
}

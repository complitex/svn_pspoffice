/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy.web;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.dictionaryfw.entity.AttributeDescription;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.EntityAttribute;
import org.complitex.dictionaryfw.entity.SimpleTypes;
import org.complitex.dictionaryfw.entity.StringCulture;
import org.complitex.dictionaryfw.entity.description.DomainObjectDescription;
import org.complitex.dictionaryfw.entity.example.DomainObjectAttributeExample;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.web.component.datatable.ArrowOrderByBorder;

/**
 *
 * @author Artem
 */
public abstract class DomainObjectList extends WebPage {

    public DomainObjectList() {
        init();
    }

    protected abstract Strategy getStrategy();

    private void init() {

        final DomainObjectDescription description = getStrategy().getDescription();
        final List<AttributeDescription> filterAttrDescs = description.getFilterAttributes();

        final DomainObjectExample example = new DomainObjectExample();
        for (AttributeDescription filterAttrDesc : filterAttrDescs) {
            example.addAttributeExample(new DomainObjectAttributeExample(filterAttrDesc.getId()));
        }

        final SortableDataProvider<DomainObject> dataProvider = new SortableDataProvider<DomainObject>() {

            @Override
            public Iterator<? extends DomainObject> iterator(int first, int count) {
                boolean asc = getSort().isAscending();
                Long sortProperty = Long.valueOf(getSort().getProperty());
                example.setOrderByAttribureTypeId(sortProperty);
                example.setAsc(asc);
                return getStrategy().find(example).iterator();
            }

            @Override
            public int size() {
                Long sortProperty = Long.valueOf(getSort().getProperty());
                example.setOrderByAttribureTypeId(sortProperty);
                return getStrategy().count(example);
            }

            @Override
            public IModel<DomainObject> model(DomainObject object) {
                return new Model<DomainObject>(object);
            }
        };
        dataProvider.setSort("", true);

        final Form filterForm = new Form("filterForm");

        ListView<AttributeDescription> columns = new ListView<AttributeDescription>("columns", filterAttrDescs) {

            @Override
            protected void populateItem(ListItem<AttributeDescription> item) {
                AttributeDescription attrDesc = item.getModelObject();
                ArrowOrderByBorder column = new ArrowOrderByBorder("column", String.valueOf(attrDesc.getId()), dataProvider);
                column.add(new Label("columnName", attrDesc.getLocalizedAttributeName(getLocale())));
                item.add(column);
            }
        };
        filterForm.add(columns);


        Link reset = new Link("reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();
                for (DomainObjectAttributeExample attributeExample : example.getAttributeExamples()) {
                    attributeExample.setValue(null);
                }
            }
        };
        filterForm.add(reset);


        ListView<AttributeDescription> filters = new ListView<AttributeDescription>("filters", filterAttrDescs) {

            @Override
            protected void populateItem(ListItem<AttributeDescription> item) {
                final AttributeDescription attrDesc = item.getModelObject();

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
                TextField filter = new TextField("filter", filterModel);
                item.add(filter);
            }
        };
        filterForm.add(filters);

        final DataView<DomainObject> data = new DataView<DomainObject>("data", dataProvider, 5) {

            @Override
            protected void populateItem(Item<DomainObject> item) {
                DomainObject entity = item.getModelObject();

                List<EntityAttribute> attrs = Lists.newArrayList();
                for (final AttributeDescription attrDesc : filterAttrDescs) {
                    EntityAttribute attr = null;
                    try {
                        attr = Iterables.find(entity.getSimpleAttributes(description), new Predicate<EntityAttribute>() {

                            @Override
                            public boolean apply(EntityAttribute attr) {
                                return attr.getAttributeTypeId().equals(attrDesc.getId());
                            }
                        });
                    } catch (NoSuchElementException e) {
                        attr = new EntityAttribute();
                        attr.setAttributeTypeId(-1L);
                    }
                    attrs.add(attr);
                }

                ListView<EntityAttribute> dataColumns = new ListView<EntityAttribute>("dataColumns", attrs) {

                    @Override
                    protected void populateItem(ListItem<EntityAttribute> item) {
                        final EntityAttribute attr = item.getModelObject();
                        String attributeValue = "";
                        if (!attr.getAttributeTypeId().equals(-1L)) {
                            AttributeDescription desc = Iterables.find(filterAttrDescs, new Predicate<AttributeDescription>() {

                                @Override
                                public boolean apply(AttributeDescription attrDesc) {
                                    return attrDesc.getId().equals(attr.getAttributeTypeId());
                                }
                            });
                            String valueType = desc.getAttributeValueDescriptions().get(0).getValueType();
                            SimpleTypes type = SimpleTypes.valueOf(valueType.toUpperCase());
                            switch (type) {
                                case STRING:
                                    attributeValue = Iterables.find(attr.getLocalizedValues(), new Predicate<StringCulture>() {

                                        @Override
                                        public boolean apply(StringCulture string) {
                                            return new Locale(string.getLocale()).equals(getLocale());
                                        }
                                    }).getValue();
                                    break;
                                case DOUBLE:
                                case INTEGER:
                                    attributeValue = attr.getLocalizedValues().get(0).getValue();
                                    break;
                                case DATE:
                                    break;

                            }
                        }
                        item.add(new Label("dataColumn", attributeValue));
                    }
                };
                item.add(dataColumns);
            }
        };
        filterForm.add(data);
        add(filterForm);

    }
}


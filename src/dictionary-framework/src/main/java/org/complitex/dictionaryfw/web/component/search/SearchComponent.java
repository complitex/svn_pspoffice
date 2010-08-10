package org.complitex.dictionaryfw.web.component.search;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.dictionaryfw.dao.StringCultureBean;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;
import org.complitex.dictionaryfw.strategy.StrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public final class SearchComponent extends Panel {

    private static final String NOT_SPECIFIED = "Not specified";

    private static final Logger log = LoggerFactory.getLogger(SearchComponent.class);

    @EJB(name = "StringCultureBean")
    private StringCultureBean stringBean;

    @EJB(name = "StrategyFactory")
    private StrategyFactory strategyFactory;

    private static final int AUTO_COMPLETE_SIZE = 10;

    private List<String> searchFilters;

    private ISearchCallback callback;

    private SearchComponentState componentState;

    public SearchComponent(String id, SearchComponentState componentState, List<String> searchFilters, ISearchCallback callback) {
        super(id);
        setOutputMarkupId(true);
        this.componentState = componentState;
        this.searchFilters = searchFilters;
        this.callback = callback;
        init();
    }

    private class FilterModel extends AutoCompleteTextField.AutoCompleteTextFieldModel {

        public FilterModel(IModel<DomainObject> model, String entityTable) {
            super(model, entityTable);
        }

        @Override
        public String getTextValue(DomainObject object) {
            if (object.getId().equals(-1L)) {
                return NOT_SPECIFIED;
            } else {
                return strategyFactory.getStrategy(getEntityTable()).displayDomainObject(object, getLocale());
            }
        }
    }

    private class Renderer extends AbstractAutoCompleteTextRenderer<DomainObject> {

        private String entityTable;

        public Renderer(String entityTable) {
            this.entityTable = entityTable;
        }

        @Override
        protected String getTextValue(DomainObject object) {
            if (object.getId().equals(-1L)) {
                return NOT_SPECIFIED;
            } else {
                return strategyFactory.getStrategy(entityTable).displayDomainObject(object, getLocale());
            }
        }
    }

    private void init() {
        final WebMarkupContainer searchPanel = new WebMarkupContainer("searchPanel");
        searchPanel.setOutputMarkupId(true);
        final AutoCompleteSettings settings = new AutoCompleteSettings();
        settings.setAdjustInputWidth(false);

//        List<String> entityTitles = Lists.newArrayList(Iterables.transform(searchFilters, new Function<String, String>() {
//
//            @Override
//            public String apply(String entity) {
//                return stringBean.displayValue(strategyFactory.getStrategy(entity).getEntity().getEntityNames(),
//                        getLocale());
//            }
//        }));

        ListView<String> columns = new ListView<String>("columns", searchFilters) {

            @Override
            protected void populateItem(ListItem<String> item) {
                final String entityTable = item.getModelObject();
                IModel<String> entityLabelModel = new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return stringBean.displayValue(strategyFactory.getStrategy(entityTable).getEntity().getEntityNames(), getLocale());
                    }
                };
                item.add(new Label("column", entityLabelModel));
            }
        };
        searchPanel.add(columns);

        final List<IModel<DomainObject>> filterModels = Lists.newArrayList(Iterables.transform(searchFilters,
                new Function<String, IModel<DomainObject>>() {

                    @Override
                    public IModel<DomainObject> apply(final String entity) {
                        return new Model<DomainObject>();
                    }
                }));

        ListView<String> filters = new ListView<String>("filters", searchFilters) {

            @Override
            protected void populateItem(final ListItem<String> item) {
                final String entity = item.getModelObject();
                final int index = item.getIndex();
                Renderer renderer = new Renderer(entity);
                final IModel<DomainObject> model = filterModels.get(index);
                DomainObject fromComponentState = componentState.get(entity);
                if (fromComponentState != null) {
                    model.setObject(fromComponentState);
                }
                AutoCompleteTextField filter = new AutoCompleteTextField("filter", new FilterModel(model, entity),
                        renderer, settings) {

                    @Override
                    protected List<DomainObject> getChoiceList(String searchTextInput) {
                        Map<String, DomainObject> previousInfo = getState(index - 1, filterModels);
                        if (!isComplete(previousInfo)) {
                            return Collections.emptyList();
                        }

                        DomainObjectExample example = new DomainObjectExample();
                        example.setTable(entity);
                        strategyFactory.getStrategy(entity).configureExample(example, transformObjects(previousInfo), searchTextInput);
                        example.setStart(0);
                        example.setSize(AUTO_COMPLETE_SIZE);
                        example.setLocale(getLocale().getLanguage());
                        List<DomainObject> list = strategyFactory.getStrategy(entity).find(example);

                        DomainObject notSpecified = new DomainObject();
                        notSpecified.setId(-1L);
                        list.add(notSpecified);

                        return list;
                    }
                };
                filter.setOutputMarkupId(true);
                if (index == searchFilters.size() - 1) {
                    invokeCallbackIfNecessary(index, filterModels, null);

                    filter.add(new AjaxFormComponentUpdatingBehavior("onblur") {

                        @Override
                        protected void onUpdate(AjaxRequestTarget target) {
                            //update model

                            invokeCallbackIfNecessary(index, filterModels, target);
                        }
                    });
                } else {
                    filter.add(new AjaxFormComponentUpdatingBehavior("onblur") {

                        @Override
                        protected void onUpdate(AjaxRequestTarget target) {
                            //update model
                            //update other models
//                            if (model.getObject() != null) {
//                                for (int j = index + 1; j < filterModels.size(); j++) {
//                                    filterModels.get(j).setObject(null);
//                                }
//                            }
//                            componentState.clear();
//
//                            refreshFilters(target);
//                            setFocusOnNextFilter(target);
                        }

                        private void refreshFilters(final AjaxRequestTarget target) {
                            ListView view = (ListView) item.getParent();
                            view.visitChildren(ListItem.class, new IVisitor<ListItem<String>>() {

                                @Override
                                public Object component(ListItem<String> visitedItem) {
                                    if (visitedItem.getIndex() > index) {
                                        target.addComponent(visitedItem.get("filter"));
                                    }
                                    return CONTINUE_TRAVERSAL;
                                }
                            });
                        }

                        private void setFocusOnNextFilter(AjaxRequestTarget target) {
                            ListItem<String> nextItem = (ListItem<String>) ((ListView) item.getParent()).get(index + 1);
                            target.focusComponent(nextItem.get("filter"));
                        }
                    });
                }
                item.add(filter);
            }
        };
        searchPanel.add(filters);
        add(searchPanel);
    }

    private Map<String, DomainObject> getState(int idx, List<IModel<DomainObject>> filterModels) {
        Map<String, DomainObject> objects = Maps.newHashMap();
        int index = idx;
        while (index > -1) {
            DomainObject object = filterModels.get(index).getObject();
            objects.put(searchFilters.get(index), object);
            index--;

        }
        return objects;
    }

    private boolean isComplete(Map<String, DomainObject> state) {
        for (String entity : state.keySet()) {
            if (state.get(entity) == null) {
                return false;
            }
        }
        return true;
    }

    private void invokeCallbackIfNecessary(int index, List<IModel<DomainObject>> filterModels, AjaxRequestTarget target) {
        Map<String, DomainObject> finalState = getState(index, filterModels);
        if (isComplete(finalState)) {
            Map<String, Long> ids = transformObjects(finalState);
            componentState.updateState(finalState);
            callback.found(this, ids, target);
        }
    }

    private Map<String, Long> transformObjects(Map<String, DomainObject> objects) {
        return Maps.transformValues(objects, new Function<DomainObject, Long>() {

            @Override
            public Long apply(DomainObject from) {
                return from != null ? from.getId() : null;
            }
        });
    }
}

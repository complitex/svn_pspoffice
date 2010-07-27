package org.complitex.dictionaryfw.web.component.search;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;
import org.complitex.dictionaryfw.strategy.StrategyFactory;
import org.complitex.dictionaryfw.util.DisplayLocalizedValueUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public final class SearchComponent extends Panel {

    private static final Logger log = LoggerFactory.getLogger(SearchComponent.class);

    @EJB(name = "DisplayLocalizedValueUtil")
    private DisplayLocalizedValueUtil displayLocalizedValueUtil;

    @EJB(name = "StrategyFactory")
    private StrategyFactory strategyFactory;

    private static final int AUTO_COMPLETE_SIZE = 10;

    private List<ISearchBehaviour> behaviours;

    private ISearchCallback callback;

    public SearchComponent(String id, List<ISearchBehaviour> behaviours, ISearchCallback callback) {
        super(id);
        this.behaviours = behaviours;
        this.callback = callback;
        init();
    }

    private class FilterModel extends AutoCompleteTextField.AutoCompleteTextFieldModel {

        public FilterModel(IModel<DomainObject> model, String entityTable) {
            super(model, entityTable);
        }

        @Override
        public String getTextValue(DomainObject object) {
            return strategyFactory.getStrategy(getEntityTable()).displayDomainObject(object, getLocale());
        }
    }

    private class SearchPanelUpdater extends AjaxFormComponentUpdatingBehavior {

        public SearchPanelUpdater() {
            super("onblur");
        }

        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            //update model
        }
    }

    private class Renderer extends AbstractAutoCompleteTextRenderer<DomainObject> {

        private String entityTable;

        public Renderer(String entityTable) {
            this.entityTable = entityTable;
        }

        @Override
        protected String getTextValue(DomainObject object) {
            return strategyFactory.getStrategy(entityTable).displayDomainObject(object, getLocale());
        }
    }

    private void init() {

        final WebMarkupContainer searchPanel = new WebMarkupContainer("searchPanel");
        searchPanel.setOutputMarkupId(true);
        final AutoCompleteSettings settings = new AutoCompleteSettings();
        settings.setAdjustInputWidth(false);

        List<String> entityTitles = Lists.newArrayList(Iterables.transform(behaviours, new Function<ISearchBehaviour, String>() {

            @Override
            public String apply(ISearchBehaviour behaviour) {
                return displayLocalizedValueUtil.displayValue(strategyFactory.getStrategy(behaviour.getEntityTable()).getDescription().getEntityNames(),
                        getLocale());
            }
        }));


        ListView<String> columns = new ListView<String>("columns", entityTitles) {

            @Override
            protected void populateItem(ListItem<String> item) {
                String entityTitle = item.getModelObject();
                item.add(new Label("column", entityTitle));
            }
        };
        searchPanel.add(columns);

        final List<IModel<DomainObject>> filterModels = Lists.newArrayList(Iterables.transform(behaviours, new Function<ISearchBehaviour, IModel<DomainObject>>() {

            @Override
            public IModel<DomainObject> apply(final ISearchBehaviour behaviour) {
                return new Model<DomainObject>();
            }
        }));

        ListView<ISearchBehaviour> filters = new ListView<ISearchBehaviour>("filters", behaviours) {

            @Override
            protected void populateItem(final ListItem<ISearchBehaviour> item) {
                final ISearchBehaviour behaviour = item.getModelObject();

                Renderer renderer = new Renderer(behaviour.getEntityTable());
                AutoCompleteTextField filter = new AutoCompleteTextField("filter", new FilterModel(filterModels.get(item.getIndex()),
                        behaviour.getEntityTable()), renderer, settings) {

                    @Override
                    protected List<DomainObject> getChoiceList(String searchTextInput) {
                        Map<String, DomainObject> previousInfo = Maps.newHashMap();
                        int index = item.getIndex() - 1;
                        while (index > -1) {
                            previousInfo.put(behaviours.get(index).getEntityTable(), filterModels.get(index).getObject());
                            index--;
                        }

                        DomainObjectExample example = behaviour.getExample(searchTextInput, previousInfo);
                        example.setStart(0);
                        example.setSize(AUTO_COMPLETE_SIZE);
                        example.setLocale(getLocale().getLanguage());
                        return strategyFactory.getStrategy(behaviour.getEntityTable()).find(example);
                    }
                };
                if (item.getIndex() == behaviours.size() - 1) {
                    filter.add(new AjaxFormComponentUpdatingBehavior("onblur") {

                        @Override
                        protected void onUpdate(AjaxRequestTarget target) {
                            //update model
                            if (filterModels.get(item.getIndex()).getObject() != null) {
                                Map<String, Long> ids = Maps.newHashMap();
                                int index = item.getIndex();
                                while (index > -1) {
                                    ids.put(behaviours.get(index).getEntityTable(), filterModels.get(index).getObject().getId());
                                    index--;
                                }
                                callback.found(getWebPage(), ids, target);
                            }
                        }
                    });
                } else {
                    filter.add(new AjaxFormComponentUpdatingBehavior("onblur") {

                        @Override
                        protected void onUpdate(AjaxRequestTarget target) {
                            //update model
                        }
                    });
                }
                item.add(filter);
            }
        };
        searchPanel.add(filters);
        add(searchPanel);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.commons.strategy.building.web.edit;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.complitex.dictionaryfw.dao.LocaleDao;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.EntityAttribute;
import org.complitex.dictionaryfw.entity.StringCulture;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;
import org.complitex.dictionaryfw.strategy.web.AbstractComplexAttributesPanel;
import org.complitex.dictionaryfw.web.component.StringPanel;
import org.complitex.dictionaryfw.web.component.search.ISearchCallback;
import org.complitex.dictionaryfw.web.component.search.SearchComponent;
import org.complitex.dictionaryfw.web.component.search.SearchComponentState;
import org.complitex.pspoffice.commons.strategy.building.BuildingStrategy;
import org.complitex.pspoffice.commons.strategy.street.StreetStrategy;
import org.complitex.pspoffice.commons.strategy.web.component.list.AjaxRemovableListView;

/**
 *
 * @author Artem
 */
public class BuildingEditComponent extends AbstractComplexAttributesPanel {

    @EJB(name = "LocaleDao")
    private LocaleDao localeDao;

    @EJB(name = "BuildingStrategy")
    private BuildingStrategy buildingStrategy;

    @EJB(name = "StreetStrategy")
    private StreetStrategy streetStrategy;

    private boolean firstRendering = true;

    public BuildingEditComponent(String id) {
        super(id);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if (firstRendering) {
            firstRendering = false;
            init();
        }
    }

    private class SynchronizedSearchComponentState extends SearchComponentState {

        private SearchComponentState parentComponentState;

        private DomainObject street;

        public SynchronizedSearchComponentState(SearchComponentState parentComponentState, DomainObject street) {
            this.parentComponentState = parentComponentState;
            this.street = street;
        }

        @Override
        public void updateState(Map<String, DomainObject> state) {
            Map<String, DomainObject> filterState = Maps.filterKeys(state, new Predicate<String>() {

                @Override
                public boolean apply(String entity) {
                    return buildingStrategy.getParentSearchFilters().contains(entity);
                }
            });
            parentComponentState.updateState(filterState);
            this.street = state.get("street");
        }

        @Override
        public DomainObject get(String entity) {
            if (!entity.equals("street")) {
                return parentComponentState.get(entity);
            } else {
                return street;
            }
        }

        @Override
        public void put(String entity, DomainObject object) {
            throw new UnsupportedOperationException();
        }
    }

    private static class StreetSearchCallback implements ISearchCallback, Serializable {

        private BuildingAttribute buildingAttribute;

        public StreetSearchCallback(BuildingAttribute buildingAttribute) {
            this.buildingAttribute = buildingAttribute;
        }

        @Override
        public void found(WebPage page, final Map<String, Long> ids, final AjaxRequestTarget target) {
            page.visitChildren(SearchComponent.class, new IVisitor<SearchComponent>() {

                @Override
                public Object component(SearchComponent searchComponent) {
                    Long streetId = ids.get("street");
//                    if (streetId != null && streetId > 0) {
                        buildingAttribute.getStreet().setValueId(streetId);
//                    }
                    if (target != null) {
                        target.addComponent(searchComponent);
                    }
                    return CONTINUE_TRAVERSAL;
                }
            });
        }
    }

    public void init() {
        final WebMarkupContainer attributesContainer = new WebMarkupContainer("attributesContainer");
        attributesContainer.setOutputMarkupId(true);
        add(attributesContainer);

        final BuildingAttributeList list = new BuildingAttributeList(getEditPage().getObject(), localeDao.getAllLocales());
        AjaxLink add = new AjaxLink("add") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                list.addNew();
                target.addComponent(attributesContainer);
            }
        };
        add(add);

        final SearchComponentState parentSearchComponentState = getEditPage().getParentSearchComponentState();
        ListView<BuildingAttribute> buildingAttributes = new AjaxRemovableListView<BuildingAttribute>("buildingAttributes", list) {

            @Override
            protected void populateItem(ListItem<BuildingAttribute> item) {
                BuildingAttribute buildingAttribute = item.getModelObject();

                item.add(newStringPanel("number", buildingAttribute.getNumber(), "Number", true));
                item.add(newStringPanel("corp", buildingAttribute.getCorp(), "Corp", false));
                item.add(newStringPanel("structure", buildingAttribute.getCorp(), "Structure", false));

                DomainObject street = null;
                Long streetId = buildingAttribute.getStreet().getValueId();
                Long cityId = parentSearchComponentState.get("city").getId();
                if (streetId != null) {
                    DomainObjectExample example = new DomainObjectExample();
                    example.setId(streetId);
                    example.setStart(0);
                    example.setSize(1);
                    streetStrategy.configureExample(example, ImmutableMap.of("city", cityId), null);
                    street = streetStrategy.find(example).get(0);
                }

                item.add(new SearchComponent("street", new SynchronizedSearchComponentState(parentSearchComponentState, street),
                        buildingStrategy.getSearchFilters(), new StreetSearchCallback(buildingAttribute)));

                addRemoveLink("remove", item, null, attributesContainer);
            }
        };
        attributesContainer.add(buildingAttributes);
    }

    private static StringPanel newStringPanel(String id, EntityAttribute attr, String label, boolean required) {
        IModel<List<StringCulture>> model = new PropertyModel<List<StringCulture>>(attr, "localizedValues");
        return new StringPanel(id, model, label, true, required);
    }
}

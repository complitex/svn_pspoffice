/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.list.apartment_card.grid;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.ComparisonType;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.back.BackInfo;
import org.complitex.dictionary.web.component.back.BackInfoManager;
import org.complitex.dictionary.web.component.back.BookmarkableBackInfo;
import org.complitex.dictionary.web.component.datatable.DataProvider;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.pspoffice.person.strategy.entity.grid.BuildingsGridEntity;
import org.complitex.pspoffice.person.strategy.entity.grid.BuildingsGridFilter;
import org.complitex.pspoffice.person.strategy.service.BuildingsGridBean;
import org.complitex.pspoffice.person.strategy.web.component.grid.FilterSearchComponent;
import org.complitex.pspoffice.person.strategy.web.list.apartment_card.ApartmentCardSearch;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.pages.ListPage;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class BuildingsGrid extends ListPage {

    public static final String CITY_PARAM = "cityId";
    public static final String STREET_PARAM = "streetId";
    private static final String PAGE_SESSION_KEY = "buildings_grid_page";
    @EJB
    private DistrictStrategy districtStrategy;
    @EJB
    private StreetStrategy streetStrategy;
    @EJB
    private BuildingStrategy buildingStrategy;
    @EJB
    private BuildingsGridBean buildingsGridBean;
    @EJB(name = "OrganizationStrategy")
    private IOrganizationStrategy organizationStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;

    private static class BuildingsGridFilterSearchComponent extends FilterSearchComponent {

        @EJB
        private StrategyFactory strategyFactory;
        @EJB
        private LocaleBean localeBean;
        private final String entity;
        private final long cityId;

        private BuildingsGridFilterSearchComponent(String id, String entity, long cityId, IModel<DomainObject> filterModel) {
            super(id, filterModel);
            this.entity = entity;
            this.cityId = cityId;
            super.init();
        }

        @Override
        protected List<? extends DomainObject> find(ComparisonType comparisonType, String term, int size) {
            IStrategy strategy = strategy();
            DomainObjectExample example = new DomainObjectExample();
            strategy.configureExample(example, ImmutableMap.of("city", cityId), term);
            example.setOrderByAttributeTypeId(strategy.getDefaultOrderByAttributeId());
            example.setAsc(true);
            example.setSize(size);
            example.setLocaleId(localeBean.convert(getLocale()).getId());
            example.setComparisonType(comparisonType.name());
            example.setStatus(ShowMode.ACTIVE.name());
            return strategy.find(example);
        }

        @Override
        protected String render(DomainObject object) {
            return strategy().displayDomainObject(object, getLocale());
        }

        private IStrategy strategy() {
            return strategyFactory.getStrategy(entity);
        }
    }

    public BuildingsGrid(PageParameters parameters) {
        final long cityId = parameters.getAsLong(CITY_PARAM);
        final Long streetId = parameters.getAsLong(STREET_PARAM);
        final boolean streetEnabled = streetId != null && streetId > 0;

        IModel<String> labelModel = new StringResourceModel("label", null,
                new Object[]{addressRendererBean.displayAddress(streetEnabled ? "street" : "city",
                    streetEnabled ? streetId : cityId, getLocale())});

        add(new Label("title", labelModel));
        add(new Label("label", labelModel));

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        add(content);

        //Filter
        final BuildingsGridFilter filter = buildingsGridBean.newFilter(cityId, streetId, getLocale());

        //Form
        final Form<Void> filterForm = new Form<Void>("filterForm");
        content.add(filterForm);

        //Data Provider
        final DataProvider<BuildingsGridEntity> dataProvider = new DataProvider<BuildingsGridEntity>() {

            @Override
            protected Iterable<BuildingsGridEntity> getData(int first, int count) {
                filter.setStart(first);
                filter.setSize(count);
                return buildingsGridBean.find(filter);
            }

            @Override
            protected int getSize() {
                return buildingsGridBean.count(filter);
            }
        };

        //Filters
        //district
        final IModel<DomainObject> districtFilterModel = new Model<DomainObject>() {

            @Override
            public void setObject(DomainObject district) {
                super.setObject(district);
                filter.setDistrictId(district != null ? district.getId() : null);
            }
        };
        filterForm.add(new BuildingsGridFilterSearchComponent("districtFilter", "district", cityId, districtFilterModel));

        //street
        final IModel<DomainObject> streetFilterModel = new Model<DomainObject>() {

            @Override
            public void setObject(DomainObject street) {
                super.setObject(street);
                filter.setStreetId(street != null ? street.getId() : null);
            }
        };
        final BuildingsGridFilterSearchComponent streetFilter =
                new BuildingsGridFilterSearchComponent("streetFilter", "street", cityId, streetFilterModel);
        filterForm.add(streetFilter);
        if (streetEnabled) {
            streetFilterModel.setObject(streetStrategy.findById(streetId, true));
            streetFilter.setEnabled(false);
        }

        //building
        filterForm.add(new TextField<String>("buildingFilter", new PropertyModel<String>(filter, "buildingNumber")));

        //Data View
        DataView<BuildingsGridEntity> buildings = new DataView<BuildingsGridEntity>("buildings", dataProvider, 1) {

            @Override
            protected void populateItem(Item<BuildingsGridEntity> item) {
                final BuildingsGridEntity buildingsGridEntity = item.getModelObject();

                //order
                item.add(new Label("order", StringUtil.valueOf(getViewOffset() + item.getIndex() + 1)));

                //district
                Link<Void> districtLink = new Link<Void>("districtLink") {

                    @Override
                    public void onClick() {
                        PageParameters params = districtStrategy.getEditPageParams(buildingsGridEntity.getDistrictId(), null, null);
                        BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(cityId, streetId));
                        params.put(TemplateStrategy.BACK_INFO_SESSION_KEY, PAGE_SESSION_KEY);
                        setResponsePage(districtStrategy.getEditPage(), params);
                    }
                };
                item.add(districtLink);
                districtLink.add(new Label("district", buildingsGridEntity.getDistrict()));

                //street
                Link<Void> streetLink = new Link<Void>("streetLink") {

                    @Override
                    public void onClick() {
                        PageParameters params = streetStrategy.getEditPageParams(buildingsGridEntity.getStreetId(), null, null);
                        BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(cityId, streetId));
                        params.put(TemplateStrategy.BACK_INFO_SESSION_KEY, PAGE_SESSION_KEY);
                        setResponsePage(streetStrategy.getEditPage(), params);
                    }
                };
                item.add(streetLink);
                streetLink.add(new Label("street", buildingsGridEntity.getStreet()));

                //building
                Link<Void> buildingLink = new Link<Void>("buildingLink") {

                    @Override
                    public void onClick() {
                        PageParameters params = buildingStrategy.getEditPageParams(buildingsGridEntity.getBuildingId(), null, null);
                        BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(cityId, streetId));
                        params.put(TemplateStrategy.BACK_INFO_SESSION_KEY, PAGE_SESSION_KEY);
                        setResponsePage(buildingStrategy.getEditPage(), params);
                    }
                };
                item.add(buildingLink);
                buildingLink.add(new Label("building", buildingsGridEntity.getBuilding()));

                //apartments
                Link<Void> apartmentsLink = new Link<Void>("apartmentsLink") {

                    @Override
                    public void onClick() {
                    }
                };
                item.add(apartmentsLink);
                apartmentsLink.add(new Label("apartments", String.valueOf(buildingsGridEntity.getApartments())));

                item.add(new ListView<DomainObject>("organizations", buildingsGridEntity.getOrganizations()) {

                    @Override
                    protected void populateItem(ListItem<DomainObject> item) {
                        final DomainObject organization = item.getModelObject();

                        Link<Void> organizationLink = new Link<Void>("organizationLink") {

                            @Override
                            public void onClick() {
                                PageParameters params = organizationStrategy.getEditPageParams(organization.getId(), null, null);
                                BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(cityId, streetId));
                                params.put(TemplateStrategy.BACK_INFO_SESSION_KEY, PAGE_SESSION_KEY);
                                setResponsePage(organizationStrategy.getEditPage(), params);
                            }
                        };
                        item.add(organizationLink);
                        organizationLink.add(new Label("organizationCode",
                                organizationStrategy.getUniqueCode(organization)));
                    }
                });
            }
        };
        filterForm.add(buildings);

        //Reset Action
        AjaxLink<Void> reset = new AjaxLink<Void>("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                districtFilterModel.setObject(null);
                streetFilterModel.setObject(null);
                filter.reset(!streetEnabled);
                target.addComponent(content);
            }
        };
        filterForm.add(reset);

        //Submit Action
        AjaxButton submit = new AjaxButton("submit", filterForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(content);
            }
        };
        filterForm.add(submit);

        content.add(new PagingNavigator("navigator", buildings, getPreferencesPage(), content));

        Link<Void> back = new Link<Void>("back") {

            @Override
            public void onClick() {
                setResponsePage(ApartmentCardSearch.class);
            }
        };
        content.add(back);
    }

    private static BackInfo gridBackInfo(long cityId, Long streetId) {
        PageParameters backPageParams = new PageParameters();
        backPageParams.put(CITY_PARAM, cityId);
        if (streetId != null) {
            backPageParams.put(STREET_PARAM, streetId);
        }
        return new BookmarkableBackInfo(BuildingsGrid.class, backPageParams);
    }
}

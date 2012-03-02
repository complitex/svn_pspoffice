/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.list.apartment_card;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import static com.google.common.collect.ImmutableList.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.strategy.IStrategy.SimpleObjectInfo;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.CollapsibleSearchComponent;
import org.complitex.dictionary.web.component.search.CollapsibleSearchPanel;
import org.complitex.dictionary.web.component.search.ISearchCallback;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.web.component.autocomplete.EnhancedAddressSearchComponent;
import org.complitex.pspoffice.person.strategy.web.edit.apartment_card.ApartmentCardEdit;
import org.complitex.pspoffice.person.strategy.web.list.apartment_card.grid.ApartmentsGrid;
import org.complitex.pspoffice.person.strategy.web.list.apartment_card.grid.BuildingsGrid;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.component.toolbar.search.CollapsibleSearchToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.odlabs.wiquery.core.javascript.JsQuery;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class ApartmentCardSearch extends FormTemplatePage {

    private class AddressSearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
            Long apartmentId = ids.get("apartment");
            if (apartmentId != null) {
                target.appendJavascript(String.valueOf(new JsQuery(submit).$().chain("click").render()));
            }
        }
    }
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    private CollapsibleSearchPanel searchPanel;
    private IndicatingAjaxLink<Void> submit;

    public ApartmentCardSearch() {
        add(new Label("title", new ResourceModel("title")));
        add(new Label("address_label", new ResourceModel("address_label")));

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        final SearchComponentState addressSearchComponentState = apartmentCardStrategy.restoreSearchState(getTemplateSession());
        WebMarkupContainer searchPanelContainer = new WebMarkupContainer("searchPanelContainer");
        searchPanelContainer.setOutputMarkupId(true);
        add(searchPanelContainer);
        final IModel<ShowMode> showmodeModel = new Model<ShowMode>(ShowMode.ACTIVE);
        searchPanel = new CollapsibleSearchPanel("searchPanel", addressSearchComponentState,
                of("country", "region", "city", "street", "building", "apartment"), new AddressSearchCallback(),
                ShowMode.ACTIVE, true, showmodeModel) {

            @EJB
            private SessionBean sessionBean;

            @Override
            protected CollapsibleSearchComponent newSearchComponent(String id, SearchComponentState searchComponentState,
                    List<String> searchFilters, ISearchCallback callback, ShowMode showMode, boolean enabled) {
                return new EnhancedAddressSearchComponent(id, searchComponentState, searchFilters, callback, showMode,
                        enabled, sessionBean.getUserOrganizationObjectIds());
            }
        };
        searchPanelContainer.add(searchPanel);

        submit = new IndicatingAjaxLink<Void>("search") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                final DomainObject city = addressSearchComponentState.get("city");

                if (city == null || city.getId() == null || city.getId() <= 0) {
                    // город не введен -> ошибка, это обязательное поле.
                    error(getString("address_invalid"));
                    target.addComponent(messages);
                    return;
                }
                //город введен.
//                storeAdditionalInfo(addressSearchComponentState);

                final DomainObject street = addressSearchComponentState.get("street");
                if (street == null || street.getId() == null || street.getId() <= 0) {
                    //улица не введена -> выводим грид домов.
                    PageParameters parameters = new PageParameters();
                    parameters.put(BuildingsGrid.CITY_PARAM, city.getId());
                    setResponsePage(BuildingsGrid.class, parameters);
                    return;
                }
                //улица введена.

                final DomainObject building = addressSearchComponentState.get("building");
                if (building == null || building.getId() == null || building.getId() <= 0) {
                    //дом не введен -> выводим грид домов для заданной улицы.
                    PageParameters parameters = new PageParameters();
                    parameters.put(BuildingsGrid.CITY_PARAM, city.getId());
                    parameters.put(BuildingsGrid.STREET_PARAM, street.getId());
                    setResponsePage(BuildingsGrid.class, parameters);
                    return;
                }
                //дом введен.
                
                final DomainObject apartment = addressSearchComponentState.get("apartment");
                if (apartment == null || apartment.getId() == null || apartment.getId() <= 0) {
                    //квартира не введена -> выводим грид квартир для заданного дома.
                    PageParameters parameters = new PageParameters();
                    parameters.put(ApartmentsGrid.BUILDING_PARAM, building.getId());
                    setResponsePage(ApartmentsGrid.class, parameters);
                    return;
                }
                //квартира введена.

                SimpleObjectInfo addressInfo = getAddressObjectInfo(addressSearchComponentState);
                if (addressInfo != null) {
                    storeAdditionalInfo(addressSearchComponentState);

                    String addressEntity = addressInfo.getEntityTable();
                    long addressId = addressInfo.getId();
                    int count = apartmentCardStrategy.countByAddress(addressEntity, addressId);
                    if (count == 1) {
                        ApartmentCard apartmentCard = apartmentCardStrategy.findOneByAddress(addressEntity, addressId);
                        setResponsePage(new ApartmentCardEdit(apartmentCard));
                    } else if (count > 1) {
                        setResponsePage(new ApartmentCardList(addressEntity, addressId));
                    } else {
                        setResponsePage(new ApartmentCardNotFound(addressEntity, addressId));
                    }
                } else {
                    target.addComponent(messages);
                }
            }
        };
        add(submit);
    }

    private void storeAdditionalInfo(SearchComponentState addressSearchComponentState) {
        apartmentCardStrategy.storeSearchState(getTemplateSession(), addressSearchComponentState);
    }

    private SimpleObjectInfo getAddressObjectInfo(SearchComponentState addressSearchComponentState) {
        DomainObject building = addressSearchComponentState.get("building");
        if (building == null || building.getId() == null || building.getId() <= 0) {
            error(getString("address_invalid"));
            return null;
        }
        DomainObject apartment = addressSearchComponentState.get("apartment");
        if (apartment == null || apartment.getId() == null || apartment.getId() <= 0) {
            return new SimpleObjectInfo("building", building.getId());
        }
        return new SimpleObjectInfo("apartment", apartment.getId());
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return of(new CollapsibleSearchToolbarButton(id, searchPanel));
    }
}

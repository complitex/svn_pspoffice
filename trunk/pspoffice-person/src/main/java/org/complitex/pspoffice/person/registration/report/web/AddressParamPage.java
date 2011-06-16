/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.web;

import static com.google.common.collect.Lists.*;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.IStrategy.SimpleObjectInfo;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.SearchComponent;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.pspoffice.person.registration.report.service.CommunalApartmentService;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public abstract class AddressParamPage extends FormTemplatePage {

    @EJB
    private CommunalApartmentService communalApartmentService;

    public AddressParamPage() {
        add(new Label("title", new ResourceModel("title")));
        add(new Label("address_label", new ResourceModel("address_label")));
        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        final SearchComponentState addressComponentState = new SearchComponentState();
        List<String> searchFilters = newArrayList("city", "street", "building", "apartment");
        if (!isSeparateApartment()) {
            searchFilters.add("room");
        }
        SearchComponent searchComponent = new SearchComponent("searchComponent", addressComponentState, searchFilters,
                null, ShowMode.ACTIVE, true);
        add(searchComponent);

        AjaxLink<Void> submit = new AjaxLink<Void>("submit") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                SimpleObjectInfo addressInfo = getAddressObjectInfo(addressComponentState);
                if (addressInfo != null && isSeparateApartment()) {
                    if ("apartment".equals(addressInfo.getEntityTable())) {
                        if (communalApartmentService.isCommunalApartment(addressInfo.getId())) {
                            error(getString("separate_apartment_required"));
                        }
                    } else {
                        error(getString("separate_apartment_required"));
                    }
                }
                if (addressInfo != null && getSession().getFeedbackMessages().isEmpty()) {
                    toReferencePage(addressInfo.getEntityTable(), addressInfo.getId());
                }
                target.addComponent(messages);
            }
        };
        add(submit);
    }

    protected boolean isSeparateApartment() {
        return false;
    }

    protected abstract void toReferencePage(String addressEntity, long addressId);

    protected final SimpleObjectInfo getAddressObjectInfo(SearchComponentState addressComponentState) {
        DomainObject building = addressComponentState.get("building");
        if (building == null || building.getId() == null || building.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            error(getString("address_invalid"));
            return null;
        }
        DomainObject apartment = addressComponentState.get("apartment");
        if (apartment == null || apartment.getId() == null || apartment.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            return new SimpleObjectInfo("building", building.getId());
        }
        DomainObject room = addressComponentState.get("room");
        if (room == null || room.getId() == null || room.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            return new SimpleObjectInfo("apartment", apartment.getId());
        }
        return new SimpleObjectInfo("room", room.getId());
    }
}


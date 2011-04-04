/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.web;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.IStrategy.RestrictedObjectInfo;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.SearchComponent;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.template.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
public final class RegistrationReportParamsPage extends FormTemplatePage {

    static final String ADDRESS_ID = "address_id";
    static final String ADDRESS_ENTITY = "address_entity";

    public RegistrationReportParamsPage() {
        init();
    }

    private void init() {
        add(new Label("title", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return getString("title");
            }
        }));
        add(new Label("address_label", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return getString("address_label");
            }
        }));
        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        final SearchComponentState addressComponentState = new SearchComponentState();
        SearchComponent searchComponent = new SearchComponent("searchComponent", addressComponentState,
                ImmutableList.of("city", "street", "building", "apartment", "room"), null, ShowMode.ALL, true);
        add(searchComponent);

        AjaxLink<Void> report = new AjaxLink<Void>("report") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                RestrictedObjectInfo addressInfo = getAddressObjectInfo(addressComponentState);
                if (addressInfo != null) {
                    PageParameters params = new PageParameters();
                    params.add(ADDRESS_ID, String.valueOf(addressInfo.getId()));
                    params.add(ADDRESS_ENTITY, addressInfo.getEntityTable());
                    setResponsePage(RegistrationReportList.class, params);
                }

                target.addComponent(messages);
            }
        };
        add(report);
    }

    private RestrictedObjectInfo getAddressObjectInfo(SearchComponentState addressComponentState) {
        DomainObject building = addressComponentState.get("building");
        if (building == null || building.getId() == null || building.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            error(getString("address_invalid"));
            return null;
        }
        DomainObject apartment = addressComponentState.get("apartment");
        if (apartment == null || apartment.getId() == null || apartment.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            return new RestrictedObjectInfo("building", building.getId());
        }
        DomainObject room = addressComponentState.get("room");
        if (room == null || room.getId() == null || room.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            return new RestrictedObjectInfo("apartment", apartment.getId());
        }
        return new RestrictedObjectInfo("room", room.getId());
    }
}


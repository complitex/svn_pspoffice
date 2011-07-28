/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.list;

import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import static com.google.common.collect.ImmutableList.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.IStrategy.SimpleObjectInfo;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.dictionary.web.component.search.WiQuerySearchComponent;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.web.edit.ApartmentCardEdit;
import org.complitex.template.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
public class ApartmentCardSearch extends FormTemplatePage {

    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;

    public ApartmentCardSearch() {
        add(new Label("title", new ResourceModel("title")));
        add(new Label("address_label", new ResourceModel("address_label")));
        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        final SearchComponentState addressSearchComponentState = new SearchComponentState();
        WiQuerySearchComponent addressSearchComponent = new WiQuerySearchComponent("addressSearchComponent",
                addressSearchComponentState, of("city", "street", "building", "apartment"), null, ShowMode.ACTIVE, true);
        add(addressSearchComponent);

        AjaxLink<Void> submit = new AjaxLink<Void>("search") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                SimpleObjectInfo addressInfo = getAddressObjectInfo(addressSearchComponentState);
                if (addressInfo != null) {
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

    protected final SimpleObjectInfo getAddressObjectInfo(SearchComponentState addressComponentState) {
        DomainObject building = addressComponentState.get("building");
        if (building == null || building.getId() == null || building.getId() <= 0) {
            error(getString("address_invalid"));
            return null;
        }
        DomainObject apartment = addressComponentState.get("apartment");
        if (apartment == null || apartment.getId() == null || apartment.getId() <= 0) {
            return new SimpleObjectInfo("building", building.getId());
        }
        return new SimpleObjectInfo("apartment", apartment.getId());
    }
}


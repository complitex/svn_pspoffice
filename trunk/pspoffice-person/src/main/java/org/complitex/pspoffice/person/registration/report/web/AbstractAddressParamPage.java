/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.web;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.IStrategy.SimpleObjectInfo;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.SearchComponent;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public abstract class AbstractAddressParamPage extends FormTemplatePage {

    public static final String ADDRESS_ID = "address_id";
    public static final String ADDRESS_ENTITY = "address_entity";
    @EJB
    private StrategyFactory strategyFactory;

    public AbstractAddressParamPage() {
        init();
    }

    private void init() {
        add(new Label("title", new ResourceModel("title")));
        add(new Label("address_label", new ResourceModel("address_label")));
        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        final SearchComponentState addressComponentState = new SearchComponentState();
        SearchComponent searchComponent = new SearchComponent("searchComponent", addressComponentState,
                ImmutableList.of("city", "street", "building", "apartment"), null, ShowMode.ACTIVE, true);
        add(searchComponent);

        AjaxLink<Void> report = new AjaxLink<Void>("submit") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                SimpleObjectInfo addressInfo = getAddressObjectInfo(addressComponentState);
                if (addressInfo != null) {
                    if (addressInfo.getEntityTable().equals("apartment")) {
                        boolean isCommunalApartment = isCommunalApartment(addressInfo.getId());
                        if (forCommunalApartments() && !isCommunalApartment) {
                            error(getString("communal_apartment"));
                        } else if (!forCommunalApartments() && isCommunalApartment) {
                            error(getString("not_communal_apartment"));
                        } else {
                            toReferencePage(addressInfo.getEntityTable(), addressInfo.getId());
                        }
                    }
                }
                target.addComponent(messages);
            }
        };
        add(report);
    }

    protected abstract void toReferencePage(String addressEntity, long addressId);

    protected abstract boolean forCommunalApartments();

    protected final SimpleObjectInfo getAddressObjectInfo(SearchComponentState addressComponentState) {
        DomainObject building = addressComponentState.get("building");
        if (building == null || building.getId() == null || building.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            error(getString("address_invalid"));
            return null;
        }
        DomainObject apartment = addressComponentState.get("apartment");
        if (apartment == null || apartment.getId() == null || apartment.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            //TODO: add building selection
            error(getString("address_invalid"));
            return null;
//            return new SimpleObjectInfo("building", building.getId());
        }
        return new SimpleObjectInfo("apartment", apartment.getId());
    }

    protected boolean isCommunalApartment(long apartmentId) {
        IStrategy roomStrategy = strategyFactory.getStrategy("room");
        DomainObjectExample example = new DomainObjectExample();
        example.setAdmin(true);
        example.setParentEntity("apartment");
        example.setStatus(ShowMode.ACTIVE.name());
        example.setParentId(apartmentId);
        List<? extends DomainObject> rooms = roomStrategy.find(example);
        return rooms != null && !rooms.isEmpty();
    }
}


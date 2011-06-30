/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.web;

import static com.google.common.collect.ImmutableList.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.IStrategy.SimpleObjectInfo;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.ISearchCallback;
import org.complitex.dictionary.web.component.search.SearchComponent;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.pspoffice.person.registration.report.entity.NeighbourFamily;
import org.complitex.pspoffice.person.registration.report.service.CommunalApartmentService;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public abstract class CommunalAddressParamPage extends FormTemplatePage {

    @EJB
    private CommunalApartmentService communalApartmentService;
    private final IModel<List<? extends NeighbourFamily>> familiesModel;
    private final WebMarkupContainer familiesContainer;
    private final FeedbackPanel messages;
    private final IModel<NeighbourFamily> selectedNeighbourFamilyModel = new Model<NeighbourFamily>();

    private class CommunalAddressCallback implements ISearchCallback, Serializable {

        @Override
        public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
            Long apartmentId = ids.get("apartment");
            if (apartmentId != null && apartmentId > 0) {
                if (communalApartmentService.isCommunalApartment(apartmentId)) {
                    List<NeighbourFamily> families = communalApartmentService.findAllNeighbourFamilies(apartmentId, getLocale());
                    familiesModel.setObject(families);
                    if (families != null && !families.isEmpty()) {
                        familiesContainer.setVisible(true);
                    } else {
                        familiesContainer.setVisible(false);
                        info(getString("emptyFamiliesLabel"));
                    }
                } else {
                    familiesContainer.setVisible(false);
                    error(getString("communal_apartment"));
                }
            } else {
                familiesContainer.setVisible(false);
                error(getString("address_invalid"));
            }
            selectedNeighbourFamilyModel.setObject(null);
            target.addComponent(familiesContainer);
            target.addComponent(messages);
        }
    }

    public CommunalAddressParamPage() {
        add(new Label("title", new ResourceModel("title")));
        add(new Label("address_label", new ResourceModel("address_label")));
        messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        SearchComponentState addressComponentState = new SearchComponentState();
        SearchComponent searchComponent = new SearchComponent("searchComponent", addressComponentState,
                of("city", "street", "building", "apartment"), new CommunalAddressCallback(), ShowMode.ACTIVE, true);
        add(searchComponent);

        familiesModel = Model.ofList(null);
        familiesContainer = new WebMarkupContainer("familiesContainer");
        familiesContainer.setOutputMarkupPlaceholderTag(true);
        familiesContainer.setVisible(false);
        add(familiesContainer);
        Form form = new Form("form");
        familiesContainer.add(form);

        final RadioGroup radioGroup = new RadioGroup("radioGroup", selectedNeighbourFamilyModel);
        radioGroup.setRequired(true);
        form.add(radioGroup);
        ListView<NeighbourFamily> families = new ListView<NeighbourFamily>("families", familiesModel) {

            @Override
            protected void populateItem(ListItem<NeighbourFamily> item) {
                NeighbourFamily family = item.getModelObject();
                item.add(new Radio<NeighbourFamily>("radio", item.getModel(), radioGroup));
                item.add(new Label("roomNumber", family.getRoomNumber()));
                item.add(new Label("name", family.getName()));
            }
        };
        radioGroup.add(families);
        AjaxButton submit = new AjaxButton("submit", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                toReferencePage((List<NeighbourFamily>) familiesModel.getObject(), selectedNeighbourFamilyModel.getObject());
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(messages);
            }
        };
        form.add(submit);
    }

    protected abstract void toReferencePage(List<NeighbourFamily> neighbourFamilies, NeighbourFamily selectedNeighbourFamily);

    protected final SimpleObjectInfo getAddressObjectInfo(SearchComponentState addressComponentState) {
        DomainObject building = addressComponentState.get("building");
        if (building == null || building.getId() == null || building.getId().equals(SearchComponentState.NOT_SPECIFIED_ID)) {
            error(getString("address_invalid"));
            return null;
        }
        DomainObject apartment = addressComponentState.get("apartment");
        if (apartment == null || apartment.getId() == null || apartment.getId().equals(SearchComponentState.NOT_SPECIFIED_ID)) {
            //TODO: add building selection
            error(getString("address_invalid"));
            return null;
//            return new SimpleObjectInfo("building", building.getId());
        }
        return new SimpleObjectInfo("apartment", apartment.getId());
    }
}


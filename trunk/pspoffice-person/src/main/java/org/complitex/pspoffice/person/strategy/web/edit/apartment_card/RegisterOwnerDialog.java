/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.apartment_card;

import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.dictionary.web.component.dateinput.MaskedDateInput;
import org.complitex.dictionary.web.component.fieldset.CollapsibleFieldset;
import org.complitex.dictionary.web.component.scroll.ScrollToElementUtil;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.RegisterOwnerCard;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
final class RegisterOwnerDialog extends Panel {

    private static final Logger log = LoggerFactory.getLogger(RegisterOwnerDialog.class);
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;
    private IModel<RegisterOwnerCard> model;
    private Dialog dialog;
    private Form<RegisterOwnerCard> form;
    private FeedbackPanel messages;
    private ApartmentCard apartmentCard;
    private WebMarkupContainer registerChildrenContainer;

    RegisterOwnerDialog(String id) {
        super(id);
        init();
    }

    private void init() {
        dialog = new Dialog("dialog");
        dialog.setModal(true);
        dialog.setWidth(650);
        dialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        dialog.setCloseOnEscape(false);
        add(dialog);

        final Label label = new Label("label", new ResourceModel("title"));
        label.setOutputMarkupId(true);
        dialog.add(label);

        messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        dialog.add(messages);

        model = new CompoundPropertyModel<RegisterOwnerCard>(new RegisterOwnerCard());
        form = new Form<RegisterOwnerCard>("form", model);
        form.setOutputMarkupId(true);
        dialog.add(form);

        MaskedDateInput registrationDate = new MaskedDateInput("registrationDate");
        registrationDate.setRequired(true);
        form.add(registrationDate);

        final List<DomainObject> allRegistrationTypes = registrationTypeStrategy.getAll();

        DisableAwareDropDownChoice<DomainObject> registrationType = new DisableAwareDropDownChoice<DomainObject>("registrationType",
                allRegistrationTypes, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return registrationTypeStrategy.displayDomainObject(object, getLocale());
            }
        });
        registrationType.setRequired(true);
        form.add(registrationType);

        registerChildrenContainer = new WebMarkupContainer("registerChildrenContainer");
        form.add(registerChildrenContainer);
        registerChildrenContainer.add(new CheckBox("registerChildren"));

        CollapsibleFieldset addressFieldset = new CollapsibleFieldset("addressFieldset", new ResourceModel("address"));
        form.add(addressFieldset);
        addressFieldset.add(new TextField<String>("country"));
        addressFieldset.add(new TextField<String>("region"));
        addressFieldset.add(new TextField<String>("district"));
        addressFieldset.add(new TextField<String>("city"));
        addressFieldset.add(new TextField<String>("street"));
        addressFieldset.add(new TextField<String>("buildingNumber"));
        addressFieldset.add(new TextField<String>("buildingCorp"));
        addressFieldset.add(new TextField<String>("apartment"));
        addressFieldset.add(new MaskedDateInput("arrivalDate"));

        IndicatingAjaxButton register = new IndicatingAjaxButton("register") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    register();
                    setResponsePage(new ApartmentCardEdit(apartmentCard.getId()));
                } catch (Exception e) {
                    log.error("", e);
                    error(getString("db_error"));
                    target.addComponent(messages);
                    target.appendJavascript(ScrollToElementUtil.scrollTo(label.getMarkupId()));
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(messages);
                target.appendJavascript(ScrollToElementUtil.scrollTo(label.getMarkupId()));
            }
        };
        form.add(register);
    }

    void open(AjaxRequestTarget target, ApartmentCard apartmentCard) {
        this.apartmentCard = apartmentCard;
        model.setObject(new RegisterOwnerCard());
        List<Attribute> childrentAttributes = apartmentCard.getOwner().getAttributes(PersonStrategy.CHILDREN);
        registerChildrenContainer.setVisible(childrentAttributes != null && !childrentAttributes.isEmpty());
        target.addComponent(form);
        target.addComponent(messages);
        dialog.open(target);
    }

    private void register() {
        apartmentCardStrategy.registerOwner(apartmentCard, model.getObject(), apartmentCard.getOwner());
    }
}

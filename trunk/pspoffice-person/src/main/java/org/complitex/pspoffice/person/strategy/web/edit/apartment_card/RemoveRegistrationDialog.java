/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.apartment_card;

import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.dictionary.web.component.DatePicker;
import org.complitex.dictionary.web.component.fieldset.CollapsibleFieldset;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.person.strategy.entity.RemoveRegistrationCard;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
final class RemoveRegistrationDialog extends Panel {

    private static final Logger log = LoggerFactory.getLogger(RemoveRegistrationDialog.class);
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    private IModel<RemoveRegistrationCard> model;
    private Dialog dialog;
    private Form<RemoveRegistrationCard> form;
    private FeedbackPanel messages;
    private long apartmentCardId;
    private List<Registration> registrationsToRemove;

    RemoveRegistrationDialog(String id) {
        super(id);
        init();
    }

    void init() {
        dialog = new Dialog("dialog");
        dialog.setModal(true);
        dialog.setWidth(650);
        dialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        dialog.setCloseOnEscape(false);
        add(dialog);

        messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        dialog.add(messages);

        model = new CompoundPropertyModel<RemoveRegistrationCard>(new RemoveRegistrationCard());
        form = new Form<RemoveRegistrationCard>("form", model);
        form.setOutputMarkupId(true);
        dialog.add(form);

        TextField<String> reason = new TextField<String>("reason");
        reason.setRequired(true);
        form.add(reason);

        DatePicker<Date> date = new DatePicker<Date>("date");
        date.setRequired(true);
        form.add(date);

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

        AjaxSubmitLink removeFromRegistration = new AjaxSubmitLink("removeFromRegistration") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    removeRegistrations();
                    setResponsePage(new ApartmentCardEdit(apartmentCardId));
                } catch (Exception e) {
                    log.error("", e);
                    error(getString("db_error"));
                    target.addComponent(messages);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(messages);
            }
        };
        form.add(removeFromRegistration);

        AjaxLink<Void> cancel = new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                closeDialog(target);
            }
        };
        form.add(cancel);
    }

    void closeDialog(AjaxRequestTarget target) {
        dialog.close(target);
    }

    void open(AjaxRequestTarget target, long apartmentCardId, List<Registration> registrationsToRemove) {
        this.apartmentCardId = apartmentCardId;
        this.registrationsToRemove = registrationsToRemove;
        model.setObject(new RemoveRegistrationCard());
        target.addComponent(form);
        target.addComponent(messages);
        dialog.open(target);
    }

    void removeRegistrations() {
        apartmentCardStrategy.removeRegistrations(apartmentCardId, registrationsToRemove, model.getObject());
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.apartment_card;

import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.entity.Registration;
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
final class ChangeRegistrationTypeDialog extends Panel {

    private static final Logger log = LoggerFactory.getLogger(ChangeRegistrationTypeDialog.class);
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;
    private IModel<DomainObject> registrationTypeModel;
    private Dialog dialog;
    private FeedbackPanel messages;
    private List<Registration> registrationsToChangeType;
    private long apartmentCardId;

    ChangeRegistrationTypeDialog(String id) {
        super(id);
        init();
    }

    private void init() {
        dialog = new Dialog("dialog");
        dialog.setModal(true);
        dialog.setWidth(350);
        dialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        dialog.setCloseOnEscape(false);
        add(dialog);

        messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        dialog.add(messages);

        final List<DomainObject> allRegistrationTypes = registrationTypeStrategy.getAll();
        registrationTypeModel = new Model<DomainObject>(allRegistrationTypes.get(0));

        DisableAwareDropDownChoice<DomainObject> registrationType = new DisableAwareDropDownChoice<DomainObject>("registrationType",
                registrationTypeModel, allRegistrationTypes, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return registrationTypeStrategy.displayDomainObject(object, getLocale());
            }
        });
        registrationType.setRequired(true);
        registrationType.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        dialog.add(registrationType);

        AjaxLink<Void> submit = new AjaxLink<Void>("submit") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    changeRegistrationType();
                    setResponsePage(new ApartmentCardEdit(apartmentCardId));
                } catch (Exception e) {
                    log.error("", e);
                    error(getString("db_error"));
                    target.addComponent(messages);
                }
            }
        };
        dialog.add(submit);

        AjaxLink<Void> cancel = new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.close(target);
            }
        };
        dialog.add(cancel);
    }

    void open(AjaxRequestTarget target, long apartmentCardId, List<Registration> registrationsToChangeType) {
        this.apartmentCardId = apartmentCardId;
        this.registrationsToChangeType = registrationsToChangeType;
        target.addComponent(messages);
        dialog.open(target);
    }

    private void changeRegistrationType() {
        apartmentCardStrategy.changeRegistrationType(apartmentCardId, registrationsToChangeType, registrationTypeModel.getObject().getId());
    }
}

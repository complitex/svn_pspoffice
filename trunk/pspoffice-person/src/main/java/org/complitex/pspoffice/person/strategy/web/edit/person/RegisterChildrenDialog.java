/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.person;

import com.google.common.collect.Lists;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.dictionary.web.component.dateinput.MaskedDateInput;
import org.complitex.dictionary.web.component.scroll.ScrollToElementUtil;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy.PersonApartmentCardAddress;
import org.complitex.pspoffice.person.strategy.entity.RegisterChildrenCard;
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
abstract class RegisterChildrenDialog extends Panel {

    private static final Logger log = LoggerFactory.getLogger(RegisterChildrenDialog.class);
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    private IModel<RegisterChildrenCard> model;
    private Dialog dialog;
    private Form<RegisterChildrenCard> form;
    private FeedbackPanel messages;
    private List<PersonApartmentCardAddress> personApartmentCardAddresses = Lists.newArrayList();
    private IModel<PersonApartmentCardAddress> personApartmentCardAddressModel;
    private List<Long> childrenIds;

    RegisterChildrenDialog(String id) {
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

        model = new CompoundPropertyModel<RegisterChildrenCard>(new RegisterChildrenCard());
        form = new Form<RegisterChildrenCard>("form", model);
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

        IChoiceRenderer<PersonApartmentCardAddress> apartmentCardAddressesRenderer =
                new IChoiceRenderer<PersonApartmentCardAddress>() {

                    @Override
                    public String getDisplayValue(PersonApartmentCardAddress apartmentCardAddress) {
                        return addressRendererBean.displayAddress(apartmentCardAddress.getAddressEntity(),
                                apartmentCardAddress.getAddressId(), getLocale());
                    }

                    @Override
                    public String getIdValue(PersonApartmentCardAddress apartmentCardAddress, int index) {
                        return String.valueOf(apartmentCardAddress.getApartmentCardId());
                    }
                };
        personApartmentCardAddressModel = new Model<PersonApartmentCardAddress>() {

            @Override
            public void setObject(PersonApartmentCardAddress personApartmentCardAddress) {
                super.setObject(personApartmentCardAddress);
                model.getObject().setApartmentCardId(personApartmentCardAddress.getApartmentCardId());
            }
        };
        final IModel<List<PersonApartmentCardAddress>> personApartmentCardAddressesModel =
                new AbstractReadOnlyModel<List<PersonApartmentCardAddress>>() {

                    @Override
                    public List<PersonApartmentCardAddress> getObject() {
                        return personApartmentCardAddresses;
                    }
                };
        RadioChoice<PersonApartmentCardAddress> apartmentCardAddresses =
                new RadioChoice<PersonApartmentCardAddress>("apartmentCardAddresses", personApartmentCardAddressModel,
                personApartmentCardAddressesModel, apartmentCardAddressesRenderer);
        apartmentCardAddresses.setRequired(true);
        form.add(apartmentCardAddresses);

        IndicatingAjaxButton register = new IndicatingAjaxButton("register") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    register();
                    close(target);
                } catch (Exception e) {
                    log.error("", e);
                    error(getString("db_error"));
                    target.addComponent(messages);
                    scrollToMessages(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.addComponent(messages);
                scrollToMessages(target);
            }

            private void scrollToMessages(AjaxRequestTarget target) {
                target.appendJavascript(ScrollToElementUtil.scrollTo(label.getMarkupId()));
            }
        };
        form.add(register);
        form.add(new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                close(target);
            }
        });
    }

    void open(AjaxRequestTarget target, List<PersonApartmentCardAddress> personApartmentCardAddresses, List<Long> newChildrenIds) {
        this.childrenIds = newChildrenIds;
        this.personApartmentCardAddresses = personApartmentCardAddresses;
        model.setObject(new RegisterChildrenCard());
        if (personApartmentCardAddresses.size() == 1) {
            personApartmentCardAddressModel.setObject(personApartmentCardAddresses.get(0));
        }
        target.addComponent(form);
        target.addComponent(messages);
        dialog.open(target);
    }

    private void close(AjaxRequestTarget target) {
        dialog.close(target);
        onClose(target);
    }

    protected abstract void onClose(AjaxRequestTarget target);

    private void register() {
        apartmentCardStrategy.registerChildren(model.getObject(), childrenIds);
    }
}

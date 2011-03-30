/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit;

import com.google.common.collect.Lists;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.web.AbstractComplexAttributesPanel;
import org.complitex.dictionary.strategy.web.DomainObjectAccessUtil;
import org.complitex.dictionary.strategy.web.DomainObjectEditPanel;
import org.complitex.dictionary.web.component.DomainObjectInputPanel;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.web.edit.validate.RegistrationValidator;

/**
 *
 * @author Artem
 */
public final class PersonRegistrationEditComponent extends AbstractComplexAttributesPanel {

    @EJB
    private StringCultureBean stringBean;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private RegistrationStrategy registrationStrategy;
    private DomainObjectEditPanel editPanel;

    public PersonRegistrationEditComponent(String id, boolean disabled) {
        super(id, disabled);
    }

    @Override
    protected void init() {
        Label registrationLabel = new Label("registrationLabel", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                EntityAttributeType attributeType = personStrategy.getEntity().getAttributeType(PersonStrategy.REGISTRATION);
                return stringBean.displayValue(attributeType.getAttributeNames(), getLocale());
            }
        });
        add(registrationLabel);
        final Person person = (Person) getDomainObject();

        final WebMarkupContainer registrationInputPanelContainer = new WebMarkupContainer("registrationInputPanelContainer");
        registrationInputPanelContainer.setOutputMarkupId(true);
        add(registrationInputPanelContainer);

        final DomainObjectInputPanel registrationInputPanel = new DomainObjectInputPanel("registrationInputPanel",
                person.getRegistration(), "registration", null, null, getInputPanel().getDate());
        registrationInputPanelContainer.add(registrationInputPanel);

        AjaxSubmitLink changeRegistration = new AjaxSubmitLink("changeRegistration") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (validateRegistration(person)) {
                    setVisible(false);
                    prepareForChangeRegistration(person);
                    registrationInputPanel.replaceWith(new DomainObjectInputPanel("registrationInputPanel",
                            person.getNewRegistration(), "registration", null, null, getInputPanel().getDate()));

                    target.addComponent(registrationInputPanelContainer);
                    target.addComponent(this);
                }
                editPanel.updateMessages(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                editPanel.updateMessages(target);
            }
        };
        changeRegistration.setOutputMarkupPlaceholderTag(true);
        changeRegistration.setVisible((person.getId() != null) && !isDisabled() && DomainObjectAccessUtil.canEdit(null, "person", person));
        add(changeRegistration);
        initEditPanel();
    }

    private void prepareForChangeRegistration(Person person) {
        DomainObject newRegistration = registrationStrategy.newInstance();
        person.setNewRegistration(newRegistration);

        RegistrationEditComponent regEditComponent = getRegistrationEditComponent();
        regEditComponent.onUpdate();

        DomainObject oldRegistration = person.getRegistration();
        Attribute oldDepartureAttribute = oldRegistration.getAttribute(RegistrationStrategy.DEPARTURE);
        Attribute newArrivalAttribute = person.getNewRegistration().getAttribute(RegistrationStrategy.ARRIVAL);
        long oldDepartureValueTypeId = oldDepartureAttribute.getValueTypeId();
        if (oldDepartureValueTypeId == RegistrationStrategy.DEPARTURE_STRING) {
            newArrivalAttribute.setValueTypeId(RegistrationStrategy.ARRIVAL_STRING);
            List<StringCulture> localizedValues = Lists.newArrayList();
            for (StringCulture sc : oldDepartureAttribute.getLocalizedValues()) {
                localizedValues.add(new StringCulture(sc.getLocaleId(), sc.getValue()));
            }
            newArrivalAttribute.setLocalizedValues(localizedValues);
        } else {
            newArrivalAttribute.setValueId(oldDepartureAttribute.getValueId());

            if (oldDepartureValueTypeId == RegistrationStrategy.DEPARTURE_APARTMENT) {
                newArrivalAttribute.setValueTypeId(RegistrationStrategy.ARRIVAL_APARTMENT);
            } else if (oldDepartureValueTypeId == RegistrationStrategy.DEPARTURE_BUILDING) {
                newArrivalAttribute.setValueTypeId(RegistrationStrategy.ARRIVAL_BUILDING);
            } else if (oldDepartureValueTypeId == RegistrationStrategy.DEPARTURE_ROOM) {
                newArrivalAttribute.setValueTypeId(RegistrationStrategy.ARRIVAL_ROOM);
            }
        }
    }
    private RegistrationEditComponent registrationEditComponent;

    private RegistrationEditComponent getRegistrationEditComponent() {
        if (registrationEditComponent == null) {
            this.visitChildren(RegistrationEditComponent.class, new Component.IVisitor<RegistrationEditComponent>() {

                @Override
                public Object component(RegistrationEditComponent comp) {
                    registrationEditComponent = comp;
                    return STOP_TRAVERSAL;
                }
            });
        }
        return registrationEditComponent;
    }

    private boolean validateRegistration(Person person) {
        DomainObject registration = person.getRegistration();
        RegistrationValidator registrationValidator = registrationStrategy.getValidator();
        return registrationValidator.validate(registration, editPanel)
                && registrationValidator.validateDepartureAddress(registration, editPanel);
    }

    private void initEditPanel() {
        editPanel = this.findParent(DomainObjectEditPanel.class);
    }
}

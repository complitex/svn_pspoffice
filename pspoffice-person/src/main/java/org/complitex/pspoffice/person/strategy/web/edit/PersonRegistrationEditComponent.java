/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit;

import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.web.AbstractComplexAttributesPanel;
import org.complitex.dictionary.strategy.web.DomainObjectAccessUtil;
import org.complitex.dictionary.web.component.DomainObjectInputPanel;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;

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
        final Panel newRegistrationInputPanel = new EmptyPanel("newRegistrationInputPanel");
        registrationInputPanelContainer.add(newRegistrationInputPanel);

        AjaxLink<Void> changeRegistration = new AjaxLink<Void>("changeRegistration") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setVisible(false);
                prepareForChangeRegistration(person);
                registrationInputPanel.setVisible(false);
                newRegistrationInputPanel.replaceWith(new DomainObjectInputPanel("newRegistrationInputPanel",
                        person.getNewRegistration(), "registration", null, null, getInputPanel().getDate()));

                target.addComponent(registrationInputPanelContainer);
                target.addComponent(this);
            }
        };
        changeRegistration.setOutputMarkupPlaceholderTag(true);
        changeRegistration.setVisible((person.getId() != null) && !isDisabled() && DomainObjectAccessUtil.canEdit(null, "person", person));
        add(changeRegistration);
    }

    private void prepareForChangeRegistration(Person person) {
        DomainObject newRegistration = registrationStrategy.newInstance();
        person.setNewRegistration(newRegistration);
    }
}

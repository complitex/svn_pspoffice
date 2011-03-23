/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit;

import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.web.AbstractComplexAttributesPanel;
import org.complitex.dictionary.web.component.DomainObjectInputPanel;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
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
        add(new DomainObjectInputPanel("registrationInputPanel", person.getRegistration(), "registration", null, null, getInputPanel().getDate()));
    }
}

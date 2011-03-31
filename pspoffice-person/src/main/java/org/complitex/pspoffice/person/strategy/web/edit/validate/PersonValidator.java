/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.validate;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.strategy.web.DomainObjectEditPanel;
import org.complitex.dictionary.strategy.web.validate.IValidator;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.pspoffice.person.strategy.entity.Person;

/**
 *
 * @author Artem
 */
public class PersonValidator implements IValidator {

    @Override
    public boolean validate(DomainObject object, DomainObjectEditPanel editPanel) {
        Person person = (Person) object;
        DomainObject registration = person.getRegistration();
        boolean isRegistrationValid = person.isRegistrationValidated() || getRegistrationValidator().validate(registration, editPanel);
        DomainObject newRegistration = person.getNewRegistration();
        boolean isNewRegistrationValid = true;
        if (newRegistration != null) {
            isNewRegistrationValid = getRegistrationValidator().validate(newRegistration, editPanel);
        }
        return isRegistrationValid && isNewRegistrationValid;
    }

    private IValidator getRegistrationValidator() {
        return EjbBeanLocator.getBean(StrategyFactory.class).getStrategy("registration").getValidator();
    }
}

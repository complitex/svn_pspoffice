/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.pspoffice.person.strategy.PersonStrategy;

/**
 *
 * @author Artem
 */
public class Person extends DomainObject {

    private String lastName;
    private String firstName;
    private String middleName;
    private DomainObject registration;
    private DomainObject newRegistration;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public DomainObject getRegistration() {
        return registration;
    }

    public void setRegistration(DomainObject registration) {
        this.registration = registration;
    }

    public DomainObject getNewRegistration() {
        return newRegistration;
    }

    public void setNewRegistration(DomainObject newRegistration) {
        this.newRegistration = newRegistration;
    }

    public void updateRegistrationAttribute() {
        getAttribute(PersonStrategy.REGISTRATION).setValueId(getRegistration().getId());
    }
}

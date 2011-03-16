/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.complitex.pspoffice.person.strategy.entity;

import org.complitex.dictionary.entity.DomainObject;

/**
 *
 * @author Artem
 */
public class Person extends DomainObject{

    private String lastName;
    private String firstName;
    private String middleName;

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
}

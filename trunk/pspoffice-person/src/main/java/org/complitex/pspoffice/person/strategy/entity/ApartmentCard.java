/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import static com.google.common.collect.Lists.*;
import java.util.List;
import org.complitex.dictionary.entity.DomainObject;
import static org.complitex.pspoffice.person.strategy.ApartmentCardStrategy.*;
import static org.complitex.dictionary.util.AttributeUtil.*;

/**
 *
 * @author Artem
 */
public class ApartmentCard extends DomainObject {

    private List<Registration> registrations = newArrayList();
    private Person owner;

    public ApartmentCard(DomainObject copy) {
        super(copy);
    }

    public ApartmentCard() {
    }

    public String getPersonalAccount() {
        return getStringValue(this, PERSONAL_ACCOUNT);
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
    }

    public void addRegistration(Registration registration) {
        this.registrations.add(registration);
    }

    public long getAddressId() {
        return getAttribute(ADDRESS).getValueId();
    }

    public String getAddressEntity() {
        long valueTypeId = getAttribute(ADDRESS).getValueTypeId();
        if (ADDRESS_ROOM == valueTypeId) {
            return "room";
        } else if (ADDRESS_APARTMENT == valueTypeId) {
            return "apartment";
        } else if (ADDRESS_BUILDING == valueTypeId) {
            return "building";
        } else {
            throw new IllegalStateException("Address attribute expected to be of " + ADDRESS_ROOM + " or " + ADDRESS_APARTMENT + " or " + ADDRESS_BUILDING
                    + ". But was: " + valueTypeId);
        }
    }

    public int getRegisteredCount() {
        return 0;
    }

    public String getFormOfOwnership() {
        return getStringValue(this, FORM_OF_OWNERSHIP);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import java.util.Date;
import org.complitex.dictionary.converter.BooleanConverter;
import org.complitex.dictionary.converter.DateConverter;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.util.EjbBeanLocator;
import static org.complitex.pspoffice.person.strategy.RegistrationStrategy.*;

/**
 *
 * @author Artem
 */
public class Registration extends DomainObject {

    public Registration() {
    }

    public Registration(DomainObject object) {
        super(object);
    }

    public String getAddressEntity() {
        Attribute registrationAddressAttribute = getAddressAttribute();
        long addressTypeId = registrationAddressAttribute.getValueTypeId();
        if (addressTypeId == ADDRESS_APARTMENT) {
            return "apartment";
        } else if (addressTypeId == ADDRESS_BUILDING) {
            return "building";
        } else if (addressTypeId == ADDRESS_ROOM) {
            return "room";
        } else {
            throw new IllegalStateException("Address type is not resolved.");
        }
    }

    private Attribute getAddressAttribute() {
        Attribute addressAttribute = getAttribute(ADDRESS);
        if (addressAttribute == null) {
            throw new IllegalStateException("Registration address attribute is null.");
        }
        return addressAttribute;
    }

    public long getAddressId() {
        Attribute registrationAddressAttribute = getAddressAttribute();
        return registrationAddressAttribute.getValueId();
    }

    public Date getRegistrationDate() {
        Attribute registrationDateAttribute = getAttribute(REGISTRATION_DATE);
        String value = stringBean().getSystemStringCulture(registrationDateAttribute.getLocalizedValues()).getValue();
        return value != null ? new DateConverter().toObject(value) : null;
    }

    public String getOwnerRelationship() {
        Attribute ownerRelationshipAttribute = getAttribute(OWNER_RELATIONSHIP);
        return stringBean().getSystemStringCulture(ownerRelationshipAttribute.getLocalizedValues()).getValue();
    }

    public boolean isOwner() {
        Attribute isOwnerAttribute = getAttribute(IS_OWNER);
        return (isOwnerAttribute != null) && new BooleanConverter().toObject(
                stringBean().getSystemStringCulture(isOwnerAttribute.getLocalizedValues()).getValue());
    }

    public boolean isResponsible() {
        Attribute isResponsibleAttribute = getAttribute(IS_RESPONSIBLE);
        return (isResponsibleAttribute != null) && new BooleanConverter().toObject(
                stringBean().getSystemStringCulture(isResponsibleAttribute.getLocalizedValues()).getValue());
    }

    public String getOwnerName() {
        Attribute ownerNameAttribute = getAttribute(OWNER_NAME);
        return ownerNameAttribute == null ? null
                : stringBean().getSystemStringCulture(ownerNameAttribute.getLocalizedValues()).getValue();
    }

    private StringCultureBean stringBean() {
        return EjbBeanLocator.getBean(StringCultureBean.class);
    }
}

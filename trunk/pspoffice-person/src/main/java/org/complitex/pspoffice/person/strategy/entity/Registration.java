/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import java.util.Date;
import java.util.Locale;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.util.EjbBeanLocator;
import static org.complitex.pspoffice.person.strategy.RegistrationStrategy.*;
import static org.complitex.dictionary.util.AttributeUtil.*;

/**
 *
 * @author Artem
 */
public class Registration extends DomainObject {

    private String address;

    public Registration() {
    }

    public Registration(DomainObject object) {
        super(object);
    }

    private AddressRendererBean addressRenderer() {
        return EjbBeanLocator.getBean(AddressRendererBean.class);
    }

    /**
     * Displays address as string value.
     * Caches the displayed value.
     * @param locale
     * @return
     */
    public String displayAddress(Locale locale) {
        if (address == null) {
            address = addressRenderer().displayAddress(getAddressEntity(), getAddressId(), locale);
        }
        return address;
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

    public String getArrivalCountry() {
        return getStringValue(this, ARRIVAL_COUNTRY);
    }

    public String getArrivalRegion() {
        return getStringValue(this, ARRIVAL_REGION);
    }

    public String getArrivalDistrict() {
        return getStringValue(this, ARRIVAL_DISTRICT);
    }

    public String getArrivalCity() {
        return getStringValue(this, ARRIVAL_CITY);
    }

    public String getArrivalStreet() {
        return getStringValue(this, ARRIVAL_STREET);
    }

    public String getArrivalBuildingNumber() {
        return getStringValue(this, ARRIVAL_BUILDING_NUMBER);
    }

    public String getArrivalBuildingCorp() {
        return getStringValue(this, ARRIVAL_BUILDING_CORP);
    }

    public String getArrivalApartment() {
        return getStringValue(this, ARRIVAL_APARTMENT);
    }

    public Date getArrivalDate() {
        return getDateValue(this, ARRIVAL_DATE);
    }

    public String getDepartureCountry() {
        return getStringValue(this, DEPARTURE_COUNTRY);
    }

    public String getDepartureRegion() {
        return getStringValue(this, DEPARTURE_REGION);
    }

    public String getDepartureDistrict() {
        return getStringValue(this, DEPARTURE_DISTRICT);
    }

    public String getDepartureCity() {
        return getStringValue(this, DEPARTURE_CITY);
    }

    public String getDepartureStreet() {
        return getStringValue(this, DEPARTURE_STREET);
    }

    public String getDepartureBuildingNumber() {
        return getStringValue(this, DEPARTURE_BUILDING_NUMBER);
    }

    public String getDepartureBuildingCorp() {
        return getStringValue(this, DEPARTURE_BUILDING_CORP);
    }

    public String getDepartureApartment() {
        return getStringValue(this, DEPARTURE_APARTMENT);
    }

    public Date getDepartureDate() {
        return getDateValue(this, DEPARTURE_DATE);
    }

    public String getDepartureReason() {
        return getStringValue(this, DEPARTURE_REASON);
    }

    public String getOwnerRelationship() {
        return getStringValue(this, OWNER_RELATIONSHIP);
    }

    public String getFormOfOwnership() {
        return getStringValue(this, FORM_OF_OWNERSHIP);
    }

    public String getHousingRights() {
        return getStringValue(this, HOUSING_RIGHTS);
    }

    public Date getRegistrationDate() {
        return getDateValue(this, REGISTRATION_DATE);
    }

    public String getRegistrationType() {
        return getStringValue(this, REGISTRATION_TYPE);
    }

    public boolean isOwner() {
        return getBooleanValue(this, IS_OWNER);
    }

    public boolean isResponsible() {
        return getBooleanValue(this, IS_RESPONSIBLE);
    }

    public String getOwnerName() {
        return getStringValue(this, OWNER_NAME);
    }

    public String getPersonalAccount() {
        return getStringValue(this, PERSONAL_ACCOUNT);
    }
}

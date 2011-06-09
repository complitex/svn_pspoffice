/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import java.io.Serializable;
import static com.google.common.collect.Maps.*;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import static org.complitex.pspoffice.person.strategy.RegistrationStrategy.*;
import static org.complitex.dictionary.util.AttributeUtil.*;

/**
 *
 * @author Artem
 */
public class Registration extends DomainObject {

    public static class Address implements Serializable {

        private String country;
        private String region;
        private String district;
        private String city;
        private String street;
        private String buildingNumber;
        private String buildingCorp;
        private String apartment;

        public Address(String country, String region, String district, String city, String street, String buildingNumber,
                String buildingCorp, String apartment) {
            this.country = country;
            this.region = region;
            this.district = district;
            this.city = city;
            this.street = street;
            this.buildingNumber = buildingNumber;
            this.buildingCorp = buildingCorp;
            this.apartment = apartment;
        }
    }
    private Map<Locale, Address> addressComponentMap = newHashMap();
    private DomainObject ownerRelationshipObject;

    public Registration() {
    }

    public Registration(DomainObject object) {
        super(object);
    }

    /**
     * Displays address components as string values.
     * Caches the displayed values.
     */
    public String getCountry(Locale locale) {
        loadAddressComponentsIfNecessary(locale);
        return addressComponentMap.get(locale).country;
    }

    public String getRegion(Locale locale) {
        loadAddressComponentsIfNecessary(locale);
        return addressComponentMap.get(locale).region;
    }

    public String getDistrict(Locale locale) {
        loadAddressComponentsIfNecessary(locale);
        return addressComponentMap.get(locale).district;
    }

    public String getCity(Locale locale) {
        loadAddressComponentsIfNecessary(locale);
        return addressComponentMap.get(locale).city;
    }

    public String getStreet(Locale locale) {
        loadAddressComponentsIfNecessary(locale);
        return addressComponentMap.get(locale).street;
    }

    public String getBuildingNumber(Locale locale) {
        loadAddressComponentsIfNecessary(locale);
        return addressComponentMap.get(locale).buildingNumber;
    }

    public String getBuildingCorp(Locale locale) {
        loadAddressComponentsIfNecessary(locale);
        return addressComponentMap.get(locale).buildingCorp;
    }

    public String getApartment(Locale locale) {
        loadAddressComponentsIfNecessary(locale);
        return addressComponentMap.get(locale).apartment;
    }

    private void loadAddressComponentsIfNecessary(Locale locale) {
        if (addressComponentMap.get(locale) == null) {
            Address address = registrationStrategy().loadAddress(getAddressEntity(), getAddressId(), locale);
            addressComponentMap.put(locale, address);
        }
    }

    private RegistrationStrategy registrationStrategy() {
        return EjbBeanLocator.getBean(RegistrationStrategy.class);
    }

    private OwnerRelationshipStrategy ownerRelationshipStrategy() {
        return EjbBeanLocator.getBean("Owner_relationshipStrategy");
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

    /**
     * Displays owner relationship as string values.
     * Caches the displayed values.
     */
    public String getOwnerRelationship(Locale locale) {
        if (ownerRelationshipObject == null) {
            Attribute ownerRelationshipAttribute = getAttribute(OWNER_RELATIONSHIP);
            if (ownerRelationshipAttribute != null) {
                Long ownerRelationshipId = ownerRelationshipAttribute.getValueId();
                if (ownerRelationshipId != null) {
                    ownerRelationshipObject = registrationStrategy().loadOwnerRelationship(ownerRelationshipId);
                    return ownerRelationshipStrategy().displayDomainObject(ownerRelationshipObject, locale);
                }
            }
        }
        return null;
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

    public DomainObject getOwnerRelationshipObject() {
        return ownerRelationshipObject;
    }
}

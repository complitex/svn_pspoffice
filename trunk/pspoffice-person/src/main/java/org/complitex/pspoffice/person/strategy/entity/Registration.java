/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import static org.complitex.pspoffice.person.strategy.RegistrationStrategy.*;
import static org.complitex.dictionary.util.AttributeUtil.*;

/**
 *
 * @author Artem
 */
public class Registration extends DomainObject {

    public static class Address implements Serializable {

        private DomainObject country;
        private DomainObject region;
        private DomainObject district;
        private DomainObject city;
        private DomainObject street;
        private Building building;
        private DomainObject apartment;

        public Address(DomainObject country, DomainObject region, DomainObject district, DomainObject city,
                DomainObject street, Building building, DomainObject apartment) {
            this.country = country;
            this.region = region;
            this.district = district;
            this.city = city;
            this.street = street;
            this.building = building;
            this.apartment = apartment;
        }
    }
    private Address address;
    private DomainObject ownerRelationshipObject;
    private Person person;

    public Registration() {
    }

    public Registration(DomainObject object) {
        super(object);
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Displays address components as string values.
     * Caches the displayed values.
     */
    public String getCountry(Locale locale) {
        loadAddressComponentsIfNecessary();
        return address.country != null ? strategy("country").displayDomainObject(address.country, locale) : null;
    }

    public String getRegion(Locale locale) {
        loadAddressComponentsIfNecessary();
        return address.region != null ? strategy("region").displayDomainObject(address.region, locale) : null;
    }

    public String getDistrict(Locale locale) {
        loadAddressComponentsIfNecessary();
        return address.district != null ? strategy("district").displayDomainObject(address.district, locale) : null;
    }

    public String getCity(Locale locale) {
        loadAddressComponentsIfNecessary();
        return address.city != null ? strategy("city").displayDomainObject(address.city, locale) : null;
    }

    public String getStreet(Locale locale) {
        loadAddressComponentsIfNecessary();
        return address.street != null ? streetStrategy().getName(address.street, locale) : null;
    }

    public String getBuildingNumber(Locale locale) {
        loadAddressComponentsIfNecessary();
        return address.building != null ? address.building.getAccompaniedNumber(locale) : null;
    }

    public String getBuildingCorp(Locale locale) {
        loadAddressComponentsIfNecessary();
        return address.building != null ? address.building.getAccompaniedCorp(locale) : null;
    }

    public String getApartment(Locale locale) {
        loadAddressComponentsIfNecessary();
        return address.apartment != null ? strategy("apartment").displayDomainObject(address.apartment, locale) : null;
    }

    private void loadAddressComponentsIfNecessary() {
        if (address == null) {
            address = registrationStrategy().loadAddress(getAddressEntity(), getAddressId());
        }
    }

    private RegistrationStrategy registrationStrategy() {
        return (RegistrationStrategy) strategy("registration");
    }

    private StrategyFactory strategyFactory() {
        return EjbBeanLocator.getBean(StrategyFactory.class);
    }

    private IStrategy strategy(String entityTable) {
        return strategyFactory().getStrategy(entityTable);
    }

    private StreetStrategy streetStrategy() {
        return (StreetStrategy) strategy("street");
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
                    return strategy("owner_relationship").displayDomainObject(ownerRelationshipObject, locale);
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
}

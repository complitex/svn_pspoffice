/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.service;

import com.google.common.base.Predicate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Sets.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.dictionary.converter.BooleanConverter;
import org.complitex.dictionary.converter.DateConverter;
import org.complitex.dictionary.converter.GenderConverter;
import org.complitex.dictionary.converter.IConverter;
import org.complitex.dictionary.converter.StringConverter;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.service.NameBean;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.pspoffice.person.registration.report.entity.RegistrationStopCoupon;
import org.complitex.pspoffice.person.registration.report.exception.PersonNotRegisteredException;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import static org.complitex.pspoffice.person.strategy.PersonStrategy.*;
import static org.complitex.pspoffice.person.strategy.RegistrationStrategy.*;
import org.complitex.pspoffice.person.strategy.entity.Person;

/**
 *
 * @author Artem
 */
@Stateless
public class RegistrationStopCouponBean extends AbstractBean {

    private static final String RESOURCE_BINDLE = RegistrationStopCouponBean.class.getName();
    private static final String MAPPING_NAMESPACE = RegistrationStopCouponBean.class.getName();
    private static final String DATE_PATTERN = "dd.MM.yyyy";
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private StrategyFactory strategyFactory;
    @EJB
    private BuildingStrategy buildingStrategy;
    @EJB
    private StringCultureBean stringBean;
    @EJB
    private StreetStrategy streetStrategy;
    @EJB
    private NameBean nameBean;

    @Transactional
    public RegistrationStopCoupon getRegistrationClosingCoupon(Person person, Locale locale, String clientLineSeparator)
            throws PersonNotRegisteredException {
        if (person == null) {
            throw new IllegalArgumentException("Person is null.");
        }
        if (person.getRegistration() == null) {
            throw new PersonNotRegisteredException();
        }
        //name
        personStrategy.loadName(person);

        //address
        DomainObject registration = person.getRegistration();

        Attribute registrationAddressAttribute = registration.getAttribute(ADDRESS);
        if (registrationAddressAttribute == null) {
            throw new IllegalStateException("Registration address attribute is null.");
        }
        long addressId = registrationAddressAttribute.getValueId();
        long addressTypeId = registrationAddressAttribute.getValueTypeId();

        IStrategy addressStrategy = null;
        String addressEntity = null;
        if (addressTypeId == ADDRESS_APARTMENT) {
            addressEntity = "apartment";
        } else if (addressTypeId == ADDRESS_BUILDING) {
            addressEntity = "building";
        } else if (addressTypeId == ADDRESS_ROOM) {
            addressEntity = "room";
        } else {
            throw new IllegalStateException("Address type is not resolved.");
        }
        addressStrategy = strategyFactory.getStrategy(addressEntity);

        DomainObject addressObject = addressStrategy.findById(addressId, true);
        SearchComponentState addressComponentState = new SearchComponentState();
        IStrategy.SimpleObjectInfo info = addressStrategy.findParentInSearchComponent(addressId, null);
        if (info != null) {
            addressComponentState = addressStrategy.getSearchComponentStateForParent(info.getId(), info.getEntityTable(), null);
            addressComponentState.put(addressEntity, addressObject);
        }
        String apartment = null;
        DomainObject apartmentObject = addressComponentState.get("apartment");
        if (apartmentObject != null && apartmentObject.getId() != null && apartmentObject.getId() > 0) {
            apartment = strategyFactory.getStrategy("apartment").displayDomainObject(apartmentObject, locale);
        }
        Building buildingObject = (Building) addressComponentState.get("building");
        String buildingNumber = buildingObject.getAccompaniedNumber(locale);
        String buildingCorp = buildingObject.getAccompaniedCorp(locale);
        Long districtId = buildingStrategy.getDistrictId(buildingObject);
        String district = null;
        if (districtId != null) {
            IStrategy districtStrategy = strategyFactory.getStrategy("district");
            DomainObject districtObject = districtStrategy.findById(districtId, true);
            district = districtStrategy.displayDomainObject(districtObject, locale);
        }
        DomainObject streetObject = addressComponentState.get("street");
        String street = streetStrategy.getName(streetObject, locale);
        DomainObject cityObject = addressComponentState.get("city");
        String city = strategyFactory.getStrategy("city").displayDomainObject(cityObject, locale);
        DomainObject regionObject = addressComponentState.get("region");
        String region = strategyFactory.getStrategy("region").displayDomainObject(regionObject, locale);
        DomainObject countryObject = addressComponentState.get("country");
        String country = strategyFactory.getStrategy("country").displayDomainObject(countryObject, locale);

        RegistrationStopCoupon coupon = new RegistrationStopCoupon();
        coupon.setFirstName(person.getFirstName());
        coupon.setLastName(person.getLastName());
        coupon.setMiddleName(person.getMiddleName());
        coupon.setPreviousNames(getPreviousNames(person.getId(), clientLineSeparator));
        coupon.setBirthDate(getDateValue(person, BIRTH_DATE));
        coupon.setBirthCountry(getStringValue(person, BIRTH_COUNTRY));
        coupon.setBirthRegion(getStringValue(person, BIRTH_REGION));
        coupon.setBirthDistrict(getStringValue(person, BIRTH_DISTRICT));
        coupon.setBirthCity(getStringValue(person, BIRTH_CITY));
        coupon.setGender(getAttributeValue(person, GENDER, new GenderConverter()));
        coupon.setAddressCountry(country);
        coupon.setAddressRegion(region);
        coupon.setAddressDistrict(district);
        coupon.setAddressCity(city);
        coupon.setAddressStreet(street);
        coupon.setAddressBuildingNumber(buildingNumber);
        coupon.setAddressBuildingCorp(buildingCorp);
        coupon.setAddressApartment(apartment);
        coupon.setDepartureCountry(getStringValue(registration, DEPARTURE_COUNTRY));
        coupon.setDepartureRegion(getStringValue(registration, DEPARTURE_REGION));
        coupon.setDepartureDistrict(getStringValue(registration, DEPARTURE_DISTRICT));
        coupon.setDepartureCity(getStringValue(registration, DEPARTURE_CITY));
        coupon.setDepartureStreet(getStringValue(registration, DEPARTURE_STREET));
        coupon.setDepartureBuildingNumber(getStringValue(registration, DEPARTURE_BUILDING_NUMBER));
        coupon.setDepartureBuildingCorp(getStringValue(registration, DEPARTURE_BUILDING_CORP));
        coupon.setDepartureApartment(getStringValue(registration, DEPARTURE_APARTMENT));
        coupon.setDepartureDate(getDateValue(registration, DEPARTURE_DATE));
        coupon.setPassportSerialNumber(getStringValue(person, PASSPORT_SERIAL_NUMBER));
        coupon.setPassportNumber(getStringValue(person, PASSPORT_NUMBER));
        coupon.setPassportAcquisitionOrganization(getStringValue(person, PASSPORT_ACQUISITION_ORGANIZATION));
        coupon.setPassportAcquisitionDate(getDateValue(person, PASSPORT_ACQUISITION_DATE));

        //TODO: add birth certificate info
        coupon.setBirthCertificateInfo(null);

        coupon.setUkraineCitizenship(getAttributeValue(person, UKRAINE_CITIZENSHIP, new BooleanConverter()));
        coupon.setChildrenInfo(getChildrenInfo(person.getChildren(), locale, clientLineSeparator));
        return coupon;
    }

    private <T> T getAttributeValue(DomainObject object, long attributeTypeId, IConverter<T> converter) {
        Attribute attribute = object.getAttribute(attributeTypeId);
        T value = null;
        if (attribute != null) {
            String attributeValue = stringBean.getSystemStringCulture(attribute.getLocalizedValues()).getValue();
            value = attributeValue != null ? converter.toObject(attributeValue) : null;
        }
        return value;
    }

    private String getStringValue(DomainObject object, long attributeTypeId) {
        return getAttributeValue(object, attributeTypeId, new StringConverter());
    }

    private Date getDateValue(DomainObject object, long attributeTypeId) {
        return getAttributeValue(object, attributeTypeId, new DateConverter());
    }

    private String getChildrenInfo(Collection<Person> children, Locale locale, String lineSeparator) {
        StringBuilder childrenInfo = new StringBuilder();
        for (Person child : children) {
            String childFullName = personStrategy.displayDomainObject(child, locale);
            Date birthDate = getDateValue(child, BIRTH_DATE);
            String birthDateAsString = null;
            if (birthDate != null) {
                DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, locale);
                birthDateAsString = dateFormat.format(birthDate);
            }
            String birthCity = getStringValue(child, BIRTH_CITY);
            String birthDistrict = getStringValue(child, BIRTH_DISTRICT);
            childrenInfo.append(childFullName);
            if (birthDateAsString != null && birthCity != null && birthDistrict != null) {
                childrenInfo.append(", ").append(ResourceUtil.getString(RESOURCE_BINDLE, "birth_info", locale)).
                        append(birthDateAsString).append(", ").append(birthCity).append(", ").append(birthDistrict);
            }
            childrenInfo.append(lineSeparator != null ? lineSeparator : " ");
        }
        return childrenInfo.toString();
    }

    @Transactional
    private String getPreviousNames(long personId, String lineSeparator) {
        TreeSet<Date> previousNameStartDates = newTreeSet(sqlSession().selectList(MAPPING_NAMESPACE + ".findPreviousNameStartDates", personId));
        if (previousNameStartDates.isEmpty()) {
            return null;
        }
        previousNameStartDates.remove(previousNameStartDates.last());
        StringBuilder previousNamesBuilder = new StringBuilder();
        for (Date startDate : previousNameStartDates) {
            DomainObjectExample example = new DomainObjectExample(personId);
            example.setStartDate(startDate);
            List<Attribute> nameAttributes = sqlSession().selectList(MAPPING_NAMESPACE + ".findPreviousNames", example);
            String firstName = nameBean.getFirstName(find(nameAttributes, new Predicate<Attribute>() {

                @Override
                public boolean apply(Attribute input) {
                    return input.getAttributeTypeId().equals(FIRST_NAME);
                }
            }).getValueId());
            String middleName = nameBean.getMiddleName(find(nameAttributes, new Predicate<Attribute>() {

                @Override
                public boolean apply(Attribute input) {
                    return input.getAttributeTypeId().equals(MIDDLE_NAME);
                }
            }).getValueId());
            String lastName = nameBean.getLastName(find(nameAttributes, new Predicate<Attribute>() {

                @Override
                public boolean apply(Attribute input) {
                    return input.getAttributeTypeId().equals(LAST_NAME);
                }
            }).getValueId());
            previousNamesBuilder.append(personStrategy.displayPerson(firstName, middleName, lastName)).
                    append(lineSeparator != null ? lineSeparator : " ");
        }
        return previousNamesBuilder.toString();
    }
}

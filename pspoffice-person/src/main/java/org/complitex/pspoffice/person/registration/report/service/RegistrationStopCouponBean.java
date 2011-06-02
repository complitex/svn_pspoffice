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
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.service.NameBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.pspoffice.person.registration.report.entity.RegistrationStopCoupon;
import org.complitex.pspoffice.person.registration.report.exception.UnregisteredPersonException;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import static org.complitex.pspoffice.person.strategy.PersonStrategy.*;
import static org.complitex.pspoffice.person.strategy.RegistrationStrategy.*;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;

/**
 *
 * @author Artem
 */
@Stateless
public class RegistrationStopCouponBean extends AbstractBean {

    private static final String RESOURCE_BUNDLE = RegistrationStopCouponBean.class.getName();
    private static final String MAPPING_NAMESPACE = RegistrationStopCouponBean.class.getName();
    private static final String DATE_PATTERN = "dd.MM.yyyy";
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private StrategyFactory strategyFactory;
    @EJB
    private BuildingStrategy buildingStrategy;
    @EJB
    private StreetStrategy streetStrategy;
    @EJB
    private NameBean nameBean;

    @Transactional
    public RegistrationStopCoupon get(Person person, Locale locale, String clientLineSeparator)
            throws UnregisteredPersonException {
        if (person == null) {
            throw new IllegalArgumentException("Person is null.");
        }
        if (person.getRegistration() == null) {
            throw new UnregisteredPersonException();
        }
        //name
        personStrategy.loadName(person);

        //address
        Registration registration = person.getRegistration();
        String addressEntity = registration.getAddressEntity();
        IStrategy addressStrategy = strategyFactory.getStrategy(addressEntity);
        long addressId = registration.getAddressId();
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
        coupon.setBirthDate(person.getBirthDate());
        coupon.setBirthCountry(person.getBirthCountry());
        coupon.setBirthRegion(person.getBirthRegion());
        coupon.setBirthDistrict(person.getBirthDistrict());
        coupon.setBirthCity(person.getBirthCity());
        coupon.setGender(person.getGender());
        coupon.setAddressCountry(country);
        coupon.setAddressRegion(region);
        coupon.setAddressDistrict(district);
        coupon.setAddressCity(city);
        coupon.setAddressStreet(street);
        coupon.setAddressBuildingNumber(buildingNumber);
        coupon.setAddressBuildingCorp(buildingCorp);
        coupon.setAddressApartment(apartment);
        coupon.setDepartureCountry(registration.getDepartureCountry());
        coupon.setDepartureRegion(registration.getDepartureRegion());
        coupon.setDepartureDistrict(registration.getDepartureDistrict());
        coupon.setDepartureCity(registration.getDepartureCity());
        coupon.setDepartureStreet(registration.getDepartureStreet());
        coupon.setDepartureBuildingNumber(registration.getDepartureBuildingNumber());
        coupon.setDepartureBuildingCorp(registration.getDepartureBuildingCorp());
        coupon.setDepartureApartment(registration.getDepartureApartment());
        coupon.setDepartureDate(registration.getDepartureDate());
        coupon.setPassportSerialNumber(person.getPassportSerialNumber());
        coupon.setPassportNumber(person.getPassportNumber());
        coupon.setPassportAcquisitionOrganization(person.getPassportAcquisitionOrganization());
        coupon.setPassportAcquisitionDate(person.getPassportAcquisitionDate());

        //TODO: add birth certificate info
        coupon.setBirthCertificateInfo(null);
        coupon.setUkraineCitizenship(person.isUkraineCitizen());
        coupon.setChildrenInfo(getChildrenInfo(person.getChildren(), locale, clientLineSeparator));
        return coupon;
    }

    private String getChildrenInfo(Collection<Person> children, Locale locale, String lineSeparator) {
        StringBuilder childrenInfo = new StringBuilder();
        for (Person child : children) {
            String childFullName = personStrategy.displayDomainObject(child, locale);
            Date birthDate = child.getBirthDate();
            String birthDateAsString = null;
            if (birthDate != null) {
                DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, locale);
                birthDateAsString = dateFormat.format(birthDate);
            }
            String birthCity = child.getBirthCity();
            String birthDistrict = child.getBirthDistrict();
            childrenInfo.append(childFullName);
            if (birthDateAsString != null && birthCity != null && birthDistrict != null) {
                childrenInfo.append(", ").append(ResourceUtil.getString(RESOURCE_BUNDLE, "birth_info", locale)).
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

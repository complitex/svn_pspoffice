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
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.service.NameBean;
import org.complitex.dictionary.util.ResourceUtil;
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
        RegistrationStopCoupon coupon = new RegistrationStopCoupon();

        //name
        personStrategy.loadName(person);

        //address
        Registration registration = person.getRegistration();
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
        coupon.setAddressCountry(registration.getCountry(locale));
        coupon.setAddressRegion(registration.getRegion(locale));
        coupon.setAddressDistrict(registration.getDistrict(locale));
        coupon.setAddressCity(registration.getCity(locale));
        coupon.setAddressStreet(registration.getStreet(locale));
        coupon.setAddressBuildingNumber(registration.getBuildingNumber(locale));
        coupon.setAddressBuildingCorp(registration.getBuildingCorp(locale));
        coupon.setAddressApartment(registration.getApartment(locale));
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

        coupon.setBirthCertificateInfo(getBirthCertificateInfo(person.getBirthCertificateInfo(),
                person.getBirthCertificateAcquisitionDate(), person.getBirthCertificateAcquisitionOrganization(), locale));
        coupon.setUkraineCitizenship(person.isUkraineCitizen());
        coupon.setChildrenInfo(getChildrenInfo(person.getChildren(), locale, clientLineSeparator));
        return coupon;
    }

    private String getBirthCertificateInfo(String birthCertificateInfo, Date birthCertificateAcquisitionDate,
            String birthCertificateAcquisitionOrganization, Locale locale) {
        if (birthCertificateInfo != null && birthCertificateAcquisitionDate != null && birthCertificateAcquisitionOrganization != null) {
            DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, locale);
            String birthCertificateAcquisitionDateAsString = dateFormat.format(birthCertificateAcquisitionDate);
            return birthCertificateInfo + ", " + birthCertificateAcquisitionDateAsString + ", " + birthCertificateAcquisitionOrganization;
        }
        return null;
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

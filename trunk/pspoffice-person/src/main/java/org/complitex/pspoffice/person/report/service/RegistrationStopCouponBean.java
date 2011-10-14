package org.complitex.pspoffice.person.report.service;

import com.google.common.collect.ImmutableMap;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.pspoffice.person.report.entity.RegistrationStopCoupon;
import org.complitex.pspoffice.person.strategy.entity.PersonName.PersonNameType;
import org.complitex.pspoffice.person.strategy.service.PersonNameBean;
import static com.google.common.collect.Sets.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.document.strategy.entity.Passport;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import static org.complitex.pspoffice.report.util.ReportDateFormatter.format;

/**
 *
 * @author Artem
 */
@Stateless
public class RegistrationStopCouponBean extends AbstractBean {

    private static final String RESOURCE_BUNDLE = RegistrationStopCouponBean.class.getName();
    private static final String MAPPING_NAMESPACE = RegistrationStopCouponBean.class.getName();
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private PersonNameBean personNameBean;
    @EJB
    private SessionBean sessionBean;
    @EJB
    private LocaleBean localeBean;
    @EJB
    private DocumentStrategy documentStrategy;

    @Transactional
    public RegistrationStopCoupon get(Registration registration, String address, Locale locale) {
        final Locale systmeLocale = localeBean.getSystemLocale();

        RegistrationStopCoupon coupon = new RegistrationStopCoupon();
        Person person = registration.getPerson();

        //name
        coupon.setLastName(person.getLastName(locale, systmeLocale));
        coupon.setFirstName(person.getFirstName(locale, systmeLocale));
        coupon.setMiddleName(person.getMiddleName(locale, systmeLocale));
        coupon.setPreviousNames(getPreviousNames(person.getId(), locale));
        coupon.setBirthDate(person.getBirthDate());
        coupon.setBirthCountry(person.getBirthCountry());
        coupon.setBirthRegion(person.getBirthRegion());
        coupon.setBirthDistrict(person.getBirthDistrict());
        coupon.setBirthCity(person.getBirthCity());
        coupon.setGender(person.getGender() != null
                ? ResourceUtil.getString(RESOURCE_BUNDLE, person.getGender().name(), locale) : "");
        coupon.setAddress(address);
        coupon.setRegistrationOrganization(sessionBean.getMainUserOrganizationName(locale));
        coupon.setDepartureCountry(registration.getDepartureCountry());
        coupon.setDepartureRegion(registration.getDepartureRegion());
        coupon.setDepartureDistrict(registration.getDepartureDistrict());
        coupon.setDepartureCity(registration.getDepartureCity());
        coupon.setDepartureDate(registration.getDepartureDate());
        personStrategy.loadDocument(person);

        Document document = person.getDocument();
        if (document instanceof Passport) {
            Passport passport = (Passport) document;
            String passportInfo = passport.getSeries() + " " + passport.getNumber();
            Date dateIssued = passport.getDateIssued();
            String organizationIssued = passport.getOrganizationIssued();
            if (!Strings.isEmpty(organizationIssued)) {
                passportInfo += ", " + ResourceUtil.getString(RESOURCE_BUNDLE, "passport_issued", locale) + " " + organizationIssued;
                if (dateIssued != null) {
                    passportInfo += " " + format(dateIssued);
                }
            }
            coupon.setPassport(passportInfo);
        } else if (document.getDocumentTypeId() == DocumentTypeStrategy.BIRTH_CERTIFICATE) {
            coupon.setBirthCertificateInfo(documentStrategy.displayDomainObject(document, locale));
        }

        personStrategy.loadChildren(person);
        coupon.setChildrenInfo(getChildrenInfo(person.getChildren(), locale));

        return coupon;
    }

    private String getChildrenInfo(Collection<Person> children, Locale locale) {
        StringBuilder childrenInfo = new StringBuilder();
        int counter = 0;
        for (Person child : children) {
            String childFullName = personStrategy.displayDomainObject(child, locale);
            String birthDate = format(child.getBirthDate());
            childrenInfo.append(childFullName).append(" ").
                    append(birthDate).append(" ").
                    append(ResourceUtil.getString(RESOURCE_BUNDLE, "birth_date_suffix", locale)).
                    append(counter < children.size() - 1 ? ", " : "");
            counter++;
        }
        return childrenInfo.toString();
    }

    @Transactional
    private String getPreviousNames(long personId, Locale locale) {
        final long localeId = localeBean.convert(locale).getId();
        TreeSet<Date> previousNameStartDates = newTreeSet(sqlSession().selectList(MAPPING_NAMESPACE + ".findPreviousNameStartDates",
                ImmutableMap.of("personId", personId, "localeId", localeId)));
        if (previousNameStartDates.isEmpty()) {
            return null;
        }
        previousNameStartDates.remove(previousNameStartDates.last());
        StringBuilder previousNamesBuilder = new StringBuilder();
        int counter = 0;
        for (Date startDate : previousNameStartDates) {
            List<Attribute> nameAttributes = sqlSession().selectList(MAPPING_NAMESPACE + ".findPreviousNames",
                    ImmutableMap.<String, Object>of("personId", personId, "startDate", startDate, "localeId", localeId));

            //first name
            String firstName = null;
            for (Attribute a : nameAttributes) {
                if (a.getAttributeTypeId().equals(PersonStrategy.FIRST_NAME)) {
                    Long nameId = a.getValueId();
                    if (nameId != null) {
                        firstName = personNameBean.findById(PersonNameType.FIRST_NAME, nameId).getName();
                        break;
                    }
                }
            }

            //last name
            String lastName = null;
            for (Attribute a : nameAttributes) {
                if (a.getAttributeTypeId().equals(PersonStrategy.LAST_NAME)) {
                    Long nameId = a.getValueId();
                    if (nameId != null) {
                        lastName = personNameBean.findById(PersonNameType.LAST_NAME, nameId).getName();
                        break;
                    }
                }
            }

            //middle name
            String middleName = null;
            for (Attribute a : nameAttributes) {
                if (a.getAttributeTypeId().equals(PersonStrategy.MIDDLE_NAME)) {
                    Long nameId = a.getValueId();
                    if (nameId != null) {
                        middleName = personNameBean.findById(PersonNameType.MIDDLE_NAME, nameId).getName();
                        break;
                    }
                }
            }

            previousNamesBuilder.append(personStrategy.displayPerson(firstName, middleName, lastName)).
                    append(counter < previousNameStartDates.size() - 1 ? ", " : "");
            counter++;
        }
        return previousNamesBuilder.toString();
    }
}

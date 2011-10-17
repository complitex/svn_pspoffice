/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.service;

import java.util.Date;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.document.strategy.entity.Passport;
import org.complitex.pspoffice.person.report.entity.RegistrationCard;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import static org.complitex.pspoffice.report.util.ReportDateFormatter.format;

/**
 *
 * @author Artem
 */
@Stateless
public class RegistrationCardBean extends AbstractBean {

    private static final String RESOURCE_BUNDLE = RegistrationCardBean.class.getName();
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private LocaleBean localeBean;
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;

    @Transactional
    public RegistrationCard get(Registration registration, String address, Locale locale) {
        RegistrationCard card = new RegistrationCard();
        Person person = registration.getPerson();
        Locale systemLocale = localeBean.getSystemLocale();
        card.setLastName(person.getLastName(locale, systemLocale));
        card.setFirstName(person.getFirstName(locale, systemLocale));
        card.setMiddleName(person.getMiddleName(locale, systemLocale));
        card.setNationality("");
        card.setBirthDate(person.getBirthDate());
        card.setBirthRegion(person.getBirthRegion());
        card.setBirthDistrict(person.getBirthDistrict());
        card.setBirthCity(person.getBirthCity());
        card.setArrivalRegion(registration.getArrivalRegion());
        card.setArrivalDistrict(registration.getArrivalDistrict());
        card.setArrivalCity(registration.getArrivalCity());
        card.setArrivalStreet(registration.getArrivalStreet());
        card.setArrivalBuilding(registration.getArrivalBuildingNumber());
        card.setArrivalCorp(registration.getArrivalBuildingCorp());
        card.setArrivalApartment(registration.getArrivalApartment());
        card.setArrivalDate(registration.getArrivalDate());
        personStrategy.loadDocument(person);
        Document document = person.getDocument();
        if (document instanceof Passport) {
            Passport passport = (Passport) document;
            card.setPassportSeries(passport.getSeries());
            card.setPassportNumber(passport.getNumber());
            Date dateIssued = passport.getDateIssued();
            String organizationIssued = passport.getOrganizationIssued();
            String issued = "";
            if (!Strings.isEmpty(organizationIssued)) {
                issued += organizationIssued;
                if (dateIssued != null) {
                    issued += ", ";
                    issued += format(dateIssued);
                }
            }
            card.setPassportIssued(issued);
        }
        card.setAddress(address);

        personStrategy.loadChildren(person);
        int counter = 0;
        for (Person child : person.getChildren()) {
            String birthDateSuffix = ResourceUtil.getString(RESOURCE_BUNDLE, "birth_date_suffix", locale);
            switch (counter) {
                case 0: {
                    card.setChild0(personStrategy.displayDomainObject(child, locale) + ", "
                            + format(child.getBirthDate()) + birthDateSuffix);
                }
                break;
                case 1: {
                    card.setChild1(personStrategy.displayDomainObject(child, locale) + ", "
                            + format(child.getBirthDate()) + birthDateSuffix);
                }
                break;
                case 2: {
                    card.setChild2(personStrategy.displayDomainObject(child, locale) + ", "
                            + format(child.getBirthDate()) + birthDateSuffix);
                }
                break;
            }
        }
        card.setMilitary(person.getMilitaryServiceRelation());

        card.setRegistrationType(registrationTypeStrategy.displayDomainObject(registration.getRegistrationType(), locale));
        card.setRegistrationDate(registration.getRegistrationDate());
        card.setDepartureRegion(registration.getDepartureRegion());
        card.setDepartureDistrict(registration.getDepartureDistrict());
        card.setDepartureCity(registration.getDepartureCity());
        card.setDepartureReason(registration.getDepartureReason());
        card.setDepartureDate(registration.getDepartureDate());

        return card;
    }
}

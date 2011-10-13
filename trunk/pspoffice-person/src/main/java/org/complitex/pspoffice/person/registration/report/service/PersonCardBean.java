/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.service;

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
import org.complitex.pspoffice.person.registration.report.entity.PersonCard;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import static org.complitex.pspoffice.report.util.ReportDateFormatter.format;

/**
 *
 * @author Artem
 */
@Stateless
public class PersonCardBean extends AbstractBean {

    private static final String RESOURCE_BUNDLE = PersonCardBean.class.getName();
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private LocaleBean localeBean;

    @Transactional
    public PersonCard get(Person person, Locale locale) {
        PersonCard card = new PersonCard();
        Locale systemLocale = localeBean.getSystemLocale();
        card.setLastName(person.getLastName(locale, systemLocale));
        card.setFirstName(person.getFirstName(locale, systemLocale));
        card.setMiddleName(person.getMiddleName(locale, systemLocale));
        card.setNationality("");
        card.setBirthDate(person.getBirthDate());
        card.setBirthRegion(person.getBirthRegion());
        card.setBirthDistrict(person.getBirthDistrict());
        card.setBirthCity(person.getBirthCity());
        card.setArrivalDistrict("");
        card.setArrivalCity("");
        card.setArrivalStreet("");
        card.setArrivalBuilding("");
        card.setArrivalCorp("");
        card.setArrivalApartment("");
        card.setArrivalDate(null);
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
        card.setAddress("");

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

        card.setRegistrationType("");
        card.setRegistrationDate(null);
        card.setDepartureRegion("");
        card.setDepartureDistrict("");
        card.setDepartureCity("");
        card.setDepartureReason("");
        card.setDepartureDate(null);

        return card;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.service;

import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.document.strategy.entity.Passport;
import org.complitex.pspoffice.person.report.entity.RegistrationCard;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import static org.complitex.pspoffice.report.util.ReportDateFormatter.format;

/**
 *
 * @author Artem
 */
@Stateless
public class RegistrationCardBean extends AbstractBean {

    @EJB
    private PersonStrategy personStrategy;

    @Transactional
    public RegistrationCard get(Registration registration, String addressEntity, long addressId) {
        RegistrationCard card = new RegistrationCard();
        Person person = registration.getPerson();
        card.setRegistration(registration);
        card.setNationality("");
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
        card.setAddressEntity(addressEntity);
        card.setAddressId(addressId);
        personStrategy.loadChildren(person);

        return card;
    }
}

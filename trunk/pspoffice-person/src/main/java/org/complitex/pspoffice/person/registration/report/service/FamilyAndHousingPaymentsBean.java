/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.service;

import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.pspoffice.person.registration.report.entity.FamilyAndHousingPayments;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;

/**
 *
 * @author Artem
 */
@Stateless
public class FamilyAndHousingPaymentsBean extends AbstractBean {

    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;

    @Transactional
    public FamilyAndHousingPayments get(String addressEntity, long addressId, Locale locale) {
        List<Person> members = personStrategy.findPersonsByAddress(addressEntity, addressId);
        FamilyAndHousingPayments payments = new FamilyAndHousingPayments();
        String address = addressRendererBean.displayAddress(addressEntity, addressId, locale);
        payments.setAddress(address);
        payments.setName(personStrategy.getOwnerOrResponsibleName(members, locale));
        payments.setPersonalAccount(personStrategy.getPersonalAccount(members, locale));
        payments.setFormOfOwnership(personStrategy.getFormOfOwnership(members, locale));

        for (Person person : members) {
            FamilyMember member = new FamilyMember();
            member.setName(personStrategy.displayDomainObject(person, locale));
            member.setBirthDate(person.getBirthDate());
            member.setPassport(person.getPassportData());
            member.setRelation(person.getRegistration().getOwnerRelationship(locale));
            payments.addFamilyMember(member);
        }
        return payments;
    }
}

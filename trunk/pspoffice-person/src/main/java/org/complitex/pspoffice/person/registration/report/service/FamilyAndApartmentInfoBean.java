/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.service;

import java.util.Collection;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.pspoffice.person.registration.report.entity.FamilyAndApartmentInfo;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.exception.UnregisteredPersonException;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;

/**
 *
 * @author Artem
 */
@Stateless
public class FamilyAndApartmentInfoBean extends AbstractBean {

    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;

    @Transactional
    public FamilyAndApartmentInfo get(String addressEntity, long addressId, Locale locale) throws UnregisteredPersonException {
        Collection<Person> persons = personStrategy.findPersonsByAddress(addressEntity, addressId);
        String address = addressRendererBean.displayAddress(addressEntity, addressId, locale);
        if (persons == null || persons.isEmpty()) {
            throw new UnregisteredPersonException(address);
        }
        FamilyAndApartmentInfo info = new FamilyAndApartmentInfo();
        info.setAddress(address);
        for (Person person : persons) {
            FamilyMember member = new FamilyMember();
            member.setName(personStrategy.displayDomainObject(person, locale));
            member.setBirthDate(person.getBirthDate());
            member.setRegistrationDate(person.getRegistration().getRegistrationDate());
            info.addFamilyMember(member);
        }
        return info;
    }
}

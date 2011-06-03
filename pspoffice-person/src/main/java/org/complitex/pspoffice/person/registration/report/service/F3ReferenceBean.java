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
import org.complitex.pspoffice.person.registration.report.entity.F3Reference;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.exception.UnregisteredPersonException;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;

/**
 *
 * @author Artem
 */
@Stateless
public class F3ReferenceBean extends AbstractBean {

    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private CommunalApartmentService communalApartmentService;
    @EJB
    private AddressRendererBean addressRendererBean;

    @Transactional
    public F3Reference get(Person person, Locale locale) throws UnregisteredPersonException {
        if (person == null) {
            throw new IllegalArgumentException("Person is null.");
        }
        if (person.getRegistration() == null) {
            throw new UnregisteredPersonException();
        }

        F3Reference f3 = new F3Reference();

        //name
        personStrategy.loadName(person);
        f3.setName(personStrategy.displayDomainObject(person, locale));

        //address
        Registration registration = person.getRegistration();
        long addressId = registration.getAddressId();
        String addressEntity = registration.getAddressEntity();
        f3.setAddress(addressRendererBean.displayAddress(addressEntity, addressId, locale));
        f3.setPersonalAccountOwnerName(personStrategy.getOwnerName(addressEntity, addressId, locale));
        List<Person> members = personStrategy.findPersonsByAddress(addressEntity, addressId);
        f3.setFormOfOwnership(personStrategy.getFormOfOwnership(members, locale));
        for (Person p : members) {
            FamilyMember member = new FamilyMember();
            member.setFirstName(p.getFirstName());
            member.setMiddleName(p.getMiddleName());
            member.setLastName(p.getLastName());
            member.setBirthDate(p.getBirthDate());
            member.setRelation(p.getRegistration().getOwnerRelationship());
            member.setRegistrationDate(p.getRegistration().getRegistrationDate());
            f3.addFamilyMember(member);
        }
        f3.setNeighbourFamilies(communalApartmentService.findNeighbourFamilies(addressEntity, addressId, locale));
        return f3;
    }
}

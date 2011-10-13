/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.service;

import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import org.complitex.pspoffice.person.report.entity.F3Reference;
import org.complitex.pspoffice.person.report.entity.FamilyMember;
import org.complitex.pspoffice.person.report.entity.NeighbourFamily;
import org.complitex.pspoffice.person.report.exception.UnregisteredPersonException;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
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
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;

    @Transactional
    public F3Reference get(Person person, Locale locale) throws UnregisteredPersonException {
        Long apartmentCardId = personStrategy.findApartmentCardIdByPermanentRegistration(person.getId());
        if (apartmentCardId == null) {
            throw new UnregisteredPersonException();
        }
        ApartmentCard apartmentCard = apartmentCardStrategy.findById(apartmentCardId, true);

        F3Reference f3 = new F3Reference();

        //name
        f3.setName(personStrategy.displayDomainObject(person, locale));

        //address
        long addressId = apartmentCard.getAddressId();
        String addressEntity = ApartmentCardStrategy.getAddressEntity(apartmentCard);
        f3.setAddress(addressRendererBean.displayAddress(addressEntity, addressId, locale));
        f3.setPersonalAccountOwnerName(personStrategy.displayDomainObject(apartmentCard.getOwner(), locale));
        f3.setFormOfOwnership(ownershipFormStrategy.displayDomainObject(apartmentCard.getOwnershipForm(), locale));

        for (Registration registration : apartmentCard.getRegistrations()) {
            if (!registration.isFinished()) {
                FamilyMember member = new FamilyMember();
                Person memberPerson = registration.getPerson();
                member.setName(personStrategy.displayDomainObject(memberPerson, locale));
                member.setBirthDate(memberPerson.getBirthDate());
                member.setRegistrationDate(registration.getRegistrationDate());
                member.setRelation(ownerRelationshipStrategy.displayDomainObject(registration.getOwnerRelationship(), locale));
                f3.addFamilyMember(member);
            }
        }

        //neighbors
        for (ApartmentCard neighbourCard : apartmentCardStrategy.getNeighbourApartmentCards(apartmentCard)) {
            NeighbourFamily family = new NeighbourFamily();
            family.setName(personStrategy.displayDomainObject(neighbourCard.getOwner(), locale));
            family.setAmount(neighbourCard.getRegisteredCount());
            f3.addNeighbourFamily(family);
        }

        return f3;
    }
}

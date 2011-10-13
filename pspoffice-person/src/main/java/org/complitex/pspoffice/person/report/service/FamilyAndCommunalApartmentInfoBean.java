/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.person.report.entity.FamilyAndCommunalApartmentInfo;
import org.complitex.pspoffice.person.report.entity.FamilyMember;
import org.complitex.pspoffice.person.report.entity.NeighbourFamily;
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
public class FamilyAndCommunalApartmentInfoBean extends AbstractBean {

    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private StrategyFactory strategyFactory;

    @Transactional
    public FamilyAndCommunalApartmentInfo get(ApartmentCard apartmentCard, Locale locale) {
        FamilyAndCommunalApartmentInfo info = new FamilyAndCommunalApartmentInfo();
        String addressEntity = ApartmentCardStrategy.getAddressEntity(apartmentCard);
        long addressId = apartmentCard.getAddressId();
        info.setAddress(addressRendererBean.displayAddress(addressEntity, addressId, locale));
        String ownerName = personStrategy.displayDomainObject(apartmentCard.getOwner(), locale);
        info.setName(ownerName);

        Long apartmentId = apartmentCardStrategy.getApartmentId(apartmentCard);
        String apartmentNumber = "";
        if (apartmentId != null) {
            IStrategy apartmentStrategy = strategyFactory.getStrategy("apartment");
            DomainObject apartmentObject = apartmentStrategy.findById(apartmentId, true);
            apartmentNumber = apartmentStrategy.displayDomainObject(apartmentObject, locale);
        }
        //neighbors
        List<ApartmentCard> allApartmentCards = new ArrayList<ApartmentCard>();
        allApartmentCards.add(apartmentCard);
        allApartmentCards.addAll(apartmentCardStrategy.getNeighbourApartmentCards(apartmentCard));
        for (ApartmentCard neighbourCard : allApartmentCards) {
            NeighbourFamily family = new NeighbourFamily();
            family.setName(personStrategy.displayDomainObject(neighbourCard.getOwner(), locale));
            family.setApartmentNumber(apartmentNumber);
            info.addNeighbourFamily(family);
        }

        for (Registration registration : apartmentCard.getRegistrations()) {
            if (!registration.isFinished()) {
                FamilyMember member = new FamilyMember();
                Person person = registration.getPerson();
                member.setName(personStrategy.displayDomainObject(person, locale));
                member.setBirthDate(person.getBirthDate());
                member.setRelation(ownerRelationshipStrategy.displayDomainObject(registration.getOwnerRelationship(), locale));
                member.setRegistrationDate(registration.getRegistrationDate());
                info.addFamilyMember(member);
            }
        }

        return info;
    }
}

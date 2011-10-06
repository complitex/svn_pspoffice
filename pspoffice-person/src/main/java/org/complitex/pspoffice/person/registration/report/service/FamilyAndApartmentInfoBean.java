/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.service;

import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.person.registration.report.entity.FamilyAndApartmentInfo;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
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
public class FamilyAndApartmentInfoBean extends AbstractBean {

    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;

    @Transactional
    public FamilyAndApartmentInfo get(ApartmentCard apartmentCard, Locale locale) {
        FamilyAndApartmentInfo info = new FamilyAndApartmentInfo();
        String addressEntity = ApartmentCardStrategy.getAddressEntity(apartmentCard);
        long addressId = apartmentCard.getAddressId();
        info.setAddress(addressRendererBean.displayAddress(addressEntity, addressId, locale));

        for (Registration registration : apartmentCard.getRegistrations()) {
            if (!registration.isFinished()) {
                FamilyMember member = new FamilyMember();
                Person person = registration.getPerson();
                member.setName(personStrategy.displayDomainObject(person, locale));
                member.setBirthDate(person.getBirthDate());
                member.setRegistrationDate(registration.getRegistrationDate());
                member.setRelation(ownerRelationshipStrategy.displayDomainObject(registration.getOwnerRelationship(), locale));
                info.addFamilyMember(member);
            }
        }
        return info;
    }
}

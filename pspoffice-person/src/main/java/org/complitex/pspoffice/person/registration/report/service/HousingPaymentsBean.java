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
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.entity.HousingPayments;
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
public class HousingPaymentsBean extends AbstractBean {

    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;
    @EJB
    private DocumentStrategy documentStrategy;

    @Transactional
    public HousingPayments get(ApartmentCard apartmentCard, Locale locale) {
        HousingPayments payments = new HousingPayments();
        String addressEntity = ApartmentCardStrategy.getAddressEntity(apartmentCard);
        long addressId = apartmentCard.getAddressId();
        payments.setAddress(addressRendererBean.displayAddress(addressEntity, addressId, locale));
        payments.setName(personStrategy.displayDomainObject(apartmentCard.getOwner(), locale));
        payments.setPersonalAccount("");
        payments.setFormOfOwnership(ownershipFormStrategy.displayDomainObject(apartmentCard.getOwnershipForm(), locale));

        for (Registration registration : apartmentCard.getRegistrations()) {
            if (!registration.isFinished()) {
                FamilyMember member = new FamilyMember();
                Person person = registration.getPerson();
                member.setName(personStrategy.displayDomainObject(person, locale));
                member.setBirthDate(person.getBirthDate());
                personStrategy.loadDocument(person);
                Document document = person.getDocument();
                if (document.getDocumentTypeId() == DocumentTypeStrategy.PASSPORT
                        || document.getDocumentTypeId() == DocumentTypeStrategy.BIRTH_CERTIFICATE) {
                    member.setPassport(documentStrategy.displayDomainObject(document, locale));
                }
                member.setRelation(ownerRelationshipStrategy.displayDomainObject(registration.getOwnerRelationship(), locale));
                payments.addFamilyMember(member);
            }
        }

        return payments;
    }
}

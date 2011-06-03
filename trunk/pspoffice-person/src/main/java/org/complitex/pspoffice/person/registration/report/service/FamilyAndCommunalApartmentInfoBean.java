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
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.pspoffice.person.registration.report.entity.FamilyAndCommunalApartmentInfo;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.entity.NeighbourFamily;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;

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
    private StrategyFactory strategyFactory;

    @Transactional
    public FamilyAndCommunalApartmentInfo get(List<NeighbourFamily> families, NeighbourFamily selectedFamily, Locale locale) {
        IStrategy roomStrategy = strategyFactory.getStrategy("room");
        long roomId = selectedFamily.getInternalRoomId();
        DomainObject room = roomStrategy.findById(roomId, true);
        if (!room.getParentEntityId().equals(100L)) {
            throw new IllegalArgumentException("Invalid room (id = " + selectedFamily.getInternalRoomId()
                    + "). Room must be inside of communal apartment.");
        }
        List<Person> members = personStrategy.findPersonsByAddress("room", roomId);
        FamilyAndCommunalApartmentInfo info = new FamilyAndCommunalApartmentInfo();
        info.setAddress(addressRendererBean.displayAddress("apartment", room.getParentId(), locale));
        for (NeighbourFamily neighbourFamily : families) {
            info.addNeighbourFamily(neighbourFamily);
        }
        info.setName(selectedFamily.getName());
        for (Person p : members) {
            FamilyMember member = new FamilyMember();
            member.setName(personStrategy.displayDomainObject(p, locale));
            member.setRelation(p.getRegistration().getOwnerRelationship());
            member.setBirthDate(p.getBirthDate());
            member.setRegistrationDate(p.getRegistration().getRegistrationDate());
            info.addFamilyMember(member);
        }
        return info;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.service;

import static com.google.common.collect.ImmutableList.*;
import java.util.Collection;
import java.util.Collections;
import static com.google.common.collect.Lists.*;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.pspoffice.person.registration.report.entity.NeighbourFamily;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;

/**
 *
 * @author Artem
 */
@Stateless
public class CommunalApartmentService extends AbstractBean {

    @EJB
    private StrategyFactory strategyFactory;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private RegistrationStrategy registrationStrategy;

    @Transactional
    public boolean isCommunalApartment(long apartmentId) {
        return !registrationStrategy.isLeafAddress(apartmentId, "apartment");
    }

    @Transactional
    public List<NeighbourFamily> findAllNeighbourFamilies(long apartmentId, Locale locale) {
        return findNeighbourFamilies(apartmentId, null, locale);
    }

    @Transactional
    private List<? extends DomainObject> findAllRooms(long apartmentId) {
        IStrategy roomStrategy = strategyFactory.getStrategy("room");
        DomainObjectExample example = new DomainObjectExample();
        example.setAdmin(true);
        example.setStatus(ShowMode.ACTIVE.name());
        example.setParentEntity("apartment");
        example.setParentId(apartmentId);
        return roomStrategy.find(example);
    }

    @Transactional
    private List<NeighbourFamily> findNeighbourFamilies(long apartmentId, Collection<Long> notIncludedRoomIds, Locale locale) {
        List<? extends DomainObject> rooms = findAllRooms(apartmentId);
        if (rooms == null || rooms.isEmpty()) {
            return null;
        }
        List<NeighbourFamily> neighbourFamilies = newArrayList();
        if (notIncludedRoomIds == null) {
            notIncludedRoomIds = Collections.emptyList();
        }
        IStrategy roomStrategy = strategyFactory.getStrategy("room");
        for (DomainObject currentRoom : rooms) {
            if (!notIncludedRoomIds.contains(currentRoom.getId())) {
                List<Person> persons = personStrategy.findPersonsByAddress("room", currentRoom.getId());
                if (persons == null || persons.isEmpty()) {
                    continue;
                }
                NeighbourFamily neighbourFamily = new NeighbourFamily();
                neighbourFamily.setInternalRoomId(currentRoom.getId());
                neighbourFamily.setAmount(persons.size());
                neighbourFamily.setRoomNumber(roomStrategy.displayDomainObject(currentRoom, locale));
                //TODO: set first owner, might all owners should be set?
                for (Person person : persons) {
                    Registration registration = person.getRegistration();
                    if (registration.isOwner() || registration.isResponsible()) {
                        neighbourFamily.setName(personStrategy.displayDomainObject(person, locale));
                        break;
                    }
                }
                if (Strings.isEmpty(neighbourFamily.getName())) {
                    neighbourFamily.setName(ResourceUtil.getString(PersonStrategy.RESOURCE_BUNDLE, "no_owner_or_responsible", locale));
                }
                neighbourFamilies.add(neighbourFamily);
            }
        }
        return neighbourFamilies;
    }

    /**
     * Finds only first owner/responsible man.
     */
    @Transactional
    public List<NeighbourFamily> findNeighbourFamilies(String addressEntity, long addressId, Locale locale) {
        if (addressEntity.equals("building")) {
            return null;
        } else if (addressEntity.equals("apartment")) {
            return null;
        } else if (addressEntity.equals("room")) {
            IStrategy roomStrategy = strategyFactory.getStrategy("room");
            DomainObject room = roomStrategy.findById(addressId, true);
            if (room == null) {
                throw new IllegalArgumentException("Room object with id = " + addressId + " doesn't exist.");
            }
            if (!room.getParentEntityId().equals(100L)) {
                return null;
            }
            return findNeighbourFamilies(room.getParentId(), of(room.getId()), locale);
        } else {
            throw new IllegalArgumentException("Address entity " + addressEntity + " value must be only the one of `building`, `apartment` or `room`.");
        }
    }
}

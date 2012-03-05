/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.grid.ApartmentCardsGridEntity;
import org.complitex.pspoffice.person.strategy.entity.grid.ApartmentCardsGridFilter;

/**
 *
 * @author Artem
 */
@Stateless
public class ApartmentCardsGridBean extends AbstractBean {

    private static final String MAPPING = ApartmentCardsGridBean.class.getName();
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private SessionBean sessionBean;
    @EJB(name = "OrganizationStrategy")
    private IOrganizationStrategy organizationStrategy;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;

    public ApartmentCardsGridFilter newFilter(long apartmentId, Locale locale) {
        final boolean isAdmin = sessionBean.isAdmin();
        return new ApartmentCardsGridFilter(apartmentId,
                !isAdmin ? sessionBean.getPermissionString("apartment_card") : null,
                isAdmin, locale);
    }

    private Map<String, Object> newParamsMap(ApartmentCardsGridFilter filter) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("apartmentCardAddressAT", ApartmentCardStrategy.ADDRESS);
        params.put("apartmentCardApartmentVT", ApartmentCardStrategy.ADDRESS_APARTMENT);
        params.put("apartmentCardRoomVT", ApartmentCardStrategy.ADDRESS_ROOM);
        params.put("apartmentId", filter.getApartmentId());
        params.put("apartmentCardPermissionString", filter.getApartmentCardPermissionString());
        params.put("admin", filter.isAdmin());
        params.put("start", filter.getStart());
        params.put("size", filter.getSize());
        return params;
    }

    public int count(ApartmentCardsGridFilter filter) {
        return (Integer) sqlSession().selectOne(MAPPING + ".count", newParamsMap(filter));
    }

    public int count(long apartmentId) {
        return count(newFilter(apartmentId, null));
    }

    public long findOne(long apartmentId) {
        return (Long) sqlSession().selectOne(MAPPING + ".find", newParamsMap(newFilter(apartmentId, null)));
    }

    public List<ApartmentCardsGridEntity> find(ApartmentCardsGridFilter filter) {
        List<Long> apartmentCardIds = sqlSession().selectList(MAPPING + ".find", newParamsMap(filter));
        final List<ApartmentCardsGridEntity> result = Lists.newArrayList();
        if (apartmentCardIds != null && !apartmentCardIds.isEmpty()) {
            for (long apartmentCardId : apartmentCardIds) {
                //apartment card
                final ApartmentCard apartmentCard = apartmentCardStrategy.findById(apartmentCardId, true);
                final String apartmentCardNumber = String.valueOf(apartmentCard.getId());

                //owner full name
                final String owner = personStrategy.displayDomainObject(apartmentCard.getOwner(), filter.getLocale());

                //ownership form
                final DomainObject ownershipFormObject = apartmentCard.getOwnershipForm();
                final String ownerhipForm = ownershipFormStrategy.displayDomainObject(ownershipFormObject, filter.getLocale());

                //registered
                final int registered = apartmentCard.getRegisteredCount();

                //organization
                final Set<Long> organizationIds = apartmentCard.getSubjectIds();
                if (organizationIds.size() > 1) {
                    throw new IllegalStateException("Apartment card cannot belong to more one organizations.");
                }
                long organizationId = organizationIds.iterator().next();
                final DomainObject organization = organizationId > 0 ? organizationStrategy.findById(organizationId, true) : null;


                result.add(new ApartmentCardsGridEntity(apartmentCardNumber, apartmentCard.getId(), ownerhipForm,
                        registered, organization, owner));
            }
        }
        return result;
    }
}

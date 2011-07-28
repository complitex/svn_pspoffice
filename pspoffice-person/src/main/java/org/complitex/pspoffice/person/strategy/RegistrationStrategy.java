/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.Strategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@Stateless
public class RegistrationStrategy extends Strategy {

    /**
     * Attribute type ids
     */
    public static final long ARRIVAL_COUNTRY = 2100;
    public static final long ARRIVAL_REGION = 2101;
    public static final long ARRIVAL_DISTRICT = 2102;
    public static final long ARRIVAL_CITY = 2103;
    public static final long ARRIVAL_STREET = 2104;
    public static final long ARRIVAL_BUILDING_NUMBER = 2105;
    public static final long ARRIVAL_BUILDING_CORP = 2106;
    public static final long ARRIVAL_APARTMENT = 2107;
    public static final long ARRIVAL_DATE = 2108;
    public static final long DEPARTURE_COUNTRY = 2109;
    public static final long DEPARTURE_REGION = 2110;
    public static final long DEPARTURE_DISTRICT = 2111;
    public static final long DEPARTURE_CITY = 2112;
    public static final long DEPARTURE_STREET = 2113;
    public static final long DEPARTURE_BUILDING_NUMBER = 2114;
    public static final long DEPARTURE_BUILDING_CORP = 2115;
    public static final long DEPARTURE_APARTMENT = 2116;
    public static final long DEPARTURE_DATE = 2117;
    public static final long DEPARTURE_REASON = 2118;
    public static final long REGISTRATION_DATE = 2119;
    public static final long REGISTRATION_TYPE = 2120;
    public static final long OWNER_RELATIONSHIP = 2121;
    public static final long PERSON = 2122;
    @EJB
    private StrategyFactory strategyFactory;
    @EJB
    private BuildingStrategy buildingStrategy;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;
    @EJB
    private PersonStrategy personStrategy;

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional
    @Override
    public Registration findById(long id, boolean runAsAdmin) {
        return findById(id, runAsAdmin, true);
    }

    @Transactional
    public Registration findById(long id, boolean runAsAdmin, boolean loadPerson) {
        DomainObject registrationObject = super.findById(id, runAsAdmin);
        if (registrationObject == null) {
            return null;
        }
        Registration registration = new Registration(registrationObject);
        if (loadPerson) {
            loadPerson(registration);
        }
        return registration;
    }

    @Transactional
    public DomainObject loadOwnerRelationship(long ownerRelationshipId) {
        return ownerRelationshipStrategy.findById(ownerRelationshipId, true);
    }

    @Transactional
    private void loadPerson(Registration registration) {
        long personId = registration.getAttribute(PERSON).getValueId();
        Person person = personStrategy.findById(personId, true);
        personStrategy.loadName(person);
        registration.setPerson(person);
    }

    @Transactional
    @Override
    public Registration findHistoryObject(long objectId, Date date) {
        DomainObject registrationObject = super.findHistoryObject(objectId, date);
        if (registrationObject == null) {
            return null;
        }
        Registration registration = new Registration(registrationObject);
        loadPerson(registration);
        return registration;
    }

    @Override
    public Class<? extends WebPage> getEditPage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getEntityTable() {
        return "registration";
    }

    @Override
    public Class<? extends WebPage> getHistoryPage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PageParameters getHistoryPageParams(long objectId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Class<? extends WebPage> getListPage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PageParameters getListPageParams() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.PERSON_MODULE_EDIT};
    }

//    @Override
//    protected Attribute fillManyValueTypesAttribute(EntityAttributeType attributeType, Long objectId) {
//        Attribute attribute = new Attribute();
//        attribute.setAttributeTypeId(attributeType.getId());
//        attribute.setObjectId(objectId);
//        attribute.setAttributeId(1L);
//
//        Long attributeValueTypeId = null;
//        if (attributeType.getId().equals(ADDRESS)) {
//            attributeValueTypeId = ADDRESS_APARTMENT;
//        }
//        attribute.setValueTypeId(attributeValueTypeId);
//        return attribute;
//    }
//    public long getAddressTypeId(String addressEntity) {
//        if ("apartment".equals(addressEntity)) {
//            return ADDRESS_APARTMENT;
//        } else if ("room".equals(addressEntity)) {
//            return ADDRESS_ROOM;
//        } else if ("building".equals(addressEntity)) {
//            return ADDRESS_BUILDING;
//        }
//        throw new IllegalStateException("Address entity `" + addressEntity + "` is not resolved.");
//    }
    @Override
    public Registration newInstance() {
        return new Registration(super.newInstance());
    }

//    @Transactional
//    public Registration.Address loadAddress(String addressEntity, long addressId) {
//        IStrategy addressStrategy = strategyFactory.getStrategy(addressEntity);
//        DomainObject addressObject = addressStrategy.findById(addressId, true);
//        SearchComponentState addressComponentState = new SearchComponentState();
//        IStrategy.SimpleObjectInfo info = addressStrategy.findParentInSearchComponent(addressId, null);
//        if (info != null) {
//            addressComponentState = addressStrategy.getSearchComponentStateForParent(info.getId(), info.getEntityTable(), null);
//            addressComponentState.put(addressEntity, addressObject);
//        }
//        DomainObject apartment = addressComponentState.get("apartment");
//        apartment = apartment != null && apartment.getId() != null && apartment.getId() > 0 ? apartment : null;
//        Building building = (Building) addressComponentState.get("building");
//        building = building != null && building.getId() != null && building.getId() > 0 ? building : null;
//        Long districtId = buildingStrategy.getDistrictId(building);
//        DomainObject district = null;
//        if (districtId != null) {
//            IStrategy districtStrategy = strategyFactory.getStrategy("district");
//            district = districtStrategy.findById(districtId, true);
//            district = district != null && district.getId() != null && district.getId() > 0 ? district : null;
//        }
//        DomainObject street = addressComponentState.get("street");
//        street = street != null && street.getId() != null && street.getId() > 0 ? street : null;
//        DomainObject city = addressComponentState.get("city");
//        city = city != null && city.getId() != null && city.getId() > 0 ? city : null;
//        DomainObject region = addressComponentState.get("region");
//        region = region != null && region.getId() != null && region.getId() > 0 ? region : null;
//        DomainObject country = addressComponentState.get("country");
//        country = country != null && country.getId() != null && country.getId() > 0 ? country : null;
//        return new Registration.Address(country, region, district, city, street, building, apartment);
//    }
    @Override
    public String[] getDescriptionRoles() {
        return new String[]{SecurityRole.PERSON_MODULE_DESCRIPTION_EDIT};
    }
}

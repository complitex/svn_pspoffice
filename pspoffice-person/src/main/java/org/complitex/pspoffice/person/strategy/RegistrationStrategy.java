/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy;

import java.util.Date;
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
    public static final long ADDRESS = 2100;
    public static final long ARRIVAL_COUNTRY = 2101;
    public static final long ARRIVAL_REGION = 2102;
    public static final long ARRIVAL_DISTRICT = 2103;
    public static final long ARRIVAL_CITY = 2104;
    public static final long ARRIVAL_STREET = 2105;
    public static final long ARRIVAL_BUILDING_NUMBER = 2106;
    public static final long ARRIVAL_BUILDING_CORP = 2107;
    public static final long ARRIVAL_APARTMENT = 2108;
    public static final long ARRIVAL_DATE = 2109;
    public static final long DEPARTURE_COUNTRY = 2110;
    public static final long DEPARTURE_REGION = 2111;
    public static final long DEPARTURE_DISTRICT = 2112;
    public static final long DEPARTURE_CITY = 2113;
    public static final long DEPARTURE_STREET = 2114;
    public static final long DEPARTURE_BUILDING_NUMBER = 2115;
    public static final long DEPARTURE_BUILDING_CORP = 2116;
    public static final long DEPARTURE_APARTMENT = 2117;
    public static final long DEPARTURE_DATE = 2118;
    public static final long DEPARTURE_REASON = 2119;
    public static final long REGISTRATION_DATE = 2120;
    public static final long REGISTRATION_TYPE = 2121;
    public static final long OWNER_RELATIONSHIP = 2122;
    public static final long FORM_OF_OWNERSHIP = 2123;
    public static final long HOUSING_RIGHTS = 2124;
    public static final long IS_OWNER = 2125;
    public static final long IS_RESPONSIBLE = 2126;
    public static final long OWNER_NAME = 2127;
    public static final long PERSONAL_ACCOUNT = 2128;
    /**
     * Attribute value type ids
     */
    public static final long ADDRESS_ROOM = 2100;
    public static final long ADDRESS_APARTMENT = 2101;
    public static final long ADDRESS_BUILDING = 2102;
    @EJB
    private StrategyFactory strategyFactory;
    @EJB
    private BuildingStrategy buildingStrategy;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional
    @Override
    public Registration findById(long id, boolean runAsAdmin) {
        DomainObject registrationObject = super.findById(id, runAsAdmin);
        if (registrationObject == null) {
            return null;
        }
        return new Registration(registrationObject);
    }

    @Transactional
    public DomainObject loadOwnerRelationship(long ownerRelationshipId) {
        return ownerRelationshipStrategy.findById(ownerRelationshipId, true);
    }

    @Transactional
    @Override
    public Registration findHistoryObject(long objectId, Date date) {
        DomainObject registrationObject = super.findHistoryObject(objectId, date);
        if (registrationObject == null) {
            return null;
        }
        return new Registration(registrationObject);
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

    @Override
    protected Attribute fillManyValueTypesAttribute(EntityAttributeType attributeType, Long objectId) {
        Attribute attribute = new Attribute();
        attribute.setAttributeTypeId(attributeType.getId());
        attribute.setObjectId(objectId);
        attribute.setAttributeId(1L);

        Long attributeValueTypeId = null;
        if (attributeType.getId().equals(ADDRESS)) {
            attributeValueTypeId = ADDRESS_APARTMENT;
        }
        attribute.setValueTypeId(attributeValueTypeId);
        return attribute;
    }

    @Transactional
    public boolean isLeafAddress(long addressId, String addressEntity) {
        boolean isLeaf = true;
        IStrategy addressStrategy = strategyFactory.getStrategy(addressEntity);
        String[] children = addressStrategy.getLogicalChildren();
        if (children != null && children.length > 0) {
            DomainObjectExample example = new DomainObjectExample();
            example.setParentId(addressId);
            example.setParentEntity(addressEntity);
            example.setAdmin(true);
            example.setStatus(ShowMode.ACTIVE.name());
            for (String child : children) {
                IStrategy childStrategy = strategyFactory.getStrategy(child);
                int count = childStrategy.count(example);
                if (count > 0) {
                    isLeaf = false;
                    break;
                }
            }
        }
        return isLeaf;
    }

    public long getAddressTypeId(String addressEntity) {
        if ("apartment".equals(addressEntity)) {
            return ADDRESS_APARTMENT;
        } else if ("room".equals(addressEntity)) {
            return ADDRESS_ROOM;
        } else if ("building".equals(addressEntity)) {
            return ADDRESS_BUILDING;
        }
        throw new IllegalStateException("Address entity `" + addressEntity + "` is not resolved.");
    }

    @Override
    public Registration newInstance() {
        return new Registration(super.newInstance());
    }

    @Transactional
    public Registration.Address loadAddress(String addressEntity, long addressId) {
        IStrategy addressStrategy = strategyFactory.getStrategy(addressEntity);
        DomainObject addressObject = addressStrategy.findById(addressId, true);
        SearchComponentState addressComponentState = new SearchComponentState();
        IStrategy.SimpleObjectInfo info = addressStrategy.findParentInSearchComponent(addressId, null);
        if (info != null) {
            addressComponentState = addressStrategy.getSearchComponentStateForParent(info.getId(), info.getEntityTable(), null);
            addressComponentState.put(addressEntity, addressObject);
        }
        DomainObject apartment = addressComponentState.get("apartment");
        apartment = apartment != null && apartment.getId() != null && apartment.getId() > 0 ? apartment : null;
        Building building = (Building) addressComponentState.get("building");
        building = building != null && building.getId() != null && building.getId() > 0 ? building : null;
        Long districtId = buildingStrategy.getDistrictId(building);
        DomainObject district = null;
        if (districtId != null) {
            IStrategy districtStrategy = strategyFactory.getStrategy("district");
            district = districtStrategy.findById(districtId, true);
            district = district != null && district.getId() != null && district.getId() > 0 ? district : null;
        }
        DomainObject street = addressComponentState.get("street");
        street = street != null && street.getId() != null && street.getId() > 0 ? street : null;
        DomainObject city = addressComponentState.get("city");
        city = city != null && city.getId() != null && city.getId() > 0 ? city : null;
        DomainObject region = addressComponentState.get("region");
        region = region != null && region.getId() != null && region.getId() > 0 ? region : null;
        DomainObject country = addressComponentState.get("country");
        country = country != null && country.getId() != null && country.getId() > 0 ? country : null;
        return new Registration.Address(country, region, district, city, street, building, apartment);
    }

    @Override
    public String[] getDescriptionRoles() {
        return new String[]{SecurityRole.PERSON_MODULE_DESCRIPTION_EDIT};
    }
}

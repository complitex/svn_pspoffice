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
import org.complitex.dictionary.converter.DateConverter;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.Strategy;
import org.complitex.dictionary.strategy.StrategyFactory;
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
    public static final long ARRIVAL_BUILDING = 2106;
    public static final long ARRIVAL_CORP = 2107;
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
    public static final long OWNER_RELATIONSHIP = 2120;
    public static final long OTHERS_RELATIONSHIP = 2121;
    public static final long HOUSING_RIGHTS = 2122;
    public static final long REGISTRATION_DATE = 2123;
    public static final long REGISTRATION_TYPE = 2124;
    /**
     * Attribute value type ids
     */
    public static final long ADDRESS_ROOM = 2100;
    public static final long ADDRESS_APARTMENT = 2101;
    public static final long ADDRESS_BUILDING = 2102;
    @EJB
    private StrategyFactory strategyFactory;
    @EJB
    private StringCultureBean stringBean;

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public boolean validateOrphans(long addressObjectId, String addressEntity) {
        boolean isOrphan = true;
        IStrategy addressStrategy = strategyFactory.getStrategy(addressEntity);
        String[] children = addressStrategy.getLogicalChildren();
        if (children != null && children.length > 0) {
            DomainObjectExample example = new DomainObjectExample();
            example.setParentId(addressObjectId);
            example.setParentEntity(addressEntity);
            for (String child : children) {
                IStrategy childStrategy = strategyFactory.getStrategy(child);
                int count = childStrategy.count(example);
                if (count > 0) {
                    isOrphan = false;
                }
            }
        }
        return isOrphan;
    }

    public String getAddressEntity(DomainObject registration) {
        Attribute registrationAddressAttribute = getAddressAttribute(registration);
        long addressTypeId = registrationAddressAttribute.getValueTypeId();
        if (addressTypeId == ADDRESS_APARTMENT) {
            return "apartment";
        } else if (addressTypeId == ADDRESS_BUILDING) {
            return "building";
        } else if (addressTypeId == ADDRESS_ROOM) {
            return "room";
        } else {
            throw new IllegalStateException("Address type is not resolved.");
        }
    }

    private Attribute getAddressAttribute(DomainObject registration) {
        Attribute addressAttribute = registration.getAttribute(ADDRESS);
        if (addressAttribute == null) {
            throw new IllegalStateException("Registration address attribute is null.");
        }
        return addressAttribute;
    }

    public long getAddressId(DomainObject registration) {
        Attribute registrationAddressAttribute = getAddressAttribute(registration);
        return registrationAddressAttribute.getValueId();
    }

    public Date getRegistrationDate(DomainObject registration) {
        Attribute registrationDateAttribute = registration.getAttribute(REGISTRATION_DATE);
        Date registrationDate = null;
        if (registrationDateAttribute != null) {
            String value = stringBean.getSystemStringCulture(registrationDateAttribute.getLocalizedValues()).getValue();
            registrationDate = value != null ? new DateConverter().toObject(value) : null;
        }
        return registrationDate;
    }

    public long getAddressTypeId(String addressEntity) {
        if ("apartment".equals(addressEntity)) {
            return ADDRESS_APARTMENT;
        } else if ("room".equals(addressEntity)) {
            return ADDRESS_ROOM;
        } else if ("building".equals(addressEntity)) {
            return ADDRESS_BUILDING;
        }
        throw new IllegalStateException("Address entity is not resolved.");
    }
}

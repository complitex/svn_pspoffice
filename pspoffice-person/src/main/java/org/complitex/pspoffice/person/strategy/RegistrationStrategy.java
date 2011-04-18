/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy;

import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
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
//    public static final long ARRIVAL = 2101;
    public static final long ADDRESS = 2100;
    public static final long ARRIVAL_REGION = 2101;
    public static final long ARRIVAL_DISTRICT = 2102;
    public static final long ARRIVAL_CITY = 2103;
    public static final long ARRIVAL_VILLAGE = 2104;
    public static final long ARRIVAL_STREET = 2105;
    public static final long ARRIVAL_BUILDING = 2106;
    public static final long ARRIVAL_CORP = 2107;
    public static final long ARRIVAL_APARTMENT = 2108;
    public static final long ARRIVAL_DATE = 2109;
    public static final long DEPARTURE_REGION = 2110;
    public static final long DEPARTURE_DISTRICT = 2111;
    public static final long DEPARTURE_CITY = 2112;
    public static final long DEPARTUREL_VILLAGE = 2113;
    public static final long DEPARTURE_DATE = 2114;
    public static final long OWNER_RELATIONSHIP = 2115;
    public static final long OTHERS_RELATIONSHIP = 2116;
    public static final long HOUSING_RIGHTS = 2117;

    //    public static final long DEPARTURE = 2102;
    /**
     * Attribute value type ids
     */
//    public static final long ARRIVAL_STRING = 2103;
//    public static final long ARRIVAL_ROOM = 2104;
//    public static final long ARRIVAL_APARTMENT = 2105;
//    public static final long ARRIVAL_BUILDING = 2106;
//    public static final long DEPARTURE_STRING = 2107;
//    public static final long DEPARTURE_ROOM = 2108;
//    public static final long DEPARTURE_APARTMENT = 2109;
//    public static final long DEPARTURE_BUILDING = 2110;
    public static final long ADDRESS_ROOM = 2100;
    public static final long ADDRESS_APARTMENT = 2101;
    public static final long ADDRESS_BUILDING = 2102;
    @EJB
    private StringCultureBean stringBean;
    @EJB
    private StrategyFactory strategyFactory;

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

//    @Transactional
//    @Override
//    protected void loadStringCultures(List<Attribute> attributes) {
//        for (Attribute attribute : attributes) {
//            if (attribute.getAttributeTypeId().equals(ARRIVAL)) {
//                if (attribute.getValueTypeId().equals(ARRIVAL_STRING)) {
//                    loadStringCultures(attribute);
//                }
//            } else if (attribute.getAttributeTypeId().equals(DEPARTURE)) {
//                if (attribute.getValueTypeId().equals(DEPARTURE_STRING)) {
//                    loadStringCultures(attribute);
//                }
//            } else if (isSimpleAttribute(attribute)) {
//                if (attribute.getValueId() != null) {
//                    loadStringCultures(attribute);
//                } else {
//                    attribute.setLocalizedValues(stringBean.newStringCultures());
//                }
//            }
//        }
//    }

    @Override
    protected Attribute fillManyValueTypesAttribute(EntityAttributeType attributeType, Long objectId) {
        Attribute attribute = new Attribute();
        attribute.setAttributeTypeId(attributeType.getId());
        attribute.setObjectId(objectId);
        attribute.setAttributeId(1L);

        Long attributeValueTypeId = null;
//        if (attributeType.getId().equals(ARRIVAL)) {
//            attributeValueTypeId = ARRIVAL_STRING;
//            attribute.setLocalizedValues(stringBean.newStringCultures());
//        } else if (attributeType.getId().equals(DEPARTURE)) {
//            attributeValueTypeId = DEPARTURE_STRING;
//            attribute.setLocalizedValues(stringBean.newStringCultures());
//        } else 
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
}

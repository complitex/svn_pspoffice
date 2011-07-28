/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy;

import com.google.common.collect.ImmutableMap;
import java.util.Date;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.ImmutableList.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.entity.description.EntityAttributeValueType;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@Stateless
public class ApartmentCardStrategy extends TemplateStrategy {

    private static final String APARTMENT_CARD_MAPPING = ApartmentCardStrategy.class.getPackage().getName() + ".ApartmentCard";
    public static final String RESOURCE_BUNDLE = ApartmentCardStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long PERSONAL_ACCOUNT = 2400;
    public static final long OWNER = 2401;
    public static final long ADDRESS = 2402;
    public static final long FORM_OF_OWNERSHIP = 2403;
    public static final long HOUSING_RIGHTS = 2404;
    public static final long REGISTRATIONS = 2405;
    /**
     * Attribute value type ids
     */
    public static final long PERSONAL_ACCOUNT_TYPE = 2400;
    public static final long OWNER_TYPE = 2401;
    public static final long ADDRESS_ROOM = 2402;
    public static final long ADDRESS_APARTMENT = 2403;
    public static final long ADDRESS_BUILDING = 2404;
    public static final long FORM_OF_OWNERSHIP_TYPE = 2405;
    public static final long HOUSING_RIGHTS_TYPE = 2406;
    public static final long REGISTRATIONS_TYPE = 2407;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private RegistrationStrategy registrationStrategy;
    @EJB
    private StringCultureBean stringBean;
    @EJB
    private StrategyFactory strategyFactory;

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getEntityTable() {
        return "apartment_card";
    }

    @Override
    protected List<Long> getListAttributeTypes() {
        return of(PERSONAL_ACCOUNT);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.PERSON_MODULE_EDIT};
    }

    @Override
    public ApartmentCard newInstance() {
        return new ApartmentCard(super.newInstance());
    }

    @Transactional
    @Override
    public ApartmentCard findById(long id, boolean runAsAdmin) {
        return findById(id, runAsAdmin, true, true);
    }

    private ApartmentCard findById(long id, boolean runAsAdmin, boolean loadOwner, boolean loadRegistrations) {
        DomainObject apartmentCardObject = super.findById(id, runAsAdmin);
        if (apartmentCardObject == null) {
            return null;
        }
        ApartmentCard apartmentCard = new ApartmentCard(apartmentCardObject);
        if (loadOwner) {
            loadOwner(apartmentCard);
        }
        if (loadRegistrations) {
            loadAllRegistrations(apartmentCard);
        }
        return apartmentCard;
    }

    @Transactional
    private void loadOwner(ApartmentCard apartmentCard) {
        long ownerId = apartmentCard.getAttribute(OWNER).getValueId();
        Person owner = personStrategy.findById(ownerId, true);
        personStrategy.loadName(owner);
        apartmentCard.setOwner(owner);
    }

    @Override
    protected void fillAttributes(DomainObject object) {
        List<Attribute> toAdd = newArrayList();
        for (EntityAttributeType attributeType : getEntity().getEntityAttributeTypes()) {
            if (!attributeType.isObsolete()) {
                if (object.getAttributes(attributeType.getId()).isEmpty()) {
                    if (!attributeType.getId().equals(REGISTRATIONS)) {
                        if ((attributeType.getEntityAttributeValueTypes().size() == 1) && !attributeType.getId().equals(REGISTRATIONS)) {
                            Attribute attribute = new Attribute();
                            EntityAttributeValueType attributeValueType = attributeType.getEntityAttributeValueTypes().get(0);
                            attribute.setAttributeTypeId(attributeType.getId());
                            attribute.setValueTypeId(attributeValueType.getId());
                            attribute.setObjectId(object.getId());
                            attribute.setAttributeId(1L);

                            if (isSimpleAttributeType(attributeType)) {
                                attribute.setLocalizedValues(stringBean.newStringCultures());
                            }
                            toAdd.add(attribute);
                        } else {
                            Attribute manyValueTypesAttribute = fillManyValueTypesAttribute(attributeType, object.getId());
                            if (manyValueTypesAttribute != null) {
                                toAdd.add(manyValueTypesAttribute);
                            }
                        }
                    }
                }
            }
        }
        if (!toAdd.isEmpty()) {
            object.getAttributes().addAll(toAdd);
        }
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

    @Transactional
    public int countByAddress(String addressEntity, long addressId) {
        checkEntity(addressEntity);
        addressEntity = Strings.capitalize(addressEntity);
        Map<String, Long> params = ImmutableMap.of("addressId", addressId);
        return (Integer) sqlSession().selectOne(APARTMENT_CARD_MAPPING + ".countBy" + addressEntity, params);
    }

    private void checkEntity(String addressEntity) {
        if (!"apartment".equals(addressEntity) && !"building".equals(addressEntity)) {
            throw new IllegalArgumentException("Address entity expected to be of 'apartment' or 'building'. But was: " + addressEntity);
        }
    }

    @Transactional
    public ApartmentCard findOneByAddress(String addressEntity, long addressId) {
        return findByAddress(addressEntity, addressId, 0, 1, true).get(0);
    }

    @Transactional
    public List<ApartmentCard> findByAddress(String addressEntity, long addressId, int start, int size) {
        return findByAddress(addressEntity, addressId, start, size, false);
    }

    private List<ApartmentCard> findByAddress(String addressEntity, long addressId, int start, int size, boolean loadRegistrations) {
        checkEntity(addressEntity);
        Map<String, Object> params = ImmutableMap.<String, Object>of("addressId", addressId, "start", start, "size", size);
        addressEntity = Strings.capitalize(addressEntity);
        List<Long> apartmentCardIds = sqlSession().selectList(APARTMENT_CARD_MAPPING + ".findBy" + addressEntity, params);
        List<ApartmentCard> apartmentCards = newArrayList();
        for (Long apartmentCardId : apartmentCardIds) {
            apartmentCards.add(findById(apartmentCardId, false, true, loadRegistrations));
        }
        return apartmentCards;
    }

    @Transactional
    public void loadAllRegistrations(ApartmentCard apartmentCard) {
        List<Attribute> registrationAttributes = apartmentCard.getAttributes(REGISTRATIONS);
        if (registrationAttributes != null && !registrationAttributes.isEmpty()) {
            for (Attribute registratioAttribute : registrationAttributes) {
                long registrationId = registratioAttribute.getValueId();
                Registration registration = registrationStrategy.findById(registrationId, false);
                if (registration == null) {
                    //find history registration
                    registration = registrationStrategy.findHistoryObject(registrationId, DateUtil.getCurrentDate());
                }
                apartmentCard.addRegistration(registration);
            }
        }
    }

    @Transactional
    public void addRegistration(long apartmentCardId, Registration registration, Date insertDate) {
        registrationStrategy.insert(registration, insertDate);
        insertRegistrationAttribute(apartmentCardId, registration.getId(), insertDate);
    }

    @Transactional
    private void insertRegistrationAttribute(long apartmentCardId, long registrationId, Date insertDate) {
        Attribute registrationAttribute = new Attribute();
        registrationAttribute.setObjectId(apartmentCardId);
        ApartmentCard apartmentCard = findById(apartmentCardId, true, false, false);
        List<Attribute> registrationAttributes = apartmentCard.getAttributes(REGISTRATIONS);
        registrationAttribute.setAttributeId(registrationAttributes != null ? registrationAttributes.size() + 1 : 1L);
        registrationAttribute.setAttributeTypeId(REGISTRATIONS);
        registrationAttribute.setValueTypeId(REGISTRATIONS_TYPE);
        registrationAttribute.setValueId(registrationId);
        registrationAttribute.setStartDate(insertDate);
        insertAttribute(registrationAttribute);
    }
}

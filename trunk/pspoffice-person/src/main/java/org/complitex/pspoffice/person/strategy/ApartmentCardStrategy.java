/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy;

import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Comparator;
import static com.google.common.collect.Maps.*;
import java.util.Date;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.ImmutableList.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.converter.DateConverter;
import org.complitex.dictionary.converter.StringConverter;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.entity.description.EntityAttributeValueType;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.web.DictionaryFwSession;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.RegisterChildrenCard;
import org.complitex.pspoffice.person.strategy.entity.RegisterOwnerCard;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.person.strategy.entity.RemoveRegistrationCard;
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
    /**
     * Set of persistable search state entities
     */
    private static final Set<String> SEARCH_STATE_ENTITES = ImmutableSet.of("country", "region", "city");
    /**
     * Full address search component state enabled key
     */
    private static final MetaDataKey<Boolean> FULL_ADDRESS_ENABLED_KEY = new MetaDataKey<Boolean>() {
    };
    /**
     * Apartment card full address enabled page
     */
    private static final String FULL_ADDRESS_ENABLED_PAGE = "APARTMENT_CARD_FULL_ADDRESS_ENABLED_PAGE";
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private RegistrationStrategy registrationStrategy;
    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;
    @EJB
    private StringCultureBean stringBean;
    @EJB
    private StrategyFactory strategyFactory;
    @EJB
    private SessionBean sessionBean;

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
        return new String[]{SecurityRole.AUTHORIZED};
    }

    @Override
    public ApartmentCard newInstance() {
        ApartmentCard apartmentCard = new ApartmentCard(super.newInstance());
        apartmentCard.getSubjectIds().clear();
        return apartmentCard;
    }

    @Transactional
    @Override
    public ApartmentCard findById(long id, boolean runAsAdmin) {
        return findById(id, runAsAdmin, true, true, true);
    }

    public ApartmentCard findById(long id, boolean runAsAdmin, boolean loadOwner, boolean loadRegistrations,
            boolean loadOwnershipForm) {
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
        if (loadOwnershipForm) {
            loadOwnershipForm(apartmentCard);
        }
        return apartmentCard;
    }

    @Transactional
    private void loadOwnershipForm(ApartmentCard apartmentCard) {
        long ownershipFormId = apartmentCard.getAttribute(FORM_OF_OWNERSHIP).getValueId();
        DomainObject ownershipForm = ownershipFormStrategy.findById(ownershipFormId, true);
        apartmentCard.setOwnershipForm(ownershipForm);
    }

    @Transactional
    private void loadOwner(ApartmentCard apartmentCard) {
        long ownerId = apartmentCard.getAttribute(OWNER).getValueId();
        Person owner = personStrategy.findPersonById(ownerId, true, true, false, false);
        apartmentCard.setOwner(owner);
    }

    public static String getAddressEntity(long addressValueTypeId) {
        if (ADDRESS_ROOM == addressValueTypeId) {
            return "room";
        } else if (ADDRESS_APARTMENT == addressValueTypeId) {
            return "apartment";
        } else if (ADDRESS_BUILDING == addressValueTypeId) {
            return "building";
        } else {
            throw new IllegalStateException("Address attribute expected to be of " + ADDRESS_ROOM + " or "
                    + ADDRESS_APARTMENT + " or " + ADDRESS_BUILDING + ". But was: " + addressValueTypeId);
        }
    }

    public static String getAddressEntity(ApartmentCard apartmentCard) {
        long valueTypeId = apartmentCard.getAttribute(ADDRESS).getValueTypeId();
        return getAddressEntity(valueTypeId);
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

    private Map<String, Object> newSearchByAddressParams(long addressId) {
        Map<String, Object> params = newHashMap();
        params.put("addressId", addressId);
        if (!sessionBean.isAdmin()) {
            params.put("userPermissionString", sessionBean.getPermissionString(getEntityTable()));
        }
        return params;
    }

    @Transactional
    public int countByAddress(String addressEntity, long addressId) {
        checkEntity(addressEntity);
        addressEntity = Strings.capitalize(addressEntity);
        Map<String, Object> params = newSearchByAddressParams(addressId);
        return (Integer) sqlSession().selectOne(APARTMENT_CARD_MAPPING + ".countBy" + addressEntity, params);
    }

    private void checkEntity(String addressEntity) {
        if (!"apartment".equals(addressEntity) && !"building".equals(addressEntity)) {
            throw new IllegalArgumentException("Address entity expected to be of 'apartment' or 'building'. But was: " + addressEntity);
        }
    }

    @Transactional
    public ApartmentCard findOneByAddress(String addressEntity, long addressId) {
        return findByAddress(addressEntity, addressId, 0, 1).get(0);
    }

    @Transactional
    public List<ApartmentCard> findByAddress(String addressEntity, long addressId, int start, int size) {
        checkEntity(addressEntity);
        Map<String, Object> params = newSearchByAddressParams(addressId);
        params.put("start", start);
        params.put("size", size);
        addressEntity = Strings.capitalize(addressEntity);
        List<Long> apartmentCardIds = sqlSession().selectList(APARTMENT_CARD_MAPPING + ".findBy" + addressEntity, params);
        List<ApartmentCard> apartmentCards = newArrayList();
        for (Long apartmentCardId : apartmentCardIds) {
            apartmentCards.add(findById(apartmentCardId, false, true, true, true));
        }
        return apartmentCards;
    }

    /**
     * Util method to get apartment for apartment and room based apartment cards.
     * @param apartmentCard
     * @return
     */
    @Transactional
    public Long getApartmentId(ApartmentCard apartmentCard) {
        Long apartmentId = null;

        String addressEntity = getAddressEntity(apartmentCard);
        long addressId = apartmentCard.getAddressId();
        if (addressEntity.equals("room")) {
            DomainObject room = strategyFactory.getStrategy("room").findById(addressId, true);
            if (room.getParentEntityId().equals(100L)) { //parent is apartment
                apartmentId = room.getParentId();
            }
        } else if (addressEntity.equals("apartment")) {
            apartmentId = addressId;
        }

        return apartmentId;
    }

    @Transactional
    public List<ApartmentCard> getNeighbourApartmentCards(ApartmentCard apartmentCard) {
        Long apartmentId = getApartmentId(apartmentCard);

        if (apartmentId == null) {
            return null;
        }

        List<ApartmentCard> neighbourApartmentCards = newArrayList();
        int count = countByAddress("apartment", apartmentId);
        for (ApartmentCard neighbourCard : findByAddress("apartment", apartmentId, 0, count)) {
            if (!neighbourCard.getId().equals(apartmentCard.getId())) {
                neighbourApartmentCards.add(neighbourCard);
            }
        }
        return neighbourApartmentCards;
    }

    @Transactional
    private void loadAllRegistrations(ApartmentCard apartmentCard) {
        List<Registration> registrations = newArrayList();
        List<Attribute> registrationAttributes = apartmentCard.getAttributes(REGISTRATIONS);
        if (registrationAttributes != null && !registrationAttributes.isEmpty()) {
            for (Attribute registrationAttribute : registrationAttributes) {
                long registrationId = registrationAttribute.getValueId();
                registrations.add(registrationStrategy.findRegistrationById(registrationId, true, true, true, true));
            }
        }
        if (!registrations.isEmpty()) {
            Collections.sort(registrations, new Comparator<Registration>() {

                @Override
                public int compare(Registration o1, Registration o2) {
                    if (o1.isFinished() && o2.isFinished()) {
                        Date d1 = o1.getDepartureDate();
                        if (d1 == null) {
                            d1 = o1.getEndDate();
                        }
                        Date d2 = o2.getDepartureDate();
                        if (d2 == null) {
                            d2 = o2.getEndDate();
                        }
                        return d2.compareTo(d1);
                    }
                    if (o1.isFinished() || o2.isFinished()) {
                        return o1.isFinished() ? 1 : -1;
                    }

                    Date d1 = o1.getRegistrationDate();
                    Date d2 = o2.getRegistrationDate();
                    return d2.compareTo(d1);
                }
            });
        }
        apartmentCard.setRegistrations(registrations);
    }

    @Transactional
    private void addRegistration(ApartmentCard apartmentCard, Registration registration, long attributeId, Date insertDate) {
        registration.setSubjectIds(apartmentCard.getSubjectIds());
        registrationStrategy.insert(registration, insertDate);
        insertRegistrationAttribute(apartmentCard, registration.getId(), attributeId, insertDate);
    }

    @Transactional
    public void addRegistration(ApartmentCard apartmentCard, Registration registration, Date insertDate) {
        long attributeId = apartmentCard.getAttributes(REGISTRATIONS).size() + 1;
        addRegistration(apartmentCard, registration, attributeId, insertDate);
    }

    @Transactional
    private void insertRegistrationAttribute(ApartmentCard apartmentCard, long registrationId, long attributeId, Date insertDate) {
        Attribute registrationAttribute = new Attribute();
        registrationAttribute.setObjectId(apartmentCard.getId());
        registrationAttribute.setAttributeId(attributeId);
        registrationAttribute.setAttributeTypeId(REGISTRATIONS);
        registrationAttribute.setValueTypeId(REGISTRATIONS_TYPE);
        registrationAttribute.setValueId(registrationId);
        registrationAttribute.setStartDate(insertDate);
        insertAttribute(registrationAttribute);
    }

    @Transactional
    public void removeRegistrations(List<Registration> removeRegistrations, RemoveRegistrationCard removeRegistrationCard) {
        Date archiveTime = DateUtil.getCurrentDate();
        Date updateRegistrationsTime = DateUtil.justBefore(archiveTime);

        for (Registration registration : removeRegistrations) {
            Registration newRegistration = CloneUtil.cloneObject(registration);
            //departure reason
            stringBean.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_REASON).getLocalizedValues()).
                    setValue(removeRegistrationCard.getReason());
            //departure date
            stringBean.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_DATE).getLocalizedValues()).
                    setValue(new DateConverter().toString(removeRegistrationCard.getDate()));
            //departure address
            stringBean.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_COUNTRY).getLocalizedValues()).
                    setValue(removeRegistrationCard.getCountry());
            stringBean.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_REGION).getLocalizedValues()).
                    setValue(removeRegistrationCard.getRegion());
            stringBean.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_DISTRICT).getLocalizedValues()).
                    setValue(removeRegistrationCard.getDistrict());
            stringBean.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_CITY).getLocalizedValues()).
                    setValue(removeRegistrationCard.getCity());
            stringBean.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_STREET).getLocalizedValues()).
                    setValue(removeRegistrationCard.getStreet());
            stringBean.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_BUILDING_NUMBER).getLocalizedValues()).
                    setValue(removeRegistrationCard.getBuildingNumber());
            stringBean.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_BUILDING_CORP).getLocalizedValues()).
                    setValue(removeRegistrationCard.getBuildingCorp());
            stringBean.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_APARTMENT).getLocalizedValues()).
                    setValue(removeRegistrationCard.getApartment());

            registrationStrategy.update(registration, newRegistration, updateRegistrationsTime);
            registrationStrategy.archive(newRegistration, archiveTime);
        }
    }

    @Transactional
    public void storeSearchState(DictionaryFwSession session, SearchComponentState searchComponentState) {
        SearchComponentState globalSearchComponentState = session.getGlobalSearchComponentState();
        globalSearchComponentState.updateState(searchComponentState);
        session.storeGlobalSearchComponentState();
    }

    @Transactional
    public SearchComponentState restoreSearchState(DictionaryFwSession session) {
        SearchComponentState globalSearchComponentState = session.getGlobalSearchComponentState();
        DomainObject defaultStreetObject = session.getPreferenceDomainObject(DictionaryFwSession.DEFAULT_STATE_PAGE, "street");
        boolean useDefault = session.getPreferenceBoolean(DictionaryFwSession.GLOBAL_PAGE, DictionaryFwSession.IS_USE_DEFAULT_STATE_KEY);
        SearchComponentState searchComponentState = new SearchComponentState();
        for (Map.Entry<String, DomainObject> searchFilterEntry : globalSearchComponentState.entrySet()) {
            String searchFilter = searchFilterEntry.getKey();
            DomainObject filterObject = searchFilterEntry.getValue();
            if (SEARCH_STATE_ENTITES.contains(searchFilter)
                    || (searchFilter.equals("street") && useDefault && isEqualStreet(defaultStreetObject, filterObject))) {
                if (filterObject != null && filterObject.getId() != null && filterObject.getId() > 0) {
                    searchComponentState.put(searchFilter, filterObject);
                }
            }
        }
        return searchComponentState;
    }

    private boolean isEqualStreet(DomainObject street1, DomainObject street2) {
        if (street1 == null || street1.getId() == null || street1.getId() <= 0
                || street2 == null || street2.getId() == null || street2.getId() <= 0) {
            return false;
        }
        return street1.getId().equals(street2.getId());
    }

    @Transactional
    public boolean isFullAddressEnabled(DictionaryFwSession session) {
        Boolean fullAddressSearchStateEnabled = session.getMetaData(FULL_ADDRESS_ENABLED_KEY);
        if (fullAddressSearchStateEnabled == null) {
            fullAddressSearchStateEnabled = session.getPreferenceBoolean(FULL_ADDRESS_ENABLED_PAGE, FULL_ADDRESS_ENABLED_PAGE);
            if (fullAddressSearchStateEnabled != null) {
                session.setMetaData(FULL_ADDRESS_ENABLED_KEY, fullAddressSearchStateEnabled);
            }
        }
        return fullAddressSearchStateEnabled != null ? fullAddressSearchStateEnabled : true;
    }

    @Transactional
    public void storeFullAddressEnabled(DictionaryFwSession session, boolean fullAddressSearchStateEnabled) {
        session.setMetaData(FULL_ADDRESS_ENABLED_KEY, fullAddressSearchStateEnabled);
        session.putPreference(FULL_ADDRESS_ENABLED_PAGE, FULL_ADDRESS_ENABLED_PAGE,
                String.valueOf(fullAddressSearchStateEnabled), true);
    }

    @Transactional
    public void changeRegistrationType(long apartmentCardId, List<Registration> registrationsToChangeType, long newRegistrationTypeId) {
        Date updateDate = DateUtil.getCurrentDate();
        for (Registration registration : registrationsToChangeType) {
            if (!registration.getRegistrationType().getId().equals(newRegistrationTypeId)) {
                Registration newRegistration = CloneUtil.cloneObject(registration);
                newRegistration.getAttribute(RegistrationStrategy.REGISTRATION_TYPE).setValueId(newRegistrationTypeId);
                registrationStrategy.update(registration, newRegistration, updateDate);
            }
        }
    }

    @Transactional
    public void registerOwner(ApartmentCard apartmentCard, RegisterOwnerCard registerOwnerCard) {
        Date insertDate = DateUtil.getCurrentDate();

        long attributeId = apartmentCard.getAttributes(REGISTRATIONS).size() + 1;
        Person owner = apartmentCard.getOwner();
        //owner registration
        addRegistration(apartmentCard,
                newRegistration(owner.getId(), registerOwnerCard, OwnerRelationshipStrategy.OWNER), attributeId++, insertDate);

        //children registration
        if (registerOwnerCard.isRegisterChildren()) {
            for (Attribute childAttribute : owner.getAttributes(PersonStrategy.CHILDREN)) {
                addRegistration(apartmentCard,
                        newRegistration(childAttribute.getValueId(), registerOwnerCard, OwnerRelationshipStrategy.CHILDREN),
                        attributeId++, insertDate);
            }
        }
    }

    private Registration newRegistration(long personId, RegisterOwnerCard registerOwnerCard, long ownerRelationshipId) {
        Registration registration = registrationStrategy.newInstance();
        registration.getAttribute(RegistrationStrategy.PERSON).setValueId(personId);
        registration.getAttribute(RegistrationStrategy.OWNER_RELATIONSHIP).setValueId(ownerRelationshipId);
        stringBean.getSystemStringCulture(registration.getAttribute(RegistrationStrategy.REGISTRATION_DATE).getLocalizedValues()).
                setValue(new DateConverter().toString(registerOwnerCard.getRegistrationDate()));
        registration.getAttribute(RegistrationStrategy.REGISTRATION_TYPE).setValueId(registerOwnerCard.getRegistrationType().getId());
        stringBean.getSystemStringCulture(registration.getAttribute(RegistrationStrategy.ARRIVAL_COUNTRY).getLocalizedValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getCountry()));
        stringBean.getSystemStringCulture(registration.getAttribute(RegistrationStrategy.ARRIVAL_REGION).getLocalizedValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getRegion()));
        stringBean.getSystemStringCulture(registration.getAttribute(RegistrationStrategy.ARRIVAL_DISTRICT).getLocalizedValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getDistrict()));
        stringBean.getSystemStringCulture(registration.getAttribute(RegistrationStrategy.ARRIVAL_CITY).getLocalizedValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getCity()));
        stringBean.getSystemStringCulture(registration.getAttribute(RegistrationStrategy.ARRIVAL_STREET).getLocalizedValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getStreet()));
        stringBean.getSystemStringCulture(registration.getAttribute(RegistrationStrategy.ARRIVAL_BUILDING_NUMBER).getLocalizedValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getBuildingNumber()));
        stringBean.getSystemStringCulture(registration.getAttribute(RegistrationStrategy.ARRIVAL_BUILDING_CORP).getLocalizedValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getBuildingCorp()));
        stringBean.getSystemStringCulture(registration.getAttribute(RegistrationStrategy.ARRIVAL_APARTMENT).getLocalizedValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getApartment()));
        stringBean.getSystemStringCulture(registration.getAttribute(RegistrationStrategy.ARRIVAL_DATE).getLocalizedValues()).
                setValue(new DateConverter().toString(registerOwnerCard.getArrivalDate()));
        return registration;
    }

    @Transactional
    public boolean validateOwnerAddressUniqueness(long addressId, long addressTypeId, long ownerId, Long apartmentCardId) {
        Map<String, Long> params = newHashMap();
        params.put("apartmentCardAddressAT", ADDRESS);
        params.put("addressId", addressId);
        params.put("addressTypeId", addressTypeId);
        params.put("apartmentCardOwnerAT", OWNER);
        params.put("ownerId", ownerId);
        params.put("apartmentCardId", apartmentCardId);
        return sqlSession().selectOne(APARTMENT_CARD_MAPPING + ".validateOwnerAddressUniqueness", params) == null;
    }

    @Transactional
    public void registerChildren(RegisterChildrenCard registerChildrenCard, List<Person> children) {
        Date insertDate = DateUtil.getCurrentDate();
        ApartmentCard apartmentCard = findById(registerChildrenCard.getApartmentCardId(), true, false, false, false);
        long attributeId = apartmentCard.getAttributes(REGISTRATIONS).size() + 1;
        for (Person child : children) {
            addRegistration(apartmentCard, newChildRegistration(child.getId(), registerChildrenCard), attributeId++, insertDate);
        }
    }

    private Registration newChildRegistration(long childId, RegisterChildrenCard registerChildrenCard) {
        Registration registration = registrationStrategy.newInstance();
        registration.getAttribute(RegistrationStrategy.PERSON).setValueId(childId);
        registration.getAttribute(RegistrationStrategy.OWNER_RELATIONSHIP).setValueId(OwnerRelationshipStrategy.CHILDREN);
        stringBean.getSystemStringCulture(registration.getAttribute(RegistrationStrategy.REGISTRATION_DATE).getLocalizedValues()).
                setValue(new DateConverter().toString(registerChildrenCard.getRegistrationDate()));
        registration.getAttribute(RegistrationStrategy.REGISTRATION_TYPE).setValueId(registerChildrenCard.getRegistrationType().getId());
        return registration;
    }
}

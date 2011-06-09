package org.complitex.pspoffice.person.strategy;

import static com.google.common.collect.ImmutableMap.*;
import java.util.Collections;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import java.util.Date;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.service.StringCultureBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.converter.BooleanConverter;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.entity.description.EntityAttributeValueType;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.NameBean;
import org.complitex.dictionary.service.PermissionBean;
import org.complitex.dictionary.strategy.DeleteException;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.web.edit.PersonEdit;
import org.complitex.pspoffice.person.strategy.web.list.PersonList;
import org.complitex.pspoffice.person.strategy.entity.FullName;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.person.strategy.util.FullNameParser;
import org.complitex.pspoffice.person.strategy.web.history.PersonHistoryPage;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@Stateless
public class PersonStrategy extends TemplateStrategy {

    private static final String PERSON_MAPPING = PersonStrategy.class.getPackage().getName() + ".Person";
    public static final String RESOURCE_BUNDLE = PersonStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long LAST_NAME = 2000;
    public static final long FIRST_NAME = 2001;
    public static final long MIDDLE_NAME = 2002;
    public static final long NATIONALITY = 2003;
    public static final long BIRTH_DATE = 2004;
    public static final long BIRTH_COUNTRY = 2005;
    public static final long BIRTH_REGION = 2006;
    public static final long BIRTH_DISTRICT = 2007;
    public static final long BIRTH_CITY = 2008;
    public static final long PASSPORT_SERIAL_NUMBER = 2009;
    public static final long PASSPORT_NUMBER = 2010;
    public static final long PASSPORT_ACQUISITION_ORGANIZATION = 2011;
    public static final long PASSPORT_ACQUISITION_DATE = 2012;
    public static final long JOB_INFO = 2013;
    public static final long MILITARY_SERVISE_RELATION = 2014;
    public static final long REGISTRATION = 2015;
    public static final long CHILDREN = 2016;
    public static final long GENDER = 2017;
    public static final long BIRTH_CERTIFICATE_INFO = 2018;
    public static final long BIRTH_CERTIFICATE_ACQUISITION_DATE = 2019;
    public static final long BIRTH_CERTIFICATE_ACQUISITION_ORGANIZATION = 2020;
    public static final long UKRAINE_CITIZENSHIP = 2021;

    /**
     * Order by related constants
     */
    public static enum OrderBy {

        LAST_NAME(PersonStrategy.LAST_NAME), FIRST_NAME(PersonStrategy.FIRST_NAME), MIDDLE_NAME(PersonStrategy.MIDDLE_NAME);
        private Long orderByAttributeId;

        private OrderBy(Long orderByAttributeId) {
            this.orderByAttributeId = orderByAttributeId;
        }

        public Long getOrderByAttributeId() {
            return orderByAttributeId;
        }
    }
    /**
     * Filter constants
     */
    public static final String LAST_NAME_FILTER = "last_name";
    public static final String FIRST_NAME_FILTER = "first_name";
    public static final String MIDDLE_NAME_FILTER = "middle_name";
    public static final long DEFAULT_ORDER_BY_ID = -1;
    @EJB
    private StringCultureBean stringBean;
    @EJB
    private RegistrationStrategy registrationStrategy;
    @EJB
    private NameBean nameBean;

    @Override
    public String getEntityTable() {
        return "person";
    }

    @Override
    public Class<? extends WebPage> getListPage() {
        return PersonList.class;
    }

    @Override
    public PageParameters getListPageParams() {
        return PageParameters.NULL;
    }

    @Override
    public Class<? extends WebPage> getEditPage() {
        return PersonEdit.class;
    }

    @Override
    public PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity) {
        PageParameters params = new PageParameters();
        params.put(TemplateStrategy.OBJECT_ID, objectId);
        return params;
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.PERSON_MODULE_EDIT};
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        Person person = (Person) object;
        return displayPerson(person.getFirstName(), person.getMiddleName(), person.getLastName());
    }

    public String displayPerson(String firstName, String middleName, String lastName) {
        // return in format 'last_name fisrt_name middle_name'
        return lastName + " " + firstName + " " + middleName;
    }

    @Transactional
    @Override
    public List<Person> find(DomainObjectExample example) {
        if (example.getId() != null && example.getId() <= 0) {
            return Collections.emptyList();
        }

        example.setTable(getEntityTable());
        if (!example.isAdmin()) {
            prepareExampleForPermissionCheck(example);
        }

        List<Person> persons = sqlSession().selectList(PERSON_MAPPING + "." + FIND_OPERATION, example);
        for (Person person : persons) {
            loadAttributes(person);
        }
        return persons;
    }

    @Override
    public void configureExample(DomainObjectExample example, Map<String, Long> ids, String searchTextInput) {
        FullName fullName = FullNameParser.parse(searchTextInput);
        if (fullName != null) {
            example.addAdditionalParam(LAST_NAME_FILTER, fullName.getLastName());
            if (fullName.getFirstName() != null) {
                example.addAdditionalParam(FIRST_NAME_FILTER, fullName.getFirstName());
            }
            if (fullName.getMiddleName() != null) {
                example.addAdditionalParam(MIDDLE_NAME_FILTER, fullName.getMiddleName());
            }
        } else {
            //setup intentionally false criteria
            example.setId(-1L);
        }
    }

    @Transactional
    @Override
    public int count(DomainObjectExample example) {
        if (example.getId() != null && example.getId() <= 0) {
            return 0;
        }

        example.setTable(getEntityTable());
        prepareExampleForPermissionCheck(example);
        return (Integer) sqlSession().selectOne(PERSON_MAPPING + "." + COUNT_OPERATION, example);
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(PersonStrategy.class.getName(), getEntityTable(), locale);
    }

    @Override
    public Person newInstance() {
        Person person = new Person();
        fillAttributes(person);
        person.setRegistration(registrationStrategy.newInstance());

        //set up subject ids to visible-by-all subject
        Set<Long> defaultSubjectIds = newHashSet(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID);
        person.setSubjectIds(defaultSubjectIds);

        return person;
    }

    @Transactional
    @Override
    protected void insertDomainObject(DomainObject object, Date insertDate) {
        Person person = (Person) object;
        DomainObject registration = person.getRegistration();
        registrationStrategy.insert(registration, insertDate);
        person.getAttribute(REGISTRATION).setValueId(registration.getId());
        person.updateChildrenAttributes();
        super.insertDomainObject(person, insertDate);
    }

    @Transactional
    @Override
    protected void insertUpdatedDomainObject(DomainObject object, Date updateDate) {
        super.insertDomainObject(object, updateDate);
    }

    @Transactional
    @Override
    public void update(DomainObject oldObject, DomainObject newObject, Date updateDate) {
        Person oldPerson = (Person) oldObject;
        Person newPerson = (Person) newObject;

        DomainObject oldRegistration = oldPerson.getRegistration();
        DomainObject newRegistration = newPerson.getRegistration();
        DomainObject changedRegistration = newPerson.getChangedRegistration();

        if (oldRegistration == null) {
            if (newRegistration != null) {
                newRegistration.setSubjectIds(newPerson.getSubjectIds());
                registrationStrategy.insert(newRegistration, updateDate);
                newPerson.getAttribute(REGISTRATION).setValueId(newRegistration.getId());
            } else {
                // do nothing
            }
        } else {
            if (changedRegistration == null) {
                Date updateRegistrationDate = newPerson.isRegistrationStopped() ? DateUtil.justBefore(updateDate) : updateDate;
                registrationStrategy.update(oldRegistration, newRegistration, updateRegistrationDate);
                if (newPerson.isRegistrationStopped()) {
                    registrationStrategy.archive(newRegistration, updateDate);
                    newPerson.getAttribute(REGISTRATION).setValueId(null);
                }
            } else {
                Date updateRegistrationDate = DateUtil.justBefore(updateDate);
                registrationStrategy.update(oldRegistration, newRegistration, updateRegistrationDate);
                changedRegistration.setSubjectIds(newPerson.getSubjectIds());
                registrationStrategy.insert(changedRegistration, updateDate);
                newPerson.getAttribute(REGISTRATION).setValueId(changedRegistration.getId());
                registrationStrategy.archive(newRegistration, updateDate);
            }
        }

        newPerson.updateChildrenAttributes();
        super.update(oldObject, newObject, updateDate);
    }

    @Transactional
    @Override
    public Person findById(long id, boolean runAsAdmin) {
        return findById(id, runAsAdmin, true, true);
    }

    @Transactional
    protected Person findById(long id, boolean runAsAdmin, boolean loadChildren, boolean loadRegistration) {
        DomainObject personObject = super.findById(id, runAsAdmin);
        if (personObject == null) {
            return null;
        }
        Person person = new Person(personObject);
        if (loadRegistration) {
            loadRegistration(person, null);
        }
        if (loadChildren) {
            loadChildren(person);
        }
        return person;
    }

    @Transactional
    private void loadRegistration(Person person, Date date) {
        Attribute registrationAttribute = person.getAttribute(REGISTRATION);
        if (registrationAttribute != null) {
            Long registrationId = registrationAttribute.getValueId();
            if (registrationId != null) {
                Registration registration = null;
                if (date == null) {
                    registration = registrationStrategy.findById(registrationId, true);
                } else {
                    registration = registrationStrategy.findHistoryObject(registrationId, date);
                }
                person.setRegistration(registration);
            }
        }
    }

    @Transactional
    public void loadName(Person person) {
        person.setFirstName(nameBean.getFirstName(person.getAttribute(FIRST_NAME).getValueId()));
        person.setMiddleName(nameBean.getMiddleName(person.getAttribute(MIDDLE_NAME).getValueId()));
        person.setLastName(nameBean.getLastName(person.getAttribute(LAST_NAME).getValueId()));
    }

    @Transactional
    private void loadChildren(Person person) {
        List<Attribute> childrenAttributes = person.getAttributes(CHILDREN);
        if (childrenAttributes != null && !childrenAttributes.isEmpty()) {
            for (Attribute childAttribute : childrenAttributes) {
                Long childId = childAttribute.getValueId();
                DomainObjectExample example = new DomainObjectExample(childId);
                example.setAdmin(true);
                List<Person> children = find(example);
                if (children != null && children.size() == 1) {
                    Person child = children.get(0);
                    if (child != null) {
                        person.addChild(child);
                    }
                }
            }
        }
    }

    @Override
    protected void fillAttributes(DomainObject object) {
        List<Attribute> toAdd = newArrayList();
        for (EntityAttributeType attributeType : getEntity().getEntityAttributeTypes()) {
            if (!attributeType.isObsolete()) {
                if (object.getAttributes(attributeType.getId()).isEmpty()) {
                    if ((attributeType.getEntityAttributeValueTypes().size() == 1) && !attributeType.getId().equals(CHILDREN)) {
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

                        //by default UKRAINE_CITIZENSHIP attribute set to TRUE.
                        if (attributeType.getId().equals(UKRAINE_CITIZENSHIP)) {
                            StringCulture systemLocaleStringCulture = stringBean.getSystemStringCulture(attribute.getLocalizedValues());
                            systemLocaleStringCulture.setValue(new BooleanConverter().toString(Boolean.TRUE));
                        }
                    }
                }
            }
        }
        if (!toAdd.isEmpty()) {
            object.getAttributes().addAll(toAdd);
        }
    }

    @Transactional
    private Set<Long> findRegistrationIds(long personId) {
        Map<String, Long> params = of("personId", personId, "registrationAttributeType", REGISTRATION);
        return newHashSet(sqlSession().selectList(PERSON_MAPPING + ".findRegistrationIds", params));
    }

    @Override
    public long getDefaultOrderByAttributeId() {
        return DEFAULT_ORDER_BY_ID;
    }

    @Transactional
    @Override
    public TreeSet<Date> getHistoryDates(long objectId) {
        TreeSet<Date> historyDates = super.getHistoryDates(objectId);
        Set<Long> addressIds = findRegistrationIds(objectId);
        for (Long addressId : addressIds) {
            TreeSet<Date> addressHistoryDates = registrationStrategy.getHistoryDates(addressId);
            historyDates.addAll(addressHistoryDates);
        }
        return historyDates;
    }

    @Transactional
    @Override
    public Person findHistoryObject(long objectId, Date date) {
        DomainObject personObject = super.findHistoryObject(objectId, date);
        if (personObject == null) {
            return null;
        }
        Person person = new Person(personObject);
        loadRegistration(person, date);
        loadChildren(person);
        return person;
    }

    @Transactional
    @Override
    public void delete(long objectId, Locale locale) throws DeleteException {
        deleteChecks(objectId, locale);
        Set<Long> registrationIds = findRegistrationIds(objectId);
        deleteStrings(objectId);
        deleteAttribute(objectId);
        deleteObject(objectId, locale);

        //delete registrations:
        for (Long registrationId : registrationIds) {
            registrationStrategy.delete(registrationId, locale);
        }
    }
//    @Transactional
//    @Override
//    public void changeChildrenActivity(long personId, boolean enable) {
//        Set<Long> registrationIds = findRegistrationIds(personId);
//        if (!registrationIds.isEmpty()) {
//            Map<String, Object> params = newHashMap();
//            params.put("personId", personId);
//            params.put("enabled", !enable);
//            params.put("status", enable ? StatusType.ACTIVE : StatusType.INACTIVE);
//            sqlSession().update(PERSON_MAPPING + ".updateRegistrationActivity", params);
//        }
//    }

    @Override
    public int getSearchTextFieldSize() {
        return 40;
    }

    @Override
    public Class<? extends WebPage> getHistoryPage() {
        return PersonHistoryPage.class;
    }

    @Transactional
    public List<Person> findPersonsByAddress(String addressEntity, long addressId) {
        long addressTypeId = registrationStrategy.getAddressTypeId(addressEntity);
        Map<String, Long> params = of("registrationAttributeType", REGISTRATION,
                "addressAttributeType", RegistrationStrategy.ADDRESS,
                "addressId", addressId,
                "addressTypeId", addressTypeId);
        List<Long> personIds = sqlSession().selectList(PERSON_MAPPING + ".findPersonIdsByAddress", params);
        List<Person> persons = newArrayList();
        for (Long personId : personIds) {
            Person person = findById(personId, true, false, true);
            loadName(person);
            persons.add(person);
        }
        return persons;
    }

    @Transactional
    public List<Person> findOwnersByAddress(String addressEntity, long addressId) {
        long addressTypeId = registrationStrategy.getAddressTypeId(addressEntity);
        Map<String, Long> params = of("registrationAttributeType", REGISTRATION,
                "addressAttributeType", RegistrationStrategy.ADDRESS,
                "addressId", addressId,
                "addressTypeId", addressTypeId,
                "isOwnerAttributeType", RegistrationStrategy.IS_OWNER);
        List<Long> personIds = sqlSession().selectList(PERSON_MAPPING + ".findOwnersByAddress", params);
        List<Person> persons = newArrayList();
        for (Long personId : personIds) {
            Person person = findById(personId, true, false, false);
            loadName(person);
            persons.add(person);
        }
        return persons;
    }

    @Transactional
    public Person findResponsibleByAddress(String addressEntity, long addressId) {
        long addressTypeId = registrationStrategy.getAddressTypeId(addressEntity);
        Map<String, Long> params = of("registrationAttributeType", REGISTRATION,
                "addressAttributeType", RegistrationStrategy.ADDRESS,
                "addressId", addressId,
                "addressTypeId", addressTypeId,
                "isResponsibleAttributeType", RegistrationStrategy.IS_RESPONSIBLE);
        Long personId = (Long) sqlSession().selectOne(PERSON_MAPPING + ".findResponsibleByAddress", params);
        Person person = personId != null ? findById(personId, true, false, true) : null;
        if (person != null) {
            loadName(person);
        }
        return person;
    }

    @Transactional
    public String getOwnerName(String addressEntity, long addressId, Locale locale) {
        List<Person> owners = findOwnersByAddress(addressEntity, addressId);
        if (owners != null && !owners.isEmpty()) {
            //TODO: take first owner, might all owners should be taken into account?
            return displayDomainObject(owners.get(0), locale);
        } else {
            Person responsible = findResponsibleByAddress(addressEntity, addressId);
            if (responsible != null) {
                return responsible.getRegistration().getOwnerName();
            }
        }
        return ResourceUtil.getString(RESOURCE_BUNDLE, "no_owner_name", locale);
    }

    public String getOwnerOrResponsibleName(List<Person> members, Locale locale) {
        if (members != null && !members.isEmpty()) {
            for (Person person : members) {
                if (person.getRegistration().isOwner() || person.getRegistration().isResponsible()) {
                    //TODO: take first owner, might all owners should be taken into account?
                    return displayDomainObject(person, locale);
                }
            }
        }
        return ResourceUtil.getString(RESOURCE_BUNDLE, "no_owner_or_responsible", locale);
    }

    public String getPersonalAccount(List<Person> members, Locale locale) {
        if (members != null && !members.isEmpty()) {
            for (Person person : members) {
                String personalAccount = person.getRegistration().getPersonalAccount();
                if (!Strings.isEmpty(personalAccount)) {
                    return personalAccount;
                }
            }
        }
        return ResourceUtil.getString(RESOURCE_BUNDLE, "no_personal_account", locale);
    }

    public String getFormOfOwnership(List<Person> members, Locale locale) {
        if (members != null && !members.isEmpty()) {
            for (Person person : members) {
                String formOfOwnership = person.getRegistration().getFormOfOwnership();
                if (!Strings.isEmpty(formOfOwnership)) {
                    return formOfOwnership;
                }
            }
        }
        return ResourceUtil.getString(RESOURCE_BUNDLE, "no_form_of_ownership", locale);
    }
}

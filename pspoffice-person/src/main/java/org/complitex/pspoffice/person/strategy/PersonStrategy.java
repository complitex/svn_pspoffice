package org.complitex.pspoffice.person.strategy;

import com.google.common.base.Predicate;
import static com.google.common.collect.ImmutableMap.*;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Iterables.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import static com.google.common.collect.Lists.*;
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
import org.apache.wicket.util.string.Strings;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.converter.BooleanConverter;
import org.complitex.dictionary.converter.DateConverter;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.StatusType;
import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.entity.description.EntityAttributeValueType;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.PersonAgeType;
import org.complitex.pspoffice.person.strategy.entity.PersonName.PersonNameType;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.person.strategy.service.PersonNameBean;
import org.complitex.pspoffice.person.strategy.web.edit.person.PersonEdit;
import org.complitex.pspoffice.person.strategy.web.list.person.PersonList;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
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
     * Person kid-adult age threshold
     */
    public static final int AGE_THRESHOLD = 16;
    /**
     * Attribute type ids
     */
    public static final long LAST_NAME = 2000;
    public static final long FIRST_NAME = 2001;
    public static final long MIDDLE_NAME = 2002;
    public static final long IDENTITY_CODE = 2003;
    public static final long BIRTH_DATE = 2004;
    public static final long BIRTH_COUNTRY = 2005;
    public static final long BIRTH_REGION = 2006;
    public static final long BIRTH_DISTRICT = 2007;
    public static final long BIRTH_CITY = 2008;
    public static final long DOCUMENT = 2009;
    public static final long DEATH_DATE = 2013;
    public static final long MILITARY_SERVICE_RELATION = 2014;
    public static final long CHILDREN = 2015;
    public static final long GENDER = 2016;
    public static final long UKRAINE_CITIZENSHIP = 2020;
    private static final Set<Long> NAME_ATTRIBUTE_IDS = ImmutableSet.of(LAST_NAME, FIRST_NAME, MIDDLE_NAME);

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
    private PersonNameBean personNameBean;
    @EJB
    private DocumentStrategy documentStrategy;
    @EJB
    private DocumentTypeStrategy documentTypeStrategy;
    @EJB
    private LocaleBean localeBean;
    @EJB
    private RegistrationStrategy registrationStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;

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
        return new PageParameters();
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
    public String[] getListRoles() {
        return new String[]{SecurityRole.PERSON_MODULE_VIEW};
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        Person person = (Person) object;
        Locale systemLocale = localeBean.getSystemLocale();
        return displayPerson(person.getFirstName(locale, systemLocale), person.getMiddleName(locale, systemLocale),
                person.getLastName(locale, systemLocale));
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
            loadName(person);
            //load subject ids
            person.setSubjectIds(loadSubjects(person.getPermissionId()));
        }
        return persons;
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
        return new Person(super.newInstance());
    }

    @Transactional
    public Person findPersonById(long id, boolean runAsAdmin, boolean loadName, boolean loadChildren, boolean loadDocument) {
        Person person = findById(id, runAsAdmin, loadName, loadChildren, loadDocument);
        if (person == null) {
            //find history person
            person = findHistoryPerson(id, loadName, loadChildren, loadDocument);
        }
        return person;
    }

    @Transactional
    private Person findHistoryPerson(long objectId, boolean loadName, boolean loadChildren, boolean loadDocument) {
        DomainObjectExample example = new DomainObjectExample(objectId);
        example.setTable(getEntityTable());
        example.setStartDate(DateUtil.getCurrentDate());

        DomainObject personObject = (DomainObject) sqlSession().selectOne(DOMAIN_OBJECT_NAMESPACE + "." + FIND_HISTORY_OBJECT_OPERATION, example);
        if (personObject == null) {
            return null;
        }
        List<Attribute> historyAttributes = loadHistoryAttributes(objectId, DateUtil.justBefore(personObject.getEndDate()));
        loadStringCultures(historyAttributes);
        personObject.setAttributes(historyAttributes);
        updateStringsForNewLocales(personObject);

        Person person = new Person(personObject);
        if (loadName) {
            loadName(person);
        }
        if (loadChildren) {
            loadChildren(person);
        }
        if (loadDocument) {
            loadDocument(person);
        }
        return person;
    }

    @Transactional
    @Override
    public Person findById(long id, boolean runAsAdmin) {
        return findById(id, runAsAdmin, true, true, true);
    }

    @Transactional
    public Person findById(long id, boolean runAsAdmin, boolean loadName, boolean loadChildren, boolean loadDocument) {
        DomainObject personObject = super.findById(id, runAsAdmin);
        if (personObject == null) {
            return null;
        }
        Person person = new Person(personObject);
        if (loadName) {
            loadName(person);
        }
        if (loadChildren) {
            loadChildren(person);
        }
        if (loadDocument) {
            loadDocument(person);
        }
        return person;
    }

    @Transactional
    private void loadName(Person person) {
        //first name
        for (Attribute firstNameAttribute : person.getAttributes(FIRST_NAME)) {
            Long nameId = firstNameAttribute.getValueId();
            if (nameId != null) {
                person.addFirstName(localeBean.getLocale(firstNameAttribute.getAttributeId()),
                        personNameBean.findById(PersonNameType.FIRST_NAME, nameId).getName());
            }
        }

        //last name
        for (Attribute lastNameAttribute : person.getAttributes(LAST_NAME)) {
            Long nameId = lastNameAttribute.getValueId();
            if (nameId != null) {
                person.addLastName(localeBean.getLocale(lastNameAttribute.getAttributeId()),
                        personNameBean.findById(PersonNameType.LAST_NAME, nameId).getName());
            }
        }

        //middle name
        for (Attribute middleNameAttribute : person.getAttributes(MIDDLE_NAME)) {
            Long nameId = middleNameAttribute.getValueId();
            if (nameId != null) {
                person.addMiddleName(localeBean.getLocale(middleNameAttribute.getAttributeId()),
                        personNameBean.findById(PersonNameType.MIDDLE_NAME, nameId).getName());
            }
        }
    }

    @Transactional
    public void loadDocument(Person person) {
        if (person.getDocument() == null) {
            long documentId = person.getAttribute(DOCUMENT).getValueId();
            Document document = documentStrategy.findDocumentById(documentId);
            person.setDocument(document);
        }
    }

    @Transactional
    public List<Document> findPreviousDocuments(Long personId) {
        if (personId == null) {
            return null;
        }
        List<Attribute> previousDocumentAttributes = sqlSession().selectList(PERSON_MAPPING + ".findPreviousDocumentAttributes",
                personId);
        List<Document> previousDocuments = newArrayList();
        for (Attribute previousDocumentAttribute : previousDocumentAttributes) {
            Document previousDocument = documentStrategy.findDocumentById(previousDocumentAttribute.getValueId());
            long documentTypeId = previousDocument.getDocumentTypeId();
            DomainObject documentType = documentTypeStrategy.findById(documentTypeId, true);
            previousDocument.setDocumentType(documentType);
            previousDocuments.add(previousDocument);
        }
        return previousDocuments;
    }

    @Transactional
    public void loadChildren(Person person) {
        List<Attribute> childrenAttributes = person.getAttributes(CHILDREN);
        if (childrenAttributes != null && !childrenAttributes.isEmpty() && person.getChildren().isEmpty()) {
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
                    if ((attributeType.getEntityAttributeValueTypes().size() == 1) && !attributeType.getId().equals(CHILDREN)
                            && !NAME_ATTRIBUTE_IDS.contains(attributeType.getId())) {
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

        updateNameAttributesForNewLocales(object);

        if (!toAdd.isEmpty()) {
            object.getAttributes().addAll(toAdd);
        }
    }

    private void updateNameAttributesForNewLocales(DomainObject person) {
        updateNameAttributeForNewLocales(person, LAST_NAME);
        updateNameAttributeForNewLocales(person, FIRST_NAME);
        updateNameAttributeForNewLocales(person, MIDDLE_NAME);
    }

    private void updateNameAttributeForNewLocales(DomainObject person, final long nameAttributeTypeId) {
        List<Attribute> nameAttributes = person.getAttributes(nameAttributeTypeId);
        person.removeAttribute(nameAttributeTypeId);
        for (final org.complitex.dictionary.entity.Locale locale : localeBean.getAllLocales()) {
            boolean found = false;
            for (Attribute nameAttribute : nameAttributes) {
                if (nameAttribute.getAttributeId().equals(locale.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                Attribute attribute = new Attribute();
                attribute.setAttributeTypeId(nameAttributeTypeId);
                attribute.setValueTypeId(nameAttributeTypeId);
                attribute.setObjectId(person.getId());
                attribute.setAttributeId(locale.getId());
                nameAttributes.add(attribute);
            }
        }
        final long systemLocaleId = localeBean.getSystemLocaleObject().getId();
        Collections.sort(nameAttributes, new Comparator<Attribute>() {

            @Override
            public int compare(Attribute o1, Attribute o2) {
                if (o1.getAttributeId().equals(systemLocaleId)) {
                    return -1;
                }
                if (o2.getAttributeId().equals(systemLocaleId)) {
                    return 1;
                }
                return o1.getAttributeId().compareTo(o2.getAttributeId());
            }
        });
        person.getAttributes().addAll(nameAttributes);
    }

    @Override
    public long getDefaultOrderByAttributeId() {
        return DEFAULT_ORDER_BY_ID;
    }

    @Override
    public int getSearchTextFieldSize() {
        return 40;
    }

    @Override
    public Class<? extends WebPage> getHistoryPage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getDescriptionRoles() {
        return new String[]{SecurityRole.PERSON_MODULE_DESCRIPTION_EDIT};
    }

    @Transactional
    public List<Person> findByName(PersonAgeType personAgeType, String lastName, String firstName, String middleName,
            Locale locale) {
        if (Strings.isEmpty(lastName)) {
            throw new IllegalArgumentException("Last name is null or empty.");
        }
        DomainObjectExample example = new DomainObjectExample();
        example.setStatus(StatusType.ACTIVE.name());
        example.addAdditionalParam("last_name", lastName);
        example.setLocaleId(localeBean.convert(locale).getId());

        firstName = firstName != null ? firstName.trim() : null;
        if (Strings.isEmpty(firstName)) {
            firstName = null;
        }
        example.addAdditionalParam("first_name", firstName);

        middleName = middleName != null ? middleName.trim() : null;
        if (Strings.isEmpty(middleName)) {
            middleName = null;
        }
        example.addAdditionalParam("middle_name", middleName);

        prepareExampleForPermissionCheck(example);

        List<Person> results = newArrayList();
        List<Person> persons = sqlSession().selectList(PERSON_MAPPING + ".findByName", example);
        for (Person person : persons) {
            loadAttributes(person);
            boolean eligiblePerson = (personAgeType == PersonAgeType.ANY)
                    || (personAgeType == PersonAgeType.KID && person.isKid())
                    || (personAgeType == PersonAgeType.ADULT && !person.isKid());
            if (eligiblePerson) {
                loadName(person);
                loadDocument(person);
                results.add(person);
            }
        }
        return results;
    }

    @Transactional
    @Override
    protected void insertDomainObject(DomainObject object, Date insertDate) {
        Person person = (Person) object;
        person.getDocument().setSubjectIds(person.getSubjectIds());
        documentStrategy.insert(person.getDocument(), insertDate);
        updateDocumentAttribute(null, person);
        prepareForSaveNameAttributes(person);
        super.insertDomainObject(object, insertDate);
    }

    private void prepareForSaveNameAttributes(Person person) {
        prepareForSaveNameAttribute(person, LAST_NAME);
        prepareForSaveNameAttribute(person, FIRST_NAME);
        prepareForSaveNameAttribute(person, MIDDLE_NAME);
    }

    private void prepareForSaveNameAttribute(Person person, long nameAttributeTypeId) {
        List<Attribute> nameAttributes = person.getAttributes(nameAttributeTypeId);
        person.removeAttribute(nameAttributeTypeId);
        for (Attribute nameAttribute : nameAttributes) {
            if (localeBean.getSystemLocaleObject().getId().equals(nameAttribute.getAttributeId())
                    || nameAttribute.getValueId() != null) {
                person.addAttribute(nameAttribute);
            }
        }
    }

    @Transactional
    @Override
    protected void insertUpdatedDomainObject(DomainObject person, Date updateDate) {
        super.insertDomainObject(person, updateDate);
    }

    @Transactional
    @Override
    public void update(DomainObject oldObject, DomainObject newObject, Date updateDate) {
        Person oldPerson = (Person) oldObject;
        Person newPerson = (Person) newObject;

        //document altering
        Date archiveDocumentDate = DateUtil.justAfter(updateDate);
        Date updateDocumentDate = DateUtil.justBefore(updateDate);

        newPerson.getDocument().setSubjectIds(newPerson.getSubjectIds());
        documentStrategy.update(oldPerson.getDocument(), newPerson.getDocument(), updateDocumentDate);

        if (newPerson.getReplacedDocument() != null) {
            documentStrategy.archive(newPerson.getDocument(), archiveDocumentDate);
            newPerson.getReplacedDocument().setSubjectIds(newPerson.getSubjectIds());
            documentStrategy.insert(newPerson.getReplacedDocument(), updateDate);
        }

        updateDocumentAttribute(oldPerson, newPerson);

        // if person was a kid but birth date has changed or time go on then it is need to update parent
        if (oldPerson.isKid() && !newPerson.isKid()) {
            removeKidFromParent(newPerson.getId(), updateDate);
        }

        prepareForSaveNameAttributes(oldPerson);
        prepareForSaveNameAttributes(newPerson);

        super.update(oldPerson, newPerson, updateDate);
    }

    @Transactional
    private void removeKidFromParent(final long childId, Date updateDate) {
        Map<String, Long> params = of("childId", childId, "personChildrenAT", CHILDREN);
        List<Long> parentIds = sqlSession().selectList(PERSON_MAPPING + ".findParents", params);
        for (long parentId : parentIds) {
            Person oldParent = findById(parentId, true);
            Person newParent = CloneUtil.cloneObject(oldParent);
            List<Attribute> children = newParent.getAttributes(CHILDREN);
            newParent.removeAttribute(CHILDREN);
            newParent.getAttributes().addAll(newArrayList(filter(children, new Predicate<Attribute>() {

                @Override
                public boolean apply(Attribute childrenAttribute) {
                    return !new Long(childId).equals(childrenAttribute.getValueId());
                }
            })));

            update(oldParent, newParent, updateDate);
        }
    }

    private void updateDocumentAttribute(Person oldPerson, Person newPerson) {
        Long documentId = null;
        if (oldPerson == null) {
            documentId = newPerson.getDocument().getId();
        } else {
            documentId = newPerson.getReplacedDocument() != null ? newPerson.getReplacedDocument().getId()
                    : newPerson.getDocument().getId();
        }
        newPerson.getAttribute(DOCUMENT).setValueId(documentId);
    }

    public static class PersonRegistration implements Serializable {

        private long registrationId;
        private Registration registration;
        private String addressEntity;
        private long addressTypeId;
        private long addressId;

        public long getAddressId() {
            return addressId;
        }

        public void setAddressId(long addressId) {
            this.addressId = addressId;
        }

        public long getAddressTypeId() {
            return addressTypeId;
        }

        public void setAddressTypeId(long addressTypeId) {
            this.addressTypeId = addressTypeId;
        }

        public long getRegistrationId() {
            return registrationId;
        }

        public void setRegistrationId(long registrationId) {
            this.registrationId = registrationId;
        }

        public String getAddressEntity() {
            return addressEntity;
        }

        public Registration getRegistration() {
            return registration;
        }

        private void setAddressEntity(String addressEntity) {
            this.addressEntity = addressEntity;
        }

        private void setRegistration(Registration registration) {
            this.registration = registration;
        }
    }

    @Transactional
    public int countPersonRegistrations(long personId) {
        return (Integer) sqlSession().selectOne(PERSON_MAPPING + ".countPersonRegistrations",
                newFindPersonRegistrationParameters(personId));
    }

    @Transactional
    public List<PersonRegistration> findPersonRegistrations(long personId) {
        List<PersonRegistration> personRegistrations = sqlSession().selectList(PERSON_MAPPING + ".findPersonRegistrations",
                newFindPersonRegistrationParameters(personId));
        for (PersonRegistration personRegistration : personRegistrations) {
            personRegistration.setRegistration(
                    registrationStrategy.findRegistrationById(personRegistration.getRegistrationId(), true, false, true, true));
            personRegistration.setAddressEntity(ApartmentCardStrategy.getAddressEntity(personRegistration.getAddressTypeId()));
        }
        return personRegistrations;
    }

    private Map<String, Long> newFindPersonRegistrationParameters(long personId) {
        return of("registrationPersonAT", RegistrationStrategy.PERSON,
                "apartmentCardRegistrationAT", ApartmentCardStrategy.REGISTRATIONS,
                "apartmentCardAddressAT", ApartmentCardStrategy.ADDRESS,
                "personId", personId);
    }

    public static class PersonApartmentCardAddress implements Serializable {

        private long apartmentCardId;
        private String addressEntity;
        private long addressTypeId;
        private long addressId;

        public String getAddressEntity() {
            return addressEntity;
        }

        private void setAddressEntity(String addressEntity) {
            this.addressEntity = addressEntity;
        }

        public long getAddressId() {
            return addressId;
        }

        public void setAddressId(long addressId) {
            this.addressId = addressId;
        }

        public long getAddressTypeId() {
            return addressTypeId;
        }

        public void setAddressTypeId(long addressTypeId) {
            this.addressTypeId = addressTypeId;
        }

        public long getApartmentCardId() {
            return apartmentCardId;
        }

        public void setApartmentCardId(long apartmentCardId) {
            this.apartmentCardId = apartmentCardId;
        }
    }

    @Transactional
    public List<PersonApartmentCardAddress> findPersonApartmentCardAddresses(long personId) {
        List<PersonApartmentCardAddress> personApartmentCardAddresses = sqlSession().selectList(PERSON_MAPPING
                + ".findPersonApartmentCardAddresses", newFindPersonRegistrationParameters(personId));
        for (PersonApartmentCardAddress personApartmentCardAddress : personApartmentCardAddresses) {
            personApartmentCardAddress.setAddressEntity(ApartmentCardStrategy.getAddressEntity(personApartmentCardAddress.getAddressTypeId()));
        }
        return personApartmentCardAddresses;
    }

    @Transactional
    public String findPermanentRegistrationAddress(long personId, Locale locale) {
        Map<Object, Object> params = builder().putAll(newFindPersonRegistrationParameters(personId)).
                put("registrationTypeAT", RegistrationStrategy.REGISTRATION_TYPE).
                put("permanentRegistrationTypeId", RegistrationTypeStrategy.PERMANENT).
                build();
        List<PersonRegistration> personRegistrations = sqlSession().selectList(
                PERSON_MAPPING + ".findPermanentRegistrationAddress", params);
        if (!personRegistrations.isEmpty()) {
            return addressRendererBean.displayAddress(
                    ApartmentCardStrategy.getAddressEntity(personRegistrations.get(0).getAddressTypeId()),
                    personRegistrations.get(0).getAddressId(), locale);
        }
        return null;
    }

    @Transactional
    public void registerPersonDeath(Person person, Date deathDate, List<PersonRegistration> activePersonRegistrations,
            Locale locale) {
        Date archiveDate = DateUtil.getCurrentDate();
        Date updateDate = DateUtil.justBefore(archiveDate);
        Date archiveDocumentDate = DateUtil.justAfter(archiveDate);

        if (activePersonRegistrations != null && !activePersonRegistrations.isEmpty()) {
            for (PersonRegistration personRegistration : activePersonRegistrations) {
                final Registration oldRegistration = personRegistration.getRegistration();
                final Registration newRegistration = CloneUtil.cloneObject(oldRegistration);
                //departure reason
                stringBean.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_REASON).getLocalizedValues()).
                        setValue(ResourceUtil.getString(RESOURCE_BUNDLE, "death_departure_reason", locale));
                //departure date
                stringBean.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_DATE).getLocalizedValues()).
                        setValue(new DateConverter().toString(deathDate));
                registrationStrategy.update(oldRegistration, newRegistration, updateDate);
                registrationStrategy.archive(newRegistration, archiveDate);
            }
        }

        if (person.isKid()) {
            removeKidFromParent(person.getId(), updateDate);
        }

        Person newPerson = CloneUtil.cloneObject(person);
        stringBean.getSystemStringCulture(newPerson.getAttribute(PersonStrategy.DEATH_DATE).getLocalizedValues()).
                setValue(new DateConverter().toString(deathDate));
        update(person, newPerson, updateDate);

        documentStrategy.archive(person.getDocument(), archiveDocumentDate);
        archive(person, archiveDate);
    }
}

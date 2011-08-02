package org.complitex.pspoffice.person.strategy;

import java.util.Collections;
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
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.converter.BooleanConverter;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.StatusType;
import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.entity.description.EntityAttributeValueType;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.NameBean;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.web.edit.person.PersonEdit;
import org.complitex.pspoffice.person.strategy.web.list.PersonList;
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
    public static final long IDENTITY_CODE = 2003;
    public static final long BIRTH_DATE = 2004;
    public static final long BIRTH_COUNTRY = 2005;
    public static final long BIRTH_REGION = 2006;
    public static final long BIRTH_DISTRICT = 2007;
    public static final long BIRTH_CITY = 2008;
    public static final long PASSPORT_SERIAL_NUMBER = 2009;
    public static final long PASSPORT_NUMBER = 2010;
    public static final long PASSPORT_ACQUISITION_ORGANIZATION = 2011;
    public static final long PASSPORT_ACQUISITION_DATE = 2012;
    public static final long DEATH_DATE = 2013;
    public static final long MILITARY_SERVISE_RELATION = 2014;
    public static final long CHILDREN = 2015;
    public static final long GENDER = 2016;
    public static final long BIRTH_CERTIFICATE_INFO = 2017;
    public static final long BIRTH_CERTIFICATE_ACQUISITION_DATE = 2018;
    public static final long BIRTH_CERTIFICATE_ACQUISITION_ORGANIZATION = 2019;
    public static final long UKRAINE_CITIZENSHIP = 2020;

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
    @Override
    public Person findById(long id, boolean runAsAdmin) {
        return findById(id, runAsAdmin, true);
    }

    @Transactional
    public Person findById(long id, boolean runAsAdmin, boolean loadChildren) {
        DomainObject personObject = super.findById(id, runAsAdmin);
        if (personObject == null) {
            return null;
        }
        Person person = new Person(personObject);
        if (loadChildren) {
            loadChildren(person);
        }
        return person;
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

    @Override
    public long getDefaultOrderByAttributeId() {
        return DEFAULT_ORDER_BY_ID;
    }

    @Transactional
    @Override
    public Person findHistoryObject(long objectId, Date date) {
        DomainObject personObject = super.findHistoryObject(objectId, date);
        if (personObject == null) {
            return null;
        }
        Person person = new Person(personObject);
        loadChildren(person);
        return person;
    }

    @Override
    public int getSearchTextFieldSize() {
        return 40;
    }

    @Override
    public Class<? extends WebPage> getHistoryPage() {
        return PersonHistoryPage.class;
    }

    @Override
    public String[] getDescriptionRoles() {
        return new String[]{SecurityRole.PERSON_MODULE_DESCRIPTION_EDIT};
    }

    @Transactional
    public List<Person> findByName(String lastName, String firstName, String middleName) {
        if (Strings.isEmpty(lastName)) {
            throw new IllegalArgumentException("Last name is null or empty.");
        }
        DomainObjectExample example = new DomainObjectExample();
        example.setStatus(StatusType.ACTIVE.name());
        example.addAdditionalParam("last_name", lastName);

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

        List<Person> persons = sqlSession().selectList(PERSON_MAPPING + ".findByName", example);
        for (Person person : persons) {
            loadAttributes(person);
        }
        return persons;
    }
}

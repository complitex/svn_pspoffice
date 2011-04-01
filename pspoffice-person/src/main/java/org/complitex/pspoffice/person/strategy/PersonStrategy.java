package org.complitex.pspoffice.person.strategy;

import com.google.common.collect.Sets;
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
import java.util.Set;
import java.util.TreeSet;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.PermissionBean;
import org.complitex.dictionary.strategy.DeleteException;
import org.complitex.dictionary.strategy.web.AbstractComplexAttributesPanel;
import org.complitex.dictionary.strategy.web.validate.IValidator;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.web.edit.PersonNameEditComponent;
import org.complitex.pspoffice.person.strategy.web.edit.PersonRegistrationEditComponent;
import org.complitex.pspoffice.person.strategy.web.edit.validate.PersonValidator;
import org.complitex.pspoffice.person.strategy.web.list.PersonList;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@Stateless
public class PersonStrategy extends TemplateStrategy {

    private static final Logger log = LoggerFactory.getLogger(PersonStrategy.class);
    private static final String PERSON_MAPPING = PersonStrategy.class.getPackage().getName() + ".Person";
    /**
     * Attribute type ids
     */
    public static final long FIRST_NAME = 2001;
    public static final long LAST_NAME = 2000;
    public static final long MIDDLE_NAME = 2002;
    public static final long REGISTRATION = 2006;

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
    @EJB
    private StringCultureBean stringBean;
    @EJB
    private RegistrationStrategy registrationStrategy;

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
    public String[] getEditRoles() {
        return new String[]{SecurityRole.PERSON_MODULE_EDIT};
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        String firstName = stringBean.displayValue(object.getAttribute(FIRST_NAME).getLocalizedValues(), locale);
        String lastName = stringBean.displayValue(object.getAttribute(LAST_NAME).getLocalizedValues(), locale);
        String middleName = stringBean.displayValue(object.getAttribute(MIDDLE_NAME).getLocalizedValues(), locale);

        // return in format 'last_name fisrt_name middle_name'
        return lastName + " " + firstName + " " + middleName;
    }

    @Override
    public List<Person> find(DomainObjectExample example) {
        example.setTable(getEntityTable());
        prepareExampleForPermissionCheck(example);

        List<Person> objects = sqlSession().selectList(PERSON_MAPPING + "." + FIND_OPERATION, example);
        for (Person person : objects) {
            loadAttributes(person);
        }
        return objects;
    }

    @Override
    public int count(DomainObjectExample example) {
        example.setTable(getEntityTable());
        prepareExampleForPermissionCheck(example);
        return (Integer) sqlSession().selectOne(PERSON_MAPPING + "." + COUNT_OPERATION, example);
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(PersonStrategy.class.getName(), getEntityTable(), locale);
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelBeforeClass() {
        return PersonNameEditComponent.class;
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass() {
        return PersonRegistrationEditComponent.class;
    }

    @Override
    public IValidator getValidator() {
        return new PersonValidator();
    }

    @Override
    public Person newInstance() {
        Person person = new Person();
        fillAttributes(person);
        person.setRegistration(registrationStrategy.newInstance());

        //set up subject ids to visible-by-all subject
        Set<Long> defaultSubjectIds = Sets.newHashSet(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID);
        person.setSubjectIds(defaultSubjectIds);

        return person;
    }

    @Transactional
    @Override
    protected void insertDomainObject(DomainObject object, Date insertDate) {
        Person person = (Person) object;
        DomainObject registration = person.getRegistration();
        registrationStrategy.insert(registration, insertDate);
        person.updateRegistrationAttribute();
        super.insertDomainObject(person, insertDate);
    }

    @Transactional
    @Override
    protected void insertUpdatedDomainObject(DomainObject object, Date updateDate) {
        super.insertDomainObject(object, updateDate);
    }

    @Override
    public void update(DomainObject oldObject, DomainObject newObject, Date updateDate) {
        Person oldPerson = (Person) oldObject;
        Person newPerson = (Person) newObject;

        DomainObject oldRegistration = oldPerson.getRegistration();
        DomainObject updatedOldRegistration = newPerson.getRegistration();
        updatedOldRegistration.setSubjectIds(newPerson.getSubjectIds());

        DomainObject newRegistration = newPerson.getNewRegistration();
        Date oldRegistrationUpdateDate = updateDate;
        if (newRegistration != null) {
            oldRegistrationUpdateDate = DateUtil.justBefore(updateDate);
        }
        registrationStrategy.update(oldRegistration, updatedOldRegistration, oldRegistrationUpdateDate);

        if (newRegistration != null) {
            newRegistration.setSubjectIds(newPerson.getSubjectIds());
            registrationStrategy.insert(newRegistration, updateDate);
            newPerson.getAttribute(REGISTRATION).setValueId(newRegistration.getId());
            registrationStrategy.archive(updatedOldRegistration);
        }

        super.update(oldObject, newObject, updateDate);
    }

    @Transactional
    @Override
    public Person findById(long id, boolean runAsAdmin) {
        DomainObjectExample example = new DomainObjectExample(id);
        example.setTable(getEntityTable());
        if (!runAsAdmin) {
            prepareExampleForPermissionCheck(example);
        } else {
            example.setAdmin(true);
        }

        Person person = (Person) sqlSession().selectOne(PERSON_MAPPING + "." + FIND_BY_ID_OPERATION, example);
        if (person != null) {
            loadAttributes(person);
            fillAttributes(person);
            updateStringsForNewLocales(person);
            //load registration object
            loadRegistration(person, null);

            //load subject ids
            person.setSubjectIds(loadSubjects(person.getPermissionId()));
        }

        return person;
    }

    @Transactional
    private void loadRegistration(Person person, Date date) {
        Attribute registrationAttribute = person.getAttribute(REGISTRATION);
        Long registrationId = registrationAttribute.getValueId();
        DomainObject registration = null;
        if (date == null) {
            registration = registrationStrategy.findById(registrationId, true);
        } else {
            registration = registrationStrategy.findHistoryObject(registrationId, date);
        }
        person.setRegistration(registration);
    }

    @Transactional
    private Set<Long> findRegistrationIds(long personId) {
        return Sets.newHashSet(sqlSession().selectList(PERSON_MAPPING + ".findRegistrationIds", personId));
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
        DomainObjectExample example = new DomainObjectExample(objectId);
        example.setTable(getEntityTable());
        example.setStartDate(date);

        Person person = (Person) sqlSession().selectOne(PERSON_MAPPING + "." + FIND_HISTORY_OBJECT_OPERATION, example);
        if (person == null) {
            return null;
        }

        List<Attribute> historyAttributes = loadHistoryAttributes(objectId, date);
        loadStringCultures(historyAttributes);
        person.setAttributes(historyAttributes);
        loadRegistration(person, date);
        updateStringsForNewLocales(person);
        return person;
    }

    @Override
    public void delete(long objectId) throws DeleteException {
        deleteChecks(objectId);

        Set<Long> registrationIds = findRegistrationIds(objectId);

        deleteStrings(objectId);
        deleteAttribute(objectId);
        deleteObject(objectId);

        //delete registrations:
        for (Long registrationId : registrationIds) {
            registrationStrategy.delete(registrationId);
        }

    }
}

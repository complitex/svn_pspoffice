package org.complitex.pspoffice.person.strategy;

import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.service.StringCultureBean;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Locale;
import org.complitex.dictionary.strategy.web.AbstractComplexAttributesPanel;
import org.complitex.dictionary.strategy.web.validate.IValidator;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.web.edit.PersonEditComponent;
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
        return PersonEditComponent.class;
    }

    @Override
    public IValidator getValidator() {
        return null;
    }
}

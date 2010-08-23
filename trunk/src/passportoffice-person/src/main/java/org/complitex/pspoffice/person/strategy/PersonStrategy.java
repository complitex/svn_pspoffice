/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.complitex.dictionaryfw.dao.StringCultureBean;
import org.complitex.dictionaryfw.dao.aop.SqlSessionInterceptor;
import org.complitex.dictionaryfw.entity.Attribute;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.description.EntityAttributeType;
import org.complitex.dictionaryfw.entity.example.AttributeExample;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.web.component.search.ISearchCallback;
import org.complitex.pspoffice.commons.web.pages.DomainObjectEdit;
import org.complitex.pspoffice.commons.web.pages.HistoryPage;

/**
 *
 * @author Artem
 */
@Stateless
@Interceptors({SqlSessionInterceptor.class})
public class PersonStrategy extends Strategy {

    /**
     * Attribute type ids
     */
    public static final long REGISTRATION = 900;

    public static final long FIRST_NAME = 901;

    public static final long LAST_NAME = 902;

    public static final long MIDDLE_NAME = 903;

    @EJB
    private StringCultureBean stringBean;

    @Override
    public String getEntityTable() {
        return "person";
    }

    @Override
    public boolean isSimpleAttributeType(EntityAttributeType attributeType) {
        return attributeType.getId() > REGISTRATION;
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
    public List<String> getSearchFilters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ISearchCallback getSearchCallback() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        String firstName = "";
        String lastName = "";
        String middleName = "";

        for (Attribute attr : object.getAttributes()) {
            if (attr.getAttributeTypeId().equals(FIRST_NAME)) {
                firstName = stringBean.displayValue(attr.getLocalizedValues(), locale);
            } else if (attr.getAttributeTypeId().equals(LAST_NAME)) {
                lastName = stringBean.displayValue(attr.getLocalizedValues(), locale);
            } else if (attr.getAttributeTypeId().equals(MIDDLE_NAME)) {
                middleName = stringBean.displayValue(attr.getLocalizedValues(), locale);
            }
        }

        // return in format 'last_name fisrt_name middle_name'
        return lastName + " " + firstName + " " + middleName;
    }

    @Override
    public void configureExample(DomainObjectExample example, Map<String, Long> ids, String searchTextInput) {
        configureExampleImpl(example, ids, searchTextInput);
    }

    private static void configureExampleImpl(DomainObjectExample example, Map<String, Long> ids, String searchTextInput) {
        Long roomId = ids.get("room");
        Long apartmentId = ids.get("apartment");
        Long buildingId = ids.get("building");

        String registration = null;
        if (roomId != null && roomId > 0) {
            registration = String.valueOf(roomId);
        } else if (apartmentId != null && apartmentId > 0) {
            registration = String.valueOf(apartmentId);
        } else if (buildingId != null && buildingId > 0) {
            registration = String.valueOf(buildingId);
        }

        AttributeExample registrationExample = null;
        try {
            registrationExample = Iterables.find(example.getAttributeExamples(), new Predicate<AttributeExample>() {

                @Override
                public boolean apply(AttributeExample attrExample) {
                    return attrExample.getAttributeTypeId().equals(REGISTRATION);
                }
            });
        } catch (NoSuchElementException e) {
            registrationExample = new AttributeExample(REGISTRATION);
            example.addAttributeExample(registrationExample);
        }
        registrationExample.setValue(registration != null ? registration : null);
    }

    @Override
    public Class<? extends WebPage> getEditPage() {
        return DomainObjectEdit.class;
    }

    @Override
    public PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity) {
        PageParameters params = new PageParameters();
        params.put(DomainObjectEdit.ENTITY, getEntityTable());
        params.put(DomainObjectEdit.OBJECT_ID, objectId);
        params.put(DomainObjectEdit.PARENT_ID, parentId);
        params.put(DomainObjectEdit.PARENT_ENTITY, parentEntity);
        return params;
    }

    @Override
    public ISearchCallback getParentSearchCallback() {
        return null;
    }

    @Override
    public Class<? extends WebPage> getHistoryPage() {
        return HistoryPage.class;
    }

    @Override
    public PageParameters getHistoryPageParams(long objectId) {
        PageParameters params = new PageParameters();
        params.put(HistoryPage.ENTITY, getEntityTable());
        params.put(HistoryPage.OBJECT_ID, objectId);
        return params;
    }

    @Override
    public String[] getChildrenEntities() {
        return null;
    }

    @Override
    public String[] getParents() {
        return null;
    }
}

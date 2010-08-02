/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.commons.strategy.room;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.complitex.dictionaryfw.dao.aop.SqlSessionInterceptor;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.EntityAttribute;
import org.complitex.dictionaryfw.entity.description.AttributeDescription;
import org.complitex.dictionaryfw.entity.description.DomainObjectDescription;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.strategy.web.DomainObjectEdit;
import org.complitex.dictionaryfw.strategy.web.DomainObjectList;
import org.complitex.dictionaryfw.util.DisplayLocalizedValueUtil;
import org.complitex.dictionaryfw.web.component.search.ISearchCallback;

/**
 *
 * @author Artem
 */
@Stateless
@Interceptors({SqlSessionInterceptor.class})
public class RoomStrategy extends Strategy {

    @EJB
    private DisplayLocalizedValueUtil displayLocalizedValueUtil;

    private static final Long NAME_ATTRIBUTE_TYPE_ID = 200L;

    @Override
    public String getEntityTable() {
        return "room";
    }

    @Override
    public DomainObjectDescription getDescription() {
        DomainObjectDescription description = super.getDescription();

        description.setFilterAttributes(Lists.newArrayList(Iterables.filter(description.getAttributeDescriptions(), new Predicate<AttributeDescription>() {

            @Override
            public boolean apply(AttributeDescription attrDesc) {
                return attrDesc.getId().equals(NAME_ATTRIBUTE_TYPE_ID);
            }
        })));
        return description;
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return displayLocalizedValueUtil.displayValue(Iterables.find(object.getAttributes(), new Predicate<EntityAttribute>() {

            @Override
            public boolean apply(EntityAttribute attr) {
                return attr.getAttributeTypeId().equals(NAME_ATTRIBUTE_TYPE_ID);
            }
        }).getLocalizedValues(), locale);
    }

    @Override
    public List<String> getSearchFilters() {
        return ImmutableList.of("apartment");
    }

//    @Override
//    public List<ISearchBehaviour> getSearchBehaviours() {
//        List<ISearchBehaviour> behaviours = Lists.newArrayList();
//        behaviours.add(new ApartmentSearchBehaviour());
//        return behaviours;
//    }
//    @Override
//    public void configureSearchAttribute(DomainObjectExample example, String searchTextInput) {
//    }
    @Override
    public ISearchCallback getSearchCallback() {
        return new SearchCallback();
    }

    @Override
    public boolean isSimpleAttributeDesc(AttributeDescription attributeDescription) {
        return attributeDescription.getId() >= NAME_ATTRIBUTE_TYPE_ID;
    }

    @Override
    public void configureExample(DomainObjectExample example, Map<String, Long> ids, String searchTextInput) {
        configureExampleImpl(example, ids);
    }

    private static void configureExampleImpl(DomainObjectExample example, Map<String, Long> ids) {
        Long apartmentId = ids.get("apartment");
        if (apartmentId != null && apartmentId > 0) {
            example.setParentId(apartmentId);
            example.setParentEntity("apartment");
        } else {
            Long buildingId = ids.get("building");
            example.setParentId(buildingId);
            example.setParentEntity("building");
        }
    }

    private static class SearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(WebPage page, Map<String, Long> ids, AjaxRequestTarget target) {
            DomainObjectList list = (DomainObjectList) page;
            configureExampleImpl(list.getExample(), ids);
            list.refreshContent(target);
        }
    }

//    @Override
//    public List<ISearchBehaviour> getParentSearchBehaviours() {
//        return getSearchBehaviours();
//    }
    @Override
    public ISearchCallback getParentSearchCallback() {
        return new ParentSearchCallback();
    }

    private static class ParentSearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(WebPage page, Map<String, Long> ids, AjaxRequestTarget target) {
            DomainObjectEdit edit = (DomainObjectEdit) page;
            Long apartmentId = ids.get("apartment");
            edit.getObject().setParentId(apartmentId);
            edit.getObject().setParentEntityId(100L);
        }
    }

    @Override
    public Map<String, String> getChildrenInfo(Locale locale) {
        return null;
    }
}

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
import org.complitex.dictionaryfw.web.component.search.ISearchBehaviour;
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

    private AttributeDescription nameAttrDesc;

    @Override
    public String getEntityTable() {
        return "room";
    }

    @Override
    public DomainObjectDescription getDescription() {
        DomainObjectDescription description = super.getDescription();

        for (AttributeDescription attrDesc : description.getAttributeDescriptions()) {
            if (attrDesc.getId().equals(200L)) {
                nameAttrDesc = attrDesc;
            }
        }
        description.setFilterAttributes(Lists.newArrayList(nameAttrDesc));

        return description;
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return displayLocalizedValueUtil.displayValue(Iterables.find(object.getAttributes(), new Predicate<EntityAttribute>() {

            @Override
            public boolean apply(EntityAttribute attr) {
                return attr.getAttributeTypeId().equals(nameAttrDesc.getId());
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
        return attributeDescription.getId() >= 200L;
    }



    @Override
    public void configureExample(DomainObjectExample example, Map<String, Long> ids, String searchTextInput) {
        configureExampleImpl(example, ids);
    }

    private static void configureExampleImpl(DomainObjectExample example, Map<String, Long> ids) {
        Long apartmentId = ids.get("apartment");
        example.setParentId(apartmentId);
        example.setParentEntity("apartment");
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

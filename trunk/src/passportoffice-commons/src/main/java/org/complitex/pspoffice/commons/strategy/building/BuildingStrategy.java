/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.commons.strategy.building;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionaryfw.dao.aop.SqlSessionInterceptor;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.EntityAttribute;
import org.complitex.dictionaryfw.entity.description.AttributeDescription;
import org.complitex.dictionaryfw.entity.description.DomainObjectDescription;
import org.complitex.dictionaryfw.entity.example.DomainObjectAttributeExample;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.strategy.web.AbstractComplexAttributesPanel;
import org.complitex.dictionaryfw.strategy.web.DomainObjectEdit;
import org.complitex.dictionaryfw.strategy.web.DomainObjectList;
import org.complitex.dictionaryfw.util.CloneUtil;
import org.complitex.dictionaryfw.util.DisplayLocalizedValueUtil;
import org.complitex.dictionaryfw.web.component.search.ISearchCallback;
import org.complitex.pspoffice.commons.strategy.building.web.edit.BuildingEditComponent;

/**
 *
 * @author Artem
 */
@Stateless
@Interceptors({SqlSessionInterceptor.class})
public class BuildingStrategy extends Strategy {

    @EJB
    private DisplayLocalizedValueUtil displayLocalizedValueUtil;

    @Override
    public String getEntityTable() {
        return "building";
    }

    @Override
    public boolean isSimpleAttributeDesc(AttributeDescription attributeDescription) {
        return attributeDescription.getId() > 504L;
    }

    @Override
    public List<DomainObject> find(DomainObjectExample example) {
        example.setTable(getEntityTable());
        List<DomainObject> buildings = session.selectList("org.complitex.pspoffice.commons.strategy.building.Building." + FIND_OPERATION, example);
        for (DomainObject building : buildings) {
            DomainObjectExample loadAttrsExample = CloneUtil.cloneObject(example);
            loadAttrsExample.setId(building.getId());
            building.setAttributes(session.selectList("org.complitex.pspoffice.commons.strategy.building.Building.loadAttributes", loadAttrsExample));
        }
        return buildings;
    }

    @Override
    public int count(DomainObjectExample example) {
        example.setTable(getEntityTable());
        return (Integer) session.selectOne("org.complitex.pspoffice.commons.strategy.building.Building." + COUNT_OPERATION, example);
    }

    @Override
    public DomainObjectDescription getDescription() {
        DomainObjectDescription description = super.getDescription();
        for (AttributeDescription attrDesc : description.getAttributeDescriptions()) {
            if (attrDesc.getId().equals(500L) || attrDesc.getId().equals(501L) || attrDesc.getId().equals(502L)) {
                description.addFilterAttribute(attrDesc);
            }
        }
        return description;
    }

//    @Override
//    public List<ISearchBehaviour> getSearchBehaviours() {
//        List<ISearchBehaviour> behaviours = Lists.newArrayList();
//        behaviours.add(new StreetSearchBehaviour());
//        return behaviours;
//    }
    @Override
    public ISearchCallback getSearchCallback() {
        return new SearchCallback();
    }

    @Override
    public void configureExample(DomainObjectExample example, Map<String, Long> ids, String searchTextInput) {
        configureExampleImpl(example, ids, searchTextInput);
    }

    @Override
    public List<String> getSearchFilters() {
        return ImmutableList.of("street");
    }

    private static void configureExampleImpl(DomainObjectExample example, Map<String, Long> ids, String searchTextInput) {
        if (!Strings.isEmpty(searchTextInput)) {
        }
        DomainObjectAttributeExample streetExample = null;
        try {
            streetExample = Iterables.find(example.getAttributeExamples(), new Predicate<DomainObjectAttributeExample>() {

                @Override
                public boolean apply(DomainObjectAttributeExample example) {
                    return example.getAttributeTypeId().equals(503L);
                }
            });
        } catch (NoSuchElementException e) {
            streetExample = new DomainObjectAttributeExample(503L);
            example.addAttributeExample(streetExample);
        }
        streetExample.setValue(String.valueOf(ids.get("street")));

        Long cityId = ids.get("city");
        if (cityId != null && cityId > 0) {
            example.setParentId(cityId);
            example.setParentEntity("city");
        }
    }

    private static class SearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(WebPage page, Map<String, Long> ids, AjaxRequestTarget target) {
            DomainObjectList list = (DomainObjectList) page;
            DomainObjectExample example = list.getExample();
            configureExampleImpl(example, ids, null);
            list.refreshContent(target);
        }
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        List<EntityAttribute> numbers = Lists.newArrayList(Iterables.filter(object.getAttributes(), new Predicate<EntityAttribute>() {

            @Override
            public boolean apply(EntityAttribute attr) {
                return attr.getAttributeTypeId().equals(500L);
            }
        }));
        return displayLocalizedValueUtil.displayValue(numbers.get(0).getLocalizedValues(), locale);
    }

//    @Override
//    public void configureSearchAttribute(DomainObjectExample example, String searchTextInput) {
//    }
//    @Override
//    public List<ISearchBehaviour> getParentSearchBehaviours() {
//        return null;
//    }
    @Override
    public ISearchCallback getParentSearchCallback() {
        return null;
    }

    private static class ParentSearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(WebPage page, Map<String, Long> ids, AjaxRequestTarget target) {
            DomainObjectEdit edit = (DomainObjectEdit) page;
            DomainObject object = edit.getObject();
            Long cityId = ids.get("city");
            if (cityId != null && cityId > 0) {
                object.setParentId(cityId);
                object.setParentEntityId(400L);
            }
        }
    }

    @Override
    public Map<String, String> getChildrenInfo(Locale locale) {
        return ImmutableMap.of("apartment", "Apartments", "room", "Rooms");
    }

    @Override
    public RestrictedObjectInfo findParentInSearchComponent(long id) {
        DomainObjectExample example = new DomainObjectExample();
        example.setTable(getEntityTable());
        example.setId(id);
        Map<String, Object> result = (Map<String, Object>) session.selectOne("org.complitex.pspoffice.commons.strategy.building.Building.findStreetInSearchComponent", example);
        Long streetId = (Long) result.get("streetId");
        if (streetId != null) {
            return new RestrictedObjectInfo("street", streetId);
        } else {
            return super.findParentInSearchComponent(id);
        }
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelClass() {
        return BuildingEditComponent.class;
    }
}

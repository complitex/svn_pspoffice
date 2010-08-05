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
import org.apache.wicket.Component.IVisitor;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionaryfw.dao.aop.SqlSessionInterceptor;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.Attribute;
import org.complitex.dictionaryfw.entity.description.AttributeDescription;
import org.complitex.dictionaryfw.entity.description.DomainObjectDescription;
import org.complitex.dictionaryfw.entity.example.DomainObjectAttributeExample;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.strategy.web.AbstractComplexAttributesPanel;
import org.complitex.dictionaryfw.strategy.web.DomainObjectEditPanel;
import org.complitex.dictionaryfw.strategy.web.DomainObjectListPanel;
import org.complitex.dictionaryfw.strategy.web.IValidator;
import org.complitex.dictionaryfw.util.CloneUtil;
import org.complitex.dictionaryfw.util.DisplayLocalizedValueUtil;
import org.complitex.dictionaryfw.util.ResourceUtil;
import org.complitex.dictionaryfw.web.component.search.ISearchCallback;
import org.complitex.dictionaryfw.web.component.search.SearchComponent;
import org.complitex.pspoffice.commons.strategy.building.web.edit.BuildingEditComponent;
import org.complitex.pspoffice.commons.strategy.building.web.edit.BuildingValidator;
import org.complitex.pspoffice.commons.web.pages.DomainObjectEdit;
import org.complitex.pspoffice.commons.web.pages.DomainObjectList;

/**
 *
 * @author Artem
 */
@Stateless
@Interceptors({SqlSessionInterceptor.class})
public class BuildingStrategy extends Strategy {

    public static final String RESOURCE_BUNDLE = BuildingStrategy.class.getName();

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
            building.setAttributes(session.selectList("org.complitex.pspoffice.commons.strategy.building.Building.loadSimpleAttributes", loadAttrsExample));
        }
        return buildings;
    }

    @Override
    public DomainObject findById(Long id) {
        DomainObjectExample example = new DomainObjectExample();
        example.setId(id);
        example.setTable(getEntityTable());
        DomainObject entity = (DomainObject) session.selectOne("org.complitex.pspoffice.commons.strategy.building.Building." + FIND_BY_ID_OPERATION, example);
        entity.setAttributes(session.selectList("org.complitex.pspoffice.commons.strategy.building.Building.loadSimpleAttributes", example));
        for (Attribute complexAttr : (List<Attribute>) session.selectList("org.complitex.pspoffice.commons.strategy.building.Building.loadComplexAttributes", example)) {
            entity.addAttribute(complexAttr);
        }
        return entity;
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

    @Override
    public DomainObject newInstance() {
        DomainObject object = super.newInstance();
        Attribute districtAttr = new Attribute();
        districtAttr.setAttributeId(1L);
        districtAttr.setAttributeTypeId(504L);
        districtAttr.setValueTypeId(504L);
        object.addAttribute(districtAttr);
        return object;
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        List<Attribute> numbers = Lists.newArrayList(Iterables.filter(object.getAttributes(), new Predicate<Attribute>() {

            @Override
            public boolean apply(Attribute attr) {
                return attr.getAttributeTypeId().equals(500L);
            }
        }));
        return displayLocalizedValueUtil.displayValue(numbers.get(0).getLocalizedValues(), locale);
    }

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
        return ImmutableList.of("country", "region", "city", "street");
    }

    private static void configureExampleImpl(DomainObjectExample example, Map<String, Long> ids, String searchTextInput) {
        if (!Strings.isEmpty(searchTextInput)) {
            DomainObjectAttributeExample number = null;
            try {
                number = Iterables.find(example.getAttributeExamples(), new Predicate<DomainObjectAttributeExample>() {

                    @Override
                    public boolean apply(DomainObjectAttributeExample attrExample) {
                        return attrExample.getAttributeTypeId().equals(500L);
                    }
                });
            } catch (NoSuchElementException e) {
                number = new DomainObjectAttributeExample(500L);
                example.addAttributeExample(number);
            }
            number.setValue(searchTextInput);
        }
        Long streetId = ids.get("street");
        if (streetId != null) {
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
            String streetIdAsString = streetId.equals(-1L) ? null : String.valueOf(streetId);
            streetExample.setValue(streetIdAsString);
        }
        Long cityId = ids.get("city");
        example.setParentId(cityId);
        example.setParentEntity("city");
    }

    private static class SearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(SearchComponent component, Map<String, Long> ids, AjaxRequestTarget target) {
            DomainObjectListPanel list = component.findParent(DomainObjectListPanel.class);
            DomainObjectExample example = list.getExample();
            configureExampleImpl(example, ids, null);
            list.refreshContent(target);
        }
    }

    @Override
    public ISearchCallback getParentSearchCallback() {
        return new ParentSearchCallback();
    }

    @Override
    public List<String> getParentSearchFilters() {
        return ImmutableList.of("country", "region", "city");
    }

    private static class ParentSearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(SearchComponent component, final Map<String, Long> ids, final AjaxRequestTarget target) {
            DomainObjectEditPanel edit = component.findParent(DomainObjectEditPanel.class);
            Long cityId = ids.get("city");
            if (cityId != null && cityId > 0) {
                edit.getObject().setParentId(cityId);
                edit.getObject().setParentEntityId(400L);
            } else {
                edit.getObject().setParentId(null);
                edit.getObject().setParentEntityId(null);
            }

            component.getPage().visitChildren(SearchComponent.class, new IVisitor<SearchComponent>() {

                @Override
                public Object component(SearchComponent searchComponent) {
                    if (target != null) {
                        target.addComponent(searchComponent);
                    }
                    return CONTINUE_TRAVERSAL;
                }
            });
        }
    }

    @Override
    public Map<String, String> getChildrenInfo(Locale locale) {
        String commonsBundle = "org.complitex.pspoffice.commons.strategy.Commons";
        return ImmutableMap.of("apartment", ResourceUtil.getString(commonsBundle, "apartment", locale),
                "room", ResourceUtil.getString(commonsBundle, "room", locale));
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

    @Override
    public IValidator getValidator() {
        return new BuildingValidator();
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
    public Class<? extends WebPage> getListPage() {
        return DomainObjectList.class;
    }

    @Override
    public PageParameters getListPageParams() {
        PageParameters params = new PageParameters();
        params.put(DomainObjectList.ENTITY, getEntityTable());
        return params;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.ejb.EJB;
import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionaryfw.dao.EntityBean;
import org.complitex.dictionaryfw.dao.LocaleBean;
import org.complitex.dictionaryfw.dao.SequenceBean;
import org.complitex.dictionaryfw.dao.StringCultureBean;
import org.complitex.dictionaryfw.entity.description.EntityAttributeType;
import org.complitex.dictionaryfw.entity.description.EntityAttributeValueType;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.Attribute;
import org.complitex.dictionaryfw.entity.InsertParameter;
import org.complitex.dictionaryfw.entity.SimpleTypes;
import org.complitex.dictionaryfw.entity.StatusType;
import org.complitex.dictionaryfw.entity.StringCulture;
import org.complitex.dictionaryfw.entity.description.Entity;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;
import org.complitex.dictionaryfw.strategy.web.AbstractComplexAttributesPanel;
import org.complitex.dictionaryfw.strategy.web.IValidator;
import org.complitex.dictionaryfw.util.Numbers;
import org.complitex.dictionaryfw.web.component.search.ISearchCallback;
import org.complitex.dictionaryfw.web.component.search.SearchComponentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public abstract class Strategy {

    private static final Logger log = LoggerFactory.getLogger(Strategy.class);

    public static final String DOMAIN_OBJECT_NAMESPACE = "org.complitex.dictionaryfw.entity.DomainObject";

    public static final String ATTRIBUTE_NAMESPACE = "org.complitex.dictionaryfw.entity.Attribute";

    public static final String FIND_BY_ID_OPERATION = "findById";

    public static final String FIND_OPERATION = "find";

    public static final String COUNT_OPERATION = "count";

    public static final String INSERT_OPERATION = "insert";

    public static final String UPDATE_OPERATION = "update";

    public static final String FIND_PARENT_IN_SEARCH_COMPONENT_OPERATION = "findParentInSearchComponent";

    @EJB
    private StrategyFactory strategyFactory;

    @EJB
    private SequenceBean sequenceBean;

    @EJB
    private StringCultureBean stringBean;

    @EJB
    private EntityBean entityBean;

    @EJB
    private LocaleBean localeBean;

    protected SqlSession session;

    public abstract String getEntityTable();

    public abstract boolean isSimpleAttributeType(EntityAttributeType attributeType);

    public void disable(DomainObject object) {
        object.setStatus(StatusType.INACTIVE);
        session.update(DOMAIN_OBJECT_NAMESPACE + "." + UPDATE_OPERATION, new InsertParameter(getEntityTable(), object));

        Map<String, String> childrenInfoInSystemLocale = getChildrenInfo(new Locale(localeBean.getSystemLocale()));
        if (childrenInfoInSystemLocale != null) {
            for (String childEntity : childrenInfoInSystemLocale.keySet()) {
                DomainObjectExample example = new DomainObjectExample();
                example.setStatus(StatusType.ACTIVE.name());
                Strategy childStrategy = strategyFactory.getStrategy(childEntity);
                childStrategy.configureExample(example, ImmutableMap.of(getEntityTable(), object.getId()), null);
                List<DomainObject> children = childStrategy.find(example);
                for (DomainObject child : children) {
                    childStrategy.disable(child);
                }
            }
        }
    }

    public void enable(DomainObject object) {
        object.setStatus(StatusType.ACTIVE);
        session.update(DOMAIN_OBJECT_NAMESPACE + "." + UPDATE_OPERATION, new InsertParameter(getEntityTable(), object));

        Map<String, String> childrenInfoInSystemLocale = getChildrenInfo(new Locale(localeBean.getSystemLocale()));
        if (childrenInfoInSystemLocale != null) {
            for (String childEntity : childrenInfoInSystemLocale.keySet()) {
                Strategy childStrategy = strategyFactory.getStrategy(childEntity);
                DomainObjectExample example = new DomainObjectExample();
                example.setStatus(StatusType.INACTIVE.name());
                childStrategy.configureExample(example, ImmutableMap.of(getEntityTable(), object.getId()), null);
                List<DomainObject> children = childStrategy.find(example);
                for (DomainObject child : children) {
                    childStrategy.enable(child);
                }
            }
        }
    }

    public DomainObject findById(Long id) {
        DomainObjectExample example = new DomainObjectExample();
        example.setId(id);
        example.setTable(getEntityTable());
        DomainObject object = (DomainObject) session.selectOne(DOMAIN_OBJECT_NAMESPACE + "." + FIND_BY_ID_OPERATION, example);

        updateForNewAttributeTypes(object);
        updateStringsForNewLocales(object);

        return object;
    }

    protected void updateStringsForNewLocales(DomainObject object) {
        for (Attribute attribute : object.getAttributes()) {
            List<StringCulture> strings = attribute.getLocalizedValues();
            if (strings != null) {
                stringBean.updateForNewLocales(strings);
            }
        }
    }

    protected void updateForNewAttributeTypes(DomainObject object) {
        List<Attribute> newAttributes = Lists.newArrayList();
        for (final EntityAttributeType attributeType : getEntity().getEntityAttributeTypes()) {
            if (isSimpleAttributeType(attributeType)) {
                try {
                    Iterables.find(object.getAttributes(), new Predicate<Attribute>() {

                        @Override
                        public boolean apply(Attribute attr) {
                            return attr.getAttributeTypeId().equals(attributeType.getId());
                        }
                    });
                } catch (NoSuchElementException e) {
                    Attribute attribute = new Attribute();
                    EntityAttributeValueType attributeValueType = attributeType.getEntityAttributeValueTypes().get(0);
                    attribute.setAttributeTypeId(attributeType.getId());
                    attribute.setValueTypeId(attributeValueType.getId());
                    attribute.setAttributeId(1L);
                    attribute.setLocalizedValues(stringBean.newStringCultures());
                    newAttributes.add(attribute);
                }
            }
        }
        if (!newAttributes.isEmpty()) {
            object.getAttributes().addAll(newAttributes);
        }
    }

    public List<DomainObject> find(DomainObjectExample example) {
        example.setTable(getEntityTable());
        return session.selectList(DOMAIN_OBJECT_NAMESPACE + "." + FIND_OPERATION, example);
    }

    public int count(DomainObjectExample example) {
        example.setTable(getEntityTable());
        return (Integer) session.selectOne(DOMAIN_OBJECT_NAMESPACE + "." + COUNT_OPERATION, example);
    }

    public Entity getEntity() {
        return entityBean.getEntity(getEntityTable());
    }

    public abstract List<EntityAttributeType> getListColumns();

    public DomainObject newInstance() {
        DomainObject object = new DomainObject();

        for (EntityAttributeType attributeType : getEntity().getEntityAttributeTypes()) {
            if (isSimpleAttributeType(attributeType)) {
                //simple attributes
                Attribute attribute = new Attribute();
                EntityAttributeValueType attributeValueType = attributeType.getEntityAttributeValueTypes().get(0);
                attribute.setAttributeTypeId(attributeType.getId());
                attribute.setValueTypeId(attributeValueType.getId());
                attribute.setAttributeId(1L);
                attribute.setLocalizedValues(stringBean.newStringCultures());
                object.addAttribute(attribute);
            }
        }
        return object;
    }

    protected void insertAttribute(Attribute attribute) {
        List<StringCulture> strings = attribute.getLocalizedValues();
        if (strings == null) {
            //reference attribute
        } else {
            Long generatedStringId = stringBean.insertStrings(strings, getEntityTable());
            attribute.setValueId(generatedStringId);
        }
        session.insert(ATTRIBUTE_NAMESPACE + "." + INSERT_OPERATION, new InsertParameter(getEntityTable(), attribute));
    }

    public void insert(DomainObject object) {
        Date startDate = new Date();
        object.setId(sequenceBean.nextId(getEntityTable()));
        insertDomainObject(object, startDate);
        for (Attribute attribute : object.getAttributes()) {
            attribute.setObjectId(object.getId());
            attribute.setStartDate(startDate);
            insertAttribute(attribute);
        }
    }

    protected void insertDomainObject(DomainObject object, Date startDate) {
        object.setStartDate(startDate);
        session.insert(DOMAIN_OBJECT_NAMESPACE + "." + INSERT_OPERATION, new InsertParameter(getEntityTable(), object));
    }

    public void update(DomainObject oldEntity, DomainObject newEntity) {
        Date updateDate = new Date();

        //attributes comparison
        for (Attribute oldAttr : oldEntity.getAttributes()) {
            boolean removed = true;
            for (Attribute newAttr : newEntity.getAttributes()) {
                if (oldAttr.getAttributeTypeId().equals(newAttr.getAttributeTypeId()) && oldAttr.getAttributeId().equals(newAttr.getAttributeId())) {
                    //the same attribute_type and the same attribute_id
                    removed = false;
                    if (!oldAttr.getStatus().equals(newAttr.getStatus())) {
                        newAttr.setStatus(oldAttr.getStatus());
                        session.update(ATTRIBUTE_NAMESPACE + "." + UPDATE_OPERATION, new InsertParameter(getEntityTable(), newAttr));
                    } else {
                        boolean needToUpdateAttribute = false;

                        List<EntityAttributeValueType> valueDescs = getEntity().
                                getAttributeType(oldAttr.getAttributeTypeId()).
                                getEntityAttributeValueTypes();

                        boolean isSimpleAttribute = false;
                        if (valueDescs.size() == 1) {
                            String attrValueType = valueDescs.get(0).getValueType();

                            try {
                                SimpleTypes simpleType = SimpleTypes.valueOf(attrValueType.toUpperCase());
                                isSimpleAttribute = true;
                                switch (simpleType) {
                                    case STRING_CULTURE: {
                                        isSimpleAttribute = true;
                                        boolean valueChanged = false;
                                        for (StringCulture oldString : oldAttr.getLocalizedValues()) {
                                            for (StringCulture newString : newAttr.getLocalizedValues()) {
                                                //compare strings
                                                if (oldString.getLocale().equals(newString.getLocale())) {
                                                    if (!Strings.isEqual(oldString.getValue(), newString.getValue())) {
                                                        valueChanged = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        if (valueChanged) {
                                            needToUpdateAttribute = true;
                                        }
                                    }
                                    break;

                                    case BOOLEAN:
                                    case DATE:
                                    case DOUBLE:
                                    case INTEGER:
                                    case STRING: {
                                        String oldString = stringBean.getSystemStringCulture(oldAttr.getLocalizedValues()).getValue();
                                        String newString = stringBean.getSystemStringCulture(newAttr.getLocalizedValues()).getValue();
                                        if (!Strings.isEqual(oldString, newString)) {
                                            needToUpdateAttribute = true;
                                        }
                                    }
                                    break;
                                }
                            } catch (IllegalArgumentException e) {
                            }
                        }

                        if (!isSimpleAttribute) {
                            Long oldValueId = oldAttr.getValueId();
                            Long oldValueTypeId = oldAttr.getValueTypeId();
                            Long newValueId = newAttr.getValueId();
                            Long newValueTypeId = newAttr.getValueTypeId();
                            if (!Numbers.isEqual(oldValueId, newValueId) || !Numbers.isEqual(oldValueTypeId, newValueTypeId)) {
                                needToUpdateAttribute = true;
                            }
                        }

                        if (needToUpdateAttribute) {
                            oldAttr.setEndDate(updateDate);
                            oldAttr.setStatus(StatusType.ARCHIVE);
                            session.update(ATTRIBUTE_NAMESPACE + "." + UPDATE_OPERATION, new InsertParameter(getEntityTable(), oldAttr));
                            newAttr.setStartDate(updateDate);
                            insertAttribute(newAttr);
                        }
                    }
                }
            }
            if (removed) {
                oldAttr.setEndDate(updateDate);
                oldAttr.setStatus(StatusType.ARCHIVE);
                session.update(ATTRIBUTE_NAMESPACE + "." + UPDATE_OPERATION, new InsertParameter(getEntityTable(), oldAttr));
            }
        }

        for (Attribute newAttr : newEntity.getAttributes()) {
            boolean added = true;
            for (Attribute oldAttr : oldEntity.getAttributes()) {
                if (oldAttr.getAttributeTypeId().equals(newAttr.getAttributeTypeId()) && oldAttr.getAttributeId().equals(newAttr.getAttributeId())) {
                    //the same attribute_type and the same attribute_id
                    added = false;
                    break;
                }
            }

            if (added) {
                newAttr.setStartDate(updateDate);
                newAttr.setObjectId(newEntity.getId());
                insertAttribute(newAttr);
            }
        }

        boolean needToUpdateObject = false;

        //entity type comparison
        Long oldEntityTypeId = oldEntity.getEntityTypeId();
        Long newEntityTypeId = newEntity.getEntityTypeId();
        if (!Numbers.isEqual(oldEntityTypeId, newEntityTypeId)) {
            needToUpdateObject = true;
        }

        //parent comparison
        Long oldParentId = oldEntity.getParentId();
        Long oldParentEntityId = oldEntity.getParentEntityId();
        Long newParentId = newEntity.getParentId();
        Long newParentEntityId = newEntity.getParentEntityId();

        if (!Numbers.isEqual(oldParentId, newParentId) || !Numbers.isEqual(oldParentEntityId, newParentEntityId)) {
            needToUpdateObject = true;
        }

        if (needToUpdateObject) {
            oldEntity.setStatus(StatusType.ARCHIVE);
            oldEntity.setEndDate(updateDate);
            session.update(DOMAIN_OBJECT_NAMESPACE + "." + UPDATE_OPERATION, new InsertParameter(getEntityTable(), oldEntity));
            insertDomainObject(newEntity, updateDate);
        }
    }

    /*
     * List page related functionality.
     */
    public abstract Class<? extends WebPage> getListPage();

    public abstract PageParameters getListPageParams();

    public abstract List<String> getSearchFilters();

    public abstract ISearchCallback getSearchCallback();

    public abstract String displayDomainObject(DomainObject object, Locale locale);

    public abstract void configureExample(DomainObjectExample example, Map<String, Long> ids, String searchTextInput);

    /*
     * Edit page related functionality.
     */
    public abstract Class<? extends WebPage> getEditPage();

    public abstract PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity);

    public List<String> getParentSearchFilters() {
        return getSearchFilters();
    }

    public abstract ISearchCallback getParentSearchCallback();

    public abstract Map<String, String> getChildrenInfo(Locale locale);

    public static class RestrictedObjectInfo {

        private String entityTable;

        private Long id;

        public RestrictedObjectInfo(String entityTable, Long id) {
            this.entityTable = entityTable;
            this.id = id;
        }

        public String getEntityTable() {
            return entityTable;
        }

        public Long getId() {
            return id;
        }
    }

    public RestrictedObjectInfo findParentInSearchComponent(long id) {
        DomainObjectExample example = new DomainObjectExample();
        example.setTable(getEntityTable());
        example.setId(id);
        Map<String, Object> result = (Map<String, Object>) session.selectOne(DOMAIN_OBJECT_NAMESPACE + "." + FIND_PARENT_IN_SEARCH_COMPONENT_OPERATION, example);
        if (result != null) {
            Long parentId = (Long) result.get("parentId");
            String parentEntity = (String) result.get("parentEntity");
            if (parentId != null && !Strings.isEmpty(parentEntity)) {
                return new RestrictedObjectInfo(parentEntity, parentId);
            }
        }
        return null;
    }

    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelClass() {
        return null;
    }

    public IValidator getValidator() {
        return null;
    }

    /*
     * Helper util method.
     */
    public SearchComponentState getSearchComponentStateForParent(Long parentId, String parentEntity) {
        if (parentId != null && parentEntity != null) {
            SearchComponentState componentState = new SearchComponentState();
            Map<String, Long> ids = Maps.newHashMap();

            RestrictedObjectInfo parentData = new RestrictedObjectInfo(parentEntity, parentId);
            while (parentData != null) {
                String currentParentEntity = parentData.getEntityTable();
                Long currentParentId = parentData.getId();
                ids.put(currentParentEntity, currentParentId);
                parentData = strategyFactory.getStrategy(currentParentEntity).findParentInSearchComponent(currentParentId);
            }
            List<String> searchFilters = getSearchFilters();
            if (searchFilters != null && !searchFilters.isEmpty()) {
                for (String searchFilter : getSearchFilters()) {
                    Long idForFilter = ids.get(searchFilter);
                    if (idForFilter == null) {
                        ids.put(searchFilter, -1L);
                    }
                }

                for (String searchFilter : getSearchFilters()) {
                    DomainObjectExample example = new DomainObjectExample();
                    example.setTable(searchFilter);
                    example.setId(ids.get(searchFilter));
                    example.setStart(0);
                    example.setSize(1);

                    strategyFactory.getStrategy(searchFilter).configureExample(example, ids, null);
                    DomainObject object = new DomainObject();
                    object.setId(-1L);
                    List<DomainObject> objects = strategyFactory.getStrategy(searchFilter).find(example);
                    if (objects != null && !objects.isEmpty()) {
                        object = objects.get(0);
                    }
                    componentState.put(searchFilter, object);
                }
                return componentState;
            }
        }
        return null;
    }

    /*
     * Description metadata
     */
    public abstract String[] getParents();
}

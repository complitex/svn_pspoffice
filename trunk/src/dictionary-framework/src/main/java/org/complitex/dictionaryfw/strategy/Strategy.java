/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionaryfw.dao.EntityDescriptionDao;
import org.complitex.dictionaryfw.dao.LocaleDao;
import org.complitex.dictionaryfw.dao.SequenceDao;
import org.complitex.dictionaryfw.dao.StringCultureDao;
import org.complitex.dictionaryfw.entity.description.AttributeDescription;
import org.complitex.dictionaryfw.entity.description.AttributeValueDescription;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.Attribute;
import org.complitex.dictionaryfw.entity.InsertParameter;
import org.complitex.dictionaryfw.entity.SimpleTypes;
import org.complitex.dictionaryfw.entity.StatusType;
import org.complitex.dictionaryfw.entity.StringCulture;
import org.complitex.dictionaryfw.entity.description.DomainObjectDescription;
import org.complitex.dictionaryfw.entity.description.EntityDescription;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;
import org.complitex.dictionaryfw.strategy.web.AbstractComplexAttributesPanel;
import org.complitex.dictionaryfw.strategy.web.IValidator;
import org.complitex.dictionaryfw.util.Numbers;
import org.complitex.dictionaryfw.web.component.search.ISearchCallback;
import org.complitex.dictionaryfw.web.component.search.SearchComponentState;

/**
 *
 * @author Artem
 */
public abstract class Strategy {

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
    private SequenceDao sequence;

    @EJB
    private StringCultureDao stringDao;

    @EJB
    private EntityDescriptionDao entityDescriptionDao;

    @EJB
    private LocaleDao localeDao;

    protected SqlSession session;

    public abstract String getEntityTable();

    protected EntityDescriptionDao getEntityDescriptionDao() {
        return entityDescriptionDao;
    }

    public abstract boolean isSimpleAttributeDesc(AttributeDescription attributeDescription);

    public DomainObject findById(Long id) {
        DomainObjectExample example = new DomainObjectExample();
        example.setId(id);
        example.setTable(getEntityTable());
        DomainObject entity = (DomainObject) session.selectOne(DOMAIN_OBJECT_NAMESPACE + "." + FIND_BY_ID_OPERATION, example);
        return entity;
    }

    public List<DomainObject> find(DomainObjectExample example) {
        example.setTable(getEntityTable());
        return session.selectList(DOMAIN_OBJECT_NAMESPACE + "." + FIND_OPERATION, example);
    }

    public int count(DomainObjectExample example) {
        example.setTable(getEntityTable());
        return (Integer) session.selectOne(DOMAIN_OBJECT_NAMESPACE + "." + COUNT_OPERATION, example);
    }

    public DomainObjectDescription getDescription() {
        DomainObjectDescription description = new DomainObjectDescription();
        EntityDescription descriptionFromDb = entityDescriptionDao.getEntityDescription(getEntityTable());
        description.setEntityNames(descriptionFromDb.getEntityNames());
        description.setEntityTypes(descriptionFromDb.getEntityTypes());
        description.setAttributeDescriptions(descriptionFromDb.getAttributeDescriptions());
        return description;
    }

    public DomainObject newInstance() {
        DomainObject entity = new DomainObject();

        for (AttributeDescription attributeDesc : getDescription().getAttributeDescriptions()) {
            if (isSimpleAttributeDesc(attributeDesc)) {
                //simple attributes
                Attribute attribute = new Attribute();
                AttributeValueDescription attributeValueDesc = attributeDesc.getAttributeValueDescriptions().get(0);
                if (attributeValueDesc.getValueType().equalsIgnoreCase(SimpleTypes.STRING.name())) {
                    attribute.setAttributeTypeId(attributeDesc.getId());
                    attribute.setValueTypeId(attributeValueDesc.getId());
                    attribute.setAttributeId(1L);
                    List<StringCulture> strings = Lists.newArrayList();
                    for (String locale : localeDao.getAllLocales()) {
                        strings.add(new StringCulture(locale, null));
                    }
                    attribute.setLocalizedValues(strings);
                }
                entity.addAttribute(attribute);
            }
        }
        return entity;
    }

    protected void insertStringCulture(StringCulture stringCulture) {
        stringDao.insert(stringCulture, getEntityTable());
    }

    protected void insertAttribute(Attribute attribute) {
        List<StringCulture> strings = attribute.getLocalizedValues();
        if (strings == null) {
            //reference attribute
        } else {
            long stringId = sequence.nextStringId(getEntityTable());
            for (StringCulture string : strings) {
                string.setId(stringId);
                insertStringCulture(string);
            }
            attribute.setValueId(stringId);
        }
        session.insert(ATTRIBUTE_NAMESPACE + "." + INSERT_OPERATION, new InsertParameter(getEntityTable(), attribute));
    }

    public void insert(DomainObject object) {
        Date startDate = new Date();
        object.setId(sequence.nextId(getEntityTable()));
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

                        List<AttributeValueDescription> valueDescs = getDescription().
                                getAttributeDesc(oldAttr.getAttributeTypeId()).
                                getAttributeValueDescriptions();

                        boolean isSimpleAttribute = false;
                        if (valueDescs.size() == 1) {
                            String attrValueType = valueDescs.get(0).getValueType();

                            if (attrValueType.equalsIgnoreCase(SimpleTypes.STRING.name())) {
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
//    {
//        PageParameters params = new PageParameters();
//        params.put(DomainObjectList.ENTITY, getEntityTable());
//        return params;
//    }

    public abstract List<String> getSearchFilters();

    public abstract ISearchCallback getSearchCallback();

    public abstract String displayDomainObject(DomainObject object, Locale locale);

    public abstract void configureExample(DomainObjectExample example, Map<String, Long> ids, String searchTextInput);

    /*
     * Edit page related functionality.
     */
    public abstract Class<? extends WebPage> getEditPage();

    public abstract PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity);
//        {
//        PageParameters params = new PageParameters();
//        params.put(DomainObjectEdit.ENTITY, getEntityTable());
//        params.put(DomainObjectEdit.OBJECT_ID, objectId);
//        params.put(DomainObjectEdit.PARENT_ID, parentId);
//        params.put(DomainObjectEdit.PARENT_ENTITY, parentEntity);
//        return params;
//    }

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

        public void setEntityTable(String entityTable) {
            this.entityTable = entityTable;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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
                String currentParentEntity = parentData.entityTable;
                Long currentParentId = parentData.id;
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
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy;

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
import org.complitex.dictionaryfw.entity.EntityAttribute;
import org.complitex.dictionaryfw.entity.InsertParameter;
import org.complitex.dictionaryfw.entity.SimpleTypes;
import org.complitex.dictionaryfw.entity.StatusType;
import org.complitex.dictionaryfw.entity.StringCulture;
import org.complitex.dictionaryfw.entity.description.DomainObjectDescription;
import org.complitex.dictionaryfw.entity.description.EntityDescription;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;
import org.complitex.dictionaryfw.strategy.web.DomainObjectEdit;
import org.complitex.dictionaryfw.strategy.web.DomainObjectList;
import org.complitex.dictionaryfw.util.Numbers;
import org.complitex.dictionaryfw.web.component.search.ISearchBehaviour;
import org.complitex.dictionaryfw.web.component.search.ISearchCallback;

/**
 *
 * @author Artem
 */
public abstract class Strategy {

    public static final String ENTITY_NAMESPACE = "org.complitex.dictionaryfw.entity.DomainObject";

    public static final String ENTITY_ATTRIBUTE_NAMESPACE = "org.complitex.dictionaryfw.entity.EntityAttribute";

    public static final String FIND_BY_ID_OPERATION = "findById";

    public static final String FIND_OPERATION = "find";

    public static final String COUNT_OPERATION = "count";

    public static final String INSERT_OPERATION = "insert";

    public static final String UPDATE_OPERATION = "update";

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
        DomainObject entity = (DomainObject) session.selectOne(ENTITY_NAMESPACE + "." + FIND_BY_ID_OPERATION, example);
        return entity;
    }

    public List<DomainObject> find(DomainObjectExample example) {
        example.setTable(getEntityTable());
        return session.selectList(ENTITY_NAMESPACE + "." + FIND_OPERATION, example);
    }

    public int count(DomainObjectExample example) {
        example.setTable(getEntityTable());
        return (Integer) session.selectOne(ENTITY_NAMESPACE + "." + COUNT_OPERATION, example);
    }

    public DomainObjectDescription getDescription() {
        DomainObjectDescription description = new DomainObjectDescription();
        EntityDescription descriptionFromDb = entityDescriptionDao.getEntityDescription(getEntityTable());
        description.setEntityNames(descriptionFromDb.getEntityNames());
        description.setAttributeDescriptions(descriptionFromDb.getAttributeDescriptions());
        return description;
    }

    public DomainObject newInstance() {
        DomainObject entity = new DomainObject();

        for (AttributeDescription attributeDesc : getDescription().getAttributeDescriptions()) {
            if (isSimpleAttributeDesc(attributeDesc)) {
                //simple attributes
                EntityAttribute attribute = new EntityAttribute();
                AttributeValueDescription attributeValueDesc = attributeDesc.getAttributeValueDescriptions().get(0);
                if (attributeValueDesc.getValueType().equalsIgnoreCase(SimpleTypes.STRING.name())) {
                    attribute.setAttributeTypeId(attributeDesc.getId());
                    attribute.setValueTypeId(attributeValueDesc.getId());
                    attribute.setAttributeId(1L);
                    for (String locale : localeDao.getAllLocales()) {
                        attribute.addLocalizedValue(new StringCulture(locale, null));
                    }
                }
                entity.addAttribute(attribute);
            }
        }
        return entity;
    }

    protected void insertStringCulture(StringCulture stringCulture) {
        stringDao.insert(stringCulture, getEntityTable());
    }

    protected void insertAttribute(EntityAttribute attribute) {
        List<StringCulture> strings = attribute.getLocalizedValues();
        long stringId = sequence.nextStringId(getEntityTable());
        for (StringCulture string : strings) {
            string.setId(stringId);
            insertStringCulture(string);
        }
        attribute.setValueId(stringId);
        session.insert(ENTITY_ATTRIBUTE_NAMESPACE + "." + INSERT_OPERATION, new InsertParameter(getEntityTable(), attribute));
    }

    public void insert(DomainObject object) {
        Date startDate = new Date();
        object.setId(sequence.nextId(getEntityTable()));
        insertDomainObject(object, startDate);
        for (EntityAttribute attribute : object.getAttributes()) {
            attribute.setObjectId(object.getId());
            attribute.setStartDate(startDate);
            insertAttribute(attribute);
        }
    }

    protected void insertDomainObject(DomainObject object, Date startDate) {
        object.setStartDate(startDate);
        session.insert(ENTITY_NAMESPACE + "." + INSERT_OPERATION, new InsertParameter(getEntityTable(), object));
    }

    public void update(DomainObject oldEntity, DomainObject newEntity) {
        //for name-based entities there are only simple attributes and parent can change.

        Date updateDate = new Date();

        for (EntityAttribute oldAttr : oldEntity.getAttributes()) {
            boolean removed = true;
            for (EntityAttribute newAttr : newEntity.getAttributes()) {
                if (oldAttr.getAttributeTypeId().equals(newAttr.getAttributeTypeId()) && oldAttr.getAttributeId().equals(newAttr.getAttributeId())) {
                    //the same attribute_type and the same attribute_id
                    removed = false;
                    if (!oldAttr.getStatus().equals(newAttr.getStatus())) {
                        newAttr.setStatus(oldAttr.getStatus());
                        session.update(ENTITY_ATTRIBUTE_NAMESPACE + "." + UPDATE_OPERATION, new InsertParameter(getEntityTable(), newAttr));
                    } else {
                        List<AttributeValueDescription> valueDescs = getDescription().
                                getAttributeDesc(oldAttr.getAttributeTypeId()).
                                getAttributeValueDescriptions();

                        if (valueDescs.size() == 1) {
                            String attrValueType = valueDescs.get(0).getValueType();

                            if (attrValueType.equalsIgnoreCase(SimpleTypes.STRING.name())) {
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
                                    oldAttr.setEndDate(updateDate);
                                    oldAttr.setStatus(StatusType.ARCHIVE);
                                    session.update(ENTITY_ATTRIBUTE_NAMESPACE + "." + UPDATE_OPERATION, new InsertParameter(getEntityTable(), oldAttr));
                                    newAttr.setStartDate(updateDate);
                                    insertAttribute(newAttr);
                                }
                            }
                        }
                    }
                }
            }
            if (removed) {
                oldAttr.setEndDate(updateDate);
                oldAttr.setStatus(StatusType.ARCHIVE);
                session.update(ENTITY_ATTRIBUTE_NAMESPACE + "." + UPDATE_OPERATION, new InsertParameter(getEntityTable(), oldAttr));
            }
        }

        for (EntityAttribute newAttr : newEntity.getAttributes()) {
            boolean added = true;
            for (EntityAttribute oldAttr : oldEntity.getAttributes()) {
                if (oldAttr.getAttributeTypeId().equals(newAttr.getAttributeTypeId()) && oldAttr.getAttributeId().equals(newAttr.getAttributeId())) {
                    //the same attribute_type and the same attribute_id
                    added = false;
                    break;
                }
            }

            if (added) {
                newAttr.setStartDate(updateDate);
                insertAttribute(newAttr);
            }
        }

        //parent comparison
        Long oldParentId = oldEntity.getParentId();
        Long oldParentEntityId = oldEntity.getParentEntityId();
        Long newParentId = newEntity.getParentId();
        Long newParentEntityId = newEntity.getParentEntityId();

        if (!Numbers.isEqual(oldParentId, newParentId) || !Numbers.isEqual(oldParentEntityId, newParentEntityId)) {
            oldEntity.setStatus(StatusType.ARCHIVE);
            oldEntity.setEndDate(updateDate);
            session.update(ENTITY_NAMESPACE + "." + UPDATE_OPERATION, new InsertParameter(getEntityTable(), oldEntity));
            insertDomainObject(newEntity, updateDate);
        }
    }

    /*
     * List page related functionality.
     */
    public Class<? extends WebPage> getListPage() {
        return DomainObjectList.class;
    }

    public PageParameters getListPageParams() {
        PageParameters params = new PageParameters();
        params.put(DomainObjectList.ENTITY, getEntityTable());
        return params;
    }

//    public abstract List<ISearchBehaviour> getSearchBehaviours();

    public abstract List<String> getSearchFilters();

    public abstract ISearchCallback getSearchCallback();

    public abstract String displayDomainObject(DomainObject object, Locale locale);

    public abstract void configureExample(DomainObjectExample example, Map<String, Long> ids, String searchTextInput);

//    public abstract void configureSearchAttribute(DomainObjectExample example, String searchTextInput);

    /*
     * Edit page related functionality.
     */
    public Class<? extends WebPage> getEditPage() {
        return DomainObjectEdit.class;
    }

    public PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity) {
        PageParameters params = new PageParameters();
        params.put(DomainObjectEdit.ENTITY, getEntityTable());
        params.put(DomainObjectEdit.OBJECT_ID, objectId);
        params.put(DomainObjectEdit.PARENT_ID, parentId);
        params.put(DomainObjectEdit.PARENT_ENTITY, parentEntity);
        return params;
    }

//    public abstract List<ISearchBehaviour> getParentSearchBehaviours();
    public List<String> getParentSearchFilters(){
        return getSearchFilters();
    }

    public abstract ISearchCallback getParentSearchCallback();

    public abstract Map<String, String> getChildrenInfo(Locale locale);
}

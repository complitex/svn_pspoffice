/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy;

import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import org.apache.ibatis.session.SqlSession;
import org.complitex.dictionaryfw.dao.EntityDescriptionDao;
import org.complitex.dictionaryfw.dao.LocaleDao;
import org.complitex.dictionaryfw.dao.SequenceDao;
import org.complitex.dictionaryfw.dao.StringCultureDao;
import org.complitex.dictionaryfw.entity.AttributeDescription;
import org.complitex.dictionaryfw.entity.AttributeValueDescription;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.EntityAttribute;
import org.complitex.dictionaryfw.entity.EntityDescription;
import org.complitex.dictionaryfw.entity.InsertParameter;
import org.complitex.dictionaryfw.entity.SimpleTypes;
import org.complitex.dictionaryfw.entity.StringCulture;
import org.complitex.dictionaryfw.entity.description.DomainObjectDescription;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;

/**
 *
 * @author Artem
 */
public abstract class Strategy {

    public static final String ENTITY_NAMESPACE = "org.complitex.dictionaryfw.entity.Entity";

    public static final String ENTITY_ATTRIBUTE_NAMESPACE = "org.complitex.dictionaryfw.entity.EntityAttribute";

    public static final String FIND_BY_ID_OPERATION = "findById";

    public static final String FIND_OPERATION = "find";

    public static final String COUNT_OPERATION = "count";

    public static final String INSERT_OPERATION = "insert";

    @EJB
    private SequenceDao sequence;

    @EJB
    private StringCultureDao stringDao;

    @EJB
    private EntityDescriptionDao entityDescriptionDao;

    @EJB
    private LocaleDao localeDao;

    private SqlSession session;

    public abstract String getEntityTable();

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
        description.setEntityTable(getEntityTable());
        description.setSimpleAttributeDescs(entityDescriptionDao.getEntityDescription(getEntityTable()).getSimpleAttributeDescs());
        return description;
    }

    public DomainObject newInstance() {
        DomainObject entity = new DomainObject();

        //simple attributes
        for (AttributeDescription attributeDesc : getDescription().getSimpleAttributeDescs()) {
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

    public void insert(DomainObject entity, String entityTable, DomainObjectDescription description) {
        Date startDate = new Date();
        entity.setId(sequence.nextId(entityTable));
        entity.setStartDate(startDate);
        session.insert(ENTITY_NAMESPACE + "." + INSERT_OPERATION, new InsertParameter(entityTable, entity));
        //store simple attributes
        for (EntityAttribute attribute : entity.getSimpleAttributes(description)) {
            attribute.setEntityId(entity.getId());
            attribute.setStartDate(startDate);
            insertAttribute(attribute);
        }
    }
//    public void update(T oldEntity, T newEntity) {
//        //for name-based entities like Apartment there are only simple attributes can change.
//
//        Date updateDate = new Date();
//
//        for (EntityAttribute oldAttr : oldEntity.getAttributes()) {
//            for (EntityAttribute newAttr : newEntity.getAttributes()) {
//                if (oldAttr.getAttributeTypeId().equals(newAttr.getAttributeTypeId())) {
//                    //the same attribute type.
//
//                    String attrValueType =
//                            entityDescriptionDao.getEntityDescription(getTable()).
//                            getAttributeDesc(oldAttr.getAttributeTypeId()).
//                            getAttributeValueDescriptions().get(0).getValueType();
//
//                    if (attrValueType.equalsIgnoreCase(SimpleTypes.STRING.name())) {
//                        boolean changed = false;
//
//                        for (StringCulture oldString : oldAttr.getLocalizedValues()) {
//                            for (StringCulture newString : newAttr.getLocalizedValues()) {
//                                //compare strings
//                                if (oldString.getLocale().equals(newString.getLocale())) {
//                                    if (!Strings.isEqual(oldString.getValue(), newString.getValue())) {
//                                        changed = true;
//                                    }
//                                }
//                            }
//                        }
//
//                        if (changed) {
//                            oldAttr.setEndDate(updateDate);
//                            oldAttr.setStatus(StatusType.ARCHIVE);
//                            session.update("org.complitex.dictionaryfw.entity.EntityAttribute.update", new InsertParameter(getTable(), oldAttr));
//                            newAttr.setStartDate(updateDate);
//                            insertAttribute(newAttr);
//                        }
//                    }
//                }
//            }
//        }
//    }
}

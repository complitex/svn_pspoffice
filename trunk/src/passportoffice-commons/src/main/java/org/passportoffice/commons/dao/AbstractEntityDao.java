/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.commons.dao;

import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.util.string.Strings;
import org.passportoffice.commons.entity.AttributeDescription;
import org.passportoffice.commons.entity.AttributeValueDescription;
import org.passportoffice.commons.entity.Entity;
import org.passportoffice.commons.entity.EntityAttribute;
import org.passportoffice.commons.entity.InsertParameter;
import org.passportoffice.commons.entity.SimpleTypes;
import org.passportoffice.commons.entity.StatusType;
import org.passportoffice.commons.entity.StringCulture;
import org.passportoffice.commons.entity.example.IEntityExample;
import org.passportoffice.commons.strategy.IDao;

/**
 *
 * @author Artem
 */
public abstract class AbstractEntityDao<T extends Entity, E extends IEntityExample> implements IDao<T, E> {

    public static final String ENTITY_NAMESPACE = "org.passportoffice.commons.entity.Entity";

    public static final String ENTITY_ATTRIBUTE_NAMESPACE = "org.passportoffice.commons.entity.EntityAttribute";

    public static final String FIND_BY_ID_OPERATION = "findById";

    public static final String FIND_OPERATION = "find";

    public static final String COUNT_OPERATION = "count";

    public static final String INSERT_OPERATION = "insert";

    @EJB
    private SequenceDao sequence;

    @EJB
    private StringCultureDao stringDao;

    @EJB
    private LocaleDao localeDao;

    @EJB
    private EntityDescriptionDao entityDescriptionDao;

    private SqlSession session;

    @Override
    public T findById(E example) {
        return (T) session.selectOne(getNamespace() + "." + FIND_BY_ID_OPERATION, example);
    }

    protected abstract String getNamespace();

    @Override
    public List<T> find(E example) {
        return session.selectList(getNamespace() + "." + FIND_OPERATION, example);
    }

    @Override
    public int count(E example) {
        return (Integer) session.selectOne(getNamespace() + "." + COUNT_OPERATION, example);
    }

    protected void configureNewEntity(T newEntity) {
        for (AttributeDescription attributeDesc : entityDescriptionDao.getEntityDescription(getTable()).getAttributeDescriptions()) {

            EntityAttribute attribute = new EntityAttribute();
            if (attributeDesc.getAttributeValueDescriptions().size() == 1) {
                AttributeValueDescription attributeValueDesc = attributeDesc.getAttributeValueDescriptions().get(0);
                if (attributeValueDesc.getValueType().equalsIgnoreCase(SimpleTypes.STRING.name())) {
                    attribute.setAttributeTypeId(attributeDesc.getId());
                    attribute.setValueTypeId(attributeValueDesc.getId());
                    attribute.setAttributeId(1L);
                    for (String locale : localeDao.getAllLocales()) {
                        attribute.addLocalizedValue(new StringCulture(locale, null));
                    }
                }
            }
            newEntity.addAttribute(attribute);
        }
    }

    public abstract T newInstance();

    protected void insertStringCulture(StringCulture stringCulture) {
        stringDao.insert(stringCulture, getTable());
    }

    protected void insertAttribute(EntityAttribute attribute) {
        List<StringCulture> strings = attribute.getLocalizedValues();
        long stringId = sequence.nextStringId(getTable());
        for (StringCulture string : strings) {
            string.setId(stringId);
            insertStringCulture(string);
        }
        attribute.setValueId(stringId);
        session.insert(ENTITY_ATTRIBUTE_NAMESPACE + "." + INSERT_OPERATION, new InsertParameter(getTable(), attribute));
    }

    @Override
    public void insert(T entity) {
        Date startDate = new Date();
        entity.setId(sequence.nextId(getTable()));
        entity.setStartDate(startDate);
        session.insert(ENTITY_NAMESPACE + "." + INSERT_OPERATION, new InsertParameter(getTable(), entity));
        //store simple attributes
        for (EntityAttribute attribute : entity.getSimpleAttributes(entityDescriptionDao.getEntityDescription(getTable()))) {
            attribute.setEntityId(entity.getId());
            attribute.setStartDate(startDate);
            insertAttribute(attribute);
        }
    }

    @Override
    public void update(T oldEntity, T newEntity) {
        //for name-based entities like Apartment there are only simple attributes can change.

        Date updateDate = new Date();

        for (EntityAttribute oldAttr : oldEntity.getAttributes()) {
            for (EntityAttribute newAttr : newEntity.getAttributes()) {
                if (oldAttr.getAttributeTypeId().equals(newAttr.getAttributeTypeId())) {
                    //the same attribute type.

                    String attrValueType =
                            entityDescriptionDao.getEntityDescription(getTable()).
                            getAttributeDesc(oldAttr.getAttributeTypeId()).
                            getAttributeValueDescriptions().get(0).getValueType();

                    if (attrValueType.equalsIgnoreCase(SimpleTypes.STRING.name())) {
                        boolean changed = false;

                        for (StringCulture oldString : oldAttr.getLocalizedValues()) {
                            for (StringCulture newString : newAttr.getLocalizedValues()) {
                                //compare strings
                                if (oldString.getLocale().equals(newString.getLocale())) {
                                    if (!Strings.isEqual(oldString.getValue(), newString.getValue())) {
                                        changed = true;
                                    }
                                }
                            }
                        }

                        if (changed) {
                            oldAttr.setEndDate(updateDate);
                            oldAttr.setStatus(StatusType.ARCHIVE);
                            session.update("org.passportoffice.commons.entity.EntityAttribute.update", new InsertParameter(getTable(), oldAttr));
                            newAttr.setStartDate(updateDate);
                            insertAttribute(newAttr);
                        }
                    }
                }
            }
        }
    }
}

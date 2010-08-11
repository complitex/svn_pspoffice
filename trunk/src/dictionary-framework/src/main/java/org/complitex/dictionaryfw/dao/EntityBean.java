/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.dao;

import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.ibatis.session.SqlSession;
import org.complitex.dictionaryfw.dao.aop.SqlSessionInterceptor;
import org.complitex.dictionaryfw.entity.description.Entity;
import org.complitex.dictionaryfw.entity.description.EntityAttributeType;
import org.complitex.dictionaryfw.entity.description.EntityAttributeValueType;

/**
 *
 * @author Artem
 */
@Stateless
@Interceptors({SqlSessionInterceptor.class})
public class EntityBean {

    private static final String ENTITY_NAMESPACE = "org.complitex.dictionaryfw.entity.description.Entity";

    @EJB
    private StringCultureBean stringBean;

    private SqlSession session;

    public Entity getEntity(String entity) {
        return (Entity) session.selectOne(ENTITY_NAMESPACE + ".load", entity);
    }

    public EntityAttributeType newAttributeType() {
        EntityAttributeType attributeType = new EntityAttributeType();
        attributeType.setAttributeNames(stringBean.newStringCultures());
        attributeType.setEntityAttributeValueTypes(new ArrayList<EntityAttributeValueType>());
        return attributeType;
    }

    public void insertAttributeType(EntityAttributeType attributeType, long entityId) {
        attributeType.setEntityId(entityId);
        Long stringId = stringBean.insertStrings(attributeType.getAttributeNames(), null);
        attributeType.setAttributeNameId(stringId);
        session.insert(ENTITY_NAMESPACE + ".insertAttributeType", attributeType);
        EntityAttributeValueType valueType = attributeType.getEntityAttributeValueTypes().get(0);
        valueType.setAttributeTypeId(attributeType.getId());
        session.insert(ENTITY_NAMESPACE + ".insertValueType", valueType);
    }

    public Collection<String> getAllEntities() {
        return session.selectList(ENTITY_NAMESPACE + ".allEntities");
    }
}

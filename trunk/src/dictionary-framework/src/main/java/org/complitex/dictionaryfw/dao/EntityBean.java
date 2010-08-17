/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.dao;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.ibatis.session.SqlSession;
import org.complitex.dictionaryfw.dao.aop.SqlSessionInterceptor;
import org.complitex.dictionaryfw.entity.description.Entity;
import org.complitex.dictionaryfw.entity.description.EntityAttributeType;
import org.complitex.dictionaryfw.entity.description.EntityAttributeValueType;
import org.complitex.dictionaryfw.entity.description.EntityType;
import org.complitex.dictionaryfw.strategy.StrategyFactory;

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

    @EJB
    private StrategyFactory strategyFactory;

    private SqlSession session;

    public Entity getEntity(String entity) {
        return (Entity) session.selectOne(ENTITY_NAMESPACE + ".load", ImmutableMap.of("entity", entity));
    }

    public Entity getFullEntity(String entity) {
        return (Entity) session.selectOne(ENTITY_NAMESPACE + ".load", ImmutableMap.of("entity", entity, "all", ""));
    }

    public EntityAttributeType newAttributeType() {
        EntityAttributeType attributeType = new EntityAttributeType();
        attributeType.setAttributeNames(stringBean.newStringCultures());
        attributeType.setEntityAttributeValueTypes(new ArrayList<EntityAttributeValueType>());
        return attributeType;
    }

    public EntityType newEntityType() {
        EntityType entityType = new EntityType();
        entityType.setEntityTypeNames(stringBean.newStringCultures());
        return entityType;
    }

    public void save(Entity oldEntity, Entity newEntity) {
        Date updateDate = new Date();

        //attributes
        Set<Long> toDeleteAttributeIds = Sets.newHashSet();

        for (EntityAttributeType oldAttributeType : oldEntity.getEntityAttributeTypes()) {
            boolean removed = true;
            for (EntityAttributeType newAttributeType : newEntity.getEntityAttributeTypes()) {
                if (oldAttributeType.getId().equals(newAttributeType.getId())) {
                    removed = false;
                    break;
                }
            }
            if (removed) {
                toDeleteAttributeIds.add(oldAttributeType.getId());
            }
        }
        removeAttributeTypes(oldEntity.getEntityTable(), toDeleteAttributeIds, updateDate);

        for (EntityAttributeType attributeType : newEntity.getEntityAttributeTypes()) {
            if (attributeType.getId() == null) {
                insertAttributeType(attributeType, newEntity.getId(), updateDate);
            }
        }

        //entity types
        Set<Long> toDeleteEntityTypeIds = Sets.newHashSet();

        for (EntityType oldEntityType : oldEntity.getEntityTypes()) {
            boolean removed = true;
            for (EntityType newEntityType : newEntity.getEntityTypes()) {
                if (oldEntityType.getId().equals(newEntityType.getId())) {
                    removed = false;
                    break;
                }
            }
            if (removed) {
                toDeleteEntityTypeIds.add(oldEntityType.getId());
            }
        }
        removeEntityTypes(toDeleteEntityTypeIds, updateDate);

        for (EntityType entityType : newEntity.getEntityTypes()) {
            if (entityType.getId() == null) {
                insertEntityType(entityType, newEntity.getId(), updateDate);
            }
        }
    }

    protected void insertAttributeType(EntityAttributeType attributeType, long entityId, Date startDate) {
        attributeType.setStartDate(startDate);
        attributeType.setEntityId(entityId);
        Long stringId = stringBean.insertStrings(attributeType.getAttributeNames(), null);
        attributeType.setAttributeNameId(stringId);
        session.insert(ENTITY_NAMESPACE + ".insertAttributeType", attributeType);
        EntityAttributeValueType valueType = attributeType.getEntityAttributeValueTypes().get(0);
        valueType.setAttributeTypeId(attributeType.getId());
        session.insert(ENTITY_NAMESPACE + ".insertValueType", valueType);
    }

    protected void insertEntityType(EntityType entityType, long entityId, Date startDate) {
        entityType.setStartDate(startDate);
        entityType.setEntityId(entityId);
        Long stringId = stringBean.insertStrings(entityType.getEntityTypeNames(), null);
        entityType.setEntityTypeNameId(stringId);
        session.insert(ENTITY_NAMESPACE + ".insertEntityType", entityType);
    }

    protected void removeAttributeTypes(String entityTable, Collection<Long> attributeTypeIds, Date endDate) {
        if (attributeTypeIds != null && !attributeTypeIds.isEmpty()) {
            Map<String, Object> params = ImmutableMap.<String, Object>builder().
                    put("endDate", endDate).
                    put("attributeTypeIds", attributeTypeIds).
                    build();
            session.update(ENTITY_NAMESPACE + ".removeAttributeTypes", params);
            strategyFactory.getStrategy(entityTable).archiveAttributes(attributeTypeIds, endDate);
        }
    }

    protected void removeEntityTypes(Collection<Long> entityTypeIds, Date endDate) {
        if (entityTypeIds != null && !entityTypeIds.isEmpty()) {
            Map<String, Object> params = ImmutableMap.<String, Object>builder().
                    put("endDate", endDate).
                    put("entityTypeIds", entityTypeIds).
                    build();
            session.update(ENTITY_NAMESPACE + ".removeEntityTypes", params);
        }
    }

    public Collection<String> getAllEntities() {
        return session.selectList(ENTITY_NAMESPACE + ".allEntities");
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.entity.description;

import java.io.Serializable;
import java.util.List;
import org.complitex.dictionaryfw.entity.StringCulture;

/**
 *
 * @author Artem
 */
public class Entity implements Serializable {

    private Long id;

    private String entityTable;

    private List<StringCulture> entityNames;

    private List<EntityAttributeType> entityAttributeTypes;

    private List<EntityType> entityTypes;

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

    public List<EntityAttributeType> getEntityAttributeTypes() {
        return entityAttributeTypes;
    }

    public void setEntityAttributeType(List<EntityAttributeType> entityAttributeTypes) {
        this.entityAttributeTypes = entityAttributeTypes;
    }

    public List<StringCulture> getEntityNames() {
        return entityNames;
    }

    public void setEntityNames(List<StringCulture> entityNames) {
        this.entityNames = entityNames;
    }

    public List<EntityType> getEntityTypes() {
        return entityTypes;
    }

    public void setEntityTypes(List<EntityType> entityTypes) {
        this.entityTypes = entityTypes;
    }

    public EntityAttributeType getAttributeType(Long attributeTypeId) {
        for (EntityAttributeType attributeDescription : getEntityAttributeTypes()) {
            if (attributeDescription.getId().equals(attributeTypeId)) {
                return attributeDescription;
            }
        }
        return null;
    }
}

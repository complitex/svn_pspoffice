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
public class EntityAttributeType implements Serializable {

    private Long id;

    private Long entityId;

    private boolean mandatory;

    private Long attributeNameId;

    private List<StringCulture> attributeNames;

    private List<EntityAttributeValueType> entityAttributeValueTypes;

    public List<StringCulture> getAttributeNames() {
        return attributeNames;
    }

    public void setAttributeNames(List<StringCulture> attributeNames) {
        this.attributeNames = attributeNames;
    }

    public List<EntityAttributeValueType> getEntityAttributeValueTypes() {
        return entityAttributeValueTypes;
    }

    public void setEntityAttributeValueTypes(List<EntityAttributeValueType> entityAttributeValueTypes) {
        this.entityAttributeValueTypes = entityAttributeValueTypes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getAttributeNameId() {
        return attributeNameId;
    }

    public void setAttributeNameId(Long attributeNameId) {
        this.attributeNameId = attributeNameId;
    }
}

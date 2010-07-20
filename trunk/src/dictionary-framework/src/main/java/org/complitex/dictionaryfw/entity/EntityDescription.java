/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.entity;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Artem
 */
public class EntityDescription implements Serializable {

    private Long id;

    private String entityTable;

    private List<AttributeDescription> attributeDescriptions;

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

    public List<AttributeDescription> getAttributeDescriptions() {
        return attributeDescriptions;
    }

    public void setAttributeDescriptions(List<AttributeDescription> attributeDescriptions) {
        this.attributeDescriptions = attributeDescriptions;
    }

    public AttributeDescription getAttributeDesc(Long attributeTypeId){
        for(AttributeDescription attributeDescription : getAttributeDescriptions()){
            if(attributeDescription.getId().equals(attributeTypeId)){
                return attributeDescription;
            }
        }
        return null;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.entity.description;

import java.util.List;
import org.complitex.dictionaryfw.entity.AttributeDescription;

/**
 *
 * @author Artem
 */
public class DomainObjectDescription {

    private String entityTable;

    private List<AttributeDescription> simpleAttributeDescs;

    private List<AttributeGroupDescription> groupDescs;

    private List<String> parentTypes;

    private List<AttributeDescription> filterAttributes;

    public String getEntityTable() {
        return entityTable;
    }

    public void setEntityTable(String entityTable) {
        this.entityTable = entityTable;
    }

    public List<AttributeDescription> getFilterAttributes() {
        return filterAttributes;
    }

    public void setFilterAttributes(List<AttributeDescription> filterAttributes) {
        this.filterAttributes = filterAttributes;
    }

    public List<AttributeGroupDescription> getGroupDescs() {
        return groupDescs;
    }

    public void setGroupDescs(List<AttributeGroupDescription> groupDescs) {
        this.groupDescs = groupDescs;
    }

    public List<String> getParentTypes() {
        return parentTypes;
    }

    public void setParentTypes(List<String> parentTypes) {
        this.parentTypes = parentTypes;
    }

    public List<AttributeDescription> getSimpleAttributeDescs() {
        return simpleAttributeDescs;
    }

    public void setSimpleAttributeDescs(List<AttributeDescription> simpleAttributeDescs) {
        this.simpleAttributeDescs = simpleAttributeDescs;
    }

    public AttributeDescription getAttributeDesc(Long attributeTypeId) {
        for (AttributeDescription attributeDescription : getSimpleAttributeDescs()) {
            if (attributeDescription.getId().equals(attributeTypeId)) {
                return attributeDescription;
            }
        }

        for (AttributeGroupDescription attributeGroupDescription : getGroupDescs()) {
            for (AttributeDescription attributeDescription : attributeGroupDescription.getAttributeDescs()) {
                if (attributeDescription.getId().equals(attributeTypeId)) {
                    return attributeDescription;
                }
            }
        }

        return null;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.entity.description;

import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import org.complitex.dictionaryfw.entity.StringCulture;

/**
 *
 * @author Artem
 */
public class DomainObjectDescription implements Serializable {

    private String entityTable;

    private List<StringCulture> entityNames;

    private List<AttributeDescription> simpleAttributeDescs;

    private List<AttributeGroupDescription> groupDescs;

    private List<String> parentTypes;

    private List<AttributeDescription> filterAttributes = Lists.newArrayList();

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

    public void addFilterAttribute(AttributeDescription filterAttribute){
        filterAttributes.add(filterAttribute);
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

    public List<StringCulture> getEntityNames() {
        return entityNames;
    }

    public void setEntityNames(List<StringCulture> entityNames) {
        this.entityNames = entityNames;
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

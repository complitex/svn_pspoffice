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

    private List<StringCulture> entityNames;

    private List<EntityType> entityTypes;

    private List<AttributeDescription> attributeDescriptions;

    private List<AttributeDescription> filterAttributes = Lists.newArrayList();

    public List<AttributeDescription> getFilterAttributes() {
        return filterAttributes;
    }

    public void setFilterAttributes(List<AttributeDescription> filterAttributes) {
        this.filterAttributes = filterAttributes;
    }

    public void addFilterAttribute(AttributeDescription filterAttribute) {
        filterAttributes.add(filterAttribute);
    }

    public List<AttributeDescription> getAttributeDescriptions() {
        return attributeDescriptions;
    }

    public void setAttributeDescriptions(List<AttributeDescription> attributeDescriptions) {
        this.attributeDescriptions = attributeDescriptions;
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

    public AttributeDescription getAttributeDesc(Long attributeTypeId) {
        for (AttributeDescription attributeDescription : getAttributeDescriptions()) {
            if (attributeDescription.getId().equals(attributeTypeId)) {
                return attributeDescription;
            }
        }
        return null;
    }
}

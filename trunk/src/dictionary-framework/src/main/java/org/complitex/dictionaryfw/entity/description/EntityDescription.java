/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.entity.description;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import org.complitex.dictionaryfw.entity.StringCulture;

/**
 *
 * @author Artem
 */
public class EntityDescription implements Serializable {

    private Long id;

    private String entityTable;

    private List<StringCulture> entityNames;

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

    public List<StringCulture> getEntityNames() {
        return entityNames;
    }

    public void setEntityNames(List<StringCulture> entityNames) {
        this.entityNames = entityNames;
    }

    public List<AttributeDescription> getSimpleAttributeDescs() {
        return Lists.newArrayList(Iterables.filter(attributeDescriptions, new Predicate<AttributeDescription>() {

            @Override
            public boolean apply(AttributeDescription attrDesc) {
                return attrDesc.isSimple();
            }
        }));
    }
}

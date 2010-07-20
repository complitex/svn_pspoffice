/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.commons.entity;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Artem
 */
public abstract class Entity implements Serializable {

    private Long id;

    private StatusType status = StatusType.ACTIVE;

    private Date startDate;

    private Date endDate;

    private Long parentId;

    private Long parentTypeId;

    private String parentType;

    private Long entityTypeId;

    private List<EntityAttribute> attributes = new ArrayList<EntityAttribute>();

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getEntityTypeId() {
        return entityTypeId;
    }

    public void setEntityTypeId(Long entityTypeId) {
        this.entityTypeId = entityTypeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public List<EntityAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<EntityAttribute> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(EntityAttribute attribute) {
        attributes.add(attribute);
    }

    public Long getParentTypeId() {
        return parentTypeId;
    }

    public void setParentTypeId(Long parentTypeId) {
        this.parentTypeId = parentTypeId;
    }

    public List<EntityAttribute> getSimpleAttributes(final EntityDescription entityDescription) {
        return Lists.newArrayList(Iterables.filter(getAttributes(), new Predicate<EntityAttribute>() {

            @Override
            public boolean apply(EntityAttribute attr) {
                AttributeDescription attrDesc = entityDescription.getAttributeDesc(attr.getAttributeTypeId());
                return (attrDesc.getAttributeValueDescriptions().size() == 1
                        && SimpleTypes.isSimpleType(attrDesc.getAttributeValueDescriptions().get(0).getValueType()))
                        ? true : false;
            }
        }));
    }

    public abstract String getDisplayName();
}

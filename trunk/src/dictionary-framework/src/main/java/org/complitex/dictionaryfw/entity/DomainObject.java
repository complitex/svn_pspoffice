/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.entity;

import org.complitex.dictionaryfw.entity.description.AttributeDescription;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.complitex.dictionaryfw.entity.description.DomainObjectDescription;

/**
 *
 * @author Artem
 */
public class DomainObject implements Serializable {

    private Long id;

    private Long entityId;

    private StatusType status = StatusType.ACTIVE;

    private Date startDate;

    private Date endDate;

    private Long parentId;

    private Long parentEntityId;

    private Long entityTypeId;

    private DomainObject parent;

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

    public Long getParentEntityId() {
        return parentEntityId;
    }

    public void setParentEntityId(Long parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    public DomainObject getParent() {
        return parent;
    }

    public void setParent(DomainObject parent) {
        this.parent = parent;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public List<EntityAttribute> getSimpleAttributes(final DomainObjectDescription description) {
        return Lists.newArrayList(Iterables.filter(getAttributes(), new Predicate<EntityAttribute>() {

            @Override
            public boolean apply(EntityAttribute attr) {
                AttributeDescription attrDesc = description.getAttributeDesc(attr.getAttributeTypeId());
                return attrDesc.isSimple();
            }
        }));
    }
}

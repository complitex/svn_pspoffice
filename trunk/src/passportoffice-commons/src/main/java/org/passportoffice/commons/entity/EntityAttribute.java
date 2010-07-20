/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.commons.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Artem
 */
public class EntityAttribute implements Serializable {

    private Long attributeId;

    private Long entityId;

    private Long attributeTypeId;

    private Long valueId;

    private Long valueTypeId;

    private Entity value;

    private String localizedValue;

    private List<StringCulture> localizedValues = new ArrayList<StringCulture>();

    private Date startDate;

    private Date endDate;

    private StatusType status = StatusType.ACTIVE;

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public Long getAttributeTypeId() {
        return attributeTypeId;
    }

    public void setAttributeTypeId(Long attributeTypeId) {
        this.attributeTypeId = attributeTypeId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
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

    public Long getValueId() {
        return valueId;
    }

    public void setValueId(Long valueId) {
        this.valueId = valueId;
    }

    public String getLocalizedValue() {
        return localizedValue;
    }

    public void setLocalizedValue(String localizedValue) {
        this.localizedValue = localizedValue;
    }

    public List<StringCulture> getLocalizedValues() {
        return localizedValues;
    }

    public void setLocalizedValues(List<StringCulture> localizedValues) {
        this.localizedValues = localizedValues;
    }

    public void addLocalizedValue(StringCulture localizedValue) {
        localizedValues.add(localizedValue);
    }

    public Entity getValue() {
        return value;
    }

    public void setValue(Entity value) {
        this.value = value;
    }

    public Long getValueTypeId() {
        return valueTypeId;
    }

    public void setValueTypeId(Long valueTypeId) {
        this.valueTypeId = valueTypeId;
    }
}

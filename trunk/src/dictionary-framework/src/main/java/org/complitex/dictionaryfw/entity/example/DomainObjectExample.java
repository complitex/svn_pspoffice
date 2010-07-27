/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.entity.example;

import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Artem
 */
public class DomainObjectExample implements Serializable {

    private String table;

    private int start;

    private int size;

    private String locale;

    private Long orderByAttribureTypeId;

    private boolean asc;

    private Long id;

    private String parentEntity;

    private Long parentId;

    private Map<String, Object> additionalParams = Maps.newHashMap();

    private List<DomainObjectAttributeExample> attributeExamples = new ArrayList<DomainObjectAttributeExample>();

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long entityId) {
        this.id = entityId;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }

    public Long getOrderByAttribureTypeId() {
        return orderByAttribureTypeId;
    }

    public void setOrderByAttribureTypeId(Long orderByAttribureTypeId) {
        this.orderByAttribureTypeId = orderByAttribureTypeId;
    }

    public List<DomainObjectAttributeExample> getAttributeExamples() {
        return attributeExamples;
    }

    public void setAttributeExamples(List<DomainObjectAttributeExample> attributeExamples) {
        this.attributeExamples = attributeExamples;
    }

    public void addAttributeExample(DomainObjectAttributeExample attributeExample) {
        attributeExamples.add(attributeExample);
    }

    public String getParentEntity() {
        return parentEntity;
    }

    public void setParentEntity(String parentEntity) {
        this.parentEntity = parentEntity;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Map<String, Object> getAdditionalParams() {
        return additionalParams;
    }

    public void setAdditionalParams(Map<String, Object> additionalParams) {
        this.additionalParams = additionalParams;
    }

    public void addAdditionalParam(String key, Object value) {
        additionalParams.put(key, value);
    }
}

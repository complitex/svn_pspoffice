/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.commons.entity;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public class AttributeValueDescription implements Serializable {

    private Long id;

    private String valueType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }
}

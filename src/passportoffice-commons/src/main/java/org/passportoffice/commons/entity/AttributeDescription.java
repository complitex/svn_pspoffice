/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.commons.entity;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.util.string.Strings;

/**
 *
 * @author Artem
 */
public class AttributeDescription implements Serializable {

    private Long id;

    private boolean mandatory;

    List<StringCulture> attributeNames;

    List<AttributeValueDescription> attributeValueDescriptions;

    public List<StringCulture> getAttributeNames() {
        return attributeNames;
    }

    public void setAttributeNames(List<StringCulture> attributeNames) {
        this.attributeNames = attributeNames;
    }

    public List<AttributeValueDescription> getAttributeValueDescriptions() {
        return attributeValueDescriptions;
    }

    public void setAttributeValueDescriptions(List<AttributeValueDescription> attributeValueDescriptions) {
        this.attributeValueDescriptions = attributeValueDescriptions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getLocalizedAttributeName(final Locale locale) {
        for (StringCulture culture : getAttributeNames()) {
            if (new Locale(culture.getLocale()).equals(locale)) {
                return culture.getValue();
            }
        }
        return getAttributeNames().get(0).getValue();
    }
}

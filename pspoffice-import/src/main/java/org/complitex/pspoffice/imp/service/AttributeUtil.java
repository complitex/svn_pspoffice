/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.imp.service;

import java.util.List;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.StringCulture;

/**
 *
 * @author Artem
 */
public final class AttributeUtil {

    private AttributeUtil() {
    }

    public static void setValue(List<StringCulture> values, long localeId, String value) {
        for (StringCulture culture : values) {
            if (culture.getLocaleId().equals(localeId)) {
                culture.setValue(value);
            }
        }
    }

    public static void setValue(Attribute attribute, long localeId, String value) {
        setValue(attribute.getLocalizedValues(), localeId, value);
    }

    public static void setValue(Attribute attribute, String value) {
        for (StringCulture culture : attribute.getLocalizedValues()) {
            culture.setValue(value);
        }
    }
}

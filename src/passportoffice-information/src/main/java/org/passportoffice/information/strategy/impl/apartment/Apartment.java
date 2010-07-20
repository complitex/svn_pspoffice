/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.information.strategy.impl.apartment;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.passportoffice.commons.entity.Entity;
import org.passportoffice.commons.entity.EntityAttribute;
import org.passportoffice.commons.entity.StatusType;

/**
 *
 * @author Artem
 */
public class Apartment extends Entity {

    @Override
    public String getDisplayName() {
        EntityAttribute nameAttribute = Iterables.find(getAttributes(), new Predicate<EntityAttribute>() {

            @Override
            public boolean apply(EntityAttribute attribute) {
                return attribute.getAttributeTypeId().equals(1L) && attribute.getStatus() == StatusType.ACTIVE;
            }
        });
        if (nameAttribute.getLocalizedValue() != null) {
            return nameAttribute.getLocalizedValue();
        }
        if (!nameAttribute.getLocalizedValues().isEmpty()) {
            //first string is for system locale
            return nameAttribute.getLocalizedValues().get(0).getValue();
        }
        return "NOT FOUND";
    }
}

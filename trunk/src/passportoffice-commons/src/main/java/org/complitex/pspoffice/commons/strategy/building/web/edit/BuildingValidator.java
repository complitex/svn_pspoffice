/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.commons.strategy.building.web.edit;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.wicket.Component;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.EntityAttribute;
import org.complitex.dictionaryfw.strategy.web.IValidator;

/**
 *
 * @author Artem
 */
public class BuildingValidator implements IValidator {

    @Override
    public boolean validate(DomainObject object, Component component) {
        boolean valid = checkNumber(object, component);
        valid &= checkStreets(object, component);
        return valid;
    }

    private boolean checkStreets(DomainObject object, Component component) {
        List<EntityAttribute> streets = Lists.newArrayList(Iterables.filter(object.getAttributes(), new Predicate<EntityAttribute>() {

            @Override
            public boolean apply(EntityAttribute attr) {
                return attr.getAttributeTypeId().equals(503L);
            }
        }));
        for (EntityAttribute street1 : streets) {
            for (EntityAttribute street2 : streets) {
                if (!street1.getAttributeId().equals(street2.getAttributeId()) && street1.getValueId().equals(street2.getValueId())) {
                    component.error("Building standing on the one street can have only one number.");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkNumber(DomainObject object, Component component) {
        List<EntityAttribute> numbers = Lists.newArrayList(Iterables.filter(object.getAttributes(), new Predicate<EntityAttribute>() {

            @Override
            public boolean apply(EntityAttribute attr) {
                return attr.getAttributeTypeId().equals(500L);
            }
        }));
        if (numbers.isEmpty()) {
            component.error("Building must has at least one number.");
            return false;
        }
        return true;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import java.io.Serializable;
import java.util.Map;
import static com.google.common.collect.Maps.*;

/**
 *
 * @author Artem
 */
public class RegistrationModification implements Serializable {

    private final Map<Long, ModificationType> attributeModificationMap = newHashMap();
    private ModificationType modificationType;

    public ModificationType getModificationType() {
        return modificationType;
    }

    public RegistrationModification setModificationType(ModificationType modificationType) {
        this.modificationType = modificationType;
        return this;
    }

    public ModificationType getAttributeModificationType(long attributeTypeId) {
        return attributeModificationMap.get(attributeTypeId);
    }

    public RegistrationModification addAttributeModification(long attributeTypeId, ModificationType modificationType) {
        attributeModificationMap.put(attributeTypeId, modificationType);
        return this;
    }
}

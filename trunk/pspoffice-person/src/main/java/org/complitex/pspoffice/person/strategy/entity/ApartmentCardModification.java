/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import java.io.Serializable;
import static com.google.common.collect.Maps.*;
import java.util.Map;

/**
 *
 * @author Artem
 */
public class ApartmentCardModification implements Serializable {

    private final Map<Long, ModificationType> attributeModificationMap = newHashMap();
    private final Map<Long, RegistrationModification> registrationModificationMap = newHashMap();

    public ApartmentCardModification() {
    }

    public ModificationType getModificationType(long attributeTypeId) {
        return attributeModificationMap.get(attributeTypeId);
    }

    public void addAttributeModification(long attributeTypeId, ModificationType modificationType) {
        attributeModificationMap.put(attributeTypeId, modificationType);
    }

    public RegistrationModification getRegistrationModification(long registrationId) {
        return registrationModificationMap.get(registrationId);
    }

    public void addRegistrationModification(long registrationId, RegistrationModification registrationModification) {
        registrationModificationMap.put(registrationId, registrationModification);
    }
}

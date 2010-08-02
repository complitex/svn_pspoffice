/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.commons.strategy.building.web.edit;

import java.io.Serializable;
import org.complitex.dictionaryfw.entity.EntityAttribute;

/**
 *
 * @author Artem
 */
public class BuildingAttribute implements Serializable {

    private Long attributeId;

    private EntityAttribute number;

    private EntityAttribute corp;

    private EntityAttribute structure;

    private EntityAttribute street;

    public BuildingAttribute(Long attributeId, EntityAttribute number, EntityAttribute corp, EntityAttribute structure, EntityAttribute street) {
        this.attributeId = attributeId;
        this.number = number;
        this.corp = corp;
        this.structure = structure;
        this.street = street;
    }

    public EntityAttribute getCorp() {
        return corp;
    }

    public void setCorp(EntityAttribute corp) {
        this.corp = corp;
    }

    public EntityAttribute getNumber() {
        return number;
    }

    public void setNumber(EntityAttribute number) {
        this.number = number;
    }

    public EntityAttribute getStructure() {
        return structure;
    }

    public void setStructure(EntityAttribute structure) {
        this.structure = structure;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public EntityAttribute getStreet() {
        return street;
    }

    public void setStreet(EntityAttribute street) {
        this.street = street;
    }
}

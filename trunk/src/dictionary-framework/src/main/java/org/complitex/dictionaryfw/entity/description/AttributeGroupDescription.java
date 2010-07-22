/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.complitex.dictionaryfw.entity.description;

import java.util.List;
import org.complitex.dictionaryfw.entity.AttributeDescription;

/**
 *
 * @author Artem
 */
public class AttributeGroupDescription {

    private String groupLabel;
    private List<AttributeDescription> attributeDescs;

    public List<AttributeDescription> getAttributeDescs() {
        return attributeDescs;
    }

    public void setAttributeDescs(List<AttributeDescription> attributeDescs) {
        this.attributeDescs = attributeDescs;
    }

    public String getGroupLabel() {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

}

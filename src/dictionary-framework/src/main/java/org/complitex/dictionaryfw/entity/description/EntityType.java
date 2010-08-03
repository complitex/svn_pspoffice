/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.entity.description;

import java.io.Serializable;
import java.util.List;
import org.complitex.dictionaryfw.entity.StringCulture;

/**
 *
 * @author Artem
 */
public class EntityType implements Serializable {

    private Long id;

    private List<StringCulture> entityTypeNames;

    public List<StringCulture> getEntityTypeNames() {
        return entityTypeNames;
    }

    public void setEntityTypeNames(List<StringCulture> entityTypeNames) {
        this.entityTypeNames = entityTypeNames;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

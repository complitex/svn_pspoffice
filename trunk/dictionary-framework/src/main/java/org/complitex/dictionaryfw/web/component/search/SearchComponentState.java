/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.web.component.search;

import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.Map;
import org.complitex.dictionaryfw.entity.DomainObject;

/**
 *
 * @author Artem
 */
public class SearchComponentState implements Serializable {

    private Map<String, DomainObject> state = Maps.newHashMap();

    public void put(String entity, DomainObject object) {
        state.put(entity, object);
    }

    public DomainObject get(String entity) {
        return state.get(entity);
    }

    public void updateState(Map<String, DomainObject> state) {
        for (Map.Entry<String, DomainObject> entry : state.entrySet()) {
            this.state.put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        state.clear();
    }
}

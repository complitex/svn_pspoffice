/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy;

import java.util.List;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.example.IEntityExample;

/**
 * T - entity type, E - example type
 *
 * @author Artem
 */
public interface IDao<T extends DomainObject, E extends IEntityExample> {

    String getTable();

    void insert(T entity);

    T findById(E example);

    public void update(T oldEntity, T newEntity);

    List<T> find(E example);

    int count(E example);
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.commons.strategy;

import java.util.List;
import org.passportoffice.commons.entity.Entity;
import org.passportoffice.commons.entity.example.IEntityExample;

/**
 * T - entity type, E - example type
 *
 * @author Artem
 */
public interface IDao<T extends Entity, E extends IEntityExample> {

    String getTable();

    void insert(T entity);

    T findById(E example);

    public void update(T oldEntity, T newEntity);

    List<T> find(E example);

    int count(E example);
}

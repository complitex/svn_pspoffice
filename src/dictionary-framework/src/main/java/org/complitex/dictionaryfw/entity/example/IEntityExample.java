/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.entity.example;

/**
 *
 * @author Artem
 */
public interface IEntityExample {

    String getLocale();

    void setLocale(String locale);

    String getTable();

    void setTable(String table);

    int getSize();

    void setSize(int size);

    int getStart();

    void setStart(int start);

    boolean isAsc();

    void setAsc(boolean asc);

    String getOrderByExpression();

    void setOrderByExpression(String orderByExpression);

    Long getId();

    void setId(Long entityId);
}

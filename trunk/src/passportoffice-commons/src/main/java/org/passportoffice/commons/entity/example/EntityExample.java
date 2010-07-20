/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.commons.entity.example;

import java.util.Locale;

/**
 *
 * @author Artem
 */
public class EntityExample implements IEntityExample {

    private String table;

    private int start;

    private int size;

    private String locale;

    private String orderByExpression;

    private boolean asc;

    private Long id;

    public EntityExample(String table, Locale locale, Long id) {
        this.table = table;
        this.locale = locale.getLanguage();
        this.id = id;
    }

    public EntityExample(String table) {
        this.table = table;
    }

    public EntityExample(String table, int start, int size, Locale locale, String orderByExpression, boolean asc) {
        this.table = table;
        this.start = start;
        this.size = size;
        this.locale = locale.getLanguage();
        this.orderByExpression = orderByExpression;
        this.asc = asc;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public void setStart(int start) {
        this.start = start;
    }

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long entityId) {
        this.id = entityId;
    }

    @Override
    public boolean isAsc() {
        return asc;
    }

    @Override
    public void setAsc(boolean asc) {
        this.asc = asc;
    }

    @Override
    public String getOrderByExpression() {
        return orderByExpression;
    }

    @Override
    public void setOrderByExpression(String orderByExpression) {
        this.orderByExpression = orderByExpression;
    }
}

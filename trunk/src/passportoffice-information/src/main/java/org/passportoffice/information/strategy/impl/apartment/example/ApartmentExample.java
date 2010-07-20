/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.information.strategy.impl.apartment.example;

import java.util.Locale;
import org.complitex.dictionaryfw.entity.example.EntityExample;
import org.passportoffice.information.strategy.impl.apartment.dao.ApartmentDao;

/**
 *
 * @author Artem
 */
public class ApartmentExample extends EntityExample {

    private String name;

    public ApartmentExample(String name) {
        super(ApartmentDao.TABLE_NAME);
        this.name = name;
    }

    public ApartmentExample(String name, int start, int size, Locale locale, String orderByExpression, boolean asc) {
        super(ApartmentDao.TABLE_NAME, start, size, locale, orderByExpression, asc);
        this.name = name;
    }

    public ApartmentExample(Locale locale, Long id) {
        super(ApartmentDao.TABLE_NAME, locale, id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.information.strategy.impl.room.example;

import java.util.Locale;
import org.complitex.dictionaryfw.entity.example.EntityExample;
import org.passportoffice.information.strategy.impl.room.dao.RoomDao;

/**
 *
 * @author Artem
 */
public class RoomExample extends EntityExample {

    private String name;

    public RoomExample(String name) {
        super(RoomDao.TABLE_NAME);
        this.name = name;
    }

    public RoomExample(String name, int start, int size, Locale locale, String orderByExpression, boolean asc) {
        super(RoomDao.TABLE_NAME, start, size, locale, orderByExpression, asc);
        this.name = name;
    }

    public RoomExample(Locale locale, Long id) {
        super(RoomDao.TABLE_NAME, locale, id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

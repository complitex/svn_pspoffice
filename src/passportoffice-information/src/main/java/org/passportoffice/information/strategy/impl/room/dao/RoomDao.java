/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.information.strategy.impl.room.dao;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.passportoffice.commons.dao.AbstractEntityDao;
import org.passportoffice.commons.dao.aop.SqlSessionInterceptor;
import org.passportoffice.information.strategy.impl.room.Room;
import org.passportoffice.information.strategy.impl.room.example.RoomExample;

/**
 *
 * @author Artem
 */
@Stateless
@LocalBean
@Interceptors({SqlSessionInterceptor.class})
public class RoomDao extends AbstractEntityDao<Room, RoomExample> {

    public static final String TABLE_NAME = "room";

    public static enum OrderBy {

        NAME;
    }

    @Override
    protected String getNamespace() {
        return "org.passportoffice.information.strategy.impl.room.Room";
    }

    @Override
    public Room newInstance() {
        Room room = new Room();
        configureNewEntity(room);
        return room;
    }

    @Override
    public String getTable() {
        return TABLE_NAME;
    }
}

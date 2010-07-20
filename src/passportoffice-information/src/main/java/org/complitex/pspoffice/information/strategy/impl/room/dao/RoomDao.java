/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.information.strategy.impl.room.dao;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.complitex.dictionaryfw.dao.AbstractEntityDao;
import org.complitex.dictionaryfw.dao.aop.SqlSessionInterceptor;
import org.complitex.pspoffice.information.strategy.impl.room.Room;
import org.complitex.pspoffice.information.strategy.impl.room.example.RoomExample;

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

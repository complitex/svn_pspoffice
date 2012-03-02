/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity.grid;

import java.io.Serializable;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public class ApartmentsGridFilter implements Serializable {

    private final long buildingId;
    private String number;
    private final String apartmentPermissionString;
    private final String roomPermissionString;
    private final boolean admin;
    private int start;
    private int size;
    private final Locale locale;

    public ApartmentsGridFilter(long buildingId, String apartmentPermissionString, String roomPermissionString,
            boolean admin, Locale locale) {
        this.buildingId = buildingId;
        this.apartmentPermissionString = apartmentPermissionString;
        this.roomPermissionString = roomPermissionString;
        this.admin = admin;
        this.locale = locale;
    }

    public void reset() {
        number = null;
        start = 0;
        size = 0;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getApartmentPermissionString() {
        return apartmentPermissionString;
    }

    public long getBuildingId() {
        return buildingId;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getRoomPermissionString() {
        return roomPermissionString;
    }
}

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
public class ApartmentCardsGridFilter implements Serializable {

    private final long apartmentId;
    private final String apartmentCardPermissionString;
    private final boolean admin;
    private int start;
    private int size;
    private final Locale locale;

    public ApartmentCardsGridFilter(long apartmentId, String apartmentCardPermissionString, boolean admin, Locale locale) {
        this.apartmentId = apartmentId;
        this.apartmentCardPermissionString = apartmentCardPermissionString;
        this.admin = admin;
        this.locale = locale;
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

    public String getApartmentCardPermissionString() {
        return apartmentCardPermissionString;
    }

    public long getApartmentId() {
        return apartmentId;
    }

    public Locale getLocale() {
        return locale;
    }
}

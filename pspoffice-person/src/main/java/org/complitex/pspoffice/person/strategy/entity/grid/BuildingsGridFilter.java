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
public class BuildingsGridFilter implements Serializable {

    private final long cityId;
    private Long districtId;
    private Long streetId;
    private String buildingNumber;
    private final String buildingPermissionString;
    private final String apartmentPermissionString;
    private final String roomPermissionString;
    private final boolean admin;
    private int start;
    private int size;
    private final Locale locale;

    public BuildingsGridFilter(long cityId, Long streetId, String buildingPermissionString, String apartmentPermissionString,
            String roomPermissionString, boolean admin, Locale locale) {
        this.cityId = cityId;
        this.streetId = streetId;
        this.buildingPermissionString = buildingPermissionString;
        this.apartmentPermissionString = apartmentPermissionString;
        this.roomPermissionString = roomPermissionString;
        this.admin = admin;
        this.locale = locale;
    }

    public void reset(boolean resetStreet) {
        districtId = null;
        if (resetStreet) {
            streetId = null;
        }
        buildingNumber = null;
    }

    public String getApartmentPermissionString() {
        return apartmentPermissionString;
    }

    public String getBuildingPermissionString() {
        return buildingPermissionString;
    }

    public String getRoomPermissionString() {
        return roomPermissionString;
    }

    public boolean isAdmin() {
        return admin;
    }

    public long getCityId() {
        return cityId;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
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

    public Long getStreetId() {
        return streetId;
    }

    public void setStreetId(Long streetId) {
        this.streetId = streetId;
    }

    public Locale getLocale() {
        return locale;
    }
}

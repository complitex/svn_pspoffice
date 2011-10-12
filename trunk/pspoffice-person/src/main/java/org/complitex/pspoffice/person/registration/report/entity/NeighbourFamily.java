/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.entity;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public class NeighbourFamily implements Serializable {

    private String name;
    private Integer amount;
    private Integer takeRooms;
    private String takeArea;
    private String apartmentNumber;
    private String roomsAndAreaInfo;
    private String otherBuildingsAndAreaInfo;
    private String loggiaAndAreaInfo;

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTakeArea() {
        return takeArea;
    }

    public void setTakeArea(String takeArea) {
        this.takeArea = takeArea;
    }

    public Integer getTakeRooms() {
        return takeRooms;
    }

    public void setTakeRooms(Integer takeRooms) {
        this.takeRooms = takeRooms;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getRoomsAndAreaInfo() {
        return roomsAndAreaInfo;
    }

    public void setRoomsAndAreaInfo(String roomsAndAreaInfo) {
        this.roomsAndAreaInfo = roomsAndAreaInfo;
    }

    public String getLoggiaAndAreaInfo() {
        return loggiaAndAreaInfo;
    }

    public void setLoggiaAndAreaInfo(String loggiaAndAreaInfo) {
        this.loggiaAndAreaInfo = loggiaAndAreaInfo;
    }

    public String getOtherBuildingsAndAreaInfo() {
        return otherBuildingsAndAreaInfo;
    }

    public void setOtherBuildingsAndAreaInfo(String otherBuildingsAndAreaInfo) {
        this.otherBuildingsAndAreaInfo = otherBuildingsAndAreaInfo;
    }
}

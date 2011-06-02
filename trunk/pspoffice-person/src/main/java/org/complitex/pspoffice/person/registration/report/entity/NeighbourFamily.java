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
    private String roomNumber;
    private Long internalRoomId;
    private String roomsAndAreaInfo;
    private String otherBuildingsAndAreaInfo;
    private String loggiaAndAreaInfo;

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
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

    public Long getInternalRoomId() {
        return internalRoomId;
    }

    public void setInternalRoomId(Long internalRoomId) {
        this.internalRoomId = internalRoomId;
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

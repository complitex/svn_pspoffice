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
}

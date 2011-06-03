/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.entity;

import static com.google.common.collect.Lists.*;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Artem
 */
public class F3Reference implements Serializable {

    private String name;
    private String address;
    private String livingArea;
    private String apartmentArea;
    private Integer takesRooms;
    private Integer rooms;
    private Integer floor;
    private Integer floors;
    private String personalAccountOwnerName;
    private String formOfOwnership;
    private String facilities;
    private String technicalState;
    private List<FamilyMember> familyMembers = newArrayList();
    private List<NeighbourFamily> neighbourFamilies = newArrayList();

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getApartmentArea() {
        return apartmentArea;
    }

    public void setApartmentArea(String apartmentArea) {
        this.apartmentArea = apartmentArea;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public List<FamilyMember> getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(List<FamilyMember> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public void addFamilyMember(FamilyMember familyMember) {
        familyMembers.add(familyMember);
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getFloors() {
        return floors;
    }

    public void setFloors(Integer floors) {
        this.floors = floors;
    }

    public String getFormOfOwnership() {
        return formOfOwnership;
    }

    public void setFormOfOwnership(String formOfOwnership) {
        this.formOfOwnership = formOfOwnership;
    }

    public String getLivingArea() {
        return livingArea;
    }

    public void setLivingArea(String livingArea) {
        this.livingArea = livingArea;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NeighbourFamily> getNeighbourFamilies() {
        return neighbourFamilies;
    }

    public void setNeighbourFamilies(List<NeighbourFamily> neighbourFamilies) {
        this.neighbourFamilies = neighbourFamilies;
    }

    public void addNeighbourFamily(NeighbourFamily neighbourFamily) {
        neighbourFamilies.add(neighbourFamily);
    }

    public String getPersonalAccountOwnerName() {
        return personalAccountOwnerName;
    }

    public void setPersonalAccountOwnerName(String personalAccountOwnerName) {
        this.personalAccountOwnerName = personalAccountOwnerName;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public Integer getTakesRooms() {
        return takesRooms;
    }

    public void setTakesRooms(Integer takesRooms) {
        this.takesRooms = takesRooms;
    }

    public String getTechnicalState() {
        return technicalState;
    }

    public void setTechnicalState(String technicalState) {
        this.technicalState = technicalState;
    }
}

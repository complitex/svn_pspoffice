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

    private String personName;
    private String personAddress;
    private String personArea;
    private String apartmentArea;
    private Integer rooms;
    private Integer personRooms;
    private Integer floor;
    private Integer numberOfStoreys;
    private String privateAccountOwnerName;
    private String personOwnership;
    private String facilities;
    private String technicalState;
    private List<FamilyMember> familyMembers = newArrayList();
    private List<NeighbourFamily> neighbourFamilies = newArrayList();

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

    public List<NeighbourFamily> getNeighbourFamilies() {
        return neighbourFamilies;
    }

    public void setNeighbourFamilies(List<NeighbourFamily> neighbourFamilies) {
        this.neighbourFamilies = neighbourFamilies;
    }

    public void addNeighbourFamily(NeighbourFamily neighbourFamily) {
        neighbourFamilies.add(neighbourFamily);
    }

    public Integer getNumberOfStoreys() {
        return numberOfStoreys;
    }

    public void setNumberOfStoreys(Integer numberOfStoreys) {
        this.numberOfStoreys = numberOfStoreys;
    }

    public String getPersonAddress() {
        return personAddress;
    }

    public void setPersonAddress(String personAddress) {
        this.personAddress = personAddress;
    }

    public String getPersonArea() {
        return personArea;
    }

    public void setPersonArea(String personArea) {
        this.personArea = personArea;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonOwnership() {
        return personOwnership;
    }

    public void setPersonOwnership(String personOwnership) {
        this.personOwnership = personOwnership;
    }

    public Integer getPersonRooms() {
        return personRooms;
    }

    public void setPersonRooms(Integer personRooms) {
        this.personRooms = personRooms;
    }

    public String getPrivateAccountOwnerName() {
        return privateAccountOwnerName;
    }

    public void setPrivateAccountOwnerName(String privateAccountOwnerName) {
        this.privateAccountOwnerName = privateAccountOwnerName;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public String getTechnicalState() {
        return technicalState;
    }

    public void setTechnicalState(String technicalState) {
        this.technicalState = technicalState;
    }
}

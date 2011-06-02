/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.entity;

import java.io.Serializable;
import static com.google.common.collect.Lists.*;
import java.util.List;

/**
 *
 * @author Artem
 */
public class FamilyAndCommunalApartmentInfo implements Serializable {

    private String name;
    private String address;
    private List<NeighbourFamily> neighbourFamilies = newArrayList();
    private List<FamilyMember> familyMembers = newArrayList();
    private String kitchenArea;
    private String bathroomArea;
    private String toiletArea;
    private String hallArea;
    private String otherSpaceInfo;
    private String sharedArea;
    private Integer floor;
    private Integer numberOfStoreys;
    private String storeroomArea;
    private String barnArea;
    private String otherBuildings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOtherBuildings() {
        return otherBuildings;
    }

    public void setOtherBuildings(String otherBuildings) {
        this.otherBuildings = otherBuildings;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBarnArea() {
        return barnArea;
    }

    public void setBarnArea(String barnArea) {
        this.barnArea = barnArea;
    }

    public String getBathroomArea() {
        return bathroomArea;
    }

    public void setBathroomArea(String bathroomArea) {
        this.bathroomArea = bathroomArea;
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

    public String getHallArea() {
        return hallArea;
    }

    public void setHallArea(String hallArea) {
        this.hallArea = hallArea;
    }

    public String getKitchenArea() {
        return kitchenArea;
    }

    public void setKitchenArea(String kitchenArea) {
        this.kitchenArea = kitchenArea;
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

    public String getOtherSpaceInfo() {
        return otherSpaceInfo;
    }

    public void setOtherSpaceInfo(String otherSpaceInfo) {
        this.otherSpaceInfo = otherSpaceInfo;
    }

    public String getSharedArea() {
        return sharedArea;
    }

    public void setSharedArea(String sharedArea) {
        this.sharedArea = sharedArea;
    }

    public String getStoreroomArea() {
        return storeroomArea;
    }

    public void setStoreroomArea(String storeroomArea) {
        this.storeroomArea = storeroomArea;
    }

    public String getToiletArea() {
        return toiletArea;
    }

    public void setToiletArea(String toiletArea) {
        this.toiletArea = toiletArea;
    }
}

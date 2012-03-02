/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity.grid;

import java.io.Serializable;
import java.util.List;
import org.complitex.dictionary.entity.DomainObject;

/**
 *
 * @author Artem
 */
public class BuildingsGridEntity implements Serializable {

    private String district;
    private Long districtId;
    private String street;
    private Long streetId;
    private String building;
    private long buildingId;
    private int apartments;
    private List<DomainObject> organizations;

    public BuildingsGridEntity(String district, Long districtId, String street, Long streetId, String building,
            long buildingId, int apartments, List<DomainObject> organizations) {
        this.district = district;
        this.districtId = districtId;
        this.street = street;
        this.streetId = streetId;
        this.building = building;
        this.buildingId = buildingId;
        this.apartments = apartments;
        this.organizations = organizations;
    }

    public String getBuilding() {
        return building;
    }

    public long getBuildingId() {
        return buildingId;
    }

    public String getDistrict() {
        return district;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public List<DomainObject> getOrganizations() {
        return organizations;
    }

    public String getStreet() {
        return street;
    }

    public Long getStreetId() {
        return streetId;
    }

    public int getApartments() {
        return apartments;
    }
}

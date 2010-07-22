package org.complitex.dictionaryfw.web.component.search;

///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.passportoffice.information.web.component.search;
//
//import java.io.Serializable;
//import org.complitex.dictionaryfw.entity.Apartment;
//import org.complitex.dictionaryfw.entity.Building;
//import org.complitex.dictionaryfw.entity.City;
//import org.complitex.dictionaryfw.entity.Country;
//import org.complitex.dictionaryfw.entity.Region;
//import org.complitex.dictionaryfw.entity.Room;
//import org.complitex.dictionaryfw.entity.Street;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// *
// * @author Artem
// */
//public class Address implements Serializable {
//
//    private static final Logger log = LoggerFactory.getLogger(Address.class);
//
//    static final Region NO_REGION = new Region();
//
//    static {
//        NO_REGION.setId(-1L);
//        NO_REGION.setLocalizedName("No region");
//    }
//
//    static final City NO_CITY = new City();
//
//    static {
//        NO_CITY.setId(-1L);
//        NO_CITY.setLocalizedName("No city");
//    }
//
//    static final Street NO_STREET = new Street();
//
//    static {
//        NO_STREET.setId(-1L);
//        NO_STREET.setLocalizedName("No street");
//    }
//
//    static final Apartment NO_APARTMENT = new Apartment();
//
//    static {
//        NO_APARTMENT.setId(-1L);
//    }
//
//    private Country country;
//
//    private Region region;
//
//    private City city;
//
//    private Street street;
//
//    private Building building;
//
//    private Apartment apartment;
//
//    private Room room;
//
//    public Apartment getApartment() {
//        return apartment;
//    }
//
//    public void setApartment(Apartment apartment) {
//        this.apartment = apartment;
//    }
//
//    public Building getBuilding() {
//        return building;
//    }
//
//    public void setBuilding(Building building) {
//        this.building = building;
//    }
//
//    public City getCity() {
//        return city;
//    }
//
//    public void setCity(City city) {
//        this.city = city;
//    }
//
//    public Country getCountry() {
//        return country;
//    }
//
//    public void setCountry(Country country) {
//        this.country = country;
//    }
//
//    public Region getRegion() {
//        return region;
//    }
//
//    public void setRegion(Region region) {
//        this.region = region;
//    }
//
//    public Room getRoom() {
//        return room;
//    }
//
//    public void setRoom(Room room) {
//        this.room = room;
//    }
//
//    public Street getStreet() {
//        return street;
//    }
//
//    public void setStreet(Street street) {
//        this.street = street;
//    }
//}

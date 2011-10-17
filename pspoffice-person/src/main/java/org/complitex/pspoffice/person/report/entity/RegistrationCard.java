/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Artem
 */
public class RegistrationCard implements Serializable {

    private String lastName;
    private String firstName;
    private String middleName;
    private String nationality;
    private Date birthDate;
    private String birthRegion;
    private String birthDistrict;
    private String birthCity;
    private String arrivalRegion;
    private String arrivalDistrict;
    private String arrivalCity;
    private String arrivalStreet;
    private String arrivalBuilding;
    private String arrivalCorp;
    private String arrivalApartment;
    private Date arrivalDate;
    private String passportSeries;
    private String passportNumber;
    private String passportIssued;
    private String address;
    private String child0;
    private String child1;
    private String child2;
    private String military;
    private Date registrationDate;
    private String registrationType;
    private String departureRegion;
    private String departureDistrict;
    private String departureCity;

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }
    private Date departureDate;
    private String departureReason;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArrivalRegion() {
        return arrivalRegion;
    }

    public void setArrivalRegion(String arrivalRegion) {
        this.arrivalRegion = arrivalRegion;
    }

    public String getArrivalApartment() {
        return arrivalApartment;
    }

    public void setArrivalApartment(String arrivalApartment) {
        this.arrivalApartment = arrivalApartment;
    }

    public String getArrivalBuilding() {
        return arrivalBuilding;
    }

    public void setArrivalBuilding(String arrivalBuilding) {
        this.arrivalBuilding = arrivalBuilding;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public String getArrivalCorp() {
        return arrivalCorp;
    }

    public void setArrivalCorp(String arrivalCorp) {
        this.arrivalCorp = arrivalCorp;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getArrivalDistrict() {
        return arrivalDistrict;
    }

    public void setArrivalDistrict(String arrivalDistrict) {
        this.arrivalDistrict = arrivalDistrict;
    }

    public String getArrivalStreet() {
        return arrivalStreet;
    }

    public void setArrivalStreet(String arrivalStreet) {
        this.arrivalStreet = arrivalStreet;
    }

    public String getBirthCity() {
        return birthCity;
    }

    public void setBirthCity(String birthCity) {
        this.birthCity = birthCity;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthDistrict() {
        return birthDistrict;
    }

    public void setBirthDistrict(String birthDistrict) {
        this.birthDistrict = birthDistrict;
    }

    public String getBirthRegion() {
        return birthRegion;
    }

    public void setBirthRegion(String birthRegion) {
        this.birthRegion = birthRegion;
    }

    public String getChild0() {
        return child0;
    }

    public void setChild0(String child0) {
        this.child0 = child0;
    }

    public String getChild1() {
        return child1;
    }

    public void setChild1(String child1) {
        this.child1 = child1;
    }

    public String getChild2() {
        return child2;
    }

    public void setChild2(String child2) {
        this.child2 = child2;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public String getDepartureDistrict() {
        return departureDistrict;
    }

    public void setDepartureDistrict(String departureDistrict) {
        this.departureDistrict = departureDistrict;
    }

    public String getDepartureReason() {
        return departureReason;
    }

    public void setDepartureReason(String departureReason) {
        this.departureReason = departureReason;
    }

    public String getDepartureRegion() {
        return departureRegion;
    }

    public void setDepartureRegion(String departureRegion) {
        this.departureRegion = departureRegion;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getMilitary() {
        return military;
    }

    public void setMilitary(String military) {
        this.military = military;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPassportIssued() {
        return passportIssued;
    }

    public void setPassportIssued(String passportIssued) {
        this.passportIssued = passportIssued;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPassportSeries() {
        return passportSeries;
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries = passportSeries;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(String registrationType) {
        this.registrationType = registrationType;
    }
}

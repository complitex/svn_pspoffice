/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.entity;

import java.io.Serializable;
import java.util.Date;
import org.complitex.dictionary.entity.Gender;

/**
 *
 * @author Artem
 */
public class RegistrationStopCoupon implements Serializable {

    private String firstName;
    private String middleName;
    private String lastName;
    private String previousNames;
    private Date birthDate;
    private String birthRegion;
    private String birthDistrict;
    private String birthCity;
    private String birthVillage;
    private Gender gender;
    private String addressDistrict;
    private String addressCity;
    private String addressVillage;
    private String addressStreet;
    private String addressBuildingNumber;
    private String addressBuildingCorp;
    private String addressApartment;
    private String registrationOrganization;
    private String departureRegion;
    private String departureDistrict;
    private String departureCity;
    private String departureVillage;
    private String departureStreet;
    private String departureBuildingNumber;
    private String departureBuildingCorp;
    private String departureApartment;
    private Date departureDate;
    private String passportSerialNumber;
    private String passportNumber;
    private String passportAcquisitionOrganization;
    private Date passportAcquisitionDate;
    private String birthCertificateInfo;
    private boolean ukraineCitizenship;
    private String childrenInfo;
    private String additionalInfo;

    public String getDepartureRegion() {
        return departureRegion;
    }

    public void setDepartureRegion(String departureRegion) {
        this.departureRegion = departureRegion;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getAddressApartment() {
        return addressApartment;
    }

    public void setAddressApartment(String addressApartment) {
        this.addressApartment = addressApartment;
    }

    public String getAddressBuildingCorp() {
        return addressBuildingCorp;
    }

    public void setAddressBuildingCorp(String addressBuildingCorp) {
        this.addressBuildingCorp = addressBuildingCorp;
    }

    public String getAddressBuildingNumber() {
        return addressBuildingNumber;
    }

    public void setAddressBuildingNumber(String addressBuildingNumber) {
        this.addressBuildingNumber = addressBuildingNumber;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressDistrict() {
        return addressDistrict;
    }

    public void setAddressDistrict(String addressDistrict) {
        this.addressDistrict = addressDistrict;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public String getAddressVillage() {
        return addressVillage;
    }

    public void setAddressVillage(String addressVillage) {
        this.addressVillage = addressVillage;
    }

    public String getBirthCertificateInfo() {
        return birthCertificateInfo;
    }

    public void setBirthCertificateInfo(String birthCertificateInfo) {
        this.birthCertificateInfo = birthCertificateInfo;
    }

    public String getBirthCity() {
        return birthCity;
    }

    public void setBirthCity(String birthCity) {
        this.birthCity = birthCity;
    }

    public String getBirthRegion() {
        return birthRegion;
    }

    public void setBirthRegion(String birthRegion) {
        this.birthRegion = birthRegion;
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

    public String getBirthVillage() {
        return birthVillage;
    }

    public void setBirthVillage(String birthVillage) {
        this.birthVillage = birthVillage;
    }

    public String getChildrenInfo() {
        return childrenInfo;
    }

    public void setChildrenInfo(String childrenInfo) {
        this.childrenInfo = childrenInfo;
    }

    public String getDepartureApartment() {
        return departureApartment;
    }

    public void setDepartureApartment(String departureApartment) {
        this.departureApartment = departureApartment;
    }

    public String getDepartureBuildingCorp() {
        return departureBuildingCorp;
    }

    public void setDepartureBuildingCorp(String departureBuildingCorp) {
        this.departureBuildingCorp = departureBuildingCorp;
    }

    public String getDepartureBuildingNumber() {
        return departureBuildingNumber;
    }

    public void setDepartureBuildingNumber(String departureBuildingNumber) {
        this.departureBuildingNumber = departureBuildingNumber;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
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

    public String getDepartureStreet() {
        return departureStreet;
    }

    public void setDepartureStreet(String departureStreet) {
        this.departureStreet = departureStreet;
    }

    public String getDepartureVillage() {
        return departureVillage;
    }

    public void setDepartureVillage(String departureVillage) {
        this.departureVillage = departureVillage;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
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

    public Date getPassportAcquisitionDate() {
        return passportAcquisitionDate;
    }

    public void setPassportAcquisitionDate(Date passportAcquisitionDate) {
        this.passportAcquisitionDate = passportAcquisitionDate;
    }

    public String getPassportAcquisitionOrganization() {
        return passportAcquisitionOrganization;
    }

    public void setPassportAcquisitionOrganization(String passportAcquisitionOrganization) {
        this.passportAcquisitionOrganization = passportAcquisitionOrganization;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPassportSerialNumber() {
        return passportSerialNumber;
    }

    public void setPassportSerialNumber(String passportSerialNumber) {
        this.passportSerialNumber = passportSerialNumber;
    }

    public String getPreviousNames() {
        return previousNames;
    }

    public void setPreviousNames(String previousNames) {
        this.previousNames = previousNames;
    }

    public String getRegistrationOrganization() {
        return registrationOrganization;
    }

    public void setRegistrationOrganization(String registrationOrganization) {
        this.registrationOrganization = registrationOrganization;
    }

    public boolean isUkraineCitizenship() {
        return ukraineCitizenship;
    }

    public void setUkraineCitizenship(boolean ukraineCitizenship) {
        this.ukraineCitizenship = ukraineCitizenship;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import java.util.Date;
import static com.google.common.collect.Lists.*;
import java.util.List;
import org.complitex.dictionary.converter.GenderConverter;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.Gender;
import org.complitex.dictionary.util.StringUtil;
import static org.complitex.dictionary.util.AttributeUtil.*;
import static org.complitex.pspoffice.person.strategy.PersonStrategy.*;

/**
 *
 * @author Artem
 */
public class Person extends DomainObject {

    private String lastName;
    private String firstName;
    private String middleName;
    private List<Person> children = newArrayList();

    public Person(DomainObject copy) {
        super(copy);
    }

    public Person() {
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

    public List<Person> getChildren() {
        return children;
    }

    public void setChildren(List<Person> children) {
        this.children = children;
    }

    public void addChild(Person child) {
        children.add(child);
    }

    public void setChild(int index, Person child) {
        children.set(index, child);
    }

    public String getFullName() {
        return lastName + " " + firstName + " " + middleName;
    }

    public String getIdentityCode() {
        return getStringValue(this, IDENTITY_CODE);
    }

    public Date getBirthDate() {
        return getDateValue(this, BIRTH_DATE);
    }

    public String getBirthCountry() {
        return getStringValue(this, BIRTH_COUNTRY);
    }

    public String getBirthRegion() {
        return getStringValue(this, BIRTH_REGION);
    }

    public String getBirthDistrict() {
        return getStringValue(this, BIRTH_DISTRICT);
    }

    public String getBirthCity() {
        return getStringValue(this, BIRTH_CITY);
    }

    public String getPassportSerialNumber() {
        return getStringValue(this, PASSPORT_SERIAL_NUMBER);
    }

    public String getPassportNumber() {
        return getStringValue(this, PASSPORT_NUMBER);
    }

    public String getPassportAcquisitionOrganization() {
        return getStringValue(this, PASSPORT_ACQUISITION_ORGANIZATION);
    }

    public Date getPassportAcquisitionDate() {
        return getDateValue(this, PASSPORT_ACQUISITION_DATE);
    }

    public Date getDeathDate() {
        return getDateValue(this, DEATH_DATE);
    }

    public String getMilitaryServiceRelation() {
        return getStringValue(this, MILITARY_SERVISE_RELATION);
    }

    public Gender getGender() {
        return getAttributeValue(this, GENDER, new GenderConverter());
    }

    public String getBirthCertificateInfo() {
        return getStringValue(this, BIRTH_CERTIFICATE_INFO);
    }

    public Date getBirthCertificateAcquisitionDate() {
        return getDateValue(this, BIRTH_CERTIFICATE_ACQUISITION_DATE);
    }

    public String getBirthCertificateAcquisitionOrganization() {
        return getStringValue(this, BIRTH_CERTIFICATE_ACQUISITION_ORGANIZATION);
    }

    public boolean isUkraineCitizen() {
        return getBooleanValue(this, UKRAINE_CITIZENSHIP);
    }

    public String getPassportData() {
        String passportSerialNumber = getStringValue(this, PASSPORT_SERIAL_NUMBER);
        String passportNumber = getStringValue(this, PASSPORT_NUMBER);
        if (passportSerialNumber != null && passportNumber != null) {
            return StringUtil.valueOf(passportSerialNumber) + " " + StringUtil.valueOf(passportNumber);
        } else {
            return null;
        }
    }

    public String getPassportOrBirthCertificate() {
        String passport = getPassportData();
        return passport != null ? passport : getStringValue(this, BIRTH_CERTIFICATE_INFO);
    }
}

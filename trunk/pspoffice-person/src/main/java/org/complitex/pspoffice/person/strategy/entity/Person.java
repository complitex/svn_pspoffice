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
import org.complitex.pspoffice.document.strategy.entity.Document;
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
    private Document document;
    private Document replacedDocument;

    public Person(DomainObject copy) {
        super(copy);
    }

    public Person() {
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Document getReplacedDocument() {
        return replacedDocument;
    }

    public void setReplacedDocument(Document replacedDocument) {
        this.replacedDocument = replacedDocument;
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

    public Date getDeathDate() {
        return getDateValue(this, DEATH_DATE);
    }

    public String getMilitaryServiceRelation() {
        return getStringValue(this, MILITARY_SERVISE_RELATION);
    }

    public Gender getGender() {
        return getAttributeValue(this, GENDER, new GenderConverter());
    }

    public boolean isUkraineCitizen() {
        return getBooleanValue(this, UKRAINE_CITIZENSHIP);
    }
}

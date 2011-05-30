/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.util.Date;
import static com.google.common.collect.Lists.*;
import java.util.List;
import org.complitex.dictionary.converter.DateConverter;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.dictionary.util.StringUtil;
import static org.complitex.pspoffice.person.strategy.PersonStrategy.*;

/**
 *
 * @author Artem
 */
public class Person extends DomainObject {

    private String lastName;
    private String firstName;
    private String middleName;
    private Registration registration;
    private Registration changedRegistration;
    private boolean registrationStopped;
    private List<Person> children = newArrayList();

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

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public Registration getChangedRegistration() {
        return changedRegistration;
    }

    public void setChangedRegistration(Registration newRegistration) {
        this.changedRegistration = newRegistration;
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

    public void updateChildrenAttributes() {
        getAttributes().removeAll(Collections2.filter(getAttributes(), new Predicate<Attribute>() {

            @Override
            public boolean apply(Attribute attr) {
                return attr.getAttributeTypeId().equals(CHILDREN);
            }
        }));
        long attributeId = 1;
        for (Person child : getChildren()) {
            addChildrenAttribute(child.getId(), attributeId++);
        }
    }

    private void addChildrenAttribute(long valueId, long attributeId) {
        Attribute childrenAttribute = new Attribute();
        childrenAttribute.setAttributeId(attributeId);
        childrenAttribute.setAttributeTypeId(CHILDREN);
        childrenAttribute.setValueTypeId(CHILDREN);
        childrenAttribute.setValueId(valueId);
        addAttribute(childrenAttribute);
    }

    public boolean isRegistrationStopped() {
        return registrationStopped;
    }

    public void setRegistrationStopped(boolean registrationStopped) {
        this.registrationStopped = registrationStopped;
    }

    public Date getBirthDate() {
        Attribute birthDateAttribute = getAttribute(BIRTH_DATE);
        String value = stringBean().getSystemStringCulture(birthDateAttribute.getLocalizedValues()).getValue();
        return value != null ? new DateConverter().toObject(value) : null;
    }

    public String getPassportData() {
        Attribute passportSerialNumberAttribute = getAttribute(PASSPORT_SERIAL_NUMBER);
        Attribute passportNumberAttribute = getAttribute(PASSPORT_NUMBER);
        String passportSerialNumber = stringBean().getSystemStringCulture(passportSerialNumberAttribute.getLocalizedValues()).getValue();
        String passportNumber = stringBean().getSystemStringCulture(passportNumberAttribute.getLocalizedValues()).getValue();
        if (passportSerialNumber != null && passportNumber != null) {
            return StringUtil.valueOf(passportSerialNumber) + " " + StringUtil.valueOf(passportNumber);
        } else {
            return null;
        }
    }

    private StringCultureBean stringBean() {
        return EjbBeanLocator.getBean(StringCultureBean.class);
    }
}

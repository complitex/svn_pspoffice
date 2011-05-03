/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import static com.google.common.collect.Lists.*;
import java.util.List;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import static org.complitex.pspoffice.person.strategy.PersonStrategy.*;

/**
 *
 * @author Artem
 */
public class Person extends DomainObject {

    private String lastName;
    private String firstName;
    private String middleName;
    private DomainObject registration;
    private DomainObject newRegistration;
    private boolean registrationClosed;
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

    public DomainObject getRegistration() {
        return registration;
    }

    public void setRegistration(DomainObject registration) {
        this.registration = registration;
    }

    public DomainObject getNewRegistration() {
        return newRegistration;
    }

    public void setNewRegistration(DomainObject newRegistration) {
        this.newRegistration = newRegistration;
    }

//    public void updateRegistrationAttribute() {
//        getAttribute(REGISTRATION).setValueId(getRegistration().getId());
//    }

//    public void insertRegistrationAttribute(long registrationId){
//        Attribute registrationAttribute = new Attribute();
//        registrationAttribute.setAttributeId(1L);
//        registrationAttribute.setAttributeTypeId(REGISTRATION);
//        registrationAttribute.setValueTypeId(REGISTRATION);
//        registrationAttribute.setValueId(registrationId);
//        addAttribute(registrationAttribute);
//    }

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

    public boolean isRegistrationClosed() {
        return registrationClosed;
    }

    public void setRegistrationClosed(boolean registrationClosed) {
        this.registrationClosed = registrationClosed;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.service;

import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.pspoffice.person.registration.report.entity.F3Reference;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.exception.UnregisteredPersonException;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;

/**
 *
 * @author Artem
 */
@Stateless
public class F3ReferenceBean extends AbstractBean {

    private static final String RESOURCE_BUNDLE = F3ReferenceBean.class.getName();
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;

    @Transactional
    public F3Reference get(Person person, Locale locale) throws UnregisteredPersonException {
        if (person == null) {
            throw new IllegalArgumentException("Person is null.");
        }
        if (person.getRegistration() == null) {
            throw new UnregisteredPersonException();
        }

        F3Reference f3 = new F3Reference();

        //name
        personStrategy.loadName(person);
        f3.setPersonName(personStrategy.displayDomainObject(person, locale));

        //address
        Registration registration = person.getRegistration();
        long addressId = registration.getAddressId();
        String addressEntity = registration.getAddressEntity();
        f3.setPersonAddress(addressRendererBean.displayAddress(addressEntity, addressId, locale));

        //children
        for (Person child : person.getChildren()) {
            personStrategy.loadRegistration(child);
            Registration childRegistration = child.getRegistration();
            if (childRegistration != null && (childRegistration.getAddressId() == addressId)
                    && addressEntity.equals(childRegistration.getAddressEntity())) {
                FamilyMember member = new FamilyMember();
                member.setFirstName(child.getFirstName());
                member.setMiddleName(child.getMiddleName());
                member.setLastName(child.getLastName());
                member.setBirthDate(child.getBirthDate());
                member.setRelation(ResourceUtil.getString(RESOURCE_BUNDLE, "children_relationship", locale));
                member.setRegistrationDate(childRegistration.getRegistrationDate());
                f3.addFamilyMember(member);
            }
        }
        return f3;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.service;

import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.pspoffice.person.registration.report.entity.F3Reference;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.exception.PersonNotRegisteredException;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;

/**
 *
 * @author Artem
 */
@Stateless
public class F3ReferenceBean extends AbstractBean {

    private static final String RESOURCE_BINDLE = F3ReferenceBean.class.getName();
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private RegistrationStrategy registrationStrategy;

    @Transactional
    public F3Reference getReference(Person person, Locale locale) throws PersonNotRegisteredException {
        if (person == null) {
            throw new IllegalArgumentException("Person is null.");
        }
        if (person.getRegistration() == null) {
            throw new PersonNotRegisteredException();
        }

        F3Reference f3 = new F3Reference();

        //name
        personStrategy.loadName(person);
        f3.setPersonName(personStrategy.displayDomainObject(person, locale));

        //address
        DomainObject registration = person.getRegistration();
        long addressId = registrationStrategy.getAddressId(registration);
        String addressEntity = registrationStrategy.getAddressEntity(registration);
        f3.setPersonAddress(addressRendererBean.displayAddress(addressEntity, addressId, locale));

        //children
        for (Person child : person.getChildren()) {
            personStrategy.loadRegistration(child);
            DomainObject childRegistration = child.getRegistration();
            if (childRegistration != null && (registrationStrategy.getAddressId(childRegistration) == addressId)
                    && addressEntity.equals(registrationStrategy.getAddressEntity(childRegistration))) {
                FamilyMember member = new FamilyMember();
                member.setFirstName(child.getFirstName());
                member.setMiddleName(child.getMiddleName());
                member.setLastName(child.getLastName());
                member.setBirthDate(personStrategy.getBirthDate(child));
                member.setRelation(ResourceUtil.getString(RESOURCE_BINDLE, "children_relationship", locale));
                member.setRegistrationDate(registrationStrategy.getRegistrationDate(childRegistration));
                f3.addFamilyMember(member);
            }
        }
        return f3;
    }
}
